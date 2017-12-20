/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hlidskialf.android.alarmclock;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.CheckBox;
import android.widget.Toast;
import android.widget.SeekBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.moya.android.alarming.R;


import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * AlarmClock application.
 */
public class AlarmClock extends AppCompatActivity {
    String q[]={
            "We haven’t got .....  Champagne",
            "George..... fly to Stockholm tomorrow.",
            "I have Flamenco classes ..........",
            "David is the boss, you need to speak to ......",
            "We have to go to the supermarket ..... some bread and milk.",
            "The party was a disaster. There ..... there!",
            "That file belongs to Patricia, give it to .......",
            "Which pen do you want?",
            "He says he’s been robbed. He can’t find his wallet -----",
            "..... orange juice in the fridge.",
            "We haven’t got ..... mineral water.",
            "The room was empty. There ..... there.",
            "The door can’t be broken! he .....",
            "He works for the Bank, .....?",
            "Have you finished the shopping ..... ?"};
            String a[]={
                    "a lot","to going","on Saturday afternoons","it", "for getting","was anybody", "them", "A one blue",
                    "not anywhere.","There isn’t no","a lot", "wasn’t nobody", "just fixed it.", "doesn’t he?","already"};
    String b[]={"little"
            ,"goes to"
            ,"in Saturday afternoons"
            ,"him",
            "to get",
            "wasn’t nobody",
            "him",
            "One blue",
            "nowhere.",
            "There is any",
            "little",
            "was anybody",
            "have just fixed it.",
            "does he?",
            "still"};

    String c[]={"too",
            "is going to"
            ,"at Saturday afternoons"
            ,"her",
            "to getting",
            "was nobody",
            "it",
            "A blue",
            "anywhere.",
            "There isn’t any",
            "too",
            "was nobody",
            "just fix it.",
            "isn’t he?",
            "now"};
    String d[]={"much",
            "go to",
            "by Saturday afternoons"
            ,"them",
            "for to get",
            "was somebody"
            ,"her",
            "The blue one",
            "somewhere",
            "There aren’t no"
            ,"much",
            "was somebody",
            "has just fixed it.",
            "didn’t he?",
            "yet"};
    int ans[]={3,2,0,0,1,2,2,
            2,2,2,3,2,3,0,1};

    String quotes[] = {"The life you complain about is perhaps someone else’s dream.","The person who makes his share from the happiness of others is the happiest person."
            ,"This is one of the things that years have taught me; If you’ve got happiness, do not question it"
            ,"Do not complain about your fortune unless you live to the end of your life.",
            "Because nature does not care about us, we feel so comfortable in the middle of nature...","Smile is the soul that is not surprised at all."
            ,"Science is the mind; poetry is also a science of the heart.","Life is too important to be taken seriously."};
    String authors[] = {"Lev TOLSTOY","Goethe","Charles Bukowski","Anton Çehov","Friedrich Nietzsche","Fyodor Dostoyevski","Maksim Gorki","Oscar Wilde"};




    private Cursor p;
    private Cursor quoteCursor;
    public static SQLiteDatabase db;
    final static String PREFERENCES = "com.hlidskialf.android.alarmclock_preferences";
    final static int SET_ALARM = 1;
    final static int BED_CLOCK = 2;
    final static String PREF_CLOCK_FACE = "face";
    final static String PREF_SHOW_CLOCK = "show_clock";
    final static String PREF_SHOW_QUICK_ALARM = "show_quick_alarm";
    final static String PREF_LAST_QUICK_ALARM = "last_quick_alarm";
    final static String PREF_SHAKE_SNOOZE = "allow_shake_snooze";
    final static String PREF_WIDGET_CLOCK_FACE = "widget_clock_face";

    final static int MENU_ITEM_EDIT=1;
    final static int MENU_ITEM_DELETE=2;
    final static int MENU_ITEM_PREVIEW=3;

    /** Cap alarm count at this number */
    final static int MAX_ALARM_COUNT = 12;

    /** This must be false for production.  If true, turns on logging,
     test code, etc. */
    final static boolean DEBUG = false;

    private SharedPreferences mPrefs;
    private LayoutInflater mFactory;
    private ViewGroup mClockLayout;
    private View mClock = null;
    private MenuItem mAddAlarmItem;
    private MenuItem mBedClockItem;
    private MenuItem mUpdateQuestion;
    private MenuItem mBackup;
    private ListView mAlarmsList;
    private Cursor mCursor;
    private View mQuickAlarm;

    /**
     * Which clock face to show
     */
    private int mFace = -1;

    /*
     * FIXME: it would be nice for this to live in an xml config file.
     */
    final static int[] CLOCKS = {
            R.layout.clock_basic_bw,
            R.layout.clock_googly,
            R.layout.clock_droid2,
            R.layout.clock_droids,
            R.layout.digital_clock,
            R.layout.analog_appwidget,
            R.layout.clock_orologio,
            R.layout.clock_roman,
            R.layout.clock_moma,
            R.layout.clock_faceless_white,
            R.layout.clock_whatever_white,
            R.layout.clock_alarm,
            R.layout.clock_pocket,
            R.layout.clock_return,
    };

    private class AlarmTimeAdapter extends CursorAdapter {
        public AlarmTimeAdapter(Context context, Cursor cursor) {
            super(context, cursor);
        }

        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View ret = mFactory.inflate(R.layout.alarm_time, parent, false);
            DigitalClock digitalClock = (DigitalClock)ret.findViewById(R.id.digitalClock);
            digitalClock.setLive(false);
            if (Log.LOGV) Log.v("newView " + cursor.getPosition());
            return ret;
        }

        public void bindView(View view, Context context, Cursor cursor) {
            final int id = cursor.getInt(Alarms.AlarmColumns.ALARM_ID_INDEX);
            final int hour = cursor.getInt(Alarms.AlarmColumns.ALARM_HOUR_INDEX);
            final int minutes = cursor.getInt(Alarms.AlarmColumns.ALARM_MINUTES_INDEX);
            final Alarms.DaysOfWeek daysOfWeek = new Alarms.DaysOfWeek(
                    cursor.getInt(Alarms.AlarmColumns.ALARM_DAYS_OF_WEEK_INDEX));
            final boolean enabled = cursor.getInt(Alarms.AlarmColumns.ALARM_ENABLED_INDEX) == 1;
            final String label =
                    cursor.getString(Alarms.AlarmColumns.ALARM_MESSAGE_INDEX);

            CheckBox onButton = (CheckBox)view.findViewById(R.id.alarmButton);
            onButton.setChecked(enabled);
            onButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    boolean isChecked = ((CheckBox) v).isChecked();
                    Alarms.enableAlarm(AlarmClock.this, id, isChecked);
                    if (isChecked) {
                        SetAlarm.popAlarmSetToast(
                                AlarmClock.this, hour, minutes, daysOfWeek);
                    }
                }
            });

            DigitalClock digitalClock = (DigitalClock)view.findViewById(R.id.digitalClock);
            if (Log.LOGV) Log.v("bindView " + cursor.getPosition() + " " + id + " " + hour +
                    ":" + minutes + " " + daysOfWeek.toString(context, true) + " dc " + digitalClock);

            digitalClock.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if (true) {
                        Intent intent = new Intent(AlarmClock.this, SetAlarm.class);
                        intent.putExtra(Alarms.ID, id);
                        startActivityForResult(intent, SET_ALARM);
                    } else {
                        // TESTING: immediately pop alarm
                        Intent fireAlarm = new Intent(AlarmClock.this, AlarmAlert.class);
                        fireAlarm.putExtra(Alarms.ID, id);
                        fireAlarm.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(fireAlarm);
                    }
                }
            });

            // set the alarm text
            final Calendar c = Calendar.getInstance();
            c.set(Calendar.HOUR_OF_DAY, hour);
            c.set(Calendar.MINUTE, minutes);
            digitalClock.updateTime(c);

            // Set the repeat text or leave it blank if it does not repeat.
            TextView daysOfWeekView = (TextView) digitalClock.findViewById(R.id.daysOfWeek);
            final String daysOfWeekStr =
                    daysOfWeek.toString(AlarmClock.this, false);
            if (daysOfWeekStr != null && daysOfWeekStr.length() != 0) {
                daysOfWeekView.setText(daysOfWeekStr);
                daysOfWeekView.setVisibility(View.VISIBLE);
            } else {
                daysOfWeekView.setVisibility(View.GONE);
            }

            // Display the label
            TextView labelView =
                    (TextView) digitalClock.findViewById(R.id.label);
            if (label != null && label.length() != 0) {
                labelView.setText(label);
            } else {
                labelView.setText(R.string.default_label);
            }

            // Build context menu
            digitalClock.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                public void onCreateContextMenu(ContextMenu menu, View view,
                                                ContextMenuInfo menuInfo) {
                    menu.setHeaderTitle(Alarms.formatTime(AlarmClock.this, c));
                    MenuItem editAlarmItem = menu.add(0, id, MENU_ITEM_EDIT, R.string.edit_alarm);
                    MenuItem deleteAlarmItem = menu.add(0, id, MENU_ITEM_DELETE, R.string.delete_alarm);
                    MenuItem previewAlarmItem = menu.add(0, id, MENU_ITEM_PREVIEW, R.string.preview_alarm);
                }
            });
        }
    };

    @Override
    public boolean onContextItemSelected(final MenuItem item) {
        switch(item.getOrder()) {
            case MENU_ITEM_EDIT:
                Intent intent = new Intent(AlarmClock.this, SetAlarm.class);
                intent.putExtra(Alarms.ID, item.getItemId());
                startActivityForResult(intent, SET_ALARM);
                break;
            case MENU_ITEM_DELETE:
                // Confirm that the alarm will be deleted.
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.delete_alarm))
                        .setMessage(getString(R.string.delete_alarm_confirm))
                        .setPositiveButton(android.R.string.ok,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface d, int w) {
                                        Alarms.deleteAlarm(AlarmClock.this,
                                                item.getItemId());
                                        updateEmptyVisibility();
                                    }
                                })
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();
                break;
            case MENU_ITEM_PREVIEW:
                Alarms.enableAlert(this, item.getItemId(),
                        getString(R.string.preview_alarm), System.currentTimeMillis());
                break;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // sanity check -- no database, no clock


        // TODO: Check if the user is not logged in then show the alert dialog


        createDatabase();
        p=db.rawQuery("SELECT * FROM questions", null);
        if(!p.moveToFirst())
            insertDB();
        quoteCursor = db.rawQuery("SELECT * FROM dailyMotivations",null);
        if (!quoteCursor.moveToFirst())
            insertDailyQuotes();
        if (getContentResolver() == null) {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.error))
                    .setMessage(getString(R.string.dberror))
                    .setPositiveButton(
                            android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                    .setOnCancelListener(
                            new DialogInterface.OnCancelListener() {
                                public void onCancel(DialogInterface dialog) {
                                    finish();
                                }})
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .create().show();
            return;
        }

        setContentView(R.layout.alarm_clock);



        mFactory = LayoutInflater.from(this);
        mPrefs = getSharedPreferences(PREFERENCES, 0);

        mCursor = Alarms.getAlarmsCursor(getContentResolver());
        mAlarmsList = (ListView) findViewById(R.id.alarms_list);
        mAlarmsList.setAdapter(new AlarmTimeAdapter(this, mCursor));
        mAlarmsList.setVerticalScrollBarEnabled(true);
        mAlarmsList.setItemsCanFocus(true);

        settingPermission();

        mClockLayout = (ViewGroup) findViewById(R.id.clock_layout);
        mClockLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final Intent intent = new Intent(AlarmClock.this, ClockPicker.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        mQuickAlarm = findViewById(R.id.quick_alarm);
        mQuickAlarm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showQuickAlarmDialog();
            }
        });

        setVolumeControlStream(android.media.AudioManager.STREAM_ALARM);

        setClockVisibility(mPrefs.getBoolean(PREF_SHOW_CLOCK, true));
        setQuickAlarmVisibility(mPrefs.getBoolean(PREF_SHOW_QUICK_ALARM, true));
    }

    @Override
    protected void onResume() {
        super.onResume();

        int face = mPrefs.getInt(PREF_CLOCK_FACE, 0);
        if (mFace != face) {
            if (face < 0 || face >= AlarmClock.CLOCKS.length)
                mFace = 0;
            else
                mFace = face;
            inflateClock();
        }

        updateSnoozeVisibility();
        updateEmptyVisibility();
        setClockVisibility(mPrefs.getBoolean(PREF_SHOW_CLOCK, true));
        setQuickAlarmVisibility(mPrefs.getBoolean(PREF_SHOW_QUICK_ALARM, true));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ToastMaster.cancelToast();
        mCursor.deactivate();
    }

    protected void inflateClock() {
        if (mClock != null) {
            mClockLayout.removeView(mClock);
        }
        mClock = mFactory.inflate(CLOCKS[mFace], null);
        mClockLayout.addView(mClock, 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        mAddAlarmItem = menu.add(0, 0, 0, R.string.add_alarm);
        mAddAlarmItem.setIcon(android.R.drawable.ic_menu_add);




        mBedClockItem = menu.add(0, 0, 0, R.string.bed_clock);
        mBedClockItem.setIcon(R.drawable.ic_menu_clock_face);



        mUpdateQuestion = menu.add(0, 0, 0, "Update Question Bank");
        mUpdateQuestion.setIcon(android.R.drawable.ic_menu_add);

        mBackup = menu.add(0, 0, 0, "BackUp & Restore");
        mBackup.setIcon(android.R.drawable.ic_menu_add);

        MenuItem settingsItem = menu.add(0, 0, 0, R.string.settings);
        settingsItem.setIcon(android.R.drawable.ic_menu_preferences);
        settingsItem.setIntent(new Intent(this, SettingsActivity.class));

        return true;
    }

    /**
     * Only allow user to add a new alarm if there are fewer than
     * MAX_ALARM_COUNT
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        mAddAlarmItem.setVisible(mAlarmsList.getChildCount() < MAX_ALARM_COUNT);

        mBedClockItem.setVisible(mPrefs.getBoolean("bedclock_enable", true));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item == mAddAlarmItem) {
            Uri uri = Alarms.addAlarm(this);
            settingPermission();
            // FIXME: scroll to new item.  mAlarmsList.requestChildRectangleOnScreen() ?
            String segment = uri.getPathSegments().get(1);
            int newId = Integer.parseInt(segment);
            if (Log.LOGV) Log.v("In AlarmClock, new alarm id = " + newId);
            Intent intent = new Intent(AlarmClock.this, SetAlarm.class);
            intent.putExtra(Alarms.ID, newId);
            startActivityForResult(intent, SET_ALARM);
            return true;
        } else if (item == mBedClockItem) {
            Intent intent = new Intent(AlarmClock.this, BedClock.class);
            startActivityForResult(intent, BED_CLOCK);
            return true;
        }
        else if (item == mUpdateQuestion)
        {
            Toast.makeText(getApplicationContext(),"To be done in the next version",Toast.LENGTH_LONG).show();
        }
        else if (item == mBackup)
        {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser == null)
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            else
                Toast.makeText(getApplicationContext(),"To be doen in the next version",Toast.LENGTH_LONG).show();
        }

        return false;
    }


    private boolean getClockVisibility() {
        return mClockLayout.getVisibility() == View.VISIBLE;
    }

    private void setClockVisibility(boolean visible) {
        mClockLayout.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private void saveClockVisibility() {
        mPrefs.edit().putBoolean(PREF_SHOW_CLOCK, getClockVisibility()).commit();
    }

    private void setQuickAlarmVisibility(boolean visible) {
        mQuickAlarm.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private void showQuickAlarmDialog() {
        View layout = mFactory.inflate(R.layout.quick_alarm_dialog, null);
        final TextView text1 = (TextView)layout.findViewById(android.R.id.text1);
        final SeekBar slider = (SeekBar)layout.findViewById(android.R.id.input);
        int last_snooze = mPrefs.getInt(PREF_LAST_QUICK_ALARM, 0);
        slider.setMax(59);
        slider.setProgress(last_snooze);
        text1.setText(AlarmClock.this.getString(R.string.minutes, String.valueOf(last_snooze+1)));
        slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seek, int value, boolean fromTouch) {
                text1.setText(AlarmClock.this.getString(R.string.minutes, String.valueOf(value+1)));
            }
            public void onStartTrackingTouch(SeekBar seek) {}
            public void onStopTrackingTouch(SeekBar seek) {}
        });

        AlertDialog d = new AlertDialog.Builder(this)
                .setTitle(R.string.quick_alarm)
                .setView(layout)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        int snooze_min = slider.getProgress() + 1;
                        long snoozeTarget = System.currentTimeMillis() + 1000*60*snooze_min;
                        long nextAlarm = Alarms.calculateNextAlert(AlarmClock.this).getAlert();
                        if (nextAlarm < snoozeTarget) {
                            // alarm will fire before snooze will...
                        } else {
                            Alarms.saveSnoozeAlert(AlarmClock.this, 0, snoozeTarget, AlarmClock.this.getString(R.string.quick_alarm));
                            Alarms.setNextAlert(AlarmClock.this);
                            Toast.makeText(AlarmClock.this,
                                    getString(R.string.alarm_alert_snooze_set, snooze_min),
                                    Toast.LENGTH_LONG).show();
                            updateSnoozeVisibility();
                            mPrefs.edit().putInt(PREF_LAST_QUICK_ALARM, snooze_min-1).commit();
                        }
                    }
                })
                .show();
    }

    private void updateSnoozeVisibility() {
        long next_snooze = mPrefs.getLong(Alarms.PREF_SNOOZE_TIME, 0);
        View v = (View)findViewById(R.id.snooze_message);
        if (next_snooze != 0) {
            TextView tv = (TextView)v.findViewById(R.id.snooze_message_text);
            Calendar c = new GregorianCalendar();
            c.setTimeInMillis(next_snooze);
            String snooze_time = Alarms.formatTime(AlarmClock.this, c);
            tv.setText(getString(R.string.snooze_message_text, snooze_time));

            v.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    v.setVisibility(View.GONE);
                    Alarms.disableSnoozeAlert(AlarmClock.this);
                    Toast.makeText(AlarmClock.this, getString(R.string.snooze_dismissed), Toast.LENGTH_LONG).show();
                    Alarms.setNextAlert(AlarmClock.this);
                }
            });
            v.setVisibility(View.VISIBLE);
        }
        else {
            v.setVisibility(View.GONE);
        }
    }
    private void updateEmptyVisibility() {
        View v = findViewById(R.id.alarms_list_empty);
        if (v != null)
            v.setVisibility(mAlarmsList.getAdapter().getCount() < 1 ? View.VISIBLE : View.GONE);
    }


    protected void createDatabase()
    {
        db=openOrCreateDatabase("QuizDB.db", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS questions(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, question text, opA text," +
                "opB text, opC text,opD text,answer NUMBER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS dailyMotivations(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, quote text, author text)");
    }
    protected void insertDB()
    {
        //tv.setText("Getting the quiz ready...");
        int l=q.length;
        for(int i=0;i<l;i++)
        {
            String query="INSERT INTO questions(question,opA,opB,opC,opD,answer) values('"+q[i]+"','"+a[i]+"','"+b[i]+
                    "','"+c[i]+"','"+d[i]+"','"+ans[i]+"');)";
            db.execSQL(query);
        }
    }

    protected void insertDailyQuotes()
    {
        int l=quotes.length;
        for (int i=0;i<l;i++)
        {
            String query="INSERT INTO dailyMotivations(quote,author) values('"+quotes[i]+"','"+authors[i]+"');";
            db.execSQL(query);
        }
    }



    public void settingPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(getApplicationContext())) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 200);

            }
        }
    }

    public void showWarning(){

        AlertDialog alertDialog = new AlertDialog.Builder(AlarmClock.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("For more efficient use of the app, we recommend you complete your profile");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Go to Profile Page",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(AlarmClock.this,LoginActivity.class));
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();

    }
}
package com.hlidskialf.android.alarmclock;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.moya.android.alarming.R;


public class EnglishCaptcha extends Dialog implements CaptchaInterface {


    CountDownTimer timer;
    int progress = 100;
    private int rightAnswers = 0;
    Button opt1,opt2,opt3,opt4;
    TextView question , numOfCorrectAns;
    ProgressBar pb;
    int scr,nscr,questions=0;
    private SQLiteDatabase db;
    private static final String x="SELECT * FROM questions";
    private Cursor c;
    String n;
    int done[]=new int[7];
    int lastQuestion ;

    CaptchaInterface.OnCorrectListener mCorrectListener;

    public void setOnCorrectListener(CaptchaInterface.OnCorrectListener listener) {
        mCorrectListener = listener;
    }

    public EnglishCaptcha(Context context) {
        super(context);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    }

    public void onCreate(Bundle icicle) {
        View layout = getLayoutInflater().inflate(R.layout.activity_english_captcha, null);
        setContentView(layout);

        opt1=(Button)layout.findViewById(R.id.quiz_detail_button1);
        opt2=(Button)layout.findViewById(R.id.quiz_detail_button2);
        opt3=(Button)layout.findViewById(R.id.quiz_detail_button3);
        opt4=(Button)layout.findViewById(R.id.quiz_detail_button4);
        question = (TextView) layout.findViewById(R.id.question);
        numOfCorrectAns = (TextView) layout.findViewById(R.id.txt_num);


        pb=(ProgressBar)layout.findViewById(R.id.ventilator_progress);

        pb.setProgress(progress);

        //tv.setText("");
        openDatabase();
        c=db.rawQuery(x, null);
        c.moveToFirst();
        getQuestion();
        numOfCorrectAns.setText("Score: 0");
        opt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(opt1.getId());
            }
        }); opt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(opt2.getId());
            }
        }); opt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(opt3.getId());
            }
        }); opt4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(opt4.getId());
            }
        });
    }



    public void checkAnswer(int id) {

        int res = id;
        switch(res)
        {
            case R.id.quiz_detail_button1:
                res = 0;
                break;
            case R.id.quiz_detail_button2:
                res = 1;
                break;
            case R.id.quiz_detail_button3:
                res = 2;
                break;
            case R.id.quiz_detail_button4:
                res = 3;
                break;
            default:
                return;
        }
        if(res==Integer.parseInt(c.getString(6)))
        {
            Toast.makeText(getContext(), "Correct Answer!", Toast.LENGTH_SHORT).show();
            scr++;
            numOfCorrectAns.setText("Score: "+scr);
            rightAnswers++;
            pb.setProgress(100-rightAnswers*50);
        }
        else
        {
            Toast.makeText(getContext(), "Incorrect Answer!", Toast.LENGTH_SHORT).show();
        }
        if(questions<5 && rightAnswers < 2)
        {
            getQuestion();
        }
        else
        {
            Toast.makeText(getContext(), "You have completed the quiz With a score of " + scr + "/" + questions, Toast.LENGTH_LONG).show();
            if (mCorrectListener != null)
                mCorrectListener.onCorrect();

            question.setVisibility(View.INVISIBLE);
            opt1.setVisibility(View.INVISIBLE);
            opt2.setVisibility(View.INVISIBLE);
            opt3.setVisibility(View.INVISIBLE);
            opt4.setVisibility(View.INVISIBLE);
            numOfCorrectAns.setVisibility(View.INVISIBLE);

            dismiss();
            Intent intent = new Intent(getContext(),DailyQuote.class);
            getContext().startActivity(intent);

        }
    }

    protected void openDatabase() {
        db = AlarmClock.db;
    }
    protected void getQuestion()
    {
        boolean f=false;int qid;

        qid=(int) Math.floor(Math.random()*15);
        if (qid == lastQuestion)
            qid=(int) Math.floor(Math.random()*15);
        lastQuestion = qid ;


        c = db.rawQuery("SELECT * FROM questions where id = " + qid , null);

        while(!c.moveToFirst())
        {
            qid=(int) Math.floor(Math.random()*50);
            c = db.rawQuery("SELECT * FROM questions where id = " + qid , null);
        }

        /*
        boolean flag=true;
        while (flag) {
            int x = Integer.parseInt(c.getString(0));
            if(qid!=Integer.parseInt(c.getString(0)))
            {
                if(!c.isLast())
                    c.moveToNext();
                else
                    c.moveToFirst();
            }
            else
                flag=false;
        }*/

        question.setText(c.getString(1));
        opt1.setText(c.getString(2));
        opt2.setText(c.getString(3));
        opt3.setText(c.getString(4));
        opt4.setText(c.getString(5));

        done[questions]=qid;
        questions++;
    }


        /*Random rand = new Random();
        int first = rand.nextInt(99);
        int second = rand.nextInt(99);

        TextView tv;
        if (first < second) {
            int temp = first;
            first = second;
            second = temp;
        }

        tv = (TextView)layout.findViewById(R.id.question_sign);
        if (rand.nextBoolean()) {
            tv.setText("+");
            mAnswer = first + second;
        }
        else {
            tv.setText("-");
            mAnswer = first - second;
        }
        TextView tv;
        tv = (TextView)layout.findViewById(R.id.question_1_tens);
        tv.setText( String.valueOf( (first / 10) % 10 ) );
        tv = (TextView)layout.findViewById(R.id.question_1_ones);
        tv.setText( String.valueOf( first % 10 ) );

        tv = (TextView)layout.findViewById(R.id.question_2_tens);
        tv.setText( String.valueOf( (second / 10) % 10 ) );
        tv = (TextView)layout.findViewById(R.id.question_2_ones);
        tv.setText( String.valueOf( second % 10 ) );

        Button b;*/
}

package com.hlidskialf.android.alarmclock;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.moya.android.alarming.R;


public class LoginActivity extends AppCompatActivity {
    private FloatingActionButton txtSignup ;
    private FirebaseAuth mAuth ;
    private ProgressDialog progress;
    private EditText username,password;
    private Button login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progress = new ProgressDialog(this);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setIcon(R.drawable.logo);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        txtSignup = (FloatingActionButton) findViewById(R.id.login_fab_signup);
        username=(EditText)findViewById(R.id.txt_login_username);

        password=(EditText)findViewById(R.id.login_txt_password);
        login=(Button)findViewById(R.id.login_btn_login);
        View view = new View(this);
        view.isInEditMode();
        //sharedpreferences=getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        //String val=sharedpreferences.getString(Name, null);



        login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String pass = password.getText().toString();
                String usrName = username.getText().toString();
                if (!TextUtils.isEmpty(pass) || !TextUtils.isEmpty(usrName)) {
                    progress.setMessage("Loading....");
                    progress.show();
                    mAuth.signInWithEmailAndPassword(usrName, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(getApplicationContext(), AlarmClock.class);
                                startActivity(intent);
                                progress.dismiss();
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "Invalid Login Information", Toast.LENGTH_LONG).show();
                                progress.hide();
                            }

                        }
                    });
                }
                else
                    Toast.makeText(getApplicationContext(),"Please Fill the form correctly.",Toast.LENGTH_LONG);
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void clickRegisterLayout(View view) {
        getWindow().setExitTransition(null);
        getWindow().setEnterTransition(null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options =
                    ActivityOptions.makeSceneTransitionAnimation(this, txtSignup, txtSignup.getTransitionName());
            startActivityForResult(new Intent(this, RegisterActivity.class), 2000, options.toBundle());
        } else {
            startActivityForResult(new Intent(this, RegisterActivity.class), 2000);
        }
    }


    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Exit")
                .setMessage("Are you sure?")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                }).setNegativeButton("no", null).show();
    }}

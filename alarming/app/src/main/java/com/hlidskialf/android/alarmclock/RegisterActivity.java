package com.hlidskialf.android.alarmclock;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.moya.android.alarming.R;

import java.util.HashMap;
import java.util.regex.Matcher;

public class RegisterActivity extends AppCompatActivity {
    private EditText email , password,repeatPassword;
    private Button createAcount ;
    private DatabaseReference myref ;
    private FirebaseAuth mAuth ;
    private ProgressDialog progress ;
    private CardView cvAdd;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        email = (EditText) findViewById(R.id.signup_txt_email);
        password = (EditText) findViewById(R.id.signup_txt_password);
        repeatPassword = (EditText) findViewById(R.id.et_repeatpassword);
        createAcount = (Button) findViewById(R.id.signup_btn_createAcount);
        progress = new ProgressDialog(this);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        cvAdd = (CardView) findViewById(R.id.cv_add);
        mAuth = FirebaseAuth.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ShowEnterAnimation();
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                animateRevealClose();
            }
        });


        createAcount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = email.getText().toString();
                String pass = password.getText().toString();
                String repeatPass = repeatPassword.getText().toString();

                if (!TextUtils.isEmpty(mail) && !TextUtils.isEmpty(pass))
                {
                    if (!pass.equals(repeatPass))
                        Toast.makeText(getApplicationContext(),"Passwords don't match!",Toast.LENGTH_LONG).show();

                    else
                    {
                        progress.setTitle("Registering User");
                        progress.setMessage("Please Wait while we create your acount.");
                        progress.show();
                        register_new_user(mail,pass);
                    }

                }
                else
                    Toast.makeText(getApplicationContext(),"Please Fill in the form Correctly.",Toast.LENGTH_LONG);

            }
        });
    }




    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void ShowEnterAnimation() {
        Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.fabtransition);
        getWindow().setSharedElementEnterTransition(transition);

        transition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                cvAdd.setVisibility(View.GONE);
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                transition.removeListener(this);
                animateRevealShow();
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }


        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void animateRevealShow() {
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(cvAdd, cvAdd.getWidth()/2,0, fab.getWidth() / 2, cvAdd.getHeight());
        mAnimator.setDuration(500);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                cvAdd.setVisibility(View.VISIBLE);
                super.onAnimationStart(animation);
            }
        });
        mAnimator.start();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void animateRevealClose() {
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(cvAdd,cvAdd.getWidth()/2,0, cvAdd.getHeight(), fab.getWidth() / 2);
        mAnimator.setDuration(500);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                cvAdd.setVisibility(View.INVISIBLE);
                super.onAnimationEnd(animation);
                fab.setImageResource(R.drawable.ic_signup);
                RegisterActivity.super.onBackPressed();
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }
        });
        mAnimator.start();
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBackPressed() {
        animateRevealClose();
    }
/*
    public void clickRegister(View view) {
        String username = editTextUsername.getText().toString();
        String password = editTextPassword.getText().toString();
        String repeatPassword = editTextRepeatPassword.getText().toString();
        if(validate(username, password, repeatPassword)){
            Intent data = new Intent();
            data.putExtra(StaticConfig.STR_EXTRA_USERNAME, username);
            data.putExtra(StaticConfig.STR_EXTRA_PASSWORD, password);
            data.putExtra(StaticConfig.STR_EXTRA_ACTION, STR_EXTRA_ACTION_REGISTER);
            setResult(RESULT_OK, data);
            finish();
        }else {
            Toast.makeText(this, "Invalid email or not match password", Toast.LENGTH_SHORT).show();
        }
    }
*/
    /**
     * Validate email, pass == re_pass
     * @return

    private boolean validate(String emailStr, String password, String repeatPassword) {
    Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(emailStr);
    return password.length() > 0 && repeatPassword.equals(password) && matcher.find();
    }
     */
    private void register_new_user(String mail, String pass) {
        mAuth.createUserWithEmailAndPassword(mail, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (task.isSuccessful()) {
                                        Toast.makeText(RegisterActivity.this, "You Registered Successfully!",
                                                Toast.LENGTH_LONG).show();
                                        progress.dismiss();
                                        Intent intent = new Intent(getApplicationContext(),AlarmClock.class);
                                        startActivity(intent);
                                        finish();
                        }
                        else
                        {
                            Toast.makeText(RegisterActivity.this, task.getException().toString(),
                                    Toast.LENGTH_LONG).show();
                            progress.hide();
                        }
                    }
                });
    }
}

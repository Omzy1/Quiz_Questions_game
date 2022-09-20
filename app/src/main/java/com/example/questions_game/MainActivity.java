package com.example.questions_game;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.telecom.Call;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

//import com.facebook.CallbackManager;
//import com.facebook.FacebookCallback;
//import com.facebook.FacebookException;
//import com.facebook.login.LoginManager;
//import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    //declaring variables
    private AnimationDrawable animationDrawable;
    //the layout
    private RelativeLayout relativeLayout;
    private GoogleSignInOptions gso;
    private GoogleSignInClient gsc;
    private MaterialButton btnGoogle, otherSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //initialising the variables
        relativeLayout = findViewById(R.id.mainLayout);
        //animating the background image by getting the background image from the layout
        //and setting the duration of the animation
        animationDrawable = (AnimationDrawable) relativeLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2500);
        animationDrawable.setExitFadeDuration(5000);
        animationDrawable.start();
        //google signIn
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);
        otherSignIn = findViewById(R.id.otherSignin);


        //otherSignIn button clicked
        otherSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //goes to the signIn activity
                navigateToAnotherActivity(MainActivity.this, SignIn.class);
            }
        });

        //google login clicked
        btnGoogle = findViewById(R.id.google);
        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });


    }

    //sign In
    void signIn() {
        Intent signIntent = gsc.getSignInIntent();
        startActivityForResult(signIntent, 1000);

    }

    //check if the user sign Up correctly with google login
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                task.getResult(ApiException.class);
                navigateToSecondActivity();
            } catch (ApiException e) {
                Toast.makeText(this, "Sign Up!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // goes to game page google signIn
    void navigateToSecondActivity() {
        Intent intent = new Intent(MainActivity.this, Game.class);
        startActivity(intent);
        finish();
    }

    //general method for moving to another activity
    void navigateToAnotherActivity(Context context, Class<?> c) {
        Intent intent = new Intent(context, c);
        startActivity(intent);
        finish();
    }


}
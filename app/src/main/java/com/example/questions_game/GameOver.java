package com.example.questions_game;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class GameOver extends AppCompatActivity {
    //declaring variables
    private MaterialButton btnOthers, btnLogout, btnTryAgain;
    private GoogleSignInOptions gso;
    private GoogleSignInClient gsc;
    private String correct, wrong, total;
    private TextView txtTotalQs, txtCorrect, txtIncorrect;
    private HashMap<String,String> scoreList;
    String  personName, name ,value;
    public GameOver() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);
        //textview
        txtTotalQs = findViewById(R.id.txtTotalQs);
        txtCorrect = findViewById(R.id.txtCorrect);
        txtIncorrect = findViewById(R.id.txtIncorrect);
        //buttons
        btnOthers = findViewById(R.id.btnOthers);
        btnTryAgain = findViewById(R.id.btnTryAgain);

        //hashmap score list
        scoreList = new HashMap<>();
        //google signIn
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);
        //sharePreference
        // Retrieving the value using its keys the file name
        // must be same in both saving and retrieving the data
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        // The value will be default as empty string because for
        // the very first time when the app is opened, there is nothing to show
         total = sharedPreferences.getString("total", "");
         correct = sharedPreferences.getString("correct", "");
        wrong = sharedPreferences.getString("incorrect", "");
        String user = sharedPreferences.getString("name", "");
        //displaying the total ,correct, incorrect
        txtTotalQs.setText("Total of Questions: " + total);
        txtCorrect.setText("Currect Answer: " + correct);
        txtIncorrect.setText(" Incorrect Answer: " + wrong);
        // storing the google account signIn detail to a variable
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        //check if the google account is not null
        if (account != null) {
            //storing the name of the user that logged in with google account inside the arraylist
            // and the score too
             personName = account.getDisplayName();
            scoreList.put(personName,correct);
            //getting the value from the name of the google account user
            value = scoreList.get(personName);
            //for each loop for storing all the values into entry variables
            for(Map.Entry<String, String> entry: scoreList.entrySet()) {
                // if given value is equal to value from entry
                // print the corresponding key
                if (entry.getValue() == value) {
                    name = entry.getKey();
                }
            }
            //if the account is null means that the user didn't logged in with the google account
        }else{
            //store the user name from the system log in inside the arraylist with the score
            scoreList.put(user,correct);
            //store the value which it is the correct
            value = scoreList.get(user);
            //loop through the entry set to store the values
            // and compared them to the value variable that it is stored above
            // in order to store into a variable the name of the user
            for(Map.Entry<String, String> entry: scoreList.entrySet()) {
                // if give value is equal to value from entry
                // print the corresponding key
                if (entry.getValue() == value) {
                    name = entry.getKey();
                }
            }

        }
        //it restart the game
       btnTryAgain.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(GameOver.this, Game.class);
               startActivity(intent);
               finish();
           }
       });
        //it will bring the user to the other activity
        btnOthers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameOver.this, OthersScore.class);
                intent.putExtra("Key", name);
                intent.putExtra("Value", value);
                startActivity(intent);
                finish();

            }
        });
        //log out button clicked will take you to the menu activity
        btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
    }

    // sign out
    void signOut() {
        gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                finish();
                startActivity(new Intent(GameOver.this, MainActivity.class));
            }
        });
    }

    }


package com.example.questions_game;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Objects;

public class OthersScore extends AppCompatActivity {
    //declaring the variables
private MaterialButton btnLogoutOthers,btnYourScore;
private HashMap<String,String>scoresList;
private FirebaseDatabase firebaseDatabase;
private DatabaseReference databaseReference, databaseReference2;
int newScore,OldscoreSignGoogle;
private GoogleSignInOptions gso;
private GoogleSignInClient gsc;
private RecyclerView recyclerView;
myAdapter myAdapter;
ArrayList<Users>list;
String oldscoreDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_others_score);
        //Buttons
        btnLogoutOthers = findViewById(R.id.btnLogoutOthers);
        btnYourScore = findViewById(R.id.btnYourScore);
        //firebase database
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReferenceFromUrl("https://questions-game-a1441-default-rtdb.firebaseio.com/");
        //google signIn
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);
        //storing the variables
        Intent intent = getIntent();
        String nameOnGoogle = intent.getStringExtra("Key");
        String score = intent.getStringExtra("Value");
        //google sign In
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);
        //google account
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        //recycleView to display the card with score information
        recyclerView = findViewById(R.id.userList);
        //if button back is clicked
        btnYourScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OthersScore.this, GameOver.class);
                startActivity(intent);
            }
        });

        //this is the table for the users in the database
        databaseReference.child("scores").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //if there is the nameOnGoogle
                    if (snapshot.hasChild(nameOnGoogle)) {
                        //try to store the previous score
                        try {
                            //this is the previous score stored in the database which it is compared to the new score
                             oldscoreDb = snapshot.child(nameOnGoogle).child("score").getValue(String.class);
                            OldscoreSignGoogle = Integer.parseInt(oldscoreDb);
                            //new score
                            newScore = Integer.parseInt(score);
                            //catch the exception if it does arrive
                        }catch (Exception e){

                        }
                        //check if the new score is bigger then the previous ,
                        // if it is the new score will be stored in the database
                        if (newScore > OldscoreSignGoogle) {
                            databaseReference.child("scores").child(nameOnGoogle).child("score").setValue(score);

                        }
                        //if there isn't the username inside the database table scores store the details inside the database
                    } else {
                        databaseReference.child("scores").child(nameOnGoogle).child("username").setValue(nameOnGoogle);
                        databaseReference.child("scores").child(nameOnGoogle).child("score").setValue(score);
                    }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
        //setting recycleView fixed for evey item in the recycleView
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(OthersScore.this));
        list = new ArrayList<>();
        myAdapter = new myAdapter(OthersScore.this, list);
        //set adapter to recycleView
        recyclerView.setAdapter(myAdapter);
        //creating a second databasereference for the scores table
        databaseReference2 = firebaseDatabase.getReference("scores");

        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //get the children inside the scores table and store them as dataSnapshot
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    //creating a user item with user details
                    Users user = dataSnapshot.getValue(Users.class);
                    //adding the item to the list
                    list.add(user);
                    //sorting the list by the smallest score to the biggest
                    try {
                        list.sort(Comparator.comparing(a -> Integer.parseInt(a.getScore())));
                    } catch(Exception e){

                    }
                        //reverse the sorting by the highest to the lowest
                        Collections.reverse(list);

                }
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //log out button triggered
        btnLogoutOthers.setOnClickListener(new View.OnClickListener() {
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
                startActivity(new Intent(OthersScore.this, MainActivity.class));
                finish();
            }
        });
    }
}
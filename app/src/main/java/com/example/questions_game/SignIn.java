package com.example.questions_game;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignIn extends AppCompatActivity {
    //declaring private variables
    private TextView txtSignUp, txtSignErrors;
    private EditText edtUsername, edtPassword;
    private Button btnLogin;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        //TextView
        txtSignUp = findViewById(R.id.txtSignUp);
        txtSignErrors = findViewById(R.id.txtSignErrors);
        //EditText
        edtUsername = findViewById(R.id.edtUsernameSignIn);
        edtPassword = findViewById(R.id.edtPasswordSignIn);
        //firebase database
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReferenceFromUrl("https://questions-game-a1441-default-rtdb.firebaseio.com/");
        //Button
        btnLogin = findViewById(R.id.btnLogIn);
        //move to another activity when txtSignUp is clicked
        viewClicked(txtSignUp, SignIn.this, Register.class);
        //btnLogin clicked
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //storing the EdiText  username and password text into two variables
                String username = edtUsername.getText().toString();
                String password = edtPassword.getText().toString();
                // Storing data into SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
                // Creating an Editor object to edit(write to the file)
                SharedPreferences.Editor myEdit = sharedPreferences.edit();
                // Storing the key and its value as the data fetched from edittext
                myEdit.putString("name", username);
                // Once the changes have been made,
                // we need to commit to apply those changes made,
                // otherwise, it will throw an error
                myEdit.commit();
                //check if username and password fields are empty
                if (username.isEmpty() || password.isEmpty()) {
                    txtSignErrors.setVisibility(View.VISIBLE);
                    txtSignErrors.setText("Please fill all the credentials.");
                } else {
                    databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            // check if username exist in firebase database
                            if (snapshot.hasChild(username)) {
                                //username exists in firebase database
                                //now get password of user from firebase data and match it with user entered password
                                final String getPassword = snapshot.child(username).child("password").getValue(String.class);
                                //check if the password entered by the user is equal to the password inside the database
                                if (getPassword.equals(password)) {
                                    Toast.makeText(SignIn.this, "Welcome " + username, Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(SignIn.this, Game.class);
//                                    intent.putExtra("userName",username);
                                    startActivity(intent);
                                    //if the passwords are not the same
                                } else {
                                    txtSignErrors.setVisibility(View.VISIBLE);
                                    txtSignErrors.setText("The username or password is incorrect");
                                }
                                //if the username is not found
                            } else {
                                txtSignErrors.setVisibility(View.VISIBLE);
                                txtSignErrors.setText("The username is not on the system,\n please register your account");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }
        });

    }
    //general method for moving into a different activity once a view is clicked
    void viewClicked(View v, Context context, Class<?> c) {
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, c);
                startActivity(intent);
                finish();
            }
        });
    }
}
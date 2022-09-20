package com.example.questions_game;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
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

public class Register extends AppCompatActivity {
    //declaring private variables
private TextView txtRegister, txtErrors;
private EditText edtUsername, edtPassowrd, edtEmail ,edtRepassword;
private Button btnRegister;
private FirebaseDatabase firebaseDatabase;
private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //TextView
        txtErrors = findViewById(R.id.txtErrors);
        txtRegister = findViewById(R.id.txtRegister);
        //EditText
        edtUsername = findViewById(R.id.edtUsername);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassowrd = findViewById(R.id.edtPassword);
        edtRepassword= findViewById(R.id.edtRePassword);
        //Button
        btnRegister = findViewById(R.id.btnRegister);
        //firebase
        firebaseDatabase = FirebaseDatabase.getInstance();
        //create Object of DatabaseReference class to access firebase's realtime database
        databaseReference = firebaseDatabase.getReferenceFromUrl("https://questions-game-a1441-default-rtdb.firebaseio.com/");
        viewClicked(txtRegister,Register.this, SignIn.class);

        // if the button register is clicked
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get data from ediText
                String username = edtUsername.getText().toString();
                String email = edtEmail.getText().toString();
                String password = edtPassowrd.getText().toString();
                String rePassword =edtRepassword.getText().toString();
                //check if the user fill all the fields
                if(username.isEmpty() || email.isEmpty() || password.isEmpty() || rePassword.isEmpty()){
                    txtErrors.setVisibility(View.VISIBLE);
                    txtErrors.setText("Please fill all the fields");

                }else{

                    //check if the passwords are matching or not
                    if(!password.equals(rePassword)){
                        txtErrors.setVisibility(View.VISIBLE);
                        txtErrors.setText("The passwords don't match.");
                    }else {
                        //check if the email is in correct format
                        if (!validateEmail(email)) {
                            txtErrors.setVisibility(View.VISIBLE);
                            txtErrors.setText("Enter a correct email");
                        } else {

                        databaseReference.child("users").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                //check if username is registered before
                                if (snapshot.hasChild(username)) {
                                    txtErrors.setVisibility(View.VISIBLE);
                                    txtErrors.setText("The username is already registered");
                                }else {
                                    //sending data to firebase realtime database
                                    //using username as a unique identifier of every user
                                    //so all the details of user comes under the username
                                    databaseReference.child("users").child(username).child("email").setValue(email);
                                    databaseReference.child("users").child(username).child("password").setValue(password);
                                    //show a success message then finish activity
                                    Toast.makeText(Register.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                                    //goes to the sign in activity once the user successfully registered
                                    Intent intent = new Intent(Register.this, SignIn.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }



                    }

                }
            }
        });
    }
    //move to another activity
    void viewClicked(View v, Context context, Class<?> c){
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, c);
                startActivity(intent);
                finish();
            }
        });
    }
    //validating the email
    public boolean validateEmail(CharSequence email){
        return((!TextUtils.isEmpty(email)) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }
}
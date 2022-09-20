package com.example.questions_game;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class Game extends AppCompatActivity {
    // declaring variables
    private TextView txtTimer, txtScore, txtQuestions;
    private Button btn1, btn2, btn3, btn4;
    private GoogleSignInOptions gso;
    private GoogleSignInClient gsc;
    int correct = 0;
    int wrong = 0;
    int total = 0;
    int count = 0;
    String score = "";
    int timer = 50;
   private  FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    String totalQ, correctA,wrongA;
    private Random random;
    //an empty constructor
    public Game(){

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        //google sign In building the request email and send it to the google client
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);
        //storing the account detail
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        // if the account is not null
        if (account != null) {
            //store the account name into a string variable
            String personName = account.getDisplayName();
            Toast.makeText(this, "Welcome " + personName, Toast.LENGTH_SHORT).show();

        }
        //TextView
        txtScore = findViewById(R.id.txtScore);
        txtTimer = findViewById(R.id.txtTimer);
        txtQuestions = findViewById(R.id.txtQuestions);
        //Buttons
        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);
        btn4 = findViewById(R.id.btn4);
        //Random
        random = new Random();
        //update questions method which it is the logic of the game
        updateQuestion();
        //timer
        AddTimer();

    }

    //make the timer into this format 0:00
    public String checkDigit(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }

    //creating a timer
    public void AddTimer() {
        new CountDownTimer(50000, 1000) {

            //creating the timer
            @Override
            public void onTick(long millisUntilFinished) {
                txtTimer.setText("Timer: 0:" + checkDigit(timer));
                timer--;
            }
            //once the timer get to 0 therefore finished it will go to the next activity
            @Override
            public void onFinish() {
                //goes to the next activity
                NextActivity();
                //convert the int variables into string
                totalQ= Integer.toString(total-1);
                correctA= Integer.toString(correct);
                wrongA= Integer.toString(wrong);
                //sharePreference for storing total,correct and incorrect
                SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
                SharedPreferences.Editor myEdit = sharedPreferences.edit();
                myEdit.putString("total",totalQ);
                myEdit.putString("correct", correctA);
                myEdit.putString("incorrect", wrongA);
                myEdit.commit();
            }
        }.start();
    }

    void updateQuestion() {

        //if there are no more questions
        if (count > 49) {
            NextActivity();
           //converting int variables into string
            totalQ= Integer.toString(total-1);
            correctA= Integer.toString(correct);
            wrongA= Integer.toString(wrong);
            //sharePreference for storing total,correct and incorrect
            SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
            SharedPreferences.Editor myEdit = sharedPreferences.edit();
            myEdit.putString("total",totalQ);
            myEdit.putString("correct", correctA);
            myEdit.putString("incorrect", wrongA);
            myEdit.commit();
            // if there are still questions
        } else {
            //random number
            int rndNumber= random.nextInt(50);
            // database
            firebaseDatabase = FirebaseDatabase.getInstance();
            //get the values of the child questions
            databaseReference = firebaseDatabase.getReference().child("Questions").child(String.valueOf(rndNumber));
            //keeps track of the numbers of questions
            total++;
            //valueEvent listener
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    //store the question, the answer and options
                    // that were taken form the database into a final variable.
                    final Question question = snapshot.getValue(Question.class);
                    //display them
                    txtQuestions.setText(question.getQuestion());
                    btn1.setText(question.getOption1());
                    btn2.setText(question.getOption2());
                    btn3.setText(question.getOption3());
                    btn4.setText(question.getOption4());
                    // if button 1 clicked
                    btn1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // if the answer is button 1
                            if (btn1.getText().toString().equals(question.getAnswer())) {
                                Toast.makeText(Game.this, "Correct Answer", Toast.LENGTH_SHORT).show();
                                btn1.setBackgroundColor(Color.GREEN);
                                //count allow the app to move to the next questions
                                count++;
                                //keep tracks of numbers of correct answers
                                correct++;
                                //cast integer into string
                                score = Integer.toString(correct);
                                txtScore.setText("Score: " + score);

                                //allow the button 1 to change to default colour
                                Handler handler = new Handler();
                                //thread
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        //change the colour back to white after 2 seconds
                                        btn1.setBackgroundColor(Color.WHITE);
                                        //recursion in order to check if the button count 49
                                        updateQuestion();
                                    }
                                }, 2000);

                                //if the button 1 is not the right answer
                            } else {
                                //if the answer is not button 1
                                Toast.makeText(Game.this, "Incorrect Answer", Toast.LENGTH_SHORT).show();
                                //keep tracks of the number of wrong answers
                                wrong++;
                                //the button 1 became red
                                btn1.setBackgroundColor(Color.RED);
                                //checking which button is the answer and displayed it green
                                //so the user knows which answer is the right one.
                                if (btn2.getText().toString().equals(question.getAnswer())) {
                                    btn2.setBackgroundColor(Color.GREEN);
                                } else if (btn3.getText().toString().equals(question.getAnswer())) {
                                    btn3.setBackgroundColor(Color.GREEN);
                                } else if (btn4.getText().toString().equals(question.getAnswer())) {
                                    btn4.setBackgroundColor(Color.GREEN);
                                }
                                count++;
                                //allow the button 1 to change to default colour
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        //change the colour of the buttons back to default
                                        btn1.setBackgroundColor(Color.WHITE);
                                        if (btn2.getText().toString().equals(question.getAnswer())) {
                                            btn2.setBackgroundColor(Color.WHITE);
                                        } else if (btn3.getText().toString().equals(question.getAnswer())) {
                                            btn3.setBackgroundColor(Color.WHITE);
                                        } else if (btn4.getText().toString().equals(question.getAnswer())) {
                                            btn4.setBackgroundColor(Color.WHITE);
                                        }
                                        //recursion until the count reach 49
                                        updateQuestion();
                                    }
                                }, 2000);
                            }
                        }
                    });
                    // if button 2 clicked
                    btn2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // if the answer is button 1
                            if (btn2.getText().toString().equals(question.getAnswer())) {
                                Toast.makeText(Game.this, "Correct Answer", Toast.LENGTH_SHORT).show();
                                btn2.setBackgroundColor(Color.GREEN);
                                correct++;
                                count++;
                                score = Integer.toString(correct);
                                txtScore.setText("Score: " + score);
                                //allow the button 2 to change to default colour
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        btn2.setBackgroundColor(Color.WHITE);
                                        updateQuestion();
                                    }
                                    //two seconds delays
                                }, 2000);

                            } else {
                                //if the answer is not button 2
                                Toast.makeText(Game.this, "Incorrect Answer", Toast.LENGTH_SHORT).show();
                                wrong++;
                                btn2.setBackgroundColor(Color.RED);
                                if (btn1.getText().toString().equals(question.getAnswer())) {
                                    btn1.setBackgroundColor(Color.GREEN);
                                } else if (btn3.getText().toString().equals(question.getAnswer())) {
                                    btn3.setBackgroundColor(Color.GREEN);
                                } else if (btn4.getText().toString().equals(question.getAnswer())) {
                                    btn4.setBackgroundColor(Color.GREEN);
                                }
                                count++;
                                //allow the button 2 to change to default colour
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        btn2.setBackgroundColor(Color.WHITE);
                                        if (btn1.getText().toString().equals(question.getAnswer())) {
                                            btn1.setBackgroundColor(Color.WHITE);
                                        } else if (btn3.getText().toString().equals(question.getAnswer())) {
                                            btn3.setBackgroundColor(Color.WHITE);
                                        } else if (btn4.getText().toString().equals(question.getAnswer())) {
                                            btn4.setBackgroundColor(Color.WHITE);
                                        }
                                        updateQuestion();
                                    }
                                }, 2000);
                            }
                        }
                    });
                    // if button 3 clicked
                    btn3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // if the answer is button 3
                            if (btn3.getText().toString().equals(question.getAnswer())) {
                                Toast.makeText(Game.this, "Correct Answer", Toast.LENGTH_SHORT).show();
                                btn3.setBackgroundColor(Color.GREEN);
                                correct++;
                                count++;
                                score = Integer.toString(correct);
                                txtScore.setText("Score: " + score);
                                //allow the button 3 to change to default colour
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        btn3.setBackgroundColor(Color.WHITE);
                                        updateQuestion();
                                    }
                                }, 2000);


                            } else {
                                //if the answer is not button 3
                                Toast.makeText(Game.this, "Incorrect Answer", Toast.LENGTH_SHORT).show();
                                wrong++;
                                btn3.setBackgroundColor(Color.RED);
                                if (btn1.getText().toString().equals(question.getAnswer())) {
                                    btn1.setBackgroundColor(Color.GREEN);
                                } else if (btn2.getText().toString().equals(question.getAnswer())) {
                                    btn2.setBackgroundColor(Color.GREEN);
                                } else if (btn4.getText().toString().equals(question.getAnswer())) {
                                    btn4.setBackgroundColor(Color.GREEN);
                                }
                                count++;
                                //allow the button 3 to change to default colour
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        btn3.setBackgroundColor(Color.WHITE);
                                        if (btn1.getText().toString().equals(question.getAnswer())) {
                                            btn1.setBackgroundColor(Color.WHITE);
                                        } else if (btn2.getText().toString().equals(question.getAnswer())) {
                                            btn2.setBackgroundColor(Color.WHITE);
                                        } else if (btn4.getText().toString().equals(question.getAnswer())) {
                                            btn4.setBackgroundColor(Color.WHITE);
                                        }
                                        updateQuestion();
                                    }
                                }, 2000);
                            }
                        }
                    });
                    // if button 4 clicked
                    btn4.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // if the answer is button 4
                            if (btn4.getText().toString().equals(question.getAnswer())) {
                                Toast.makeText(Game.this, "Correct Answer", Toast.LENGTH_SHORT).show();
                                btn4.setBackgroundColor(Color.GREEN);
                                correct++;
                                count++;
                                score = Integer.toString(correct);
                                txtScore.setText("Score: " + score);

                                //allow the button 4 to change to default colour
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        btn4.setBackgroundColor(Color.WHITE);
                                        updateQuestion();
                                    }
                                }, 2000);


                            } else {
                                //if the answer is not button 1
                                Toast.makeText(Game.this, "Incorrect Answer", Toast.LENGTH_SHORT).show();
                                wrong++;
                                btn4.setBackgroundColor(Color.RED);
                                if (btn2.getText().toString().equals(question.getAnswer())) {
                                    btn2.setBackgroundColor(Color.GREEN);
                                } else if (btn3.getText().toString().equals(question.getAnswer())) {
                                    btn3.setBackgroundColor(Color.GREEN);
                                } else if (btn1.getText().toString().equals(question.getAnswer())) {
                                    btn1.setBackgroundColor(Color.GREEN);
                                }
                                //count keep track of questions
                                count++;
                                //allow the button 4 to change to default colour
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        btn4.setBackgroundColor(Color.WHITE);
                                        if (btn2.getText().toString().equals(question.getAnswer())) {
                                            btn2.setBackgroundColor(Color.WHITE);
                                        } else if (btn3.getText().toString().equals(question.getAnswer())) {
                                            btn3.setBackgroundColor(Color.WHITE);
                                        } else if (btn1.getText().toString().equals(question.getAnswer())) {
                                            btn1.setBackgroundColor(Color.WHITE);
                                        }
                                        updateQuestion();
                                    }
                                }, 2000);
                            }
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

    }

    //goes to the next activity gameOver class
    void NextActivity() {
        Toast.makeText(this, "Game Over", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Game.this, GameOver.class);
        startActivity(intent);
        finish();
    }


}
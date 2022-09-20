package com.example.questions_game;

public class Users {
    //declaring the private variables
    private String username;
    private String score;
    //the constructor
    public Users(){}
    //the constructor
    public Users(String username, String score) {
        this.username = username;
        this.score = score;
    }
    //getter and setter methods
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }
}


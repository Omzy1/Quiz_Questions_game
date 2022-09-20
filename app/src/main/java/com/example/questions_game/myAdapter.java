package com.example.questions_game;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class myAdapter extends RecyclerView.Adapter<myAdapter.MyVewHolder> {
    //context
    Context context;
    //list of users
    ArrayList<Users> list;
    //the constructor
    public myAdapter(Context context, ArrayList<Users> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyVewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //create the viewHolder
        View v = LayoutInflater.from(context).inflate(R.layout.item, parent,false);
        return new MyVewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyVewHolder holder, int position) {
        //show the text
        Users user = list.get(position);
        holder.name.setText("Username: " + user.getUsername());
        holder.score.setText("Score: " + user.getScore());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    //viewHolder class to hold view reference for every item in the recycleView
    public static class MyVewHolder extends RecyclerView.ViewHolder{
      private TextView name,score;
        public MyVewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            score = itemView.findViewById(R.id.score);

        }
    }

}

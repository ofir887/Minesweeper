package com.example.ofirm.minesweeper;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.view.LayoutInflater;
import android.widget.TextView;


import java.util.ArrayList;

/**
 * Created by OfirMonis on 27/12/2015.
 */
public class ScoreAdapter extends ArrayAdapter<Score> {

    ArrayList<Score> scores = new ArrayList<>();

    public ScoreAdapter(Context context, ArrayList<Score> score) {
        super(context, 0, score);

    }



        public View getView(int position, View convertView, ViewGroup parent) {

            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.score_row, parent, false);
                    Score score = getItem(position);
                    TextView time = (TextView) convertView.findViewById(R.id.ScoreField);
                    int minuets  = score.getTime()/60;
                    int secs = score.getTime() % 60;
                    String time1 = String.valueOf(minuets)+":"+String.format("%02d",secs);
                    if (score.getTime() != 0)
                        time.setText(time1);
                    else
                        time.setText("");
                    TextView name = (TextView) convertView.findViewById(R.id.NameField);
                    name.setText(score.getName());

            }







            // Lookup view for data population
            // Populate the data into the template view using the data object
            // Return the completed view to render on screen
            return convertView;


        }
    public void setScores(ArrayList<Score> score){
        this.scores = score;
    }
}



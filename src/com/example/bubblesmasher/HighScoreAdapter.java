package com.example.bubblesmasher;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class HighScoreAdapter extends ArrayAdapter<Score>{

	private final Context context;
    private final ArrayList<Score> ScoreArrayList;

    public HighScoreAdapter(Context context, ArrayList<Score> ScoreArrayList) {

        super(context, R.layout.row, ScoreArrayList);

        this.context = context;
        this.ScoreArrayList = ScoreArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.row, parent, false);

        TextView username = (TextView) rowView.findViewById(R.id.label);
        TextView score = (TextView) rowView.findViewById(R.id.value);
 
        //Settext into textview
        username.setText(ScoreArrayList.get(position).getUsername());
        score.setText(ScoreArrayList.get(position).getScore()		);

        return rowView;
    }
}

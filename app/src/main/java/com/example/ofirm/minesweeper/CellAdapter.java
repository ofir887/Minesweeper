package com.example.ofirm.minesweeper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

public class CellAdapter extends ArrayAdapter<Cell>{
    public CellAdapter(Context context, ArrayList<Cell> cell) {
        super(context, 0, cell);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            viewHolder = new ViewHolder();

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.game_cell, parent, false);
            viewHolder.imageView = (ImageView)convertView.findViewById(R.id.ImageView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        Cell cell = getItem(position);

        switch (cell.getStatus()){
            case Cell.COVERED:
                viewHolder.imageView.setImageResource(R.drawable.cell);
                break;
            case Cell.FLAGGED:
                viewHolder.imageView.setImageResource(R.drawable.flag);
                break;
            case Cell.MINED:
                viewHolder.imageView.setImageResource(R.drawable.mine);
                break;
            case Cell.EMPTY:
                viewHolder.imageView.setImageResource(R.drawable.empty_cell);
                cell.setClickable(false);
                break;
            case Cell.RED_MINE:
                viewHolder.imageView.setImageResource(R.drawable.red_mine);
                break;
            case Cell.WRONG_MINED:
                viewHolder.imageView.setImageResource(R.drawable.wrong_mine);
                break;
            case Cell.NUMBER:
                cell.setClickable(false);
                switch (cell.getNumberOfMines()){
                    case 1:
                        viewHolder.imageView.setImageResource(R.drawable.one);
                        break;
                    case 2:
                        viewHolder.imageView.setImageResource(R.drawable.two);
                        break;
                    case 3:
                        viewHolder.imageView.setImageResource(R.drawable.three);
                        break;
                    case 4:
                        viewHolder.imageView.setImageResource(R.drawable.four);
                        break;
                    case 5:
                        viewHolder.imageView.setImageResource(R.drawable.five);
                        break;
                    case 6:
                        viewHolder.imageView.setImageResource(R.drawable.six);
                        break;
                    case 7:
                        viewHolder.imageView.setImageResource(R.drawable.seven);
                        break;
                    case 8:
                        viewHolder.imageView.setImageResource(R.drawable.eight);
                        break;
                }
                this.isEnabled(position);
        }
        // Lookup view for data population
        // Populate the data into the template view using the data object
        // Return the completed view to render on screen
        return convertView;


    }

    @Override
    public boolean isEnabled(int position){
        return this.getItem(position).getClickable();
    }

    static class  ViewHolder{
        ImageView imageView;
    }
}

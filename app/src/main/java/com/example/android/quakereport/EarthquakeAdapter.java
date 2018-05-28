package com.example.android.quakereport;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by darip on 13-01-2018.
 */

public class EarthquakeAdapter extends ArrayAdapter<Earthquake> {

    public EarthquakeAdapter(Context context, List<Earthquake> arrayList){
        super(context, 0 ,arrayList);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if(listItemView == null)
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        Earthquake earthquake = getItem(position);
        TextView mag = (TextView) listItemView.findViewById(R.id.item_mag);
        TextView location = (TextView) listItemView.findViewById(R.id.item_location);
        TextView place = (TextView) listItemView.findViewById(R.id.item_place);
        TextView date = (TextView) listItemView.findViewById(R.id.item_date);
        TextView time = (TextView) listItemView.findViewById(R.id.item_time);
        GradientDrawable magCircle = (GradientDrawable) mag.getBackground();
        int magColor = getMagColor(earthquake.getMag());
        magCircle.setColor(magColor);
        mag.setText(earthquake.getMag());
        location.setText(earthquake.getLocation());
        place.setText(earthquake.getPlace());
        date.setText(earthquake.getDate());
        time.setText(earthquake.getTime());

        return listItemView;
    }

    public int getMagColor(String mag){
        double magnitude = Double.parseDouble(mag);
        int magnitudeColorResourceId;
        int mg = (int) Math.floor(magnitude);
        switch(mg){
            case 0:
            case 1:
                magnitudeColorResourceId = R.color.magnitude1;
                break;
            case 2:
                magnitudeColorResourceId = R.color.magnitude2;
                break;
            case 3:
                magnitudeColorResourceId = R.color.magnitude3;
                break;
            case 4:
                magnitudeColorResourceId = R.color.magnitude4;
                break;
            case 5:
                magnitudeColorResourceId = R.color.magnitude5;
                break;
            case 6:
                magnitudeColorResourceId = R.color.magnitude6;
                break;
            case 7:
                magnitudeColorResourceId = R.color.magnitude7;
                break;
            case 8:
                magnitudeColorResourceId = R.color.magnitude8;
                break;
            case 9:
                magnitudeColorResourceId = R.color.magnitude9;
                break;
            default:
                magnitudeColorResourceId = R.color.magnitude10plus;
                break;
        }
        return ContextCompat.getColor(getContext(), magnitudeColorResourceId);
    }
}

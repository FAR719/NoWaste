package com.far.nowaste;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CardViewAdapter extends BaseAdapter {

    //istanzia il layout costruito
    LayoutInflater mInflater;

    String[] wasteTypes;

    public CardViewAdapter(Context c, String[] w) {
        wasteTypes = w;
        mInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    // definizione metodi necessari
    @Override
    public int getCount() {
        return wasteTypes.length;
    }

    @Override
    public Object getItem(int position) {
        return wasteTypes[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //view w viewgroup sono inutilizzate, spero che single_main_cardview sia giusto
        View v = mInflater.inflate(R.layout.single_main_cardview, null);
        //cerca nella view
        TextView wasteCardTextView = (TextView) v.findViewById(R.id.wasteCardTextView);
        ImageView wasteCardImageView = (ImageView) v.findViewById(R.id.wasteCardImageView);

        // ottieni i valori
        String wasteType = wasteTypes[position];

        // bisogna ottenere e settare i valori dell'immagine

        wasteCardTextView.setText(wasteType);

        return null;
    }
}

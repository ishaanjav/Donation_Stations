package com.example.anany.restaurantleftoverfood.Adapters;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.anany.restaurantleftoverfood.R;

import java.util.ArrayList;
import java.util.List;

public class StatusAdapter extends ArrayAdapter<String> {

    private List<String> mData;
    public Resources mResources;
    private LayoutInflater mInflater;

    public StatusAdapter(@NonNull Context context, int resource, @NonNull List<String> objects, Resources resLocal) {
        super(context, resource, objects);
        mData = objects;
        mResources = resLocal;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {

        View row = mInflater.inflate(R.layout.status_spinner, parent, false);
        TextView label = (TextView) row.findViewById(R.id.list_item);
        label.setText(mData.get(position).toString());


        //Set meta data here and later we can access these values from OnItemSelected Event Of Spinner
        row.setTag(R.string.meta_position, Integer.toString(position));
        row.setTag(R.string.meta_title, mData.get(position).toString());

        return row;
    }


}

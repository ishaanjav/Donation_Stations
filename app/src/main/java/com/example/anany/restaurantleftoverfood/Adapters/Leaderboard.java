package com.example.anany.restaurantleftoverfood.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.anany.restaurantleftoverfood.R;
import com.example.anany.restaurantleftoverfood.Storage.RequestDonation;

import java.util.ArrayList;
import java.util.List;

public class Leaderboard extends ArrayAdapter<com.example.anany.restaurantleftoverfood.Storage.Leaderboard> {
    private Context mContext;
    private List<com.example.anany.restaurantleftoverfood.Storage.Leaderboard> list = new ArrayList<>();

    public Leaderboard(@NonNull Context context, ArrayList<com.example.anany.restaurantleftoverfood.Storage.Leaderboard> list) {
        super(context, 0, list);
        mContext = context;
        this.list = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        final com.example.anany.restaurantleftoverfood.Storage.Leaderboard row = list.get(position);
        listItem = LayoutInflater.from(mContext).inflate(R.layout.leaderboard, parent, false);

        TextView name = listItem.findViewById(R.id.name);
        TextView points = listItem.findViewById(R.id.points);
        name.setText(row.getName());
        points.setText(row.getDonationPoints());

        return listItem;
    }
}

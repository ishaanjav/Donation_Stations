package com.example.anany.restaurantleftoverfood.Adapters;

import android.content.Context;
import android.graphics.Movie;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.anany.restaurantleftoverfood.R;
import com.example.anany.restaurantleftoverfood.RestaurantInfo;

import java.util.ArrayList;
import java.util.List;

public class RestaurantAdapter extends ArrayAdapter<RestaurantInfo> {
    private Context mContext;
    private List<RestaurantInfo> list = new ArrayList<>();

    public RestaurantAdapter(@NonNull Context context, ArrayList<RestaurantInfo> list) {
        super(context, 0, list);
        mContext = context;
        this.list = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.restaurant_rows, parent, false);
/*

        RestaurantInfo row = list.get(position);

        TextView nameView = listItem.findViewById(R.id.shelterName);
        TextView addressView = listItem.findViewById(R.id.shelterAddress);
        TextView phoneView = listItem.findViewById(R.id.shelterManagerPhone);
        TextView managerView = listItem.findViewById(R.id.shelterManagerName);
        TextView timeView = listItem.findViewById(R.id.time);

        nameView.setText(row.getName());
        phoneView.setText(row.getContactPhone());
        addressView.setText(row.getAddress());
        managerView.setText(row.getManagerName());
        timeView.setText(row.getClosingTime());
*/

        return listItem;
    }
}
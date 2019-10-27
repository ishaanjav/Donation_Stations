package com.example.anany.restaurantleftoverfood;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anany.restaurantleftoverfood.Adapters.RestaurantAdapter;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Homepage extends AppCompatActivity {

    TextView title;
    Button viewMap;
    ListView listView;

    boolean firstTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        title = findViewById(R.id.title);

        viewMap = findViewById(R.id.viewMap);
        String info = "";
        try {
            info = readFromFile(getApplicationContext());
        } catch (IOException e) {
            makeToast(e.getMessage());
        }
        firstTime = false;

        listView = findViewById(R.id.list);

        if (info.contains("restaurant")) {
            title.setText("Shelters Using This App");
            //displayShelterInfo();
        } else if (info.contains("shelter")) {
            title.setText("Restaurants Using This App");
            displayRestaurantInfo();
        }

        //TODO do stuff with firstTime.txt
        readFirstTime();

        viewMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //LatLng coordinate = getLocationFromAddress(getApplicationContext(), "508 Flamingo Court Murphy Texas");
                //makeToast("Latitude: " + coordinate.latitude +"\n" + "Longitude: " + coordinate.longitude);
                startActivity(new Intent(Homepage.this, MapsActivity.class));
            }
        });

    }

    private void readFirstTime() {
        try {
            InputStream inputStream = getApplicationContext().openFileInput("firstTime.txt");
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }
                if (stringBuilder.toString().length() > 11) {
                    //INFO firstTime.txt exists which means it is their first time signing in.
                    firstTime = true;
                    showAppInfo();
                }
                //DONE if firstTime.txt exists show app info.
                //OLD if firstTime.txt exists, find a way to show how to navigate the app.
                //TODO if firstTime.txt exists, then use TourGuide (check Voice Dialer App) to show how to navigate.
                //TODO Then, delete firstTime.txt
                inputStream.close();
            }
        } catch (Exception e) {
            //README file doesn't exist.
            //makeToast(e.toString);
        }
    }

    @Override
    public void onBackPressed() {
        askIfSure();
        //super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.signed_in, menu);

        // return true so that the menu pop up is opened
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                showAppInfo();
                break;
            case R.id.signOut:
                signOut();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showAppInfo() {
        final Dialog alert = new Dialog(Homepage.this);
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert.setContentView(R.layout.about_this_app2);
        alert.setCancelable(true);

        alert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                //makeToast("Not showing.");
                if(firstTime) {
                    //Done Use TourGuide (check Voice Dialer App) to show how to navigate through Homepage.
                    //TODO Then, delete firstTime.txt
                    firstTime = false;
                }
            }
        });
        alert.show();
    }

    public void askIfSure() {
        final Dialog alert = new Dialog(Homepage.this);
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert.setContentView(R.layout.want_to_sign_out);
        alert.setCancelable(false);

        Window window = alert.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        window.setAttributes(wlp);

        ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
        InsetDrawable inset = new InsetDrawable(back, 5);
        alert.getWindow().setBackgroundDrawable(inset);

        TextView message = alert.findViewById(R.id.message);

        Button yes = alert.findViewById(R.id.yes);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //INFO they want to sign out.
                //makeToast("Work on signOut() function.");
                signOut();
            }
        });

        Button no = alert.findViewById(R.id.no);
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //INFO they don't want to sign out.
                alert.dismiss();
            }
        });

        alert.show();
    }

    private void signOut() {
        //DONE Delete info.txt.
        //DONE Delete signedIn.txt
        //DONE Go back to MainActivity.java
        File dir = getFilesDir();
        File file = new File(dir, "info.txt");
        boolean deleted = file.delete();
        File file2 = new File(dir, "signedIn.txt");
        boolean deleted2 = file2.delete();

        //makeToast("Deleted: \n" + deleted + "\n" + deleted2);
        startActivity(new Intent(Homepage.this, MainActivity.class));
    }

    public LatLng getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }

            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude());

        } catch (IOException e) {
            makeToast(e.toString());
        }

        return p1;
    }

    private void displayRestaurantInfo() {

        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("restaurants");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot) {
                RestaurantAdapter mAdapter;
                ArrayList<RestaurantInfo> restaurantList = new ArrayList<>();

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String name = child.child("Restaurant").getValue().toString();
                    String time = child.child("Closing Time").getValue().toString();
                    String address = child.child("Address").getValue().toString();
                    String manager = child.child("Manager").getValue().toString();
                    String phone = child.child("Phone").getValue().toString();
                    restaurantList.add(new RestaurantInfo(name, manager, phone, address, time));
                }
                makeToast("# of Restaurants: " + restaurantList.size());

                mAdapter = new RestaurantAdapter(getApplicationContext(), restaurantList);
                listView = findViewById(R.id.list);
                listView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled( DatabaseError databaseError) {

            }
        };

        dbref.addValueEventListener(valueEventListener);

    }

   /* private void displayShelterInfo() {

        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("shelters");

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot) {
                ShelterAdapter mAdapter;
                ArrayList<ShelterInfo> shelterList = new ArrayList<>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String name = child.child("Shelter").getValue().toString();
                    String address = child.child("Address").getValue().toString();
                    String manager = child.child("Manager").getValue().toString();
                    String phone = child.child("Phone").getValue().toString();
                    shelterList.add(new ShelterInfo(name, manager, phone, address));
                }
                makeToast("# of Shelters: " + shelterList.size());
                mAdapter = new ShelterAdapter(getApplicationContext(), shelterList);
                listView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled( DatabaseError databaseError) {

            }
        };

        dbref.addValueEventListener(valueEventListener);
    }
*/
    private String readFromFile(Context context) throws IOException {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput("info.txt");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            makeToast("Error: could not find info. \n" + e.toString());
        } catch (IOException e) {
            makeToast("Error: could not read file:\n" + e.toString());
        }

        return ret;
    }

    private void makeToast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

}

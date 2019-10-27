package com.example.anany.restaurantleftoverfood;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMinZoomPreference(6.0f);
        mMap.setMaxZoomPreference(15.0f);

        String info = "";
        try {
            info = readFromFile(getApplicationContext());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String address = "";
        String[] words = info.split(" ");
        for (int i = 1; i < words.length; i++) {
            address += words[i] + " ";
        }

        LatLng coordinate = getLocationFromAddress(getApplicationContext(), address);
        //makeToast("Latitude: " + coordinate.latitude + "\n" + "Longitude: " + coordinate.longitude);

        LatLngBounds AUSTRALIA = new LatLngBounds(
                new LatLng(coordinate.latitude - 0.4, coordinate.longitude - 0.4), new LatLng(coordinate.latitude + 0.4, coordinate.longitude + 0.4));

        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(AUSTRALIA, 0));

        String title = "";
        if (words[0].contains("rest")) {
            title = "Your Restaurant";
            makeShelterMarkers(mMap);
        } else {
            title = "Your Shelter";
            makeRestaurantMarkers(mMap);
        }
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.image2);
        mMap.addMarker(new MarkerOptions().position(coordinate).title(title)).setIcon(icon);

    }

    private void makeRestaurantMarkers(final GoogleMap map) {
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("restaurants");

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot) {
                ArrayList<String> address = new ArrayList<>();
                ArrayList<String> name = new ArrayList<>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    address.add(child.child("Address").getValue().toString());
                    name.add(child.child("Restaurant").getValue().toString());
                }
                int counter = 0;
                for (String t : address) {
                    LatLng coordinate = getLocationFromAddress(getApplicationContext(), t);
                    map.addMarker(new MarkerOptions().position(coordinate).title(name.get(counter)));
                    counter++;
                }

            }

            @Override
            public void onCancelled( DatabaseError databaseError) {

            }
        };

        dbref.addValueEventListener(valueEventListener);
    }

    private void makeShelterMarkers(final GoogleMap map) {
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("shelters");

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot) {
                ArrayList<String> address = new ArrayList<>();
                ArrayList<String> name = new ArrayList<>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    address.add(child.child("Address").getValue().toString());
                    name.add(child.child("Shelter").getValue().toString());
                }
                int counter = 0;
                for (String t : address) {
                    LatLng coordinate = getLocationFromAddress(getApplicationContext(), t);
                    map.addMarker(new MarkerOptions().position(coordinate).title(name.get(counter)));
                    counter++;
                }

            }

            @Override
            public void onCancelled( DatabaseError databaseError) {

            }
        };

        dbref.addValueEventListener(valueEventListener);
    }

    private String readFromFile(Context context) throws IOException {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput("info.txt");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                //I think what this is doing is only storing the last line of the file which is the address.
                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            makeToast("File not found: " + e.toString());
        } catch (IOException e) {
            makeToast("Can not read file: " + e.toString());
        }

        return ret;
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


    private void makeToast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

}

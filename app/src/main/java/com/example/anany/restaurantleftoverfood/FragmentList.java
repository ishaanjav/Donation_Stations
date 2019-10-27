package com.example.anany.restaurantleftoverfood;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anany.restaurantleftoverfood.Adapters.ShelterAdapter;
import com.example.anany.restaurantleftoverfood.Storage.ShelterInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentList.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentList extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static Context mContext;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FragmentList() {
        // Required empty public constructor
    }

    TextView title;
    Button viewMap;
    ListView listView;
    String type;
    String address;
    double searchRadius, lat, lon;
    String units, distance, name;
    boolean inList, inMap;
    Dialog alert;

    boolean doneLoading;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1   Parameter 1.
     * @param param2   Parameter 2.
     * @param mContext
     * @return A new instance of fragment FragmentList.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentList newInstance(String param1, String param2, Context context) {
        FragmentList fragment = new FragmentList();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        mContext = context;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        /*
        }*/
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        title = getView().findViewById(R.id.title);
        type = "";
        address = "";
        viewMap = getView().findViewById(R.id.viewMap);
        readFromFile();
        listView = getView().findViewById(R.id.list);

        //TODO ListView tap vs scroll (for phone icon)
        //TODO When actually releasing app, in ShelterAdapter, comment out code to send the call.

        if (inList) {
            title.setText("Nearby Restaurants / Shelters");
            title.setTextSize(24.0f);
        } else if (type.contains("est")) {
            title.setText("Nearby Shelters");
            title.setTextSize(26.0f);
        } else if (type.contains("elt")) {
            title.setTextSize(26.0f);
            title.setText("Nearby Restaurants");
        }
        doneLoading = false;
        displayInfo();
        mapClicker();
        super.onViewCreated(view, savedInstanceState);
    }

    private void mapClicker() {
        viewMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TabLayout layout = (TabLayout) ((Home) getActivity()).findViewById(R.id.tabs);
                layout.getTabAt(2).select();
            }
        });
    }

    String result = "";

    private void displayInfo() {
        if (!doneLoading) {
            alert = new Dialog(mContext);
            alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
            alert.setContentView(R.layout.loading);
            alert.setCancelable(true);

            //alert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            alert.show();
        }

        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Places");

        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int position = listView.getFirstVisiblePosition();

                ShelterAdapter mAdapter;
                ArrayList<ShelterInfo> shelterList = new ArrayList<>();
                ArrayList<Double> distances = new ArrayList<>();

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String type2 = child.child("Type").getValue().toString();
                    boolean discoverable = Boolean.parseBoolean(child.child("Discoverable to " + type).getValue().toString());
                    double latT = Double.parseDouble(child.child("Latitude").getValue().toString());
                    double lonT = Double.parseDouble(child.child("Longitude").getValue().toString());
                    if (discoverable) {
                        if (!type2.equals(type)) {
                            double dis = goodDistance(latT, lonT);
                            if (dis <= searchRadius) {
                                String name2 = child.child("Name").getValue().toString();
                                String addressMa = child.child("Address").getValue().toString();
                                String manager = child.child("Manager Name").getValue().toString();
                                String phone = child.child("Phone").getValue().toString();
                                String website = child.child("Website").getValue().toString();
                                String openingHours = child.child("Hours").getValue().toString();

                                //DONE Change below to accept boolean if it is same type.
                                //DONE Change below to accept website, opening hours, distance, and units.
                                //DONE If restaurant, have to read from # of Donations and pass it in as status
                                distances.add(dis);
                                if (type2.contains("elt")) {
                                    shelterList.add(new ShelterInfo(name2, manager, phone, addressMa, website, openingHours, dis + "", units, false, type2, dis, latT, lonT, true, child.child("Food Status").getValue().toString(), child.child("Amount").getValue().toString()));
                                } else {
                                    shelterList.add(new ShelterInfo(name2 , manager, phone, addressMa, website, openingHours, dis + "", units, false, type2, dis, latT, lonT, true, child.child("Num of Donations").getValue().toString(), "No food required."));
                                }
                            }
                        } else if (inList) {
                            double dis = goodDistance(latT, lonT);
                            String address2 = child.child("Address").getValue().toString();
                            if (dis <= searchRadius && !address2.equals(address)) {
                                String name2 = child.child("Name").getValue().toString();
                                String manager = child.child("Manager Name").getValue().toString();
                                String phone = child.child("Phone").getValue().toString();
                                String website = child.child("Website").getValue().toString();
                                String openingHours = child.child("Hours").getValue().toString();

                                distances.add(dis);
                                if (type2.contains("elt")) {
                                    shelterList.add(new ShelterInfo(name2, manager, phone, address2, website, openingHours, dis + "", units, true, type2, dis, latT, lonT, inMap, child.child("Food Status").getValue().toString(), child.child("Amount").getValue().toString()));
                                } else {
                                    shelterList.add(new ShelterInfo(name2, manager, phone, address2, website, openingHours, dis + "", units, true, type2, dis, latT, lonT, inMap, child.child("Num of Donations").getValue().toString(), "No food required."));
                                }
                            }
                        }

                    }
                }
                doneLoading = true;

                TreeMap<Double, ShelterInfo> tm = new TreeMap<>();

                for (int i = 0; i < distances.size(); i++) {
                    tm.put(distances.get(i), shelterList.get(i));
                }

                ArrayList<ShelterInfo> sorted = new ArrayList<>();
                for (Map.Entry<Double, ShelterInfo> entry : tm.entrySet()) {
                    sorted.add(entry.getValue());
                }


                mAdapter = new ShelterAdapter(mContext, sorted);
                listView.setAdapter(mAdapter);
                if (position + 1 < sorted.size())
                    listView.setSelection(position + 1);
                else
                    listView.setSelection(sorted.size() - 1);
                alert.dismiss();
                alert.cancel();
                //title.setText(result);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                alert.dismiss();
                alert.cancel();
                makeToast("Error getting results. Make sure you have a good WiFi connection.");
            }
        });

    }

    private double goodDistance(double latT, double lonT) {
        //result += "  " + Math.sqrt(Math.pow(latT - lat, 2) + Math.pow(lonT - lon, 2));
        return Math.sqrt(Math.pow(latT - lat, 2) + Math.pow(lonT - lon, 2));
    }

    private void readFromFile() {
        try {
            InputStream inputStream = mContext.openFileInput("info.txt");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                int count = 0;
                while ((receiveString = bufferedReader.readLine()) != null) {
                    if (count == 0) {
                        type = receiveString;
                    }
                    if (count == 1) {
                        address = receiveString;
                    }
                    if (count == 2) {
                        searchRadius = Double.parseDouble(receiveString);
                    }
                    if (count == 3) {
                        lat = Double.parseDouble(receiveString);
                    }
                    if (count == 4) {
                        lon = Double.parseDouble(receiveString);
                    }
                    if (count == 5) {
                        units = receiveString;
                    }
                    if (count == 6) {
                        distance = receiveString;
                    }
                    if (count == 7) {
                        inList = Boolean.parseBoolean(receiveString);
                    }
                    if (count == 8) {
                        inMap = Boolean.parseBoolean(receiveString);
                    }
                    if (count == 12) {
                        name = receiveString;
                    }

                    count++;
                }

                inputStream.close();
            }
        } catch (Exception e) {
            makeToast("Can not read file. Please reload the page.\nERROR: " + e.toString());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_fragment_list, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.signed_in, menu);
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
        final Dialog alert = new Dialog(mContext);
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert.setContentView(R.layout.about_this_app3);
        alert.setCancelable(true);
        alert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        alert.show();
    }

    private void signOut() {
        File dir = mContext.getFilesDir();
        File file = new File(dir, "info.txt");
        boolean deleted = file.delete();
        File file2 = new File(dir, "signedIn.txt");
        boolean deleted2 = file2.delete();
        //makeToast("Deleted: \n" + deleted + "\n" + deleted2);
        startActivity(new Intent(mContext, MainActivity.class));
    }


    private void makeToast(String s) {
        Toast.makeText(mContext, s, Toast.LENGTH_LONG).show();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}

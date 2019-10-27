package com.example.anany.restaurantleftoverfood;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anany.restaurantleftoverfood.Storage.Leaderboard;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentHome.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentHome#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentHome extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static TabLayout tabs;

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FragmentHome() {
        // Required empty public constructor
    }

    TextView numUsers, leaderboardType;
    ImageView help;
    Switch sw;
    String type;
    String address;

    ImageView map;
    Button mapB, listB, settingsB, donationsB;
    ListView leaderboard;
    TextView noresults;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @param mContext
     * @return A new instance of fragment FragmentHome.
     */
    // TODO: Rename and change types and number of parameters
    static Context mContext;
    //TabLayout tabs;

    public static FragmentHome newInstance(String param1, String param2, Context c, TabLayout tab) {
        FragmentHome fragment = new FragmentHome();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        tabs = tab;
        mContext = c;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        numUsers = getView().findViewById(R.id.numUsers);
        //numUsers.setText("HI");
        leaderboardType = getView().findViewById(R.id.donationsT);
        help = getView().findViewById(R.id.help);
        //map = getView().findViewById(R.id.mapImage);
        donationsB = getView().findViewById(R.id.donationsButton);
        listB = getView().findViewById(R.id.listButton);
        mapB = getView().findViewById(R.id.mapButton);
        settingsB = getView().findViewById(R.id.settingsButton);
        sw = getView().findViewById(R.id.show);
        sw.setChecked(true);
        leaderboard = getView().findViewById(R.id.leaderboard);
        noresults = getView().findViewById(R.id.noResults2);
        countUsers();
        detectClicks();
        handleSwitch();

        handleDonationsLeaderboard(true);
        //DONE If the list of stuff is empty, show/hide the no results textview.

        helper();
        super.onViewCreated(view, savedInstanceState);
    }

    private void handleDonationsLeaderboard(final boolean donations) {
        //TODO In Donations Leaderboard, store the To and From considering whether the shelter wants to be discoverable and to who and
        //TODO whether the restaurant wants to be discoverable and to who.
        //TODO If they don't want to be discoverable to current app user, then for name say Anonymous, otherwise show only name.
        //TODO If the address matches that of second line address in info.txt, say "You"
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Places");
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> name = new ArrayList<>();
                ArrayList<String> specialInfo = new ArrayList<>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String type2 = child.child("Type").getValue().toString();
                    String addresser = child.child("Address").getValue().toString();
                    if (type2.contains("est")) {
                        int numPoints;
                        if (addresser.equals(address)) {
                            if (!donations) {
                                numPoints = Integer.parseInt(child.child("Donation Points").getValue().toString());
                            } else {
                                numPoints = Integer.parseInt(child.child("Num of Donations").getValue().toString());
                            }
                            name.add("You");
                            specialInfo.add(numPoints + "");
                        } else {
                            boolean discoverable = Boolean.parseBoolean(child.child("Discoverable to " + type2).getValue().toString());
                            if (!donations) {
                                numPoints = Integer.parseInt(child.child("Donation Points").getValue().toString());
                            } else {
                                numPoints = Integer.parseInt(child.child("Num of Donations").getValue().toString());
                            }
                            if (numPoints > 0) {
                                if (!discoverable)
                                    name.add("Which Wich");
                                else
                                    name.add("Chick-fil-A");
                                specialInfo.add(numPoints + "");
                            }
                        }
                    }
                }

                int count = 0;

                ArrayList<Leaderboard> list = new ArrayList<>();

                final int initialSize = name.size();
                if (initialSize > 0) {
                    noresults.setVisibility(View.INVISIBLE);
                    leaderboard.setVisibility(View.VISIBLE);
                    while (count < 5 && count < initialSize) {
                        int maxPos = -1;
                        int maxVal = 0;
                        int count2 = 0;
                        for (String t : specialInfo) {
                            int num = Integer.parseInt(t);
                            if (num > maxVal) {
                                maxVal = num;
                                maxPos = count2;
                            }
                            count2++;
                        }
                        if (maxPos != -1) {
                            list.add(new Leaderboard(name.get(maxPos), specialInfo.get(maxPos)));
                            name.remove(maxPos);
                            specialInfo.remove(maxPos);
                        }
                            count++;
                        }
                        com.example.anany.restaurantleftoverfood.Adapters.Leaderboard adapter = new
                                com.example.anany.restaurantleftoverfood.Adapters.Leaderboard(getContext(), list);

                        int height = 108 * list.size();
                        leaderboard.getLayoutParams().height = height;

                        leaderboard.setAdapter(adapter);
                    } else{
                        noresults.setVisibility(View.VISIBLE);
                        leaderboard.setVisibility(View.INVISIBLE);
                    }


                }

                @Override
                public void onCancelled (@NonNull DatabaseError databaseError){

                }
            });
        }

        private void detectClicks () {
        /*map.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                TabLayout layout = (TabLayout) ((Home) getActivity()).findViewById(R.id.tabs);
                layout.getTabAt(2).select();
                return false;
            }
        });*/
            View.OnClickListener clicker = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int id = v.getId();
                    switch (id) {
                        case R.id.listButton:
                            TabLayout layout = (TabLayout) ((Home) getActivity()).findViewById(R.id.tabs);
                            layout.getTabAt(1).select();
                            break;
                        case R.id.mapButton:
                            TabLayout layout2 = (TabLayout) ((Home) getActivity()).findViewById(R.id.tabs);
                            layout2.getTabAt(2).select();
                            break;
                        case R.id.donationsButton:
                            TabLayout layout3 = (TabLayout) ((Home) getActivity()).findViewById(R.id.tabs);
                            layout3.getTabAt(3).select();
                            break;
                        case R.id.settingsButton:
                            TabLayout layout4 = (TabLayout) ((Home) getActivity()).findViewById(R.id.tabs);
                            layout4.getTabAt(4).select();
                            break;
                    }
                }
            };

            listB.setOnClickListener(clicker);
            mapB.setOnClickListener(clicker);
            donationsB.setOnClickListener(clicker);
            settingsB.setOnClickListener(clicker);

        }

        private void handleSwitch () {
            sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    //TODO Write code to change the results based on what the check is.
                    //TODO Read From Firebase table Donations and have a HashMap of occurences. Then get top 5.
                    //TODO If the address matches that of second line address in info.txt, say "You"
                    //INFO Don't care about discoverability.
                    if (!isChecked) {
                        leaderboardType.setText("Points");
                        handleDonationsLeaderboard(false);
                    } else {
                        leaderboardType.setText("Donations");
                        handleDonationsLeaderboard(true);
                    }
                }
            });
        }

        private void helper () {
            help.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    RelativeLayout sv = getView().findViewById(R.id.rel);
                    final Snackbar snackbar = Snackbar
                            .make(sv, "Donation points are earned based on the neediness of the shelter. This shows the most frequent donators in the area.", Snackbar.LENGTH_INDEFINITE)
                            .setDuration(6500)
                            .setActionTextColor(Color.parseColor("#8ce73c"));
                    snackbar.setAction("DISMISS", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Call your action method here
                            snackbar.dismiss();
                        }
                    });
                    TextView snackTextView = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
                    snackTextView.setMaxLines(7);

                    snackbar.show();
                    return true;
                }
            });
        }

        String placeName;

        private void countUsers () {
            String ret = "";
            double searchRadius = 1;
            double lat = 0, lon = 0;
            String units = "", distance = "";
            try {
                InputStream inputStream = mContext.openFileInput("info.txt");

                if (inputStream != null) {
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String receiveString = "";

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
                        } else if (count == 12) {
                            placeName = receiveString;
                        } else if (count > 12) {
                            break;
                        }
                        count++;
                    }
                    inputStream.close();

                    if (type.contains("est")) {
                        donationsB.setText("View Donation Requests");
                    } else {
                        donationsB.setText("Make Donation Requests");
                    }

                    //INFO Read the Firebase entries.
                    DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Places");
                    final double finalSearchRadius = searchRadius;
                    final double finalLat = lat;
                    final double finalLon = lon;
                    final String finalDistance = distance;
                    final String finalUnits = units;
                    dbref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            boolean good = false;
                            String result = "";
                            int count = 0;
                            for (DataSnapshot children : dataSnapshot.getChildren()) {
                                double la = Double.parseDouble(children.child("Latitude").getValue().toString());
                                double lo = Double.parseDouble(children.child("Longitude").getValue().toString());
                                if (goodDistance(finalSearchRadius, finalLat, finalLon, la, lo)) {
                                    count++;
                                }
                            }

                            numUsers.setText("There are " + (count) + " users within " + finalDistance + " " + finalUnits + ".");
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            makeToast("There was an error. Please try again");
                            //progressDialog.dismiss();
                        }
                    });
                }
            } catch (Exception e) {
                makeToast("Info not found. Please log out and log back in.\nERROR: " + e.toString());
            }
        }

        private boolean goodDistance ( double finalSearchRadius, double finalLat, double finalLon,
        double la, double lo){
            return (finalSearchRadius >= Math.sqrt(Math.pow(finalLat - la, 2) + Math.pow(finalLon - lo, 2))) ? true : false;
        }

        @Override
        public void onAttach (Context context){
            super.onAttach(context);
            if (context instanceof OnFragmentInteractionListener) {
                mListener = (OnFragmentInteractionListener) context;
            } else {
                throw new RuntimeException(context.toString()
                        + " must implement OnFragmentInteractionListener");
            }
        }

        @Override
        public void onDetach () {
            super.onDetach();
            mListener = null;
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

    /*public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //MenuInflater inflater = getMenuInflater();
        menu.add(R.menu.signed_in);
        //inflater.inflate(R.menu.signed_in, menu);

        // return true so that the menu pop up is opened
        super.onCreateOptionsMenu(menu, inflater);

    }*/
        @Override
        public void onCreateOptionsMenu (Menu menu, MenuInflater inflater){
            inflater.inflate(R.menu.signed_in, menu);
        }

        @Override
        public boolean onOptionsItemSelected (MenuItem item){
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

        public void showAppInfo () {
            final Dialog alert = new Dialog(mContext);
            alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
            alert.setContentView(R.layout.about_this_app2);
            alert.setCancelable(true);
            alert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            alert.show();
        }

        private void signOut () {
            File dir = mContext.getFilesDir();
            File file = new File(dir, "info.txt");
            boolean deleted = file.delete();
            File file2 = new File(dir, "signedIn.txt");
            boolean deleted2 = file2.delete();
            //makeToast("Deleted: \n" + deleted + "\n" + deleted2);
            startActivity(new Intent(mContext, MainActivity.class));
        }


        private void makeToast (String s){
            Toast.makeText(mContext, s, Toast.LENGTH_LONG).show();
        }
    }

package com.example.anany.restaurantleftoverfood;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
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
import java.io.OutputStreamWriter;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentAppSettings.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentAppSettings#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentAppSettings extends Fragment {
    // DONE: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // DONE: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    static Context mContext;

    public FragmentAppSettings() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @param tabs
     * @return A new instance of fragment FragmentAppSettings.
     */
    // DONE: Rename and change types and number of parameters
    public static FragmentAppSettings newInstance(String param1, String param2, Context m, TabLayout tabs) {
        FragmentAppSettings fragment = new FragmentAppSettings();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        mContext = m;
        return fragment;
    }

    String type, searchRadius, units;
    boolean dRest, sRest, list, map;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        //makeToast("In App Settings.");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment_app_settings, container, false);
    }

    Button back;
    Button save;
    static FrameLayout settingsFrame;
    CheckBox inList, inMap;
    Switch s1, s2;
    TextView t1, t2;
    AutoCompleteTextView at;
    Spinner units2;
    String address, latLong, lat, lon, val, name;

    TextView showOthers;
    ImageView type1H, type2H, listH, mapH, searchH;

    int deletedClicks = 0;
    String verificationCode;
    String username, password;

    @Override
    public void onViewCreated( View view,  Bundle savedInstanceState) {
        back = getView().findViewById(R.id.back);
        save = getView().findViewById(R.id.save);
        inList = getView().findViewById(R.id.check1);
        inMap = getView().findViewById(R.id.check2);
        s1 = getView().findViewById(R.id.switch1);
        s2 = getView().findViewById(R.id.switch2);
        t1 = getView().findViewById(R.id.text1);
        t2 = getView().findViewById(R.id.text2);
        at = getView().findViewById(R.id.distance);
        showOthers = getView().findViewById(R.id.showOther);
        units2 = getView().findViewById(R.id.units);
        type1H = getView().findViewById(R.id.typeHelp);
        type2H = getView().findViewById(R.id.typeHelp2);
        listH = getView().findViewById(R.id.listHelp);
        mapH = getView().findViewById(R.id.mapHelp);
        searchH = getView().findViewById(R.id.searchHelp);
        deletedClicks = 0;

        readData();

        helpButtons();
        if (type.contains("est")) {
            t1.setText("Shelters");
            t2.setText("Other Restaurants");
            s1.setChecked(sRest);
            showOthers.setText("Show other restaurants in:");
            s2.setChecked(dRest);
        } else {
            t1.setText("Restaurant");
            showOthers.setText("Show other shelters in:");
            t2.setText("Other Shelters");
            s2.setChecked(sRest);
            s1.setChecked(dRest);
        }
        inList.setChecked(list);
        inMap.setChecked(map);

        at.setText(searchRadius);
        units2.setSelection((units.equals("mi")) ? 0 : 1);

        at.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 3) {
                    int max = 300;
                    if (units2.getSelectedItem().toString().equals("km")) {
                        max = 475;
                    }
                    makeToast("Maximum distance allowed is " + max + " " + units2.getSelectedItem().toString() + ".");
                    at.setText(s.toString().substring(0, s.length() - 1));
                    at.setSelection(at.getText().toString().length());
                } else if (s.length() > 0) {
                    int t = Integer.parseInt(s.toString());
                    int max = 300;
                    if (units2.getSelectedItem().toString().equals("km")) {
                        max = 475;
                    }
                    if (t > max) {
                        makeToast("Maximum distance allowed is " + max + " " + units2.getSelectedItem().toString() + ".");
                        at.setText(s.toString().substring(0, s.length() - 1));
                        at.setSelection(at.getText().toString().length());
                    }

                }
            }
        });

        settingsFrame = getView().findViewById(R.id.settingsFrame);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentSettings.fl.setVisibility(View.VISIBLE);
                settingsFrame.setVisibility(View.INVISIBLE);
                FragmentManager fm = getActivity().getSupportFragmentManager();
                for (int i = 0; i < ((FragmentManager) fm).getBackStackEntryCount(); ++i) {
                    fm.popBackStack();
                }
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkInfo();
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }


    private void checkInfo() {
        boolean good = true;
        if (units2.getSelectedItem().toString().equals("mi")) {
            if (Integer.parseInt(at.getText().toString()) < 5) {
                makeToast("The minimum distance is 5 miles.");
                good = false;
            }
        } else {
            if (Integer.parseInt(at.getText().toString()) < 8) {
                makeToast("The minimum distance is 8 kilometers.");
                good = false;
            }
        }
        if (good) {
            boolean discover1 = s1.isChecked();
            boolean discover2 = s2.isChecked();
            boolean List = inList.isChecked();
            boolean Map = inMap.isChecked();
            int dis = 30;
            if ((!at.getText().toString().isEmpty()) && at.getText() != null && at.getText().length() > 0)
                dis = Integer.parseInt(at.getText().toString());
            else
                at.setText("30");

            String unit = units2.getSelectedItem().toString();
            double latLong = 0;
            if (unit.equals("mi"))
                latLong = ((double) dis) / 69;
            else
                latLong = ((double) dis) / 111;
            latLong = (double) Math.round(latLong * 100000000) / 100000000d;
            //makeToast("Distance: " + latLong);
            boolean discoverR, discoverS;
            if (type.contains("est")) {
                discoverR = s2.isChecked();
                discoverS = s1.isChecked();
            } else {
                discoverR = s1.isChecked();
                discoverS = s2.isChecked();
            }

            //OLD Recall the static methods of Map and List to refresh their data.
            //TODO Recall the static methods of Donations Leaderboard and Latest Donors/Recipients to refresh the data.

            //DONE Make a Loading Progress box when writing to Firebase.

            //INFO For below, it seems to automatically detect that info.txt changed and it auto updates.
            //DONE Save in Firebase

            String info = type + "\n" + address + "\n" + latLong + "\n" + lat + "\n" + lon + "\n" + units2.getSelectedItem().toString() + "\n" + at.getText().toString() + "\n" + List + "\n" + Map + "\n" + discoverR + "\n" + discoverS + "\n" + val + "\n" + name;
            saveInfoTxt(info);
            saveFirebase(latLong, at.getText().toString(), units2.getSelectedItem().toString(), List, Map, discoverR, discoverS);


        }
    }

    Dialog alert;
    boolean doneSaving;

    private void saveFirebase(final double searchRadius, final String distance, final String units, final boolean inList, final boolean inMap, final boolean R, final boolean S) {

        Settings.makeLoading(mContext);
        doneSaving = false;

        if (!doneSaving) {
            final Snackbar snackbar = Snackbar
                    .make(settingsFrame, "A strong WiFi connection is recommended, otherwise this may take a while.", Snackbar.LENGTH_INDEFINITE)
                    .setDuration(15000)
                    .setActionTextColor(Color.parseColor("#8ce73c"));
            snackbar.setAction("DISMISS", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Call your action method here
                    snackbar.dismiss();
                }
            });
            TextView snackTextView = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
            snackTextView.setMaxLines(5);

            snackbar.show();
        }

        final DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Places");
        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot) {
                for (DataSnapshot children : dataSnapshot.getChildren()) {
                    String tAddress = children.child("Address").getValue().toString();
                    if (tAddress.equals(address)) {
                        DatabaseReference newRef = FirebaseDatabase.getInstance().getReference("Places");
                        newRef.child(children.getKey()).child("Search Radius").setValue(searchRadius);
                        newRef.child(children.getKey()).child("Distance").setValue(distance);
                        newRef.child(children.getKey()).child("Units").setValue(units);
                        newRef.child(children.getKey()).child("Others in List").setValue(inList + "");
                        newRef.child(children.getKey()).child("Others in Map").setValue(inMap + "");
                        newRef.child(children.getKey()).child("Discoverable to Restaurant").setValue(R + "");
                        newRef.child(children.getKey()).child("Discoverable to Shelter").setValue(S + "").addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                FragmentSettings.fl.setVisibility(View.VISIBLE);
                                settingsFrame.setVisibility(View.INVISIBLE);
                                FragmentManager fm = getActivity().getSupportFragmentManager();
                                for (int i = 0; i < ((FragmentManager) fm).getBackStackEntryCount(); ++i) {
                                    fm.popBackStack();
                                }

                                final Snackbar snackbar = Snackbar
                                        .make(settingsFrame, "Saved your data!", Snackbar.LENGTH_INDEFINITE)
                                        .setDuration(2200)
                                        .setActionTextColor(Color.parseColor("#8ce73c"));
                                snackbar.setAction("DISMISS", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        // Call your action method here
                                        snackbar.dismiss();
                                    }
                                });
                                TextView snackTextView = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
                                snackTextView.setMaxLines(5);

                                snackbar.show();
                                Settings.dismiss();
                                if(type.contains("elt")){
                                    DonationsShelter.readInfo();
                                    DonationsShelter.requestDonation();
                                }else{
                                    DonationsRestaurant.readInfo2();
                                    DonationsRestaurant.offerDonation();
                                }

                            }
                        });


                        doneSaving = true;


                        break;
                    }
                }

                if (doneSaving) {

                }

            }

            @Override
            public void onCancelled( DatabaseError databaseError) {
                Settings.dismiss();
            }
        });
    }

    private void saveInfoTxt(String info) {
        OutputStreamWriter outputStreamWriter;
        try {
            outputStreamWriter = new OutputStreamWriter(mContext.openFileOutput("info.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(info);
            outputStreamWriter.close();
        } catch (Exception e) {
            makeToast(e.toString());
        }

    }

    @Override
    public void onDestroy() {
        settingsFrame.setVisibility(View.INVISIBLE);
        FragmentSettings.fl.setVisibility(View.VISIBLE);
        super.onDestroy();
    }

    private void helpButtons() {
        type1H.setOnTouchListener(info("(RECOMMENDED). This is so that " + (type.equals("Restaurant") ? "shelters using this app can see that you are willing to donate food." : "restaurants using this app can see that you are accepting food donations."), 6000));
        type2H.setOnTouchListener(info("(RECOMMENDED). This is so that " + (type.equals("Shelter") ? "other shelters using this app can see that you are also using it." : "restaurants using this app can see that you are also using it."), 5000));
        listH.setOnTouchListener(info("This is so that on the homepage, " + (type.equals("Shelter") ? "you can see a list of other shelters also using this app." : "you can see a list of other restaurants also using this app."), 4100));
        mapH.setOnTouchListener(info("This is so that on the map, " + (type.equals("Shelter") ? "you can see the locations of other shelters using this app in the area." : "you can see the locations of other restaurants using this app in the area."), 3500));
        searchH.setOnTouchListener(info("This is the farthest distance a " + (type.equals("Shelter") ? "restaurant " : "shelter ") + "can be from you for you to see them in your list.", 5000));
    }

    private View.OnTouchListener info(final String s, final int durations) {
        View.OnTouchListener onTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final Snackbar snackbar = Snackbar
                        .make(settingsFrame, s, Snackbar.LENGTH_INDEFINITE)
                        .setDuration(durations)
                        .setActionTextColor(Color.parseColor("#8ce73c"));
                snackbar.setAction("DISMISS", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Call your action method here
                        snackbar.dismiss();
                    }
                });
                TextView snackTextView = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
                snackTextView.setMaxLines(5);

                snackbar.show();
                return false;
            }
        };
        return onTouchListener;
    }

    private void readData() {
        try {
            InputStream inputStream = mContext.openFileInput("info.txt");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                int counter = 0;
                while ((receiveString = bufferedReader.readLine()) != null) {
                    if (counter == 0)
                        type = receiveString;
                    if (counter == 1)
                        address = receiveString;
                    if (counter == 2)
                        latLong = receiveString;
                    if (counter == 3)
                        lat = receiveString;
                    if (counter == 4)
                        lon = receiveString;
                    if (counter == 11)
                        val = receiveString;
                    if (counter == 12)
                        name = receiveString;
                    if (counter == 5)
                        units = receiveString;
                    if (counter == 6)
                        searchRadius = receiveString;
                    if (counter == 7)
                        list = Boolean.parseBoolean(receiveString);
                    if (counter == 8)
                        map = Boolean.parseBoolean(receiveString);
                    if (counter == 9)
                        dRest = Boolean.parseBoolean(receiveString);
                    if (counter == 10)
                        sRest = Boolean.parseBoolean(receiveString);
                    if (counter == 11) {
                        verificationCode = receiveString;
                        break;
                    }

                    counter++;
                }

                inputStream.close();

            }
        } catch (FileNotFoundException e) {
            //makeToast("File not found: " + e.toString());
            //makeToast("Doesn't exist");
        } catch (IOException e) {
            //makeToast("Can not read file: " + e.toString());
        }
    }

    // DONE: Rename method, update argument and hook method into UI event
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
        // DONE: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void makeToast(String s) {
        Toast.makeText(mContext, s, Toast.LENGTH_LONG).show();
    }
}

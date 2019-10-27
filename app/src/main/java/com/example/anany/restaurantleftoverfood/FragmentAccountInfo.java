package com.example.anany.restaurantleftoverfood;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Layout;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
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
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentAccountInfo.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentAccountInfo#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentAccountInfo extends Fragment {
    // DONE: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // DONE: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    static Context mContext;
    String rating;

    public FragmentAccountInfo() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @param tabs
     * @return A new instance of fragment FragmentAccountInfo.
     */
    // DONE: Rename and change types and number of parameters
    int changes = 0;
    FrameLayout settingsFrame;
    ImageView managerH, addressH, userH, passH;
    String type = "", address = "", manager = "", username = "", password = "", VC = "";
    EditText usernameE, passwordE, managerE;
    Button back, save;

    boolean clicked;

    public static FragmentAccountInfo newInstance(String param1, String param2, Context m, TabLayout tabs) {
        FragmentAccountInfo fragment = new FragmentAccountInfo();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        mContext = m;

        return fragment;
    }

    @Override
    public void onDestroy() {
        settingsFrame.setVisibility(View.INVISIBLE);
        FragmentSettings.fl.setVisibility(View.VISIBLE);
        clicked = false;
        super.onDestroy();
    }


    private void helpButtons() {
        addressH.setOnTouchListener(info("This is a very important part and is used to determine and validate your " + (type.equals("Shelter") ? "shelter" : "restaurant") + "'s location. Search from the map or by entering the name and address in the box.", 6000));
        userH.setOnTouchListener(info("This is the username that you will use to sign in to the app across various devices. It has to be a unique username. (Your username and password will be sent to you in an automated call.)", 6500));
        passH.setOnTouchListener(info("This is the password that you will use to sign in to the app across various devices. It has to be at least 8 characters long and alpha numeric. (Your username and password will be sent to you in an automated call.)", 700));
        managerH.setOnTouchListener(info("This is so that other " + (type.equals("Shelter") ? "restaurants" : "shelters") + " can contact your " + (type.equals("Shelter") ? "shelter" : "restaurant") + " and know who they're talking to.", 4000));
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

    private void readInfo() {
        try {
            InputStream inputStream = mContext.openFileInput("accountInfo.txt");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";

                int counter = 0;
                while ((receiveString = bufferedReader.readLine()) != null) {
                    if (counter == 0)
                        type = receiveString;
                    if (counter == 1)
                        username = receiveString;
                    if (counter == 2)
                        password = receiveString;
                    if (counter == 3)
                        manager = receiveString;
                    if (counter == 4)
                        address = receiveString;
                    if (counter == 5)
                        VC = receiveString;
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


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        Places.initialize(mContext, "AIzaSyAAuJitQF8VFy5vxkFcTbsW2TmS8y7J4js");
        PlacesClient placesClient = Places.createClient(mContext);
    }

    String hours = "", name = "", phone = "", website = "", address2 = "";
    LatLng location;
    boolean validPlace = false, wrongType = false;
    TextView error;


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        settingsFrame = getView().findViewById(R.id.settingsFrame);
        managerH = getView().findViewById(R.id.managerHelp);
        addressH = getView().findViewById(R.id.addressHelp);
        userH = getView().findViewById(R.id.userHelp);
        passH = getView().findViewById(R.id.passHelp);
        usernameE = getView().findViewById(R.id.username);
        passwordE = getView().findViewById(R.id.password);
        managerE = getView().findViewById(R.id.managerName);
        back = getView().findViewById(R.id.back);
        save = getView().findViewById(R.id.save);
        error = getView().findViewById(R.id.error);

        changes = 0;
        readInfo();
        buttonClicks();
        usernameE.setText(username);
        passwordE.setText(password);
        managerE.setText(manager);
        setupAutocompleteSupportFragment();
        autocompleteSupportFragment.setText(address);

        helpButtons();
        super.onViewCreated(view, savedInstanceState);
    }

    public void checkInput() {
        String manager = managerE.getText().toString();
        String street = address;
        String user = usernameE.getText().toString();
        String pass = passwordE.getText().toString();
        if (address2.equals("") || address2.isEmpty()) {
            address2 = address;
        }
        error.setText("");

        if (!address2.equals(address) && changes == 0) {
            Snackbar.make(settingsFrame, "Are you sure you want to change the address?", Snackbar.LENGTH_LONG).show();
            changes++;
        }

        if (manager.equals("") || manager.length() < 1 || manager == null) {
            snackBarErrorText();
            error.setText("You haven't entered the manager's name.");
            clicked = false;
        } else if (street.equals("") || street.length() == 0 || street == null) {
            snackBarErrorText();
            error.setText("You haven't entered the address.");
            clicked = false;
        } else if (!alphaNumeric(pass)) {
            snackBarErrorText();
            error.setText("Your password does not meet the requirements. Click the help button.");
            clicked = false;
        } else if (user.length() == 0 || user.isEmpty() || user == null) {
            snackBarErrorText();
            error.setText("Please enter a username.");
            clicked = false;
        } else {
            if (address2.equals(address)) {
                usernameUnique(type, manager, user, pass, address2);
            } else {
                if (validPlace) {
                    if (phone.equalsIgnoreCase("Not available")) {
                        clicked = false;
                        makeToast("CLICKED: " + clicked);
                        makeAlertInfo(true, "Invalid Address", "The place that you selected does not have a verifiable phone #. Please choose another place or if you think this is a mistake, contact ijapss101@gmail.com.");
                    } else if (name.equalsIgnoreCase("Not available")) {
                        makeAlertInfo(true, "Invalid Address", "The location you choose does not have a name. If you think this is a mistake, contact ijapss101@gmail.com.");
                        clicked = false;
                    } else if (address.contains(name)) {
                        makeAlertInfo(true, "Invalid Address", "Please choose another place or if you think this is a mistake, contact ijapss101@gmail.com.");
                        clicked = false;
                    } else {
                        //askIfCertain(rORs, manager, user, pass);
                        usernameUnique(type, manager, user, pass, address2);
                    }
                } else {
                    if (wrongType) {
                        makeAlertInfo(true, "Invalid Address", "The address that you have chosen is not valid and was most likely that for a road or premise. Please search by location name first followed by address. If you think this an error, contact ijapps101@gmail.com.");
                        clicked = false;
                    } else {
                        makeAlertInfo(true, "Invalid Address", "The address that you have chosen is not valid. Possible reasons include:\n      Not a " + type + "\n      No verifiable phone #.\nIf you think this an error, contact ijapps101@gmail.com.");
                        clicked = false;
                    }
                }
            }
        }
    }

    public void usernameUnique(final String rORs, final String manager, final String user, final String pass, final String a) {
        final Snackbar snackbar = Snackbar
                .make(settingsFrame, "A strong WiFi connection is recommended, otherwise this may take a while.", Snackbar.LENGTH_INDEFINITE)
                .setDuration(20000)
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

        Settings.makeVerifying(mContext);

        DatabaseReference accounts = FirebaseDatabase.getInstance().getReference("userAddress");
        accounts.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean uG = true;
                boolean aG = true;
                String uE = "";
                String aE = "";
                String vals = "";
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    String users = data.child("Username").getValue().toString();
                    String addressm = data.child("Address").getValue().toString();
                    vals += address + "\n";
                    if (users.equals(user) && !user.equals(username)) {
                        uG = false;
                        uE = user;
                    }
                    if (addressm.equals(a) && !addressm.equals(address)) {
                        aG = false;
                        aE = addressm;
                    }
                }
                //makeToast("Address " + address + "\n" + "Address2: " + address2);
                //error.setText("CHOSEN: " + address2 + "\n" + vals);
                snackbar.dismiss();
                if (!uG) {
                    snackBarErrorText();
                    error.setText("This username (" + uE + ") is already taken. Please choose another.");
                    clicked = false;
                    Settings.dismissVerifying();
                } else if (!aG) {
                    snackBarErrorText();
                    error.setText("The place with this address (" + aE + ") already has an account. If you think this is an error, contact ijapps101@gmail.com.");
                    clicked = false;
                    Settings.dismissVerifying();
                } else {
                    askIfCertain(type, manager, user, pass);
                    Settings.dismissVerifying();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    Dialog alert;
    boolean doneSaving;

    private void askIfCertain(final String rORs, final String manager, final String user, final String pass) {

        final Dialog alert = new Dialog(mContext);
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert.setContentView(R.layout.are_you_sure2);
        alert.setCancelable(false);
        alert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        Button close = alert.findViewById(R.id.continueN);
        Button stay = alert.findViewById(R.id.stay);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
                alert.cancel();
                error.setText("");
                clicked = false;
                //DONE Save to info.txt
                //DONE Save to accountInfo.txt
                //DONE Save to Firebase: Places
                //DONE Save to Firebase: userAddress
                //DONE Change layout of Dialog
                //INFO When saving in Firebase check if the verification code matches.
                //INFO Save manager, user, pass, and address2.

                //makeToast("DATA: " + user + "\n" + pass + "\n" + manager + "\n" + address2);
                OutputStreamWriter outputStreamWriter = null;
                try {
                    outputStreamWriter = new OutputStreamWriter(mContext.openFileOutput("accountInfo.txt", Context.MODE_PRIVATE));
                    outputStreamWriter.write(type + "\n" + user + "\n" + pass + "\n" + manager + "\n" + address2 + "\n" + VC);
                    outputStreamWriter.close();
                } catch (Exception e) {
                    makeToast("ERROR: " + e.toString());
                }

                try {
                    InputStream inputStream = mContext.openFileInput("info.txt");

                    if (inputStream != null) {
                        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                        String receiveString = "";
                        StringBuilder stringBuilder = new StringBuilder();
                        String latLong = "", lat = "", lon = "", val = "", name2 = "", units = "", searchRadius = "", list = "", map = "", dRest = "", sRest = "";
                        int counter = 0;
                        while ((receiveString = bufferedReader.readLine()) != null) {
                            if (counter == 2)
                                latLong = receiveString;
                            if (counter == 3)
                                lat = receiveString;
                            if (counter == 4)
                                lon = receiveString;
                            if (counter == 11)
                                val = receiveString;
                            if (counter == 12)
                                name2 = receiveString;
                            if (counter == 5)
                                units = receiveString;
                            if (counter == 6)
                                searchRadius = receiveString;
                            if (counter == 7)
                                list = Boolean.parseBoolean(receiveString) + "";
                            if (counter == 8)
                                map = Boolean.parseBoolean(receiveString) + "";
                            if (counter == 9)
                                dRest = Boolean.parseBoolean(receiveString) + "";
                            if (counter == 10)
                                sRest = Boolean.parseBoolean(receiveString) + "";

                            counter++;
                        }
                        inputStream.close();
                        String info;
                        doneSaving = false;
                        Settings.makeLoading(mContext);
                        if (location == null) {
                            info = type + "\n" + address2 + "\n" + latLong + "\n" + lat + "\n" + lon + "\n" + units + "\n" + searchRadius + "\n" + list + "\n" + map + "\n" + dRest + "\n" + sRest + "\n" + val + "\n" + name;
                            savePlaces(user, pass, manager, address2, lat, lon);
                        } else {
                            info = type + "\n" + address2 + "\n" + latLong + "\n" + location.latitude + "\n" + location.longitude + "\n" + units + "\n" + searchRadius + "\n" + list + "\n" + map + "\n" + dRest + "\n" + sRest + "\n" + val + "\n" + name;
                            savePlaces(user, pass, manager, address2, location.latitude + "", location.longitude + "");
                        }

                        try {
                            outputStreamWriter = new OutputStreamWriter(mContext.openFileOutput("info.txt", Context.MODE_PRIVATE));
                            outputStreamWriter.write("" + info);
                            outputStreamWriter.close();
                        } catch (Exception e) {
                            makeToast("ERROR: " + e.toString());
                        }

                    }
                } catch (Exception e) {
                    makeToast("ERROR: " + e.toString());
                }

            }
        });

        stay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
                clicked = false;
                alert.cancel();
            }
        });

        alert.show();

    }

    private void savePlaces(final String user, final String pass, final String manager, final String address2, final String lat, final String lon) {
        if (!doneSaving) {
            final Snackbar snackbar = Snackbar
                    .make(settingsFrame, "A strong WiFi connection is recommended, otherwise this may take a while.", Snackbar.LENGTH_INDEFINITE)
                    .setDuration(20000)
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
            public void onDataChange(DataSnapshot dataSnapshot) {
                //makeToast("Checking Places");
                for (DataSnapshot children : dataSnapshot.getChildren()) {
                    String verification = children.child("Verification Code").getValue().toString();
                    if (verification.equals(VC)) {
                        DatabaseReference newRef = FirebaseDatabase.getInstance().getReference("Places");
                        newRef.child(children.getKey()).child("Address").setValue(address2);
                        newRef.child(children.getKey()).child("Manager Name").setValue(manager);
                        newRef.child(children.getKey()).child("Password").setValue(pass);
                        newRef.child(children.getKey()).child("Hours").setValue(hours);
                        newRef.child(children.getKey()).child("Phone").setValue(phone);
                        newRef.child(children.getKey()).child("Name").setValue(name);
                        newRef.child(children.getKey()).child("Website").setValue(website);
                        newRef.child(children.getKey()).child("Latitude").setValue(lat);
                        newRef.child(children.getKey()).child("Rating").setValue(rating);
                        newRef.child(children.getKey()).child("Longitude").setValue(lon);
                        newRef.child(children.getKey()).child("Username").setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                saveUserAddress(user, address2);
                            }
                        });
                        break;
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Settings.dismiss();
                makeToast("ERROR: " + databaseError.toString());
            }
        });

    }


    private void saveUserAddress(final String u, final String a) {
        final DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("userAddress");
        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot children : dataSnapshot.getChildren()) {
                    String verification = children.child("Verification Code").getValue().toString();
                    if (verification.equals(VC)) {
                        DatabaseReference newRef = FirebaseDatabase.getInstance().getReference("userAddress");
                        newRef.child(children.getKey()).child("Address").setValue(a);
                        newRef.child(children.getKey()).child("Username").setValue(u).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                FragmentSettings.fl.setVisibility(View.VISIBLE);
                                settingsFrame.setVisibility(View.INVISIBLE);
                                FragmentManager fm = getActivity().getSupportFragmentManager();
                                for (int i = 0; i < ((FragmentManager) fm).getBackStackEntryCount(); ++i) {
                                    fm.popBackStack();
                                }
                                clicked = false;

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
                                if (type.contains("elt")) {
                                    DonationsShelter.readInfo();
                                    DonationsShelter.requestDonation();
                                    DonationsShelter.readDonationReceptions();
                                } else {
                                    DonationsRestaurant.readInfo2();
                                    DonationsRestaurant.offerDonation();
                                    DonationsRestaurant.readDonationReceptions();
                                }
                            }
                        });
                        doneSaving = true;
                        break;
                    }
                }
                Settings.dismiss();

                if (doneSaving) {

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Settings.dismiss();
                makeToast("ERROR: " + databaseError.toString());
            }
        });
    }

    public boolean alphaNumeric(String p) {
        if (p.length() < 8) return false;
        boolean chars = false;
        boolean nums = false;
        for (char c : p.toCharArray()) {
            if (Character.isLetter(c))
                chars = true;
            if (Character.isDigit(c))
                nums = true;
            if (nums && chars)
                return true;
        }
        return false;
    }

    private void snackBarErrorText() {
        final Snackbar snackbar = Snackbar
                .make(settingsFrame, "Check the red error text.", Snackbar.LENGTH_INDEFINITE)
                .setDuration(2000)
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

    private void buttonClicks() {
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
                //makeToast("Clicked: " + clicked);
                if (!clicked) {
                    clicked = true;
                    checkInput();
                }
            }
        });
    }

    AutocompleteSupportFragment autocompleteSupportFragment;

    private void setupAutocompleteSupportFragment() {
        autocompleteSupportFragment =
                (AutocompleteSupportFragment)
                        getChildFragmentManager().findFragmentById(R.id.auto);

        autocompleteSupportFragment.setPlaceFields(getPlaceFields());
        autocompleteSupportFragment.setOnPlaceSelectedListener(getPlaceSelectionListener());
        autocompleteSupportFragment.setHint("Start Searching by Name!");
        //autocompleteSupportFragment.setCountry(getCountry());
        //autocompleteSupportFragment.setLocationBias(getLocationBias());
        autocompleteSupportFragment.setTypeFilter(null);
        EditText etPlace = (EditText) autocompleteSupportFragment.getView().findViewById(R.id.places_autocomplete_search_input);
        etPlace.setHint("Start searching by name!");
        etPlace.setHintTextColor(Color.parseColor("#0dac15"));
        Typeface face = ResourcesCompat.getFont(mContext, R.font.regular);
        etPlace.setTextSize(22.0f);
        etPlace.setTypeface(face);
        //etPlace.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        etPlace.setPadding(-3, -8, 0, -8);
        //etPlace.setBackground(getResources().getDrawable(R.drawable.editbg));
    }


    private PlaceSelectionListener getPlaceSelectionListener() {
        return new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                makeToast(
                        "You chose: " + place.getName() + ".");
                location = place.getLatLng();
                validPlace = false;
                wrongType = false;
                hours = "";
                changes = 0;
                String output3 = "";
                if (place.getRating() != null)
                    rating = place.getRating() + "";
                else
                    rating = "NA";

                if (place.getTypes() != null) {
                    List<Place.Type> types = place.getTypes();
                    for (Place.Type t : types) {
                        output3 += t.toString() + "\n";
                    }
                    if (type.equals("Shelter")) {
                        //makeToast("TYPE: " + type);
                        for (Place.Type type : types) {
                            if (type.toString().equalsIgnoreCase("establishment") || type.toString().equalsIgnoreCase("health") || type.toString().equalsIgnoreCase("hospital")) {
                                validPlace = true;
                                break;
                            } else {
                                validPlace = false;
                            }

                            if (type.toString().equalsIgnoreCase("route") || type.toString().equalsIgnoreCase("intersection") || type.toString().equalsIgnoreCase("street_number") || type.toString().equalsIgnoreCase("street_address") || type.toString().equalsIgnoreCase("premise")) {
                                wrongType = true;
                            }
                        }
                    } else {
                        for (Place.Type type : types) {
                            if (type.toString().equalsIgnoreCase("supermarket") || type.toString().equalsIgnoreCase("restaurant") || type.toString().equalsIgnoreCase("meal_takeaway") || type.toString().equalsIgnoreCase("meal_delivery") || type.toString().equalsIgnoreCase("food") || type.toString().equalsIgnoreCase("cafe") || type.toString().equalsIgnoreCase("bakery")) {
                                validPlace = true;
                                break;
                            } else {
                                validPlace = false;
                            }

                            if (type.toString().equalsIgnoreCase("route") || type.toString().equalsIgnoreCase("intersection") || type.toString().equalsIgnoreCase("street_number") || type.toString().equalsIgnoreCase("street_address") || type.toString().equalsIgnoreCase("premise")) {
                                wrongType = true;
                            }
                        }
                        //   makeToast("TYPE: " + type + "\nValidplace: " + validPlace + "\nWrong place: " + wrongType);
                    }
                } else {
                    validPlace = false;
                }

                address2 = place.getAddress();
                //makeToast("ADDRESS2 : " + address2);
                if (place.getName() != null)
                    name = place.getName();
                else
                    name = "Not available";

                if (place.getPhoneNumber() != null)
                    phone = place.getPhoneNumber();
                else {
                    phone = "Not available";

                    final Snackbar snackbar = Snackbar
                            .make(settingsFrame, "WARNING! The place you selected does not have a valid phone # that can be used to verify your account. Please choose another location or if you think this is an error, contact ijapps101@gmail.com.", Snackbar.LENGTH_INDEFINITE)
                            .setDuration(15000)
                            .setActionTextColor(Color.parseColor("#8ce73c"));
                    snackbar.setAction("DISMISS", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            snackbar.dismiss();
                        }
                    });
                    TextView snackTextView = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
                    snackTextView.setBreakStrategy(Layout.BREAK_STRATEGY_SIMPLE);
                    snackTextView.setMaxLines(8);
                    snackbar.show();
                }
                String output2 = "";
                if (place.getOpeningHours() != null) {
                    //hours = place.getOpeningHours().toString();
                    List<String> openingHours = place.getOpeningHours().getWeekdayText();
                    for (String s : openingHours) {
                        output2 += s + "\n";
                        hours += s + "   ";
                    }
                } else
                    output2 = "Not available";

                if (place.getWebsiteUri() != null)
                    website = place.getWebsiteUri().toString();
                else
                    website = "Not available";

                String output = address2 + "\n" + name + "\n" + phone + "\n" + website + "\n" + output2;
                //makeAlertInfo(true, "Result", output + "\n" + output3);
            }

            @Override
            public void onError(Status status) {
                makeToast(status.getStatusMessage());
            }
        };
    }

    private void makeAlertInfo(boolean change, String t, String m) {
        final Dialog alert = new Dialog(mContext);
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert.setContentView(R.layout.alert_info);
        alert.setCancelable(true);

        alert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView title = alert.findViewById(R.id.title);
        TextView message = alert.findViewById(R.id.message);
        if (change) {
            title.setText(t);
            message.setText(m);
        }

        Button close = alert.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
                alert.cancel();
            }
        });

        alert.show();

    }

    private List<Place.Field> getPlaceFields() {
        List<Place.Field> arr = new ArrayList<>();
        arr.add(Place.Field.ADDRESS);
        arr.add(Place.Field.NAME);
        arr.add(Place.Field.LAT_LNG);
        arr.add(Place.Field.TYPES);
        arr.add(Place.Field.PHONE_NUMBER);
        arr.add(Place.Field.OPENING_HOURS);
        arr.add(Place.Field.WEBSITE_URI);
        arr.add(Place.Field.RATING);
        return arr;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment_account_info, container, false);
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

    private static void makeToast(String s) {
        Toast.makeText(mContext, s, Toast.LENGTH_LONG).show();
    }

}

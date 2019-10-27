package com.example.anany.restaurantleftoverfood;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
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

public class InfoPages extends AppCompatActivity implements LocationListener {

    static double lat;
    static double lon;
    LatLng location;

    static boolean current;
    static boolean checked;
    static String mName, sUser, sPass;

    public static String getmName() {
        return mName;
    }

    public static void setmName(String mName) {
        InfoPages.mName = mName;
    }

    public static String getsUser() {
        return sUser;
    }

    public static void setsUser(String sUser) {
        InfoPages.sUser = sUser;
    }

    public static String getsPass() {
        return sPass;
    }

    public static void setsPass(String sPass) {
        InfoPages.sPass = sPass;
    }

    public static boolean isChecked() {
        return checked;
    }

    public static void setChecked(boolean checked) {
        InfoPages.checked = checked;
    }

    public boolean getCurrent() {
        return current;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getLtype() {
        return ltype;
    }

    public void setLtype(String ltype) {
        this.ltype = ltype;
    }

    static String ltype;

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };
    String rating;
    ToggleButton type;
    boolean firstClick = false;
    EditText /*tname,*/ managerName, /*streetA,*/ /*cityA, stateA, */
            usernameE, passwordE;
    TextView title, error, error2;
    ImageView typeH, /*nameH,*/
            managerH, addressH, userH, passH;
    Button next;
    //ImageView map;
    FusedLocationProviderClient fusedLocationProviderClient;
    Location l;
    TextView chooseAddress;

    //PlaceDetectionClient placeDetectionClient;
    //Button pickMap;
    /*AutoCompleteTextView state;
    String[] statesList = {
            "AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "FL", "GA", "HI", "ID", "IL", "IN", "IA", "KS", "KY", "LA", "ME", "MD", "MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ", "NM", "NY", "NC", "ND", "OH", "OK", "OR", "PA", "RI", "SC", "SD", "TN", "TX", "UT", "VT", "VA", "WA", "WV", "WI", "WY"
    };*/

    AutocompleteSupportFragment autocompleteSupportFragment;


    static boolean addressGood = false;
    static String address = "", name, website, phone, hours = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_pages);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        //TODO in Verification Page, Before you can sign in, you need to verify your account. An automated voice message was sent to your
        //TODO restaurant/shelter phone #. Please use the verification code here.

        Places.initialize(getApplicationContext(), "AIzaSyAAuJitQF8VFy5vxkFcTbsW2TmS8y7J4js");

        location = null;
        //requestLocation("The location of your shelter/restaurant is needed in order to verify that it exists and to obtain details such as open hours and images.");
        findViews();
        helpButtons();
        rating = "NA";

        Intent t = getIntent();
        if (t.getExtras() != null) {
            //location = getLocation();
            //current = true;
            makeAlertInfo(false, "", "");
        } else {
           /* readLocation();
            readPageData();
            //makeToast("Lat: " + lat + " Lon: " + lon);
            location = new LatLng(lat, lon);
            if (lat == 39.7) {
                location = getLocation();
                current = true;
            } else
                current = false;*/
        }
        setupAutocompleteSupportFragment();
      /*  error2.setText("");
        error.setText("");*/

        type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firstClick) {
                    type.setTextColor(Color.parseColor("#0eb016"));
                    firstClick = false;
                    title.setText(getResources().getText(R.string.shelterinfo));
                    chooseAddress.setText("Enter Shelter Name");
                } else {
                    type.setTextColor(Color.parseColor("#ffffff"));
                    title.setText(getResources().getText(R.string.restaurantinfo));
                    chooseAddress.setText("Enter Restaurant Name");
                    firstClick = true;
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Write function to check if everything is filled in properly and ready to go.
                checkInput();

                //makeToast("Clicked");
            }
        });

    }

    public void readPageData() {

        try {
            InputStream inputStream = getApplicationContext().openFileInput("extra.txt");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                int count = 1;
                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString + "\n");
                    if (count == 1) {
                        boolean r = Boolean.parseBoolean(receiveString);
                        type.setChecked(Boolean.parseBoolean(receiveString));
                        if (!r) {
                            type.setTextColor(Color.parseColor("#0eb016"));
                            firstClick = false;
                            title.setText(getResources().getText(R.string.shelterinfo));
                        } else {
                            type.setTextColor(Color.parseColor("#ffffff"));
                            title.setText(getResources().getText(R.string.restaurantinfo));
                            firstClick = true;
                        }
                    }
                    if (count == 2)
                        managerName.setText(receiveString);
                    if (count == 3)
                        usernameE.setText(receiveString);
                    if (count == 4)
                        passwordE.setText(receiveString);
                    count++;
                }
                //error2.setText(stringBuilder.toString());

                inputStream.close();
            }
        } catch (Exception e) {
            makeToast("Can not read file: " + e.toString());
        }
    }

    public void readLocation() {
        try {
            InputStream inputStream = getApplicationContext().openFileInput("place.txt");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();
                int count = 0;
                boolean temp = Boolean.parseBoolean(bufferedReader.readLine());
                stringBuilder.append(temp + "\n");
                if (temp) {
                    addressGood = temp;
                    hours = "";
                    while ((receiveString = bufferedReader.readLine()) != null) {
                        stringBuilder.append(receiveString + "\n");
                        if (count == 0)
                            lat = Double.parseDouble(receiveString);
                        if (count == 1) lon = Double.parseDouble(receiveString);
                        if (count == 2) address = receiveString;
                        if (count == 3) name = receiveString;
                        if (count == 4) phone = receiveString;
                        if (count == 5) website = receiveString;
                        if (count >= 6) hours += receiveString + "  ";
                        count++;
                    }
                    //streetA.setText(address);
                } else if (addressGood) {
                    //Do nothing because previous one was good and this one is bad so don't change.
                    //streetA.setText(address);
                } else {
                    //If former and previous were bad.
                    addressGood = false;
                    hours = "";
                    while ((receiveString = bufferedReader.readLine()) != null) {
                        stringBuilder.append(receiveString + "\n");
                        if (count == 0)
                            lat = Double.parseDouble(receiveString);
                        if (count == 1) lon = Double.parseDouble(receiveString);
                        if (count == 2) address = receiveString;
                        if (count == 3) name = receiveString;
                        if (count == 4) phone = receiveString;
                        if (count == 5) website = receiveString;
                        if (count >= 6) hours += receiveString + "  ";
                        count++;
                    }
                    //streetA.setText(address);
                }
                /*if (streetA.getText().toString().equalsIgnoreCase("null"))
                    streetA.setText("");*/

                error.setText("TEMP: " + temp + "   ADDRESSGOOD: " + addressGood);
                error2.setText("Address: " + address + "\nName: " + name + "\nPhone: " + phone + "\nWebsite: " + website + "\nHours: " + hours);

                inputStream.close();
            }
        } catch (Exception e) {
            makeToast("Can not read file: " + e.toString());
        }
    }

    private void nearbyLocations() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //requestLocation("The current location is needed in order to show the nearby locations.");
        } else {
            Places.initialize(getApplicationContext(), "AIzaSyAAuJitQF8VFy5vxkFcTbsW2TmS8y7J4js");
            PlacesClient placesClient = Places.createClient(this);
            FindCurrentPlaceRequest currentPlaceRequest =
                    FindCurrentPlaceRequest.newInstance(getPlaceFields());
            Task<FindCurrentPlaceResponse> currentPlaceTask =
                    placesClient.findCurrentPlace(currentPlaceRequest);

            currentPlaceTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    makeToast(e.toString());
                    error.setText(e.toString());
                    //makeAlertInfo();
                }
            });
            currentPlaceTask.addOnSuccessListener(new OnSuccessListener<FindCurrentPlaceResponse>() {
                @Override
                public void onSuccess(FindCurrentPlaceResponse findCurrentPlaceResponse) {
                    makeToast(StringUtil.stringify(findCurrentPlaceResponse, false));
                    error.setText(StringUtil.stringify(findCurrentPlaceResponse, false));
                }
            });
        }
    }

    private void requestLocation(String s) {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage(s)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(InfoPages.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        10);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        10);
            }
        } else {
            //location = getLocation();
            current = true;
        }
    }

    public void launchMap() {
        //requestLocation("The location of your shelter/restaurant is needed in order to verify that it exists and to obtain details such as open hours and images.");
        setChecked(type.isChecked());
        setmName(managerName.getText().toString());
        setsPass(passwordE.getText().toString());
        setsUser(usernameE.getText().toString());
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            String permission = Manifest.permission.ACCESS_FINE_LOCATION;
            int res = getApplicationContext().checkCallingOrSelfPermission(permission);
            mName = managerName.getText().toString();
            sUser = usernameE.getText().toString();
            sPass = passwordE.getText().toString();
            checked = type.isChecked();

            if (res == PackageManager.PERMISSION_GRANTED) {
                //String val = location.latitude + "\n" + location.longitude;
                setLat(lat);
                setLon(lon);
                setLtype(type.getText().toString());
            } else {
                setLat(39.7);
                setLon(-99.7);
                setLtype(type.getText().toString());
            }
            startActivity(new Intent(InfoPages.this, pickPlace.class));
        } else {
            //location = getLocation();
            if (location == null) {
                setLat(39.7);
                setLon(-99.7);
                setLtype(type.getText().toString());
                //location = getLocation();
                current = true;
            } else {
                String val = location.latitude + "\n" + location.longitude;
                //makeToast(val);
                setLat(lat);
                setLon(lon);
                setLtype(type.getText().toString());
            }

           /* Handler handler = new Handler();
            Runnable r = new Runnable() {
                @Override
                public void run() {*/
            startActivity(new Intent(InfoPages.this, pickPlace.class));
     /*           }
            };

            handler.postDelayed(r, 2000);*/

        }

    }

    private void setupAutocompleteSupportFragment() {
        autocompleteSupportFragment =
                (AutocompleteSupportFragment)
                        getSupportFragmentManager().findFragmentById(R.id.auto);

        autocompleteSupportFragment.setPlaceFields(getPlaceFields());
        autocompleteSupportFragment.setOnPlaceSelectedListener(getPlaceSelectionListener());
        autocompleteSupportFragment.setHint("Start Searching by Name!");
        //autocompleteSupportFragment.setCountry(getCountry());
        //autocompleteSupportFragment.setLocationBias(getLocationBias());
        autocompleteSupportFragment.setTypeFilter(null);
        EditText etPlace = (EditText) autocompleteSupportFragment.getView().findViewById(R.id.places_autocomplete_search_input);
        etPlace.setHint("Start searching by name!");
        etPlace.setHintTextColor(Color.parseColor("#0dac15"));
        Typeface face = ResourcesCompat.getFont(getApplicationContext(), R.font.regular);
        etPlace.setTextSize(22.0f);
        etPlace.setTypeface(face);
        //etPlace.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        etPlace.setPadding(-3, -8, 0, -8);
        //etPlace.setBackground(getResources().getDrawable(R.drawable.editbg));
    }

    boolean validPlace = false, wrongType = false;
    Snackbar snackbar;


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
                if (snackbar != null)
                    snackbar.dismiss();
                String output3 = "";
                if (place.getRating() == null)
                    rating = "NA";
                else
                    rating = place.getRating() + "";

                if (place.getTypes() != null) {
                    List<Place.Type> types = place.getTypes();
                    for (Place.Type t : types) {
                        output3 += t.toString() + "\n";
                    }
                    if (type.getText().toString().equals("Shelter")) {
                        for (Place.Type type : types) {
                            if (type.toString().equalsIgnoreCase("establishment") || type.toString().equalsIgnoreCase("health") || type.toString().equalsIgnoreCase("hospital")) {
                                validPlace = true;
                                break;
                            } else if (type.toString().equalsIgnoreCase("route") || type.toString().equalsIgnoreCase("intersection") || type.toString().equalsIgnoreCase("street_number") || type.toString().equalsIgnoreCase("street_address") || type.toString().equalsIgnoreCase("premise")) {
                                wrongType = true;
                            } else {
                                validPlace = false;
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
                        }
                    }
                } else {
                    validPlace = false;
                }

                address = place.getAddress();
                if (place.getName() != null)
                    name = place.getName();
                else
                    name = "Not available";

                if (place.getPhoneNumber() != null)
                    phone = place.getPhoneNumber();
                else {
                    phone = "Not available";
                    ScrollView frameLayout = findViewById(R.id.rel22);
                    snackbar = Snackbar
                            .make(frameLayout, "WARNING! The place you selected does not have a valid phone # that can be used to verify your account. Please choose another location or if you think this is an error, contact ijapps101@gmail.com.", Snackbar.LENGTH_INDEFINITE)
                            .setDuration(10000)
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

                String output = address + "\n" + name + "\n" + phone + "\n" + website + "\n" + output2;
                //makeAlertInfo(true, "Result", output + "\n" + output3);
            }

            @Override
            public void onError(Status status) {
                makeToast(status.getStatusMessage());
            }
        };
    }

    public LatLng getLocation() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        Location currentLocation = null;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //requestLocation("Your location permission is requested to make your address search faster. Please consider enabling it.");
            return null;
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            String locationProvider = LocationManager.GPS_PROVIDER;
            currentLocation = locationManager.getLastKnownLocation(locationProvider);
            if (currentLocation != null) {
                lon = currentLocation.getLongitude();
                lat = currentLocation.getLatitude();
                //makeToast("Location " + lng + " " + lat);
                Log.d("Log", "longtitude=" + lon + ", latitude=" + lat);
            } else {
                makeToast("Null");
            }
        }

        return new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
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
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lon = location.getLongitude();
    }


    private void makeAlertInfo(boolean change, String t, String m) {
        final Dialog alert = new Dialog(InfoPages.this);
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

    private void helpButtons() {
        typeH.setOnTouchListener(info("This is very important and is used to determine whether you are a shelter or a restaurant. Tap on the box to change the response.", 5000));
        addressH.setOnTouchListener(info("This is a very important part and is used to determine and validate your " + (type.getText().toString().equals("Shelter") ? "shelter" : "restaurant") + "'s location. Search from the map or by entering the name and address in the box.", 6000));
        userH.setOnTouchListener(info("This is the username that you will use to sign in to the app across various devices. It has to be a unique username.", 6500));
        passH.setOnTouchListener(info("This is the password that you will use to sign in to the app across various devices. It has to be at least 8 characters long and alpha numeric.", 7000));
        managerH.setOnTouchListener(info("This is so that other " + (type.getText().toString().equals("Shelter") ? "restaurants" : "shelters") + " can contact your " + (type.getText().toString().equals("Shelter") ? "shelter" : "restaurant") + " and know who they're talking to.", 4000));
    }

    private View.OnTouchListener info(final String s, final int durations) {
        final ScrollView sv = findViewById(R.id.rel22);
        View.OnTouchListener onTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final Snackbar snackbar = Snackbar
                        .make(sv, s, Snackbar.LENGTH_INDEFINITE)
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

    private void snackBarErrorText() {
        ScrollView sv = findViewById(R.id.rel22);
        final Snackbar snackbar = Snackbar
                .make(sv, "Check the red error text.", Snackbar.LENGTH_INDEFINITE)
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

    public void checkInput() {
        String rORs = type.getText().toString();
        //String namer = tname.getText().toString();
        String manager = managerName.getText().toString();
        //String street = streetA.getText().toString();
        String street = address;
        String user = usernameE.getText().toString();
        String pass = passwordE.getText().toString();
        error.setText("");
        error2.setText("");
       /* boolean[] uniques = usernameUnique(user, address);
        TextView invisi = findViewById(R.id.invisi);
        uniques[0] = Boolean.parseBoolean(invisi.getText().toString().split(" ")[0]);
        uniques[1] = Boolean.parseBoolean(invisi.getText().toString().split(" ")[1]);
        makeToast("Username: " + uniques[0] + "\nPassword: " + uniques[1]);*/
        /*if (namer.equals("") || namer.length() == 0 || namer == null) {
            error.setText("You haven't entered the " + (rORs.equals("Restaurant") ? "restaurant name." : "shelter name."));
            snackBarErrorText();
        } else */
        if (manager.equals("") || manager.length() < 2 || manager == null) {
            snackBarErrorText();
            error.setText("You haven't entered the manager's name.");
        } else if (street.equals("") || street.length() == 0 || street == null) {
            snackBarErrorText();
            error.setText("You haven't entered the address.");
        } else if (!alphaNumeric(pass)) {
            snackBarErrorText();
            error2.setText("Your password does not meet the requirements. Click the help button.");
        } else if (user.length() == 0 || user.isEmpty() || user == null) {
            snackBarErrorText();
            error2.setText("Please enter a username.");
        }/* else if (!uniques[0]) {
            snackBarErrorText();
            error2.setText("This username is already taken. Please choose another.");
        } else if (!uniques[1]) {
            snackBarErrorText();
            error2.setText("The place with this address already has an account. If you think this is an error, contact ijapps101@gmail.com.");
        }*/ else {
            if (validPlace) {
                if (phone.equalsIgnoreCase("Not available")) {
                    makeAlertInfo(true, "Invalid Address", "The place that you selected does not have a verifiable phone #. Please choose another place or if you think this is a mistake, contact ijapss101@gmail.com.");
                } else if (name.equalsIgnoreCase("Not available")) {
                    makeAlertInfo(true, "Invalid Address", "The location you choose does not have a name. If you think this is a mistake, contact ijapss101@gmail.com.");
                } else if (address.contains(name)) {
                    makeAlertInfo(true, "Invalid Address", "Please choose another place or if you think this is a mistake, contact ijapss101@gmail.com.");
                } else {
                    //askIfCertain(rORs, manager, user, pass);
                    usernameUnique(rORs, manager, user, pass, address);
                }
            } else {
                if (wrongType) {
                    makeAlertInfo(true, "Invalid Address", "The address that you have chosen is not valid and was most likely that for a road or premise. Please search by location name first followed by address. If you think this an error, contact ijapps101@gmail.com.");
                } else {
                    makeAlertInfo(true, "Invalid Address", "The address that you have chosen is not valid. Possible reasons include:\n      Not a " + type.getText().toString() + "\n      No verifiable phone #.\nIf you think this an error, contact ijapps101@gmail.com.");
                }
            }
        }
    }

    private void askIfCertain(final String rORs, final String manager, final String user, final String pass) {
        final Dialog alert = new Dialog(InfoPages.this);
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert.setContentView(R.layout.are_you_sure);
        alert.setCancelable(false);
        alert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        Button close = alert.findViewById(R.id.continueN);
        Button stay = alert.findViewById(R.id.stay);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
                alert.cancel();
                Intent intent = new Intent(InfoPages.this, Settings.class);
                intent.putExtra("Type", rORs);
                intent.putExtra("Name", name);
                intent.putExtra("Manager", manager);
                intent.putExtra("Address", address);
                intent.putExtra("Username", user);
                intent.putExtra("Password", pass);
                intent.putExtra("Website", website);
                intent.putExtra("Phone", phone);
                intent.putExtra("Rating", rating);
                intent.putExtra("Hours", hours);
                intent.putExtra("Lat", location.latitude);
                intent.putExtra("Long", location.longitude);
                error.setText("");
                error2.setText("");
                startActivity(intent);
            }
        });

        stay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
                alert.cancel();
            }
        });

        alert.show();

    }

    public void usernameUnique(final String rORs, final String manager, final String user, final String pass, final String a) {
        DatabaseReference accounts = FirebaseDatabase.getInstance().getReference("userAddress");
        accounts.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean uG = true;
                boolean aG = true;
                String uE = "";
                String aE = "";
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    String users = data.child("Username").getValue().toString();
                    String address = data.child("Address").getValue().toString();
                    if (users.equals(user)) {
                        uG = false;
                        uE = user;
                    }
                    if (address.equals(a)) {
                        aG = false;
                        aE = address;
                    }
                }

                if (!uG) {
                    snackBarErrorText();
                    error2.setText("This username (" + uE + ") is already taken. Please choose another.");
                } else if (!aG) {
                    snackBarErrorText();
                    error2.setText("The place with this address (" + aE + ") already has an account. If you think this is an error, contact ijapps101@gmail.com.");
                } else {
                    askIfCertain(rORs, manager, user, pass);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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

    /*@Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }*/

    @Override
    public void onBackPressed() {
        startActivity(new Intent(InfoPages.this, MainActivity.class));
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sign_up_pages, menu);

        // return true so that the menu pop up is opened
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                showAppInfo();
        }
        return super.onOptionsItemSelected(item);
    }

    public void showAppInfo() {
        final Dialog alert = new Dialog(InfoPages.this);
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert.setContentView(R.layout.about_this_app);
        alert.setCancelable(true);

        alert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        alert.show();

    }

    public void findViews() {
        type = findViewById(R.id.togglebutton);
        //tname = findViewById(R.id.tname);
        title = findViewById(R.id.next);
        typeH = findViewById(R.id.typeHelp);
        //nameH = findViewById(R.id.nameHelp);
        managerH = findViewById(R.id.managerHelp);
        addressH = findViewById(R.id.addressHelp);
        error = findViewById(R.id.error);
        error2 = findViewById(R.id.error2);
        userH = findViewById(R.id.userHelp);
        passH = findViewById(R.id.passHelp);
        chooseAddress = findViewById(R.id.chooseAddress);
        //streetA = findViewById(R.id.street);
        managerName = findViewById(R.id.managerName);
        /*cityA = findViewById(R.id.city);
         */
        //map = findViewById(R.id.map);
        usernameE = findViewById(R.id.username);
        passwordE = findViewById(R.id.password);
        next = findViewById(R.id.nextB);
        // nearby = findViewById(R.id.nearby);
        //pickMap = findViewById(R.id.pickMap);
        //state = findViewById(R.id.state);
    }

    private void makeToast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}

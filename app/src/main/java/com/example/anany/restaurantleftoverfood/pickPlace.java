package com.example.anany.restaurantleftoverfood;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.location.Address;
import android.location.Geocoder;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.OpeningHours;
import com.google.android.libraries.places.api.model.Period;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPhotoResponse;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class pickPlace extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;

    double lon = -99.7;
    double lat = 39.7;
    //TextView responseView;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 23487;
    Place currentPlace;
    LatLng location;
    MarkerOptions[] list = new MarkerOptions[2];
    AutocompleteSupportFragment autocompleteSupportFragment;
    String type = "Shelter";
    int tries = 0;
    boolean choosen;
    Button returnBack;
    String address, phone, image, website, hours, name;
    Bitmap placeBitmap;
    String output;

    String city;
    boolean validPlace;
    boolean wrongType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_place);
        Places.initialize(getApplicationContext(), "AIzaSyAAuJitQF8VFy5vxkFcTbsW2TmS8y7J4js");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        choosen = false;
        tries = 0;
        validPlace = false;
        location = null;
        placeBitmap = null;
        wrongType = false;

        returnBack = findViewById(R.id.returnB);

        setButtonClickListener();
        //responseView = findViewById(R.id.response);
       /* try {
            readFromFile(getApplicationContext());
        } catch (IOException e) {
            makeToast(e.toString());
        }*/

        setupAutocompleteSupportFragment();
    }

    public void setButtonClickListener() {
        returnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InfoPages infoPages = new InfoPages();
                OutputStreamWriter outputStreamWriter = null;
                if (location != null) {
                    if (validPlace || tries == 1) {
                        infoPages.setLat(location.latitude);
                        infoPages.setLon(location.longitude);
                        try {
                            outputStreamWriter = new OutputStreamWriter(getApplicationContext().openFileOutput("place.txt", Context.MODE_PRIVATE));
                            //outputStreamWriter.write(location.latitude + "\n" + location.longitude);
                            outputStreamWriter.write(validPlace + "\n"+ location.latitude + "\n" + location.longitude+"\n"+ output);
                            outputStreamWriter.close();

                            InfoPages ip = new InfoPages();
                            outputStreamWriter = new OutputStreamWriter(getApplicationContext().openFileOutput("extra.txt", Context.MODE_PRIVATE));
                            //outputStreamWriter.write(location.latitude + "\n" + location.longitude);
                            outputStreamWriter.write(ip.isChecked() + "\n"+ ip.getmName() + "\n" + ip.getsUser() +"\n"+ ip.getsPass());
                            outputStreamWriter.close();
                            startActivity(new Intent(pickPlace.this, InfoPages.class));
                        } catch (Exception e) {
                            makeToast("ERROR: " + e.toString());
                        }
                    } else {
                        if (!wrongType) {
                            FrameLayout frameLayout = findViewById(R.id.frame);
                            final Snackbar snackbar = Snackbar
                                    .make(frameLayout, "WARNING! The place you have chosen is invalid because it does not seem to be a " + type + ". Please change the type of location on the previous page, search for another location, or contact ijapps101@gmail.com if you think this is a mistake.", Snackbar.LENGTH_INDEFINITE)
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

                            tries++;
                        } else {
                            FrameLayout frameLayout = findViewById(R.id.frame);
                            final Snackbar snackbar = Snackbar
                                    .make(frameLayout, "WARNING! The place you have chosen is invalid because it does not seem to be a " + type + ", but instead more of a premise or street.\n*Please do not search only by address, but instead, search using the name of the place.", Snackbar.LENGTH_INDEFINITE)
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

                            tries++;
                        }
                    }
                } else {
                    if (tries == 0) {
                        FrameLayout frameLayout = findViewById(R.id.frame);
                        String s;
                        if (choosen)
                            s = "IMPORTANT!!! You have chosen a location on the map and must now search for the name in the search box. Otherwise, the address won't register.";
                        else
                            s = "IMPORTANT!!! You have not chosen a location. Are you sure you want to go back? Pick your location from the map or search box. Regardless, you must finalize your decision in the search box.";
                        final Snackbar snackbar = Snackbar
                                .make(frameLayout, s, Snackbar.LENGTH_INDEFINITE)
                                .setDuration(10000)
                                .setActionTextColor(Color.parseColor("#8ce73c"));
                        snackbar.setAction("DISMISS", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Call your action method here
                                snackbar.dismiss();
                            }
                        });
                        TextView snackTextView = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
                        snackTextView.setBreakStrategy(Layout.BREAK_STRATEGY_SIMPLE);
                        snackTextView.setMaxLines(8);
                        snackbar.show();
                    } else {
                        try {
                            outputStreamWriter = new OutputStreamWriter(getApplicationContext().openFileOutput("place.txt", Context.MODE_PRIVATE));
                            outputStreamWriter.write("false\n39.7\n-99.7\n"+ output);
                            outputStreamWriter.close();
                            InfoPages ip = new InfoPages();
                            outputStreamWriter = new OutputStreamWriter(getApplicationContext().openFileOutput("extra.txt", Context.MODE_PRIVATE));
                            //outputStreamWriter.write(location.latitude + "\n" + location.longitude);
                            outputStreamWriter.write(ip.isChecked() + "\n"+ ip.getmName() + "\n" + ip.getsUser() +"\n"+ ip.getsPass());
                            outputStreamWriter.close();
                            startActivity(new Intent(pickPlace.this, InfoPages.class));
                        } catch (Exception e) {
                            makeToast("ERROR: " + e.toString());
                        }

                        tries = 0;
                    }

                    tries++;
                }
            }
        });
    }

    private void setupAutocompleteSupportFragment() {
        autocompleteSupportFragment =
                (AutocompleteSupportFragment)
                        getSupportFragmentManager().findFragmentById(R.id.autocomplete_support_fragment);

        autocompleteSupportFragment.setPlaceFields(getPlaceFields());
        autocompleteSupportFragment.setOnPlaceSelectedListener(getPlaceSelectionListener());
        autocompleteSupportFragment.setHint("Start Searching by Name!");
        //autocompleteSupportFragment.setCountry(getCountry());
        //autocompleteSupportFragment.setLocationBias(getLocationBias());
        autocompleteSupportFragment.setTypeFilter(null);
    }


    private List<Place.Field> getPlaceFields() {
        List<Place.Field> arr = new ArrayList<>();
        arr.add(Place.Field.ADDRESS);
        arr.add(Place.Field.NAME);
        arr.add(Place.Field.LAT_LNG);
        arr.add(Place.Field.TYPES);
        arr.add(Place.Field.PHONE_NUMBER);
        arr.add(Place.Field.ID);
        arr.add(Place.Field.OPENING_HOURS);
        arr.add(Place.Field.WEBSITE_URI);
        arr.add(Place.Field.PHOTO_METADATAS);
        return arr;
    }

    
    private PlaceSelectionListener getPlaceSelectionListener() {
        return new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                makeToast(
                        "You chose: " + place.getName() + ".");
                currentPlace = place;
                location = place.getLatLng();

                String output3 = "";
                if (place.getTypes() != null) {
                    List<Place.Type> types = place.getTypes();
                    for (Place.Type t : types) {
                        output3 += t.toString() + "\n";
                    }
                    if (type.equals("Shelter")) {
                        for (Place.Type type : types) {
                            if (type.toString().equalsIgnoreCase("health") || type.toString().equalsIgnoreCase("establishment") || type.toString().equalsIgnoreCase("hospital")) {
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
                if (place.getName() != null || !place.getName().isEmpty())
                    name = place.getName();
                else
                    name = "Not available";

                if (place.getPhoneNumber() != null)
                    phone = place.getPhoneNumber();
                else {
                    phone = "Not available";
                    FrameLayout frameLayout = findViewById(R.id.frame);
                    final Snackbar snackbar = Snackbar
                            .make(frameLayout, "WARNING! The place you selected does not have a valid phone # that can be used to verify your account. Please choose another location or if you think this is an error, contact ijapps101@gmail.com.", Snackbar.LENGTH_INDEFINITE)
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
                    }
                } else
                    output2 = "Not available";

                if (place.getWebsiteUri() != null)
                    website = place.getWebsiteUri().toString();
                else
                    website = "Not available";

                output = address + "\n" + name + "\n" + phone + "\n" + website + "\n" + output2;
                makeAlertInfo(output3);

                mMap.clear();
                //makeAlertInfo("Name: " + place.getName() + "\nAddress: " + place.getAddress() + "\nPhone Number: " + place.getPhoneNumber());
                if (list[0] != null)
                    mMap.addMarker(list[0]);
                tries = 0;
                mMap.addMarker(new MarkerOptions().position(location).title(place.getName()));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 15.0f));
            }

            @Override
            public void onError(Status status) {
                makeToast(status.getStatusMessage());
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent intent) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == AutocompleteActivity.RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(intent);
                makeToast(
                        StringUtil.stringifyAutocompleteWidget(place, false));
                makeToast(place.getPhoneNumber());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(intent);
                makeToast(status.getStatusMessage());
            } else if (resultCode == AutocompleteActivity.RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }

        // Required because this class extends AppCompatActivity which extends FragmentActivity
        // which implements this method to pass onActivityResult calls to child fragments
        // (eg AutocompleteFragment).
        super.onActivityResult(requestCode, resultCode, intent);
    }

    private void getPhoneNumber() {

        /*placesClient = Places.createClient(getApplicationContext());

        List<Place.Field> fields = Arrays.asList(Place.Field.PHOTO_METADATAS);
        FetchPlaceRequest placeRequest = FetchPlaceRequest.builder(place.getId(), fields).build();
        placesClient.fetchPlace(placeRequest).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
            @Override
            public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
                Place place = fetchPlaceResponse.getPlace();
                PhotoMetadata photoMetadata = place.getPhotoMetadatas().get(0);

                FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                        .setMaxWidth(500) // Optional.
                        .setMaxHeight(300) // Optional.
                        .build();
                placesClient.fetchPhoto(photoRequest).addOnSuccessListener(new OnSuccessListener<FetchPhotoResponse>() {
                    @Override
                    public void onSuccess(FetchPhotoResponse fetchPhotoResponse) {
                        Bitmap bitmap = fetchPhotoResponse.getBitmap();
                        placeBitmap = bitmap == null ? null : bitmap;
                        makeToast("Bitmap is null: " + (bitmap == null));

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure( Exception e) {
                        makeToast("Error getting an image: " + e.toString());
                    }
                });

            }
        });*/
    }

    private String readFromFile(Context context) throws IOException {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput("location.txt");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();
                int count = 0;
                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                    if (count == 0)
                        lat = Double.parseDouble(receiveString);
                    if (count == 1) lon = Double.parseDouble(receiveString);
                    count++;
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            //makeToast("Current Location Unknown");
            //makeToast("Doesn't exist");
        } catch (IOException e) {
            //makeToast("Can not read file: " + e.toString());
        }

        return ret;
    }


    @Override
    public void onBackPressed() {
        InfoPages infoPages = new InfoPages();
        OutputStreamWriter outputStreamWriter = null;
        if (location != null) {
            if (validPlace || tries == 1) {
                infoPages.setLat(location.latitude);
                infoPages.setLon(location.longitude);
                try {
                    outputStreamWriter = new OutputStreamWriter(getApplicationContext().openFileOutput("place.txt", Context.MODE_PRIVATE));
                    //outputStreamWriter.write(location.latitude + "\n" + location.longitude);
                    outputStreamWriter.write(validPlace + "\n"+ location.latitude + "\n" + location.longitude+"\n"+ output);
                    outputStreamWriter.close();

                    InfoPages ip = new InfoPages();
                    outputStreamWriter = new OutputStreamWriter(getApplicationContext().openFileOutput("extra.txt", Context.MODE_PRIVATE));
                    //outputStreamWriter.write(location.latitude + "\n" + location.longitude);
                    outputStreamWriter.write(ip.isChecked() + "\n"+ ip.getmName() + "\n" + ip.getsUser() +"\n"+ ip.getsPass());
                    outputStreamWriter.close();
                    startActivity(new Intent(pickPlace.this, InfoPages.class));
                    super.onBackPressed();
                } catch (Exception e) {
                    makeToast("ERROR: " + e.toString());
                }
            } else {
                if (!wrongType) {
                    FrameLayout frameLayout = findViewById(R.id.frame);
                    final Snackbar snackbar = Snackbar
                            .make(frameLayout, "WARNING! The place you have chosen is invalid because it does not seem to be a " + type + ". Please change the type of location on the previous page, search for another location, or contact ijapps101@gmail.com if you think this is a mistake.", Snackbar.LENGTH_INDEFINITE)
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

                    tries++;
                } else {
                    FrameLayout frameLayout = findViewById(R.id.frame);
                    final Snackbar snackbar = Snackbar
                            .make(frameLayout, "WARNING! The place you have chosen is invalid because it does not seem to be a " + type + ", but instead more of a premise or street.\n*Please do not search only by address, but instead, search using the name of the place.", Snackbar.LENGTH_INDEFINITE)
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

                    tries++;
                }
            }
        } else {
            if (tries == 0) {
                FrameLayout frameLayout = findViewById(R.id.frame);
                String s;
                if (choosen)
                    s = "IMPORTANT!!! You have chosen a location on the map and must now search for the name in the search box. Otherwise, the address won't register.";
                else
                    s = "IMPORTANT!!! You have not chosen a location. Are you sure you want to go back? Pick your location from the map or search box. Regardless, you must finalize your decision in the search box.";
                final Snackbar snackbar = Snackbar
                        .make(frameLayout, s, Snackbar.LENGTH_INDEFINITE)
                        .setDuration(10000)
                        .setActionTextColor(Color.parseColor("#8ce73c"));
                snackbar.setAction("DISMISS", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Call your action method here
                        snackbar.dismiss();
                    }
                });
                TextView snackTextView = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
                snackTextView.setBreakStrategy(Layout.BREAK_STRATEGY_SIMPLE);
                snackTextView.setMaxLines(8);
                snackbar.show();
            } else {
                try {
                    outputStreamWriter = new OutputStreamWriter(getApplicationContext().openFileOutput("place.txt", Context.MODE_PRIVATE));
                    outputStreamWriter.write("false\n39.7\n-99.7\n"+ output);
                    outputStreamWriter.close();

                    InfoPages ip = new InfoPages();
                    outputStreamWriter = new OutputStreamWriter(getApplicationContext().openFileOutput("extra.txt", Context.MODE_PRIVATE));
                    //outputStreamWriter.write(location.latitude + "\n" + location.longitude);
                    outputStreamWriter.write(ip.isChecked() + "\n"+ ip.getmName() + "\n" + ip.getsUser() +"\n"+ ip.getsPass());
                    outputStreamWriter.close();
                    startActivity(new Intent(pickPlace.this, InfoPages.class));
                    super.onBackPressed();
                } catch (Exception e) {
                    makeToast("ERROR: " + e.toString());
                }

                tries = 0;
            }

            tries++;
        }

    }

    PlacesClient placesClient;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        /*mMap.setMinZoomPreference(6.0f);
        mMap.setMaxZoomPreference(15.0f);*/
        //makeToast("On Map Ready: " + lon + " " + lat);
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                InfoPages ip = new InfoPages();
                //makeToast("LAT: " +ip.getLat());
                lon = ip.getLon();
                lat = ip.getLat();
                type = ip.getLtype();
                boolean current = ip.getCurrent();
                //makeToast("TYPE: " + type);
                if (lon != -99.7) {
                    LatLngBounds AUSTRALIA = new LatLngBounds(
                            new LatLng(lat - 0.04, lon - 0.04), new LatLng(lat + 0.04, lon + 0.04));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(AUSTRALIA, 0));
                    if (current) {
                        list[0] = new MarkerOptions().position(new LatLng(lat, lon)).icon(BitmapDescriptorFactory.fromBitmap(scaledBitmap(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.bluecircle), 0.15f)));
                        mMap.addMarker(list[0]);
                    } else {
                        mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lon)));
                    }
                } else {
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lat, lon)));
                    mMap.setMinZoomPreference(3.4f);
                }

                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        mMap.clear();
                        if (list[0] != null)
                            mMap.addMarker(list[0]);
                        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                        List<Address> strings = null;
                        try {
                            strings = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 3);
                        } catch (IOException e) {
                            makeToast("Error. Couldn't get address: " + e.toString());
                        }
                        String title = "";
                        if (strings == null || strings.isEmpty()) {
                            title = "Marker";
                        } else {
                            title = strings.get(0).getAddressLine(0);
                            //makeToast("You chose: " + strings.get(0).getAddressLine(0));
                        }
                        tries = 0;
                        choosen = true;
                        location = null;
                        autocompleteSupportFragment.setText("");

                        //task.addOnCompleteListener(response -> setLoading(false));

                        FrameLayout frameLayout = findViewById(R.id.frame);
                        final Snackbar snackbar = Snackbar
                                .make(frameLayout, "Great, you got the location! Now you need to enter the search box and type in the name of the place.", Snackbar.LENGTH_INDEFINITE)
                                .setDuration(8000)
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

                        FindAutocompletePredictionsRequest.Builder requestBuilder =
                                FindAutocompletePredictionsRequest.builder()
                                        .setQuery(strings.get(0).getAddressLine(0))
                                        .setTypeFilter(TypeFilter.ADDRESS);

                        placesClient = Places.createClient(getApplicationContext());

                        if (true) {
                            requestBuilder.setSessionToken(AutocompleteSessionToken.newInstance());
                        }

                        Task<FindAutocompletePredictionsResponse> task =
                                placesClient.findAutocompletePredictions(requestBuilder.build());

                        task.addOnSuccessListener(new OnSuccessListener<FindAutocompletePredictionsResponse>() {
                            @Override
                            public void onSuccess(final FindAutocompletePredictionsResponse findAutocompletePredictionsResponse) {
                                List<AutocompletePrediction> list = findAutocompletePredictionsResponse.getAutocompletePredictions();
                                if (list.isEmpty() || list == null) {
                                    //makeToast("Please reselect the place or use the search box.");
                                } else {
                                    AutocompletePrediction ap = list.get(0);
                                    //makeToast(StringUtil.stringify(findAutocompletePredictionsResponse, true));
                                    makeAlertInfo(StringUtil.stringify(findAutocompletePredictionsResponse, true));
                                    ap.getPlaceId();
                                    //makeToast("ID: " + ap.getPlaceId());
                                    List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.PHONE_NUMBER);
                                    FetchPlaceRequest request = FetchPlaceRequest.builder(ap.getPlaceId(), placeFields).build();
                                    placesClient = Places.createClient(getApplicationContext());
                                    placesClient.fetchPlace(request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                                        @Override
                                        public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
                                            Place place = fetchPlaceResponse.getPlace();
                                            TextView tt = findViewById(R.id.message);
                                            makeAlertInfo(tt.getText().toString() + "\n-----------------\nName: " + place.getName() + "\nAddress: " + place.getAddress() + "\nPhone #: " + place.getPhoneNumber());
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure( Exception e) {
                                            makeToast("Error getting the place. Please try using the search box.\nMessage: " + e.toString());
                                        }
                                    });
                                }
                            }
                        });
                        task.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure( Exception e) {
                                makeToast(e.toString());
                            }
                        });

                        mMap.addMarker(new MarkerOptions().position(latLng).title(title));
                /*if(list[0]!=null){
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    builder.include(list[0].getPosition());
                    builder.include(latLng);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(),250));
                }else{*/
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));

                        //mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                        //}
                    }
                });
            }
        });


    }
    /**/

    private void makeAlertInfo(String s) {
        TextView message = findViewById(R.id.message);
        message.setText(s);

    }

    private Bitmap scaledBitmap(Bitmap b, float scale) {
        Bitmap bitmap = Bitmap.createScaledBitmap(b, Math.round(b.getWidth() * scale), Math.round(b.getHeight() * scale), false);
        return bitmap;
    }

    private void makeToast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

}

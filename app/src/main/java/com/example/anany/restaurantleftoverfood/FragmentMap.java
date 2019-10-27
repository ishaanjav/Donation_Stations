package com.example.anany.restaurantleftoverfood;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anany.restaurantleftoverfood.Storage.Singleton;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentMap.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentMap#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentMap extends Fragment implements OnMapReadyCallback {
    // DONE: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static Context mContext;

    // DONE: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FragmentMap() {
        // Required empty public constructor
    }

    static String address;
    static double searchRadius;
    static double lat;
    static double lon;
    static String units;
    static String distance;
    String name;
    static boolean inMap;
    static String type;

    AutocompleteSupportFragment autocompleteSupportFragment;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentMap.
     */
    // DONE: Rename and change types and number of parameters
    public static FragmentMap newInstance(String param1, String param2, Context context) {
        FragmentMap fragment = new FragmentMap();
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

        setHasOptionsMenu(true);
    }

    MapView mapView;
    static GoogleMap map;
    static ImageView target;

    static Animation right;
    static Animation left;
    static Animation oRight;
    static Animation oLeft;

    static Dialog show;
    static ImageView info;
    static RelativeLayout box;

    static ImageView web;
    static ImageView pho;

    static Marker specialMarker;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        readFromFile();
        target = getView().findViewById(R.id.target);
        target.setVisibility(View.INVISIBLE);
        right = AnimationUtils.loadAnimation(mContext, R.anim.slide_out_right);
        oRight = AnimationUtils.loadAnimation(mContext, R.anim.opposite_right);
        oLeft = AnimationUtils.loadAnimation(mContext, R.anim.opposite_left);
        left = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_left);
        box = getView().findViewById(R.id.box);
        info = getView().findViewById(R.id.info);
        setupAutocompleteSupportFragment();
        firsttime = 0;

        box.setVisibility(View.INVISIBLE);
        /*mapView = getView().findViewById(R.id.map);
        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        } else {
            makeToast("NULL MAP");
        }*/
    }

    private void setupAutocompleteSupportFragment() {
        autocompleteSupportFragment =
                (AutocompleteSupportFragment)
                        getChildFragmentManager().findFragmentById(R.id.auto);

        autocompleteSupportFragment.setPlaceFields(getPlaceFields());
        autocompleteSupportFragment.setOnPlaceSelectedListener(getPlaceSelectionListener());
        autocompleteSupportFragment.setHint("Search");
        //autocompleteSupportFragment.setCountry(getCountry());
        //autocompleteSupportFragment.setLocationBias(getLocationBias());
        autocompleteSupportFragment.setTypeFilter(null);
        EditText etPlace = (EditText) autocompleteSupportFragment.getView().findViewById(R.id.places_autocomplete_search_input);
        etPlace.setHint("Search");
        etPlace.setHintTextColor(Color.parseColor("#7927c2"));
        Typeface face = ResourcesCompat.getFont(mContext, R.font.regular);
        etPlace.setTextSize(22.0f);
        etPlace.setTypeface(face);
        //etPlace.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        etPlace.setPadding(-3, -8, 0, -8);
        //etPlace.setBackground(getResources().getDrawable(R.drawable.slback));
    }

    String ignoreId;


    private PlaceSelectionListener getPlaceSelectionListener() {
        return new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                if (specialMarker != null) {
                    specialMarker.remove();
                }
                //DONE Make following a blue marker.
                //DONE Make green marker lighter.
                //OLD Maker blue marker on tap and set title to name and address.
                LatLng latLng = place.getLatLng();
                BitmapDescriptor same = BitmapDescriptorFactory.fromBitmap(scaledBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.blue), 0.083f));
                specialMarker = map.addMarker(new MarkerOptions().title(place.getName()).icon(same).position(latLng));
                makeToast(place.getName() + "\n" + place.getAddress());
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f));
            }

            @Override
            public void onError(Status status) {
                makeToast(status.getStatusMessage());
            }
        };
    }

    @Override
    public void onStart() {

        super.onStart();
    }

    private List<Place.Field> getPlaceFields() {
        List<Place.Field> arr = new ArrayList<>();
        arr.add(Place.Field.ADDRESS);
        arr.add(Place.Field.NAME);
        arr.add(Place.Field.LAT_LNG);
        return arr;
    }

    static double yards;
    static boolean on;
    static HashMap<String, MarkerInfo> markers;
    static MarkerInfo markerInfo;

    static boolean changed = true;
    boolean ignore;
    static String id;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //DONE Find out how to recall onMapReady each time user comes to this fragment.
        //DONE So do it each time when you come here. Restart the map.
        MapsInitializer.initialize(mContext);

        map = googleMap;
        //map.clear();
        id = "";
        ignoreId = "";
        ignore = false;
        doMapStuff();

    }

    static double sLat, sLon;

    static Marker fromList;
    static boolean check;

    public static void doMapStuff() {
        //final String receive = readForSpecial();
        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
*/

        sLat = 0;
        sLon = 0;
        boolean changeCamera = false;
        /*if (receive.length() > 10) {
            changeCamera = true;
            sLat = Double.parseDouble(receive.split(" ")[0]);
            sLon = Double.parseDouble(receive.split(" ")[1]);
        }*/
        if (Singleton.isShowInMap()) {
            changeCamera = true;
            sLat = Singleton.getLat();
            sLon = Singleton.getLon();
        }
        makeToast("Showing results within " + distance + " " + units + " radius.");
        LatLngBounds AUSTRALIA = new LatLngBounds(
                new LatLng(lat - searchRadius * 1.162, lon - searchRadius * 1.162), new LatLng(lat + searchRadius * 1.162, lon + searchRadius * 1.162));
        target.setVisibility(View.INVISIBLE);
        box.setVisibility(View.INVISIBLE);
        //README They have clicked View on Map from List.
        on = true;
        check = false;
        if (changeCamera) {
            /*Intent intent = new Intent(mContext, Home.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);*/
            double latDif = Math.abs(lat - sLat);
            double lonDif = Math.abs(lon - sLon);
            double centerLat = latDif / 2 + Math.min(lat, sLat);
            double centerLon = lonDif / 2 + Math.min(lon, sLon);

            final LatLngBounds box = new LatLngBounds(
                    new LatLng(centerLat - 0.95 * latDif, centerLon - 0.95 * lonDif), new LatLng(centerLat + 0.95 * latDif, centerLon + 0.95 * lonDif));

            Singleton.setShowInMap(false);
            on = false;
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(box, 0));
            /*new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //makeToast("HI!");
                    map.animateCamera(CameraUpdateFactory.newLatLngBounds(box, 0));
                }
            },2000);*/

            File file = new File(mContext.getFilesDir(), "inMap.txt");
            boolean deleted = file.delete();
            changeCamera = false;
           /* SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);*/
        } else {
            check = true;
            changed = false;
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(AUSTRALIA, 0), new GoogleMap.CancelableCallback() {
                @Override
                public void onFinish() {
                    if (check) {
                        check = false;
                        changed = true;
                    }
                }

                @Override
                public void onCancel() {
                    changed = true;
                    check = false;
                    target.setVisibility(View.VISIBLE);
                    target.startAnimation(left);
                }
            });

        }
        //check = false;
        target.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                LatLngBounds AUSTRALIA2 = new LatLngBounds(
                        new LatLng(lat - searchRadius * 1.162, lon - searchRadius * 1.162), new LatLng(lat + searchRadius * 1.162, lon + searchRadius * 1.162));

                check = true;
                changed = false;

                target.startAnimation(right);
                target.setVisibility(View.INVISIBLE);

                map.animateCamera(CameraUpdateFactory.newLatLngBounds(AUSTRALIA2, 0), new GoogleMap.CancelableCallback() {
                    @Override
                    public void onFinish() {
                        if (check) {
                            check = false;
                            changed = true;
                        }
                    }

                    @Override
                    public void onCancel() {
                        changed = true;
                        check = false;
                        target.setVisibility(View.VISIBLE);
                        target.startAnimation(left);
                    }
                });


               /* Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        changed = true;
                    }
                }, 1375);*/
                return false;

            }
        });




        /*map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                changed = true;
                if(changed&&target.getVisibility() != View.VISIBLE) {
                    target.setVisibility(View.VISIBLE);
                    target.startAnimation(left);
                }
            }
        });*/
        map.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                if (changed && target.getVisibility() != View.VISIBLE) {
                    target.setVisibility(View.VISIBLE);
                    target.startAnimation(left);
                }
            }
        });
        info.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //DONE Write code to show info using markerInfo.

                if (fromList != null) {
                    markerInfo = markers.get(fromList.getId());
                    fromList = null;
                }

                show = new Dialog(mContext);
                show.requestWindowFeature(Window.FEATURE_NO_TITLE);

                if (markerInfo.getType2().contains("est"))
                    show.setContentView(R.layout.show_map_info);
                else
                    show.setContentView(R.layout.show_map_info2);


                show.setCancelable(true);
                show.getWindow().setBackgroundDrawableResource(R.color.transparent);

                TextView title = show.findViewById(R.id.title);
                title.setPaintFlags(title.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                title.setText(markerInfo.getName());

                if (markerInfo.getType2().contains("elt")) {
                    //INFO Show the amount of food required.
                    TextView amount = show.findViewById(R.id.amount);
                    if (amount != null)
                        amount.setText(markerInfo.getAmount());
                }

                TextView location = show.findViewById(R.id.location);
                location.setText(markerInfo.getType2());

                TextView specialT = show.findViewById(R.id.specialT);
                TextView special = show.findViewById(R.id.special);
                if (!markerInfo.getType2().contains("elt"))
                    specialT.setText("# of Donations:");

                special.setText(markerInfo.getStatus());

                TextView address = show.findViewById(R.id.address);
                address.setText(markerInfo.getAddress2());

                TextView hours = show.findViewById(R.id.hours);
                hours.setText(getHours(markerInfo.getHours()));

                web = show.findViewById(R.id.webIcon);
                pho = show.findViewById(R.id.phoneIcon);

                pho.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:+1 732-306-9190"));
                        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.CALL_PHONE}, 1);
                        } else
                            makeToast("Calling " + markerInfo.getName() + "\nat " + markerInfo.getPhone() + ".");
                        //mContext.startActivity(intent);
                        return false;
                    }
                });

                web.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (markerInfo.getWebsite().contains("Not av")) {
                            makeToast(markerInfo.getName() + " does not have a website.");
                        } else {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(markerInfo.getWebsite()));
                            mContext.startActivity(intent);
                        }
                        return false;
                    }
                });

                String in = markerInfo.getAddress2() + "\n" + markerInfo.getName() + "\n" + markerInfo.getHours() + "\n" +
                        markerInfo.getPhone() + "\n" + markerInfo.getType2() + "\n" + markerInfo.getWebsite() + "\n" + markerInfo.getLatT() + "\n" +
                        markerInfo.getLonT();

                TextView distance = show.findViewById(R.id.distance);
                double latT = markerInfo.getLatT();
                double lonT = markerInfo.getLonT();
                double dif = Math.sqrt(Math.pow(latT - lat, 2) + Math.pow(lonT - lon, 2));

                if (units.equals("mi")) {
                    dif *= 69;
                } else
                    dif *= 110;

                dif = Math.round(dif * 10) / 10d;
                distance.setText(dif + " " + units);

                TextView phone = show.findViewById(R.id.phone);
                phone.setText(markerInfo.getPhone());

                show.show();

                return false;
            }
        });

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                //target.setVisibility(View.VISIBLE);

                if (!on) {
                    //DONE Make them slide left;
                    box.startAnimation(oLeft);
                    box.setVisibility(View.INVISIBLE);
                    on = true;
                   /* target.startAnimation(right);
                    target.setVisibility(View.INVISIBLE);*/
                }
            }
        });
        markers = new HashMap();
        /*if(units.equals("mi")){
            yards = Double.parseDouble(distance)*1760d;
        }else{
            yards = Double.parseDouble(distance)*1093.61;
        }*/
        yards = searchRadius * 81d * 1300d;

        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Places");
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                map.clear();
                markers.clear();

                //HashMap<String, Marker> markerStore = new HashMap<>();

                MarkerOptions m = new MarkerOptions().position(new LatLng(lat, lon)).icon(BitmapDescriptorFactory.fromBitmap(scaledBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.bluecircle), 0.19f)));
                Marker t = map.addMarker(m.title("You"));
                id = t.getId();

                CircleOptions circleOptions = new CircleOptions().center(new LatLng(lat, lon)).radius(yards)
                        .strokeWidth(7).strokeColor(0Xff8a41f0).fillColor(0x4ae1ccff);

                map.addCircle(circleOptions);
                BitmapDescriptor same = BitmapDescriptorFactory.fromBitmap(scaledBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.red), 0.086f));
                BitmapDescriptor different = BitmapDescriptorFactory.fromBitmap(scaledBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.image), 0.022f));

                double count = 2;
                for (DataSnapshot children : dataSnapshot.getChildren()) {

                    boolean show = Boolean.parseBoolean(children.child("Discoverable to " + type).getValue().toString());
                    String type2 = children.child("Type").getValue().toString();
                    //MarkerOptions m3 = new MarkerOptions().position(new LatLng(lat - 0.02 * count, lon - 0.03 * count)).icon(different);

                    String address2 = children.child("Address").getValue().toString();
                    //map.addMarker(m3.title("SHOW: " + show + " TYPE: " + type ));
                    count += 2;
                    if (show) {
                       /* MarkerOptions m4 = new MarkerOptions().position(new LatLng(lat +0.02 * count, lon + 0.03 * count)).icon(same);

                        map.addMarker(m4.title("SHOW: " + show + " TYPE: " + type ));*/

                        if (type2.equals(type)) {
                            if (inMap && !address2.equals(address)) {
                                String name = children.child("Name").getValue().toString();
                                String phone = children.child("Phone").getValue().toString();
                                String website = children.child("Website").getValue().toString();
                                String hours = children.child("Hours").getValue().toString();
                                double latT = Double.parseDouble(children.child("Latitude").getValue().toString());
                                double lonT = Double.parseDouble(children.child("Longitude").getValue().toString());
                                MarkerOptions m2 = new MarkerOptions().position(new LatLng(latT, lonT)).icon(same);
                                String status;
                                String amount;
                                if (type2.contains("elt")) {
                                    status = children.child("Food Status").getValue().toString();
                                    amount = children.child("Amount").getValue().toString();
                                } else {
                                    status = children.child("Num of Donations").getValue().toString();
                                    amount = "NA";
                                }
                                Marker marker = (map.addMarker(m2.title(name)));
                                MarkerInfo markerInfo = new MarkerInfo(name, type2, latT, lonT, address2, phone, hours, website, status, amount);

                                markers.put(marker.getId(), markerInfo);
                                //markerStore.put(marker.getId(), marker);
                                if (sLat == latT && sLon == lonT) {
                                    marker.showInfoWindow();
                                    box.startAnimation(oRight);
                                    sLat = 0;
                                    sLon = 0;
                                    box.setVisibility(View.VISIBLE);
                                    fromList = marker;
                                } else {
                                }
                            }
                        } else {
                            String name = children.child("Name").getValue().toString();
                            String phone = children.child("Phone").getValue().toString();
                            String website = children.child("Website").getValue().toString();
                            String hours = children.child("Hours").getValue().toString();
                            double latT = Double.parseDouble(children.child("Latitude").getValue().toString());
                            double lonT = Double.parseDouble(children.child("Longitude").getValue().toString());
                            MarkerOptions m2 = new MarkerOptions().position(new LatLng(latT, lonT)).icon(different);
                            //TODO If restaurant, have to read from # of Donations and pass it in as status
                            String status;
                            String amount;
                            if (type2.contains("elt")) {
                                status = children.child("Food Status").getValue().toString();
                                amount = children.child("Amount").getValue().toString();
                            } else {
                                status = children.child("Num of Donations").getValue().toString();
                                amount = "NA";
                            }

                            Marker marker = (map.addMarker(m2.title(name)));
                            //Marker
                            MarkerInfo markerInfo = new MarkerInfo(name, type2, latT, lonT, address2, phone, hours, website, status, amount);
                            //makeToast("NAME: " + markerInfo.getName());
                            markers.put(marker.getId(), markerInfo);
                            //markerStore.put(marker.getId(), marker);
                            if (sLat == latT && sLon == lonT) {
                                marker.showInfoWindow();
                                box.startAnimation(oRight);
                                box.setVisibility(View.VISIBLE);
                                sLat = 0;
                                sLon = 0;
                                fromList = marker;
                            } else {
                            }
                        }
                    }
                }
                /*if(firsttime == 1){
                    firsttime ++;
                    for(Map.Entry<String, MarkerInfo> entry: markers.entrySet()){
                        if(entry.getValue().getLatT() == Singleton.getLat() && entry.getValue().getLonT() == Singleton.getLon()){
                            Marker s = markerStore.get(entry.getKey());
                            s.showInfoWindow();
                            box.startAnimation(oRight);
                            sLat = 0;
                            sLon = 0;
                            box.setVisibility(View.VISIBLE);
                            fromList = s;
                            break;
                        }
                    }
                }*/
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                makeToast("Error loading locations. Please make sure you have a good WiFi connection.\nERROR: " + databaseError.toString());
            }
        });

        //DONE Write an onclicklistener for the info icon.

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                /*info.dismiss();
                info.cancel();*/
                if (show != null) {
                    show.dismiss();
                    show.cancel();
                }
                boolean fine = false;
                if (specialMarker == null) {
                    fine = true;
                } else {
                    if (!marker.getId().equals(specialMarker.getId()))
                        fine = true;
                }

                if (!marker.getId().equals(id) && fine) {
                    box.clearAnimation();
                    box.setVisibility(View.VISIBLE);
                    box.startAnimation(oRight);
                    markerInfo = markers.get(marker.getId());
                    /*String keys = "";
                    for (MarkerInfo s : markers.values()) {
                        keys += s.getAddress2() + "\n";
                    }*/
                    makeToast("This is " + markerInfo.getName() + ".");
                    //DONE Write code to show the info icon pop up.
                    //DONE Show food status when they click on marker. Or show it in info box.
                    on = false;
                } else if (box.getVisibility() == View.VISIBLE) {
                    box.setVisibility(View.INVISIBLE);
                }

                return false;
            }
        });
           /* }
        }, 500);*/
    }

    public static int firsttime;

    public static void animateLocation(final double sLt, final double sLn) {
        double latDif = Math.abs(lat - sLt);
        double lonDif = Math.abs(lon - sLn);
        double centerLat = latDif / 2 + Math.min(lat, sLt);
        double centerLon = lonDif / 2 + Math.min(lon, sLn);
        sLat = sLt;
        sLon = sLn;

        final LatLngBounds box2 = new LatLngBounds(
                new LatLng(centerLat - 0.95 * latDif, centerLon - 0.95 * lonDif), new LatLng(centerLat + 0.95 * latDif, centerLon + 0.95 * lonDif));

        //Singleton.setShowInMap(false);
        if (firsttime == 0) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    BitmapDescriptor different = BitmapDescriptorFactory.fromBitmap(scaledBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.image), 0.022f));

                    MarkerOptions m2 = new MarkerOptions().position(new LatLng(sLt, sLn)).icon(different);

                    Marker marker = (map.addMarker(m2.title(Singleton.getName())));
                    /*MarkerInfo markerInfo = new MarkerInfo(Singleton.getName(), Singleton.getType(), Singleton.getLat(), Singleton.getLon(), Singleton.getAddress(), Singleton.getPhone(), Singleton.getHours(), Singleton.getWebsite());
                    markers.put(marker.getId(), markerInfo);
                    //makeToast("NAME: " + markerInfo.getName());*/
                    marker.showInfoWindow();
                    /*box.startAnimation(oRight);
                    box.setVisibility(View.VISIBLE);
                    sLat = 0;
                    sLon = 0;
                    fromList = marker;*/

                    makeToast("Tap on the marker again to view extra info.");
                    map.animateCamera(CameraUpdateFactory.newLatLngBounds(box2, 0));
                }
            }, 000);
            firsttime++;
        } else {

        }

    }

    public static void resetMap() {
        if (map != null) {
            map.clear();
        }
    }

    private static String readForSpecial() {
        try {
            InputStream inputStream = mContext.openFileInput("inMap.txt");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";

                receiveString = bufferedReader.readLine();

                return receiveString;
            }
        } catch (Exception e) {
            //makeToast("Error: " + e.toString());
        }
        return "";
    }


    private static Bitmap scaledBitmap(Bitmap b, float scale) {
        Bitmap bitmap = Bitmap.createScaledBitmap(b, Math.round(b.getWidth() * scale), Math.round(b.getHeight() * scale), false);
        return bitmap;
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
        /*View view = inflater.inflate(R.layout.fragment_fragment_map, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                makeToast("Hi");
            }
        });
        MapsInitializer.initialize(getContext());*/
        // Inflate the layout for this fragment
       /* mapView = (MapView) getView().findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        ;

        try {
            MapsInitializer.initialize(mContext);
            handleMap();
        } catch (Exception e) {
            makeToast(e.toString());
        }*/

        //map = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMapAsync();
       /* SupportMapFragment fragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        fragment = SupportMapFragment.newInstance();
        //transaction.replace(R.id.map, fragment).commit();
        fragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                if (googleMap == null) {
                    makeToast("Null map.");
                } else {
                    googleMap.getUiSettings().setAllGesturesEnabled(true);
                    googleMap.addMarker(new MarkerOptions().position(new LatLng(30, 90)).title("HI"));
                }
            }
        });*/


        return inflater.inflate(R.layout.fragment_fragment_map, container, false);
    }

    private void handleMap() {
        //GoogleMap map = mapView.getMapAsync(onMap);
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
        alert.setContentView(R.layout.about_this_app4);
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


    private static void makeToast(String s) {
        if (mContext != null)
            Toast.makeText(mContext, s, Toast.LENGTH_LONG).show();
    }

    private static String getHours(String openingHours) {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        String next = "";
        String result = "";
        int part = 0;
        switch (day) {
            case Calendar.SUNDAY:
                part = openingHours.indexOf("Sunday");
                break;
            case Calendar.MONDAY:
                part = openingHours.indexOf("Monday");
                next = "Tue";
                break;
            case Calendar.TUESDAY:
                part = openingHours.indexOf("Tuesday");
                next = "Wed";
                break;
            case Calendar.WEDNESDAY:
                next = "Thu";
                part = openingHours.indexOf("Wednesday");
                break;
            case Calendar.THURSDAY:
                part = openingHours.indexOf("Thursday");
                next = "Fri";
                break;
            case Calendar.FRIDAY:
                part = openingHours.indexOf("Friday");
                next = "Sat";
                break;
            case Calendar.SATURDAY:
                part = openingHours.indexOf("Saturday");
                next = "Sun";
                break;
        }
        int firstS = openingHours.indexOf(" ", part + 1);
        if (Character.isDigit(openingHours.charAt(firstS + 1))) {
            if (day == 1) {
                result = openingHours.substring(firstS + 1, openingHours.length() - 3);
            } else {
                result = openingHours.substring(firstS + 1, openingHours.indexOf(next) - 3);
            }
            String splitter[] = result.split(",");
            if (splitter.length > 1) {
                result = splitter[0];
                int count = 0;
                for (String s : splitter) {
                    if (count != 0)
                        result += ",\n" + s;
                    count++;
                }
            }
            /*
            int firstPM = openingHours.indexOf("PM", firstS + 5);

            if (day != 1) {
                if (firstPM < openingHours.indexOf(next)) {
                    result = openingHours.substring(firstS + 1, firstPM + 2);
                    if (openingHours.length() > firstPM + 5) {
                        if (openingHours.charAt(firstPM + 2) == ',') {
                            result += ",\n" + openingHours.substring(firstPM + 4, openingHours.indexOf("PM", firstPM + 5) + 2);
                        }
                    }
                } else {
                    int firstAM = openingHours.indexOf("AM", firstS + 5);
                    int nextAM = openingHours.indexOf("AM", firstAM + 5);
                    result = openingHours.substring(firstS + 1, nextAM + 2);
                    if (openingHours.length() > nextAM + 5) {
                        if (openingHours.charAt(nextAM + 2) == ',') {
                            result += ",\n" + openingHours.substring(nextAM + 4, openingHours.indexOf("PM", firstPM + 5) + 2);
                        }
                    }
                }
            } else {
                result = openingHours.substring(firstS + 1, firstPM + 2);
                if (openingHours.length() > firstPM + 5) {
                    if (openingHours.charAt(firstPM + 2) == ',') {
                        result += ",\n" + openingHours.substring(firstPM + 4, openingHours.indexOf("PM", firstPM + 5) + 2);
                    }
                }
            }*/
        } else {
            if (day != 1) {
                int location = openingHours.indexOf(next);
                location -= 3;
                result = openingHours.substring(firstS + 1, location);
            } else {
                result = openingHours.substring(firstS + 1, openingHours.length() - 3);
            }
        }
        return result;
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
}

package com.example.anany.restaurantleftoverfood;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.widget.NestedScrollView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anany.restaurantleftoverfood.Adapters.DonationsListAdapter;
import com.example.anany.restaurantleftoverfood.Adapters.RequestDonationAdapter;
import com.example.anany.restaurantleftoverfood.Adapters.ShelterAdapter;
import com.example.anany.restaurantleftoverfood.Adapters.ShelterApproval;
import com.example.anany.restaurantleftoverfood.Storage.DonationsList;
import com.example.anany.restaurantleftoverfood.Storage.RequestDonation;
import com.example.anany.restaurantleftoverfood.Storage.ShelterInfo;
import com.google.android.gms.common.util.Strings;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import tourguide.tourguide.Overlay;
import tourguide.tourguide.ToolTip;
import tourguide.tourguide.TourGuide;

public class DonationsShelter extends Fragment implements AdapterView.OnItemSelectedListener {

    static Context mContext;

    public static Fragment newInstance(String param1, String param2, Context context) {
        DonationsRestaurant fragment = new DonationsRestaurant();
        mContext = context;
        return fragment;
    }

    static RelativeLayout nested1, nested2, nested3;
    static CancelableScrollView bigScroll;

    static String verification;
    Button update;
    private Boolean mAllowSelectionFiring = false;

    //TODO Decide whether to hard code value of Food Security Status in Settings.java automatically to Doing fine
    //TODO Decide how info will be stored such as donations and donation points and whetehr it will be from a file
    //TODO created at login time or whether it'll be from Firebase.

    //TODO Decide on how to show the food status/# of donations in google maps and in the list.
    //TODO Will we have to read from a different Firebase path to get the food status or will we have to read from Places path.

    //README
    //DONE Probably what you should do is have a value for Food Statuses.
    //OLD After user logs in, in DonationsShelter (here), check if
    //OLD file foodStatus.txt exists. If it exists, then set the spinner to whatever the content of foodStatus is.
    //OLD Also then write the foodStatus to Places path in Firebase.
    //OLD If the file does not exist, read from Firebase and get the food status from Places. Then, write it to the file and then set it to spinner.
    //DONE When logging in user, read the status from Food Status in Places(if not null).
    //DONE Then write status to foodStatus.txt
    //DONE In DonationsShelter(here) read foodStatus.txt and then set spinner.
    //OLD When loggin out, delete foodStatus.txt
    //DONE Also when account of shelter created, auto add foodstatus and set it to Doing Fine. Save to Firebase Places

    static ImageView help, help2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mAllowSelectionFiring = true;
        return inflater.inflate(R.layout.donations_shelter, container, false);
    }

    static RelativeLayout box;

    Spinner statusS;
    static String status;
    CoordinatorLayout container;
    String numLine;

    int lower, upper;
    static EditText num1, num2;
    int tries;
    static EditText n1, n2;
    int d1, d2;

    static String units;
    static double lat, lon;
    static int firstTime = 0;
    static String c1, c2, c3, c4;
    static ListView list1, list2, list3;

    static RelativeLayout box1, box2, box3;
    static TextView s1;

    boolean goodToGo;
    static TextView statusT;

    TextView l1, l2;
    TextView l3, l4, l5;
   static int nc1 = 0, nc2 = 0, nc3 = 0;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mContext = getActivity();
        help = getView().findViewById(R.id.help);
        help2 = getView().findViewById(R.id.help2);
        helpClicker();
        container = getView().findViewById(R.id.container);
        bigScroll = getView().findViewById(R.id.bigScroll);

        /*ArrayList<String> spinnerAdapterData = new ArrayList<>();
        String[] spinnerItemsArray = getResources().getStringArray(R.array.status);
        Collections.addAll(spinnerAdapterData, spinnerItemsArray);
        StatusAdapter adapter = new StatusAdapter(mContext, R.layout.support_simple_spinner_dropdown_item, spinnerAdapterData, getResources());
        mAllowSelectionFiring = true;*/
        tries = 0;
        s1 = getView().findViewById(R.id.subtitle1);

        num1 = getView().findViewById(R.id.firstNum);
        num2 = getView().findViewById(R.id.secondNum);
        update = getView().findViewById(R.id.update);
        update.setVisibility(View.INVISIBLE);
        box = getView().findViewById(R.id.bigBox);
        n1 = getView().findViewById(R.id.firstDay);
        n2 = getView().findViewById(R.id.secondDay);
        list3 = getView().findViewById(R.id.donorsList3);
        list2 = getView().findViewById(R.id.donorsList2);
        list1 = getView().findViewById(R.id.donorsList);
        box3 = getView().findViewById(R.id.box3);
        box1 = getView().findViewById(R.id.box1);
        box2 = getView().findViewById(R.id.box2);
        statusT = getView().findViewById(R.id.statusT);
        
        l3 = getView().findViewById(R.id.t1);
        l4 = getView().findViewById(R.id.t2);
        l5 = getView().findViewById(R.id.t3);
        l1 = getView().findViewById(R.id.numIdText);
        l2 = getView().findViewById(R.id.foodText);

        nested1 = getView().findViewById(R.id.bigBox);
        nested2 = getView().findViewById(R.id.bigBox2);
        nested3 = getView().findViewById(R.id.bigBox3);


        status = "0 0";
        /*statusS = (Spinner) getView().findViewById(R.id.status);
        statusS.setAdapter(adapter);
        statusS.setBackground(getResources().getDrawable(R.drawable.spinner_box2));
        statusS.setOnItemSelectedListener(this);*/
        readStatus();
        readInfo();
        // nested1.getLayoutParams().height=400;
        //nested2.getLayoutParams().height=350;


        requestDonation();
        readDonationReceptions();
        readPendingOffers();
        //checkFirstDonations();

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Integer.parseInt(num1.getText().toString()) <= Integer.parseInt(num2.getText().toString())) {
                    c3 = num1.getText().toString();
                    c4 = num2.getText().toString();
                }

                if (Integer.parseInt(n1.getText().toString()) <= Integer.parseInt(n2.getText().toString())) {
                    status = n1.getText().toString() + " " + n2.getText().toString();
                }

                if (Integer.parseInt(n1.getText().toString()) > 62 || Integer.parseInt(n2.getText().toString()) > 62) {
                    status = (int) Math.min(Integer.parseInt(n1.getText().toString()), 62) + " " + (int) Math.min(Integer.parseInt(n2.getText().toString()), 62);
                }

                goodToGo = false;
                checkInfo();
                clearLists();
                requestDonation();
                readDonationReceptions();
                readPendingOffers();
                //DONE HAve to add 3rd function to read ListView

                Animation fadeOut = new AlphaAnimation(1, 0);
                fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
                fadeOut.setDuration(1100);
                update.startAnimation(fadeOut);
                update.setVisibility(View.INVISIBLE);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) box.getLayoutParams();
                        params.setMargins(3, -83, 3, 0);
                        box.setLayoutParams(params);
                    }
                }, 850);

            }
        });

        makeUpdateVisibile();

        super.onViewCreated(view, savedInstanceState);
    }

    private void checkFirstDonations() {
        try {
            InputStream inputStream = mContext.openFileInput("firstDonation.txt");
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                //DONE if firstTime.txt exists show app info.
                //OLD if firstTime.txt exists, find a way to show how to navigate the app.
                //DONE if firstTime.txt exists, then use TourGuide (check Voice Dialer App) to show how to navigate.
                //DONE Then, delete firstTime.txt

                //TODO Then in DonationsShelter and DonationsRestaurant, check if firstDonation.txt exists
                //TODO IF firstDonation.txt exists, then make an Alert Dialog explaining what this page is for and how to use it.
                //TODO Ex: on this page you can view donations that you have been offered as well as make requests and accept/decline them.
                //  You can also set your food status and how many people you will be serving.
                if (stringBuilder.toString().length() > 11) {
                    //INFO firstTime.txt exists which means it is their first time signing in.
                    showPageInfo();
                }

                inputStream.close();
            }
        } catch (Exception e) {
            //INFO file doesn't exist.
            //makeToast(e.toString);
        }
    }


    TourGuide mTutorialHandler;
    int clicks;
    private void showPageInfo() {
        mTutorialHandler = TourGuide.init((Activity)mContext).with(TourGuide.Technique.CLICK);
        mTutorialHandler.setToolTip(new ToolTip()
                .setTitle("")
                .setDescription("Specify when the food will be needed.")
                .setGravity(Gravity.BOTTOM)
                .setBackgroundColor(0xFFc98b51)
        );
        Overlay r = new Overlay();
        r.setBackgroundColor(0x99000000);
        r.disableClickThroughHole(false);
        r.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clicks == 0) {
                    mTutorialHandler.cleanUp();
                    mTutorialHandler.setToolTip(new ToolTip()
                            .setTitle("")
                            .setDescription("Specify how much food will be needed.")
                            .setGravity(Gravity.BOTTOM)
                            .setBackgroundColor(0xFFc98b51)
                    );
                    clicks++;
                    mTutorialHandler.playOn(l2);
                } else if (clicks == 1) {
                    mTutorialHandler.cleanUp();
                    mTutorialHandler.setToolTip(new ToolTip()
                            .setTitle("")
                            .setDescription("View completed donations or donations pending pickup.")
                            .setGravity(Gravity.BOTTOM)
                            .setBackgroundColor(0xFFc98b51)
                    );
                    clicks++;
                    mTutorialHandler.playOn(l3);
                } else if (clicks == 2) {
                    mTutorialHandler.cleanUp();
                    mTutorialHandler.setToolTip(new ToolTip()
                            .setTitle("")
                            .setDescription("View donations pending your acceptance.")
                            .setGravity(Gravity.TOP)
                            .setBackgroundColor(0xFFc98b51)
                    );
                    clicks++;
                    mTutorialHandler.playOn(l4);
                } else if (clicks == 3) {
                    mTutorialHandler.cleanUp();
                    mTutorialHandler.setToolTip(new ToolTip()
                            .setTitle("")
                            .setDescription("Request donations from restaurants.")
                            .setGravity(Gravity.TOP)
                            .setBackgroundColor(0xFFc98b51)
                    );
                    clicks++;
                    mTutorialHandler.playOn(l5);
                } else {
                    mTutorialHandler.cleanUp();
                    deleteFirstDonation();
                }
            }
        });
        mTutorialHandler.setOverlay(r);
        mTutorialHandler.playOn(l1);
    }

    private void deleteFirstDonation() {
        //DONE Uncomment below
        File dir = mContext.getFilesDir();
        File file = new File(dir, "firstDonation.txt");
        boolean deleted = file.delete();
    }

    public static void readPendingOffers() {
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Accept");
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String tempVC = child.getKey();
                    if (tempVC.equals(verification)) {
                        DatabaseReference dbref2 = FirebaseDatabase.getInstance().getReference("Accept/" + tempVC);
                        dbref2.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                ArrayList<DonationsList> al = new ArrayList();
                                ShelterApproval adapter;
                                ArrayList<Double> distances = new ArrayList<>();

                                for (DataSnapshot child : dataSnapshot.getChildren()) {
                                    String VC = child.child("Restaurant VC").getValue().toString();
                                    double latT = Double.parseDouble(child.child("Restaurant Latitude").getValue().toString());
                                    double lonT = Double.parseDouble(child.child("Restaurant Longitude").getValue().toString());
                                    double dis = goodDistance(latT, lonT);
                                    String name2 = child.child("Restaurant Name").getValue().toString();
                                    String addressMa = child.child("Restaurant Address").getValue().toString();
                                    String manager = child.child("Restaurant Manager").getValue().toString();
                                    String phone = child.child("Restaurant Phone").getValue().toString();
                                    String website = child.child("Restaurant Website").getValue().toString();
                                    String openingHours = child.child("Restaurant Hours").getValue().toString();
                                    String date = "";
                                    String time = "";
                                    String status = child.child("Status").getValue().toString();
                                    String message = "";
                                    String shelterStatus = child.child("Shelter Status").getValue().toString();
                                    String shelterAmount = child.child("Shelter Amount").getValue().toString();
                                    String messageInfo = child.child("Message InfoS").getValue().toString();
                                    String rDate = child.child("Date").getValue().toString();
                                    String rTime = child.child("Time").getValue().toString();
                                    date = child.child("Date").getValue().toString();
                                    time = child.child("Time").getValue().toString();
                                    message = child.child("MessageR").getValue().toString();

                                    distances.add(dis);
                                    al.add(new DonationsList(name2, manager, phone, addressMa, website, openingHours, dis + "", units,
                                            false, lat, lon, latT, lonT, verification, VC,
                                            address, name, phone2, website2, manager2, hours2, status, date, time, message, messageInfo
                                            , shelterStatus, shelterAmount, rDate, rTime));
                                }

                                if (al.size() != 0) {
                                    /*TreeMap<Double, DonationsList> tm = new TreeMap<>();

                                    for (int i = 0; i < distances.size(); i++) {
                                        tm.put(distances.get(i), al.get(i));
                                    }

                                    ArrayList<DonationsList> sorted = new ArrayList<>();
                                    for (Map.Entry<Double, DonationsList> entry : tm.entrySet()) {
                                        sorted.add(entry.getValue());
                                    }*/

                                    list2.setVisibility(View.VISIBLE);
                                    box2.setVisibility(View.INVISIBLE);
                                    adapter = new ShelterApproval(mContext, al);
                                    list2.setAdapter(adapter);
                                    nested2.requestLayout();

                                    if(nc2 != 0){
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            CharSequence name = mContext.getString(R.string.pend);
                                            String description = mContext.getString(R.string.pend);
                                            int importance = NotificationManager.IMPORTANCE_DEFAULT;
                                            NotificationChannel channel = new NotificationChannel("Pending Offers", name, importance);
                                            channel.setDescription(description);
                                            // Register the channel with the system; you can't change the importance
                                            // or other notification behaviors after this
                                            NotificationManager notificationManager = mContext.getSystemService(NotificationManager.class);
                                            notificationManager.createNotificationChannel(channel);
                                        }
                                        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, "Pending Offers")
                                                .setContentTitle("New Offer!")
                                                .setContentText("You have a new food donation offer!")
                                                .setSmallIcon(R.drawable.infoimage)
                                                .setStyle(new NotificationCompat.BigTextStyle()
                                                        .bigText("Tap on the notification to check it out!"))
                                                .setPriority(NotificationCompat.PRIORITY_MAX);
                                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);

// notificationId is a unique int for each notification that you must define
                                        notificationManager.notify(1, builder.build());
                                    }else{
                                        nc2++;
                                    }

                                    if (al.size() == 1) {
                                        nested2.getLayoutParams().height = 835;
                                        list2.getLayoutParams().height = 980;
                                    } else {
                                        nested2.getLayoutParams().height = 1100;
                                        list2.getLayoutParams().height = 1180;
                                    }
                                } else {
                                    nested2.getLayoutParams().height = 350;
                                    list2.getLayoutParams().height = 350;
                                    box2.setVisibility(View.VISIBLE);
                                    list2.setVisibility(View.INVISIBLE);

                                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) box2.getLayoutParams();
                                    params.height = 350;
                                    box2.setLayoutParams(params);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void readDonationReceptions() {
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Donations");
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String tempVC = child.getKey();
                    if (tempVC.equals(verification)) {
                        DatabaseReference dbref2 = FirebaseDatabase.getInstance().getReference("Donations/" + tempVC);
                        dbref2.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                ArrayList<DonationsList> al = new ArrayList();
                                DonationsListAdapter adapter;
                                //TODO You will also eventually have to check if other values are not null and then pass those in too
                                //  Like the Date Collected or Accepted/Approved.
                                for (DataSnapshot child : dataSnapshot.getChildren()) {
                                    String VC = child.child("Restaurant VC").getValue().toString();
                                    double latT = Double.parseDouble(child.child("Restaurant Latitude").getValue().toString());
                                    double lonT = Double.parseDouble(child.child("Restaurant Longitude").getValue().toString());
                                    double dis = goodDistance(latT, lonT);
                                    String name2 = child.child("Restaurant Name").getValue().toString();
                                    String addressMa = child.child("Restaurant Address").getValue().toString();
                                    String manager = child.child("Restaurant Manager").getValue().toString();
                                    String phone = child.child("Restaurant Phone").getValue().toString();
                                    String website = child.child("Restaurant Website").getValue().toString();
                                    String openingHours = child.child("Restaurant Hours").getValue().toString();
                                    String date = "";
                                    String time = "";
                                    String status = child.child("Status").getValue().toString();
                                    String message = "";
                                    String shelterStatus = child.child("Shelter Status").getValue().toString();
                                    String shelterAmount = child.child("Shelter Amount").getValue().toString();
                                    String messageInfo = child.child("Message InfoS").getValue().toString();
                                    String rDate = child.child("Date").getValue().toString();
                                    String rTime = child.child("Time").getValue().toString();
                                    if (status.contains("Approval")) {
                                        date = child.child("Date").getValue().toString();
                                        time = child.child("Time").getValue().toString();
                                    } else if (status.contains("Pickup")) {
                                        date = child.child("Pickup Date").getValue().toString();
                                        time = child.child("Pickup Time").getValue().toString();
                                    } else if (status.contains("Cancelled")) {
                                        date = child.child("Cancelled Date").getValue().toString();
                                        time = child.child("Cancelled Time").getValue().toString();
                                    } else if (status.contains("Declined")) {
                                        date = child.child("Decline Date").getValue().toString();
                                        time = child.child("Decline Time").getValue().toString();
                                    } else {
                                        date = child.child("Done Date").getValue().toString();
                                        time = child.child("Done Time").getValue().toString();
                                    }
                                    message = child.child("MessageR").getValue().toString();

                                    al.add(new DonationsList(name2, manager, phone, addressMa, website, openingHours, dis + "", units,
                                            false, lat, lon, latT, lonT, verification, VC,
                                            address, name, phone2, website2, manager2, hours2, status, date, time, message, messageInfo
                                            , shelterStatus, shelterAmount, rDate, rTime));
                                }
                                //DONE Have to reorder not based on distance, but instead the status, then the time.
                                //  I think time will happen automatically in right order. May have to reverse it though.
                                if (al.size() != 0) {
                                    ArrayList<DonationsList> declined = new ArrayList<>();
                                    ArrayList<DonationsList> cancelled = new ArrayList<>();
                                    ArrayList<DonationsList> finished = new ArrayList<>();
                                    ArrayList<DonationsList> pickup = new ArrayList<>();
                                    ArrayList<DonationsList> pending = new ArrayList<>();
                                    for (DonationsList dl : al) {
                                        String status = dl.getStatus();
                                        if (status.contains("Approval")) {
                                            pending.add(dl);
                                        } else if (status.contains("Pickup")) {
                                            pickup.add(dl);
                                        } else if (status.contains("Cancelled")) {
                                            cancelled.add(dl);
                                        } else if (status.contains("Declined")) {
                                            declined.add(dl);
                                        } else
                                            finished.add(dl);
                                    }

                                    ArrayList<DonationsList> rightOrder = new ArrayList<>();
                                    rightOrder.addAll(pending);
                                    rightOrder.addAll(pickup);
                                    rightOrder.addAll(finished);
                                    rightOrder.addAll(cancelled);
                                    rightOrder.addAll(declined);

                                    list1.setVisibility(View.VISIBLE);
                                    box1.setVisibility(View.INVISIBLE);
                                    adapter = new DonationsListAdapter(mContext, rightOrder);
                                    list1.setAdapter(adapter);
                                    nested1.requestLayout();
                                    statusT.setVisibility(View.VISIBLE);

                                    if(nc1 != 0){
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            CharSequence name = mContext.getString(R.string.pend);
                                            String description = mContext.getString(R.string.pend);
                                            int importance = NotificationManager.IMPORTANCE_DEFAULT;
                                            NotificationChannel channel = new NotificationChannel("Donation Reception", name, importance);
                                            channel.setDescription(description);
                                            // Register the channel with the system; you can't change the importance
                                            // or other notification behaviors after this
                                            NotificationManager notificationManager = mContext.getSystemService(NotificationManager.class);
                                            notificationManager.createNotificationChannel(channel);
                                        }
                                        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, "Donation Reception")
                                                .setContentTitle("New Donation!")
                                                .setContentText("You have a new food donation!")
                                                .setSmallIcon(R.drawable.infoimage)
                                                .setStyle(new NotificationCompat.BigTextStyle()
                                                        .bigText("Tap on the notification to check it out!"))
                                                .setPriority(NotificationCompat.PRIORITY_MAX);
                                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);

// notificationId is a unique int for each notification that you must define
                                        notificationManager.notify(2, builder.build());
                                    }else{
                                        nc1++;
                                    }

                                    //DONE Based on size of ListView, set height
                                    //  If size == 1 ----> height = 700 or something
                                    //  If size > 1  ----> height = 1100 or something.
                                    if (al.size() == 1) {
                                        nested1.getLayoutParams().height = 900;
                                        list1.getLayoutParams().height = 950;
                                    } else {
                                        nested1.getLayoutParams().height = 920;
                                        list1.getLayoutParams().height = 1090;
                                    }
                                } else {
                                    nested1.getLayoutParams().height = 400;
                                    list1.getLayoutParams().height = 400;
                                    box1.setVisibility(View.VISIBLE);
                                    list1.setVisibility(View.INVISIBLE);
                                    statusT.setVisibility(View.INVISIBLE);

                                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) box1.getLayoutParams();
                                    params.height = 350;
                                    box1.setLayoutParams(params);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    static String address, name, phone2, website2, manager2, hours2;
    static double distance2;

    public static void readInfo() {
        try {
            InputStream inputStream = mContext.openFileInput("info.txt");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                //status = bufferedReader.readLine();

                int count = 0;
                String s;
                while ((s = bufferedReader.readLine()) != null) {
                    if (count == 1)
                        address = s;
                    if (count == 3)
                        lat = Double.parseDouble(s);
                    else if (count == 4)
                        lon = Double.parseDouble(s);
                    else if (count == 5)
                        units = s;
                    else if (count == 2)
                        distance2 = Double.parseDouble(s);
                    else if (count == 12)
                        name = s;
                    else if (count == 13)
                        phone2 = s;
                    else if (count == 14)
                        website2 = s;
                    else if (count == 15)
                        manager2 = s;
                    else if (count == 16)
                        hours2 = s;
                    count++;
                }

            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onDestroy() {
        firstTime = 0;
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
        fadeOut.setDuration(1100);
        update.startAnimation(fadeOut);
        update.setVisibility(View.INVISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) box.getLayoutParams();
                params.setMargins(3, -77, 3, 0);
                box.setLayoutParams(params);
            }
        }, 850);
        super.onDestroy();
    }

    private void clearLists() {
        if (list1 != null)
            list1.setAdapter(null);
        if (list2 != null)
            list2.setAdapter(null);
        if (list3 != null)
            list3.setAdapter(null);
    }

    public static void requestDonation() {
        //DONE Read all restaurants
        //INFO Only care about Rating for making requests/offers.
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Places");
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<RequestDonation> al = new ArrayList();
                RequestDonationAdapter adapter;
                ArrayList<Double> distances = new ArrayList<>();

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String type = child.child("Type").getValue().toString();
                    if (type.contains("est")) {
                        boolean discoverable = Boolean.parseBoolean(child.child("Discoverable to Shelter").getValue().toString());
                        if (discoverable) {
                            double latT = Double.parseDouble(child.child("Latitude").getValue().toString());
                            double lonT = Double.parseDouble(child.child("Longitude").getValue().toString());
                            double dis = goodDistance(latT, lonT);
                            if (dis <= distance2) {
                                String name2 = child.child("Name").getValue().toString();
                                String addressMa = child.child("Address").getValue().toString();
                                String manager = child.child("Manager Name").getValue().toString();
                                String phone = child.child("Phone").getValue().toString();
                                String website = child.child("Website").getValue().toString();
                                String openingHours = child.child("Hours").getValue().toString();
                                double lat2 = Double.parseDouble(child.child("Latitude").getValue().toString());
                                double lon2 = Double.parseDouble(child.child("Longitude").getValue().toString());
                                String number = child.child("Num of Donations").getValue().toString();
                                String rating = child.child("Rating").getValue().toString();
                                String VC = child.child("Verification Code").getValue().toString();
                                //check = child.child("RNum").getValue().toString();
                                boolean good = true;
                                if (child.child("RNum") != null) {
                                    String[] verifications = child.child("RNum").getValue().toString().split("h");
                                    for (String VCs : verifications) {
                                        if (VCs.equals(verification)) {
                                            good = false;
                                            break;
                                        }
                                    }
                                }
                                if (good) {
                                    distances.add(dis);
                                    al.add(new RequestDonation(name2, manager, phone, addressMa, website, openingHours, dis + "", units,
                                            false, lat, lon, lat2, lon2, rating, number, verification, VC,
                                            address, name, phone2, website2, manager2, hours2, status, c3 + " " + c4));
                                }
                            }
                        }
                    }
                }

                if (al.size() != 0) {
                    TreeMap<Double, RequestDonation> tm = new TreeMap<>();

                    for (int i = 0; i < distances.size(); i++) {
                        tm.put(distances.get(i), al.get(i));
                    }

                    ArrayList<RequestDonation> sorted = new ArrayList<>();
                    for (Map.Entry<Double, RequestDonation> entry : tm.entrySet()) {
                        sorted.add(entry.getValue());
                    }

                    list3.setVisibility(View.VISIBLE);
                    box3.setVisibility(View.INVISIBLE);
                    adapter = new RequestDonationAdapter(mContext, sorted);
                    list3.setAdapter(adapter);
                    nested3.requestLayout();
                    nested3.getLayoutParams().height = 930;
                    list3.getLayoutParams().height = 980;
                } else {
                    nested3.getLayoutParams().height = 350;
                    list3.getLayoutParams().height = 350;
                    box3.setVisibility(View.VISIBLE);
                    list3.setVisibility(View.INVISIBLE);

                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) box3.getLayoutParams();
                    params.height = 350;
                    box3.setLayoutParams(params);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public static double goodDistance(double latT, double lonT) {
        //result += "  " + Math.sqrt(Math.pow(latT - lat, 2) + Math.pow(lonT - lon, 2));
        return Math.sqrt(Math.pow(latT - lat, 2) + Math.pow(lonT - lon, 2));
    }

    Snackbar snackbar;

    private void checkInfo() {
        String s1 = "", s2 = "";
        s1 = num1.getText().toString();
        s2 = num2.getText().toString();
        String d1 = "", d2 = "";
        d1 = n1.getText().toString();
        d2 = n2.getText().toString();

        if (snackbar != null)
            snackbar.dismiss();
        if (!s1.equals("") && !s2.equals("") && !d1.equals("") && !d2.equals("")) {

            if (Integer.parseInt(d1) > Integer.parseInt(d2)) {
                snackbar = Snackbar
                        .make(container, "The lower bound must be smaller than the upper bound for the number of days.", Snackbar.LENGTH_INDEFINITE)
                        .setDuration(5000)
                        .setActionTextColor(Color.parseColor("#F7E9CE"));
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
            } else if (Integer.parseInt(d2) > 62) {
                snackbar = Snackbar
                        .make(container, "You cannot request food in advance over 2 months (62 days).", Snackbar.LENGTH_INDEFINITE)
                        .setDuration(5000)
                        .setActionTextColor(Color.parseColor("#F7E9CE"));
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
            } else if (Integer.parseInt(d2) - Integer.parseInt(d1) > 7) {
                snackbar = Snackbar
                        .make(container, "The range for the number of days is too unspecific. Please choose a range within a week.", Snackbar.LENGTH_INDEFINITE)
                        .setDuration(9000)
                        .setActionTextColor(Color.parseColor("#F7E9CE"));
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
            } else if (Integer.parseInt(s1) > Integer.parseInt(s2)) {
                snackbar = Snackbar
                        .make(container, "The lower bound must be smaller than the upper bound for the number of people.", Snackbar.LENGTH_INDEFINITE)
                        .setDuration(5000)
                        .setActionTextColor(Color.parseColor("#F7E9CE"));
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
            } else if (Integer.parseInt(s2) - Integer.parseInt(s1) > (Integer.parseInt(s2) / 2) && Integer.parseInt(s2) > 9) {
                snackbar = Snackbar
                        .make(container, "The range for the number of people is too large. It should be less than half of the maximum. Please be more specific.", Snackbar.LENGTH_INDEFINITE)
                        .setDuration(9000)
                        .setActionTextColor(Color.parseColor("#F7E9CE"));
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
            } else if (Integer.parseInt(s2) - Integer.parseInt(s1) > 100 && Integer.parseInt(s2) > 360) {
                snackbar = Snackbar
                        .make(container, "The range for the number of people is too large. It should be less than 100 for such large amounts. Please be more specific.", Snackbar.LENGTH_INDEFINITE)
                        .setDuration(9000)
                        .setActionTextColor(Color.parseColor("#F7E9CE"));
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
            } else {
                goodToGo = true;
                saveStatusinFirebase(s1, s2, d1, d2);
                savefoodStatus(s1, s2, d1, d2);
            }
        } else {
            snackbar = Snackbar
                    .make(container, "One of the fields is empty. Please enter the information.", Snackbar.LENGTH_INDEFINITE)
                    .setDuration(3000)
                    .setActionTextColor(Color.parseColor("#F7E9CE"));
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
    }

    private void makeUpdateVisibile() {
        if (firstTime != 0) {
            TextWatcher tt = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (update.getVisibility() == View.INVISIBLE && !s.toString().equals(c1) && !s.toString().equals(c2) && !s.toString().equals(c3) && !s.toString().equals(c4)) {
                        Animation fadeIn = new AlphaAnimation(0, 1);
                        fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
                        fadeIn.setDuration(1500);
                        update.setAnimation(fadeIn);
                        update.setVisibility(View.VISIBLE);
                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) box.getLayoutParams();
                        params.setMargins(3, 20, 3, 0);
                        box.setLayoutParams(params);
                    }
                }
            };
            num1.addTextChangedListener(tt);
            num2.addTextChangedListener(tt);
            n1.addTextChangedListener(tt);
            n2.addTextChangedListener(tt);
        }
    }

    private void saveStatusinFirebase(final String n1, final String n2, final String d1, final String d2) {
        //TODO Save the status in Firebase.
        Settings.makeSavingBrown(mContext);
        final DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Places");
        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String val = child.child("Verification Code").getValue().toString();
                    if (val.equals(verification)) {
                        if (Integer.parseInt(n1) == 0 && Integer.parseInt(n2) == 0)
                            dbref.child(child.getKey()).child("Amount").setValue("None");
                        else if (Integer.parseInt(n1) == Integer.parseInt(n2))
                            dbref.child(child.getKey()).child("Amount").setValue(n1 + " people");
                        else
                            dbref.child(child.getKey()).child("Amount").setValue(n1 + " to " + n2 + " people");

                        OnSuccessListener<Void> onSuccessListener = new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Settings.dismissSavingBrown();
                                snackbar = Snackbar
                                        .make(container, "Saved your info!", Snackbar.LENGTH_INDEFINITE)
                                        .setDuration(1500);
                                TextView snackTextView = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
                                snackTextView.setMaxLines(5);
                                snackTextView.setTextColor(Color.parseColor("#F7E9CE"));

                                snackbar.show();
                            }
                        };

                        if (Integer.parseInt(d1) == 0 && Integer.parseInt(d2) == 0)
                            dbref.child(child.getKey()).child("Food Status").setValue("Not Needed").addOnSuccessListener(onSuccessListener);
                        else if (Integer.parseInt(d1) == Integer.parseInt(d2))
                            dbref.child(child.getKey()).child("Food Status").setValue(d1 + " days").addOnSuccessListener(onSuccessListener);
                        else
                            dbref.child(child.getKey()).child("Food Status").setValue(d1 + " to " + d2 + " days").addOnSuccessListener(onSuccessListener);

                        /*dbref.child(child.getKey()).child("Food Status").setValue(status).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Settings.dismissSavingBrown();
                                snackbar = Snackbar
                                        .make(container, "Saved your info!", Snackbar.LENGTH_INDEFINITE)
                                        .setDuration(1500);
                                TextView snackTextView = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
                                snackTextView.setMaxLines(5);
                                snackTextView.setTextColor(Color.parseColor("#F7E9CE"));

                                snackbar.show();
                            }
                        });*/
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void savefoodStatus(String s1, String s2, String day1, String day2) {
        OutputStreamWriter outputStreamWriter = null;
        try {
            outputStreamWriter = new OutputStreamWriter(mContext.openFileOutput("foodStatus.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(day1 + " " + day2 + "\n" + s1 + " " + s2 + "\n" + verification);
            outputStreamWriter.close();
        } catch (Exception e) {
            makeToast("ERROR: " + e.toString() + "\nPlease try again.");
        }
    }


    @Override
    public void onDestroyView() {
        mAllowSelectionFiring = false;
        super.onDestroyView();
    }

    private void readStatus() {
        try {
            InputStream inputStream = mContext.openFileInput("foodStatus.txt");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                status = bufferedReader.readLine();
                c1 = status.split(" ")[0];
                c2 = status.split(" ")[1];
                n1.setText(c1);
                n2.setText(c2);
                /*if (status.contains("ine"))
                    statusS.setSelection(0);
                else if (status.contains("utu"))
                    statusS.setSelection(1);
                else if (status.contains("oon"))
                    statusS.setSelection(2);
                else if (status.contains("ent"))
                    statusS.setSelection(3);
*/
                numLine = bufferedReader.readLine();
                verification = bufferedReader.readLine();

                c3 = numLine.split(" ")[0];
                c4 = numLine.split(" ")[1];
                num1.setText(c3);
                num2.setText(c4);
                firstTime++;
            }
        } catch (Exception e) {
            //makeToast("ERROR. Please log out and then sign in again.\n" + e.toString());
            /*final Snackbar snackbar = Snackbar
                    .make(container, "Please log out and then sign in again with a stable WiFi connection.\nIf the error persists contact ijapps101@gmail.com", Snackbar.LENGTH_INDEFINITE)
                    .setDuration(10000)
                    .setActionTextColor(Color.parseColor("#F7E9CE"));
            snackbar.setAction("DISMISS", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Call your action method here
                    snackbar.dismiss();
                }
            });
            TextView snackTextView = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
            snackTextView.setMaxLines(5);

            snackbar.show();*/
        }


    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //TODO Have to check for id to see which spinner it is.
        if (mAllowSelectionFiring) {
            if (isAdded() && isVisible() && getUserVisibleHint() && tries != 0) {
                if (view != null && statusS != null) {
                    update.setVisibility(View.VISIBLE);
                    int selectedItemPosition = Integer.parseInt(view.getTag(R.string.meta_position).toString().trim());
                    String selectedItemTitle = view.getTag(R.string.meta_title).toString().trim();
                    status = selectedItemTitle;
                    if (position == 0) {
                        Toast.makeText(mContext, "You have specified that you are not currently in need of food.", Toast.LENGTH_LONG).show();
                    } else if (position == 1) {
                        Toast.makeText(mContext, "You have specified that you will need donations in the future.", Toast.LENGTH_LONG).show();
                    } else if (position == 2) {
                        Toast.makeText(mContext, "You have specified that you will need donations soon.", Toast.LENGTH_LONG).show();
                    } else if (position == 3) {
                        Toast.makeText(mContext, "You have specified that you need donations urgently.", Toast.LENGTH_LONG).show();
                    }

                }
            } else {
                tries++;
            }
        } else {
        }
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private void helpClicker() {
        help.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                makeToast("This helps restaurants know when you will need the food.");
                //removeRNum("854037", "349587");
                return false;
            }
        });
        help2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                makeToast("This helps restaurants know how much food you will need.");
                return false;
            }
        });
    }

    private void removeRNum(String VC1, String VC2) {
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Places");
        clearRNum(dbref, VC1, VC2);
        clearRNum(dbref, VC2, VC1);
    }

    private void clearRNum(final DatabaseReference dbref, final String VC, final String deleteVC) {
        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String temp = child.child("Verification Code").getValue().toString();
                    if (temp.equals(VC)) {
                        if (child.child("RNum").getValue().toString() != null) {
                            String RNum = child.child("RNum").getValue().toString();
                            String[] splitter = RNum.split("h");
                            String result = "hi";
                            int count = 0;
                            for (String v : splitter) {
                                if (!v.equals(deleteVC)) {
                                    if (count == 0)
                                        result = v;
                                    else
                                        result += "h" + v;
                                    count++;
                                }
                            }
                            dbref.child(child.getKey()).child("RNum").setValue(result);
                        }
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                makeToast("Error. Please have a good WiFi connection!");
            }
        });
    }

    private void makeToast(String s) {
        Toast.makeText(mContext, s, Toast.LENGTH_LONG).show();
    }

}

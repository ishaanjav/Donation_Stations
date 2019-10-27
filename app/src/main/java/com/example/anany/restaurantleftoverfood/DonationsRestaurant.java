package com.example.anany.restaurantleftoverfood;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anany.restaurantleftoverfood.Adapters.DonationsListAdapter;
import com.example.anany.restaurantleftoverfood.Adapters.OfferDonationAdapter;
import com.example.anany.restaurantleftoverfood.Adapters.RDonationsListAdapter;
import com.example.anany.restaurantleftoverfood.Adapters.RequestDonationAdapter;
import com.example.anany.restaurantleftoverfood.Adapters.RestaurantApproval;
import com.example.anany.restaurantleftoverfood.Storage.DonationsList;
import com.example.anany.restaurantleftoverfood.Storage.RequestDonation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class DonationsRestaurant extends Fragment {

    static Context mContext;

    public static Fragment newInstance(String param1, String param2, Context context) {
        DonationsRestaurant fragment = new DonationsRestaurant();
        mContext = context;

        return fragment;
    }

    static String verificationCode;
    static int donationsNum;
    int donationPoints;

    CoordinatorLayout container;

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
    }

    //DONE In the "Your Donations" box, show the status as well: like Pending Confirmation. Received
    //DONE Based on whether the donation was received or pending change the background.
    //DONE If not using the rating of shelters, then remove it so that it is only for shelters in both InfoPages and AccountInfo

    ImageView help;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.donations_restaurant, container, false);
    }

    static TextView points, donations;
    static ListView list1, list2, list3;
    static RelativeLayout nested1, nested2, nested3;
    static RelativeLayout box1, box2, box3;
    static TextView statusT;

    static int nc1 = 0, nc2 = 0;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mContext = getActivity();
        points = getView().findViewById(R.id.points);
        help = getView().findViewById(R.id.help2);
        donations = getView().findViewById(R.id.donations);
        container = getView().findViewById(R.id.container);
        nested1 = getView().findViewById(R.id.bigBox);
        nested2 = getView().findViewById(R.id.bigBox2);
        nested3 = getView().findViewById(R.id.bigBox3);
        list3 = getView().findViewById(R.id.donorsList3);
        list2 = getView().findViewById(R.id.donorsList2);
        list1 = getView().findViewById(R.id.donorsList);
        box3 = getView().findViewById(R.id.box3);
        box1 = getView().findViewById(R.id.box1);
        box2 = getView().findViewById(R.id.box2);
        statusT = getView().findViewById(R.id.statusT);

        helpClick();
        readInfo();
        readInfo2();

        clearLists();
        offerDonation();
        readDonationReceptions();
        changeDonationInfo();
        readPendingOffers();

        super.onViewCreated(view, savedInstanceState);
    }

    public static void readPendingOffers() {
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Accept");
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String tempVC = child.getKey();
                    if (tempVC.equals(verificationCode)) {
                        DatabaseReference dbref2 = FirebaseDatabase.getInstance().getReference("Accept/" + tempVC);
                        dbref2.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                ArrayList<DonationsList> al = new ArrayList();
                                RestaurantApproval adapter;

                                for (DataSnapshot child : dataSnapshot.getChildren()) {
                                    String VC = child.child("Shelter VC").getValue().toString();
                                    double latT = Double.parseDouble(child.child("Shelter Latitude").getValue().toString());
                                    double lonT = Double.parseDouble(child.child("Shelter Longitude").getValue().toString());
                                    double dis = goodDistance(latT, lonT);
                                    String name2 = child.child("Shelter Name").getValue().toString();
                                    String addressMa = child.child("Shelter Address").getValue().toString();
                                    String manager = child.child("Shelter Manager").getValue().toString();
                                    String phone = child.child("Shelter Phone").getValue().toString();
                                    String website = child.child("Shelter Website").getValue().toString();
                                    String openingHours = child.child("Shelter Hours").getValue().toString();
                                    String date = "";
                                    String time = "";
                                    String status = child.child("Status").getValue().toString();
                                    String message = "";
                                    String shelterStatus = child.child("Shelter Status").getValue().toString();
                                    String shelterAmount = child.child("Shelter Amount").getValue().toString();
                                    String messageInfo = child.child("Message InfoS").getValue().toString();
                                    //DONE Have to change rDate and rTime to get the request date and time properly.
                                    String rDate = child.child("Date").getValue().toString();
                                    String rTime = child.child("Time").getValue().toString();
                                    date = child.child("Date").getValue().toString();
                                    time = child.child("Time").getValue().toString();
                                    message = child.child("MessageS").getValue().toString();

                                    al.add(new DonationsList(name2, manager, phone, addressMa, website, openingHours, dis + "", units,
                                            false, lat, lon, latT, lonT, VC, verificationCode,
                                            address, name, phone2, website2, manager2, hours2, status, date, time, message, messageInfo
                                            , shelterStatus, shelterAmount, rDate, rTime));
                                }

                                if (al.size() != 0) {
                                    list2.setVisibility(View.VISIBLE);
                                    box2.setVisibility(View.INVISIBLE);
                                    adapter = new RestaurantApproval(mContext, al);
                                    list2.setAdapter(adapter);
                                    nested2.requestLayout();

                                    if(nc2 != 0){
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            CharSequence name = mContext.getString(R.string.pend);
                                            String description = mContext.getString(R.string.pend);
                                            int importance = NotificationManager.IMPORTANCE_DEFAULT;
                                            NotificationChannel channel = new NotificationChannel("Donation Request", name, importance);
                                            channel.setDescription(description);
                                            // Register the channel with the system; you can't change the importance
                                            // or other notification behaviors after this
                                            NotificationManager notificationManager = mContext.getSystemService(NotificationManager.class);
                                            notificationManager.createNotificationChannel(channel);
                                        }
                                        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, "Donation Request")
                                                .setContentTitle("New Donation Request!")
                                                .setContentText("You have a new food donation request!")
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
                                        nested2.getLayoutParams().height = 900;
                                        list2.getLayoutParams().height = 1060;
                                    } else {
                                        nested2.getLayoutParams().height = 1090;
                                        list2.getLayoutParams().height = 1170;
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

    static String type;

    public static void changeDonationInfo(){
        if(type.contains("est")) {
            final DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Places");
            dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        String tempVC = child.child("Verification Code").getValue().toString();
                        if (tempVC.equals(verificationCode)) {
                            String path = child.getKey();
                            DatabaseReference checker = FirebaseDatabase.getInstance().getReference("Places/" + path);
                            checker.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    String donationNumber = dataSnapshot.child("Num of Donations").getValue().toString();
                                    String donationPoints = dataSnapshot.child("Donation Points").getValue().toString();

                                    donations.setText(donationNumber);
                                    points.setText(donationPoints);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }


                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    public static void readDonationReceptions() {
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Donations");
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String tempVC = child.getKey();
                    if (tempVC.equals(verificationCode)) {
                        DatabaseReference dbref2 = FirebaseDatabase.getInstance().getReference("Donations/" + tempVC);
                        dbref2.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                ArrayList<DonationsList> al = new ArrayList();
                                RDonationsListAdapter adapter;
                                //DONE You will also eventually have to check if other values are not null and then pass those in too
                                //  Like the Date Collected or Accepted/Approved.
                                for (DataSnapshot child : dataSnapshot.getChildren()) {
                                    String VC = child.child("Shelter VC").getValue().toString();
                                    double latT = Double.parseDouble(child.child("Shelter Latitude").getValue().toString());
                                    double lonT = Double.parseDouble(child.child("Shelter Longitude").getValue().toString());
                                    double dis = goodDistance(latT, lonT);
                                    String name2 = child.child("Shelter Name").getValue().toString();
                                    String addressMa = child.child("Shelter Address").getValue().toString();
                                    String manager = child.child("Shelter Manager").getValue().toString();
                                    String phone = child.child("Shelter Phone").getValue().toString();
                                    String website = child.child("Shelter Website").getValue().toString();
                                    String openingHours = child.child("Shelter Hours").getValue().toString();
                                    String date = "";
                                    String time = "";
                                    String status = child.child("Status").getValue().toString();
                                    String message = "";
                                    String messageInfo = child.child("Message InfoR").getValue().toString();
                                    String shelterStatus = child.child("Shelter Status").getValue().toString();
                                    String shelterAmount = child.child("Shelter Amount").getValue().toString();
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
                                    message = child.child("MessageS").getValue().toString();

                                    al.add(new DonationsList(name2, manager, phone, addressMa, website, openingHours, dis + "", units,
                                            false, lat, lon, latT, lonT, verificationCode, VC,
                                            address, name, phone2, website2, manager2, hours2, status, date, time, message, messageInfo
                                            , shelterStatus, shelterAmount, rTime, rDate));
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
                                    adapter = new RDonationsListAdapter(mContext, rightOrder);
                                    list1.setAdapter(adapter);
                                    nested1.requestLayout();
                                    statusT.setVisibility(View.VISIBLE);

                                    if(nc1 != 0){
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            CharSequence name = mContext.getString(R.string.pend);
                                            String description = mContext.getString(R.string.pend);
                                            int importance = NotificationManager.IMPORTANCE_DEFAULT;
                                            NotificationChannel channel = new NotificationChannel("Donation Accepted", name, importance);
                                            channel.setDescription(description);
                                            // Register the channel with the system; you can't change the importance
                                            // or other notification behaviors after this
                                            NotificationManager notificationManager = mContext.getSystemService(NotificationManager.class);
                                            notificationManager.createNotificationChannel(channel);
                                        }
                                        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, "Donation Accepted")
                                                .setContentTitle("Donation Accepted!")
                                                .setContentText("A shelter is waiting for a donation!")
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
                                        nested1.getLayoutParams().height = 735;
                                        list1.getLayoutParams().height = 812;
                                    } else {
                                        nested1.getLayoutParams().height = 920;
                                        list1.getLayoutParams().height = 1000;
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

    public static void offerDonation() {
        //DONE Read all restaurants
        //INFO Only care about Rating for making requests/offers.
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Places");
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<RequestDonation> al = new ArrayList();
                OfferDonationAdapter adapter;
                ArrayList<Double> distances = new ArrayList<>();

                //String result2 = "";
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String type = child.child("Type").getValue().toString();
                    String name2 = child.child("Name").getValue().toString();
                    //result2+= type + ": " + name2 + "\n";
                    if (type.contains("elt")) {
                        boolean discoverable = Boolean.parseBoolean(child.child("Discoverable to Restaurant").getValue().toString());
                        if (discoverable) {
                            double latT = Double.parseDouble(child.child("Latitude").getValue().toString());
                            double lonT = Double.parseDouble(child.child("Longitude").getValue().toString());
                            double dis = goodDistance(latT, lonT);
                            if (dis <= distance2) {
                                String addressMa = child.child("Address").getValue().toString();
                                String manager = child.child("Manager Name").getValue().toString();
                                String phone = child.child("Phone").getValue().toString();
                                String website = child.child("Website").getValue().toString();
                                String openingHours = child.child("Hours").getValue().toString();
                                double lat2 = Double.parseDouble(child.child("Latitude").getValue().toString());
                                double lon2 = Double.parseDouble(child.child("Longitude").getValue().toString());
                                String status = child.child("Food Status").getValue().toString();
                                String amount = child.child("Amount").getValue().toString();
                                String VC = child.child("Verification Code").getValue().toString();
                                //check = child.child("RNum").getValue().toString();
                                boolean good = true;
                                if (child.child("RNum") != null) {
                                    String[] verifications = child.child("RNum").getValue().toString().split("h");
                                    for (String VCs : verifications) {
                                        if (VCs.equals(verificationCode)) {
                                            good = false;
                                            break;
                                        }
                                    }
                                }
                                if (good) {
                                    distances.add(dis);
                                    //Settings.makeTesting(mContext, verificationCode +" " + VC);
                                    al.add(new RequestDonation(name2, manager, phone, addressMa, website, openingHours, dis + "", units,
                                            false, lat, lon, lat2, lon2, "3", donationsNum + "", verificationCode, VC,
                                            address, name, phone2, website2, manager2, hours2, status, amount));
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

                    adapter = new OfferDonationAdapter(mContext, sorted);
                    //list3.setAdapter(adapter);
                    list3.setAdapter(adapter);
                    nested3.requestLayout();
                    nested3.getLayoutParams().height = 905;
                    list3.getLayoutParams().height = 985;
                } else {
                    //makeToast("Size is 0!");
                    nested3.getLayoutParams().height = 350;
                    list3.getLayoutParams().height = 430;
                    box3.setVisibility(View.VISIBLE);
                    box3.getLayoutParams().height = 350;
                    list3.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public void readInfo() {
        //DONE Set Liseteners on the restaurants donations points and numbers. When it changes, write to donations.txt
        try {
            InputStream inputStream = mContext.openFileInput("donations.txt");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String s = bufferedReader.readLine();
                donationsNum = Integer.parseInt(s.split(" ")[0]);
                donationPoints = Integer.parseInt(s.split(" ")[1]);
                donations.setText(donationsNum);
                points.setText(donationPoints);
                verificationCode = bufferedReader.readLine();
                //makeToast("VC: " + verificationCode);
                Settings.makeTesting(mContext, verificationCode);
            }
        } catch (Exception e) {
            /*Snackbar snackbar = new Snackbar();
            textView*/
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

            snackbar.show();
 */
        }
    }

    static String address, name, phone2, website2, manager2, hours2, units;
    static double lat, lon;
    static double distance2;

    public static void readInfo2() {
        try {
            InputStream inputStream = mContext.openFileInput("info.txt");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                //status = bufferedReader.readLine();

                int count = 0;
                String s;
                while ((s = bufferedReader.readLine()) != null) {
                    if(count == 0 )
                        type = s;
                    else if (count == 1)
                        address = s;
                    if (count == 3)
                        lat = Double.parseDouble(s);
                    else if (count == 4)
                        lon = Double.parseDouble(s);
                    else if (count == 5)
                        units = s;
                    else if (count == 2)
                        distance2 = Double.parseDouble(s);
                    else if (count == 11)
                        verificationCode = s;
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

    public static double goodDistance(double latT, double lonT) {
        //result += "  " + Math.sqrt(Math.pow(latT - lat, 2) + Math.pow(lonT - lon, 2));
        return Math.sqrt(Math.pow(latT - lat, 2) + Math.pow(lonT - lon, 2));
    }

    private void clearLists() {
        if (list1 != null)
            list1.setAdapter(null);
        if (list2 != null)
            list2.setAdapter(null);
        if (list3 != null)
            list3.setAdapter(null);
    }

    private void helpClick() {
        help.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                makeToast("It's easy to earn points! Just offer donations to shelters and you'll earn points based on their food status at the time!");
                return false;
            }
        });
    }

    private void makeToast(String s) {
        Toast.makeText(mContext, s, Toast.LENGTH_LONG).show();
    }
}

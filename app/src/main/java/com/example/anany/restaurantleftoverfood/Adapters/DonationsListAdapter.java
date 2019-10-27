package com.example.anany.restaurantleftoverfood.Adapters;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anany.restaurantleftoverfood.DonationsRestaurant;
import com.example.anany.restaurantleftoverfood.R;
import com.example.anany.restaurantleftoverfood.Settings;
import com.example.anany.restaurantleftoverfood.Storage.DonationsList;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class DonationsListAdapter extends ArrayAdapter<DonationsList> {
    private Context mContext;
    private List<DonationsList> list = new ArrayList<>();
    Button request, moreInfo;
    ImageView call, copy;

    public DonationsListAdapter(@NonNull Context context, ArrayList<DonationsList> list) {
        super(context, 0, list);
        mContext = context;
        this.list = list;
    }


    Dialog show;
    int clicks;
    int clicks2;
    int clicks3, clicks4, clicks5, clicks6, clicks7, clicks8, clicks9, clicks10;

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        final DonationsList row = list.get(position);
        //DONE Set the ListItem depending on the Status.
        listItem = LayoutInflater.from(mContext).inflate(R.layout.shelter_pending_approval, parent, false);
        clicks = 0;
        clicks2 = 0;
        clicks3 = clicks4 = clicks5 = clicks6 = clicks7 = clicks8 = clicks9 = clicks10 = 0;
        String status = row.getStatus();

        if (status.contains("Restaurant Approval")) {
            processRestaurantApproval(listItem, row);
            handleCommon(listItem, row);
        } else if (status.contains("Pickup")) {
            listItem = LayoutInflater.from(mContext).inflate(R.layout.shelter_pending_pickup, parent, false);
            processShelterPickup(listItem, row);
            handleCommon(listItem, row);
        } else if (status.contains("Done")) {
            listItem = LayoutInflater.from(mContext).inflate(R.layout.shelter_done_pickup, parent, false);
            handleCommon(listItem, row);
        } else if (status.contains("Cancel")) {
            listItem = LayoutInflater.from(mContext).inflate(R.layout.shelter_cancelled, parent, false);
            handleCommon2(listItem, row);
        } else if (status.contains("Decline")) {
            listItem = LayoutInflater.from(mContext).inflate(R.layout.shelter_declined, parent, false);
            handleCommon2(listItem, row);
        }

        TextView date = listItem.findViewById(R.id.date);
        date.setText(row.getTime() + ", " + row.getDate());
        return listItem;
    }

    private void processShelterPickup(View listItem, final DonationsList row) {
        //DONE Check if the following works.
        Button completed = listItem.findViewById(R.id.done);
        if (completed != null) {
            completed.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    clicks6++;
                    if (clicks6 == 1) {
                        final Dialog show = new Dialog(mContext);
                        show.setContentView(R.layout.confirm_receival);
                        show.setCancelable(true);

                        final EditText messageT = show.findViewById(R.id.custom);

                        if (show == null)
                            show.show();
                        Button yes = show.findViewById(R.id.yes);
                        Button no = show.findViewById(R.id.no);
                        no.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View view, MotionEvent motionEvent) {
                                show.dismiss();
                                show.cancel();
                                clicks6 = 0;
                                return false;
                            }
                        });
                        yes.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View view, MotionEvent motionEvent) {
                                clicks7++;
                                if (clicks7 == 1) {
                                    String message = "";
                                    //DONE When button pressed, you have to change status in Shelter's Donations path for this instance to
                                    //  Done or whatever.
                                    //DONE In the Donations path for the restaurant, you have to set the status to Done.
                                    Settings.makeProcessingBrownUncancelable(mContext);

                                    if (messageT.getText().toString() == null || messageT.getText().toString().length() < 1) {
                                        message = "None";
                                    } else
                                        message = messageT.getText().toString();

                                    final Date currentTime = Calendar.getInstance().getTime();
                                    String t = currentTime.toString();
                                    String[] stuff = t.split(" ");
                                    final String date = stuff[1] + " " + stuff[2];
                                    String time = stuff[3];
                                    int hours = Integer.parseInt(time.split(":")[0]);
                                    if (hours > 12) {
                                        time = (hours - 12) + ":" + time.split(":")[1] + " PM";
                                    } else if (hours == 0) {
                                        time = "12" + ":" + time.split(":")[1] + " AM";
                                    } else {
                                        time += time.split(":")[0] + ":" + time.split(":")[1] + " AM";
                                    }
                                    addRestaurantPoints(row, date, time);
                                    //removeRNum(row);

                                    show.cancel();
                                    show.dismiss();

                                    DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Donations");
                                    final String finalMessage = message;
                                    final String finalMessage1 = message;
                                    final String finalTime = time;
                                    dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                                String VC = child.getKey();
                                                if (VC.equals(row.getShelterVC())) {
                                                    final DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Donations/" + VC);
                                                    dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                                                String status = child.child("Status").getValue().toString();
                                                                String tempVC = child.child("Restaurant VC").getValue().toString();
                                                                if (tempVC.equals(row.getRestaurantVC()) && status.equals(row.getStatus())) {
                                                                /*DatabaseReference dbref2 = child.getRef();
                                                                dbref2.push().setValue();*/


                                                                /*final HashMap<String, String> stuff2 = new HashMap<>();
                                                                stuff2.put("Cancelled Date", date);
                                                                stuff2.put("Cancelled Time", time);*/
                                                                    dbref.child(child.getKey()).child("Done Date").setValue(date);
                                                                    dbref.child(child.getKey()).child("Done Time").setValue(finalTime);
                                                                    dbref.child(child.getKey()).child("MessageS").setValue(finalMessage);

                                                                    final String finalTime2 = finalTime;
                                                                    final String finalTime1 = finalTime;
                                                                    dbref.child(child.getKey()).child("Status").setValue("Done").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            final DatabaseReference Rdbref = FirebaseDatabase.getInstance().getReference("Donations");
                                                                            Rdbref.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                @Override
                                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                                                                                        String VC2 = child.getKey();
                                                                                        if (VC2.equals(row.getRestaurantVC())) {
                                                                                            final DatabaseReference Rdbref2 = FirebaseDatabase.getInstance().getReference("Donations/" + VC2);
                                                                                            Rdbref2.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                @Override
                                                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                                                                                                        String status = child.child("Status").getValue().toString();
                                                                                                        String tempVC = child.child("Shelter VC").getValue().toString();
                                                                                                        if (tempVC.equals(row.getShelterVC()) && status.equals(row.getStatus())) {
                                                                                                            Rdbref2.child(child.getKey()).child("Done Date").setValue(date);
                                                                                                            Rdbref2.child(child.getKey()).child("Done Time").setValue(finalTime);
                                                                                                            Rdbref2.child(child.getKey()).child("MessageS").setValue(finalMessage1);


//                                                                                                            addRestaurantPoints(row, date, finalTime1);
                                                                                                            //removeRNum(row);
                                                                                                            clicks7 = 0;
                                                                                                            clicks6 = 0;
                                                                                                            Rdbref2.child(child.getKey()).child("Status").setValue("Done")/*.addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                @Override
                                                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                                                    //DONE When receival confirmed, cancelled, or declined delete address of restaurant from RNum in Firebase.
                                                                                                                    //DONE Write code to award donation points in Donations path then with matching restaurant VC.
                                                                                                                    //DONE In RDonationsListAdapter, not only do you have to give the donation points but you also need to
                                                                                                                    //  Write it to the donationsNum.txt file and maybe call the function again to change what is displayed.


                                                                                                                }
                                                                                                            })*/;
                                                                                                            break;
                                                                                                        }
                                                                                                    }
                                                                                                }

                                                                                                @Override
                                                                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                                                    makeToast("Error. Please have a good WiFi connection!");
                                                                                                    clicks7 = 0;
                                                                                                    clicks6 = 0;
                                                                                                }
                                                                                            });
                                                                                        }
                                                                                    }
                                                                                }

                                                                                @Override
                                                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                                    makeToast("Error. Please have a good WiFi connection!");
                                                                                    clicks7 = 0;
                                                                                }
                                                                            });
                                                                        }
                                                                    });
                                                                    clicks6 = 0;
                                                                    break;
                                                                }
                                                            }

                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                                            makeToast("Error. Please have a good WiFi connection!");
                                                            clicks7 = 0;
                                                        }
                                                    });
                                                    break;
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            makeToast("Error. Please have a good WiFi connection!");
                                            clicks7 = 0;
                                        }
                                    });

                                    makeToast("Have to change stuff in paths to confirm pickup.");
                                }
                                return false;
                            }
                        });
                        show.show();

                        show.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                                clicks6 = 0;
                                clicks7 = 0;
                            }
                        });

                    }
                    return false;
                }
            });
        }
    }

    private void addRestaurantPoints(final DonationsList row, final String date, String time) {
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Places");
        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String temp = child.child("Verification Code").getValue().toString();
                    if (temp.equals(row.getRestaurantVC())) {
                        String status = row.getShelterStatus();
                        String amount = row.getShelterAmount();

                        float donationPoints = Float.parseFloat(child.child("Donation Points").getValue().toString());

                        float points;
                        if (row.getMessageInfo().contains("Shelter Request")) {
                            String requestDate = row.getRequestDate();
                            String requestTime = row.getRequestTime();
                            String[] split = requestDate.split(" ");
                            String[] split2 = date.split(" ");

                            int dayNum = Integer.parseInt(split[1]);
                            int doneDayNum = Integer.parseInt(split2[1]);
                            int monthScore = getMonthNum(split[0]);
                            int doneMonthScore = getMonthNum(split2[0]);

                            int monthDif = doneMonthScore - monthScore;
                            if (monthDif < 0)
                                monthDif += 12;

                            int dayDif = doneDayNum - dayNum;
                            if (dayDif < 0) {
                                monthDif--;
                                dayDif += 30;
                            }

                            int totalDayDif = monthDif * 30 + dayDif;

                            if (totalDayDif < 2) {
                                points = (float) (getPoints(status, amount) * 1.8f);
                            } else if (totalDayDif < 4) {
                                points = (float) (getPoints(status, amount) * 1.5f);
                            } else if (totalDayDif < 8) {
                                points = (float) (getPoints(status, amount) * 1.2f);
                            } else if (totalDayDif < 15) {
                                points = (float) (getPoints(status, amount) * 0.9f);
                            } else {
                                points = (float) (getPoints(status, amount) * 0.5f);
                            }
                        } else
                            points = (float) getPoints(status, amount);

                        donationPoints += points;
                        int numOfDonations = Integer.parseInt(child.child("Num of Donations").getValue().toString());

                        DatabaseReference toAdd = child.getRef();
                        final float finalDonationPoints = donationPoints;

                        makeToast("Points: " + donationPoints);

                        toAdd.child("Num of Donations").setValue("" + (numOfDonations + 1));
                        toAdd.child("Donation Points").setValue(((int) donationPoints) + "").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                //makeToast("Updated the score: \n+" + finalDonationPoints);
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

    public float getPoints(String status, String amount) {
       /* String split[] = status.split(" ");
        String[] split2 = amount.split(" ");
        String realShelterStatus = ss;
        String realShelterAmount = sa;*/
        int day1;
        int day2;
        int people1;
        int people2;

        String[] array1 = amount.split(" ");
        String[] array2 = status.split(" ");
        String realShelterAmount = "";
        String realShelterStatus = "";
        if (amount.contains("None")) {
            realShelterAmount = "0 to 0";
            people1 = 0;
            people2 = 0;
        } else if (amount.contains("to")) {
            realShelterAmount = amount;
            people1 = Integer.parseInt(array1[0]);
            people2 = Integer.parseInt(array1[2]);
        } else {
            people1 = Integer.parseInt(array1[0]);
            people2 = Integer.parseInt(array1[0]);
        }

        if (status.contains("Not")) {
            day1 = 0;
            day2 = 0;
        } else if (status.contains("to")) {
            realShelterAmount = amount;
            day1 = Integer.parseInt(array2[0]);
            day2 = Integer.parseInt(array2[2]);
        } else {
            day1 = Integer.parseInt(array2[0]);
            day2 = Integer.parseInt(array2[0]);
        }

        //makeToast(split[0] + " " + split[2] + "\n" + split2[0] + " " + split2[2]);
        //makeToast(status + "\n" + amount);

        float starter = 10f;

        float special = 1;
        if (day1 < 3) {
            special = 1.3f;
        } else if (day1 < 6)
            special = 1.1f;

        float special2 = 1;
        float avg = (people1 + people2) / 2;
        if (avg > 9000) {
            special2 = 20;
        } else if (avg > 6000)
            special2 = 18;
        else if (avg > 3000)
            special2 = 16;
        else if (avg > 1000)
            special2 = 15;
        else if (avg > 500)
            special2 = 13;
        else if (avg > 300)
            special2 = 7;
        else if (avg > 200)
            special2 = 5;
        else if (avg > 100)
            special2 = 4;
        else if (avg > 60)
            special2 = 2;
        else if (avg > 30)
            special2 = 1.5f;
        //DONE Try changing the return type to a float so that it is more accurate.
        return (float) (starter * special * special2);
    }

    public static int getMonthNum(String s) {
        switch (s) {
            case "Jan":
                return 1;
            case "Feb":
                return 2;
            case "Mar":
                return 3;
            case "Apr":
                return 4;
            case "May":
                return 5;
            case "Jun":
                return 6;
            case "Jul":
                return 7;
            case "Aug":
                return 8;
            case "Sep":
                return 9;
            case "Oct":
                return 10;
            case "Nov":
                return 11;
            case "Dec":
                return 12;
            default:
                return 12;
        }
    }

    private void removeRNum(DonationsList row) {
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Places");
        clearRNum(dbref, row.getShelterVC(), row.getRestaurantVC());
        clearRNum(dbref, row.getRestaurantVC(), row.getShelterVC());
        clicks2 = 0;
        clicks7 = 0;
        clicks = 0;
        clicks8 = 0;
        Settings.dismissBrownUncancellable();
    }

    private void clearRNum(final DatabaseReference dbref, final String VC, final String removeVC) {
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
                                if (!v.equals(removeVC)) {
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

    //Future files used below code in ListView 2 for approving and declining.
    /*private void processShelterApproval(View listItem, DonationsList row) {
        Button approve = listItem.findViewById(R.id.approve);
        Button decline = listItem.findViewById(R.id.decline);

        if(approve != null){
            approve.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    clicks++;
                    if (clicks == 1) {
                        //Future files Handle code for approving request
                        clicks = 0;
                    }
                    return false;
                }
            });
        }
    }*/

    private void processRestaurantApproval(View listItem, final DonationsList row) {
        Button cancel = listItem.findViewById(R.id.cancel);
        if (cancel != null) {
            cancel.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    clicks++;
                    if (clicks == 1) {
                        //DONE set it back to clicks = 0 after whatever processing is done.
                        final Dialog show = new Dialog(mContext);
                        show.setContentView(R.layout.ask_sure_cancel);
                        show.setCancelable(true);

                        TextView message = show.findViewById(R.id.message);
                        message.setText("Do you want to cancel your request to " + row.getName() + "? They haven't approved or declined yet.\nMaybe explain why below:");
                        final EditText messageT = show.findViewById(R.id.custom);

                        show.show();

                        Button yes = show.findViewById(R.id.yes);
                        Button no = show.findViewById(R.id.no);
                        no.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View view, MotionEvent motionEvent) {
                                show.dismiss();
                                show.cancel();
                                clicks = 0;
                                return false;
                            }
                        });
                        yes.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View view, MotionEvent motionEvent) {
                                clicks2++;
                                if (clicks2 == 1) {
                                    //DONE Have to process cancellation. Change status for Shelter in Donations to Cancelled.
                                    //DONE  For restaurant, go to Accept/Approve and delete it from there and write to Donations under shelter VC
                                    //DONE  Saying that it was cancelled. Find a way in 2nd ListView to display text saying latest instance was cancelled.
                                    //makeToast("Have to change stuff in paths.");
                                    Settings.makeProcessingBrownUncancelable(mContext);
                                    //removeRNum(row);
                                    DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Donations");
                                    dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                                String VC = child.getKey();
                                                if (VC.equals(row.getShelterVC())) {
                                                    final String messageText;
                                                    final DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Donations/" + VC);
                                                    if (messageT.getText().toString() == null || messageT.getText().toString().length() < 1) {
                                                        messageText = "None";
                                                    } else
                                                        messageText = messageT.getText().toString();
                                                    dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                                                String status = child.child("Status").getValue().toString();
                                                                String tempVC = child.child("Restaurant VC").getValue().toString();
                                                                if (tempVC.equals(row.getRestaurantVC()) && status.equals(row.getStatus())) {
                                                                /*DatabaseReference dbref2 = child.getRef();
                                                                dbref2.push().setValue();*/
                                                                    final Date currentTime = Calendar.getInstance().getTime();
                                                                    String t = currentTime.toString();
                                                                    String[] stuff = t.split(" ");
                                                                    final String date = stuff[1] + " " + stuff[2];
                                                                    String time = stuff[3];
                                                                    int hours = Integer.parseInt(time.split(":")[0]);
                                                                    if (hours > 12) {
                                                                        time = (hours - 12) + ":" + time.split(":")[1] + " PM";
                                                                    } else if (hours == 0) {
                                                                        time = "12" + ":" + time.split(":")[1] + " AM";
                                                                    } else {
                                                                        time += time.split(":")[0] + ":" + time.split(":")[1] + " AM";
                                                                    }

                                                                /*final HashMap<String, String> stuff2 = new HashMap<>();
                                                                for (DataSnapshot children : child.getChildren()) {
                                                                    stuff2.put(children.getKey(), children.getValue().toString());
                                                                }
                                                                stuff2.put("Cancelled Date", date);
                                                                stuff2.put("Cancelled Time", time);
                                                                dbref.push().setValue(stuff2).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        makeToast("Done putting cancel.");
                                                                    }
                                                                });*/

                                                                    dbref.child(child.getKey()).child("Cancelled Date").setValue(date);
                                                                    dbref.child(child.getKey()).child("Cancelled Time").setValue(time);
                                                                    dbref.child(child.getKey()).child("MessageS").setValue(messageText);

                                                                    final String finalTime = time;
                                                                    dbref.child(child.getKey()).child("Status").setValue("Cancelled").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            final DatabaseReference Rdbref = FirebaseDatabase.getInstance().getReference("Accept");
                                                                            Rdbref.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                @Override
                                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                                                                                        String VC2 = child.getKey();
                                                                                        if (VC2.equals(row.getRestaurantVC())) {
                                                                                            final DatabaseReference Rdbref2 = FirebaseDatabase.getInstance().getReference("Accept/" + VC2);
                                                                                            Rdbref2.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                @Override
                                                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                                                                                                        String status = child.child("Status").getValue().toString();
                                                                                                        String tempVC = child.child("Shelter VC").getValue().toString();

                                                                                                        if (tempVC.equals(row.getShelterVC()) && status.equals(row.getStatus())) {
                                                                                                            //INFO Get the values and write them to Donations path under Restaurant's ID.
                                                                                                            //  Then you can go ahead and delete the child.
                                                                                                            HashMap<String, String> vals = new HashMap<>();
                                                                                                            //vals.putAll(stuff2);
                                                                                                        /*for (DataSnapshot children : dataSnapshot.getChildren()) {
                                                                                                            for(DataSnapshot more: children.getChildren()) {
                                                                                                                vals.put(children.getKey(), children.getValue().toString());
                                                                                                                Settings.makeTesting(mContext, children.getKey());
                                                                                                            }
                                                                                                        }*/
                                                                                                            vals.put("Date", quicker(child, "Date"));
                                                                                                            vals.put("Time", quicker(child, "Time"));
                                                                                                            vals.put("Units", quicker(child, "Units"));
                                                                                                            vals.put("Decline Date", quicker(child, "Decline Date"));
                                                                                                            vals.put("Decline Time", quicker(child, "Decline Time"));
                                                                                                            vals.put("Distance", quicker(child, "Distance"));
                                                                                                            vals.put("Done Date", quicker(child, "Done Date"));
                                                                                                            vals.put("Done Time", quicker(child, "Done Time"));
                                                                                                            vals.put("Message InfoR", quicker(child, "Message InfoR"));
                                                                                                            vals.put("Message InfoS", quicker(child, "Message InfoS"));
                                                                                                            vals.put("MessageR", quicker(child, "MessageR"));
                                                                                                            vals.put("MessageS", messageText);
                                                                                                            vals.put("Pickup Date", quicker(child, "Pickup Date"));
                                                                                                            vals.put("Pickup Time", quicker(child, "Pickup Time"));
                                                                                                            vals.put("Restaurant Address", quicker(child, "Restaurant Address"));
                                                                                                            vals.put("Restaurant Hours", quicker(child, "Restaurant Hours"));
                                                                                                            vals.put("Restaurant Latitude", quicker(child, "Restaurant Latitude"));
                                                                                                            vals.put("Restaurant Longitude", quicker(child, "Restaurant Longitude"));
                                                                                                            vals.put("Restaurant Manager", quicker(child, "Restaurant Manager"));
                                                                                                            vals.put("Restaurant Name", quicker(child, "Restaurant Name"));
                                                                                                            vals.put("Restaurant Phone", quicker(child, "Restaurant Phone"));
                                                                                                            vals.put("Restaurant Rating", quicker(child, "Restaurant Rating"));
                                                                                                            vals.put("Restaurant VC", quicker(child, "Restaurant VC"));
                                                                                                            vals.put("Restaurant Website", quicker(child, "Restaurant Website"));
                                                                                                            vals.put("Shelter Address", quicker(child, "Shelter Address"));
                                                                                                            vals.put("Shelter Hours", quicker(child, "Shelter Hours"));
                                                                                                            vals.put("Shelter Latitude", quicker(child, "Shelter Latitude"));
                                                                                                            vals.put("Shelter Longitude", quicker(child, "Shelter Longitude"));
                                                                                                            vals.put("Shelter Manager", quicker(child, "Shelter Manager"));
                                                                                                            vals.put("Shelter Name", quicker(child, "Shelter Name"));
                                                                                                            vals.put("Shelter Phone", quicker(child, "Shelter Phone"));
                                                                                                            vals.put("Shelter Status", quicker(child, "Shelter Status"));
                                                                                                            vals.put("Shelter VC", quicker(child, "Shelter VC"));
                                                                                                            vals.put("Shelter Website", quicker(child, "Shelter Website"));
                                                                                                            vals.put("Shelter Amount", quicker(child, "Shelter Amount"));

                                                                                                            vals.put("Status", "Cancelled");
                                                                                                            vals.put("Cancelled Date", date);
                                                                                                            vals.put("Cancelled Time", finalTime);
                                                                                                            //makeToast("VALUES:\n\n" + vals.toString());
                                                                                                            //DONE Write the values to Donations path under Restaurant ID.
                                                                                                            DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Donations/" + row.getRestaurantVC());
                                                                                                            dbref.push().setValue(vals);
                                                                                                            show.cancel();
                                                                                                            show.dismiss();

                                                                                                            //DONE When receival confirmed, cancelled, or declined delete address of restaurant from RNum in Firebase.
//                                                                                                            removeRNum(row);
                                                                                                            clicks2 = 0;

                                                                                                            DatabaseReference toRemove = Rdbref2.child(child.getKey());
                                                                                                            toRemove.removeValue();
                                                                                                            break;
                                                                                                        }
                                                                                                    }
                                                                                                }

                                                                                                @Override
                                                                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                                                    clicks2 = 0;
                                                                                                    makeToast("Error. Please have a good WiFi connection!");
                                                                                                }
                                                                                            });
                                                                                            break;
                                                                                        }
                                                                                    }
                                                                                }

                                                                                @Override
                                                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                                    clicks2 = 0;
                                                                                    makeToast("Error. Please have a good WiFi connection!");
                                                                                }
                                                                            });
                                                                        }
                                                                    });
                                                                    clicks = 0;
                                                                    break;
                                                                }
                                                            }

                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                                            makeToast("Error. Please have a good WiFi connection!");
                                                            clicks2 = 0;
                                                        }
                                                    });
                                                    break;
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            makeToast("Error. Please have a good WiFi connection!");
                                            clicks2 = 0;
                                        }
                                    });
                                    clicks = 0;
                                }
                                return false;
                            }
                        });

                        show.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                                clicks = 0;
                                clicks2 = 0;
                            }
                        });
                    }
                    return false;
                }
            });
        }
    }

    private String quicker(DataSnapshot child, String s) {
        return child.child(s).getValue().toString();
    }

    private void handleCommon2(View listItem, final DonationsList row) {
        TextView name = listItem.findViewById(R.id.shelterName);
        name.setText(row.getName());
        name.setPaintFlags(name.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        TextView phone = listItem.findViewById(R.id.phone);
        phone.setText(row.getContactPhone());

        TextView message = listItem.findViewById(R.id.message);
        message.setText(row.getMessage());

        ImageView copy = listItem.findViewById(R.id.copy);
        ImageView call = listItem.findViewById(R.id.call);

        call.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:+1 732-306-9190"));
                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.CALL_PHONE}, 1);
                } else
                    makeToast("Calling " + row.getName() + "\nat " + row.getContactPhone() + ".");
                //mContext.startActivity(intent);
                return false;
            }
        });

        copy.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Phone Number", row.getContactPhone());
                clipboard.setPrimaryClip(clip);
                makeToast("Phone # copied!");
                return false;
            }
        });

    }

    private void handleCommon(View listItem, final DonationsList row) {
        TextView name = listItem.findViewById(R.id.shelterName);
        name.setText(row.getName());
        name.setPaintFlags(name.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        TextView manager = listItem.findViewById(R.id.manager);
        manager.setText(row.getManagerName());

        TextView phone = listItem.findViewById(R.id.phone);
        phone.setText(row.getContactPhone());

        TextView message = listItem.findViewById(R.id.message);
        message.setText(row.getMessage());

        ImageView copy = listItem.findViewById(R.id.copy);
        ImageView call = listItem.findViewById(R.id.call);

        call.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:+1 732-306-9190"));
                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.CALL_PHONE}, 1);
                } else
                    makeToast("Calling " + row.getName() + "\nat " + row.getContactPhone() + ".");
                //mContext.startActivity(intent);
                return false;
            }
        });

        copy.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Phone Number", row.getContactPhone());
                clipboard.setPrimaryClip(clip);
                makeToast("Phone # copied!");
                return false;
            }
        });

    }


    private void makeToast(String s) {
        Toast.makeText(mContext, s, Toast.LENGTH_LONG).show();
    }
}

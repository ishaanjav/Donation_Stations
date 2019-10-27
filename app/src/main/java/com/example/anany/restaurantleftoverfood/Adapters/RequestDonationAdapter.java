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
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anany.restaurantleftoverfood.R;
import com.example.anany.restaurantleftoverfood.Settings;
import com.example.anany.restaurantleftoverfood.Storage.RequestDonation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class RequestDonationAdapter extends ArrayAdapter<RequestDonation> {
    private Context mContext;
    private List<RequestDonation> list = new ArrayList<>();
    Button request, moreInfo;
    ImageView call, copy;

    public RequestDonationAdapter(@NonNull Context context, ArrayList<RequestDonation> list) {
        super(context, 0, list);
        mContext = context;
        this.list = list;
    }


    Dialog show;
    int clicks;
    int clicks2;

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        final RequestDonation row = list.get(position);
        listItem = LayoutInflater.from(mContext).inflate(R.layout.request_donation, parent, false);

        TextView nameView = listItem.findViewById(R.id.shelterName);
        TextView addressView = listItem.findViewById(R.id.distance);
        TextView hours = listItem.findViewById(R.id.hours);
        TextView managerView = listItem.findViewById(R.id.manager);
        TextView rating = listItem.findViewById(R.id.rating);
        TextView phoner = listItem.findViewById(R.id.phone);
        request = listItem.getRootView().findViewById(R.id.request);
        moreInfo = listItem.findViewById(R.id.moreInfo);
        call = listItem.findViewById(R.id.call);
        copy = listItem.findViewById(R.id.copy);
        ImageView[] stars = {listItem.findViewById(R.id.star1), listItem.findViewById(R.id.star2), listItem.findViewById(R.id.star3), listItem.findViewById(R.id.star4), listItem.findViewById(R.id.star5)};

        double ratingN = Double.parseDouble(row.getRating());
        int t = (int) ratingN;
        for (int i = 0; i < t; i++) {
            stars[i].setImageDrawable(mContext.getDrawable(R.drawable.star));
        }
        if (ratingN > t)
            stars[t].setImageDrawable(mContext.getDrawable(R.drawable.halfstar));

        nameView.setPaintFlags(nameView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        nameView.setText(row.getName());
        hours.setText(getHours(row.getOpeningHours()));
        rating.setText(row.getRating());
        if (row.getContactPhone().contains("+1 "))
            phoner.setText(row.getContactPhone().substring(3));
        else
            phoner.setText(row.getContactPhone());

        double dis = Double.parseDouble(row.getDistance());
        if (row.getUnits().contains("i")) {
            dis *= 69;
        } else {
            dis *= 111;
        }
        final boolean map = row.isInMap();


        dis = Math.round(dis * 10) / 10d;
        addressView.setText(dis + " " + row.getUnits());

        managerView.setText(row.getManagerName());
        clicks = 0;

        final View finalListItem = listItem;
        request.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (!row.getAmount().equals("0 0") && !row.getStatus().equals("0 0")) {
                    clicks++;
                    if (clicks == 1) {

                        //TODO Ask if Sure
                        //TODO Then say your request will be sent. Do you want to send a custom message to the folks at {restaurant name}?
                        //  If they don't want to send message, pass in None.

                        //TODO Save the time and date

                        //TODO When they send the request, in Firebase, for the restaurant write to RNum.
                        //TODO First read from it and check if it is not null. Then, append "h" and the address of the current shelter.
                        //TODO Write the info to table called Donations and then to path with shelter verification code. Write all the info. Write status: Pending approval.
                        //TODO Also write the path to table called Accept and then to path with restaurant verification. Write info about shelter
                        //TODO Save hours.

                        final Dialog show = new Dialog(mContext);
                        //show.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        show.setContentView(R.layout.ask_if_request_sure);
                        show.setCancelable(true);

                        final TextView message = show.findViewById(R.id.message);
                        message.setText("Consider sending additional details to " + row.getName() + " along with your request.");
                        //message.setText("AMOUNT: " + row.getAmount() + "\nStatus: " + row.getStatus());
                        //show.getWindow().setBackgroundDrawableResource(R.color.transparent);

                        TextView details = show.findViewById(R.id.details);
                        details.setText(details.getText() + "\n        " + row.getStatus().split(" ")[0] + " to " + row.getStatus().split(" ")[1] + " days" + "\n        " +
                                row.getAmount().split(" ")[0] + " to " + row.getAmount().split(" ")[1] + " people");

                        final EditText custom = show.findViewById(R.id.custom);
                        Button no = show.findViewById(R.id.no);
                        Button yes = show.findViewById(R.id.yes);
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

                        //TODO When receival confirmed, or cancelled, delete address of restaurant from RNum in Firebase.

                        final String finalTime = time;
                        final String finalTime1 = time;
                        clicks2 = 0;
                        yes.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View view, MotionEvent motionEvent) {
                                clicks2++;
                                if (clicks2 == 1) {

                                    //makeToast("DATE: " + date + "\n" + "TIME: " + finalTime);
                                    String customMessage = custom.getText().toString();
                                    if (customMessage.isEmpty() || customMessage.length() == 0)
                                        customMessage = "None";
                                    //makeToast(customMessage);
                                    //DONE Save the data.
                                    //DONE Message and date

                                    Settings.makeProcessing2(mContext);
                                    final String ss;
                                    int n1 = Integer.parseInt(row.getStatus().split(" ")[0]);
                                    int n2 = Integer.parseInt(row.getStatus().split(" ")[1]);
                                    if ((n1) == 0 && (n2) == 0)
                                        ss = ("None");
                                    else if ((n1) == (n2))
                                        ss = (n1 + " days");
                                    else
                                        ss = (n1 + " to " + n2 + " days");

                                    final String sa;
                                    int c1 = Integer.parseInt(row.getAmount().split(" ")[0]);
                                    int c2 = Integer.parseInt(row.getAmount().split(" ")[1]);
                                    if ((c1) == 0 && (c2) == 0)
                                        sa = ("None");
                                    else if ((c1) == (c2))
                                        sa = (c1 + " people");
                                    else
                                        sa = (c1 + " to " + c2 + " people");

                                    final String realShelterStatus = ss;
                                    final String realShelterAmount = sa;

                                    DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Donations/" + row.getShelterVC());
                                    HashMap<String, String> vals = new HashMap<>();
                                    vals.put("Restaurant VC", row.getRestaurantVC());
                                    vals.put("Restaurant Name", row.getName());
                                    vals.put("Restaurant Address", row.getAddress());
                                    vals.put("Restaurant Manager", row.getManagerName());
                                    vals.put("Restaurant Phone", row.getContactPhone());
                                    vals.put("Restaurant Latitude", row.getToLat() + "");
                                    vals.put("Restaurant Longitude", row.getToLon() + "");
                                    vals.put("Restaurant Hours", row.getOpeningHours());
                                    vals.put("Restaurant Website", row.getWebsite());
                                    vals.put("Restaurant Rating", row.getRating());
                                    vals.put("Distance", row.getDis());
                                    vals.put("Units", row.getUnits());
                                    vals.put("MessageS", customMessage);
                                    vals.put("MessageR", "None");
                                    vals.put("Date", date);
                                    vals.put("Time", finalTime1);
                                    vals.put("Message InfoS", "Shelter Request");
                                    vals.put("Message InfoR", "None");

                                    vals.put("Cancelled Date", ",");
                                    vals.put("Cancelled Time", ",");
                                    vals.put("Pickup Date", ",");
                                    vals.put("Pickup Time", ",");
                                    vals.put("Decline Date", ",");
                                    vals.put("Decline Time", ",");
                                    vals.put("Done Date", ",");
                                    vals.put("Done Time", ",");

                                    vals.put("Status", "Pending Restaurant Approval");
                                    vals.put("Shelter Name", row.getCurrentName());
                                    vals.put("Shelter Phone", row.getCurrentPhone());
                                    vals.put("Shelter Website", row.getCurrentWebsite());
                                    vals.put("Shelter Hours", row.getCurrentHours());
                                    vals.put("Shelter Manager", row.getCurrentManager());
                                    vals.put("Shelter Status", realShelterStatus);
                                    vals.put("Shelter Amount", realShelterAmount);
                                    vals.put("Shelter Address", row.getCurrentAddress());
                                    vals.put("Shelter Latitude", row.getLat() + "");
                                    vals.put("Shelter Longitude", row.getLon() + "");
                                    vals.put("Shelter VC", row.getShelterVC());

                                    final String finalCustomMessage = customMessage;
                                    dbref.push().setValue(vals).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            DatabaseReference dbref2 = FirebaseDatabase.getInstance().getReference("Accept/" + row.getRestaurantVC());
                                            HashMap<String, String> vals2 = new HashMap<>();
                                            vals2.put("Restaurant VC", row.getRestaurantVC());
                                            vals2.put("Restaurant Name", row.getName());
                                            vals2.put("Restaurant Address", row.getAddress());
                                            vals2.put("Restaurant Manager", row.getManagerName());
                                            vals2.put("Restaurant Phone", row.getContactPhone());
                                            vals2.put("Restaurant Latitude", row.getToLat() + "");
                                            vals2.put("Restaurant Longitude", row.getToLon() + "");
                                            vals2.put("Restaurant Hours", row.getOpeningHours());
                                            vals2.put("Restaurant Website", row.getWebsite());
                                            vals2.put("Restaurant Rating", row.getRating());
                                            vals2.put("Distance", row.getDis());
                                            vals2.put("Units", row.getUnits());
                                            vals2.put("Message InfoS", "Shelter Request");
                                            vals2.put("Message InfoR", "None");
                                            vals2.put("MessageR", "None");
                                            vals2.put("MessageS", finalCustomMessage);

                                            vals2.put("Cancelled Date", ",");
                                            vals2.put("Cancelled Time", ",");
                                            vals2.put("Pickup Date", ",");
                                            vals2.put("Pickup Time", ",");
                                            vals2.put("Decline Date", ",");
                                            vals2.put("Decline Time", ",");
                                            vals2.put("Done Date", ",");
                                            vals2.put("Done Time", ",");

                                            vals2.put("Status", "Pending Restaurant Approval");
                                            vals2.put("Date", date);
                                            vals2.put("Time", finalTime1);
                                            vals2.put("Shelter Name", row.getCurrentName());
                                            vals2.put("Shelter Phone", row.getCurrentPhone());
                                            vals2.put("Shelter Website", row.getCurrentWebsite());
                                            vals2.put("Shelter Hours", row.getCurrentHours());
                                            vals2.put("Shelter Manager", row.getCurrentManager());
                                            vals2.put("Shelter Status", realShelterStatus);
                                            vals2.put("Shelter Amount", realShelterAmount);
                                            vals2.put("Shelter Address", row.getCurrentAddress());
                                            vals2.put("Shelter Latitude", row.getLat() + "");
                                            vals2.put("Shelter Longitude", row.getLon() + "");
                                            vals2.put("Shelter VC", row.getShelterVC());
                                            clicks = 0;
                                            clicks2 = 0;
                                            /*show.dismiss();
                                            show.cancel();*/
                                            dbref2.push().setValue(vals2);
                                            final DatabaseReference reference
                                                    = FirebaseDatabase.getInstance().getReference("Places");
                                            //Settings.dismissProcessing2();
                                            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    for (final DataSnapshot child : dataSnapshot.getChildren()) {
                                                        String VC = child.child("Verification Code").getValue().toString();
                                                        //Settings.makeTesting(mContext, VC + "\n" + row.getRestaurantVC());
                                                        if (VC.equals(row.getRestaurantVC())) {
                                                            DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Places/" + child.getKey() + "/RNum");
                                                            if (child.child("RNum") == null) {
                                                                dbref.setValue(row.getShelterVC()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        shelter(row, show, finalListItem);
                                                                    }
                                                                });
                                                            } else {
                                                                if (child.child("RNum").getValue().toString().length() < 3) {
                                                                    dbref.setValue(row.getShelterVC()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            shelter(row, show, finalListItem);
                                                                        }
                                                                    });
                                                                } else {
                                                                    String temp = child.child("RNum").getValue().toString();
                                                                    dbref.setValue(temp + "h" + row.getShelterVC()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            shelter(row, show, finalListItem);
                                                                        }
                                                                    });
                                                                }
                                                            }
                                                            break;
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                    Settings.dismissProcessing2();
                                                }
                                            });

                                            /*dbref2.push().setValue(vals).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    final DatabaseReference reference
                                                            = FirebaseDatabase.getInstance().getReference("Places");
                                                    Settings.dismissProcessing2();
                                                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                                                String VC = child.child("Verification Code").getValue().toString();
                                                                if (VC.equals(row.getRestaurantVC())) {
                                                                    if (child.child("RNum") == null) {
                                                                        reference.child(child.getKey()).child("RNum").setValue(row.getShelterVC()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                makeToast("SICESS!");
                                                                                Settings.dismissProcessing2();
                                                                            }
                                                                        });
                                                                    } else {
                                                                        if (child.child("RNum").getValue().toString().length() < 3) {
                                                                            reference.child(child.getKey()).child("RNum").setValue(row.getShelterVC()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    makeToast("SICESS!");
                                                                                    Settings.dismissProcessing2();
                                                                                }
                                                                            });
                                                                        } else {
                                                                            String temp = child.child("RNum").getValue().toString();
                                                                            reference.child(child.getKey()).child("RNum").setValue(temp + "h" + row.getShelterVC()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    makeToast("SICESS!");
                                                                                    Settings.dismissProcessing2();
                                                                                }
                                                                            });
                                                                        }
                                                                    }
                                                                }
                                                                break;
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                                            Settings.dismissProcessing2();
                                                        }
                                                    });
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Settings.dismissProcessing2();
                                                }
                                            }).addOnCanceledListener(new OnCanceledListener() {
                                                @Override
                                                public void onCanceled() {
                                                    Settings.dismissProcessing2();
                                                }
                                            });*/
                                        }
                                    });
                                }
                                return false;
                            }
                        });

                        no.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View view, MotionEvent motionEvent) {
                                show.dismiss();
                                show.cancel();
                                clicks = 0;
                                return false;
                            }
                        });
                        show.show();

                        show.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                                clicks = 0;
                            }
                        });
                    }
                } else {
                    //TODO If this still displays even after changing the info away from 0, try recalling (in DonationsShelter) the function
                    //  Where you are reading from Firebase and setting the Adapter.
                    clicks++;
                    if (clicks == 1) {
                        final Snackbar snackbar = Snackbar
                                .make(finalListItem, "You have to provide details on when the food is needed and how much will be needed, at the top of the page" +
                                        ".", Snackbar.LENGTH_INDEFINITE)
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
                        clicks = 0;
                        clicks2 = 0;
                        snackbar.show();
                    }
                }
                return true;
            }
        });

        moreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //DONE Write code to show the Alert Dialog.
                Dialog show = new Dialog(mContext);
                show.requestWindowFeature(Window.FEATURE_NO_TITLE);
                show.setContentView(R.layout.request_donation_info);
                show.setCancelable(true);
                show.getWindow().setBackgroundDrawableResource(R.color.transparent);
                //ImageView web, pho;

                TextView title = show.findViewById(R.id.title);
                title.setPaintFlags(title.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                title.setText(row.getName());

                TextView num = show.findViewById(R.id.donationsNum);
                num.setText(row.getNumberofDonations());

                TextView address = show.findViewById(R.id.address);
                address.setText(row.getAddress());

                TextView website = show.findViewById(R.id.website);
                String web = row.getWebsite();
                if (web.contains("https://"))
                    website.setText(web.substring(8, web.length()));
                else if (web.contains("http://"))
                    website.setText(web.substring(7, web.length()));
                else
                    website.setText(web);

                ImageView webImage = show.findViewById(R.id.webImage);
                if (!web.contains("Not ")) {
                    webImage.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(row.getWebsite()));
                            mContext.startActivity(browserIntent);
                            return false;
                        }
                    });
                } else {
                    webImage.setVisibility(View.INVISIBLE);
                }

                TextView manager = show.findViewById(R.id.manager);
                manager.setText(row.getManagerName());

                //DONE IF they are a shelter, get and display the amount of food needed. (ID: amount)

                TextView hours = show.findViewById(R.id.hours);
                String raw = row.getOpeningHours();
                String[] days = raw.split("( ){3}");
                String result = "";
                result += days[0];
                for (int i = 1; i < days.length; i++) {
                    result += "\n" + days[i];
                }
                result.replace("Monday", "Mon");
                result.replace("Tuesday", "Tue");
                result.replace("Wednesday", "Wed");
                result.replace("Thursday", "Thu");
                result.replace("Friday", "Fri");
                result.replace("Saturday", "Sat");
                result.replace("Sunday", "Sun");


                String s = row.getOpeningHours();
                int r = s.indexOf("Tuesday");
                String mon = "Mon:  " + s.substring(8, r - 3);
                int r2 = s.indexOf("Wednesday", r - 3);
                String tue = "Tue:  " + s.substring(r + 9, r2 - 3);
                int r3 = s.indexOf("Thursday", r2 - 3);
                String wed = "Wed:  " + s.substring(r2 + 11, r3 - 3);
                int r4 = s.indexOf("Friday", r3 - 3);
                String thu = "Thu:  " + s.substring(r3 + 10, r4 - 3);
                int r5 = s.indexOf("Saturday", r4 - 3);
                String fri = "Fri:  " + s.substring(r4 + 8, r5 - 3);
                int r6 = s.indexOf("Sunday", r5 - 3);
                String sat = "Sat:  " + s.substring(r5 + 10, r6 - 3);
                String sun = "Sun:  " + s.substring(r6 + 8, row.getOpeningHours().length() - 3);
               /* mon.replace("Monday", "Mon");
                tue.replace("Tuesday", "Tue");
                wed.replace("Wednesday", "Wed");
                thu.replace("Thursday", "Thu");
                fri.replace("Friday", "Fri");
                sat.replace("Saturday", "Sat");
                sun.replace("Sunday", "Sun");*/

                if (mon.contains(",")) {
                    String[] splitter = mon.split(", ");
                    mon = splitter[0];
                    int count = 0;
                    for (String rs : splitter) {
                        if (count != 0)
                            mon += ",\n" + "         " + rs;
                        count++;
                    }
                }
                if (tue.contains(",")) {
                    String[] splitter = tue.split(", ");
                    tue = splitter[0];
                    int count = 0;
                    for (String rs : splitter) {
                        if (count != 0)
                            tue += ",\n" + "         " + rs;
                        count++;
                    }
                }
                if (wed.contains(",")) {
                    String[] splitter = wed.split(", ");
                    wed = splitter[0];
                    int count = 0;
                    for (String rs : splitter) {
                        if (count != 0)
                            wed += ",\n" + "         " + rs;
                        count++;
                    }
                }
                if (thu.contains(",")) {
                    String[] splitter = thu.split(", ");
                    thu = splitter[0];
                    int count = 0;
                    for (String rs : splitter) {
                        if (count != 0)
                            thu += ",\n" + "         " + rs;
                        count++;
                    }
                }
                if (fri.contains(",")) {
                    String[] splitter = fri.split(", ");
                    fri = splitter[0];
                    int count = 0;
                    for (String rs : splitter) {
                        if (count != 0)
                            fri += ",\n" + "         " + rs;
                        count++;
                    }
                }
                if (sat.contains(",")) {
                    String[] splitter = sat.split(", ");
                    sat = splitter[0];
                    int count = 0;
                    for (String rs : splitter) {
                        if (count != 0)
                            sat += ",\n" + "         " + rs;
                        count++;
                    }
                }
                if (sun.contains(",")) {
                    String[] splitter = sun.split(", ");
                    sun = splitter[0];
                    int count = 0;
                    for (String rs : splitter) {
                        if (count != 0)
                            sun += ",\n" + "         " + rs;
                        count++;
                    }
                }

                SpannableString ss = new SpannableString(mon);
                ss.setSpan(new StyleSpan(Typeface.BOLD), 0, 4, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                ss.setSpan(new ForegroundColorSpan(0XFFB67645), 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                SpannableString s2 = new SpannableString(tue);
                s2.setSpan(new StyleSpan(Typeface.BOLD), 0, 4, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                s2.setSpan(new ForegroundColorSpan(0XFFB67645), 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                SpannableString s3 = new SpannableString(wed);
                s3.setSpan(new StyleSpan(Typeface.BOLD), 0, 4, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                s3.setSpan(new ForegroundColorSpan(0XFFB67645), 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                SpannableString s4 = new SpannableString(thu);
                s4.setSpan(new StyleSpan(Typeface.BOLD), 0, 4, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                s4.setSpan(new ForegroundColorSpan(0XFFB67645), 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                SpannableString s5 = new SpannableString(fri);
                s5.setSpan(new StyleSpan(Typeface.BOLD), 0, 4, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                s5.setSpan(new ForegroundColorSpan(0XFFB67645), 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                SpannableString s6 = new SpannableString(sat);
                s6.setSpan(new StyleSpan(Typeface.BOLD), 0, 4, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                s6.setSpan(new ForegroundColorSpan(0XFFB67645), 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                SpannableString s7 = new SpannableString(sun);
                s7.setSpan(new ForegroundColorSpan(0XFFB67645), 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                s7.setSpan(new StyleSpan(Typeface.BOLD), 0, 4, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);

                String m = ss.toString();
                String rf = s2.toString();

                hours.setText(TextUtils.concat(ss, "\n", s2, "\n", s3, "\n", s4, "\n", s5, "\n", s6, "\n", s7));
                Button directions = show.findViewById(R.id.directions);
                directions.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        Uri navigationIntentUri = Uri.parse("google.navigation:q=" + row.getToLat() + "," + row.getToLon());//creating intent with latlng
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, navigationIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        mContext.startActivity(mapIntent);
                        return false;
                    }
                });
                //hours.setText(getHours(row.));

                /*web = show.findViewById(R.id.webIcon);
                pho = show.findViewById(R.id.phoneIcon);

                pho.setOnTouchListener(new View.OnTouchListener() {
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

                web.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(row.getWebsite()));
                        mContext.startActivity(intent);
                        return false;
                    }
                });*/

                TextView distance = show.findViewById(R.id.distance);
                double dif = Double.parseDouble(row.getDis());

                if (row.getUnits().equals("mi")) {
                    dif *= 69;
                } else
                    dif *= 110;

                dif = Math.round(dif * 10) / 10d;
                distance.setText(dif + " " + row.getUnits());

                TextView phone = show.findViewById(R.id.phone);
                if (row.getContactPhone().contains("+1 "))
                    phone.setText(row.getContactPhone().substring(3));
                else
                    phone.setText(row.getContactPhone());

                show.show();
            }
        });

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

        return listItem;
    }

    private void shelter(final RequestDonation row, final Dialog show, final View finalListItem) {
        final DatabaseReference reference
                = FirebaseDatabase.getInstance().getReference("Places");
        //Settings.dismissProcessing2();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (final DataSnapshot child : dataSnapshot.getChildren()) {
                    String VC = child.child("Verification Code").getValue().toString();
                    //Settings.makeTesting(mContext, VC + "\n" + row.getRestaurantVC());
                    HashMap<String, String> hm = new HashMap<>();
                    hm.put("RNum", row.getRestaurantVC());
                    if (VC.equals(row.getShelterVC())) {
                        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Places/" + child.getKey() + "/RNum");
                        if (child.child("RNum") == null) {
                            //reference.child(child.getKey()).child("RNum").setValue(row.getRestaurantVC());
                            dbref.setValue(row.getRestaurantVC()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Settings.dismissProcessing2();
                                }
                            });
                        } else {
                            if (child.child("RNum").getValue().toString().length() < 3) {
                                dbref.setValue(row.getRestaurantVC()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Settings.dismissProcessing2();
                                    }
                                });
                            } else {
                                String temp = child.child("RNum").getValue().toString();
                                dbref.setValue(temp + "h" + row.getRestaurantVC()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Settings.dismissProcessing2();
                                    }
                                });
                            }
                        }
                        show.dismiss();
                        show.cancel();

                        View v = ((Activity) mContext).getWindow().getDecorView().findViewById(android.R.id.content);

                        final Snackbar snackbar = Snackbar
                                .make(((Activity) mContext).getWindow().getDecorView().getRootView(), "Request sent to " + row.getName() + "!", Snackbar.LENGTH_INDEFINITE)
                                .setDuration(2000);

                        //snackbar.getView().setLayoutParams(((CoordinatorLayout.LayoutParams) snackbar.getView().getLayoutParams()).setMargins(7, 5, 7, 30));

                      /*  CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams)
                                snackbar.getView().getLayoutParams();
                        params.setMargins(7, 5, 7, 30);
                        snackbar.getView().setLayoutParams(params);*/

                        TextView snackTextView = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
                        snackTextView.setMaxLines(5);

                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params.setMargins(10, 10, 10, 90);
                        snackTextView.setLayoutParams(params);
                        snackTextView.setTextColor(0XFFF7E9CE);

                        snackbar.show();
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Settings.dismissProcessing2();
            }
        });

    }

    private void makeToast(String s) {
        Toast.makeText(mContext, s, Toast.LENGTH_LONG).show();
    }

    private String getHours(String openingHours) {
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
}
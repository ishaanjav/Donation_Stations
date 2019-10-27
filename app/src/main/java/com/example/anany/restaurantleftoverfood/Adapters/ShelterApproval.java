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
import android.graphics.Typeface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
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

public class ShelterApproval extends ArrayAdapter<DonationsList> {
    private Context mContext;
    private List<DonationsList> list = new ArrayList<>();
    Button request, moreInfo;
    ImageView call, copy;

    public ShelterApproval(@NonNull Context context, ArrayList<DonationsList> list) {
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
        listItem = LayoutInflater.from(mContext).inflate(R.layout.shelter_needs_accept, parent, false);
        clicks = 0;
        clicks2 = 0;
        clicks3 = clicks4 = clicks5 = clicks6 = clicks7 = clicks8 = clicks9 = clicks10 = 0;

        String status = row.getStatus();

        TextView name = listItem.findViewById(R.id.shelterName);
        name.setText(row.getName());

        TextView manager = listItem.findViewById(R.id.manager);
        manager.setText(row.getManagerName());

        TextView phone = listItem.findViewById(R.id.phone);
        phone.setText(row.getContactPhone());

        TextView message = listItem.findViewById(R.id.message);
        message.setText(row.getMessage());

        TextView date = listItem.findViewById(R.id.date);
        if (row.getDate().charAt(4) == '0')
            date.setText(row.getTime() + ", " + row.getDate().substring(0, 4) + row.getDate().substring(4));
        else
            date.setText(row.getTime() + ", " + row.getDate());

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

        processShelterApproval(listItem, row);

        return listItem;
    }

    //TODO use below code in ListView 2 for approving and declining.
    private void processShelterApproval(final View listItem, final DonationsList row) {
        Button approve = listItem.findViewById(R.id.approve);
        Button decline = listItem.findViewById(R.id.decline);
        Button moreInfo = listItem.findViewById(R.id.moreInfo);

        approve.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                clicks++;
                if (clicks == 1) {
                    final Dialog show = new Dialog(mContext);
                    show.setContentView(R.layout.ask_sure_cancel);
                    show.setCancelable(true);

                    TextView message = show.findViewById(R.id.message);
                    message.setText("Send a message along with your approval.");
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
                            clicks8++;
                            if (clicks8 == 1) {
                                //TODO In Donations for Restaurant, change the status and add the new message.
                                //TODO Copy Accept for Shelter and write it to Donations.
                                //TODO Status is Pending Pickup.
                                Settings.makeProcessingBrownUncancelable(mContext);
                                removeRNum(row);
                                DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Donations");
                                dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                                            String VC = child.getKey();
                                            if (VC.equals(row.getRestaurantVC())) {
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
                                                            String tempVC = child.child("Shelter VC").getValue().toString();
                                                            if (tempVC.equals(row.getShelterVC()) && status.equals(row.getStatus())) {
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

                                                                dbref.child(child.getKey()).child("Pickup Date").setValue(date);
                                                                dbref.child(child.getKey()).child("Pickup Time").setValue(time);
                                                                dbref.child(child.getKey()).child("MessageS").setValue(messageText);

                                                                final String finalTime = time;
                                                                dbref.child(child.getKey()).child("Status").setValue("Pickup");

                                                                final DatabaseReference Rdbref2 = FirebaseDatabase.getInstance().getReference("Accept/" + row.getShelterVC());
                                                                Rdbref2.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                                                                            String status = child.child("Status").getValue().toString();
                                                                            String tempVC = child.child("Restaurant VC").getValue().toString();
                                                                            Settings.dismissBrownUncancellable();

                                                                            if (tempVC.equals(row.getRestaurantVC()) && status.equals(row.getStatus())) {
                                                                                //INFO Get the values and write them to Donations path under Restaurant's ID.
                                                                                //  Then you can go ahead and delete the child.
                                                                                HashMap<String, String> vals = new HashMap<>();

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
                                                                                vals.put("Cancelled Date", quicker(child, "Cancelled Date"));
                                                                                vals.put("Cancelled Time", quicker(child, "Cancelled Time"));
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

                                                                                vals.put("Status", "Pickup");
                                                                                vals.put("Pickup Date", date);
                                                                                vals.put("Pickup Time", finalTime);
                                                                                //makeToast("VALUES:\n\n" + vals.toString());
                                                                                //DONE Write the values to Donations path under Shelter ID.
                                                                                DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Donations/" + row.getShelterVC());
                                                                                dbref.push().setValue(vals);
                                                                                show.cancel();
                                                                                show.dismiss();

                                                                                //DONE When receival confirmed, cancelled, or declined delete address of restaurant from RNum in Firebase.
                                                                                clicks = 0;
                                                                                Settings.dismissBrownUncancellable();

                                                                                DatabaseReference toRemove = Rdbref2.child(child.getKey());
                                                                                toRemove.removeValue();

                                                                                Dialog done = new Dialog(mContext);
                                                                                done.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                                                done.setContentView(R.layout.ready_for_pickup);
                                                                                done.setCancelable(true);
                                                                                done.getWindow().setBackgroundDrawableResource(R.color.transparent);

                                                                                TextView messageT = done.findViewById(R.id.message);
                                                                                SpannableString text = new SpannableString("Your donation is ready for pickup from " + row.getName() + "!" +
                                                                                        "\nReach out to them to confirm a time.");
                                                                                text.setSpan(new StyleSpan(Typeface.BOLD), 49, row.getName().length() + 40,
                                                                                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                                                messageT.setText(text);
                                                                            /*messageT.setText("Your donation is ready for pickup from " + row.getName() + "!" +
                                                                                    "\nReach out to them to confirm a time.");*/
                                                                                //removeRNum(row);

                                                                                Button directions = done.findViewById(R.id.directions);
                                                                                directions.setOnTouchListener(new View.OnTouchListener() {
                                                                                    @Override
                                                                                    public boolean onTouch(View view, MotionEvent motionEvent) {
                                                                                        Uri navigationIntentUri = Uri.parse("google.navigation:q=" + row.getToLat() + "," + row.getToLon());//creating intent with latlng
                                                                                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, navigationIntentUri);
                                                                                        Settings.dismissBrownUncancellable();
                                                                                        mapIntent.setPackage("com.google.android.apps.maps");
                                                                                        mContext.startActivity(mapIntent);
                                                                                        return false;
                                                                                    }
                                                                                });
                                                                                clicks8 = 0;

                                                                                done.show();
                                                                                break;
                                                                            }

                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                        Settings.dismissBrownUncancellable();
                                                                        clicks8 = 0;
                                                                        clicks = 0;
                                                                        makeToast("Error. Please have a good WiFi connection!");
                                                                    }
                                                                });

                                                            /*.addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    final DatabaseReference Rdbref = FirebaseDatabase.getInstance().getReference("Accept");
                                                                    Rdbref.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                                                                String VC2 = child.getKey();
                                                                                if (VC2.equals(row.getShelterVC())) {
                                                                                    final DatabaseReference Rdbref2 = FirebaseDatabase.getInstance().getReference("Accept/" + VC2);
                                                                                    Rdbref2.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                        @Override
                                                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                                                                                String status = child.child("Status").getValue().toString();
                                                                                                String tempVC = child.child("Restaurant VC").getValue().toString();

                                                                                                if (tempVC.equals(row.getRestaurantVC()) && status.equals(row.getStatus())) {
                                                                                                    //INFO Get the values and write them to Donations path under Restaurant's ID.
                                                                                                    //  Then you can go ahead and delete the child.
                                                                                                    HashMap<String, String> vals = new HashMap<>();
                                                                                                    //vals.putAll(stuff2);
                                                                                                            *//*for (DataSnapshot children : dataSnapshot.getChildren()) {
                                                                                                                for(DataSnapshot more: children.getChildren()) {
                                                                                                                    vals.put(children.getKey(), children.getValue().toString());
                                                                                                                    Settings.makeTesting(mContext, children.getKey());
                                                                                                                }
                                                                                                            }*//*
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
                                                                                                    vals.put("Cancelled Date", quicker(child, "Cancelled Date"));
                                                                                                    vals.put("Cancelled Time", quicker(child, "Cancelled Time"));
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

                                                                                                    vals.put("Status", "Pickup");
                                                                                                    vals.put("Pickup Date", date);
                                                                                                    vals.put("Pickup Time", finalTime);
                                                                                                    //makeToast("VALUES:\n\n" + vals.toString());
                                                                                                    //DONE Write the values to Donations path under Shelter ID.
                                                                                                    DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Donations/" + row.getShelterVC());
                                                                                                    dbref.push().setValue(vals);
                                                                                                    show.cancel();
                                                                                                    show.dismiss();

                                                                                                    //DONE When receival confirmed, cancelled, or declined delete address of restaurant from RNum in Firebase.
                                                                                                    clicks = 0;
                                                                                                    Settings.dismissBrownUncancellable();

                                                                                                    DatabaseReference toRemove = Rdbref2.child(child.getKey());
                                                                                                    toRemove.removeValue();

                                                                                                    Dialog done = new Dialog(mContext);
                                                                                                    done.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                                                                    done.setContentView(R.layout.ready_for_pickup);
                                                                                                    done.setCancelable(true);
                                                                                                    done.getWindow().setBackgroundDrawableResource(R.color.transparent);

                                                                                                    TextView messageT = done.findViewById(R.id.message);
                                                                                                    messageT.setText("Your donation is ready for pickup from " + row.getName() + "!" +
                                                                                                            "\nReach out to them to confirm a time.");

                                                                                                    Button directions = done.findViewById(R.id.directions);
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

                                                                                                    done.show();
                                                                                                    break;
                                                                                                }
                                                                                            }
                                                                                        }

                                                                                        @Override
                                                                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                                            clicks = 0;
                                                                                            makeToast("Error. Please have a good WiFi connection!");
                                                                                        }
                                                                                    });
                                                                                    break;
                                                                                }
                                                                            }
                                                                        }

                                                                        @Override
                                                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                            clicks = 0;
                                                                            makeToast("Error. Please have a good WiFi connection!");
                                                                        }
                                                                    });
                                                                }
                                                            });*/
                                                                clicks8 = 0;
                                                                clicks = 0;
                                                                break;
                                                            }
                                                        }

                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                                        makeToast("Error. Please have a good WiFi connection!");
                                                        Settings.dismissBrownUncancellable();
                                                        clicks8 = 0;
                                                        clicks = 0;
                                                    }
                                                });
                                                break;
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        makeToast("Error. Please have a good WiFi connection!");
                                        clicks8 = 0;
                                        clicks = 0;
                                    }
                                });
                            }

                            return false;
                        }
                    });

                    show.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            clicks8 = 0;
                            clicks = 0;
                        }
                    });
                }
                return false;
            }
        });

        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        decline.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                clicks2++;
                //makeToast("TOUCHED " + clicks2);
                if (clicks2 == 1) {
                    //makeToast("TOUCHED: " + clicks2);
                    final Dialog show = new Dialog(mContext);
                    show.setContentView(R.layout.ask_sure_cancel);
                    show.setCancelable(true);

                    TextView message = show.findViewById(R.id.message);
                    message.setText("Would you like to send a message to let " + row.getName() + " know why you declined their offer?");

                    final EditText messageT = show.findViewById(R.id.custom);
                    show.show();
                    Button yes = show.findViewById(R.id.yes);
                    Button no = show.findViewById(R.id.no);
                    no.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            show.dismiss();
                            show.cancel();
                            clicks2 = 0;
                            return false;
                        }
                    });

                    yes.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            //TODO Handle code for declining request
                            //Settings.makeProcessingBrownUncancelable(mContext);
                            DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Donations");
                            removeRNum(row);
                            dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                                        String VC = child.getKey();
                                        if (VC.equals(row.getRestaurantVC())) {
                                            final String messageText;
                                            final DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Donations/" + VC);
                                            if (messageT.getText().toString() == null || messageT.getText().toString().length() < 1) {
                                                messageText = "None";
                                            } else
                                                messageText = messageT.getText().toString();
                                            Settings.dismissBrownUncancellable();

                                            dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                                                        String status = child.child("Status").getValue().toString();
                                                        String tempVC = child.child("Shelter VC").getValue().toString();
                                                        if (tempVC.equals(row.getShelterVC()) && status.equals(row.getStatus())) {
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

                                                            dbref.child(child.getKey()).child("Decline Date").setValue(date);
                                                            dbref.child(child.getKey()).child("Decline Time").setValue(time);
                                                            dbref.child(child.getKey()).child("MessageS").setValue(messageText);
                                                            Settings.dismissBrownUncancellable();
                                                            show.dismiss();
                                                            show.cancel();
                                                            final String finalTime = time;
                                                            dbref.child(child.getKey()).child("Status").setValue("Declined").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    final DatabaseReference Rdbref = FirebaseDatabase.getInstance().getReference("Accept");
                                                                    Rdbref.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                                                                String VC2 = child.getKey();
                                                                                if (VC2.equals(row.getShelterVC())) {
                                                                                    final DatabaseReference Rdbref2 = FirebaseDatabase.getInstance().getReference("Accept/" + VC2);
                                                                                    Rdbref2.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                        @Override
                                                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                                                                                String status = child.child("Status").getValue().toString();
                                                                                                String tempVC = child.child("Restaurant VC").getValue().toString();

                                                                                                if (tempVC.equals(row.getRestaurantVC()) && status.equals(row.getStatus())) {
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
                                                                                                    vals.put("Pickup Date", quicker(child, "Pickup Date"));
                                                                                                    vals.put("Pickup Time", quicker(child, "Pickup Time"));
                                                                                                    vals.put("Distance", quicker(child, "Distance"));
                                                                                                    vals.put("Done Date", quicker(child, "Done Date"));
                                                                                                    vals.put("Done Time", quicker(child, "Done Time"));
                                                                                                    vals.put("Message InfoR", quicker(child, "Message InfoR"));
                                                                                                    vals.put("Message InfoS", quicker(child, "Message InfoS"));
                                                                                                    vals.put("MessageR", quicker(child, "MessageR"));
                                                                                                    vals.put("MessageS", messageText);
                                                                                                    vals.put("Cancelled Date", quicker(child, "Cancelled Date"));
                                                                                                    vals.put("Cancelled Time", quicker(child, "Cancelled Time"));
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

                                                                                                    vals.put("Status", "Declined");
                                                                                                    vals.put("Decline Date", date);
                                                                                                    vals.put("Decline Time", finalTime);
                                                                                                    //makeToast("VALUES:\n\n" + vals.toString());
                                                                                                    //DONE Write the values to Donations path under Shelter ID.
                                                                                                    DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Donations/" + row.getShelterVC());
                                                                                                    dbref.push().setValue(vals);
                                                                                                    show.cancel();
                                                                                                    show.dismiss();

                                                                                                    //DONE When receival confirmed, cancelled, or declined delete address of restaurant from RNum in Firebase.
                                                                                                    clicks2 = 0;
                                                                                                    Settings.dismissBrownUncancellable();
                                                                                                    Settings.dismissBrownUncancellable();
                                                                                                    Settings.dismissBrownUncancellable();
                                                                                                    Settings.dismissBrownUncancellable();

                                                                                                    //removeRNum(row);
                                                                                                    DatabaseReference toRemove = Rdbref2.child(child.getKey());
                                                                                                    toRemove.removeValue();

                                                                                                    final Snackbar snackbar = Snackbar
                                                                                                            .make(listItem, "The offer from " + row.getName() + " was declined.", Snackbar.LENGTH_INDEFINITE)
                                                                                                            .setDuration(2100);

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
                                                                                            clicks2 = 0;
                                                                                            Settings.dismissBrownUncancellable();
                                                                                            show.dismiss();
                                                                                            show.cancel();
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
                                                                            Settings.dismissBrownUncancellable();
                                                                            show.dismiss();
                                                                            show.cancel();
                                                                            makeToast("Error. Please have a good WiFi connection!");
                                                                        }
                                                                    });
                                                                }
                                                            });
                                                            show.dismiss();
                                                            show.cancel();
                                                            Settings.dismissBrownUncancellable();
                                                            clicks2 = 0;
                                                            break;
                                                        }
                                                    }

                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                    Settings.dismissBrownUncancellable();
                                                    show.dismiss();
                                                    show.cancel();
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
                                    Settings.dismissBrownUncancellable();
                                    show.dismiss();
                                    show.cancel();
                                    makeToast("Error. Please have a good WiFi connection!");
                                    clicks2 = 0;
                                }
                            });

                            return false;
                        }
                    });

                    show.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            Settings.dismissBrownUncancellable();
                            show.dismiss();
                            show.cancel();
                            clicks2 = 0;
                        }
                    });
                }
                return false;
            }
        });

        moreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog show = new Dialog(mContext);
                show.requestWindowFeature(Window.FEATURE_NO_TITLE);
                show.setContentView(R.layout.request_donation_info);
                show.setCancelable(true);
                show.getWindow().setBackgroundDrawableResource(R.color.transparent);

                handleMoreInfo(show, row);
                show.show();
            }
        });

        /*moreInfo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                clicks3++;
                if (clicks3 == 1) {
                    Dialog show = new Dialog(mContext);
                    show.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    show.setContentView(R.layout.request_donation_info);
                    show.setCancelable(true);
                    show.getWindow().setBackgroundDrawableResource(R.color.transparent);

                    handleMoreInfo(show, row);

                    show.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            clicks3 = 0;
                        }
                    });
                }

                return false;
            }
        });*/
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


    private String quicker(DataSnapshot child, String s) {
        return child.child(s).getValue().toString();
    }

    private void handleMoreInfo(Dialog show, final DonationsList row) {
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

    }


    private void makeToast(String s) {
        Toast.makeText(mContext, s, Toast.LENGTH_LONG).show();
    }
}

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

public class RDonationsListAdapter extends ArrayAdapter<DonationsList> {
    private Context mContext;
    private List<DonationsList> list = new ArrayList<>();
    Button request, moreInfo;
    ImageView call, copy;

    public RDonationsListAdapter(@NonNull Context context, ArrayList<DonationsList> list) {
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
        listItem = LayoutInflater.from(mContext).inflate(R.layout.restaurant_pending_approval, parent, false);
        clicks = 0;
        clicks2 = 0;
        clicks3 = clicks4 = clicks5 = clicks6 = clicks7 = clicks8 = clicks9 = clicks10 = 0;
        String status = row.getStatus();

        if (status.contains("Shelter Approval")) {
            processShelterApproval(listItem, row);
            TextView manager = listItem.findViewById(R.id.manager);
            manager.setText(row.getManagerName());
        } else if (status.contains("Pickup")) {
            listItem = LayoutInflater.from(mContext).inflate(R.layout.restaurant_pending_pickup, parent, false);
            TextView manager = listItem.findViewById(R.id.manager);
            manager.setText(row.getManagerName());
        } else if (status.contains("Done")) {
            listItem = LayoutInflater.from(mContext).inflate(R.layout.restaurant_done_pickup, parent, false);
            TextView manager = listItem.findViewById(R.id.manager);
            manager.setText(row.getManagerName());
        } else if (status.contains("Cancel")) {
            listItem = LayoutInflater.from(mContext).inflate(R.layout.shelter_cancelled, parent, false);
        } else if (status.contains("Decline")) {
            listItem = LayoutInflater.from(mContext).inflate(R.layout.shelter_declined, parent, false);
        }

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

        TextView date = listItem.findViewById(R.id.date);
        date.setText(row.getTime() + ", " + row.getDate());
        return listItem;
    }

    private void processShelterApproval(View listItem, final DonationsList row) {
        Button cancel = listItem.findViewById(R.id.cancel);
        final TextView title = listItem.findViewById(R.id.shelterName);
        final TextView message = listItem.findViewById(R.id.message);
        //message.setText("HI OUTSIDE");
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
                        message.setText("Do you want to cancel your offer to " + row.getName() + "? They haven't approved or declined yet.");
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
                                //TODO Have to process cancellation. Change status for Restaurant in Donations to Cancelled.
                                //  For shelter, go to Accept/Approve and delete it from there and write to Donations under shelter VC
                                //  Saying that it was cancelled. Find a way in 2nd ListView to display text saying latest instance was cancelled.
                                clicks2++;
                                if (clicks2 == 1) {
                                    //DONE Have to process cancellation. Change status for Shelter in Donations to Cancelled.
                                    //DONE  For restaurant, go to Accept/Approve and delete it from there and write to Donations under shelter VC
                                    //DONE  Saying that it was cancelled. Find a way in 2nd ListView to display text saying latest instance was cancelled.
                                    //makeToast("Have to change stuff in paths.");
                                    Settings.makeProcessingBrownUncancelable(mContext);
                                    //Settings.makeTesting(mContext, "HI outside");
                                    removeRNum(row);
                                    DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Donations");
                                    dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            /*Settings.makeTesting(mContext, "Listener");
                                            Settings.makeTesting(mContext,
                                                    dataSnapshot.getChildren().toString());
                                            Settings.makeTesting(mContext, row.getShelterVC());*/

                                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                                String VC = child.getKey();
                                                //Settings.makeTesting(mContext, row.getShelterVC());
                                                if (VC.equals(row.getShelterVC())) {
                                                    final String messageText;
                                                    final DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Donations/" + VC);
                                                    if (messageT.getText().toString() == null || messageT.getText().toString().length() < 1) {
                                                        messageText = "None";
                                                    } else
                                                        messageText = messageT.getText().toString();

                                                    //Settings.makeTesting(mContext, "HI");
                                                    dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                                                String status = child.child("Status").getValue().toString();
                                                                //Settings.makeTesting(mContext, "HI Hello");
                                                                String tempVC = child.child("Shelter VC").getValue().toString();
                                                                if (tempVC.equals(row.getRestaurantVC()) && status.equals(row.getStatus())) {
                                                                /*DatabaseReference dbref2 = child.getRef();
                                                                dbref2.push().setValue();*/
                                                                    //Settings.makeTesting(mContext, "HI INSIDE");
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
                                                                    dbref.child(child.getKey()).child("MessageR").setValue(messageText);

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
                                                                                                        String tempVC = child.child("Restaurant VC").getValue().toString();

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
                                                                                                            vals.put("MessageS", quicker(child, "MessageS"));
                                                                                                            vals.put("MessageR", messageText);
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
                                                                                                            //removeRNum(row);
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
                            }
                        });
                    }
                    return false;
                }
            });
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


    private String quicker(DataSnapshot child, String s) {
        return child.child(s).getValue().toString();
    }

    private void makeToast(String s) {
        Toast.makeText(mContext, s, Toast.LENGTH_LONG).show();
    }
}

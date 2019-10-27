package com.example.anany.restaurantleftoverfood;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.HashMap;
import java.util.Set;

public class Settings extends AppCompatActivity {

    TextView type1, type2;
    ImageView type1H, type2H, listH, mapH, searchH;
    Switch s1, s2;

    Spinner units;
    AutoCompleteTextView distance;
    String type;

    CheckBox list, map;

    Button finish;
    Bundle previous;
    String address, manager, name, username, password, website, phone, hours;
    TextView text;
    int clicks = 0;
    Dialog alert;
    double lati, lon;
    String rating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Intent t = getIntent();
        type = t.getStringExtra("Type");
        name = t.getStringExtra("Name");
        password = t.getStringExtra("Password");
        username = t.getStringExtra("Username");
        manager = t.getStringExtra("Manager");
        address = t.getStringExtra("Address");
        website = t.getStringExtra("Website");
        phone = t.getStringExtra("Phone");
        hours = t.getStringExtra("Hours");
        lati = t.getDoubleExtra("Lat", 0);
        lon = t.getDoubleExtra("Long", 0);
        rating = t.getStringExtra("Rating");

        text = findViewById(R.id.showOther);
        previous = t.getExtras();
        findViews();
        list.setChecked(true);
        map.setChecked(true);
        //DONE for the Settings page. Say one last thing. Would you like to modify the following settings? If they click no in the Alert
        //DONE dialog, then take them to verification page. If they click yes let them modify.

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //DONE do a check and see if everything is good.
                if (clicks == 0)
                    checkInfo();
                else
                    makeToast("Processing...");
                //clicks++;
            }
        });


        s1.setChecked(true);
        if (type.equals("Restaurant")) {
            type1.setText("Shelters");
            type2.setText("Other Restaurants");
            text.setText("Show other restaurants in:");
        } else {
            type1.setText("Restaurants");
            type2.setText("Other Shelters");
            text.setText("Show other shelters in:");
        }
        helpButtons();
        distance.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 3) {
                    int max = 300;
                    if (units.getSelectedItem().toString().equals("km")) {
                        max = 475;
                    }
                    makeToast("Maximum distance allowed is " + max + " " + units.getSelectedItem().toString() + ".");
                    distance.setText(s.toString().substring(0, s.length() - 1));
                    distance.setSelection(distance.getText().toString().length());
                } else if (s.length() > 0) {
                    int t = Integer.parseInt(s.toString());
                    int max = 300;
                    if (units.getSelectedItem().toString().equals("km")) {
                        max = 475;
                    }
                    if (t > max) {
                        makeToast("Maximum distance allowed is " + max + " " + units.getSelectedItem().toString() + ".");
                        distance.setText(s.toString().substring(0, s.length() - 1));
                        distance.setSelection(distance.getText().toString().length());
                    }

                }
            }
        });

        makeAlertInfo();
    }

    int val = 0;

    private void checkInfo() {
        //DONE - Get and remove 1st verification code from Firebase.
        //DONE Save username, verification code, and address in userAddress in Firebase
        //DONE Make an empty file called verifying
        //DONE Save info.txt
        //DONE Save in Firebase under "toVerify"
        //OLD Send automated voice call to 8482482353
        boolean good = true;
        if (units.getSelectedItem().toString().equals("mi")) {
            if (Integer.parseInt(distance.getText().toString()) < 5) {
                makeToast("The minimum distance is 5 miles.");
                good = false;
            }
        } else {
            if (Integer.parseInt(distance.getText().toString()) < 8) {
                makeToast("The minimum distance is 8 kilometers.");
                good = false;
            }
        }
        if (good) {
            boolean discover1 = s1.isChecked();
            boolean discover2 = s2.isChecked();
            boolean inList = list.isChecked();
            boolean inMap = map.isChecked();
            int dis = 30;
            if ((!distance.getText().toString().isEmpty()) && distance.getText() != null && distance.getText().length() > 0)
                dis = Integer.parseInt(distance.getText().toString());
            else
                distance.setText("30");

            String unit = units.getSelectedItem().toString();
            double latLong = 0;
            if (unit.equals("mi"))
                latLong = ((double) dis) / 69;
            else
                latLong = ((double) dis) / 111;
            latLong = (double) Math.round(latLong * 100000000) / 100000000d;
            //makeToast("Distance: " + latLong);

            int verificationCode = getVerificationCode(latLong, inList, inMap);

        }
    }

    private void saveInfoTxt(String info) {
        OutputStreamWriter outputStreamWriter;
        try {
            outputStreamWriter = new OutputStreamWriter(getApplicationContext().openFileOutput("info.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(info);
            outputStreamWriter.close();
        } catch (Exception e) {
            makeToast(e.toString());
        }

    }

    private void savePlaces(int code, double lat) {
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Places");
        HashMap<String, String> hashMap = new HashMap<>();

        hashMap.put("Type", type);
        hashMap.put("Name", name);
        hashMap.put("Address", address);
        hashMap.put("Username", username);
        hashMap.put("Password", password);
        hashMap.put("Manager Name", manager);
        hashMap.put("Phone", phone);
        hashMap.put("Website", website);
        hashMap.put("Hours", hours);
        hashMap.put("Rating", rating);
        hashMap.put("RNum", "hi");
        if (type.contains("elter")) {
            hashMap.put("Discoverable to Shelter", s2.isChecked() + "");
            hashMap.put("Discoverable to Restaurant", s1.isChecked() + "");
            hashMap.put("Food Status", "Not Needed");
            hashMap.put("Amount", "None");
        } else {
            hashMap.put("Discoverable to Shelter", s1.isChecked() + "");
            hashMap.put("Discoverable to Restaurant", s2.isChecked() + "");
            hashMap.put("Num of Donations", "0");
            hashMap.put("Donation Points", "0");
        }
        hashMap.put("Latitude", lati + "");
        hashMap.put("Longitude", lon + "");
        hashMap.put("Units", units.getSelectedItem().toString());
        hashMap.put("Distance", distance.getText().toString());
        hashMap.put("Others in List", list.isChecked() + "");
        hashMap.put("Others in Map", map.isChecked() + "");
        hashMap.put("Search Radius", lat + "");
        hashMap.put("Verification Code", "" + code);

        dbref.push().setValue(hashMap);
    }

    private void goToVerificationPage(int code, double lat) {
        Intent intent = new Intent(Settings.this, MainActivity.class);
        //OLD Don't send data in intent. Save it in a file called tempData.txt.
        intent.putExtra("Code", code);
        intent.putExtra("Name", name);
        intent.putExtra("Username", username);
        intent.putExtra("Password", password);
        alert.dismiss();
        alert.cancel();
        /*intent.putExtra("Type", type);
        intent.putExtra("Name", name);
        intent.putExtra("Address", address);
        intent.putExtra("Username", username);
        intent.putExtra("Password", password);
        intent.putExtra("Manager Name", manager);
        intent.putExtra("Phone", phone);
        intent.putExtra("Website", website);
        intent.putExtra("Hours", hours);
        if (type.contains("elter")) {
            intent.putExtra("Discoverable to Shelter", s2.isChecked() + "");
            intent.putExtra("Discoverable to Restaurant", s1.isChecked() + "");
        } else {
            intent.putExtra("Discoverable to Shelter", s1.isChecked() + "");
            intent.putExtra("Discoverable to Restaurant", s2.isChecked() + "");
        }
        intent.putExtra("Others in List", list.isChecked() + "");
        intent.putExtra("Others in Map", map.isChecked() + "");
        intent.putExtra("Search Radius", lat + "");
        intent.putExtra("Verification Code", "" + code);*/
        /*String save = type+"\n"+name+"\n"+address+"\n"+username+"\n"+password+"\n"+manager+"\n"+phone+"\n";
        save += website+"\n"+hours+"\n"+ list.isChecked()+"\n" + map.isChecked()+"\n"+ lat+"\n"+code+"\n";
        if (type.contains("elter")) {
            save+= s2.isChecked() + "\n" + s1.isChecked();
        } else {
            save+= s1.isChecked() + "\n" + s2.isChecked();
        }
        OutputStreamWriter outputStreamWriter;
        try {
            outputStreamWriter = new OutputStreamWriter(getApplicationContext().openFileOutput("tempData.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(save);
            outputStreamWriter.close();
        } catch (Exception e) {
            makeToast(e.toString());
        }*/

        startActivity(intent);
    }


    private void makeFiletoVerifying(int code) {
        OutputStreamWriter outputStreamWriter;
        try {
            outputStreamWriter = new OutputStreamWriter(getApplicationContext().openFileOutput("verifying.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write("" + code);
            outputStreamWriter.close();
        } catch (Exception e) {
            makeToast(e.toString());
        }
    }

    private void saveFirebaseuserAddress(int code) {
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("userAddress");
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("Username", username);
        hashMap.put("Address", address);
        hashMap.put("Verification Code", code + "");
        dbref.push().setValue(hashMap);
    }

    @Override
    public void onBackPressed() {
        makeWarning();
        //super.onBackPressed();
    }

    private void makeWarning() {
        final Dialog alert = new Dialog(Settings.this);
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert.setContentView(R.layout.are_you_sure);
        alert.setCancelable(false);
        alert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        Button close = alert.findViewById(R.id.continueN);
        Button stay = alert.findViewById(R.id.stay);
        TextView message = alert.findViewById(R.id.message);
        message.setText("If you go back, none of your information will be saved and you will have to restart. Are you sure you want to do this?");
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
                alert.cancel();
                startActivity(new Intent(Settings.this, InfoPages.class));
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

    static Dialog tester;

    public static void makeTesting(Context c, String s) {
        tester = new Dialog(c);
        tester.requestWindowFeature(Window.FEATURE_NO_TITLE);
        tester.setContentView(R.layout.saving);
        tester.setCancelable(true);

        TextView saver = tester.findViewById(R.id.saver);
        saver.setText(s);
        //alert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        tester.show();
    }

    public static void dismissTester() {
        tester.dismiss();
        tester.cancel();
    }

    static Dialog special;

    public static void makeLoading(Context c) {
        special = new Dialog(c);
        special.requestWindowFeature(Window.FEATURE_NO_TITLE);
        special.setContentView(R.layout.saving);
        special.setCancelable(false);
        //alert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        special.show();
    }

    public static void dismiss() {
        special.dismiss();
        special.cancel();
    }

    static Dialog special4;

    public static void makeSavingBrown(Context c) {
        special4 = new Dialog(c);
        special4.requestWindowFeature(Window.FEATURE_NO_TITLE);
        special4.setContentView(R.layout.saving2);
        special4.setCancelable(false);

        //alert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        special4.show();
    }

    public static void dismissSavingBrown() {
        if(special4 != null) {
            special4.dismiss();
            special4.cancel();
        }
    }

    static Dialog special2;

    public static void makeVerifying(Context c) {
        special2 = new Dialog(c);
        special2.requestWindowFeature(Window.FEATURE_NO_TITLE);
        special2.setContentView(R.layout.verfiying);
        special2.setCancelable(false);

        //alert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        special2.show();
    }

    public static void dismissVerifying() {
        if(special2 != null) {
            special2.dismiss();
            special2.cancel();
        }
    }

    static Dialog special3;

    public static void makeProcessing(Context c) {
        special3 = new Dialog(c);
        special3.requestWindowFeature(Window.FEATURE_NO_TITLE);
        special3.setContentView(R.layout.progress);
        special3.setCancelable(false);

        //alert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        special3.show();
    }

    public static void dismissProcessing() {
        if(special3 != null) {
            special3.dismiss();
            special3.cancel();
        }
    }

    static Dialog special5;

    public static void makeProcessing2(Context c) {
        special5 = new Dialog(c);
        special5.requestWindowFeature(Window.FEATURE_NO_TITLE);
        special5.setContentView(R.layout.progress2);
        special5.setCancelable(true);

        //alert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        special5.show();
    }

    public static void dismissProcessing2() {
        if(special5 != null) {
            special5.dismiss();
            special5.cancel();
        }
    }

    static Dialog special6;

    public static void makeProcessingBrownUncancelable(Context c) {
        special6 = new Dialog(c);
        special6.requestWindowFeature(Window.FEATURE_NO_TITLE);
        special6.setContentView(R.layout.progress2);
        special6.setCancelable(true);

        //alert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        special6.show();
    }

    public static void dismissBrownUncancellable() {
        if(special6 != null) {
            special6.dismiss();
            special6.cancel();
        }
    }

    static Dialog special7;

    public static void makeProcessingBrownUncancelable2(Context c, int i) {
        if (i == 1) {
            special7 = new Dialog(c);
            special7.requestWindowFeature(Window.FEATURE_NO_TITLE);
            special7.setContentView(R.layout.progress2);
            special7.setCancelable(true);

            //alert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            special7.show();
        }
    }

    public static void dismissBrownUncancellabl2() {
        special7.dismiss();
        special7.cancel();
    }


    public int getVerificationCode(final double latLong, final boolean inList, final boolean inMap) {
        /*final ProgressDialog progressDialog = new ProgressDialog(getApplicationContext());
        progressDialog.setMessage("Processing...");
        progressDialog.show();*/
        alert = new Dialog(Settings.this);
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert.setContentView(R.layout.progress);
        alert.setCancelable(false);

        //alert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alert.show();
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Codes");
        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> ds = dataSnapshot.getChildren();
                DataSnapshot first = ds.iterator().next();
                val = Integer.parseInt(first.getValue().toString());
                OutputStreamWriter outputStreamWriter = null;
                try {
                    outputStreamWriter = new OutputStreamWriter(getApplicationContext().openFileOutput("num.txt", Context.MODE_PRIVATE));
                    outputStreamWriter.write("" + val);
                    outputStreamWriter.close();
                } catch (Exception e) {
                    makeToast(e.toString());
                }
                String info = type + "\n" + address + "\n" + latLong + "\n" + inList + "\n" + inMap + "\n" + val;

                //makeFiletoVerifying(verificationCode);
                saveFirebaseuserAddress(val);
                saveInfoTxt(info);
                savePlaces(val, latLong);
                saveFirstTime();

                //makeToast("INFO.txt: " + info);

                //makeToast("VALUE: " + val);
                goToVerificationPage(val, latLong);

                first.getRef().removeValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                makeToast("There was an error. Please try again");
                alert.dismiss();
                //progressDialog.dismiss();
            }
        });
        /*try {
            InputStream inputStream = getApplicationContext().openFileInput("num.txt");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                val = Integer.parseInt(stringBuilder.toString());
            }
        } catch (Exception e) {
            makeToast(e.toString());
        }*/

        return 0;
    }

    private void saveFirstTime() {
        OutputStreamWriter outputStreamWriter;
        try {
            outputStreamWriter = new OutputStreamWriter(getApplicationContext().openFileOutput("firstTime.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write("Hello. File exists.");
            outputStreamWriter.close();
        } catch (Exception e) {
            makeToast(e.toString());
        }
    }

    private void makeAlertInfo() {
        final Dialog alert = new Dialog(Settings.this);
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert.setContentView(R.layout.change_settings_2);
        alert.setCancelable(false);

        Window window = alert.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        window.setAttributes(wlp);

        ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
        InsetDrawable inset = new InsetDrawable(back, 5);
        alert.getWindow().setBackgroundDrawable(inset);

        TextView message = alert.findViewById(R.id.message);
        message.setText("Would you like to modify the above settings now? You can always change them later.");

        Button yes = alert.findViewById(R.id.yes);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clicks == 0) {
                    alert.dismiss();
                    alert.cancel();
                } else
                    makeToast("Processing...");
            }
        });

        Button no = alert.findViewById(R.id.no);
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //DONE Go to Verification page, But first save all the data and send the automated voice call.
                if (clicks == 0)
                    checkInfo();
                else
                    makeToast("Processing...");
                clicks++;
            }
        });

        alert.show();
    }

    private void helpButtons() {
        type1H.setOnTouchListener(info("(RECOMMENDED). This is so that " + (type.equals("Restaurant") ? "shelters using this app can see that you are willing to donate food." : "restaurants using this app can see that you are accepting food donations."), 6000));
        type2H.setOnTouchListener(info("(RECOMMENDED). This is so that " + (type.equals("Shelter") ? "other shelters using this app can see that you are also using it." : "restaurants using this app can see that you are also using it."), 5000));
        listH.setOnTouchListener(info("This is so that on the homepage, " + (type.equals("Shelter") ? "you can see a list of other shelters also using this app." : "you can see a list of other restaurants also using this app."), 4100));
        mapH.setOnTouchListener(info("This is so that on the map, " + (type.equals("Shelter") ? "you can see the locations of other shelters using this app in the area." : "you can see the locations of other restaurants using this app in the area."), 3500));
        searchH.setOnTouchListener(info("This is the farthest distance a " + (type.equals("Shelter") ? "restaurant " : "shelter ") + "can be from you for you to see them in your list.", 5000));
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

    public void findViews() {
        type1 = findViewById(R.id.text1);
        type2 = findViewById(R.id.text2);
        type1H = findViewById(R.id.typeHelp);
        type2H = findViewById(R.id.typeHelp2);
        s1 = findViewById(R.id.switch1);
        units = findViewById(R.id.units);
        listH = findViewById(R.id.listHelp);
        mapH = findViewById(R.id.mapHelp);
        searchH = findViewById(R.id.searchHelp);
        list = findViewById(R.id.check1);
        map = findViewById(R.id.check2);
        s2 = findViewById(R.id.switch2);
        finish = findViewById(R.id.finish);
        distance = findViewById(R.id.distance);
    }

    @Override
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
        final Dialog alert = new Dialog(Settings.this);
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert.setContentView(R.layout.about_this_app);
        alert.setCancelable(true);

        alert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        alert.show();
    }

    public void makeToast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }
}

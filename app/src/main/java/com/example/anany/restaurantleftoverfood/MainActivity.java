package com.example.anany.restaurantleftoverfood;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anany.restaurantleftoverfood.Adapters.SpinnerAdapter;
import com.github.jinatonic.confetti.CommonConfetti;
import com.github.jinatonic.confetti.ConfettiManager;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
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
import java.util.Collections;
import java.util.List;
import java.util.Random;

import tourguide.tourguide.Overlay;
import tourguide.tourguide.ToolTip;
import tourguide.tourguide.TourGuide;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    ScrollView rel;
    Button signup;
    Button login;
    EditText username, password;
    TextView forgotU, forgotP, error, forgotV;

    private Boolean mAllowSelectionFiring = false;
    //For ignoring first time onItemSelected Event Firing
    Spinner itemList;
    String forgotChoice;
    boolean firstLaunch;
    TourGuide mTutorialHandler;
    int clicks = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //rel = findViewById(R.id.rel22);
        //INFO check for following files
        //DONE Have to check for signedIn.txt - if it exists, then launch Home.
        //DONE When checking login credentials, if correct get address and other relevant info and save it in info.txt.
        //Future Files: When they log out, delete info.txt and signedIn.txt
        //Future Files: When you get to Home, delete firstTime.txt and firstLaunch.txt
        //Future Files: Home, if firstTime exists, then give special info.
        //README:firstTime.txt vs firstLaunch.txt. firstTime is created after they create a new account.
        // // firstLaunch is after they log out. It is displayed to them as if they are a new app user.
        ///README I may only use firstLaunch for the app's firstLaunch. Doesn't make sense to display info every time they log out.
        firstLaunch = false;
        Intent receiveIntent = getIntent();
        Bundle bundle = receiveIntent.getExtras();
        if (bundle != null) {
            if (bundle.size() > 3)
                makeWelcomeDialog(receiveIntent.getStringExtra("Username"), receiveIntent.getStringExtra("Password"), receiveIntent.getStringExtra("Code"));
        }

        Places.initialize(getApplicationContext(), "AIzaSyAAuJitQF8VFy5vxkFcTbsW2TmS8y7J4js");

        try {
            String temp = readFromFile(getApplicationContext(), "signedIn.txt");
            if (temp.isEmpty() || temp.length() < 5) {
                //File does not exist.
                /*makeToast("TEMP : " + temp);
                startActivity(new Intent(getApplicationContext(), VerificationPage.class));*/
                //INFO Deciding whether to show them app info if it is their first launch or not.
                //makeToast("verifying.txt Does not exist");
                try {
                    String temp2 = "";
                    temp2 = readFromFile(getApplicationContext(), "firstLaunch.txt");
                    if (temp2.isEmpty() || temp2.length() < 5) {
                        //README It is their first launch
                        firstLaunch = true;
                        showAppInfo2();
                        OutputStreamWriter outputStreamWriter;
                        try {
                            outputStreamWriter = new OutputStreamWriter(getApplicationContext().openFileOutput("firstLaunch.txt", Context.MODE_PRIVATE));
                            outputStreamWriter.write("Hello There");
                            outputStreamWriter.close();
                        } catch (Exception e) {
                            makeToast(e.toString());
                        }
                    } else if (temp2.length() > 5) {
                        //README File exists. Only show it when it does not exist.
                    }
                } catch (IOException e) {
                    makeToast(e.toString());
                }
            } else if (temp.length() > 5) {
                //README signedIn exists.
                startActivity(new Intent(getApplicationContext(), Home.class));

            }
        } catch (IOException e) {
            makeToast(e.toString());
        }

        login = findViewById(R.id.login);
        signup = findViewById(R.id.signUp);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        /*forgotP = findViewById(R.id.forgotP);
        forgotV = findViewById(R.id.forgotVC);*/
        forgotU = findViewById(R.id.forgotU);
        error = findViewById(R.id.error);

        //DONE Make Alert Dialogs for when they click forgot stuff.
        forgotInfo();
        signUpClicked();

        //startActivity(new Intent(MainActivity.this, Home.class));

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkInfo();
                if (mTutorialHandler != null)
                    mTutorialHandler.cleanUp();

                /*final ConfettiManager c = CommonConfetti.explosion(screen, 3 * screen.getWidth() / 16, screen.getHeight() / 2 - 650,
                        new int[]{0XFF7fed64, 0XFF78fade, 0XFFFF8400, 0XFFFFE600, 0XFF1EBA47, 0XFF4359DE, 0XFFCE5FED, 0XFFFF85DE, 0XFFF00000})
                        .infinite().setEmissionRate(130);*/
                //startActivity(new Intent(MainActivity.this, Home.class));
                /*CommonConfetti.rainingConfetti(container, new int[]{0XFF7fed64,0XFF78fade,0XFFFF8400, 0XFFFFE600, 0XFF1EBA47, 0XFF4359DE, 0XFFCE5FED, 0XFFFF85DE, 0XFFF00000})
                        .infinite().setEmissionDuration(1300).setEmissionRate(1000).setVelocityY(1200);*/

                //makeWelcomeDialog("HI", "NO", "YE");

            }
        });

        ArrayList<String> spinnerAdapterData = new ArrayList<>();
        String[] spinnerItemsArray = getResources().getStringArray(R.array.forgot);
        Collections.addAll(spinnerAdapterData, spinnerItemsArray);
        SpinnerAdapter adapter = new SpinnerAdapter(this, R.layout.forgot_spinner, spinnerAdapterData, getResources());

        itemList = (Spinner) findViewById(R.id.spinner);
        itemList.setAdapter(adapter);

        itemList.setOnItemSelectedListener(this);

        forgotChoice = itemList.getSelectedItem().toString();

        //generateVerificationCodes();

    }

    ConfettiManager c, c2, c3;

    private void makeWelcomeDialog(String username, String password, String code) {
        try {
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
                int val = Integer.parseInt(stringBuilder.toString());

                final Dialog alert = new Dialog(MainActivity.this);
                alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
                alert.setContentView(R.layout.congrats);
                alert.setCancelable(false);
                alert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                final RelativeLayout container = alert.findViewById(R.id.confettiH);
                ViewTreeObserver viewTreeObserver = container.getViewTreeObserver();
                container.post(new Runnable() {
                    @Override
                    public void run() {
                        RelativeLayout screen = findViewById(R.id.rel22);

                        c = CommonConfetti.explosion(container, 3 * screen.getWidth() / 16, screen.getHeight() / 2 - 650,
                                new int[]{0XFF7fed64, 0XFF78fade, 0XFFFF8400, 0XFFFFE600, 0XFF1EBA47, 0XFF4359DE, 0XFFCE5FED, 0XFFFF85DE, 0XFFF00000})
                                .infinite().setEmissionRate(130);

                        c2 = CommonConfetti.explosion(container, 11 * screen.getWidth() / 16, screen.getHeight() / 2 - 650,
                                new int[]{0XFF7fed64, 0XFF78fade, 0XFFFF8400, 0XFFFFE600, 0XFF1EBA47, 0XFF4359DE, 0XFFCE5FED, 0XFFFF85DE, 0XFFF00000})
                                .infinite().setEmissionRate(130);


                        c3 = CommonConfetti.rainingConfetti(screen, new int[]{0XFF7fed64, 0XFF78fade, 0XFFFF8400, 0XFFFFE600, 0XFF1EBA47, 0XFF4359DE, 0XFFCE5FED, 0XFFFF85DE, 0XFFF00000})
                                .infinite().setEmissionRate(200).setVelocityY(800);

                    }
                });



               /* CommonConfetti.rainingConfetti(container, new int[]{0XFF7fed64,0XFF78fade,0XFFFF8400, 0XFFFFE600, 0XFF1EBA47, 0XFF4359DE, 0XFFCE5FED, 0XFFFF85DE, 0XFFF00000})
                        .infinite().setEmissionDuration(1300).setEmissionRate(1000).setVelocityY(1200);*/

                Button ok = alert.findViewById(R.id.close);

                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alert.dismiss();
                        alert.cancel();
                        c.terminate();
                        c2.terminate();
                        c3.terminate();
                        mTutorialHandler = TourGuide.init(MainActivity.this).with(TourGuide.Technique.CLICK);
                        mTutorialHandler.setToolTip(new ToolTip()
                                .setTitle("Good to Go!")
                                .setDescription("Sign in to your account!")
                                .setGravity(Gravity.BOTTOM)
                                .setBackgroundColor(0xFF48A837)
                        );
                        Overlay r = new Overlay();
                        r.setBackgroundColor(0x99000000);
                        r.disableClickThroughHole(false);
                        r.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mTutorialHandler.cleanUp();
                            }
                        });
                        mTutorialHandler.setOverlay(r);
                        mTutorialHandler.playOn(login);
                    }
                });

                TextView info = alert.findViewById(R.id.info);
                String message = "Username: " + username + "\nPassword: " + password + "\n*Verification Code: " + val;
                SpannableString ss = new SpannableString(message);
                ss.setSpan(new StyleSpan(Typeface.BOLD), 0, 10, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                ss.setSpan(new StyleSpan(Typeface.BOLD), message.indexOf("Pass"), message.indexOf("Pass") + 10, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                ss.setSpan(new StyleSpan(Typeface.BOLD), message.indexOf("Verifi"), message.indexOf("Verifi") + 18, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                info.setText(ss);

                alert.show();

            }
        } catch (Exception e) {
            RelativeLayout sv = findViewById(R.id.rel22);
            final Snackbar snackbar = Snackbar
                    .make(sv, "Donations Stations ran into an error. Try closing and relaunching the app.", Snackbar.LENGTH_INDEFINITE)
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
            snackTextView.setMaxLines(5);

            snackbar.show();
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if (mAllowSelectionFiring) {
            int selectedItemPosition = Integer.parseInt(view.getTag(R.string.meta_position).toString().trim());
            String selectedItemTitle = view.getTag(R.string.meta_title).toString().trim();
            forgotChoice = selectedItemTitle;
            Toast.makeText(getApplicationContext(), "Click on \"Forgot Your\" to retrieve your " + selectedItemTitle.substring(0, selectedItemTitle.length() - 1), Toast.LENGTH_LONG).show();
        } else {
            mAllowSelectionFiring = true;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    public void generateVerificationCodes() {
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Codes");
        int[] nums = new int[800000];

        for (int i = 100000; i <= 899999; i++)
            nums[i - 100000] = i;

        Random rnd = new Random();
        for (int i = nums.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            int a = nums[index];
            nums[index] = nums[i];
            nums[i] = a;
        }
        int count = 0;
        for (int i : nums) {
            dbref.child("Key" + count).setValue(i + "");
            count++;
        }

    }

    private void checkInfo() {
        //DONE Check the username and password from Firebase.
        //DONE Read the matching info from Firebase about stuff like searchRadius. (Check data for info.txt)
        //DONE Save info in info.txt and go to Home.
        final String user = username.getText().toString();
        final String pass = password.getText().toString();
        error.setText("");
        if (user.isEmpty()) {
            error.setText("You have not entered a username.");
            snackBarErrorText();
        } else if (pass.isEmpty()) {
            error.setText("You have not entered a password.");
            snackBarErrorText();
        } else {
            final Dialog special2 = new Dialog(MainActivity.this);
            special2.requestWindowFeature(Window.FEATURE_NO_TITLE);
            special2.setContentView(R.layout.verfiying);
            special2.setCancelable(false);
            RelativeLayout rel = findViewById(R.id.rel22);
            final Snackbar snackbar = Snackbar
                    .make(rel, "A strong WiFi connection is recommended, otherwise this may take a while.", Snackbar.LENGTH_INDEFINITE)
                    .setDuration(20000)
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

            //alert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            special2.show();
            DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Places");
            dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    boolean good = false;
                    String type = "", address = "", latLong = "", inList = "", inMap = "", val = "", discoverR = "", discoverS = "";
                    String lat = "", lon = "", units = "";
                    String distance = "";
                    String name = "";
                    String status = "hi";
                    String manager = "";
                    String num = "";
                    String website = "", phone2 = "", hours = "";
                    String donationsNum = "hi", points = "";
                    for (DataSnapshot children : dataSnapshot.getChildren()) {
                        String username = children.child("Username").getValue().toString();
                        String password = children.child("Password").getValue().toString();
                        if (username.equals(user) && password.equals(pass)) {
                            good = true;
                            type = children.child("Type").getValue().toString();
                            address = children.child("Address").getValue().toString();
                            latLong = children.child("Search Radius").getValue().toString();
                            manager = children.child("Manager Name").getValue().toString();
                            inList = children.child("Others in List").getValue().toString();
                            inMap = children.child("Others in Map").getValue().toString();
                            val = children.child("Verification Code").getValue().toString();
                            discoverR = children.child("Discoverable to Restaurant").getValue().toString();
                            discoverS = children.child("Discoverable to Shelter").getValue().toString();
                            lat = children.child("Latitude").getValue().toString();
                            lon = children.child("Longitude").getValue().toString();
                            hours = children.child("Hours").getValue().toString();
                            website = children.child("Website").getValue().toString();
                            phone2 = children.child("Phone").getValue().toString();
                            name = children.child("Name").getValue().toString();
                            distance = children.child("Distance").getValue().toString();
                            units = children.child("Units").getValue().toString();
                            if (type.contains("elt")) {
                                status = children.child("Food Status").getValue().toString();
                                num = children.child("Amount").getValue().toString();
                                if (num.contains("one")) {
                                    num = "0 0";
                                } else if (num.contains("to")) {
                                    String temp = num;
                                    num = temp.split(" ")[0] + " " + temp.split(" ")[2];
                                } else {
                                    String temp = num;
                                    num = temp.split(" ")[0] + " " + temp.split(" ")[0];
                                }

                                if (status.contains("eed")) {
                                    status = "0 0";
                                } else if (status.contains("to")) {
                                    String temp = status;
                                    status = temp.split(" ")[0] + " " + temp.split(" ")[2];
                                } else {
                                    String temp = status;
                                    status = temp.split(" ")[0] + " " + temp.split(" ")[0];
                                }


                            } else {
                                donationsNum = children.child("Num of Donations").getValue().toString();
                                points = children.child("Donation Points").getValue().toString();
                            }
                            break;
                        }
                    }
                    special2.dismiss();
                    //DONE In Adapter, read from info.txt and check whether to disable view in map. Make it gray to disable and on click, show toast saying  you have chosen not to see the type in map.
                    //DONE Show Snackbar when signing in about Wifi connection
                    //DONE Handle hours where there is more than one per day (Spice Thai Cafe)
                    special2.cancel();
                    snackbar.dismiss();
                    if (!good) {
                        error.setText("The credentials you entered are incorrect.");
                        snackBarErrorText();
                    } else {
                        String info = type + "\n" + address + "\n" + latLong + "\n" + lat + "\n" + lon + "\n" + units + "\n" + distance
                                + "\n" + inList + "\n" + inMap + "\n" + discoverR + "\n" + discoverS + "\n" + val + "\n" + name + "\n"
                                + phone2 + "\n" + website + "\n" + manager + "\n" + hours + "\n" + status + "\n" + num;
                        //makeToast(info);
                        OutputStreamWriter outputStreamWriter = null;
                        try {
                            outputStreamWriter = new OutputStreamWriter(getApplicationContext().openFileOutput("info.txt", Context.MODE_PRIVATE));
                            outputStreamWriter.write("" + info);
                            outputStreamWriter.close();
                            outputStreamWriter = new OutputStreamWriter(getApplicationContext().openFileOutput("signedIn.txt", Context.MODE_PRIVATE));
                            outputStreamWriter.write("The user is signed in!");
                            outputStreamWriter.close();
                            outputStreamWriter = new OutputStreamWriter(getApplicationContext().openFileOutput("accountInfo.txt", Context.MODE_PRIVATE));
                            outputStreamWriter.write(type + "\n" + user + "\n" + pass + "\n" + manager + "\n" + address + "\n" + val);
                            outputStreamWriter.close();
                            if (!status.equals("hi")) {
                                outputStreamWriter = new OutputStreamWriter(getApplicationContext().openFileOutput("foodStatus.txt", Context.MODE_PRIVATE));
                                outputStreamWriter.write(status + "\n" + num + "\n" + val);
                                outputStreamWriter.close();
                            } else if (!donationsNum.equals("hi")) {
                                outputStreamWriter = new OutputStreamWriter(getApplicationContext().openFileOutput("donations.txt", Context.MODE_PRIVATE));
                                outputStreamWriter.write(donationsNum + " " + points + "\n" + val);
                                outputStreamWriter.close();
                            }
                            startActivity(new Intent(MainActivity.this, Home.class));
                        } catch (Exception e) {
                            makeToast("ERROR: " + e.toString() + "\nPlease try again.");
                        }
                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    makeToast("There was an error. Please try again");
                    //progressDialog.dismiss();
                }
            });
        }

    }

    private void snackBarErrorText() {
        RelativeLayout sv = findViewById(R.id.rel22);
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

    public void signUpClicked() {
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, InfoPages.class);
                i.putExtra("Info", "first");
                startActivity(i);
            }
        });
    }

    String address;
    String name;
    String phone;

    //README For launching the alertdialogs
    public void forgotInfo() {
        //INFO For all of the below have an AutoCompleteSearchFragment where they enter Restaurant name
        //INFO Also ask for verification codes
        //INFO Display results directly for username and password
        //INFO For forgot Verification Code don't have EditText for verification code.
        forgotU.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (forgotChoice.contains("name")) {
                    makeToast("User");
                    //DONE Write code to retrieve username
                    //INFO Restaurant location and V Code.
                    forgotInfoDialog("u");
                } else if (forgotChoice.contains("word")) {
                    makeToast("Pass");
                    //DONE Write code to retrieve password
                    //INFO Restaurant location and V Code.
                    forgotInfoDialog("p");
                } else {
                    makeToast("Verification");
                    //DONE Write code to retrieve verification code
                    //INFO Restaurant location and password or contact ijapps101@gmail.com.
                    forgotInfoDialogV();
                }
                return false;
            }
        });

    }

    //INFO for verification code
    private void forgotInfoDialogV() {
        final Dialog alert = new Dialog(MainActivity.this);
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert.setContentView(R.layout.forgot_verification);
        alert.setCancelable(true);
        alert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        address = "";
        name = "";
        phone = "";
        final TextView error2 = alert.findViewById(R.id.error);
        error2.setText("");

        Button email = alert.findViewById(R.id.auto);
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                error2.setText("");
                if (address.isEmpty() || address.length() < 5) {
                    error2.setText("Please enter an address or location.");
                } else {
                    checkAddress(alert, error2);
                }

            }
        });
        setUpAutocompleteSearchFragment(alert, error2);

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

    private void checkAddress(final Dialog alert, final TextView error) {
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Places");
        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean good = false;
                String result = "";
                for (DataSnapshot children : dataSnapshot.getChildren()) {
                    String ad = children.child("Address").getValue().toString();
                    if (address.equals(ad)) {
                        good = true;
                        break;
                    }
                }
                if (good) {
                    //DONE Dismiss Alert Dialog and send email..
                    //makeToast("RESULT: " + result);
                    alert.dismiss();
                    alert.cancel();
                    String message = "Dear IJ Apps,\n          We at " + name + " have forgotten or misplaced the verification code for our account. Our address is " + address + ".\n          Would you be able to forward us our verification code?\n\nThank you,\n       " + name + "\n\nContact us at: " + phone;
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:ijapps101@gmail.com"));
                    //emailIntent.setType("text/plain");
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"ijapps101@gmail.com"});
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Forgot My Verification Code");
                    emailIntent.putExtra(Intent.EXTRA_TEXT, message);
                    startActivity(Intent.createChooser(emailIntent, "How do you want to send the mail?"));
                } else {
                    error.setText("The location of this address does not have a registered account.");
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                makeToast("There was an error. Please try again");
                //progressDialog.dismiss();
            }
        });

    }


    //INFO For username and password
    private void forgotInfoDialog(final String i) {
        address = "";
        if (i.equals("u") || i.equals("p")) {
            final Dialog alert = new Dialog(MainActivity.this);
            alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
            alert.setContentView(R.layout.forgot_info);
            alert.setCancelable(true);
            alert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);


            Button close = alert.findViewById(R.id.close);
            Button check = alert.findViewById(R.id.searcher);
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alert.dismiss();
                    alert.cancel();
                }
            });

            final EditText vc = alert.findViewById(R.id.VC);
            final TextView incorrect = alert.findViewById(R.id.error);
            incorrect.setText("");

            setUpAutocompleteSearchFragment(alert, incorrect);

            vc.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    incorrect.setText("");
                    return false;
                }
            });
            check.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    incorrect.setText("");
                    String vCode = vc.getText().toString();
                    if (address.isEmpty() || address.length() < 5) {
                        incorrect.setText("You did not select an address");
                    } else if (vCode.length() != 6) {
                        incorrect.setText("You have not entered the 6-digit verification code.");
                    } else {
                        //DONE Read Firebase.
                        validate(vCode, address, incorrect, (i.equals("u") ? true : false), alert);
                    }
                }
            });

            alert.show();
        }
    }

    private void validate(final String vCode, final String address, final TextView error, final boolean username, final Dialog alert) {
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Places");
        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean good = false;
                String result = "";
                for (DataSnapshot children : dataSnapshot.getChildren()) {
                    String VC = children.child("Verification Code").getValue().toString();
                    String ad = children.child("Address").getValue().toString();
                    if (username) {
                        result = children.child("Username").getValue().toString();
                    } else {
                        result = children.child("Password").getValue().toString();
                    }

                    if (address.equals(ad) && VC.equals(vCode)) {
                        good = true;
                        break;
                    }
                }
                if (good) {
                    //DONE Dismiss Alert Dialog and display result.
                    makeToast("RESULT: " + result);
                    alert.dismiss();
                    alert.cancel();
                    showResult(username, result);
                } else {
                    error.setText("The info you entered is incorrect.");
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                makeToast("There was an error. Please try again");
                //progressDialog.dismiss();
            }
        });

    }

    private void showResult(boolean username, String result) {
        final Dialog alert = new Dialog(MainActivity.this);
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert.setContentView(R.layout.show_result);
        alert.setCancelable(false);
        alert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView message = alert.findViewById(R.id.message);
        if (username) {
            message.setText("Dont forget your username again, so save it somewhere!");
        } else {
            message.setText("Dont forget your password again, so save it somewhere!");
        }
        TextView display = alert.findViewById(R.id.display);

        if (username) {
            SpannableString ss = new SpannableString("Username: " + result);
            ss.setSpan(new StyleSpan(Typeface.BOLD), 0, 9, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            display.setText(ss);
        } else {
            SpannableString ss = new SpannableString("Password: " + result);
            ss.setSpan(new StyleSpan(Typeface.BOLD), 0, 9, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            display.setText(ss);
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

    @Override
    public void onBackPressed() {

    }

    public void setUpAutocompleteSearchFragment(Dialog alert, TextView error) {
        final AutocompleteSupportFragment autocompleteSupportFragment =
                (AutocompleteSupportFragment)
                        getSupportFragmentManager().findFragmentById(R.id.auto2);


        autocompleteSupportFragment.setPlaceFields(getPlaceFields());
        autocompleteSupportFragment.setOnPlaceSelectedListener(getPlaceSelectionListener(error));
        autocompleteSupportFragment.setHint("Search by Address!");
        //autocompleteSupportFragment.setLocationBias(getLocationBias());
        autocompleteSupportFragment.setTypeFilter(null);
        EditText etPlace = (EditText) autocompleteSupportFragment.getView().findViewById(R.id.places_autocomplete_search_input);
        etPlace.setHint("Search for Address!");
        etPlace.setHintTextColor(Color.parseColor("#0b9c12"));
        Typeface face = ResourcesCompat.getFont(getApplicationContext(), R.font.regular);
        etPlace.setTextSize(22.0f);
        etPlace.setTypeface(face);
        //etPlace.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        etPlace.setPadding(-3, -8, 0, -8);
        alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                getSupportFragmentManager().beginTransaction().remove((Fragment) autocompleteSupportFragment).commit();
                //getFragmentManager().beginTransaction().remove((Fragment) autocompleteSupportFragment).commit();
                //autocompleteSupportFragment.onDestroyView();

              /* finish();
               startActivity(getIntent());*/
               /* FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.auto)*/
            }
        });
    }


    private PlaceSelectionListener getPlaceSelectionListener(final TextView error) {
        return new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                makeToast(
                        "You chose: " + place.getName() + ".");
                address = place.getAddress();
                phone = place.getPhoneNumber();
                error.setText("");
                name = place.getName();
            }

            @Override
            public void onError(Status status) {
                makeToast(status.getStatusMessage());
            }
        };
    }

    private List<Place.Field> getPlaceFields() {
        List<Place.Field> arr = new ArrayList<>();
        arr.add(Place.Field.ADDRESS);
        arr.add(Place.Field.NAME);
        arr.add(Place.Field.PHONE_NUMBER);
        return arr;
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

    public void showAppInfo2() {
        final Dialog alert = new Dialog(MainActivity.this);
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert.setContentView(R.layout.about_this_app_welcome);
        alert.setCancelable(true);

        alert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (firstLaunch) {
                    //DONE Use TourGuide (check Voice Dialer App) to show how to login and logout.
                    final Button login = findViewById(R.id.login);
                    final Button signUp = findViewById(R.id.signUp);
                    login.setEnabled(false);

                    mTutorialHandler = TourGuide.init(MainActivity.this).with(TourGuide.Technique.CLICK);
                    mTutorialHandler.setToolTip(new ToolTip()
                            .setTitle("Welcome!")
                            .setDescription("Login if you already have an account!")
                            .setGravity(Gravity.BOTTOM)
                            .setBackgroundColor(0xFF48A837)
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
                                        .setTitle("New User?")
                                        .setDescription("Create an account in less than a minute!")
                                        .setGravity(Gravity.TOP)
                                        .setBackgroundColor(0xFF48A837)
                                );
                                login.setEnabled(true);
                                clicks++;
                                mTutorialHandler.playOn(signUp);
                            } else {
                                mTutorialHandler.cleanUp();
                            }
                        }
                    });
                    mTutorialHandler.setOverlay(r);
                    mTutorialHandler.playOn(login);

                    firstLaunch = false;
                }
            }
        });

        alert.show();

    }

    public void showAppInfo() {
        final Dialog alert = new Dialog(MainActivity.this);
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert.setContentView(R.layout.about_this_app);
        alert.setCancelable(true);

        alert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                /*if (firstLaunch) {
                    //DONE Use TourGuide (check Voice Dialer App) to show how to login and logout.
                    final Button login = findViewById(R.id.login);
                    final Button signUp = findViewById(R.id.signUp);
                    login.setEnabled(false);

                    mTutorialHandler = TourGuide.init(MainActivity.this).with(TourGuide.Technique.CLICK);
                    mTutorialHandler.setToolTip(new ToolTip()
                            .setTitle("Welcome!")
                            .setDescription("Login if you already have an account!")
                            .setGravity(Gravity.BOTTOM)
                            .setBackgroundColor(0xFF48A837)
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
                                        .setTitle("New User?")
                                        .setDescription("Create an account in less than a minute!")
                                        .setGravity(Gravity.TOP)
                                        .setBackgroundColor(0xFF48A837)
                                );
                                login.setEnabled(true);
                                clicks++;
                                mTutorialHandler.playOn(signUp);
                            } else {
                                mTutorialHandler.cleanUp();
                            }
                        }
                    });
                    mTutorialHandler.setOverlay(r);
                    mTutorialHandler.playOn(login);

                    firstLaunch = false;
                }*/
            }
        });

        alert.show();

    }

    private String readFromFile(Context context, String fileName) throws IOException {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(fileName);

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            //makeToast("File not found: " + e.toString());
            //makeToast("Doesn't exist");
        } catch (IOException e) {
            //makeToast("Can not read file: " + e.toString());
        }

        return ret;
    }

    private void makeToast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
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
}

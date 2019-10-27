package com.example.anany.restaurantleftoverfood;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.InsetDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.example.anany.restaurantleftoverfood.ui.main.SectionsPagerAdapter;
import com.google.android.gms.maps.SupportMapFragment;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import tourguide.tourguide.Overlay;
import tourguide.tourguide.ToolTip;
import tourguide.tourguide.TourGuide;

public class Home extends AppCompatActivity implements FragmentHome.OnFragmentInteractionListener,
        FragmentList.OnFragmentInteractionListener, FragmentMap.OnFragmentInteractionListener, FragmentSettings.OnFragmentInteractionListener
        , FragmentDonations.OnFragmentInteractionListener, FragmentAccountInfo.OnFragmentInteractionListener,
        FragmentAppSettings.OnFragmentInteractionListener {

    boolean firstTime;

    TabLayout tabs;
    Toolbar toolbar;
    ViewPager viewPager;
    TourGuide mTutorialHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        tabs = findViewById(R.id.tabs);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager(), tabs);
        viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        tabs.setupWithViewPager(viewPager);
        tabs.setBackground(ContextCompat.getDrawable(Home.this, R.drawable.tab_back2));

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Donation Stations");
        setSupportActionBar(toolbar);

        //DONE Figure out how to get Menu Options in Fragments

        //TODO In the Settings Fragment, what you have to do is if read from info.txt to set their settings beforehand.
        //TODO Then, when they click Save, rewrite info.txt with the new data.
        //TODO Then, replace with the new data in Firebase.
        //TODO Then, make SnackBar saying "saved".

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.pend);
            String description = getString(R.string.pend);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("Donation Reception", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "Donation Reception")
                .setContentTitle("My notification")
                .setContentText("Much longer text that cannot fit one line...")
                .setSmallIcon(R.drawable.infoimage)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Much longer text that cannot fit one line..."))
                .setPriority(NotificationCompat.PRIORITY_MAX);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

// notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, builder.build());*/
        readInfo();
        firstTime = false;
        readFirstTime();
        //tabs.getTabAt(3).select();

        //TODO Display the latest donors.
        //TODO Display the latest receivers.
        //TODO Display the restaurants with most donations and points.
        //INFO (Points are based on the food status of the shelter when restaurant was donating).

        //DONE Change color of tabs and actionbar based on which tab is showing.
        //DONE Maybe make the ActionBar grey and black for all of them and only the tabs change color.
        tabStuff();

    }

    ImageView image;
    int changeCount = 0;

    private void tabStuff() {
        tabs.setSelectedTabIndicatorColor(Color.parseColor("#499ff5"));
        tabs.setSelectedTabIndicatorHeight((int) (5 * getResources().getDisplayMetrics().density));
        tabs.setTabTextColors(Color.parseColor("#2074c7"), Color.parseColor("#57a5f2"));
        tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        LinearLayout linearLayout = (LinearLayout) tabs.getChildAt(0);
        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(Color.parseColor("#0063BD"));
        drawable.setSize(3, 1);
        linearLayout.setDividerPadding(10);
        linearLayout.setDividerDrawable(drawable);
        image = findViewById(R.id.invisi);
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int num = tab.getPosition();
                View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.tab_view, null);
                if (num == 0) {
                    //tabs.setSelectedTabIndicatorColor(Color.parseColor("#000000"));
                    ViewGroup tabStrip = (ViewGroup) tabs.getChildAt(0);
                    for (int i = 1; i < 5; i++) {
                        View tabView = tabStrip.getChildAt(i);
                        ViewCompat.setBackground(tabView, getDrawable(R.drawable.tab_back));
                    }
                    /*tabs.getChildAt(1).setBackground(getDrawable(R.drawable.tab_back));
                    tabs.getChildAt(2).setBackground(getDrawable(R.drawable.tab_back));
                    tabs.getChildAt(3).setBackground(getDrawable(R.drawable.tab_back));
                    tabs.getChildAt(4).setBackground(getDrawable(R.drawable.tab_back));*/
                    tabs.setSelectedTabIndicatorColor(Color.parseColor("#499ff5"));
                    tabs.setSelectedTabIndicatorHeight((int) (5 * getResources().getDisplayMetrics().density));
                    tabs.setTabTextColors(Color.parseColor("#2074c7"), Color.parseColor("#57a5f2"));
                    LinearLayout linearLayout = (LinearLayout) tabs.getChildAt(0);
                    linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
                    GradientDrawable drawable = new GradientDrawable();
                    drawable.setColor(Color.parseColor("#0063BD"));
                    drawable.setSize(3, 1);
                    linearLayout.setDividerPadding(10);
                    linearLayout.setDividerDrawable(drawable);
                    tabs.setSelectedTabIndicatorColor(Color.parseColor("#000000"));
                    /*SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                    mapFragment.getMapAsync(FragmentMap.);*/

                } else if (num == 1) {
                    //tabs.setBackground(getDrawable(R.drawable.tab_back2));
                    ViewGroup tabStrip = (ViewGroup) tabs.getChildAt(0);
                    for (int i = 0; i < 5; i++) {
                        if (i != 1) {
                            View tabView = tabStrip.getChildAt(i);
                            ViewCompat.setBackground(tabView, getDrawable(R.drawable.tab_back2));
                        }
                    }
                    tabs.setSelectedTabIndicatorColor(Color.parseColor("#499ff5"));

                    //tabs.setBackgroundColor(getResources().getColor(R.color.Black));
                    /*tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
                    GradientDrawable drawable2 = new GradientDrawable();
                    drawable2.setColor(getColor(R.color.Black));
                    //drawable.setSize(3, 1);
                    tabs.getChildAt(0).setBackground(drawable2);
                    tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
                    tabs.getTabAt(0).setText("Home Page");

/*tabs.getChildAt(0).setBackground(getDrawable(R.drawable.tab_back2));
                    tabs.getChildAt(2).setBackground(getDrawable(R.drawable.tab_back2));
                    tabs.getChildAt(3).setBackground(getDrawable(R.drawable.tab_back2));
                    tabs.getChildAt(4).setBackground(getDrawable(R.drawable.tab_back2));*/
                    //image.setBackground(getDrawable(R.drawable.tab_back2));
                    //view.setBackground(getDrawable(R.drawable.tab_back2));
                    /*TextView text = view.findViewById(R.id.changeC);
                    text.setText("#db0404");*/
                    //tabs.getTabAt(0).setCustomView(view);
                    tabs.setSelectedTabIndicatorHeight((int) (5 * getResources().getDisplayMetrics().density));
                    tabs.setTabTextColors(Color.parseColor("#cc0202"), Color.parseColor("#ff0000"));
                    LinearLayout linearLayout = (LinearLayout) tabs.getChildAt(0);
                    linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
                    GradientDrawable drawable = new GradientDrawable();
                    drawable.setColor(Color.RED);
                    drawable.setSize(3, 1);
                    linearLayout.setDividerPadding(10);
                    linearLayout.setDividerDrawable(drawable);
                } else if (num == 2) {
                    //tabs.setBackground(getDrawable(R.drawable.tab_back2));
                    ViewGroup tabStrip = (ViewGroup) tabs.getChildAt(0);
                    for (int i = 0; i < 5; i++) {
                        if (i != 2) {
                            View tabView = tabStrip.getChildAt(i);
                            ViewCompat.setBackground(tabView, getDrawable(R.drawable.tab_back3));
                        }
                    }
                    if (changeCount != 0) {
                        FragmentMap.resetMap();
                        FragmentMap.doMapStuff();
                    }
                    tabs.setSelectedTabIndicatorColor(Color.parseColor("#499ff5"));
                    tabs.setSelectedTabIndicatorHeight((int) (5 * getResources().getDisplayMetrics().density));
                    tabs.setTabTextColors(Color.parseColor("#9927a3"), Color.parseColor("#c23ccf"));
                    LinearLayout linearLayout = (LinearLayout) tabs.getChildAt(0);
                    linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
                    GradientDrawable drawable = new GradientDrawable();
                    drawable.setColor(Color.parseColor("#9927a3"));
                    drawable.setSize(3, 1);
                    linearLayout.setDividerPadding(10);
                    linearLayout.setDividerDrawable(drawable);
                    changeCount++;
                } else if (num == 3) {
                    //tabs.setBackground(getDrawable(R.drawable.tab_back2));
                    ViewGroup tabStrip = (ViewGroup) tabs.getChildAt(0);
                    for (int i = 0; i < 5; i++) {
                        if (i != 3) {
                            View tabView = tabStrip.getChildAt(i);
                            ViewCompat.setBackground(tabView, getDrawable(R.drawable.tab_back4));
                        }
                    }
                    tabs.setSelectedTabIndicatorColor(Color.parseColor("#499ff5"));
                    tabs.setSelectedTabIndicatorHeight((int) (5 * getResources().getDisplayMetrics().density));
                    tabs.setTabTextColors(Color.parseColor("#bd6d28"), Color.parseColor("#db873d"));
                    LinearLayout linearLayout = (LinearLayout) tabs.getChildAt(0);
                    linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
                    GradientDrawable drawable = new GradientDrawable();
                    drawable.setColor(Color.parseColor("#bd6d28"));
                    drawable.setSize(3, 1);
                    linearLayout.setDividerPadding(10);
                    linearLayout.setDividerDrawable(drawable);
                    //checkFirstDonations();
                } else if (num == 4) {
                    //tabs.setBackground(getDrawable(R.drawable.tab_back2));
                    ViewGroup tabStrip = (ViewGroup) tabs.getChildAt(0);
                    for (int i = 0; i < 4; i++) {
                        View tabView = tabStrip.getChildAt(i);
                        ViewCompat.setBackground(tabView, getDrawable(R.drawable.tab_back5));
                    }
                    tabs.setSelectedTabIndicatorColor(Color.parseColor("#499ff5"));
                    tabs.setSelectedTabIndicatorHeight((int) (5 * getResources().getDisplayMetrics().density));
                    tabs.setTabTextColors(Color.parseColor("#23a840"), Color.parseColor("#2dbd4c"));
                    LinearLayout linearLayout = (LinearLayout) tabs.getChildAt(0);
                    linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
                    GradientDrawable drawable = new GradientDrawable();
                    drawable.setColor(Color.parseColor("#23a840"));
                    drawable.setSize(3, 1);
                    linearLayout.setDividerPadding(10);
                    linearLayout.setDividerDrawable(drawable);

                    /*SectionsPagerAdapter t = ((SectionsPagerAdapter) viewPager.getAdapter());
                    FragmentSettings frag = t.get
                    FragmentAccountInfo frag = getSupportFragmentManager().findFragmentById();
                    if (frag!=null)*/

                   /* new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            FragmentSettings.account.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //viewPager.setCurrentItem(5);

                                    SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getApplicationContext(), getSupportFragmentManager(), tabs);
                                    ViewPager viewPager = findViewById(R.id.view_pager);
                                    viewPager.setAdapter(sectionsPagerAdapter);
                                    tabs.setupWithViewPager(viewPager);

                                    viewPager.setCurrentItem(5);
                                    makeToast("CLICKED 2");
                                    *//*setContentView(R.layout.fragment_fragment_account_info);*//*
                                }
                            });

                            FragmentSettings.settings.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    makeToast("CLICKED 1");
                                    viewPager.setCurrentItem(6);
                                    //setContentView(R.layout.fragment_fragment_app_settings);
                                }
                            });
                        }
                    },1000);
*/
                }
                tabs.setSelectedTabIndicatorColor(Color.parseColor("#000000"));
                tabs.setSelectedTabIndicatorHeight((int) (5 * getResources().getDisplayMetrics().density));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void checkFirstDonations() {
        try {
            InputStream inputStream = getApplicationContext().openFileInput("firstDonation.txt");
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
                    //showPageInfo();
                    makeToast("EXISTINGG GOOD");
                    android.support.v4.app.Fragment donations = getSupportFragmentManager().findFragmentById(R.id.fl);
                    TextView test = donations.getView().findViewById(R.id.foodText);
                    test.setText("HELLO");
                }

                inputStream.close();
            }
        } catch (Exception e) {
            //INFO file doesn't exist.
            //makeToast(e.toString);
        }
    }

    private void readFirstTime() {
        try {
            InputStream inputStream = getApplicationContext().openFileInput("firstTime.txt");
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
                    firstTime = true;
                    showAppInfo();
                    writeDonationsFirst();
                }

                inputStream.close();
            }
        } catch (Exception e) {
            //INFO file doesn't exist.
            //makeToast(e.toString);
        }
    }

    private void deleteFirstTime() {
        //DONE Uncomment below
        File dir = getFilesDir();
        File file = new File(dir, "firstTime.txt");
        boolean deleted = file.delete();
    }

    private void deleteFirstDonation() {
        //DONE Uncomment below
        File dir = getFilesDir();
        File file = new File(dir, "firstDonation.txt");
        boolean deleted = file.delete();
    }

    private void writeDonationsFirst() {
        OutputStreamWriter outputStreamWriter = null;
        try {
            outputStreamWriter = new OutputStreamWriter(getApplicationContext().openFileOutput("firstDonation.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write("First Donation");
            outputStreamWriter.close();
        } catch (Exception e) {
            //makeToast("ERROR: " + e.toString() + "\nPlease try again.");
        }
    }


    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();
        String s = "";
        if (getSupportFragmentManager().getBackStackEntryCount() != 0) {
            s = (String) getSupportFragmentManager().getBackStackEntryAt(0).getBreadCrumbTitle();
            FragmentManager tr = getSupportFragmentManager();
            for (int i = 0; i < tr.getBackStackEntryCount(); ++i)
                tr.popBackStack();
        }
        if (s.equals("hi")) {
        } else if (count == 0)
            askIfSure();
        getSupportFragmentManager().popBackStack();
    }

    int clicks;

    public void showAppInfo() {
        final Dialog alert = new Dialog(Home.this);
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //alert.setContentView(R.layout.about_this_app2);
        alert.setContentView(R.layout.first_time_ask_4_donations);
        alert.setCancelable(true);
        clicks = 0;

        TextView donateText = alert.findViewById(R.id.t2);
        TextView donateText2 = alert.findViewById(R.id.t1);

        SpannableString ss = new SpannableString(donateText.getText().toString());
        ss.setSpan(new UnderlineSpan(), 86, donateText.getText().toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new ForegroundColorSpan(Color.parseColor("#007ee6")), 86, donateText.getText().toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new StyleSpan(Typeface.BOLD), 86, donateText.getText().toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        donateText.setText(ss);
        Button donate = alert.findViewById(R.id.donate);
        donate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.paypal.me/IJApps"));
                startActivity(browserIntent);
            }
        });

        View.OnTouchListener toucher = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.paypal.me/IJApps"));
                startActivity(browserIntent);
                return false;
            }
        };
        donateText.setOnTouchListener(toucher);
        donateText2.setOnTouchListener(toucher);

        alert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        /*final View page1 = tabs.getChildAt(0).getRootView();
        final View page2 = tabs.getChildAt(1).getRootView();
        View page3 = tabs.getChildAt(2).getRootView();
        View page4 = tabs.getChildAt(3).getRootView();
        View page5 = tabs.getChildAt(4).getRootView();*/
        final View page1 = ((ViewGroup) tabs.getChildAt(0)).getChildAt(0);
        final View page2 = ((ViewGroup) tabs.getChildAt(0)).getChildAt(1);
        final View page3 = ((ViewGroup) tabs.getChildAt(0)).getChildAt(2);
        final View page4 = ((ViewGroup) tabs.getChildAt(0)).getChildAt(3);
        final View page5 = ((ViewGroup) tabs.getChildAt(0)).getChildAt(4);
        alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                //makeToast("Not showing.");
                if (firstTime) {
                    firstTime = false;
                    //DONE Use TourGuide (check Voice Dialer App) to show how to navigate through Homepage.
                    //DONE Then, delete firstTime.txt

                    mTutorialHandler = TourGuide.init(Home.this).with(TourGuide.Technique.CLICK);
                    mTutorialHandler.setToolTip(new ToolTip()
                            .setTitle("Home Page")
                            .setDescription("Over here, you can view the Donations Leaderboard and general info as well as access the other pages.")
                            .setGravity(Gravity.BOTTOM)
                            .setBackgroundColor(0xFF4dbbff)
                    );
                    Overlay r = new Overlay();
                    r.setBackgroundColor(0x99000000);
                    r.disableClickThroughHole(false);
                    r.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (clicks == 0) {
                                tabs.getTabAt(1).select();
                                mTutorialHandler.cleanUp();
                                mTutorialHandler.setToolTip(new ToolTip()
                                        .setTitle("")
                                        .setDescription("View a list of nearby restaurants and shelters along with detailed information.")
                                        .setGravity(Gravity.BOTTOM)
                                        .setBackgroundColor(0xFFe04343)
                                );
                                clicks++;
                                mTutorialHandler.playOn(page2);
                            } else if (clicks == 1) {
                                tabs.getTabAt(2).select();
                                mTutorialHandler.cleanUp();
                                mTutorialHandler.setToolTip(new ToolTip()
                                        .setTitle("")
                                        .setDescription("View the locations of shelters and restaurants within a search radius on a map. You can also tap on markers to get additional information and use the search bar to find specific results.")
                                        .setGravity(Gravity.BOTTOM)
                                        .setBackgroundColor(0xFF9968ed)
                                );
                                clicks++;
                                mTutorialHandler.playOn(page2);
                            } else if (clicks == 2) {
                                tabs.getTabAt(3).select();
                                mTutorialHandler.cleanUp();
                                String description = "Here, you can offer donations to shelters that are in need of food as well as accept any pending requests. " +
                                        "The page also gives information about the donations that you've made and how many points you have.";

                                if(description.contains("elt")){
                                    description = "Here, you can request donations from restaurants as well as accept any offers that you have received." +
                                            "The page also allows you to set how much food you will need and when.";
                                }
                                mTutorialHandler.setToolTip(new ToolTip()
                                        .setTitle("")
                                        .setDescription(description)
                                        .setGravity(Gravity.BOTTOM)
                                        .setBackgroundColor(0xFFc98b51)
                                );
                                clicks++;
                                mTutorialHandler.playOn(page3);
                            } else if (clicks == 3) {
                                tabs.getTabAt(4).select();
                                mTutorialHandler.cleanUp();
                                mTutorialHandler.setToolTip(new ToolTip()
                                        .setTitle("")
                                        .setDescription("On this page, you can change the app settings such as your discoverability to other shelters and restaurants" +
                                                "or the search radius for results that show up in the list.")
                                        .setGravity(Gravity.BOTTOM)
                                        .setBackgroundColor(0xFF33c461)
                                );
                                clicks++;
                                mTutorialHandler.playOn(page5);
                            } else {
                                mTutorialHandler.cleanUp();
                                tabs.getTabAt(0).select();
                                deleteFirstTime();
                                firstTime = false;
                            }
                        }
                    });
                    mTutorialHandler.setOverlay(r);
                    mTutorialHandler.playOn(page1);
                }
            }
        });
        alert.show();
    }

    String type;

    public void readInfo() {
        try {
            InputStream inputStream = getApplicationContext().openFileInput("info.txt");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                int count = 0;
                String s;
                while ((s = bufferedReader.readLine()) != null) {
                    if (count == 1)
                        type = s;
                    else if (count > 1) break;
                    count++;
                }

            }
        } catch (Exception e) {

        }
    }

    public void askIfSure() {
        final Dialog alert = new Dialog(Home.this);
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //DONE Make different layouts with different colors for different tabs
        //DONE Write code to detect which tab. Then setContentView to layout accordingly.

        //TODO Have to figure out how when user changes settings and info.txt, reread from file.
        //TODO What to do is when user changes and clicks save, create a file called reread.
        //TODO Then, if reread exists, in each of the fragments reread from info.txt and delete reread.
        if (tabs.getSelectedTabPosition() == 0)
            alert.setContentView(R.layout.want_to_sign_out);
        if (tabs.getSelectedTabPosition() == 1)
            alert.setContentView(R.layout.want_to_sign_out_2);
        if (tabs.getSelectedTabPosition() == 2)
            alert.setContentView(R.layout.want_to_sign_out_3);
        if (tabs.getSelectedTabPosition() == 3)
            alert.setContentView(R.layout.want_to_sign_out_4);
        if (tabs.getSelectedTabPosition() == 4)
            alert.setContentView(R.layout.want_to_sign_out_5);

        alert.setCancelable(false);

        Window window = alert.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        window.setAttributes(wlp);

        ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
        InsetDrawable inset = new InsetDrawable(back, 5);
        alert.getWindow().setBackgroundDrawable(inset);

        TextView message = alert.findViewById(R.id.message);

        Button yes = alert.findViewById(R.id.yes);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //INFO they want to sign out.
                makeToast("Work on signOut() function.");
                signOut();
            }
        });

        Button no = alert.findViewById(R.id.no);
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //INFO they don't want to sign out.
                alert.dismiss();
            }
        });

        alert.show();
    }

    private void signOut() {
        //DONE Delete info.txt.
        //DONE Delete signedIn.txt
        //DONE Go back to MainActivity.java
        File dir = getFilesDir();
        File file = new File(dir, "info.txt");
        boolean deleted = file.delete();
        File file2 = new File(dir, "signedIn.txt");
        boolean deleted2 = file2.delete();

        //makeToast("Deleted: \n" + deleted + "\n" + deleted2);
        startActivity(new Intent(Home.this, MainActivity.class));
    }

    private void makeToast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
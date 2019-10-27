package com.example.anany.restaurantleftoverfood.Adapters;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anany.restaurantleftoverfood.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class VerificationPage extends AppCompatActivity {

    Bundle bundles;
    String type, address, latLong, inList, inMap, verificationCode, name, manager, website, username, password, phone, hours, disS, disR;

    TextView message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_page);

        //TODO Write data to Places
        //TODO Save Info.txt
        //TODO Create a file called firsttime so that in Homepage, if firsttime exists then it shows special info.

        Intent receiveIntent = getIntent();
        //INFO Don't use intent
        //TODO Read from tempData.txt to get the info.
        String result = "Result";
        try {
            InputStream inputStream = getApplicationContext().openFileInput("tempData.txt");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                int count = 0;
                while ((receiveString = bufferedReader.readLine()) != null) {
                    if (count == 0) type = receiveString;
                    if (count == 1) name = receiveString;
                    if (count == 2) address = receiveString;
                    if (count == 3) username = receiveString;
                    if (count == 4) password = receiveString;
                    if (count == 5) manager = receiveString;
                    if (count == 6) phone = receiveString;
                    if (count == 7) website = receiveString;
                    if (count == 8) hours = receiveString;
                    if (count == 9) inList = receiveString;
                    if (count == 10) inMap = receiveString;
                    if (count == 11) latLong = receiveString;
                    if (count == 12) verificationCode = receiveString;
                    if (count == 13) disS = receiveString;
                    if (count == 14) disR = receiveString;
                    stringBuilder.append(receiveString + "\n");
                    count++;
                }
                result = stringBuilder.toString();
                inputStream.close();
            }
        } catch (Exception e) {
            makeToast(e.toString());
        }
        //bundles = receiveIntent.getExtras();
        /*type = receiveIntent.getStringExtra("Type");
        address = receiveIntent.getStringExtra("Address");
        latLong = receiveIntent.getStringExtra("Search Radius");
        inList = receiveIntent.getStringExtra("Others in List");
        inMap = receiveIntent.getStringExtra("Others in Map");
        verificationCode = receiveIntent.getStringExtra("Verification Code");*/
        message = findViewById(R.id.text);
        //makeToast(result);

        //INFO The below will not work if the user closes the app because the intent is not remembered.
        //TODO This is why you have to read from tempData.txt to get the info.
        message.setText("A verification code was sent to the phone # of your " + Character.toLowerCase(type.charAt(0)) + type.substring(1) + " through an automated voice call.");
       // message.setText(result);
    }


    private void correctLogin() {
        String info = type + "\n" + address + "\n" + latLong + "\n" + inList + "\n" + inMap + "\n" + verificationCode;
        //saveInfoTxt(info);
        //savePlaces();
        createAlertDialog();
        //TODO Make Alert Dialog saying Success! Have a checkbox for autolaunching the HomePage. Have a close button.
        //INFO for above, if they choose checkbox, then save info.txt and go straight to HomePage.
        //INFO if they don't choose checkbox, go to MainActivity.
        //TODO Create a file called firsttime
    }

    private void createAlertDialog() {
        //TODO Make an Alert Dialog saying Success, and option to autolaunch HomePage
    }



    @Override
    public void onBackPressed() {
        makeToast("You cannot go back until you verify your account.");
    }

    public void makeToast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
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
        final Dialog alert = new Dialog(VerificationPage.this);
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert.setContentView(R.layout.about_this_app);
        alert.setCancelable(true);
        alert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alert.show();
    }
}

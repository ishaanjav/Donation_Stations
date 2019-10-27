package com.example.anany.restaurantleftoverfood;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Restaurant extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    EditText name, manager, phone, address;
    Button finish;
    Spinner type;
    String closingTime = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);
        name = findViewById(R.id.restaurantName);
        manager = findViewById(R.id.managerName);
        phone = findViewById(R.id.phoneNum);
        address = findViewById(R.id.address);

        finish = findViewById(R.id.finish);

        Spinner spinner = (Spinner) findViewById(R.id.time1);

        // Spinner click listener
        spinner.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("5:00 PM");
        categories.add("6:00 PM");
        categories.add("7:00 PM");
        categories.add("8:00 PM");
        categories.add("9:00 PM");
        categories.add("10:00 PM");
        categories.add("11:00 PM");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((name.getText().toString().isEmpty() || name.getText().toString() == null) || (address.getText().toString().isEmpty() || address.getText().toString() == null) || (closingTime == null || closingTime.isEmpty())) {
                    makeToast("You have not entered the required information.");
                } else if ((phone.getText().toString().isEmpty() || phone.getText().toString() == null) || /*getPhoneLength*/phone.getText().toString().length() < 10) {
                    makeToast("Please enter a valid phone number.");
                } else {
                    String shelterName = name.getText().toString();
                    String managerName = manager.getText().toString();
                    String phoneName = getPhoneNumber();
                    String location = address.getText().toString();

                    /*if (phoneName.equals("none")) {
                        String toWrite = "restaurant\n" + location;
                        writeToFile(toWrite, getApplicationContext());
                        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("restaurants");
                        HashMap<String, String> hm = new HashMap<>();
                        hm.put("Restaurant", shelterName);
                        hm.put("Manager", managerName);
                        hm.put("Address", location);
                        hm.put("Phone", phoneName);
                        hm.put("Closing Time", closingTime);
                        dbref.push().setValue(hm);
                        startActivity(new Intent(getApplicationContext(), Homepage.class));
                    } else if (phoneName.length() > 0) {
                        if (phoneName.length() < 10) {
                            makeToast("The phone number that you have entered is too short.");
                        } else {*/
                    String toWrite = "restaurant\n" + location;
                    writeToFile(toWrite, getApplicationContext());
                    DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("restaurants");
                    HashMap<String, String> hm = new HashMap<>();
                    hm.put("Restaurant", shelterName);
                    hm.put("Manager", managerName);
                    hm.put("Address", location);
                    hm.put("Phone", phoneName);
                    startActivity(new Intent(getApplicationContext(), Homepage.class));
                    hm.put("Closing Time", closingTime);
                    dbref.push().setValue(hm);
                    startActivity(new Intent(getApplicationContext(), Homepage.class));
                    //}
                    //}
                }
            }
        });


    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();
        makeToast("You selected: " + item);
        closingTime = item;
        // Showing selected spinner item
    }

    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    private void writeToFile(String data, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("info.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
            makeToast("Data Saved.");
        } catch (IOException e) {
            makeToast("Exception -  " + "File write failed: " + e.toString());
        }
    }

    private String getPhoneNumber() {
        String temp = phone.getText().toString();
        if (temp.isEmpty() || temp == null) {
            return "none";
        }
        String output = "";
        for (char s : temp.toCharArray()) {
            if (Character.isDigit(s)) output += s + "";
        }
        return output;
    }

    private void makeToast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

}

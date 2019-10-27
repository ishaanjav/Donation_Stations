package com.example.anany.restaurantleftoverfood;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;

public class Shelter extends AppCompatActivity {

    EditText shelter, manager, phone, address;
    Button finish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shelter);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        shelter = findViewById(R.id.shelterName);
        manager = findViewById(R.id.managerName);
        phone = findViewById(R.id.phoneNum);
        address = findViewById(R.id.address);

        finish = findViewById(R.id.finish);

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((shelter.getText().toString().isEmpty() || shelter.getText().toString() == null) || (address.getText().toString().isEmpty() || address.getText().toString() == null) || (manager.getText().toString().isEmpty() || manager.getText().toString() == null)) {
                    makeToast("You have not entered the required information.");
                } else {
                    String shelterName = shelter.getText().toString();
                    String managerName = manager.getText().toString();
                    String phoneName = getPhoneNumber();
                    String location = address.getText().toString();

                    if (phoneName.equals("none")) {
                        String toWrite = "shelter\n" + location;
                        writeToFile(toWrite, getApplicationContext());
                        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("shelters");
                        HashMap<String, String> hm = new HashMap<>();
                        hm.put("Shelter", shelterName);
                        hm.put("Manager", managerName);
                        hm.put("Address", location);
                        hm.put("Phone", phoneName);
                        dbref.push().setValue(hm);
                        startActivity(new Intent(getApplicationContext(), Homepage.class));
                    } else if (phoneName.length() > 0) {
                        if (phoneName.length() < 10) {
                            makeToast("The phone number that you have entered is too short.");
                        } else {
                            String toWrite = "shelter\n" + location;
                            writeToFile(toWrite, getApplicationContext());
                            DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("shelters");
                            HashMap<String, String> hm = new HashMap<>();
                            hm.put("Shelter", shelterName);
                            hm.put("Manager", managerName);
                            hm.put("Address", location);
                            hm.put("Phone", phoneName);
                            dbref.push().setValue(hm);
                            startActivity(new Intent(getApplicationContext(), Homepage.class));
                        }
                    }
                }
            }
        });


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

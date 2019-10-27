package com.example.anany.restaurantleftoverfood;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentDonations.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentDonations#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentDonations extends Fragment {
    // DONE: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static Context mContext;

    // DONE: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FragmentDonations() {
        // Required empty public constructor
    }

    public String type;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentDonations.
     */
    // DONE: Rename and change types and number of parameters
    public static FragmentDonations newInstance(String param1, String param2, Context context) {
        FragmentDonations fragment = new FragmentDonations();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        mContext = context;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment_donations, container, false);
    }

    // DONE: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onViewCreated( View view,  Bundle savedInstanceState) {
        /*FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.headlines_fragment, DonationsShelter.newInstance("Hi", "ho", mContext)); // newInstance() is a static factory method.
        transaction.commit();
        transaction.replace(R.id.article_fragment, DonationsRestaurant.newInstance("Hi", "ho", mContext)); // newInstance() is a static factory method.
        transaction.commit();*/

        readType();
        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.article_fragment);
        Fragment fragment2 = getChildFragmentManager().findFragmentById(R.id.headlines_fragment);

        if (type.contains("est")) {
            FragmentManager fm = getFragmentManager();
            fm.beginTransaction()
                    .hide(fragment2)
                    .commit();
        } else {
            FragmentManager fm = getFragmentManager();
            fm.beginTransaction()
                    .hide(fragment)
                    .commit();
        }


        super.onViewCreated(view, savedInstanceState);
    }

    public void readType() {
        try {
            InputStream inputStream = mContext.openFileInput("info.txt");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                type = bufferedReader.readLine();
            }
        } catch (Exception e) {
            //makeToast("Can not read file: " + e.toString());
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.signed_in, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                showAppInfo();
                break;
            case R.id.signOut:
                signOut();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showAppInfo() {
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Places");

        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot child: dataSnapshot.getChildren()){
                    DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Places/" + child.getKey() + "/RNum");
                    dbref.setValue("hi");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //TODO UNCOMMENT BELOW
        /*final Dialog alert = new Dialog(mContext);
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert.setContentView(R.layout.about_this_app5);
        alert.setCancelable(true);
        alert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        alert.show();*/
    }

    private void signOut() {
        File dir = mContext.getFilesDir();
        File file = new File(dir, "info.txt");
        boolean deleted = file.delete();
        File file2 = new File(dir, "signedIn.txt");
        boolean deleted2 = file2.delete();
        //makeToast("Deleted: \n" + deleted + "\n" + deleted2);
        startActivity(new Intent(mContext, MainActivity.class));
    }


    private void makeToast(String s) {
        Toast.makeText(mContext, s, Toast.LENGTH_LONG).show();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // DONE: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}

package com.example.anany.restaurantleftoverfood;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.CardView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
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
 * {@link FragmentSettings.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentSettings#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentSettings extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static Context mContext;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    static TabLayout tabs;

    public FragmentSettings() {
        // Required empty public constructor
    }

    Button delete;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @param tab
     * @return A new instance of fragment FragmentSettings.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentSettings newInstance(String param1, String param2, Context context, TabLayout tab) {
        FragmentSettings fragment = new FragmentSettings();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        mContext = context;
        tabs = tab;
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
        return inflater.inflate(R.layout.fragment_fragment_settings, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    static Button settings, account, viewVC;
    String verificationCode, username, password;
    int deletedClicks;

    @Override
    public void onViewCreated( View view,  Bundle savedInstanceState) {
        CardView background = getView().findViewById(R.id.back);
        background.setBackground(getResources().getDrawable(R.drawable.card_bg));
        settings = getView().findViewById(R.id.changeSettings);
        account = getView().findViewById(R.id.changeInfo);
        viewVC = getView().findViewById(R.id.VC);
        delete = getView().findViewById(R.id.delete);
        deletedClicks = 0;
        readInfo();
        readData2();
        buttonClicks();

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deletedClicks == 0) {
                    askIfSure();
                }
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }

    private void askIfSure() {
        final Dialog alert = new Dialog(mContext);
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert.setContentView(R.layout.delete_verification);
        alert.setCancelable(false);
        alert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        deletedClicks++;
        Button no = alert.findViewById(R.id.cancel);
        Button yes = alert.findViewById(R.id.delete);
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletedClicks = 0;
                alert.dismiss();
                alert.cancel();
            }
        });

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView error = alert.findViewById(R.id.error);
                EditText user = alert.findViewById(R.id.username);
                EditText VC = alert.findViewById(R.id.VC);
                EditText pass = alert.findViewById(R.id.password);

                String u = "", p = "", vc = "";
                error.setText("");
                u = user.getText().toString();
                p = pass.getText().toString();
                vc = VC.getText().toString();
                /*user.setText(username);
                pass.setText(password);
                VC.setText(verificationCode);*/

                if (u.equals(username) && p.equals(password) && vc.equals(verificationCode)) {
                    alert.dismiss();
                    alert.cancel();
                    error.setText("");

                    final Dialog alert2 = new Dialog(mContext);
                    alert2.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    alert2.setContentView(R.layout.delete_sure);
                    alert2.setCancelable(false);
                    alert2.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                    Button no = alert2.findViewById(R.id.stay);
                    Button yes = alert2.findViewById(R.id.delete);
                    no.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deletedClicks = 0;
                            alert2.dismiss();
                            alert2.cancel();
                        }
                    });

                    yes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            makeToast("DELETING ACCOUNT!");
                            deleteAll();
                        }
                    });


                    alert2.show();
                } else {
                    error.setText("Incorrect field(s).");
                    makeToast("Invalid information. Please try again.");
                }

            }
        });

        alert.show();
    }

    private void deleteAll() {
        FrameLayout fr = getView().findViewById(R.id.frameL);
        final Snackbar snackbar = Snackbar
                .make(fr, "A strong WiFi connection is recommended, otherwise this may take a while.", Snackbar.LENGTH_INDEFINITE)
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

        Settings.makeProcessing(mContext);
        //TODO You also have to delete from the paths related to Donations.
        //TODO Like you have to delete the child verification code from Donations path and Accept.
        //TODO Then, in Accept, you also have to go through each of the other individual children and read each of their entries to see if the Shelter/Restaurant verification code = whatever was deleted.
        //  If there is a match of the VCs, delete that nested child.

        final DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Places");
        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot) {
                String key = "";
                for (DataSnapshot children : dataSnapshot.getChildren()) {
                    String VC = children.child("Verification Code").getValue().toString();
                    if (VC.equals(verificationCode)) {
                        key = children.getKey();
                        break;
                    }
                }

                dbref.child(key).removeValue();

                final DatabaseReference userAddress = FirebaseDatabase.getInstance().getReference("userAddress");
                userAddress.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange( DataSnapshot dataSnapshot) {
                        String key = "";
                        for (DataSnapshot children : dataSnapshot.getChildren()) {
                            String VC = children.child("Verification Code").getValue().toString();
                            if (VC.equals(verificationCode)) {
                                key = children.getKey();
                                break;
                            }
                        }

                        userAddress.child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                signOut();
                                snackbar.dismiss();
                                deletedClicks = 0;
                                Settings.dismissProcessing();
                            }
                        });
                    }

                    @Override
                    public void onCancelled( DatabaseError databaseError) {
                        makeToast("ERROR!");
                        Settings.dismissProcessing();
                    }
                });

            }

            @Override
            public void onCancelled( DatabaseError databaseError) {
                makeToast("ERROR!");
                Settings.dismissProcessing();
            }
        });
        //DONE Delete Places
        //DONE Delete userAddress
        //DONE Delete signedIn.txt
        //DONE Delete firstTime.txt
        //DONE See if you have to delete any others.
        //DONE Start Intent to MainActivity.

        //TODO Test to see if it works (if deleting the account works).
    }

    private void readData2() {
        try {
            InputStream inputStream = mContext.openFileInput("accountInfo.txt");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                int counter = 0;
                while ((receiveString = bufferedReader.readLine()) != null) {
                    if (counter == 1)
                        username = receiveString;
                    else if (counter == 2)
                        password = receiveString;
                    else if (counter > 2)
                        break;
                    counter++;
                }
            }
        } catch (Exception e) {
            makeToast("Error. Please log out and log back in.");
        }
    }

    private void readInfo() {
        try {
            InputStream inputStream = mContext.openFileInput("info.txt");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";

                int counter = 0;
                while ((receiveString = bufferedReader.readLine()) != null) {
                    if (counter == 11) {
                        verificationCode = receiveString;
                        break;
                    }

                    counter++;
                }

                inputStream.close();
            }
        } catch (FileNotFoundException e) {
            //makeToast("File not found: " + e.toString());
            //makeToast("Doesn't exist");
        } catch (IOException e) {
            //makeToast("Can not read file: " + e.toString());
        }
    }


    static CardView fl;

    private void buttonClicks() {
        viewVC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog d = new Dialog(mContext);
                d.requestWindowFeature(Window.FEATURE_NO_TITLE);
                d.setCancelable(true);
                d.setContentView(R.layout.show_verification);

                TextView message = d.findViewById(R.id.message);
                Button btn = d.findViewById(R.id.close);
                ImageView copy = d.findViewById(R.id.copy);

                copy.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("Verification Code", verificationCode);
                        clipboard.setPrimaryClip(clip);
                        makeToast("Verification Code copied!");
                        return false;
                    }
                });

                message.setText(verificationCode);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        d.dismiss();

                        d.cancel();
                    }
                });

                d.show();
            }
        });
        View.OnClickListener clicker = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //DONE Try question from Stackoverflow how to set layout dynamically in android
                //OLD In the OnClickListener, just call setContentView to layout file for Fragment.
                Fragment frag;
                String tag = "";
                if (v.getId() == settings.getId()) {
                    //mContext.setContentView(R.layout.fragment_fragment_app_settings);
                    tag = "settings";
                    frag = FragmentAppSettings.newInstance("hi", "hi", mContext, tabs);
                } else {
                    //setContentView(R.layout.fragment_fragment_app_settings);
                    tag = "account";
                    frag = FragmentAccountInfo.newInstance("hi", "hi", mContext, tabs);
                }
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.frameL, frag, tag);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.addToBackStack(null);
                ft.setBreadCrumbShortTitle("hi");
                ft.setBreadCrumbTitle("hi");
                /*frag.getView().setFocusableInTouchMode(true);
                frag.getView().requestFocus();
                frag.getView().setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        if(keyCode == KeyEvent.KEYCODE_BACK){
                            makeToast("HI. BAKC");
                            return true;
                        }
                        return false;
                    }
                });*/
                ft.commit();
                fl = getView().findViewById(R.id.back);
                fl.setVisibility(View.INVISIBLE);

            }
        };

        settings.setOnClickListener(clicker);
        account.setOnClickListener(clicker);
    }

    private void removeFragment() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment f = getFragmentManager().findFragmentByTag("account");
        if (f != null) {
            ft.remove(f);
        } else {
            f = getFragmentManager().findFragmentByTag("settings");
            ft.remove(f);
        }
        ft.commit();
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
        final Dialog alert = new Dialog(mContext);
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert.setContentView(R.layout.about_this_app6);
        alert.setCancelable(true);
        alert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        alert.show();
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}

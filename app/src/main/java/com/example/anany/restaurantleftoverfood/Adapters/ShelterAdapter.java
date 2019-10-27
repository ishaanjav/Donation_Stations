package com.example.anany.restaurantleftoverfood.Adapters;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anany.restaurantleftoverfood.FragmentMap;
import com.example.anany.restaurantleftoverfood.Home;
import com.example.anany.restaurantleftoverfood.R;
import com.example.anany.restaurantleftoverfood.Storage.ShelterInfo;
import com.example.anany.restaurantleftoverfood.Storage.Singleton;

import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ShelterAdapter extends ArrayAdapter<ShelterInfo> {
    private Context mContext;
    private List<ShelterInfo> list = new ArrayList<>();
    Button inMap, moreInfo;
    ImageView call, website;

    public ShelterAdapter(@NonNull Context context, ArrayList<ShelterInfo> list) {
        super(context, 0, list);
        mContext = context;
        this.list = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        final ShelterInfo row = list.get(position);
        if (listItem == null) {
            if (row.getType().contains("elt")) {
                listItem = LayoutInflater.from(mContext).inflate(R.layout.shelter_rows, parent, false);
            } else {
                listItem = LayoutInflater.from(mContext).inflate(R.layout.restaurant_rows, parent, false);
            }
        }

        if (row.getType().contains("elt")) {
            listItem = LayoutInflater.from(mContext).inflate(R.layout.shelter_rows, parent, false);
        } else {
            listItem = LayoutInflater.from(mContext).inflate(R.layout.restaurant_rows, parent, false);
        }

        TextView nameView = listItem.findViewById(R.id.shelterName);
        TextView addressView = listItem.findViewById(R.id.shelterDistance);
        TextView hours = listItem.findViewById(R.id.shelterHours);
        TextView managerView = listItem.findViewById(R.id.shelterManagerName);
        inMap = listItem.findViewById(R.id.map);
        moreInfo = listItem.findViewById(R.id.moreInfo);
        call = listItem.findViewById(R.id.phone);
        website = listItem.findViewById(R.id.web);

        if (row.getType().contains("elt")) {
            TextView special = listItem.findViewById(R.id.special);
            if (special != null)
                special.setText(row.getStatus().trim());
        }

        nameView.setPaintFlags(nameView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        nameView.setText(row.getName());
        hours.setText(getHours(row.getOpeningHours()));


        double dis = Double.parseDouble(row.getDistance());
        if (row.getUnits().contains("i")) {
            dis *= 69;
        } else {
            dis *= 111;
        }
        final boolean map = row.isInMap();
        if (map) {

        } else {
            inMap.setBackgroundColor(0XFFd5caca);
        }

        dis = Math.round(dis * 10) / 10d;
        addressView.setText(dis + " " + row.getUnits());

        managerView.setText(row.getManagerName());

        //DONE Write code for View More Info and View In Map buttons

        inMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //DONE Write code to show the location in map.
                if (map) {
                    OutputStreamWriter outputStreamWriter = null;
                    try {
                        outputStreamWriter = new OutputStreamWriter(mContext.openFileOutput("inMap.txt", Context.MODE_PRIVATE));
                        outputStreamWriter.write("" + row.getLat() + " " + row.getLon());
                        outputStreamWriter.close();
                        Home t = (Home) mContext;
                        Singleton.setShowInMap(true);
                        Singleton.setLat(row.getLat());
                        Singleton.setLon(row.getLon());
                        Singleton.setName(row.getName());
                        Singleton.setPhone(row.getContactPhone());
                        Singleton.setAddress(row.getAddress());
                        Singleton.setHours(row.getOpeningHours());
                        Singleton.setType(row.getType());
                        Singleton.setWebsite(row.getWebsite());
                        makeToast("FIRSTTIME: " + FragmentMap.firsttime);
                        if (FragmentMap.firsttime == 0)
                            FragmentMap.animateLocation(row.getLat(), row.getLon());

                        TabLayout layout = (TabLayout) (t.findViewById(R.id.tabs));
                        layout.getTabAt(2).select();
                    } catch (Exception e) {
                        makeToast("ERROR: " + e.toString() + "\nPlease try again.");
                    }
                } else {
                    makeToast("You have specified in settings that you don't want to view " + row.getType() + "s in the map.");
                }
            }
        });

        moreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //DONE Write code to show the Alert Dialog.
                Dialog show = new Dialog(mContext);
                show.requestWindowFeature(Window.FEATURE_NO_TITLE);
                if (row.getType().contains("est"))
                    show.setContentView(R.layout.show_more_info2);
                else
                    show.setContentView(R.layout.show_more_info3);
                show.setCancelable(true);
                show.getWindow().setBackgroundDrawableResource(R.color.transparent);
                ImageView web, pho;

                TextView title = show.findViewById(R.id.title);
                title.setPaintFlags(title.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                title.setText(row.getName());

                TextView location = show.findViewById(R.id.location);
                location.setText(row.getType());

                TextView address = show.findViewById(R.id.address);
                address.setText(row.getAddress());

                TextView specialText = show.findViewById(R.id.special);
                TextView special = show.findViewById(R.id.specialT);
                if (!row.getType().contains("elt")) {
                    specialText.setText("# of Donations: ");
                }
                special.setText(row.getStatus());

                TextView manager = show.findViewById(R.id.manager);
                manager.setText(row.getManagerName());

                //DONE IF they are a shelter, get and display the amount of food needed. (ID: amount)
                if (row.getType().contains("elt")) {
                    TextView amount = show.findViewById(R.id.amount);
                    if (amount != null)
                        amount.setText(row.getAmount());
                }

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
                ss.setSpan(new ForegroundColorSpan(0XFFa16767), 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                SpannableString s2 = new SpannableString(tue);
                s2.setSpan(new StyleSpan(Typeface.BOLD), 0, 4, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                s2.setSpan(new ForegroundColorSpan(0XFFa16767), 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                SpannableString s3 = new SpannableString(wed);
                s3.setSpan(new StyleSpan(Typeface.BOLD), 0, 4, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                s3.setSpan(new ForegroundColorSpan(0XFFa16767), 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                SpannableString s4 = new SpannableString(thu);
                s4.setSpan(new StyleSpan(Typeface.BOLD), 0, 4, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                s4.setSpan(new ForegroundColorSpan(0XFFa16767), 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                SpannableString s5 = new SpannableString(fri);
                s5.setSpan(new StyleSpan(Typeface.BOLD), 0, 4, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                s5.setSpan(new ForegroundColorSpan(0XFFa16767), 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                SpannableString s6 = new SpannableString(sat);
                s6.setSpan(new StyleSpan(Typeface.BOLD), 0, 4, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                s6.setSpan(new ForegroundColorSpan(0XFFa16767), 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                SpannableString s7 = new SpannableString(sun);
                s7.setSpan(new ForegroundColorSpan(0XFFa16767), 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                s7.setSpan(new StyleSpan(Typeface.BOLD), 0, 4, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);

                String m = ss.toString();
                String rf = s2.toString();

                hours.setText(TextUtils.concat(ss, "\n", s2, "\n", s3, "\n", s4, "\n", s5, "\n", s6, "\n", s7));
                //hours.setText(getHours(row.));

                web = show.findViewById(R.id.webIcon);
                pho = show.findViewById(R.id.phoneIcon);

                pho.setOnTouchListener(new View.OnTouchListener() {
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

                if (row.getWebsite().contains("Not ")) {
                    RelativeLayout back = show.findViewById(R.id.imgB);
                    back.setBackgroundColor(0XFFbfbfbf);
                }
                web.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (row.getWebsite().contains("Not ")) {
                            makeToast(row.getName() + " does not have a website.");
                        } else {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(row.getWebsite()));
                            mContext.startActivity(intent);
                        }
                        return false;
                    }
                });
                TextView distance = show.findViewById(R.id.distance);
                double dif = row.getDis();

                if (row.getUnits().equals("mi")) {
                    dif *= 69;
                } else
                    dif *= 110;

                dif = Math.round(dif * 10) / 10d;
                distance.setText(dif + " " + row.getUnits());

                TextView phone = show.findViewById(R.id.phone);
                phone.setText(row.getContactPhone());

                show.show();
            }
        });

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

        if (row.getWebsite().contains("Not av")) {
            website.setVisibility(View.INVISIBLE);
        } else {
            website.setVisibility(View.VISIBLE);
            website.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(row.getWebsite()));
                    mContext.startActivity(intent);
                    return false;
                }
            });
        }

        if (row.isSame()) {
            ScrollView rel = listItem.findViewById(R.id.back);
            rel.setBackgroundColor(0xFFffe6e7);
        } else {
            ScrollView rel = listItem.findViewById(R.id.back);
            rel.setBackgroundColor(0xFFffffff);
        }


        return listItem;
    }

    private void makeToast(String s) {
        Toast.makeText(mContext, s, Toast.LENGTH_LONG).show();
    }

    private String getHours(String openingHours) {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        String next = "";
        String result = "";
        int part = 0;
        switch (day) {
            case Calendar.SUNDAY:
                part = openingHours.indexOf("Sunday");
                break;
            case Calendar.MONDAY:
                part = openingHours.indexOf("Monday");
                next = "Tue";
                break;
            case Calendar.TUESDAY:
                part = openingHours.indexOf("Tuesday");
                next = "Wed";
                break;
            case Calendar.WEDNESDAY:
                next = "Thu";
                part = openingHours.indexOf("Wednesday");
                break;
            case Calendar.THURSDAY:
                part = openingHours.indexOf("Thursday");
                next = "Fri";
                break;
            case Calendar.FRIDAY:
                part = openingHours.indexOf("Friday");
                next = "Sat";
                break;
            case Calendar.SATURDAY:
                part = openingHours.indexOf("Saturday");
                next = "Sun";
                break;
        }
        int firstS = openingHours.indexOf(" ", part + 1);
        if (Character.isDigit(openingHours.charAt(firstS + 1))) {
            if (day == 1) {
                result = openingHours.substring(firstS + 1, openingHours.length() - 3);
            } else {
                result = openingHours.substring(firstS + 1, openingHours.indexOf(next) - 3);
            }
            String splitter[] = result.split(",");
            if (splitter.length > 1) {
                result = splitter[0];
                int count = 0;
                for (String s : splitter) {
                    if (count != 0)
                        result += ",\n" + s;
                    count++;
                }
            }
            /*
            int firstPM = openingHours.indexOf("PM", firstS + 5);

            if (day != 1) {
                if (firstPM < openingHours.indexOf(next)) {
                    result = openingHours.substring(firstS + 1, firstPM + 2);
                    if (openingHours.length() > firstPM + 5) {
                        if (openingHours.charAt(firstPM + 2) == ',') {
                            result += ",\n" + openingHours.substring(firstPM + 4, openingHours.indexOf("PM", firstPM + 5) + 2);
                        }
                    }
                } else {
                    int firstAM = openingHours.indexOf("AM", firstS + 5);
                    int nextAM = openingHours.indexOf("AM", firstAM + 5);
                    result = openingHours.substring(firstS + 1, nextAM + 2);
                    if (openingHours.length() > nextAM + 5) {
                        if (openingHours.charAt(nextAM + 2) == ',') {
                            result += ",\n" + openingHours.substring(nextAM + 4, openingHours.indexOf("PM", firstPM + 5) + 2);
                        }
                    }
                }
            } else {
                result = openingHours.substring(firstS + 1, firstPM + 2);
                if (openingHours.length() > firstPM + 5) {
                    if (openingHours.charAt(firstPM + 2) == ',') {
                        result += ",\n" + openingHours.substring(firstPM + 4, openingHours.indexOf("PM", firstPM + 5) + 2);
                    }
                }
            }*/
        } else {
            if (day != 1) {
                int location = openingHours.indexOf(next);
                location -= 3;
                result = openingHours.substring(firstS + 1, location);
            } else {
                result = openingHours.substring(firstS + 1, openingHours.length() - 3);
            }
        }
        return result;
    }
}
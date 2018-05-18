package com.example.haolongwang.placessearch;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowAnimationFrameStats;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class DetailsAct extends AppCompatActivity {

    private String address;
    private String phone;
    private String rating;
    private String google;
    private String website;
    private String price = "$";
    private String det;
    private String placeid;
    private GeoDataClient mGeoDataClient;
    private Bitmap bitmap;
    private JSONArray photosarr;
    public static Bitmap[] bitmaps;
    private ListView listView;
    private int bms;
    private bitmaparr getbitps;
    private String[] photosrefers;
    private String[] photoswidths;
    private String[] photourls;
    private String jsonresponseid;
    private String jsonresponseidy;
    private String[] namereviews;
    private String[] ratingreviews;
    private String[] timereviews;
    private String[] reviewses;
    private String[] picsreviews;
    private Spinner spinner;
    private Spinner spinners;
    private double lat;
    private double lng;
    private double[] latlng = new double[2];
    private String[] author_url;
    private long aLong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        jsonresponseid = intent.getStringExtra("jsonresponseid");
        jsonresponseidy = intent.getStringExtra("jsonresponseidy");
        det = intent.getStringExtra("dets");

        if(jsonresponseid == null) {
            Intent intenteA = new Intent(DetailsAct.this, MainActivity.class);
            startActivityForResult(intenteA, 1);
        }
        else {
            if (det.equals("info")) {
                //requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
                setContentView(R.layout.detailsinfo);
                //getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);

                try {
                    JSONObject jsonObject = new JSONObject(jsonresponseid);
                    JSONObject result = jsonObject.getJSONObject("result");
                    address = result.getString("formatted_address");
                    phone = result.getString("formatted_phone_number");
                    rating = result.getString("rating");
                    google = result.getString("url");
                    if(result.getString("website") == null) {
                        website = result.getString("url");
                    } else {
                        website = result.getString("website");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                TextView addressdet = (TextView) this.findViewById(R.id.address_details);
                TextView phonedet = (TextView) this.findViewById(R.id.phone_details);
                TextView pricedet = (TextView) this.findViewById(R.id.price_details);
                RatingBar ratingdet = (RatingBar) this.findViewById(R.id.rating_details);
                TextView googledet = (TextView) this.findViewById(R.id.google_details);
                TextView websitedet = (TextView) this.findViewById(R.id.website_details);
                addressdet.setText(address);
                phonedet.setText(phone);
                ratingdet.setMax(500);
                double rated = Double.parseDouble(rating);
                int rateint = (new Double(rated*100)).intValue();
                ratingdet.setProgress(rateint);
                googledet.setText(google);
                websitedet.setText(website);
                pricedet.setText(price);

            } else if (det.equals("photos")) {
                setContentView(R.layout.detailsphotos);
                //listView = (ListView) this.findViewById(R.id.photos);
                //mGeoDataClient = Places.getGeoDataClient(this, null);
                try {
                    JSONObject jsonObject = new JSONObject(jsonresponseid);
                    JSONObject result = jsonObject.getJSONObject("result");
                    placeid = result.getString("place_id");
                    photosarr = result.getJSONArray("photos");
                    photosrefers = new String[photosarr.length()];
                    photoswidths = new String[photosarr.length()];
                    photourls = new String[photosarr.length()];
                    for (int p = 0; p < photosarr.length(); p++) {
                        JSONObject photoobject = photosarr.getJSONObject(p);
                        photosrefers[p] = photoobject.getString("photo_reference");
                        photoswidths[p] = photoobject.getString("width");
                        photourls[p] = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=" + photoswidths[p] + "&photoreference=" + photosrefers[p] + "&key=AIzaSyB6uoZXm7bR-3Hay3bpMSFdf1iAhnwx11E";
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //bitmaps = new Bitmap[photosarr.length()];

            /*bitmaparr bitmps = new bitmaparr() {
                @Override
                public void bitmapobtain(int bm, Bitmap bitp) {
                    bitmaps[bm] = bitp;
                }
            };

            for (int i = 0; i < photosarr.length(); i++){
                getBitmap(1, bitmps);
            }*/
                listView = (ListView) this.findViewById(R.id.photos);
                photoAdaptor PhotoAdaptor = new photoAdaptor();
                listView.setAdapter(PhotoAdaptor);
            } else if (det.equals("map")) {
                try {
                    JSONObject jsonObject = new JSONObject(jsonresponseid);
                    JSONObject result = jsonObject.getJSONObject("result");
                    address = result.getString("formatted_address");
                    lat = result.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                    lng = result.getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                    latlng[0] = lat;
                    latlng[1] = lng;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent intentemap = new Intent(DetailsAct.this, MapPane.class);
                intentemap.putExtra("TLocation", address);
                intentemap.putExtra("latlng", latlng);
                intentemap.putExtra("jsonresponseid", jsonresponseid);
                intentemap.putExtra("jsonresponseidy", jsonresponseidy);
                startActivityForResult(intentemap, 1);
            } else if (det.equals("reviews")) {
                setContentView(R.layout.detailsreviews);
                listView = (ListView) this.findViewById(R.id.listreviews);

                try {
                    JSONObject jsonObject = new JSONObject(jsonresponseid);
                    JSONObject result = jsonObject.getJSONObject("result");
                    JSONArray reviewsarr = result.getJSONArray("reviews");
                    namereviews = new String[reviewsarr.length()];
                    ratingreviews = new String[reviewsarr.length()];
                    timereviews = new String[reviewsarr.length()];
                    reviewses = new String[reviewsarr.length()];
                    picsreviews = new String[reviewsarr.length()];
                    author_url = new String[reviewsarr.length()];
                    for (int r = 0; r < reviewsarr.length(); r++) {
                        JSONObject reviewobject = reviewsarr.getJSONObject(r);
                        namereviews[r] = reviewobject.getString("author_name");
                        ratingreviews[r] = reviewobject.getString("rating");
                        timereviews[r] = reviewobject.getString("time");
                        aLong = Long.parseLong(timereviews[r]);
                        SimpleDateFormat lsdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date lDate = new Date(aLong*1000);
                        timereviews[r] = lsdFormat.format(lDate);
                        reviewses[r] = reviewobject.getString("text");
                        picsreviews[r] = reviewobject.getString("profile_photo_url");
                        author_url[r] = reviewobject.getString("author_url");
                    }
                    reviewsAdaptor ReviewsAdaptor = new reviewsAdaptor();
                    listView.setAdapter(ReviewsAdaptor);
                } catch (JSONException e) {
                    noneAdaptor NoneAdaptor = new noneAdaptor();
                    listView.setAdapter(NoneAdaptor);
                }

                spinner = (Spinner) this.findViewById(R.id.googleyelp);
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.googleyelp, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                spinners = (Spinner) this.findViewById(R.id.gyrates);
                ArrayAdapter<CharSequence> adapters = ArrayAdapter.createFromResource(this, R.array.gyrates, android.R.layout.simple_spinner_item);
                adapters.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinners.setAdapter(adapters);

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String gy = (String) spinner.getItemAtPosition(position);
                        Toast.makeText(DetailsAct.this, gy, Toast.LENGTH_SHORT).show();
                        if (gy == (String) spinner.getItemAtPosition(0)) {
                            try {
                                JSONObject jsonObject = new JSONObject(jsonresponseid);
                                JSONObject result = jsonObject.getJSONObject("result");
                                JSONArray reviewsarr = result.getJSONArray("reviews");
                                namereviews = new String[reviewsarr.length()];
                                ratingreviews = new String[reviewsarr.length()];
                                timereviews = new String[reviewsarr.length()];
                                reviewses = new String[reviewsarr.length()];
                                picsreviews = new String[reviewsarr.length()];
                                author_url = new String[reviewsarr.length()];
                                for (int r = 0; r < reviewsarr.length(); r++) {
                                    JSONObject reviewobject = reviewsarr.getJSONObject(r);
                                    namereviews[r] = reviewobject.getString("author_name");
                                    ratingreviews[r] = reviewobject.getString("rating");
                                    timereviews[r] = reviewobject.getString("time");
                                    aLong = Long.parseLong(timereviews[r]);
                                    SimpleDateFormat lsdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    Date lDate = new Date(aLong*1000);
                                    timereviews[r] = lsdFormat.format(lDate);
                                    reviewses[r] = reviewobject.getString("text");
                                    picsreviews[r] = reviewobject.getString("profile_photo_url");
                                    author_url[r] = reviewobject.getString("author_url");
                                }
                                reviewsAdaptor ReviewsAdaptor = new reviewsAdaptor();
                                listView.setAdapter(ReviewsAdaptor);
                            } catch (JSONException e) {
                                noneAdaptor NoneAdaptor = new noneAdaptor();
                                listView.setAdapter(NoneAdaptor);
                            }

                            spinners.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    String selects = (String) spinners.getItemAtPosition(position);
                                    Toast.makeText(DetailsAct.this, selects, Toast.LENGTH_SHORT).show();
                                    if (selects == (String) spinners.getItemAtPosition(0)) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(jsonresponseid);
                                            JSONObject result = jsonObject.getJSONObject("result");
                                            JSONArray reviewsarr = result.getJSONArray("reviews");
                                            namereviews = new String[reviewsarr.length()];
                                            ratingreviews = new String[reviewsarr.length()];
                                            timereviews = new String[reviewsarr.length()];
                                            reviewses = new String[reviewsarr.length()];
                                            picsreviews = new String[reviewsarr.length()];
                                            author_url = new String[reviewsarr.length()];
                                            for (int r = 0; r < reviewsarr.length(); r++) {
                                                JSONObject reviewobject = reviewsarr.getJSONObject(r);
                                                namereviews[r] = reviewobject.getString("author_name");
                                                ratingreviews[r] = reviewobject.getString("rating");
                                                timereviews[r] = reviewobject.getString("time");
                                                aLong = Long.parseLong(timereviews[r]);
                                                SimpleDateFormat lsdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                Date lDate = new Date(aLong*1000);
                                                timereviews[r] = lsdFormat.format(lDate);
                                                reviewses[r] = reviewobject.getString("text");
                                                picsreviews[r] = reviewobject.getString("profile_photo_url");
                                                author_url[r] = reviewobject.getString("author_url");
                                            }
                                            reviewsAdaptor ReviewsAdaptor = new reviewsAdaptor();
                                            listView.setAdapter(ReviewsAdaptor);
                                        } catch (JSONException e) {
                                            noneAdaptor NoneAdaptor = new noneAdaptor();
                                            listView.setAdapter(NoneAdaptor);
                                        }
                                    } else if (selects == (String) spinners.getItemAtPosition(1)) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(jsonresponseid);
                                            JSONObject result = jsonObject.getJSONObject("result");
                                            JSONArray reviewsarr = result.getJSONArray("reviews");
                                            JSONArray MRreviews = new JSONArray();
                                            List<JSONObject> jsonObjectList = new ArrayList<JSONObject>();
                                            for (int i = 0; i < reviewsarr.length(); i++) {
                                                jsonObjectList.add(reviewsarr.getJSONObject(i));
                                            }
                                            Collections.sort(jsonObjectList, new Comparator<JSONObject>() {
                                                private String key = "rating";

                                                @Override
                                                public int compare(JSONObject o1, JSONObject o2) {
                                                    String oA = new String();
                                                    String oB = new String();
                                                    try {
                                                        oA = o1.get(key).toString();
                                                        oB = o2.get(key).toString();
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                    return oB.compareTo(oA);
                                                }
                                            });
                                            for (int j = 0; j < reviewsarr.length(); j++) {
                                                MRreviews.put(jsonObjectList.get(j));
                                            }
                                            namereviews = new String[reviewsarr.length()];
                                            ratingreviews = new String[reviewsarr.length()];
                                            timereviews = new String[reviewsarr.length()];
                                            reviewses = new String[reviewsarr.length()];
                                            picsreviews = new String[reviewsarr.length()];
                                            author_url = new String[reviewsarr.length()];
                                            for (int r = 0; r < reviewsarr.length(); r++) {
                                                JSONObject reviewobject = MRreviews.getJSONObject(r);
                                                namereviews[r] = reviewobject.getString("author_name");
                                                ratingreviews[r] = reviewobject.getString("rating");
                                                timereviews[r] = reviewobject.getString("time");
                                                aLong = Long.parseLong(timereviews[r]);
                                                SimpleDateFormat lsdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                Date lDate = new Date(aLong*1000);
                                                timereviews[r] = lsdFormat.format(lDate);
                                                reviewses[r] = reviewobject.getString("text");
                                                picsreviews[r] = reviewobject.getString("profile_photo_url");
                                                author_url[r] = reviewobject.getString("author_url");
                                            }
                                            reviewsAdaptor ReviewsAdaptor = new reviewsAdaptor();
                                            listView.setAdapter(ReviewsAdaptor);
                                        } catch (JSONException e) {
                                            noneAdaptor NoneAdaptor = new noneAdaptor();
                                            listView.setAdapter(NoneAdaptor);
                                        }
                                    } else if (selects == (String) spinners.getItemAtPosition(2)) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(jsonresponseid);
                                            JSONObject result = jsonObject.getJSONObject("result");
                                            JSONArray reviewsarr = result.getJSONArray("reviews");
                                            JSONArray LRreviews = new JSONArray();
                                            List<JSONObject> jsonObjectList = new ArrayList<JSONObject>();
                                            for (int i = 0; i < reviewsarr.length(); i++) {
                                                jsonObjectList.add(reviewsarr.getJSONObject(i));
                                            }
                                            Collections.sort(jsonObjectList, new Comparator<JSONObject>() {
                                                private String key = "rating";

                                                @Override
                                                public int compare(JSONObject o1, JSONObject o2) {
                                                    String oA = new String();
                                                    String oB = new String();
                                                    try {
                                                        oA = o1.get(key).toString();
                                                        oB = o2.get(key).toString();
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                    return oA.compareTo(oB);
                                                }
                                            });
                                            for (int j = 0; j < reviewsarr.length(); j++) {
                                                LRreviews.put(jsonObjectList.get(j));
                                            }
                                            namereviews = new String[reviewsarr.length()];
                                            ratingreviews = new String[reviewsarr.length()];
                                            timereviews = new String[reviewsarr.length()];
                                            reviewses = new String[reviewsarr.length()];
                                            picsreviews = new String[reviewsarr.length()];
                                            author_url = new String[reviewsarr.length()];
                                            for (int r = 0; r < reviewsarr.length(); r++) {
                                                JSONObject reviewobject = LRreviews.getJSONObject(r);
                                                namereviews[r] = reviewobject.getString("author_name");
                                                ratingreviews[r] = reviewobject.getString("rating");
                                                timereviews[r] = reviewobject.getString("time");
                                                aLong = Long.parseLong(timereviews[r]);
                                                SimpleDateFormat lsdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                Date lDate = new Date(aLong*1000);
                                                timereviews[r] = lsdFormat.format(lDate);
                                                reviewses[r] = reviewobject.getString("text");
                                                picsreviews[r] = reviewobject.getString("profile_photo_url");
                                                author_url[r] = reviewobject.getString("author_url");
                                            }
                                            reviewsAdaptor ReviewsAdaptor = new reviewsAdaptor();
                                            listView.setAdapter(ReviewsAdaptor);
                                        } catch (JSONException e) {
                                            noneAdaptor NoneAdaptor = new noneAdaptor();
                                            listView.setAdapter(NoneAdaptor);
                                        }
                                    } else if (selects == (String) spinners.getItemAtPosition(3)) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(jsonresponseid);
                                            JSONObject result = jsonObject.getJSONObject("result");
                                            JSONArray reviewsarr = result.getJSONArray("reviews");
                                            JSONArray MRTreviews = new JSONArray();
                                            List<JSONObject> jsonObjectList = new ArrayList<JSONObject>();
                                            for (int i = 0; i < reviewsarr.length(); i++) {
                                                jsonObjectList.add(reviewsarr.getJSONObject(i));
                                            }
                                            Collections.sort(jsonObjectList, new Comparator<JSONObject>() {
                                                private String key = "time";

                                                @Override
                                                public int compare(JSONObject o1, JSONObject o2) {
                                                    String oA = new String();
                                                    String oB = new String();
                                                    try {
                                                        oA = o1.get(key).toString();
                                                        oB = o2.get(key).toString();
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                    return oB.compareTo(oA);
                                                }
                                            });
                                            for (int j = 0; j < reviewsarr.length(); j++) {
                                                MRTreviews.put(jsonObjectList.get(j));
                                            }
                                            namereviews = new String[reviewsarr.length()];
                                            ratingreviews = new String[reviewsarr.length()];
                                            timereviews = new String[reviewsarr.length()];
                                            reviewses = new String[reviewsarr.length()];
                                            picsreviews = new String[reviewsarr.length()];
                                            author_url = new String[reviewsarr.length()];
                                            for (int r = 0; r < reviewsarr.length(); r++) {
                                                JSONObject reviewobject = MRTreviews.getJSONObject(r);
                                                namereviews[r] = reviewobject.getString("author_name");
                                                ratingreviews[r] = reviewobject.getString("rating");
                                                timereviews[r] = reviewobject.getString("time");
                                                aLong = Long.parseLong(timereviews[r]);
                                                SimpleDateFormat lsdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                Date lDate = new Date(aLong*1000);
                                                timereviews[r] = lsdFormat.format(lDate);
                                                reviewses[r] = reviewobject.getString("text");
                                                picsreviews[r] = reviewobject.getString("profile_photo_url");
                                                author_url[r] = reviewobject.getString("author_url");
                                            }
                                            reviewsAdaptor ReviewsAdaptor = new reviewsAdaptor();
                                            listView.setAdapter(ReviewsAdaptor);
                                        } catch (JSONException e) {
                                            noneAdaptor NoneAdaptor = new noneAdaptor();
                                            listView.setAdapter(NoneAdaptor);
                                        }
                                    } else if (selects == (String) spinners.getItemAtPosition(4)) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(jsonresponseid);
                                            JSONObject result = jsonObject.getJSONObject("result");
                                            JSONArray reviewsarr = result.getJSONArray("reviews");
                                            JSONArray LRTreviews = new JSONArray();
                                            List<JSONObject> jsonObjectList = new ArrayList<JSONObject>();
                                            for (int i = 0; i < reviewsarr.length(); i++) {
                                                jsonObjectList.add(reviewsarr.getJSONObject(i));
                                            }
                                            Collections.sort(jsonObjectList, new Comparator<JSONObject>() {
                                                private String key = "time";

                                                @Override
                                                public int compare(JSONObject o1, JSONObject o2) {
                                                    String oA = new String();
                                                    String oB = new String();
                                                    try {
                                                        oA = o1.get(key).toString();
                                                        oB = o2.get(key).toString();
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                    return oA.compareTo(oB);
                                                }
                                            });
                                            for (int j = 0; j < reviewsarr.length(); j++) {
                                                LRTreviews.put(jsonObjectList.get(j));
                                            }
                                            namereviews = new String[reviewsarr.length()];
                                            ratingreviews = new String[reviewsarr.length()];
                                            timereviews = new String[reviewsarr.length()];
                                            reviewses = new String[reviewsarr.length()];
                                            picsreviews = new String[reviewsarr.length()];
                                            author_url = new String[reviewsarr.length()];
                                            for (int r = 0; r < reviewsarr.length(); r++) {
                                                JSONObject reviewobject = LRTreviews.getJSONObject(r);
                                                namereviews[r] = reviewobject.getString("author_name");
                                                ratingreviews[r] = reviewobject.getString("rating");
                                                timereviews[r] = reviewobject.getString("time");
                                                aLong = Long.parseLong(timereviews[r]);
                                                SimpleDateFormat lsdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                Date lDate = new Date(aLong*1000);
                                                timereviews[r] = lsdFormat.format(lDate);
                                                reviewses[r] = reviewobject.getString("text");
                                                picsreviews[r] = reviewobject.getString("profile_photo_url");
                                                author_url[r] = reviewobject.getString("author_url");
                                            }
                                            reviewsAdaptor ReviewsAdaptor = new reviewsAdaptor();
                                            listView.setAdapter(ReviewsAdaptor);
                                        } catch (JSONException e) {
                                            noneAdaptor NoneAdaptor = new noneAdaptor();
                                            listView.setAdapter(NoneAdaptor);
                                        }
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {
                                }
                            });
                        } else if (gy == (String) spinner.getItemAtPosition(1)) {
                            try {
                                JSONObject jsonObject = new JSONObject(jsonresponseidy);
                                JSONArray reviewsarry = jsonObject.getJSONArray("reviews");
                                namereviews = new String[reviewsarry.length()];
                                ratingreviews = new String[reviewsarry.length()];
                                timereviews = new String[reviewsarry.length()];
                                reviewses = new String[reviewsarry.length()];
                                picsreviews = new String[reviewsarry.length()];
                                author_url = new String[reviewsarry.length()];
                                for (int r = 0; r < reviewsarry.length(); r++) {
                                    JSONObject reviewobject = reviewsarry.getJSONObject(r);
                                    namereviews[r] = reviewobject.getJSONObject("user").getString("name");
                                    ratingreviews[r] = reviewobject.getString("rating");
                                    timereviews[r] = reviewobject.getString("time_created");
                                    reviewses[r] = reviewobject.getString("text");
                                    picsreviews[r] = reviewobject.getJSONObject("user").getString("image_url");
                                    author_url[r] = reviewobject.getString("url");
                                }
                                reviewsAdaptor ReviewsAdaptor = new reviewsAdaptor();
                                listView.setAdapter(ReviewsAdaptor);
                            } catch (JSONException e) {
                                noneAdaptor NoneAdaptor = new noneAdaptor();
                                listView.setAdapter(NoneAdaptor);
                            }

                            spinners.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    String selects = (String) spinners.getItemAtPosition(position);
                                    Toast.makeText(DetailsAct.this, selects, Toast.LENGTH_SHORT).show();
                                    if (selects == (String) spinners.getItemAtPosition(0)) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(jsonresponseidy);
                                            JSONArray reviewsarry = jsonObject.getJSONArray("reviews");
                                            namereviews = new String[reviewsarry.length()];
                                            ratingreviews = new String[reviewsarry.length()];
                                            timereviews = new String[reviewsarry.length()];
                                            reviewses = new String[reviewsarry.length()];
                                            picsreviews = new String[reviewsarry.length()];
                                            author_url = new String[reviewsarry.length()];
                                            for (int r = 0; r < reviewsarry.length(); r++) {
                                                JSONObject reviewobject = reviewsarry.getJSONObject(r);
                                                namereviews[r] = reviewobject.getJSONObject("user").getString("name");
                                                ratingreviews[r] = reviewobject.getString("rating");
                                                timereviews[r] = reviewobject.getString("time_created");
                                                reviewses[r] = reviewobject.getString("text");
                                                picsreviews[r] = reviewobject.getJSONObject("user").getString("image_url");
                                                author_url[r] = reviewobject.getString("url");
                                            }
                                            reviewsAdaptor ReviewsAdaptor = new reviewsAdaptor();
                                            listView.setAdapter(ReviewsAdaptor);
                                        } catch (JSONException e) {
                                            noneAdaptor NoneAdaptor = new noneAdaptor();
                                            listView.setAdapter(NoneAdaptor);
                                        }
                                    } else if (selects == (String) spinners.getItemAtPosition(1)) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(jsonresponseidy);
                                            JSONArray reviewsarry = jsonObject.getJSONArray("reviews");
                                            JSONArray MRreviewsy = new JSONArray();
                                            List<JSONObject> jsonObjectList = new ArrayList<JSONObject>();
                                            for (int i = 0; i < reviewsarry.length(); i++) {
                                                jsonObjectList.add(reviewsarry.getJSONObject(i));
                                            }
                                            Collections.sort(jsonObjectList, new Comparator<JSONObject>() {
                                                private String key = "rating";

                                                @Override
                                                public int compare(JSONObject o1, JSONObject o2) {
                                                    String oA = new String();
                                                    String oB = new String();
                                                    try {
                                                        oA = o1.get(key).toString();
                                                        oB = o2.get(key).toString();
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                    return oB.compareTo(oA);
                                                }
                                            });
                                            for (int j = 0; j < reviewsarry.length(); j++) {
                                                MRreviewsy.put(jsonObjectList.get(j));
                                            }
                                            namereviews = new String[reviewsarry.length()];
                                            ratingreviews = new String[reviewsarry.length()];
                                            timereviews = new String[reviewsarry.length()];
                                            reviewses = new String[reviewsarry.length()];
                                            picsreviews = new String[reviewsarry.length()];
                                            author_url = new String[reviewsarry.length()];
                                            for (int r = 0; r < reviewsarry.length(); r++) {
                                                JSONObject reviewobject = MRreviewsy.getJSONObject(r);
                                                namereviews[r] = reviewobject.getJSONObject("user").getString("name");
                                                ratingreviews[r] = reviewobject.getString("rating");
                                                timereviews[r] = reviewobject.getString("time_created");
                                                reviewses[r] = reviewobject.getString("text");
                                                picsreviews[r] = reviewobject.getJSONObject("user").getString("image_url");
                                                author_url[r] = reviewobject.getString("url");
                                            }
                                            reviewsAdaptor ReviewsAdaptor = new reviewsAdaptor();
                                            listView.setAdapter(ReviewsAdaptor);
                                        } catch (JSONException e) {
                                            noneAdaptor NoneAdaptor = new noneAdaptor();
                                            listView.setAdapter(NoneAdaptor);
                                        }
                                    } else if (selects == (String) spinners.getItemAtPosition(2)) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(jsonresponseidy);
                                            JSONArray reviewsarry = jsonObject.getJSONArray("reviews");
                                            JSONArray LRreviewsy = new JSONArray();
                                            List<JSONObject> jsonObjectList = new ArrayList<JSONObject>();
                                            for (int i = 0; i < reviewsarry.length(); i++) {
                                                jsonObjectList.add(reviewsarry.getJSONObject(i));
                                            }
                                            Collections.sort(jsonObjectList, new Comparator<JSONObject>() {
                                                private String key = "rating";

                                                @Override
                                                public int compare(JSONObject o1, JSONObject o2) {
                                                    String oA = new String();
                                                    String oB = new String();
                                                    try {
                                                        oA = o1.get(key).toString();
                                                        oB = o2.get(key).toString();
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                    return oA.compareTo(oB);
                                                }
                                            });
                                            for (int j = 0; j < reviewsarry.length(); j++) {
                                                LRreviewsy.put(jsonObjectList.get(j));
                                            }
                                            namereviews = new String[reviewsarry.length()];
                                            ratingreviews = new String[reviewsarry.length()];
                                            timereviews = new String[reviewsarry.length()];
                                            reviewses = new String[reviewsarry.length()];
                                            picsreviews = new String[reviewsarry.length()];
                                            author_url = new String[reviewsarry.length()];
                                            for (int r = 0; r < reviewsarry.length(); r++) {
                                                JSONObject reviewobject = LRreviewsy.getJSONObject(r);
                                                namereviews[r] = reviewobject.getJSONObject("user").getString("name");
                                                ratingreviews[r] = reviewobject.getString("rating");
                                                timereviews[r] = reviewobject.getString("time_created");
                                                reviewses[r] = reviewobject.getString("text");
                                                picsreviews[r] = reviewobject.getJSONObject("user").getString("image_url");
                                                author_url[r] = reviewobject.getString("url");
                                            }
                                            reviewsAdaptor ReviewsAdaptor = new reviewsAdaptor();
                                            listView.setAdapter(ReviewsAdaptor);
                                        } catch (JSONException e) {
                                            noneAdaptor NoneAdaptor = new noneAdaptor();
                                            listView.setAdapter(NoneAdaptor);
                                        }
                                    } else if (selects == (String) spinners.getItemAtPosition(3)) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(jsonresponseidy);
                                            JSONArray reviewsarry = jsonObject.getJSONArray("reviews");
                                            JSONArray MRTreviewsy = new JSONArray();
                                            List<JSONObject> jsonObjectList = new ArrayList<JSONObject>();
                                            for (int i = 0; i < reviewsarry.length(); i++) {
                                                jsonObjectList.add(reviewsarry.getJSONObject(i));
                                            }
                                            Collections.sort(jsonObjectList, new Comparator<JSONObject>() {
                                                private String key = "time_created";

                                                @Override
                                                public int compare(JSONObject o1, JSONObject o2) {
                                                    String oA = new String();
                                                    String oB = new String();
                                                    try {
                                                        oA = o1.get(key).toString();
                                                        oB = o2.get(key).toString();
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                    return oB.compareTo(oA);
                                                }
                                            });
                                            for (int j = 0; j < reviewsarry.length(); j++) {
                                                MRTreviewsy.put(jsonObjectList.get(j));
                                            }
                                            namereviews = new String[reviewsarry.length()];
                                            ratingreviews = new String[reviewsarry.length()];
                                            timereviews = new String[reviewsarry.length()];
                                            reviewses = new String[reviewsarry.length()];
                                            picsreviews = new String[reviewsarry.length()];
                                            author_url = new String[reviewsarry.length()];
                                            for (int r = 0; r < reviewsarry.length(); r++) {
                                                JSONObject reviewobject = MRTreviewsy.getJSONObject(r);
                                                namereviews[r] = reviewobject.getJSONObject("user").getString("name");
                                                ratingreviews[r] = reviewobject.getString("rating");
                                                timereviews[r] = reviewobject.getString("time_created");
                                                reviewses[r] = reviewobject.getString("text");
                                                picsreviews[r] = reviewobject.getJSONObject("user").getString("image_url");
                                                author_url[r] = reviewobject.getString("url");
                                            }
                                            reviewsAdaptor ReviewsAdaptor = new reviewsAdaptor();
                                            listView.setAdapter(ReviewsAdaptor);
                                        } catch (JSONException e) {
                                            noneAdaptor NoneAdaptor = new noneAdaptor();
                                            listView.setAdapter(NoneAdaptor);
                                        }
                                    } else if (selects == (String) spinners.getItemAtPosition(4)) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(jsonresponseidy);
                                            JSONArray reviewsarry = jsonObject.getJSONArray("reviews");
                                            JSONArray LRTreviewsy = new JSONArray();
                                            List<JSONObject> jsonObjectList = new ArrayList<JSONObject>();
                                            for (int i = 0; i < reviewsarry.length(); i++) {
                                                jsonObjectList.add(reviewsarry.getJSONObject(i));
                                            }
                                            Collections.sort(jsonObjectList, new Comparator<JSONObject>() {
                                                private String key = "time_created";

                                                @Override
                                                public int compare(JSONObject o1, JSONObject o2) {
                                                    String oA = new String();
                                                    String oB = new String();
                                                    try {
                                                        oA = o1.get(key).toString();
                                                        oB = o2.get(key).toString();
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                    return oA.compareTo(oB);
                                                }
                                            });
                                            for (int j = 0; j < reviewsarry.length(); j++) {
                                                LRTreviewsy.put(jsonObjectList.get(j));
                                            }
                                            namereviews = new String[reviewsarry.length()];
                                            ratingreviews = new String[reviewsarry.length()];
                                            timereviews = new String[reviewsarry.length()];
                                            reviewses = new String[reviewsarry.length()];
                                            picsreviews = new String[reviewsarry.length()];
                                            author_url = new String[reviewsarry.length()];
                                            for (int r = 0; r < reviewsarry.length(); r++) {
                                                JSONObject reviewobject = LRTreviewsy.getJSONObject(r);
                                                namereviews[r] = reviewobject.getJSONObject("user").getString("name");
                                                ratingreviews[r] = reviewobject.getString("rating");
                                                timereviews[r] = reviewobject.getString("time_created");
                                                reviewses[r] = reviewobject.getString("text");
                                                picsreviews[r] = reviewobject.getJSONObject("user").getString("image_url");
                                                author_url[r] = reviewobject.getString("url");
                                            }
                                            reviewsAdaptor ReviewsAdaptor = new reviewsAdaptor();
                                            listView.setAdapter(ReviewsAdaptor);
                                        } catch (JSONException e) {
                                            noneAdaptor NoneAdaptor = new noneAdaptor();
                                            listView.setAdapter(NoneAdaptor);
                                        }
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {
                                }
                            });
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            }
        }
    }

    class reviewsAdaptor extends BaseAdapter{
        @Override
        public int getCount(){
            return namereviews.length;
        }
        @Override
        public Object getItem(int position){
            return null;
        }
        @Override
        public long getItemId(int position){
            return 0;
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent){
            View view = getLayoutInflater().inflate(R.layout.samplereviews, null);
            TextView namereView = (TextView) view.findViewById(R.id.namereviews);
            RatingBar ratingreView = (RatingBar) view.findViewById(R.id.ratingreviews);
            TextView timereView = (TextView) view.findViewById(R.id.timereviews);
            TextView reviewesView = (TextView) view.findViewById(R.id.reviewses);
            getImage respImage = new getImage();
            respImage.execute(picsreviews[position]);
            AsyncResImage Asyncicon = new AsyncResImage() {
                View v;
                @Override
                public void AsyncResImage(View view){
                    v = view;
                }
                @Override
                public void onImageReceived(Bitmap Data) {
                    ImageView picsreView = (ImageView) v.findViewById(R.id.picses);
                    picsreView.setImageBitmap(Data);
                    picsreView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Uri uri = Uri.parse(author_url[position]);
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(uri);
                            startActivity(intent);
                        }
                    });
                }
            };
            Asyncicon.AsyncResImage(view);
            respImage.setOnAsyncResponse(Asyncicon);
            namereView.setText(namereviews[position]);
            ratingreView.setMax(500);
            double rated = Double.parseDouble(ratingreviews[position]);
            int rateint = (new Double(rated*100)).intValue();
            ratingreView.setProgress(rateint);
            timereView.setText(timereviews[position]);
            reviewesView.setText(reviewses[position]);
            return view;
        }
    }

    class noneAdaptor extends BaseAdapter{
        @Override
        public int getCount(){
            return 1;
        }
        @Override
        public Object getItem(int position){
            return null;
        }
        @Override
        public long getItemId(int position){
            return 0;
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent){
            View view = getLayoutInflater().inflate(R.layout.none, null);
            TextView none = (TextView) view.findViewById(R.id.none);
            none.setText("No records");

            return view;
        }
    }

    class photoAdaptor extends BaseAdapter {
        @Override
        public int getCount(){
            return photosarr.length();
        }
        @Override
        public Object getItem(int position){
            return null;
        }
        @Override
        public long getItemId(int position){
            return 0;
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent){
            /*ViewHolder viewHolder = null;
            if(convertView == null){
                convertView = getLayoutInflater().inflate(R.layout.detailsphotoses, null);
                viewHolder = new ViewHolder();
                viewHolder.ImageHolder = (ImageView) convertView.findViewById(R.id.photoses);
                convertView.setTag(viewHolder);
            }
            else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            bitmaparr bitmps = new bitmaparr() {
                @Override
                public void bitmapobtain(int bm, Bitmap bitp) {
                    bitmaps[bm] = bitp;
                }
            };
            getBitmap(position, bitmps);
            viewHolder.ImageHolder.setImageBitmap(bitmaps[position]);
            return convertView;*/
            View view = getLayoutInflater().inflate(R.layout.detailsphotoses, null);
            getImage respImage = new getImage();
            respImage.execute(photourls[position]);
            AsyncResImage Asyncicon = new AsyncResImage() {
                View v;
                @Override
                public void AsyncResImage(View view){
                    v = view;
                }
                @Override
                public void onImageReceived(Bitmap Data) {
                    ImageView photosView = (ImageView) v.findViewById(R.id.photoses);
                    photosView.setImageBitmap(Data);
                }
            };
            Asyncicon.AsyncResImage(view);
            respImage.setOnAsyncResponse(Asyncicon);
            return view;
        }
        class ViewHolder{
            private ImageView ImageHolder;
        }
    }

    public void getBitmap(int bm, bitmaparr getbitp){
        bms = bm;
        getbitps = getbitp;
        final Task<PlacePhotoMetadataResponse> photoMetadataResponse = mGeoDataClient.getPlacePhotos(placeid);
        photoMetadataResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                // Get the list of photos.
                PlacePhotoMetadataResponse photos = task.getResult();
                // Get the PlacePhotoMetadataBuffer (metadata for all of the photos).
                PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                // Get the first photo in the list.
                PlacePhotoMetadata photoMetadata = photoMetadataBuffer.get(bms);
                // Get a full-size bitmap for the photo.
                Task<PlacePhotoResponse> photoResponse = mGeoDataClient.getPhoto(photoMetadata);
                photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                        PlacePhotoResponse photo = task.getResult();
                        bitmap = photo.getBitmap();
                        getbitps.bitmapobtain(bms, bitmap);
                    }
                });
                photoMetadataBuffer.release();
            }
        });
    }

    public void info(View view){
        det = "info";
        Intent intent = new Intent(DetailsAct.this, DetailsAct.class);
        intent.putExtra("dets", det);
        intent.putExtra("jsonresponseid", jsonresponseid);
        intent.putExtra("jsonresponseidy", jsonresponseidy);
        startActivityForResult(intent, 1);
    }

    public void photo(View view){
        det = "photos";
        Intent intent = new Intent(DetailsAct.this, DetailsAct.class);
        intent.putExtra("dets", det);
        intent.putExtra("jsonresponseid", jsonresponseid);
        intent.putExtra("jsonresponseidy", jsonresponseidy);
        startActivityForResult(intent, 1);
    }

    public void map(View view){
        det = "map";
        Intent intent = new Intent(DetailsAct.this, DetailsAct.class);
        intent.putExtra("dets", det);
        intent.putExtra("jsonresponseid", jsonresponseid);
        intent.putExtra("jsonresponseidy", jsonresponseidy);
        startActivityForResult(intent, 1);
    }

    public void reviews(View view){
        det = "reviews";
        Intent intent = new Intent(DetailsAct.this, DetailsAct.class);
        intent.putExtra("dets", det);
        intent.putExtra("jsonresponseid", jsonresponseid);
        intent.putExtra("jsonresponseidy", jsonresponseidy);
        startActivityForResult(intent, 1);
    }

    public void twitter(View view){
        String twitterurl="https://twitter.com/intent/tweet?text=Check out "+address+" located at "+address+". Website: "+website+"&hashtags=TravelAndEntertainmentSearch";
        Uri uri = Uri.parse(twitterurl);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        startActivity(intent);
    }

}

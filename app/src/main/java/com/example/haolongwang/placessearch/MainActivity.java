package com.example.haolongwang.placessearch;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private EditText keywordView;
    private AutoCompleteTextView locationView;
    private String swith;
    private ListView listView;
    private int pos;
    private String category = "Default";
    private Spinner spinner;
    private RadioGroup group;
    private RadioButton radioButton1;
    private RadioButton radioButton2;
    private String locationss = "University of Southern California";
    private boolean cancel = false;
    private boolean editA = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_main);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);*/

        Intent intent = getIntent();
        if (intent.getStringExtra("swiths") == null) {
            swith = "s";
        } else {
            swith = intent.getStringExtra("swiths");
        }

        if (swith.equals("s")) {
            setContentView(R.layout.activity_main);

            spinner = (Spinner) findViewById(R.id.category);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.category_array, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    category = (String) spinner.getItemAtPosition(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            keywordView = (EditText) findViewById(R.id.keyword);
            locationView = (AutoCompleteTextView) findViewById(R.id.location);
            locationView.setEnabled(false);
            editA = false;
            group = (RadioGroup) findViewById(R.id.radiogroup);
            radioButton1 = (RadioButton) findViewById(R.id.radio1);
            radioButton2 = (RadioButton) findViewById(R.id.radio2);
            radioButton1.setChecked(true);

            group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if (radioButton1.isChecked()) {
                        locationss = "University of Southern California";
                        locationView.setEnabled(false);
                        editA = false;
                        loadjson resp = new loadjson();
                        resp.execute("http://ip-api.com/json");
                        resp.setOnAsyncResponse(new AsyncResponse() {
                            @Override
                            public void onDataReceived(String Data) {
                            }
                        });
                    }
                    else if (radioButton2.isChecked()){
                        locationView.setEnabled(true);
                        editA = true;
                    }
                }
            });

            String[] autoStrings = new String[] {
                    "University of Southern California",
                    "Golden Gate University",
                    "Mount Saint Mary's University, Los Angeles",
                    "West Coast University - Center for Graduate Studies",
                    "Bryan University Los Angeles",
                    "Pacific States University",
                    "Southwestern University School",
                    "Langer's Delicatessen-Restaurant",
                    "Holbox",
                    "Guelaguetza Restaurant",
                    "Patina Restaurant",
                    "800 Degrees Woodfired Kitchen",
                    "Happy Tom's Restaurant",
                    "Angelini Osteria"
            };
            ArrayAdapter<String> adapterAuto = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_dropdown_item_1line, autoStrings);
            locationView.setAdapter(adapterAuto);

            Button onsubmit = (Button) findViewById(R.id.clear);
            onsubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clears();
                }
            });
        }
        else {
            if(ListAct.viewf==null){
                setContentView(R.layout.nofavor);
            }
            else{
                setContentView(ListAct.viewf);
                ListView listViewf = (ListView) ListAct.viewf.findViewById(R.id.listfavors);
                final Intent finalIntentf = new Intent(MainActivity.this, DetailsAct.class);
                listViewf.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        pos = position;
                        String place = ListAct.placeids.get(pos);
                        String urlg = "http://csci571-phps.us-west-1.elasticbeanstalk.com/?placeid=" + place;
                        loadjson respg = new loadjson();
                        respg.execute(urlg);
                        respg.setOnAsyncResponse(new AsyncResponse() {
                            @Override
                            public void onDataReceived(String Data) {
                                String place = ListAct.placeids.get(pos);
                                String jsonresponseid = Data;
                                finalIntentf.putExtra("jsonresponseid", jsonresponseid);
                                String urly = "http://csci571-phps.us-west-1.elasticbeanstalk.com/?yelp=yelp&placeid=" + place;
                                loadjson respy = new loadjson();
                                respy.execute(urly);
                                respy.setOnAsyncResponse(new AsyncResponse() {
                                    @Override
                                    public void onDataReceived(String Data) {
                                        String jsonresponseidy = Data;
                                        finalIntentf.putExtra("jsonresponseidy", jsonresponseidy);
                                        String dets = "info";
                                        finalIntentf.putExtra("dets", dets);
                                        startActivityForResult(finalIntentf, 1);
                                    }
                                });
                            }
                        });
                    }
                });
            }
        }

    }

    public void search(View view) {
        onEditor();
        if(cancel==false) {
            EditText keywords = (EditText) findViewById(R.id.keyword);
            String keywordss = keywords.getText().toString();
            String categoryss = category;
            EditText distances = (EditText) findViewById(R.id.distance);
            String distancess = "";
            if (distances.getText().toString().equals("") || distances.getText().toString().equals(" ")) {
                distancess = "16090";
            } else {
                distancess = distances.getText().toString()+"999";
            }
            if(editA==true) {
                locationss = locationView.getText().toString();
            }
            String url = "http://csci571-phps.us-west-1.elasticbeanstalk.com/?keyword=" + keywordss + "&category=" + categoryss + "&distance=" + distancess + "&texts=" + locationss;
            //String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=34.0223519,-118.285117&radius=8000&type=default&keyword=football&key=AIzaSyB6uoZXm7bR-3Hay3bpMSFdf1iAhnwx11E";
            loadjson resp = new loadjson();
            resp.execute(url);
            resp.setOnAsyncResponse(new AsyncResponse() {
                @Override
                public void onDataReceived(String Data) {
                    String jsonresponse = Data;
                    Intent intent = new Intent(MainActivity.this, ListAct.class);
                    intent.putExtra("jsonresponse", jsonresponse);
                    startActivityForResult(intent, 1);
                }
            });
        }
        else {
            Toast.makeText(MainActivity.this, "Please fix all fields with errors", Toast.LENGTH_SHORT).show();
        }
    }

    public void onEditor() {

        keywordView.setError(null);
        locationView.setError(null);

        String keyword = keywordView.getText().toString();
        String location = locationView.getText().toString();
        View focusView = null;
        cancel = false;

        if(TextUtils.isEmpty(keyword)) {
            keywordView.setError("Please enter mandatory field");
            focusView = keywordView;
            cancel = true;
        }

        if(cancel) {
            focusView.requestFocus();
        }
        else {
            if (TextUtils.isEmpty(location)&&editA==true) {
                locationView.setError("Please enter mandatory field");
                focusView = locationView;
                cancel = true;
                focusView.requestFocus();
            }
        }

    }

    public void clears(){
        EditText keywords = (EditText) findViewById(R.id.keyword);
        keywords.setText("");
        EditText distances = (EditText) findViewById(R.id.distance);
        distances.setText("");
        AutoCompleteTextView locations = (AutoCompleteTextView) findViewById(R.id.location);
        locations.setText("");
        spinner = (Spinner) findViewById(R.id.category);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.category_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = (String) spinner.getItemAtPosition(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        ListAct.viewf = null;
        locationView.setEnabled(false);
        editA = false;
        radioButton1.setChecked(true);
    }

    public void s(View view){
        swith = "s";
        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        intent.putExtra("swiths", swith);
        startActivityForResult(intent, 1);
    }

    public void f(View view){
        swith = "f";
        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        intent.putExtra("swiths", swith);
        startActivityForResult(intent, 1);
    }

}

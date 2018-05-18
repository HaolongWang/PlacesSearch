package com.example.haolongwang.placessearch;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ListAct extends AppCompatActivity {

    private String[] icons;
    private String[] names;
    private String[] vicinitys;
    private String[] placeid;
    private String place;
    private String jsonresponse;
    private JSONObject nextpage;
    private Button previous;
    private Button favor;
    private ArrayList<String> favorsnames = new ArrayList<>();
    private ArrayList<String> favorsicons = new ArrayList<>();
    private ArrayList<String> favorsvicinitys = new ArrayList<>();
    public static View viewf;
    private int remo;
    public static ArrayList<String> placeids = new ArrayList<>();
    private JSONArray resultsx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);

        Intent intent = getIntent();
        jsonresponse = intent.getStringExtra("jsonresponse");

        if(jsonresponse == null) {
            Intent intente = new Intent(ListAct.this, MainActivity.class);
            startActivityForResult(intente, 1);
        }
        else {
            try {
                JSONObject jsonObjectx = new JSONObject(jsonresponse);
                resultsx = jsonObjectx.getJSONArray("results");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (resultsx.length() == 0) {
                setContentView(R.layout.none);
                TextView x = (TextView) findViewById(R.id.none);
                x.setText("No results");
            }
            else {
                try {
                    JSONObject jsonObject = new JSONObject(jsonresponse);
                    nextpage = jsonObject;
                    JSONArray results = jsonObject.getJSONArray("results");
                    if (results != null) {
                        icons = new String[results.length()];
                        names = new String[results.length()];
                        vicinitys = new String[results.length()];
                        placeid = new String[results.length()];
                        for (int i = 0; i < results.length(); i++) {
                            JSONObject jsonObject2 = results.getJSONObject(i);
                            icons[i] = jsonObject2.getString("icon");
                            names[i] = jsonObject2.getString("name");
                            vicinitys[i] = jsonObject2.getString("vicinity");
                            placeid[i] = jsonObject2.getString("place_id");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                ListView listView = (ListView) this.findViewById(R.id.list);
                jsonAdaptor JsonAdaptor = new jsonAdaptor();
                listView.setAdapter(JsonAdaptor);

                final Intent finalIntent = new Intent(ListAct.this, DetailsAct.class);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        place = placeid[position];
                        String urlg = "http://csci571-phps.us-west-1.elasticbeanstalk.com/?placeid=" + place;
                        //String url = "https://maps.googleapis.com/maps/api/place/details/json?placeid="+placeid[position]+"&key=AIzaSyB6uoZXm7bR-3Hay3bpMSFdf1iAhnwx11E";
                        loadjson respg = new loadjson();
                        respg.execute(urlg);
                        respg.setOnAsyncResponse(new AsyncResponse() {
                            @Override
                            public void onDataReceived(String Data) {
                                String jsonresponseid = Data;
                                finalIntent.putExtra("jsonresponseid", jsonresponseid);
                                String urly = "http://csci571-phps.us-west-1.elasticbeanstalk.com/?yelp=yelp&placeid=" + place;
                                loadjson respy = new loadjson();
                                respy.execute(urly);
                                respy.setOnAsyncResponse(new AsyncResponse() {
                                    @Override
                                    public void onDataReceived(String Data) {
                                        String jsonresponseidy = Data;
                                        finalIntent.putExtra("jsonresponseidy", jsonresponseidy);
                                        String dets = "info";
                                        finalIntent.putExtra("dets", dets);
                                        startActivityForResult(finalIntent, 1);
                                    }
                                });
                            }
                        });
                    }
                });

                Button next = (Button) this.findViewById(R.id.next);
                try {
                    String enabled = nextpage.getString("next_page_token");
                    next.setEnabled(true);
                } catch (JSONException e) {
                    next.setEnabled(false);
                }

                previous = (Button) this.findViewById(R.id.previous);

            }
        }

    }

    class jsonAdaptor extends BaseAdapter{
        @Override
        public int getCount(){
            return icons.length;
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
            View view = getLayoutInflater().inflate(R.layout.sample, null);
            TextView nameView = (TextView) view.findViewById(R.id.name);
            TextView vicView = (TextView) view.findViewById(R.id.vicinity);
            favor = (Button) view.findViewById((R.id.heart));
            getImage respImage = new getImage();
            respImage.execute(icons[position]);
            AsyncResImage Asyncicon = new AsyncResImage() {
                View v;
                @Override
                public void AsyncResImage(View view){
                    v = view;
                }
                @Override
                public void onImageReceived(Bitmap Data) {
                    ImageView iconView = (ImageView) v.findViewById(R.id.icon);
                    iconView.setImageBitmap(Data);
                }
            };
            Asyncicon.AsyncResImage(view);
            respImage.setOnAsyncResponse(Asyncicon);
            nameView.setText(names[position]);
            vicView.setText(vicinitys[position]);
            favor.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    favorsnames.add(names[position]);
                    favorsicons.add(icons[position]);
                    favorsvicinitys.add((vicinitys[position]));
                    placeids.add(placeid[position]);
                    //v.setBackground(getResources().getDrawable(R.drawable.heart_fill_red));
                    v.setBackgroundResource(R.drawable.heart_fill_red);
                    String fa = names[position] +" was added to favorites";
                    Toast.makeText(ListAct.this, fa, Toast.LENGTH_SHORT).show();
                    viewf = getLayoutInflater().inflate(R.layout.favor, null);
                    ListView listView = (ListView) viewf.findViewById(R.id.listfavors);
                    favorAdaptor FavorAdaptor = new favorAdaptor();
                    FavorAdaptor.favor(favorsnames, favorsicons, favorsvicinitys);
                    listView.setAdapter(FavorAdaptor);
                }
            });
            return view;
        }
    }

    class favorAdaptor extends BaseAdapter {
        ArrayList<String> getnames;
        ArrayList<String> getincons;
        ArrayList<String> getVicis;
        public void favor(ArrayList<String> getnames, ArrayList<String> getincons, ArrayList<String> getVicis){
            this.getnames = getnames;
            this.getincons = getincons;
            this.getVicis = getVicis;
        }
        @Override
        public int getCount(){
            return getnames.size();
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
            remo = position;
            View view = getLayoutInflater().inflate(R.layout.samplefavors, null);
            TextView nameView = (TextView) view.findViewById(R.id.name);
            TextView vicView = (TextView) view.findViewById(R.id.vicinity);
            final Button favor = (Button) view.findViewById((R.id.heart));
            getImage respImage = new getImage();
            respImage.execute(getincons.get(position));
            AsyncResImage Asyncicon = new AsyncResImage() {
                View v;
                @Override
                public void AsyncResImage(View view){
                    v = view;
                }
                @Override
                public void onImageReceived(Bitmap Data) {
                    ImageView iconView = (ImageView) v.findViewById(R.id.icon);
                    iconView.setImageBitmap(Data);
                }
            };
            Asyncicon.AsyncResImage(view);
            respImage.setOnAsyncResponse(Asyncicon);
            nameView.setText(getnames.get(position));
            vicView.setText(getVicis.get(position));
            favor.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    getnames.remove(remo);
                    getincons.remove(remo);
                    getVicis.remove(remo);
                    viewf = getLayoutInflater().inflate(R.layout.favor, null);
                    ListView listView = (ListView) viewf.findViewById(R.id.listfavors);
                    favorAdaptor FavorAdaptor = new favorAdaptor();
                    FavorAdaptor.favor(getnames, getincons, getVicis);
                    listView.setAdapter(FavorAdaptor);

                    if(getnames.isEmpty()){
                        viewf = null;
                    }

                    Intent intent = new Intent(ListAct.this, MainActivity.class);
                    intent.putExtra("swiths", "f");
                    startActivityForResult(intent, 1);
                }
                });
            return view;
        }
    }

    public void next(View view){
        try {
            JSONObject jsonObjectnext = new JSONObject(jsonresponse);
            String nextpageid = jsonObjectnext.getString("next_page_token");
                String urlnext = "http://csci571-phps.us-west-1.elasticbeanstalk.com/?pagetoken=" + nextpageid;
                loadjson respnext = new loadjson();
                respnext.execute(urlnext);
                respnext.setOnAsyncResponse(new AsyncResponse() {
                    @Override
                    public void onDataReceived(String Data) {
                        String jsonresponsenext = Data;
                        Intent intent = new Intent(ListAct.this, ListAct.class);
                        intent.putExtra("jsonresponse", jsonresponsenext);
                        startActivityForResult(intent, 1);
                    }
                });
            previous.setEnabled(true);
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void previous(View view){
        finish();
    }

    public void s(View view){
        String swith = "s";
        Intent intent = new Intent(ListAct.this, MainActivity.class);
        intent.putExtra("swiths", swith);
        startActivityForResult(intent, 1);
    }

    public void f(View view){
        String swith = "f";
        Intent intent = new Intent(ListAct.this, MainActivity.class);
        intent.putExtra("swiths", swith);
        startActivityForResult(intent, 1);
    }

}

package com.example.haolongwang.placessearch;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MapPane extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private AutoCompleteTextView FLocationT;
    private String TLocation;
    private String FLocation;
    private String urlmap;
    private String jsonresponseid;
    private String jsonresponseidy;
    private double lat;
    private double lng;
    private double[] latlng = new double[2];
    private boolean flag = false;
    private Spinner spinner;
    private  String mode;
    private boolean isSpinnerFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailsmap);
        spinner = (Spinner) this.findViewById(R.id.travelmodels);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.travel_models, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        FLocationT = (AutoCompleteTextView) this.findViewById(R.id.maplocation);
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
        ArrayAdapter<String> adapterAuto = new ArrayAdapter<String>(MapPane.this, android.R.layout.simple_dropdown_item_1line, autoStrings);
        FLocationT.setAdapter(adapterAuto);
        mode = "driving";
        FLocationT.addTextChangedListener(new ETChangeListener());
        Intent intent = getIntent();
        jsonresponseid = intent.getStringExtra("jsonresponseid");
        jsonresponseidy = intent.getStringExtra("jsonresponseidy");
        latlng = intent.getDoubleArrayExtra("latlng");
        lat = latlng[0];
        lng = latlng[1];
        TLocation = intent.getStringExtra("TLocation");
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mode = (String) spinner.getItemAtPosition(position);

                if(isSpinnerFirst){
                    isSpinnerFirst = false;
                }
                else {
                    mMap.clear();
                    urlmap = "https://maps.googleapis.com/maps/api/directions/json?origin=" + FLocation + "&destination=" + TLocation + "&mode=" + mode + "&key=AIzaSyB6uoZXm7bR-3Hay3bpMSFdf1iAhnwx11E";
                    loadjson resp = new loadjson();
                    resp.execute(urlmap);
                    resp.setOnAsyncResponse(new AsyncResponse() {
                        @Override
                        public void onDataReceived(String Data) {
                            try {
                                String mapresponse = Data;
                                parseMap(mapresponse);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    mMap.clear();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void parseMap(String data) throws JSONException {
        if (data == null) return;

        List<Route> routes = new ArrayList<>();
        JSONObject jsonData = new JSONObject(data);
        JSONArray jsonRoutes = jsonData.getJSONArray("routes");
        for(int i=0; i<jsonRoutes.length(); i++){
            JSONObject jsonRoute = jsonRoutes.getJSONObject(i);
            Route route = new Route();

            JSONObject overview_polylineJson = jsonRoute.getJSONObject("overview_polyline");
            JSONArray jsonLegs = jsonRoute.getJSONArray("legs");
            JSONObject jsonLeg = jsonLegs.getJSONObject(0);
            JSONObject jsonEndLocation = jsonLeg.getJSONObject("end_location");
            JSONObject jsonStartLocation = jsonLeg.getJSONObject("start_location");

            route.endAddress = jsonLeg.getString("end_address");
            route.startAddress = jsonLeg.getString("start_address");
            route.startLocation = new LatLng(jsonStartLocation.getDouble("lat"), jsonStartLocation.getDouble("lng"));
            route.endLocation = new LatLng(jsonEndLocation.getDouble("lat"), jsonEndLocation.getDouble("lng"));
            route.points = decodePolyLine(overview_polylineJson.getString("points"));

            routes.add(route);
        }
        onDirectionFinderSuccess(routes);
    }

    class Route {
        public String endAddress;
        public String startAddress;
        public LatLng startLocation;
        public LatLng endLocation;
        public List<LatLng> points;
        public Route(){
        }
    }

    private List<LatLng> decodePolyLine(final String poly) {
        int len = poly.length();
        int index = 0;
        List<LatLng> decoded = new ArrayList<>();
        int lat = 0;
        int lng = 0;
        while (index<len){
            int b;
            int shift = 0;
            int result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b>=0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
            shift = 0;
            result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b>=0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;
            decoded.add(new LatLng(
                    lat / 100000d, lng / 100000d
            ));
        }
        return decoded;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(lat, lng);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Here"));
        //LatLng sample = new LatLng(34.026, -118.268);
        /*mMap.addPolyline(new PolylineOptions().add(
                sydney,
                sample
        )
                .width(10)
                .color(Color.RED)
        );*/
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13));
        //mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    public void onDirectionFinderSuccess(List<Route> routes) {
        ArrayList<Polyline> ploylinePaths = new ArrayList<>();
        ArrayList<Marker> originMarkers = new ArrayList<>();
        ArrayList<Marker> destinationMarkers = new ArrayList<>();

        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 13));
            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .title(route.startAddress)
                    .position(route.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .title(route.endAddress)
                    .position(route.endLocation)));
            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.RED).
                    width(10);
            polylineOptions.add(route.startLocation);
            for(int i=0; i<route.points.size(); i++)
                polylineOptions.add(route.points.get(i));
            polylineOptions.add(route.endLocation);

            ploylinePaths.add(mMap.addPolyline(polylineOptions));
        }
    }

    public void info(View view){
        String det = "info";
        Intent intent = new Intent(MapPane.this, DetailsAct.class);
        intent.putExtra("dets", det);
        intent.putExtra("jsonresponseid", jsonresponseid);
        intent.putExtra("jsonresponseidy", jsonresponseidy);
        startActivityForResult(intent, 1);
    }

    public void photo(View view){
        String det = "photos";
        Intent intent = new Intent(MapPane.this, DetailsAct.class);
        intent.putExtra("dets", det);
        intent.putExtra("jsonresponseid", jsonresponseid);
        intent.putExtra("jsonresponseidy", jsonresponseidy);
        startActivityForResult(intent, 1);
    }

    public void map(View view){
        String det = "map";
        Intent intent = new Intent(MapPane.this, DetailsAct.class);
        intent.putExtra("dets", det);
        intent.putExtra("jsonresponseid", jsonresponseid);
        intent.putExtra("jsonresponseidy", jsonresponseidy);
        startActivityForResult(intent, 1);
    }

    public void reviews(View view){
        String det = "reviews";
        Intent intent = new Intent(MapPane.this, DetailsAct.class);
        intent.putExtra("dets", det);
        intent.putExtra("jsonresponseid", jsonresponseid);
        intent.putExtra("jsonresponseidy", jsonresponseidy);
        startActivityForResult(intent, 1);
    }

    public class ETChangeListener implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }
        @Override
        public void afterTextChanged(Editable editable) {
            if(flag){
                return;
            }
            flag = true;
            mMap.clear();
            FLocation = editable.toString();
            urlmap = "https://maps.googleapis.com/maps/api/directions/json?origin="+FLocation+"&destination="+TLocation+"&mode="+mode+"&key=AIzaSyB6uoZXm7bR-3Hay3bpMSFdf1iAhnwx11E";
            loadjson resp = new loadjson();
            resp.execute(urlmap);
            resp.setOnAsyncResponse(new AsyncResponse() {
                @Override
                public void onDataReceived(String Data) {
                    try {
                        String mapresponse = Data;
                        parseMap(mapresponse);
                    } catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            });
            mMap.clear();
            flag = false;
        }
    }

}
package com.findtheway;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.List;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.config.LocationAccuracy;
import io.nlopez.smartlocation.location.config.LocationParams;
import io.nlopez.smartlocation.location.providers.LocationGooglePlayServicesWithFallbackProvider;

import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.toRadians;

public class Mapforpoly2 extends FragmentActivity implements OnMapReadyCallback,OnLocationUpdatedListener {
    final static int PERMISSION_ALL = 1;
    final static String[] PERMISSIONS = {Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};
    static final int MY_PERMISSIONS_REQUEST_ACCESS_LOCATION = 5555;
    static  String polyline;
    private GoogleMap mMap;
    MarkerOptions mo;
    Marker marker;
    Graph g;
    LocationManager locationManager;
    double latitude;
    double longitude;
    MarkerOptions Marker2;
    String stationdestination;
    SQLiteDatabase mDb;
    DBnavi mHelper;
    DBdis mHelperdis;
    Station b = new Station();
    int alldistance;
    Cursor mCursor;
    ArrayList<Node> stationArray = new ArrayList<>();
    ArrayList<Node> dirArray = new ArrayList<>();
    Graph Graph;
    ArrayList<Edge> edgeList = new ArrayList<>();

    protected Node searchStation(int line, int id, ArrayList<Node> nodes) {
        for(int i=0;i< nodes.size();i++){
            if(nodes.get(i).getLine()==line && nodes.get(i).getID()==id)
                return nodes.get(i);
        }
        Log.d("Error",""+ line + "," + id);
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        stationdestination = getIntent().getStringExtra("x");
        mHelper = new DBnavi(this);
        mDb = mHelper.getWritableDatabase();

        mHelper.onUpgrade(mDb,1,1);
        mCursor = mDb.rawQuery("SELECT " + DBnavi.COL_Line + ", "
                + DBnavi.COL_ID + ", " + DBnavi.COL_Name + ", " + DBnavi.COL_Lat+ ", "
                + DBnavi.COL_Lon + ", " + DBnavi.COL_Trip + " FROM " + DBnavi.TABLE_NAME , null);
        mCursor.moveToFirst();

        while ( !mCursor.isAfterLast() ){
            Node b = new Node();
            b.setLine(mCursor.getInt(mCursor.getColumnIndex(DBnavi.COL_Line)));
            b.setID(mCursor.getInt(mCursor.getColumnIndex(DBnavi.COL_ID)));
            b.setName(mCursor.getString(mCursor.getColumnIndex(DBnavi.COL_Name)));
            b.setLat(mCursor.getDouble(mCursor.getColumnIndex(DBnavi.COL_Lat)));
            b.setLon(mCursor.getDouble(mCursor.getColumnIndex(DBnavi.COL_Lon)));
            b.setTrip(mCursor.getString(mCursor.getColumnIndex(DBnavi.COL_Trip)));
            dirArray.add(b);
            mCursor.moveToNext();
        }
        mHelperdis = new DBdis(this);
        mDb = mHelperdis.getWritableDatabase();
        mHelperdis.onUpgrade(mDb,1,1);
        mCursor = mDb.rawQuery("SELECT " + DBdis.COL_Route + ", "
                + DBdis.COL_Linefrom + ", " + DBdis.COL_Lineto + ", "
                + DBdis.COL_IDform + ", " + DBdis.COL_IDto + " , "
                + DBdis.COL_Distance + ", " + DBdis.COL_Polyline + " FROM " + DBdis.TABLE_NAME, null);
        final ArrayList<Edge> distanArray = new ArrayList<>();
        mCursor.moveToFirst();
        while (!mCursor.isAfterLast()) {
            Edge b = new Edge();
            b.setRoute(mCursor.getInt(mCursor.getColumnIndex(DBdis.COL_Route)));
            b.setLinefrom(mCursor.getInt(mCursor.getColumnIndex(DBdis.COL_Linefrom)));
            b.setLineto(mCursor.getInt(mCursor.getColumnIndex(DBdis.COL_Lineto)));
            b.setIdfrom(mCursor.getInt(mCursor.getColumnIndex(DBdis.COL_IDform)));
            b.setIdto(mCursor.getInt(mCursor.getColumnIndex(DBdis.COL_IDto)));
            b.setDistance(mCursor.getInt(mCursor.getColumnIndex(DBdis.COL_Distance)));
            b.setPolyline(mCursor.getString(mCursor.getColumnIndex(DBdis.COL_Polyline)));
            b.setNodefrom(searchStation(b.getLinefrom(),b.getIdfrom(),dirArray));
            b.setNodeto(searchStation(b.getLineto(),b.getIdto(),dirArray));
            distanArray.add(b);
            mCursor.moveToNext();
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Marker2 = new MarkerOptions().position(new LatLng(0, 0)).title("My Current Location");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Smartonlocationclick();
                marker.remove();
                Marker2 = (new MarkerOptions().position(new LatLng(latitude, longitude))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.track)).title("MyLocation"));
                marker = mMap.addMarker(Marker2);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude)));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15));
                stationArray = filterStationInRange(dirArray);
                ArrayList<Node> alldestination = new ArrayList<>();
                for(int i=0;i<dirArray.size();i++) {
                    if(stationdestination.equals(dirArray.get(i).getName())){
                        alldestination.add(dirArray.get(i));
                    Log.d("StationName", ""+alldestination.get(0).getName());
                    }
                }
                int minDistance=Integer.MAX_VALUE;
                int Distance =Integer.MAX_VALUE;
                g = new Graph(distanArray, dirArray);
                for(int i=0;i<stationArray.size();i++) {
                    g.calculateShortestDistances(stationArray.get(i));
                    for(int j=0;j<alldestination.size();j++)
                    {   Node destinationnode = alldestination.get(j);
                        Distance = destinationnode.getDistanceFromSource();
                        Log.d("Check distance",""+Distance);
                        if(Distance < minDistance){
                            minDistance = Distance;
                           edgeList = g.getNavigationto(alldestination.get(j));
                            }
                        }
                }
                g.calculateShortestDistances(stationArray.get(0));
                g.printResult();
                   for (int i = 0; i < edgeList.size(); i++) {
                        List<LatLng> decodedPath = PolyUtil.decode(edgeList.get(i).getPolyline());
                        mMap.addPolyline(new PolylineOptions().addAll(decodedPath)
                                .width(10)
                                .color(Color.DKGRAY));
                       mMap.addMarker(new MarkerOptions()
                               .position(new LatLng(edgeList.get(i).getNodefrom().getLat(), edgeList.get(i).getNodefrom().getLon())).icon(BitmapDescriptorFactory.fromResource(R.drawable.pon))
                               .title(String.valueOf(edgeList.get(i).getNodefrom().getLine())));
                       mMap.addMarker(new MarkerOptions()
                               .position(new LatLng(edgeList.get(i).getNodeto().getLat(), edgeList.get(i).getNodeto().getLon())).icon(BitmapDescriptorFactory.fromResource(R.drawable.pon))
                               .title(String.valueOf(edgeList.get(i).getNodeto().getLine())));

                       Log.d("Check edge list ", "" + edgeList.get(i).getIdfrom());
                       Log.d("Check id list ", "" + edgeList.get(i).getIdto());

                   }
            }



        });
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int item_id = item.getItemId();
                if (item_id == R.id.listcan) {
                    Intent intent = new Intent(Mapforpoly2.this, DB_listcan.class);
                    startActivity(intent);
                } else if (item_id == R.id.listbus) {
                    Intent intent = new Intent(Mapforpoly2.this,DB_listbus.class);
                    startActivity(intent);
                }
                else if (item_id == R.id.navi){
                    Intent intent = new Intent(Mapforpoly2.this,DB_listnavi.class);
                    startActivity(intent);
                }
                return false;
            }
        });
        b = getIntent().getParcelableExtra("x");
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
//        mHelper = new Database(this);
//        mDb = mHelper.getWritableDatabase();
        Smartonlocationclick();
        mMap = googleMap;
        marker =  mMap.addMarker(Marker2);
//        Log.d("location after clicked", ""+latitude+","+longitude);
////        List<LatLng> decodedPath = PolyUtil.decode(b.getPolyline());
//        mMap.addPolyline(new PolylineOptions().addAll(decodedPath)
//                .width(20)
//                .color(Color.RED));
//
//    List<LatLng> decodedPath2 = PolyUtil.decode(b.getPolylineB());
//        mMap.addPolyline(new PolylineOptions().addAll(decodedPath2)
//                .width(20)
//                .color(Color.BLUE));
}

//    public static void main(String[] args){
//        Edge[] edges = {
//                new Edge(0,2,1),
//                new Edge(0,3,4),
//                new Edge(0,4,2),
//                new Edge(0,1,3),
//                new Edge(1,3,2),
//                new Edge(1,4,3),
//
//        };
//        Graph g = new Graph(edges);
//        g.calculateShortestDistance();
//
//        g.printresult();
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    startSmartLocation();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED)
        {
//            // Should we show an explanation?
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                    Manifest.permission.ACCESS_FINE_LOCATION)) {
//
//                // Show an explanation to the user *asynchronously* -- don't block
//                // this thread waiting for the user's response! After the user
//                // sees the explanation, try again to request the permission.
//
//            } else {
//
//                // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_LOCATION);

//                 MY_PERMISSIONS_REQUEST_ACCESS_LOCATION is an
//                 app-defined int constant. The callback method gets the
//                 result of the request.
//            }
        }
        else {
            startSmartLocation();
        }
    }

    protected void startSmartLocation() {
        if(SmartLocation.with(this).location().state().locationServicesEnabled()) {
            SmartLocation.with(this)
                    .location(new LocationGooglePlayServicesWithFallbackProvider(this))
                    .config(LocationParams.BEST_EFFORT)
                    .start(this);

        } else {
            locationServiceUnavailable(1);
        }
    }

    protected void Smartonlocationclick() {
        LocationParams param = new  LocationParams.Builder()
                .setAccuracy(LocationAccuracy.HIGH)
                .setInterval(10000)
                .build();
        if(SmartLocation.with(this).location().state().locationServicesEnabled()) {
            SmartLocation.with(this)
                    .location(new LocationGooglePlayServicesWithFallbackProvider(this))
                    .config(param)
                    .start(this);

        } else {
            locationServiceUnavailable(1);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        SmartLocation.with(this)
                .location()
                .stop();
    }

    @Override
    public void onLocationUpdated(Location location) {
        // TODO Do something when location was updated
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        float accuracy = location.getAccuracy();
        float bearing = location.getBearing();
        String provider = location.getProvider();
        Log.d("location after clicked", ""+latitude+","+longitude);
        marker.remove();
        Marker2 = (new MarkerOptions().position(new LatLng(latitude,longitude))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.track)).title("MyLocation"));
        marker = mMap.addMarker(Marker2);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude,longitude)));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longitude),15));
    }

    private void locationServiceUnavailable(final int status) {
        // TODO Do something when location service is unavailable
        String message, title, btnText;
        if (status == 1) {
            message = "Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                    "use this app";
            title = "Enable Location";
            btnText = "Location Settings";
        } else {
            message = "Please allow this app to access location!";
            title = "Permission access";
            btnText = "Grant";
        }
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setCancelable(false);
        dialog.setTitle(title)
                .setMessage(message)
                .setPositiveButton(btnText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        if (status == 1) {
                            Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(myIntent);
                        } else
                            requestPermissions(PERMISSIONS, PERMISSION_ALL);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        finish();
                    }
                });
        dialog.show();
    }

    ArrayList<Node> filterStationInRange(ArrayList<Node> dirArray)
    {
        ArrayList<Node> stationarray = new ArrayList<>();
        double d;
        int i;
        int j=0;
        double R= 6371e3;
        double lat=toRadians(latitude);
        double lon=toRadians(longitude);
        int countstation = 0;
        for(i=0;i<dirArray.size();i++) {
            double lat2 =dirArray.get(i).getLat();
            double lon2 =dirArray.get(i).getLon();
            lat2 = toRadians(lat2);
            lon2 = toRadians(lon2);
//              Log.d("check lat lon",""+ lat +  "," + lon + ", " + lat2 + ", " + lon2);

            double a = (sin((lat2-lat)/2)*sin((lat2-lat)/2))+(cos(lat)*(cos(lat2)*sin((lon2-lon)/2)*sin((lon2-lon)/2)));
            double c = 2*atan2(sqrt(a),sqrt(1-a));
//              Log.d("check c",""+c );

            d = R*c*10;
            dirArray.get(i).setDis(d);
//            Log.d("check dis",""+d );
            if(d<10000)
            {
                stationarray.add(dirArray.get(i));
                Log.d("check dis",""+stationarray.get(countstation).getDis() );
                countstation++;
            }
        }
        return stationarray;

    }
}


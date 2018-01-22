package com.example.android.unicarsindos;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.android.unicarsindos.data.UniCarContract;
import com.example.android.unicarsindos.utilities.OpenMapUtils;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap googleMap;
    String selectedUser;
    LatLng sindosLatLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Intent intentThatStartedThisActivity=getIntent();

        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {
                selectedUser = intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_TEXT);
            }
        }
        sindosLatLog=new LatLng(40.658315, 22.803919);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_2);
        mapFragment.getMapAsync(this);
    }


    public void onMapReady(GoogleMap gMap) {
        googleMap=gMap;
        Cursor mCursor= getContentResolver().query(UniCarContract.UniCarEntry.CONTENT_URI,
                new String[]{UniCarContract.UniCarEntry.COLUMN_LATITUDE, UniCarContract.UniCarEntry.COLUMN_LONGITUDE},
                UniCarContract.UniCarEntry.COLUMN_EMAIL+"=?",
                new String[]{selectedUser},
                null);
        mCursor.moveToFirst();
        double longitude=mCursor.getDouble(mCursor.getColumnIndex(UniCarContract.UniCarEntry.COLUMN_LONGITUDE));
        double latitude=mCursor.getDouble(mCursor.getColumnIndex(UniCarContract.UniCarEntry.COLUMN_LATITUDE));
        LatLng selectedLocation= new LatLng(latitude,longitude);
        OpenMapUtils.initMap(this,googleMap,selectedLocation,sindosLatLog);
    }
}

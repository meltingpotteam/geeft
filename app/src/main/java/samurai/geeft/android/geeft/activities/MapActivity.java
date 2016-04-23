package samurai.geeft.android.geeft.activities;

/* Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import samurai.geeft.android.geeft.R;

/**
 * This shows how to draw circles on a map.
 */
public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {


    private static final String EXTRA_ADDRESS =
            "samurai.geeft.android.geeft.address";;
    private LatLng SYDNEY;

    List<Address> addresses;

    private static final double DEFAULT_RADIUS = 2000;


    private GoogleMap mMap;

    private List<DraggableCircle> mCircles = new ArrayList<DraggableCircle>(1);


    private int mStrokeColor;

    private int mFillColor;

    public static Intent newIntent(@NonNull Context context, String address) {
        Intent intent = new Intent(context, MapActivity.class);
        intent.putExtra(EXTRA_ADDRESS, address);
        return intent;
    }


    private class DraggableCircle {

        //private final Marker centerMarker;

        //private final Marker radiusMarker;

        private final Circle circle;

        private double radius;

        public DraggableCircle(LatLng center, double radius) {
            this.radius = radius;
            circle = mMap.addCircle(new CircleOptions()
                    .center(center)
                    .radius(radius)
                    .strokeColor(mStrokeColor)
                    .fillColor(mFillColor));
        }

        public DraggableCircle(LatLng center, LatLng radiusLatLng) {
            this.radius = toRadiusMeters(center, radiusLatLng);
            circle = mMap.addCircle(new CircleOptions()
                    .center(center)
                    .radius(radius)
                    .strokeColor(mStrokeColor)
                    .fillColor(mFillColor));
        }

        /*public boolean onMarkerMoved(Marker marker) {
            if (marker.equals(centerMarker)) {
                circle.setCenter(marker.getPosition());
                radiusMarker.setPosition(toRadiusLatLng(marker.getPosition(), radius));
                return true;
            }
            if (marker.equals(radiusMarker)) {
                radius = toRadiusMeters(centerMarker.getPosition(), radiusMarker.getPosition());
                circle.setRadius(radius);
                return true;
            }
            return false;
        }*/

        public void onStyleChange() {
            circle.setFillColor(mFillColor);
            circle.setStrokeColor(mStrokeColor);
        }
    }


    private static double toRadiusMeters(LatLng center, LatLng radius) {
        float[] result = new float[1];
        Location.distanceBetween(center.latitude, center.longitude,
                radius.latitude, radius.longitude, result);
        return result[0];
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Intent intent = getIntent();

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        if(isNetworkConnected()) {
            // Override the default content description on the view, for accessibility mode.
            map.setContentDescription(getString(R.string.map_circle_description));
            mFillColor = Color.parseColor("#8dd4d4d4");
            mStrokeColor = Color.GRAY;

            String address = getIntent().getStringExtra(EXTRA_ADDRESS);
            addresses = getLocationFromAddress(address);
            // Move the map so that it is centered on the initial circle
            if (addresses != null && mMap!=null) {
                SYDNEY = new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
                mMap.addCircle(new CircleOptions()
                        .center(SYDNEY)
                        .radius(DEFAULT_RADIUS)
                        .strokeColor(mStrokeColor)
                        .fillColor(mFillColor));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SYDNEY, 12.0f));
            }
        }else{
            mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }


    public List<Address> getLocationFromAddress(String strAddress){

        Geocoder coder = new Geocoder(this);
        List<Address> address;

        try {
            address = coder.getFromLocationName(strAddress,5);
            if (address==null) {
                return null;
            }
            Address location=address.get(0);
            location.getLatitude();
            location.getLongitude();

            return address;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
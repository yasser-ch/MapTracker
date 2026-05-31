package com.example.maptracker;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker currentMarker;
    private TextView tvCoordinates;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvCoordinates = findViewById(R.id.tv_coordinates);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Toast.makeText(getApplicationContext(),
                getString(R.string.map_ready),
                Toast.LENGTH_SHORT).show();

        // Default position — Marrakech
        LatLng defaultLocation = new LatLng(31.6295, -7.9811);
        mMap.addMarker(new MarkerOptions()
                .position(defaultLocation)
                .title(getString(R.string.marker_title)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12f));

        boolean permissionGranted =
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED;

        if (permissionGranted) {
            startLocationUpdates();
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    200
            );
        }
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) return;

        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                2000,
                0,
                new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();

                        String coordText = getString(R.string.location_update,
                                String.valueOf(latitude),
                                String.valueOf(longitude));

                        tvCoordinates.setText(coordText);
                        Toast.makeText(getApplicationContext(),
                                coordText, Toast.LENGTH_SHORT).show();

                        LatLng updatedPosition = new LatLng(latitude, longitude);

                        if (currentMarker == null) {
                            currentMarker = mMap.addMarker(new MarkerOptions()
                                    .position(updatedPosition)
                                    .title(getString(R.string.marker_title)));
                        } else {
                            currentMarker.setPosition(updatedPosition);
                        }

                        mMap.animateCamera(CameraUpdateFactory
                                .newLatLngZoom(updatedPosition, 15f));
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {}

                    @Override
                    public void onProviderEnabled(String provider) {
                        Toast.makeText(getApplicationContext(),
                                "Provider activé : " + provider,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                        buildAlertMessageNoGps();
                    }
                }
        );
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.gps_disabled))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.gps_yes),
                        new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, final int id) {
                                startActivity(new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            }
                        })
                .setNegativeButton(getString(R.string.gps_no),
                        new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, final int id) {
                                dialog.cancel();
                            }
                        });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 200) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this,
                        getString(R.string.permission_granted),
                        Toast.LENGTH_SHORT).show();
                onMapReady(mMap);
            } else {
                Toast.makeText(this,
                        getString(R.string.permission_denied),
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}
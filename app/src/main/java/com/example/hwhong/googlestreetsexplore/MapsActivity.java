package com.example.hwhong.googlestreetsexplore;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.games.leaderboard.Leaderboard;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private final String ADDRESS = "Flatiron Building";
    private final LatLng DEFAUT = new LatLng(40.741518, -73.989557);
    private Context context;
    private GroundOverlay overlay;

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        context = this;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        drawMarkers();

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Toast.makeText(context, "This is " + marker.getTitle(), Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Toast.makeText(context, "This is " + marker.getSnippet(), Toast.LENGTH_SHORT).show();
                LatLng latLng = marker.getPosition();

                String path = "google.streetview:cbll=%s,%s&mz=21";
                path = String.format(path, latLng.latitude, latLng.longitude);
                Uri uri = Uri.parse(path);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {
                marker.setTitle(marker.getTitle());
                marker.setSnippet("Click to see more!");

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 15));
                LatLng latLng = marker.getPosition();
                setStreetThumbnails(latLng);
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {

                if(overlay != null) {
                    overlay.remove();
                }
            }
        });
    }

    private void drawMarkers() {
        LatLng latLng = DEFAUT;

        MarkerOptions options = new MarkerOptions();
        options.position(latLng);
        options.title(ADDRESS);
        options.snippet("LatLng " + latLng );
        options.anchor(0.5f, 1.0f);
        options.draggable(true);

        mMap.addMarker(options);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        setStreetThumbnails(latLng);
    }

    private void setStreetThumbnails(final LatLng latLng) {
        String url = "http://maps.googleapis.com/maps/api/streetview?" +
                "size=450x250&location=%s,%s&sensor=true";
        url = String.format(url, latLng.latitude, latLng.longitude);

        RequestQueue queue = Volley.newRequestQueue(context);
        ImageRequest request = new ImageRequest(
                url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        overlay = mMap.addGroundOverlay(
                                new GroundOverlayOptions()
                                        .image(BitmapDescriptorFactory.fromBitmap(response))
                                        .anchor(0, 1.5f)
                                        .position(latLng, 400f, 200f)
                        );
                        overlay.setTransparency(0.3f);
                    }
                }, 0, 0, Bitmap.Config.RGB_565,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );

        queue.add(request);

    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Log.d("map", "map is ready");

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}

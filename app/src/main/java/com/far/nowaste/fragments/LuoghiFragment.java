package com.far.nowaste.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.transition.TransitionManager;

import com.far.nowaste.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.LinkedList;
import java.util.List;

public class LuoghiFragment extends Fragment {

    // variabaili
    ConstraintLayout parent, mapContainer;
    SupportMapFragment supportMapFragment;
    FusedLocationProviderClient client;
    FloatingActionButton gpsBtn, fullScreenBtn;

    OvershootInterpolator interpolator = new OvershootInterpolator();

    boolean collapsed = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_luoghi, container, false);

        // assegnazione variabile
        parent = view.findViewById(R.id.luoghiLayout);
        mapContainer = view.findViewById(R.id.mapContainer);
        supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);
        gpsBtn = view.findViewById(R.id.gpsButton);
        fullScreenBtn = view.findViewById(R.id.fullScreenButton);

        // inizializzazione FusedLocation
        client = LocationServices.getFusedLocationProviderClient(getContext());

        // Check permessi
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // permesso concesso
            getCurrentLocation();
        } else {
            // permesso negato
            // RICHIESTA PERMESSO
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},44);
        }

        // load marker
        caricaMarker();

        gpsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentLocation();
            }
        });

        fullScreenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collapsed  = !collapsed;
                collapse();
            }
        });

        return view;
    }

    // metodi Maps
    private void getCurrentLocation() {
        // permessi per usare getLastLocation
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Inizializzazione task Location
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    // Sincronizza Mappa
                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            // inizializzazione Latitudine e Longitudine
                            LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());

                            // marker per segnalare la posizione attuale
                            MarkerOptions markerOptions = new MarkerOptions().position(latLng);

                            // Zoom Mappa
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,16));

                            // aggiungere il marker sulla mappa
                            Marker marker = googleMap.addMarker(markerOptions);

                            Bitmap icon = drawableToBitmap(ContextCompat.getDrawable(getContext(), R.drawable.ic_my_location));

                            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(icon);

                            marker.setIcon(bitmapDescriptor);
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 44){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //Quando Permesso concesso
                getCurrentLocation();
            }
        }
    }

    private void caricaMarker(){
        // inizializzazione latitudine e longitudine
        double lat1 = 41.308120, lng1 = 16.297810;
        double lat2 = 41.307560, lng2 = 16.264670;
        double lat3 = 41.323926, lng3 = 16.230124;


        //inizializzazione LatLng
        LatLng latLng1 = new LatLng(lat1,lng1);
        LatLng latLng2 = new LatLng(lat2,lng2);
        LatLng latLng3 = new LatLng(lat3,lng3);

        MarkerOptions markerOptions1 = new MarkerOptions().position(latLng1).title("Bar.S.A.")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).snippet("Servizio Ambientale");
        MarkerOptions markerOptions2 = new MarkerOptions().position(latLng2).title("Ecocentro Parco degli Ulivi")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).snippet("Ecocentro");
        MarkerOptions markerOptions3 = new MarkerOptions().position(latLng3).title("Smaltimento Rifiuti")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).snippet("Ecoambiente");

        List<MarkerOptions> listMO = new LinkedList<>();
        listMO.add(markerOptions1);
        listMO.add(markerOptions2);
        listMO.add(markerOptions3);

        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                for(MarkerOptions item : listMO){
                    googleMap.addMarker(item);

                }
                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        gpsBtn.animate().translationY(-140f).setInterpolator(interpolator).setDuration(400).start();
                        return false;
                    }
                });

                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        gpsBtn.animate().translationY(0f).setInterpolator(interpolator).setDuration(400).start();
                    }
                });
            }
        });
    }

    private static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    private void collapse() {
        TransitionManager.beginDelayedTransition(parent);
        //change layout params
        int height = mapContainer.getHeight();
        ViewGroup.LayoutParams layoutParams = mapContainer.getLayoutParams();
        layoutParams.height = !collapsed ? height / 2 : height * 2;
        mapContainer.requestLayout();
    }
}
package com.far.nowaste;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class LuoghiFragmentt extends Fragment {
    // variabaili
    SupportMapFragment supportMapFragment;
    FusedLocationProviderClient client;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_luoghi, container, false);

        supportMapFragment = MainActivity.getSupportMapFragment();


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

        return view;
    }

    public void getCurrentLocation() {
        // permessi per usare getLastLocation
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Inizializzazione task Location
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                // Se ha successo

                if(location != null){
                    // Sincronizza Mappa
                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            // inizializzazione Latitudine e Longitudine
                            LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());

                            // marker per segnalare la posizione attuale
                            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Sono qui!");

                            // Zoom Mappa
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,16));

                            // aggiungere il marker sulla mappa
                            googleMap.addMarker(markerOptions);
                        }
                    });
                }

            }
        });

    }
}

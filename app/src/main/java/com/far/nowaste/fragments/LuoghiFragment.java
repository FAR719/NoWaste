package com.far.nowaste.fragments;

import android.Manifest;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;

import com.far.nowaste.MainActivity;
import com.far.nowaste.R;
import com.far.nowaste.objects.Luogo;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.List;

public class LuoghiFragment extends Fragment implements OnMapReadyCallback {

    // variabili
    ConstraintLayout parent, mapContainer, listaLayout;
    SupportMapFragment mapFragment;
    FusedLocationProviderClient client;
    FloatingActionButton gpsBtn, fullScreenBtn;

    GoogleMap map;
    boolean locationPermissionGranted;
    Location lastKnownLocation;

    RecyclerView mListaLuoghi;
    List<Luogo> luoghi;

    OvershootInterpolator interpolator;

    static public boolean ISEXPANDED;
    int collapsedheight, expandedHeight, miniFAB, normalFAB;
    Drawable fsIcon, fsExitIcon;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_luoghi, container, false);

        // assegnazione variabile
        parent = view.findViewById(R.id.luoghiLayout);
        mapContainer = view.findViewById(R.id.mapContainer);
        listaLayout = view.findViewById(R.id.listaLuoghi_layout);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);
        gpsBtn = view.findViewById(R.id.gpsButton);
        fullScreenBtn = view.findViewById(R.id.fullScreenButton);
        mListaLuoghi = view.findViewById(R.id.luoghi_recyclerView);

        locationPermissionGranted = false;

        interpolator = new OvershootInterpolator();

        // calcola le altezze della mappa e le grandezze dei pulsanti
        final float scale = getContext().getResources().getDisplayMetrics().density;
        collapsedheight = (int) (300 * scale + 0.5f);
        expandedHeight = parent.getHeight();
        miniFAB = (int) (40 * scale + 0.5f);
        normalFAB = (int) (56 * scale + 0.5f);

        // fullscreen drawables
        fsIcon = ContextCompat.getDrawable(getContext(), R.drawable.ic_fullscreen);
        fsExitIcon = ContextCompat.getDrawable(getContext(), R.drawable.ic_fullscreen_exit);

        // inizializzazione FusedLocation
        client = LocationServices.getFusedLocationProviderClient(getContext());

        // ask for permission
        getLocationPermission();

        // gps onclick
        gpsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateLocationUI();
            }
        });

        // fullscreen onclick
        fullScreenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateMap();
            }
        });

        mapFragment.getMapAsync(this);

        caricaLuoghi();

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        // default location impostata su Bari
        LatLng defaultLocation = new LatLng( 41.11148, 16.8554);
        map.moveCamera(CameraUpdateFactory.newLatLng(defaultLocation));

        // show current position
        updateLocationUI();

        // animazione pulsante gps
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

    private void getLocationPermission() {
        // Check permessi
        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // permesso concesso
            locationPermissionGranted = true;
        } else {
            // permesso negato, richiedi il permesso
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},44);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        if (requestCode == 44){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //Quando Permesso concesso
                locationPermissionGranted = true;
            }
        }
    }

    // abilita la posizione attuale
    private void updateLocationUI() {
        if (map == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                getDeviceLocation();
            } else {
                map.setMyLocationEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    // get the current location
    private void getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = client.getLastLocation();
                locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), 16));
                            } else {
                                // controlla se il gps è acceso
                                enableLocation();
                            }
                        } else {
                            Log.e("TAG", "Current location is null.");
                            Log.e("TAG", "Exception: %s", task.getException());
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    private void caricaLuoghi(){
        LatLng myPosition = new LatLng(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude());

        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        fStore.collection("luoghi").orderBy("nome").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                luoghi = new ArrayList<>();
                // aggiungi i marker
                for (DocumentSnapshot document : queryDocumentSnapshots) {
                    Luogo luogo = document.toObject(Luogo.class);
                    luoghi.add(luogo);
                    LatLng latLng = new LatLng(luogo.getLat(), luogo.getLng());
                    MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(luogo.getNome()).visible(false)
                            .snippet(luogo.getCategoria()).icon(BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    if(SphericalUtil.computeDistanceBetween(myPosition,markerOptions.getPosition()) < 500){
                        markerOptions.visible(true);
                    }
                    mapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            googleMap.addMarker(markerOptions);

                        }
                    });
                }

                // imposta lista luoghi
                LuogoAdapter luogoAdapter = new LuogoAdapter(getContext(), luoghi);
                mListaLuoghi.setAdapter(luogoAdapter);
                mListaLuoghi.setLayoutManager(new LinearLayoutManager(getContext()));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("LOG", "Error! " + e.getLocalizedMessage());
            }
        });

    }

    // imposta il fullscreen
    public void animateMap() {
        TransitionManager.beginDelayedTransition(parent);
        //change layout params
        ViewGroup.LayoutParams layoutParams = mapContainer.getLayoutParams();
        if (!ISEXPANDED) {
            layoutParams.height = expandedHeight;
            fullScreenBtn.setCustomSize(normalFAB);
            fullScreenBtn.setImageDrawable(fsExitIcon);
            gpsBtn.setCustomSize(normalFAB);
            listaLayout.animate().alpha(0f).setInterpolator(interpolator).setDuration(1000).start();
        } else {
            layoutParams.height = collapsedheight;
            fullScreenBtn.setCustomSize(miniFAB);
            fullScreenBtn.setImageDrawable(fsIcon);
            gpsBtn.setCustomSize(miniFAB);
            listaLayout.animate().alpha(1f).setInterpolator(interpolator).setDuration(1000).start();
        }
        ViewGroup root = (ViewGroup) parent;
        android.transition.TransitionManager.beginDelayedTransition(root);
        AutoTransition transition = new AutoTransition();
        transition.setDuration(2000);
        android.transition.TransitionManager.beginDelayedTransition(root, transition);

        mapContainer.requestLayout();
        ISEXPANDED = !ISEXPANDED;
    }

    // controlla se il gps è disattivato e chiede di attivarlo
    private void enableLocation() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getContext())
                .checkLocationSettings(builder.build());
        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    // All location settings are satisfied. The client can initialize location requests here.
                } catch (ApiException exception) {
                    switch (exception.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the user a dialog.
                            try {
                                // Cast to a resolvable exception.
                                ResolvableApiException resolvable = (ResolvableApiException) exception;
                                // Show the dialog by calling startResolutionForResult() and check the result in onActivityResult().
                                resolvable.startResolutionForResult(getActivity(), 10);
                            } catch (IntentSender.SendIntentException | ClassCastException e) {
                                Log.e("LOG", "Error! " + e.getLocalizedMessage());
                                ((MainActivity)getActivity()).showSnackbar("Errore! Il gps non è disponibile!");
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            Log.e("LOG", "Error! " + exception.getLocalizedMessage());
                            ((MainActivity)getActivity()).showSnackbar("Errore! Il gps non è disponibile!");
                            break;
                    }
                }
            }
        });
    }

    // adapter per la recView
    class LuogoAdapter extends RecyclerView.Adapter<LuogoAdapter.LuogoViewHolder> {

        Context context;
        List<Luogo> luoghiList;

        public LuogoAdapter(Context c, List<Luogo> luogList) {
            context = c;
            luoghiList = luogList;
        }

        @NonNull
        @Override
        public LuogoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.layout_recycler_view_luoghi_item, parent, false);

            return new LuogoViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull LuogoViewHolder holder, int position) {
            holder.mNome.setText(luoghiList.get(position).getNome());
            holder.mCategoria.setText(luoghiList.get(position).getCategoria());
            Drawable icon = ContextCompat.getDrawable(getContext(), R.drawable.ic_place);
            holder.mIcona.setImageDrawable(icon);
            holder.itemLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            // Zoom Mappa
                            LatLng latLng = new LatLng(luoghiList.get(position).getLat(), luoghiList.get(position).getLng());
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,16));
                        }
                    });
                }
            });
        }

        @Override
        public int getItemCount() {
            return luoghiList.size();
        }

        public class LuogoViewHolder extends RecyclerView.ViewHolder {

            TextView mNome, mCategoria;
            ImageView mIcona;
            ConstraintLayout itemLayout;

            public LuogoViewHolder(@NonNull View itemView) {
                super(itemView);
                itemLayout = itemView.findViewById(R.id.luogo_layout);
                mNome = itemView.findViewById(R.id.luogo_nome);
                mCategoria = itemView.findViewById(R.id.luogo_categoria);
                mIcona = itemView.findViewById(R.id.luogo_icona);
            }
        }
    }
}
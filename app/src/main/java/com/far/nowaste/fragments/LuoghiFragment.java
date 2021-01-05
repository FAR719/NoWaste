package com.far.nowaste.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
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
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;

import com.far.nowaste.R;
import com.far.nowaste.objects.Luogo;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.LinkedList;
import java.util.List;

public class LuoghiFragment extends Fragment {

    // variabaili
    ConstraintLayout parent, mapContainer;
    SupportMapFragment supportMapFragment;
    FusedLocationProviderClient client;
    FloatingActionButton gpsBtn, fullScreenBtn;

    RecyclerView mListaLuoghi;
    FirestoreRecyclerAdapter adapter;

    OvershootInterpolator interpolator = new OvershootInterpolator();

    boolean collapsed = false;
    int collapsedheight, expandedHeight, miniFAB, normalFAB;
    Drawable fs, fsExit;

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
        mListaLuoghi = view.findViewById(R.id.luoghi_recyclerView);

        // calcola le altezze della mappa e le grandezze dei pulsanti
        final float scale = getContext().getResources().getDisplayMetrics().density;
        collapsedheight = (int) (300 * scale + 0.5f);
        expandedHeight = parent.getHeight();
        miniFAB = (int) (40 * scale + 0.5f);
        normalFAB = (int) (56 * scale + 0.5f);

        // fullscreen drawables
        fs = ContextCompat.getDrawable(getContext(), R.drawable.ic_fullscreen);
        fsExit = ContextCompat.getDrawable(getContext(), R.drawable.ic_fullscreen_exit);

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

        // gps onclick
        gpsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentLocation();
            }
        });

        // fullscreen onclick
        fullScreenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collapsed  = !collapsed;
                collapse();
            }
        });

        // markers onclick sposta il pulsante del gps
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
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

        // query
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        Query query = fStore.collection("luoghi").orderBy("nome");

        FirestoreRecyclerOptions<Luogo> options = new FirestoreRecyclerOptions.Builder<Luogo>().setQuery(query, Luogo.class).build();

        adapter = new FirestoreRecyclerAdapter<Luogo, LuogoViewHolder>(options) {
            @NonNull
            @Override
            public LuogoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_recycler_view_luoghi, parent, false);
                return new LuogoViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull LuogoViewHolder holder, int position, @NonNull Luogo model) {
                holder.mNome.setText(model.getNome());
                holder.mCategoria.setText(model.getCategoria());
                Drawable icon = ContextCompat.getDrawable(getContext(), R.drawable.ic_home_work);
                holder.mIcona.setImageDrawable(icon);
                holder.itemLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(GoogleMap googleMap) {
                                // Zoom Mappa
                                LatLng latLng = new LatLng(model.getLat(),model.getLng());
                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,16));
                            }
                        });
                    }
                });
            }
        };

        // View Holder
        mListaLuoghi.setHasFixedSize(true);
        mListaLuoghi.setLayoutManager(new LinearLayoutManager(getContext()));
        mListaLuoghi.setAdapter(adapter);

        // divider nella recyclerView
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        mListaLuoghi.addItemDecoration(dividerItemDecoration);

        return view;
    }

    private class LuogoViewHolder extends RecyclerView.ViewHolder{
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

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();

        // load marker
        caricaMarker();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
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
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        fStore.collection("luoghi").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot document : queryDocumentSnapshots) {
                    Luogo luogo = document.toObject(Luogo.class);
                    LatLng latLng = new LatLng(luogo.getLat(), luogo.getLng());
                    MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(luogo.getNome())
                            .snippet(luogo.getCategoria()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            googleMap.addMarker(markerOptions);
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("LOG", "Error! " + e.getLocalizedMessage());
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
        ViewGroup.LayoutParams layoutParams = mapContainer.getLayoutParams();
        if (collapsed) {
            layoutParams.height = expandedHeight;
            fullScreenBtn.setCustomSize(normalFAB);
            fullScreenBtn.setImageDrawable(fsExit);
            gpsBtn.setCustomSize(normalFAB);
        } else {
            layoutParams.height = collapsedheight;
            fullScreenBtn.setCustomSize(miniFAB);
            fullScreenBtn.setImageDrawable(fs);
            gpsBtn.setCustomSize(miniFAB);
        }
        //layoutParams.height = !collapsed ? height / 2 : height * 2;
        mapContainer.requestLayout();
    }
}
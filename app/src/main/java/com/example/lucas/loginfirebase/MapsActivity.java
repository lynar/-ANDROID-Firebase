package com.example.lucas.loginfirebase;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    //Variable de la "Vue" de l'application
    //private TextView m_Longitude;
    //private TextView m_Latitude;
    private Button m_Envoyer;
    private Button LogOut;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;

    ///testing notification
    private Button m_Notif;

    //Variable Algo et TAG
    private static String TAG = "Projet";
    private double mLongitude_algo;
    private double mLatitude_algo;
    private double m_latitude;
    private double m_longitude;

    //Getter et Setter
    public double getLongitude_algo() {
        return mLongitude_algo;
    }

    public void setLongitude_algo(double mLongitude_algo) {
        this.mLongitude_algo = mLongitude_algo;
    }

    public double getLatitude_algo() {
        return mLatitude_algo;
    }

    public void setLatitude_algo(double mLatitude_algo) {
        this.mLatitude_algo = mLatitude_algo;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        firebaseAuth=FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        firebaseUser = firebaseAuth.getInstance().getCurrentUser();
        LogOut = (Button) findViewById(R.id.Button_Logout);
        m_Envoyer = (Button) findViewById(R.id.Boutton_Envoyer);

        //Localisation
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        /** Test si le gps est activé ou non */
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            /** on lance notre activity (qui est une dialog) */
            Intent localIntent = new Intent(this, PermissionGps.class);
            //localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(localIntent);
        }

        ArrayList<LocationProvider> providers = new ArrayList<LocationProvider>();
        ArrayList<String> names = (ArrayList<String>) locationManager.getProviders(true);

        for (String name : names) {
            providers.add(locationManager.getProvider(name));

        }

        Criteria critere = new Criteria();

        // Pour indiquer la précision voulue
        // On peut mettre ACCURACY_FINE pour une haute précision ou ACCURACY_COARSE pour une moins bonne précision
        critere.setAccuracy(Criteria.ACCURACY_FINE);

        // Est-ce que le fournisseur doit être capable de donner une altitude ?
        critere.setAltitudeRequired(true);

        // Est-ce que le fournisseur doit être capable de donner une direction ?
        critere.setBearingRequired(true);

        // Est-ce que le fournisseur peut être payant ?
        critere.setCostAllowed(false);

        // Pour indiquer la consommation d'énergie demandée
        // Criteria.POWER_HIGH pour une haute consommation, Criteria.POWER_MEDIUM pour une consommation moyenne et Criteria.POWER_LOW pour une basse consommation
        critere.setPowerRequirement(Criteria.POWER_HIGH);

        // Est-ce que le fournisseur doit être capable de donner une vitesse ?
        critere.setSpeedRequired(true);

        /// Demander la permission si elle n'est pas accorder pour acceder au information de géolocalisation
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.INTERNET}
                        ,10);
            }
            return;
        }

        //Cette Methode permet de d'update la localisation du téléphone
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 150, new LocationListener() {

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }

            @Override
            public void onLocationChanged(Location location) {
                Log.d(TAG, "onLocationChanged: RENTRE DANS LA FONCTION");
                //On récupère la longitude et la latitude afin de l'afficher dans l'application.
                Log.d(TAG, "Latitude " + location.getLatitude() + " et longitude " + location.getLongitude());
                //mettre la longitude et la lattitude dans les attributs de la classe
                setLatitude_algo(location.getLatitude());
                setLongitude_algo(location.getLongitude());
                m_latitude = location.getLatitude();
                m_longitude = location.getLongitude();
                //Move the camera to the user's location and zoom in!
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(m_latitude, m_longitude), 12.0f));

            }
        });

        //Cette Methode permet de d'update la localisation du téléphone
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60000, 150, new LocationListener() {

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }

            @Override
            public void onLocationChanged(Location location) {
                Log.d(TAG, "onLocationChanged: RENTRE DANS LA FONCTION");
                //On récupère la longitude et la latitude afin de l'afficher dans l'application.
                Log.d(TAG, "Latitude " + location.getLatitude() + " et longitude " + location.getLongitude());
                //mettre la longitude et la lattitude dans les attributs de la classe
                setLatitude_algo(location.getLatitude());
                setLongitude_algo(location.getLongitude());
                m_latitude = location.getLatitude();
                m_longitude = location.getLongitude();
                //Move the camera to the user's location and zoom in!
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(m_latitude, m_longitude), 12.0f));

            }
        });

        // Log OUT boutton pour se deconnecter
        LogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference removeData = databaseReference.child("token_id");
                removeData.setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        firebaseAuth.signOut();
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        finish();
                    }
                });
            }
        });

        m_Envoyer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Latitude2 " + m_latitude + " et longitude2 " + m_longitude);
            Intent userslist = new Intent(getApplicationContext() ,UsersList.class );
            userslist.putExtra("Latitude", m_latitude);
            userslist.putExtra("Longitude", m_longitude);
            startActivity(userslist);

            }
        });
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
        Log.d(TAG, "onMapReady: RENTRE DANS LA FONCTION");
        mMap = googleMap;

        // Localisation
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

}

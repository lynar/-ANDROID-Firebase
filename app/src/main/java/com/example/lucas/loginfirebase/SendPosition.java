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
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SendPosition extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    //position of the first phone
    private double mLongitude_phoneA;
    private double mLatitude_phoneA;
    private Button m_Envoyer;
    private Button LogOut;
    private static String TAG = "SendPosition";
    private double mLongitude_algo;
    private double mLatitude_algo;
    private double m_latitude;
    private double m_longitude;
    private String mSendToName;
    private String mSendToID;
    private int notiftype;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    ///initialise Firebase
    private DatabaseReference dref;

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

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_maps);

        firebaseAuth=FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        LogOut = (Button) findViewById(R.id.Button_Logout);
        m_Envoyer = (Button) findViewById(R.id.Boutton_Envoyer);
        /// we are using intent and getDoubleExtra to have the value from the previous activity
        Intent intent = getIntent();
        mLatitude_phoneA = intent.getDoubleExtra("Latitude", 0);
        mLongitude_phoneA = intent.getDoubleExtra("Longitude", 0);
        mSendToName = intent.getStringExtra("SendToName");
        mSendToID = intent.getStringExtra("SendToID");
        //define the type of notification 0 for position, 1 for meeting plac
        notiftype = intent.getIntExtra("type", 0);

        Log.d(TAG, "To send the notif to " + mSendToID + " " + mSendToName + " " + notiftype );

        String current_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d(TAG, "Current Id" + current_id);

        m_Envoyer.setVisibility(View.INVISIBLE);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ///creation of a node notification
        ///la notification va etre liée au profil de la personne qui envoie grace au current ID
        //et le from indique a qui la donnée doit etre envoyée.
        String key = FirebaseDatabase.getInstance().getReference("users").push().getKey();
            dref = FirebaseDatabase.getInstance().getReference("users/" + mSendToID + "/notification");
            Map<String, Object> notificationMassage = new HashMap<>();
            notificationMassage.put("latitude", mLatitude_phoneA);
            notificationMassage.put("longitude", mLongitude_phoneA);
            notificationMassage.put("from", current_id);
            notificationMassage.put("type", notiftype);
            dref.child(key).setValue(notificationMassage).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "Sending notif completed");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SendPosition.this, "Failure to send the notification", Toast.LENGTH_SHORT).show();
                }
            });
            ;
            Toast.makeText(getApplicationContext(), "Your notification has been sent to " + mSendToName, Toast.LENGTH_SHORT).show();



        LogOut = (Button) findViewById(R.id.Button_Logout);
        m_Envoyer = (Button) findViewById(R.id.Boutton_Envoyer);

        //Localisation
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        ArrayList<LocationProvider> providers = new ArrayList<LocationProvider>();
        ArrayList<String> names = (ArrayList<String>) locationManager.getProviders(true);

        for (String name : names) {
            providers.add(locationManager.getProvider(name));

        }

        //Cette Methode permet de d'update la localisation du téléphone
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
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

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: RENTRE DANS LA FONCTION");
        mMap = googleMap;
        //Cette Methode permet de d'update la localisation du téléphone
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

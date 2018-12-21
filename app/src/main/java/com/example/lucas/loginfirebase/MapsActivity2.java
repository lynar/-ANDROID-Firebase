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
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MapsActivity2 extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    //Variable de la "Vue" de l'application
    //private TextView m_Longitude;
    //private TextView m_Latitude;
    private Button m_Envoyer;

    //Variable Algo et TAG
    private static String TAG = "Projet";
    private double mLongitude_algo;
    private double mLatitude_algo;
    private String lat;
    private String longi;
    private String data;
    private String from;
    private String titre;
    private String name;
    private String type;

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    private Button m_Meet;
    private Button LogOut;
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
        setContentView(R.layout.activity_maps2);

        /// permet de récupérer les informations de l'activité précédente
        // ici l'activity précédente est la notification
        Intent intent = getIntent();
        data = intent.getStringExtra("message"); // on récupére les informations contenues dans la notif
        from = intent.getStringExtra("from_user_id"); /// recupéération de la personne qui a envoyé la notif
        titre = intent.getStringExtra("title");
        type = intent.getStringExtra("type"); // permet de déterminer les informations a afficher sur le layout
        /// en fonction  du type de notif, l'interface n'est pas tout à fait la meme

        Log.d(TAG, "the data is " + data);
        String[] parts = data.split(" "); //séparer le message de la notif pour récupérer latitude et longitude
        lat = parts[0];
        longi = parts[1];

        String[] parts2 = titre.split(" "); //séparer le titre de la notif pour récupérer le nom de personne qui a envoyé le message
        name = parts2[2];

        Log.d(TAG, "Display ID from " + from);
        Log.d(TAG, "Display type " + type);

        LogOut = (Button) findViewById(R.id.Button_Logout);
        m_Meet = (Button) findViewById(R.id.Boutton_Meet);

        firebaseAuth=FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        /// si le type est 0 --> la notif est celle de la position de la personne
        if(type.equals("0")) {
            /// dans ces conditions un toast s'affiche pour dire de séléctionner un rendez-vous
            Context context = getApplicationContext();
            CharSequence text = "Veuillez séléctionner un lieu de rendez-vous!";
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }

        //Localisation
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

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

        //Permet de d'obtenir la permission de localiser le téléphone

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Permission Accepter ");
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
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
        /// si le type est 1 --> la notif est le lieu de rendez-vous
        if(type.equals("1")) {
            /// un bouton pour lancer le GPS s'affiche
            m_Meet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Latitude2 " + lat + " et longitude2 " + longi);
                    /// VERS GPS envoyer les coordonnées dans l'intent
                    /// Nous n'avons pas eu le temps de le faire
                    Uri gmmIntentUri = Uri.parse("google.navigation:q=" + lat + "," + longi);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);

                }
            });
        }
        else
        {
            m_Meet.setVisibility(View.GONE);
        }
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

        if(type.equals("0")) {
            /// dans les conditions d'un message de position, il est possible de clicker longuement
            /// sur la Map afin d'envoyer la nouvelle position comme un lieu de rendez-vous
            LatLng positionphoneA = new LatLng(Double.parseDouble(lat), Double.parseDouble(longi));
            mMap.addMarker(new MarkerOptions().position(positionphoneA).title("Marker at my friend position"));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(lat), Double.parseDouble(longi)), 12.0f));
            final MarkerOptions marker = new MarkerOptions();
            mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng latLng) {
                    ///lors du click un Marker apparait
                    mMap.addMarker(marker
                            .position(latLng)
                            .snippet("")
                            .title("Meeting place"));
                    Double latitude = marker.getPosition().latitude;
                    Double longitude = marker.getPosition().longitude;
                    Context context = getApplicationContext();
                    /// lors de se marker l'activity pour l'envoie de la position s'ouvre
                    Intent sendPosition = new Intent(getApplicationContext(), SendPosition.class);
                    sendPosition.putExtra("Latitude", latitude);
                    sendPosition.putExtra("Longitude", longitude);
                    sendPosition.putExtra("SendToName", name);
                    sendPosition.putExtra("SendToID", from);
                    sendPosition.putExtra("type", 1); //define the type of notification 0 for position, 1 for meeting place
                    startActivity(sendPosition);
                }
            });
        }
        else {
            /// sinon on affiche le lieu de rendez-vous
            LatLng positionphoneA = new LatLng(Double.parseDouble(lat), Double.parseDouble(longi));
            mMap.addMarker(new MarkerOptions().position(positionphoneA).title("Marker at the meeting place"));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(lat), Double.parseDouble(longi)),12.0f));
        }
    }
}

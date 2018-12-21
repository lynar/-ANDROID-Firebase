package com.example.lucas.loginfirebase;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UsersList extends AppCompatActivity {

    /// attribute for the layout of the listview
    private ListView userlist;
    private ArrayAdapter<User> m_Adapter;
    private ArrayList<User> m_list = new ArrayList<>();
    private Map<String, Object> map;

    ///attributes for the firebase real-time database
    private DatabaseReference dref;
    private static final String TAG = "UserList";

    //position of the first phone
    private double mLongitude_phoneA;
    private double mLatitude_phoneA;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);

        /// we are using intent and getDoubleExtra to have the value from the previous activity
        Intent intent = getIntent();
        mLatitude_phoneA = intent.getDoubleExtra("Latitude",0);
        mLongitude_phoneA = intent.getDoubleExtra("Longitude",0);

        userlist = (ListView) findViewById(R.id.listview);
        ///initialise the database with the route
        dref = FirebaseDatabase.getInstance().getReference("users");

        ///adapter that allows us to see the different user
        m_Adapter = new ArrayAdapter<User>(this, android.R.layout.simple_list_item_1, m_list);
        userlist.setAdapter(m_Adapter);

        /// to trigger the appropriate methode when a change happen in the database
        /// lorsqu'un users est créé il apparait dans la listview
        dref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    map = (Map<String, Object>) dataSnapshot.getValue();
                     String UID = dataSnapshot.getKey();
                    Log.d(TAG, "Value is: " + map);
                    String name = (String) map.get("m_name");
                    Log.d(TAG, "Name: " + name + " ID: " + UID);
                    User user = new User(name, UID);
                    Log.d(TAG, "User created: " + user.getM_name());
                    m_list.add(user);
                    m_Adapter.notifyDataSetChanged();
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                map = (Map<String, Object>) dataSnapshot.getValue();
                String UID = dataSnapshot.getKey();
                Log.d(TAG, "Value is: " + map);
                String name = (String) map.get("m_name");
                Log.d(TAG, "Name: " + name + " ID: " + UID);
                User user = new User(name, UID);
                Log.d(TAG, "User created: " + user.getM_name());
                m_list.add(user);
                m_Adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /// cette partie du code concerne le choix du user.
        /*
        Lors du click sur un utilisateur, on récupérer les informations du nom de
        la personne a qui envoyé ainsi que son ID. Nous avons également besoin de la latitude
        et de la longitude du current user. Toutes ces données sont transmises à l'activité suivante
         */
        userlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0,
                                    View arg1, int arg2, long arg3) {
                //le code à effectuer suite à un click sur un user
                Log.d(TAG, "Testing on click");
                Log.d(TAG, "Lat" + mLatitude_phoneA);
                Log.d(TAG, "Long" + mLongitude_phoneA);
                String name =m_list.get(arg2).getM_name();
                String id =m_list.get(arg2).getM_ID();
                Log.d(TAG, "Name " + name + " ID " + id);
                Intent sendPosition = new Intent(getApplicationContext(), SendPosition.class);
                sendPosition.putExtra("Latitude", mLatitude_phoneA);
                sendPosition.putExtra("Longitude", mLongitude_phoneA);
                sendPosition.putExtra("SendToName", name);
                sendPosition.putExtra("SendToID", id);
                sendPosition.putExtra("type", 0); //define the type of notification 0 for position, 1 for meeting place
                startActivity(sendPosition);
            }
        });
    }
}

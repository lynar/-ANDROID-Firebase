package com.example.lucas.loginfirebase;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class MainActivity extends AppCompatActivity {

    private TextView SignIn;
    private EditText User;
    private EditText Password;
    private Button Continue;
    private TextView Text_SignUp;
    private String m_User;
    private String m_Password;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    private static final String TAG = "LoginFirebase";

    //GETTERS ET SETTERS
    public String getM_User() {
        return m_User;
    }

    public void setM_User(String m_User) {
        this.m_User = m_User;
    }

    public String getM_Password() {
        return m_Password;
    }

    public void setM_Password(String m_Password) {
        this.m_Password = m_Password;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Instancier les objet Graphique
        SignIn = (TextView) findViewById(R.id.Text_SignIn);
        User = (EditText) findViewById(R.id.EditText_UserName);
        Password = (EditText) findViewById(R.id.EditText_PassWord);
        Continue = (Button) findViewById(R.id.Button_SignIn);
        Text_SignUp = (TextView) findViewById(R.id.Text_SignUp);

        //Instancier l'objet Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        if(!VerifyInternetConnexion.isConnectedInternet(MainActivity.this))
        {
            //Je suis connecté à internet
            //Je ne suis pas connecté à internet
            AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
            localBuilder
                    .setMessage(" Vous n'êtes pas connecté à internet. Activer internet pour pouvoir vous connecter")
                    .setCancelable(false)
                    .setNegativeButton("ok ",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                    paramDialogInterface.cancel();
                                }
                            }
                    );
            localBuilder.create().show();
        }


        Continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setM_User(User.getText().toString());
                setM_Password(Password.getText().toString());
                if(valid())
                {
                    //recherche le user dans la base de donnée pour le logger
                    firebaseAuth.signInWithEmailAndPassword(getM_User(),getM_Password()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                           if(task.isSuccessful())
                           {
                            String token_id = FirebaseInstanceId.getInstance().getToken();
                            String user_id = firebaseAuth.getCurrentUser().getUid();

                            DatabaseReference user_data = databaseReference.child(user_id);
                            user_data.child("token_id").setValue(token_id).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getApplicationContext(), "Welcome, wait for the map to zoom in", Toast.LENGTH_LONG).show();
                                    Log.d(TAG, "onComplete: Successful");
                                    finish();
                                    //Envoie vers la page welcome de l'application
                                    Intent i = new Intent(MainActivity.this,MapsActivity.class);
                                    startActivity(i);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(MainActivity.this,"Failure",Toast.LENGTH_SHORT).show();
                                }
                            });

                        }else{
                            Toast.makeText(getApplicationContext(), "Wrong UserName or Password", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onComplete: Unsuccessful");
                        }
                        }
                    });
                }
                else
                {

                }

            }
        });

        Text_SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,SignUpActivity.class);
                startActivity(i);
            }
        });
    }

    //Permet de verifier la validité des champs rentrés.
    public boolean valid ()
    {
        boolean result = false;

        if(getM_Password().equals("")||getM_User().equals(""))
        {
            Toast.makeText(getApplicationContext(), "Fill all the fields", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "valid: Doesn't Works : field not fill");
        }
        else
        {
            result=true;
        }
                return result;
    }
}

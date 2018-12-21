package com.example.lucas.loginfirebase;

import android.content.Intent;
import android.support.annotation.NonNull;
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
import com.google.firebase.database.Logger;

public class SignUpActivity extends AppCompatActivity {

    private TextView MainSignUp;
    private EditText NewName;
    private EditText NewUser;
    private EditText NewPassword;
    private Button NewContinue;
    private TextView BackSignIn;
    FirebaseAuth firebaseAuth;
    private String m_Name;
    private String m_UserName;
    private String m_Password;
    private static final String TAG = "LoginFirebase";
    //Database Attribut :
    DatabaseReference mFirebaseDatabase;
    FirebaseDatabase mFirebaseInstance;
    private String m_userId;

    //Getter et Setter
    public String getM_Name() {
        return m_Name;
    }

    public void setM_Name(String m_Name) {
        this.m_Name = m_Name;
    }

    public String getM_Password() {
        return m_Password;
    }

    public void setM_Password(String m_Password) {
        this.m_Password = m_Password;
    }

    public String getM_UserName() {
        return m_UserName;
    }

    public void setM_UserName(String m_UserName) {
        this.m_UserName = m_UserName;
    }

    public String getM_userId() { return m_userId; }

    public void setM_userId(String m_userId) { this.m_userId = m_userId; }

    //ON CREATE
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        MainSignUp = (TextView) findViewById(R.id.Text_MainSignUp);
        NewName = (EditText) findViewById(R.id.EditText_NewName);
        NewUser = (EditText) findViewById(R.id.EditText_NewUserName);
        NewPassword = (EditText) findViewById(R.id.EditText_NewPassword);
        NewContinue = (Button) findViewById(R.id.Button_SignUp);
        BackSignIn = (TextView) findViewById(R.id.TextView_ReturnSignIn);

        //Instancier Authentification
        firebaseAuth = FirebaseAuth.getInstance();

        // Instancier BBD :
         mFirebaseInstance = FirebaseDatabase.getInstance();
       // mFirebaseInstance.setLogLevel(Logger.Level.DEBUG);
        // get reference to 'users' node
        mFirebaseDatabase = mFirebaseInstance.getReference("users");

        //Implémentation du Boutton
        NewContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //On alloue ce qu'on a taper dans les différents edittext au variable correspondantes
                setM_Name(NewName.getText().toString());
                setM_UserName(NewUser.getText().toString());
                setM_Password(NewPassword.getText().toString());
                //on test la condition de validité des champs.
                if(valid())
                {
                    //On demande de créer un nouvelle utilisateur dans fire base
                firebaseAuth.createUserWithEmailAndPassword(getM_UserName(),getM_Password()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //Si cela est fait avec succes
                        if(task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Signed up Sucessful", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onComplete: Successful");
                            //Mettre les données dans la database
                            setM_userId(firebaseAuth.getCurrentUser().getUid());
                            Log.d(TAG, "onClick: Affichage User ID : " + getM_userId());
                            //Creer et stocker le nouvelle utilisateur
                            writeNewUser(getM_userId(),getM_Name(),getM_UserName(),getM_UserName());
                            //Finish the process
                            finish();
                            //Retourne vers la Main Activity
                            Intent i3 = new Intent(SignUpActivity.this,MainActivity.class);
                            startActivity(i3);
                        }
                        else
                        {
                            //Si la création n'est pas faites avec succes
                            Toast.makeText(getApplicationContext(), "Error in Sign in", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onComplete: Unsuccessful");
                        }
                    }
                });

                }

            }
        });

        BackSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i2 = new Intent(SignUpActivity.this,MainActivity.class);
                startActivity(i2);
            }
        });

    }

    public boolean valid ()
    {
        boolean result = false;

        if (getM_Name().equals("")||getM_Password().equals("")||getM_UserName().equals(""))
        {
            Toast.makeText(getApplicationContext(), "Fill all the fields", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "valid: Doesn't Works : field not fill");
        }
        else
        {
            result = true;
        }

        return result;
    }

    public void writeNewUser (String p_UserID, String p_Name, String p_Username, String p_email)
    {
      User user = new User(p_Name,p_Username,p_email);
      mFirebaseDatabase.child(p_UserID).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
          @Override
          public void onSuccess(Void aVoid) {
              Log.d(TAG, "User created");
          }
      }).addOnFailureListener(new OnFailureListener() {
          @Override
          public void onFailure(@NonNull Exception e) {
              Log.d(TAG, "Failure to create user");
          }
      });
        ;
    }
}

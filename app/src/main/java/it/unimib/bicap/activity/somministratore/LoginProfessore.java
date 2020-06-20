package it.unimib.bicap.activity.somministratore;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

import it.unimib.bicap.activity.HomePage;
import it.unimib.bicap.activity.HomePageSomministratore;
import it.unimib.bicap.R;
import it.unimib.bicap.constanti.ActivityConstants;
import it.unimib.bicap.databinding.ActivityLoginProfessoreBinding;

public class LoginProfessore extends AppCompatActivity {

    private static final String TAG = "LoginProfessore";
    private ActivityLoginProfessoreBinding binding;
    private FirebaseAuth mAuth;
    private boolean fromHome;
    private  boolean esisteMail;
    private List<String> sommAttivi;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("utenti");

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        
        fromHome = getIntent().getExtras().getBoolean(ActivityConstants.INTENT_FROM_HOME);

        final SharedPreferences sharedPref = getSharedPreferences("author", Context.MODE_PRIVATE);
        esisteMail = sharedPref.getBoolean("esisteMail", false);
        /*while (sharedPref.getString("sommAttivi", null) == null){

        }*/
        sommAttivi = Arrays.asList(sharedPref.getString("sommAttivi", null).split(","));
        try {
            updateUI(currentUser, fromHome, esisteMail);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void updateUI(final FirebaseUser currentUser, boolean fromHome, boolean esisteMail) throws InterruptedException {
        //final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        this.getSharedPreferences(ActivityConstants.SHARED_PREFERENCE_NAME, 0).edit().remove("autore");
        //final SharedPreferences sharedPref = getSharedPreferences(ActivityConstants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        Log.d(TAG, "esiste Mail: " + esisteMail);
        //esisteMail = sharedPref.getBoolean("esisteMail", false);
        //Log.d(TAG, "currentUser: " + currentUser.getEmail());
        if (currentUser != null) {
            Log.d(TAG, "entra Utente");
            Log.d(TAG, "fromHome: " + fromHome);
            String email = currentUser.getEmail();
            //checkEmailExistsOrNot(email);
            Log.d(TAG, "email: " + currentUser.getEmail());
            Log.d(TAG, "mail esiste? " + esisteMail);
            if (esisteMail) {
                Log.d(TAG, "entra email");
                if (fromHome) {
                    Log.d(TAG, email);
                    Log.d(TAG, "entra fromHome");
                    // Read from the database
                    myRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // This method is called once with the initial value and again
                            // whenever data at this location is updated.

                            String autore = dataSnapshot.child(currentUser.getUid()).child("autore").getValue().toString();

                            Log.d(TAG, "Value is: " + autore);

                            SharedPreferences.Editor editor = getSharedPreferences("author", Context.MODE_PRIVATE).edit();

                            editor.putString("autore", autore);
                            editor.commit();
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            // Failed to read value
                            Log.w(TAG, "Failed to read value.", error.toException());
                        }
                    });

                    Intent intentLogged = new Intent(this, HomePageSomministratore.class);
                    //intentLogged.putExtra("Email", email);
                    intentLogged.putExtra(ActivityConstants.INTENT_FROM_HOME, fromHome);
                    intentLogged.putExtra(ActivityConstants.INTENT_EMAIL, email);
                    startActivity(intentLogged);
                    finish();
                } else {
                    Log.d(TAG,"not from home");
                    Intent intentHome = new Intent(this, HomePage.class);
                    startActivity(intentHome);
                    finish();
                    overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @SuppressLint("SourceLockedOrientationActivity")
        @Override
        protected void onCreate (@Nullable Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            binding = ActivityLoginProfessoreBinding.inflate(getLayoutInflater());
            View view = binding.getRoot();
            setContentView(view);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            Toolbar toolbar = findViewById(R.id.toolbar_main);
            toolbar.setTitle("");
            setSupportActionBar(toolbar);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intentHome = new Intent(getApplicationContext(), HomePage.class);
                    startActivity(intentHome);
                    finish();
                    overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                }
            });

            mAuth = FirebaseAuth.getInstance();

            binding.imAccedi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String email = binding.etEmail.getText().toString();
                    String password = binding.etPassword.getText().toString();

                    if (email.equals("")) {
                        Snackbar.make(v, "Attenzione, email non valida", Snackbar.LENGTH_SHORT).show();
                    } else if (password.equals("")) {
                        Snackbar.make(v, "Attenzione, password non valida", Snackbar.LENGTH_SHORT).show();
                    } else
                        loginUser(email, password);
                }
            });

            binding.tvPassDimenticata.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intentPassDimenticata = new Intent(getApplicationContext(), RecuperaPassword.class);
                    startActivity(intentPassDimenticata);
                    finish();
                }
            });
        }

        private void loginUser (final String email, String password){
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.i(TAG, "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                fromHome = true;
                                /*boolean esisteMail = false;
                                int passaUtente = checkMail(email);
                                while (passaUtente == Integer.parseInt(null)){

                                }
                                if (passaUtente == 1)
                                    esisteMail = true;*/

                                if (sommAttivi.contains(email)) {
                                    Log.d(TAG, "sommAttivi contiente: " + sommAttivi.toString());
                                    esisteMail = true;
                                }
                                else {
                                    esisteMail = false;
                                    Log.d(TAG, "sommAttivi non contiene: " + sommAttivi.toString());
                                }

                                try {
                                    updateUI(user, fromHome, esisteMail);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.i(TAG, "createUserWithEmail:failure", task.getException());
                                esisteMail = false;
                                Snackbar.make(binding.linearlayout, "Attenzione, credenziali non valide !", Snackbar.LENGTH_SHORT).show();
                                try {
                                    updateUI(null, fromHome, esisteMail);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
            }

    /*private int checkMail(final String email) {
        //final boolean[] valore = {false};
        final int[] continua = new int[0];
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (DataSnapshot d : dataSnapshot.getChildren()){
                    if (continua == null) {
                        if (d.child("email").getValue().equals(email) && d.child("attivo").getValue().equals("true")) {
                            // valore[0] = true;
                            continua[0] = 1;
                        }
                    }
                }
                if (continua == null)
                    continua[0] = 2;
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        while(continua.length == 0){

        }

        //return valore[0];
        //assert continua != null;
        return  continua[0];
    }*/

    @Override
    public void startActivity(Intent intent){
        super.startActivity(intent);
        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
    }

    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), HomePage.class));
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        finish();

    }
}

//TODO: Email: admin@admin.com Password:alessio
//TODO: aaggiungere nome e cognome
//TODO: aggiungere il controllo se viene eliminato e aggiungere la possibilità di eliminare un somministratore



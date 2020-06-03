package it.unimib.bicap;

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

import it.unimib.bicap.constanti.ActivityConstants;
import it.unimib.bicap.databinding.ActivityLoginProfessoreBinding;

public class LoginProfessore extends AppCompatActivity {

    private static final String TAG = "LoginProfessore";
    private ActivityLoginProfessoreBinding binding;
    private FirebaseAuth mAuth;
    private boolean fromHome;
    private  boolean esisteMail;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("utenti");

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        
        fromHome = getIntent().getExtras().getBoolean("fromHome");

        final SharedPreferences sharedPref = getSharedPreferences("author", Context.MODE_PRIVATE);
        esisteMail = sharedPref.getBoolean("esisteMail", false);
        try {
            updateUI(currentUser, fromHome, esisteMail);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void updateUI(FirebaseUser currentUser, boolean fromHome, boolean esisteMail) throws InterruptedException {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        this.getSharedPreferences(ActivityConstants.SHARED_PREFERENCE_NAME, 0).edit().remove("autore");
        final SharedPreferences sharedPref = getSharedPreferences(ActivityConstants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        esisteMail = sharedPref.getBoolean("esisteMail", false);
        if (user != null) {
            String email = user.getEmail();
            //checkEmailExistsOrNot(email);
            Log.d(TAG, "email: " + user.getEmail());
            Log.d(TAG, "mail esiste? " + esisteMail);
            if (esisteMail) {
                if (fromHome) {
                    Log.d(TAG, email);
                    // Read from the database
                    myRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // This method is called once with the initial value and again
                            // whenever data at this location is updated.

                            String autore = dataSnapshot.child(user.getUid()).child("autore").getValue().toString();

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
                    intentLogged.putExtra("fromHome", fromHome);
                    intentLogged.putExtra("email", email);
                    startActivity(intentLogged);
                    finish();
                } else {
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
        }

        private void loginUser (String email, String password){
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.i(TAG, "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                fromHome = true;
                                esisteMail = true;
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

    @Override
    public void startActivity(Intent intent){
        super.startActivity(intent);
        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
    }

    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), HomePage.class));
        finish();

    }
}

//TODO: Email: admin@admin.com Password:alessio
//TODO: aaggiungere nome e cognome
//TODO: aggiungere il controllo se viene eliminato e aggiungere la possibilità di eliminare un somministratore



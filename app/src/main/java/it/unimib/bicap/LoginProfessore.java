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

import it.unimib.bicap.databinding.ActivityLoginProfessoreBinding;

public class LoginProfessore extends AppCompatActivity {

    private static final String TAG = "LoginProfessore";
    private ActivityLoginProfessoreBinding binding;
    private FirebaseAuth mAuth;
    private boolean fromHome;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("utenti");

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        
        fromHome = getIntent().getExtras().getBoolean("fromHome");
        updateUI(currentUser, fromHome);
    }

    private void updateUI(FirebaseUser currentUser, boolean fromHome) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final SharedPreferences sharedPref = getSharedPreferences("author", Context.MODE_PRIVATE);

        if (user != null) {
            if (fromHome) {
                String email = user.getEmail();
                // Read from the database

                if(sharedPref.getString("autore", null) == null) {

                    Log.d(TAG, "shared preferences non presenti");

                    myRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // This method is called once with the initial value and again
                            // whenever data at this location is updated.

                            String nome = dataSnapshot.child(user.getUid()).child("nome").getValue().toString();
                            String cognome = dataSnapshot.child(user.getUid()).child("cognome").getValue().toString();

                            String autore = nome + " " +  cognome;

                            Log.d(TAG, "Value is: " + autore);

                            SharedPreferences.Editor editor = sharedPref.edit();

                            editor.putString("autore", autore);
                            editor.commit();
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            // Failed to read value
                            Log.w(TAG, "Failed to read value.", error.toException());
                        }
                    });
                }
                else
                    Log.d(TAG, "shared preferences presenti");

                Intent intentLogged = new Intent(this, HomePageSomministratore.class);
                intentLogged.putExtra("Email", email);
                intentLogged.putExtra("fromHome", fromHome);
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
            toolbar.setTitle("Area Professore");
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
                                updateUI(user, fromHome);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.i(TAG, "createUserWithEmail:failure", task.getException());
                                Snackbar.make(binding.linearlayout, "Attenzione, credenziali non valide !", Snackbar.LENGTH_SHORT).show();
                                updateUI(null, fromHome);
                            }
                        }
                    });
        }

    //override startActivity con animazione slide avanti
    @Override
    public void startActivity(Intent intent){
        super.startActivity(intent);
        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
    }
}

//TODO: Email: admin@admin.com Password:alessio
//TODO: aaggiungere nome e cognome



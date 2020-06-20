package it.unimib.bicap;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import it.unimib.bicap.constanti.ActivityConstants;
import it.unimib.bicap.databinding.ActivityLoginProfessoreBinding;

public class LoginSomministratore extends AppCompatActivity {

    private static final String TAG = "LoginSomministratore";
    private ActivityLoginProfessoreBinding binding;
    private FirebaseAuth mAuth;
    private boolean daHomepage;
    private boolean esisteMail;
    private ProgressDialog dialogCaricamento;
    FirebaseUser utenteCorrente;
    private List<String> sommAttivi = new ArrayList<>();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("utenti");

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        utenteCorrente = mAuth.getCurrentUser();
        
        daHomepage = getIntent().getExtras().getBoolean(ActivityConstants.INTENT_FROM_HOME);

        final SharedPreferences sharedPref = getSharedPreferences("author", Context.MODE_PRIVATE);

        if (sharedPref.getString("sommAttivi", null) != null) {
            sommAttivi = Arrays.asList(sharedPref.getString("sommAttivi", null).split(","));
        }

        try {
            updateUI(utenteCorrente, daHomepage);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void updateUI(final FirebaseUser currentUser, boolean fromHome) throws InterruptedException {
        //final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        this.getSharedPreferences(ActivityConstants.SHARED_PREFERENCE_NAME, 0).edit().remove("autore");
        //final SharedPreferences sharedPref = getSharedPreferences(ActivityConstants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        //esisteMail = sharedPref.getBoolean("esisteMail", false);
        //Log.d(TAG, "currentUser: " + currentUser.getEmail());
        if (currentUser != null) {
            String email = currentUser.getEmail();
            if (daHomepage) {
                controllaEmail(email);

                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String autore = dataSnapshot.child(currentUser.getUid()).child("autore").getValue().toString();
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
            } else{
                Intent intentHome = new Intent(getApplicationContext(), HomePage.class);
                startActivity(intentHome);
                finish();
                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
            }
            /*if (esisteMail) {
                Log.d(TAG, "entra email");
                if (fromHome) {
                    Log.d(TAG, email);
                    Log.d(TAG, "entra fromHome");
                    Read from the database
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
                } else {
                    Intent intentHome = new Intent(this, HomePage.class);
                    startActivity(intentHome);
                    finish();
                    overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                }
            }*/
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

            dialogCaricamento = new ProgressDialog(this);
            dialogCaricamento.setTitle("Caricamento");
            dialogCaricamento.setMessage("Attendere...");
            dialogCaricamento.setCancelable(false);

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
                    } else {
                        dialogCaricamento.show();
                        loginUser(email, password);
                    }
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
                                daHomepage = true;
                                try {
                                    updateUI(user, daHomepage);
                                    //sommAttivi.add(email);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                dialogCaricamento.dismiss();
                                Snackbar.make(binding.linearlayout, "Attenzione, credenziali non valide !", Snackbar.LENGTH_SHORT).show();
                                try {
                                    updateUI(null, daHomepage);
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
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        finish();

    }

    public void controllaEmail (final String email){
        if (sommAttivi.contains(email)){
            Intent intentLogged = new Intent(getApplicationContext(), HomePageSomministratore.class);
            intentLogged.putExtra(ActivityConstants.INTENT_FROM_HOME, daHomepage);
            intentLogged.putExtra(ActivityConstants.INTENT_EMAIL, email);
            startActivity(intentLogged);
            finish();
        } else{
            dialogCaricamento.dismiss();
            mostraDialogUtenteNotAttivo();
        }
    }

    private void mostraDialogUtenteNotAttivo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Attenzzione");
        builder.setMessage("Il somministratore con email: " + binding.etEmail.getText().toString() + "\n è stato disattivato.");
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        mAuth.signOut();
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}

//TODO: Email: admin@admin.com Password:alessio
//TODO: aaggiungere nome e cognome
//TODO: aggiungere il controllo se viene eliminato e aggiungere la possibilità di eliminare un somministratore



package it.unimib.bicap;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.transition.MaterialContainerTransform;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import it.unimib.bicap.constanti.ActivityConstants;
import it.unimib.bicap.databinding.ActivityCreazioneSomministartoreBinding;

public class CreazioneSomministratore extends AppCompatActivity {

    private ActivityCreazioneSomministartoreBinding binding;
    private FirebaseAuth mAuth;
    private static final String TAG = "CreazioneSomministratore";
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("utenti");
    private FirebaseAuth mAuth1;
    private FirebaseAuth mAuth2;
    private FirebaseUser user;
    private String email1;
    private String password1;
    private ProgressDialog dialog;
    private String autore1;

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCreazioneSomministartoreBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        dialog = new ProgressDialog(this);

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(" ");
        toolbar.inflateMenu(R.menu.main_menu);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth = FirebaseAuth.getInstance();
                String email = getIntent().getStringExtra(ActivityConstants.INTENT_EMAIL);
                Intent intentHomepage = new Intent(getApplicationContext(), HomePageSomministratore.class);
                intentHomepage.putExtra(ActivityConstants.INTENT_EMAIL, email);
                startActivity(intentHomepage);
                finish();
            }
        });

        mAuth1 = FirebaseAuth.getInstance();

        FirebaseOptions firebaseOptions = new FirebaseOptions.Builder()
                .setDatabaseUrl(ActivityConstants.DATABASE_URL)
                .setApiKey(ActivityConstants.DATABASE_API_KEY)
                .setApplicationId(ActivityConstants.DATABASE_APPLICATION_ID).build();

        try { FirebaseApp myApp = FirebaseApp.initializeApp(getApplicationContext(), firebaseOptions, "AnyAppName");
            mAuth2 = FirebaseAuth.getInstance(myApp);
        } catch (IllegalStateException e){
            mAuth2 = FirebaseAuth.getInstance(FirebaseApp.getInstance("AnyAppName"));
        }

        final String autore = binding.etNomeSomm.getText().toString();
        Log.d(TAG, autore);

        //Log.d(TAG, email);

        //Log.d(TAG, password);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentCreazioneSomm = new Intent(getApplicationContext(), GestioneSomministratore.class);
                startActivity(intentCreazioneSomm);
                finish();
                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
            }
        });


        binding.btnRegistraSomm.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onClick(View v) {
                String email = binding.etEmailSomm.getText().toString();
                if (binding.switchButton.isChecked()) {
                    checkMailSecond(email);
                } else {
                    String autore = binding.etNomeSomm.getText().toString();
                    Log.d(TAG, "premo il bottone");
                    String password = binding.etPasswordSomm.getText().toString();
                    email1 = email;
                    password1 = password;
                    autore1 = autore;
                    dialog.setTitle("Attendere");
                    dialog.setMessage("Attendere");
                    dialog.show();
                    if (password.length() > 5) {
                        createUser(email, password, autore);
                        Log.d(TAG, "creato nuovo somministratore");
                    } else {
                        Toast.makeText(getApplicationContext(), "La password deve essere più lunga di 5 caratteri", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        binding.switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    binding.tfAutore.setVisibility(View.INVISIBLE);
                    binding.tfPassword.setVisibility(View.INVISIBLE);
                } else{
                    binding.tfAutore.setVisibility(View.VISIBLE);
                    binding.tfPassword.setVisibility(View.VISIBLE);
                }
            }
        });

        }

    private void checkMailSecond(final String email) {
        myRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean variabile = false;
                for (DataSnapshot d : dataSnapshot.getChildren()){
                    if (!variabile){
                        if (d.child("email").getValue()!= null && d.child("email").getValue().equals(email)){
                            variabile = true;
                            Log.d(TAG, "variabile: " + variabile);
                        }
                    }
                }
                if (variabile){
                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @SuppressLint("LongLogTag")
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // This method is called once with the initial value and again
                            // whenever data at this location is updated.
                            HashMap<String, Object> map = new HashMap<>();
                            for (DataSnapshot d : dataSnapshot.getChildren()){
                                if (d.child("email").getValue().equals(email)) {
                                    Log.d(TAG, "trovato  utente");
                                    map.put("attivo", "true");
                                    d.getRef().updateChildren(map);
                                    //d.getRef().child("attivo").setValue("true");
                                    Log.d(TAG, "attivo? " + d.child("attivo").getValue().toString());
                                }
                            }

                            showDialogSecond();

                            Snackbar.make(findViewById(android.R.id.content),
                                    "Utente riattivato correttamente", Snackbar.LENGTH_SHORT).show();
                            //String value = dataSnapshot.getValue(String.class);
                            //Log.d(TAG, "Value is: " + value);
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            dialog.dismiss();
                            // Failed to read value
                            //Log.w(TAG, "Failed to read value.", error.toException());
                        }
                    });
                } else {
                    Snackbar.make(findViewById(android.R.id.content),
                            "Attenzione, l'email potrebbe non esistere oppure l'utente è già attivo", Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void createUser(final String email, String password, final String autore) {
        checkMail(email, password, autore);
        /*if (checkMail(email)) {
            myRef.addValueEventListener(new ValueEventListener() {
                @SuppressLint("LongLogTag")
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    for (DataSnapshot d : dataSnapshot.getChildren()){
                        if (d.child("email").equals(email)) {
                            Log.d(TAG, "trovato  utente");
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("attivo", "true");
                            d.getRef().updateChildren(map);
                        }
                    }
                    //String value = dataSnapshot.getValue(String.class);
                    //Log.d(TAG, "Value is: " + value);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    //Log.w(TAG, "Failed to read value.", error.toException());
                }
            });
        } else {
            mAuth2.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @SuppressLint("LongLogTag")
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                //Log.d(TAG, "createUserWithEmail:success");
                                user = mAuth2.getCurrentUser();
                                Log.d(TAG, "problema successo");
                                myRef.child(user.getUid()).child("autore").setValue(autore);
                                myRef.child(user.getUid()).child("email").setValue(email);
                                myRef.child(user.getUid()).child("attivo").setValue("true");
                                //mFirebaseAuth2.updateCurrentUser(mAuth.getCurrentUser());
                                mAuth2.signOut();
                                //TODO: fix presa user
                            } else {
                                // If sign in fails, display a message to the user.
                                //Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Log.d(TAG, "problema insuccesso");
                                Toast.makeText(getApplicationContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                            }

                            // ...
                        }
                    });
        }*/
    }

    @SuppressLint("LongLogTag")
    private void checkMail(final String email, final String password, final String autore) {
        // Read from the database
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                boolean variabile = false;
                for (DataSnapshot d : dataSnapshot.getChildren()){
                    Log.d(TAG, "email esistente: " + d.child("email").getValue().toString());
                    if(variabile == false){
                        if (d.child("email").getValue()!= null && d.child("email").getValue().equals(email)){
                            variabile = true;
                            Log.d(TAG, "variabile: " + variabile);
                        }
                    }
                }
                Log.d(TAG, "variabile metodo: " + variabile);
                if (variabile){
                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @SuppressLint("LongLogTag")
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // This method is called once with the initial value and again
                            // whenever data at this location is updated.
                            HashMap<String, Object> map = new HashMap<>();
                            for (DataSnapshot d : dataSnapshot.getChildren()){
                                if (d.child("email").getValue().equals(email)) {
                                    Log.d(TAG, "trovato  utente");
                                    map.put("attivo", "true");
                                    d.getRef().updateChildren(map);
                                    //d.getRef().child("attivo").setValue("true");
                                    Log.d(TAG, "attivo? " + d.child("attivo").getValue().toString());
                                }
                            }
                            //String value = dataSnapshot.getValue(String.class);
                            //Log.d(TAG, "Value is: " + value);
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            // Failed to read value
                            //Log.w(TAG, "Failed to read value.", error.toException());
                        }
                    });
                }
                else{
                    mAuth2.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(CreazioneSomministratore.this, new OnCompleteListener<AuthResult>() {
                                @SuppressLint("LongLogTag")
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        //Log.d(TAG, "createUserWithEmail:success");
                                        user = mAuth2.getCurrentUser();
                                        Log.d(TAG, "problema successo");
                                        myRef.child(user.getUid()).child("autore").setValue(autore);
                                        myRef.child(user.getUid()).child("email").setValue(email);
                                        myRef.child(user.getUid()).child("attivo").setValue("true");
                                        //mFirebaseAuth2.updateCurrentUser(mAuth.getCurrentUser());
                                        dialog.dismiss();
                                        showDialog();
                                        mAuth2.signOut();
                                        //TODO: fix presa user
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        //Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                        Log.d(TAG, "problema insuccesso");
                                        Toast.makeText(getApplicationContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                                    }

                                    // ...
                                }
                            });
                }
                //String value = dataSnapshot.getValue(String.class);
                //Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    public void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Hai creato un somministratore");
        builder.setMessage("Hai creato un nuovo somministratore con i seguenti dati: \nNome: " + autore1 + "\nEmail: " + email1 + "\n" + "Password: " + password1);
        builder.setPositiveButton("Torna alla homepage", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent HomePageSomministratoreRicarica = new Intent(getApplicationContext(), GestioneSomministratore.class);
                startActivity(HomePageSomministratoreRicarica);
                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showDialogSecond() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Hai riattivato un somministratore");
        builder.setMessage("Hai riattivato un vecchio somministratore con la seguente Email: \nEmail: " + binding.etEmailSomm.getText());
        builder.setPositiveButton("Torna alla homepage", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent HomePageSomministratoreRicarica = new Intent(getApplicationContext(), GestioneSomministratore.class);
                startActivity(HomePageSomministratoreRicarica);
                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), GestioneSomministratore.class));
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        finish();
    }
}

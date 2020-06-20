package it.unimib.bicap.activity.somministratore;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
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

import it.unimib.bicap.R;
import it.unimib.bicap.constanti.ActivityConstants;
import it.unimib.bicap.databinding.ActivityCreazioneSomministartoreBinding;

public class CreazioneSomministratore extends AppCompatActivity {

    private ActivityCreazioneSomministartoreBinding binding;
    private static final String TAG = "CreazioneSomministratore";
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("utenti");
    private FirebaseAuth mAuth1;
    private FirebaseAuth mAuth2;
    private FirebaseUser utente;
    private String email;
    private String password;
    private String autore;
    private ProgressDialog dialog;

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
                Intent intentCreazioneSomm = new Intent(getApplicationContext(), GestioneSomministratore.class);
                startActivity(intentCreazioneSomm);
                finish();
                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
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

        binding.btnRegistraSomm.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onClick(View v) {
                email = binding.etEmailSomm.getText().toString();
                autore = binding.etNomeSomm.getText().toString();
                password = binding.etPasswordSomm.getText().toString();
                dialog.setTitle("Caricamento");
                dialog.setMessage("Attendere");
                dialog.show();
                if (email.equals("") || (password.equals("") && binding.tfPassword.getVisibility() == View.VISIBLE) || (autore.equals("")
                        && binding.tfAutore.getVisibility() == View.VISIBLE)) {
                    dialog.dismiss();
                    Snackbar.make(findViewById(android.R.id.content), "Attenzione, ci sono dei campi vuoti", Snackbar.LENGTH_SHORT).show();
                } else if (binding.switchButton.isChecked()) {
                    riattivazioneUtente(email);
                } else {
                    if (password.length() > 5) {
                        creaUtente(email, password, autore);
                    } else {
                        Snackbar.make(findViewById(android.R.id.content), "La password deve essere più lunga di 5 caratteri", Snackbar.LENGTH_SHORT).show();
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
                    binding.etEmailSomm.setText("");
                } else{
                    binding.tfAutore.setVisibility(View.VISIBLE);
                    binding.tfPassword.setVisibility(View.VISIBLE);
                    binding.etEmailSomm.setText("");
                }
            }
        });

        }

    private void riattivazioneUtente(final String email) {
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean utenteTrovato = false;
                for (DataSnapshot d : dataSnapshot.getChildren()){
                    if (! utenteTrovato){
                        if (d.child("attivo").getValue() != null && d.child("attivo").getValue().equals("false") &&
                                d.child("email").getValue() != null && d.child("email").getValue().equals(email)){
                            utenteTrovato = true;
                            //Log.d(TAG, "utenteTrovato: " + utenteTrovato);
                        }
                    }
                }
                if (utenteTrovato){
                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @SuppressLint("LongLogTag")
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            HashMap<String, Object> map = new HashMap<>();
                            for (DataSnapshot d : dataSnapshot.getChildren()){
                                if (d.child("email").getValue().equals(email)) {
                                    Log.d(TAG, "trovato  utente");
                                    map.put("attivo", "true");
                                    d.getRef().updateChildren(map);
                                }
                            }
                            mostraDialogRiattivazione();
                            dialog.dismiss();
                            Snackbar.make(findViewById(android.R.id.content),
                                    "Utente riattivato correttamente", Snackbar.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            dialog.dismiss();
                        }
                    });
                } else {
                    dialog.dismiss();
                    mostraDialogErroreRiattivazione();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void creaUtente(final String email, final String password, final String autore) {
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean utenteTrovato = false;
                for (DataSnapshot d : dataSnapshot.getChildren()){
                    if(!utenteTrovato){
                        if (d.child("email").getValue() != null && d.child("email").getValue().equals(email)){
                            utenteTrovato = true;
                        }
                    }
                }
                if (utenteTrovato){
                    dialog.dismiss();
                    mostraDialogErroreCreazione();
                }
                else{
                    mAuth2.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(CreazioneSomministratore.this, new OnCompleteListener<AuthResult>() {
                                @SuppressLint("LongLogTag")
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        utente = mAuth2.getCurrentUser();
                                        Log.d(TAG, "problema successo");
                                        myRef.child(utente.getUid()).child("autore").setValue(autore);
                                        myRef.child(utente.getUid()).child("email").setValue(email);
                                        myRef.child(utente.getUid()).child("attivo").setValue("true");
                                        dialog.dismiss();
                                        mostraDialogCreazione();
                                        mAuth2.signOut();
                                    } else {
                                        Snackbar.make(findViewById(android.R.id.content), "Authentication failed.", Snackbar.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    public void mostraDialogCreazione() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Hai creato un somministratore");
        builder.setMessage("Hai creato un nuovo somministratore con i seguenti dati: \nNome: " + autore + "\nEmail: " + email + "\n" + "Password: " + password);
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

    public void mostraDialogRiattivazione() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Hai riattivato un somministratore");
        builder.setMessage("Hai riattivato un vecchio somministratore con la seguente Email: \n" + binding.etEmailSomm.getText());
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
        dialog.setCancelable(false);
    }

    private void mostraDialogErroreCreazione() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Attenzione");
        builder.setMessage("Stai cercando di creare un utente già presente nell'app");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCancelable(false);
    }

    private void mostraDialogErroreRiattivazione() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Attenzione");
        builder.setMessage("Stai cercando di riattivare un utente già attivo nell'app oppure che non esiste");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCancelable(false);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), GestioneSomministratore.class));
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        finish();
    }
}

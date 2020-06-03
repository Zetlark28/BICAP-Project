package it.unimib.bicap;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCreazioneSomministartoreBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth = FirebaseAuth.getInstance();
                String email = getIntent().getStringExtra("email");
                Intent intentHomepage = new Intent(getApplicationContext(), HomePageSomministratore.class);
                intentHomepage.putExtra("email", email);
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
                String autore = binding.etNomeSomm.getText().toString();
                Log.d(TAG, "premo il bottone");
                String email = binding.etEmailSomm.getText().toString();
                String password = binding.etPasswordSomm.getText().toString();
                createUser(email, password, autore);
                Log.d(TAG, "creato nuovo somministratore");
                Toast.makeText(getApplicationContext(), "Hai creato un nuovo somministratore con i seguenti dati: \n" + "Nome: " + autore + "\n" + "Email: " + email, Toast.LENGTH_SHORT).show();
                Intent intentHomepage = new Intent(getApplicationContext(), GestioneSomministratore.class);
                startActivity(intentHomepage);
                finish();
            }
        });

        }

    private void createUser(final String email, String password, final String autore) {
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
    }

}

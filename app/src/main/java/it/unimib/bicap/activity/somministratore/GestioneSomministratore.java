package it.unimib.bicap.activity.somministratore;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.unimib.bicap.R;
import it.unimib.bicap.constanti.ActivityConstants;
import it.unimib.bicap.databinding.ActivityGestioneSomministratoreBinding;

public class GestioneSomministratore extends AppCompatActivity {

    private ActivityGestioneSomministratoreBinding binding;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("utenti");
    private final String TAG = "GestioneSomministratore";
    HashMap<String, String> somministratori = new HashMap<>();
    private ProgressDialog progressDialog;
    List<String> email = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityGestioneSomministratoreBinding.inflate(getLayoutInflater());
        View v = binding.getRoot();
        setContentView(v);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        toolbar.setTitle("Gestisci i somministratori");
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentQuestionari = new Intent(getApplicationContext(), HomePageSomministratore.class);
                startActivity(intentQuestionari);
                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                finish();
            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Caricamento...");
        progressDialog.setProgress(10);
        progressDialog.setMax(100);
        progressDialog.setMessage("Attendi!");

        new DownloadSomministratoriTask().execute();

        binding.btnCreaSomm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentCreazioneSomm = new Intent(getApplicationContext(), CreazioneSomministratore.class);
                startActivity(intentCreazioneSomm);
                overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
            }
        });

        boolean finito = getListaSommAttivi();

        while(! finito){

        }
        Log.d(TAG, "Hashmap finale: " + somministratori);

        binding.btnEliminaSomm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent deleteSomm = new Intent(getApplicationContext(), EliminaSomministratore.class);
                Log.d(TAG, "Intent problem: " + somministratori.toString());
                deleteSomm.putStringArrayListExtra("emails", (ArrayList<String>) email);
                deleteSomm.putExtra(ActivityConstants.INTENT_SOMMINISTRATORI, somministratori);
                deleteSomm.putExtra(ActivityConstants.INTENT_HOME, true);
                startActivity(deleteSomm);
                overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                finish();
            }
        });
    }

    public class DownloadSomministratoriTask extends AsyncTask<Void, Void, Void> {
        public void onPreExecute() {
            progressDialog.show();
        }
        public Void doInBackground(Void... unused) {
            return null;
        }
    }

    public boolean getListaSommAttivi() {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //HashMap<String, String> mappa = new HashMap<>();
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    if(d.child("attivo").getValue() != null && d.child("email").getValue() != null && d.child("attivo").getValue().equals("true") && ! d.child("email").getValue().equals("admin@admin.com")) {
                        somministratori.put(d.child("email").getValue().toString(), d.child("autore").getValue().toString());
                        email.add(d.child("email").getValue().toString());
                        Log.d(TAG, "Hasmap: " + somministratori.toString());
                        //somministratori.add(d.getValue().toString());
                    }
                }

                progressDialog.dismiss();
                //Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        return true;
    }

    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), HomePageSomministratore.class));
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        finish();

    }

}

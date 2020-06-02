package it.unimib.bicap;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Caricamento...");
        progressDialog.setProgress(10);
        progressDialog.setMax(100);
        progressDialog.setMessage("Attendi!");

        new DownloadSomministratoriTask().execute();

        Log.d(TAG, "HashMap finale 1: " + somministratori.toString());
        boolean finito = getListaSommAttivi();

        while(! finito){

        }
        Log.d(TAG, "Hashmap finale 2: " + somministratori);

        binding.btnEliminaSomm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentCreaSomm = new Intent(getApplicationContext(), EliminaSomministratore.class);
                Log.d(TAG, "Intent problem: " + somministratori.toString());
                intentCreaSomm.putStringArrayListExtra("emails", (ArrayList<String>) email);
                intentCreaSomm.putExtra("somministratori", somministratori);
                startActivity(intentCreaSomm);
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
                    somministratori.put(d.child("email").getValue().toString(), d.child("autore").getValue().toString());
                    email.add(d.child("email").getValue().toString());
                    Log.d(TAG, "Hasmap: " + somministratori.toString());
                    //somministratori.add(d.getValue().toString());
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

}

package it.unimib.bicap.activity.somministratore;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;
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

        getListaSommAttivi();

        binding.btnEliminaSomm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (somministratori.size() == 0){
                    Snackbar.make(v, "Attenzione, non sono presenti somministratori da eliminare", Snackbar.LENGTH_SHORT).show();
                } else {
                    Intent eliminaSomm = new Intent(getApplicationContext(), EliminaSomministratore.class);
                    eliminaSomm.putStringArrayListExtra("emails", (ArrayList<String>) email);
                    eliminaSomm.putExtra(ActivityConstants.INTENT_SOMMINISTRATORI, somministratori);
                    startActivity(eliminaSomm);
                    overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                    finish();
                }
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    public class DownloadSomministratoriTask extends AsyncTask<Void, Void, Void> {
        public void onPreExecute() {
            progressDialog.show();
        }
        public Void doInBackground(Void... unused) {
            return null;
        }
    }

    public void getListaSommAttivi() {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    String attivo = (String) d.child("attivo").getValue();
                    String emailFirebase = (String) d.child("email").getValue();
                    String autore = (String) d.child("autore").getValue();
                    if(attivo!= null && emailFirebase != null && attivo.equals("true")
                            && (! emailFirebase.equals(ActivityConstants.EMAIL_ADMIN) && ! emailFirebase.equals(ActivityConstants.EMAIL_PROF)
                    && ! emailFirebase.equals(ActivityConstants.AUTHORIZED_EMAIL))) {
                        somministratori.put(emailFirebase, autore);
                        email.add(emailFirebase);
                    }
                }

                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), HomePageSomministratore.class));
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        finish();

    }

}

package it.unimib.bicap.activity.somministratore;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import it.unimib.bicap.R;
import it.unimib.bicap.activity.HomePage;
import it.unimib.bicap.constanti.ActivityConstants;
import it.unimib.bicap.databinding.ActivityHomepageSomministratoreBinding;
import it.unimib.bicap.service.PDFViewer;


public class HomePageSomministratore extends AppCompatActivity {
    private static final int ONE_MB = 1024 * 1024;
    private static JSONObject progetti;
    private static JSONArray progettiAutore;
    private static JSONArray progettiDaSelezionare;
    private ProgressDialog progressDialog;
    private ProgressDialog errorDialog;
    private CountDownTimer cTimer;
    private static final String TAG = "HomePageSomministratore";
    private ActivityHomepageSomministratoreBinding binding;
    private FirebaseAuth mAuth;
    private String emailSomministratore;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mAuth = FirebaseAuth.getInstance();

        binding = ActivityHomepageSomministratoreBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        emailSomministratore = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        toolbar.setTitle(ActivityConstants.HOMEPAGE_SOMMINISTRATORE_TOOLBAR_TITLE);
        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.main_menu);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Caricamento...");
        progressDialog.setProgress(10);
        progressDialog.setMax(100);
        progressDialog.setMessage("Attendi!");
        errorDialog = new ProgressDialog(this);
        startTimer();

        new DownloadProgettiTask().execute();
        final String autore = getSharedPreferences(ActivityConstants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE).getString(ActivityConstants.SHARED_PREFERENCE_AUTORE_KEY, null);
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        StorageReference ref = mStorageRef.child(ActivityConstants.FIREBASE_STORAGE_CHILD_PROGETTI);
        ref.getBytes(ONE_MB).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                String json;
                try {
                    json = new String(bytes, StandardCharsets.UTF_8);
                    progetti = new JSONObject(json);
                    progettiDaSelezionare = progetti.getJSONArray("progetti");
                    progettiAutore = new JSONArray();
                    for (int i = 0; i < progettiDaSelezionare.length(); i++) {
                        if (progettiDaSelezionare.getJSONObject(i).getString("autore").equals(autore)) {
                            progettiAutore.put(progettiDaSelezionare.getJSONObject(i));
                        }
                    }
//
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                progressDialog.dismiss();
                cancelTimer();
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentLogProf = new Intent(getApplicationContext(), HomePage.class);
                startActivity(intentLogProf);
                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                finish();
            }
        });

        binding.btnElimina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(progettiAutore.length()!=0){
                    Intent intentEliminaProgetto = new Intent (getApplicationContext(), EliminaProgetti.class);
                    intentEliminaProgetto.putExtra(ActivityConstants.INTENT_LISTA_PROGETTI_AUTORE, progettiAutore.toString());
                    intentEliminaProgetto.putExtra(ActivityConstants.INTENT_LISTA_PROGETTI, progetti.toString());
                    intentEliminaProgetto.putExtra(ActivityConstants.INTENT_RETURN, false);
                    startActivity(intentEliminaProgetto);
                    overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                }

                Snackbar.make(v, "Non sono presenti progetti da eliminare", Snackbar.LENGTH_SHORT).show();

            }
        });


        binding.btnCrea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (progettiAutore != null) {
                    Intent intentCreaProgetto = new Intent(getApplicationContext(), CreazioneProgetto.class);
                    intentCreaProgetto.putExtra(ActivityConstants.INTENT_LISTA_PROGETTI, progettiDaSelezionare.toString());
                    startActivity(intentCreaProgetto);
                    overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                } else {
                    Snackbar.make(v, "Attento, è in corso un processo interno!", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        FirebaseAuth.getInstance().getCurrentUser();
        String email = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
        MenuItem item = menu.findItem(R.id.addSomm);
        assert email != null;
        if (!email.equals(ActivityConstants.AUTHORIZED_EMAIL) && !email.equals(ActivityConstants.EMAIL_ADMIN) && !email.equals(ActivityConstants.EMAIL_PROF))
            item.setVisible(false);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }
    void startTimer() {
        cTimer = new CountDownTimer(10000, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                errorCarica();
            }
        };
        cTimer.start();
    }

    void cancelTimer() {
        if(cTimer!=null)
            cTimer.cancel();
    }
    public void errorCarica() {
        progressDialog.dismiss();
        errorDialog.setTitle("Problemi di connessione.");
        errorDialog.setMessage("Assicurati di essere connesso all'internet e riprova!");
        errorDialog.setCancelable(false);
        errorDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Ricarica", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent HomePageSomministratoreRicarica = new Intent (getApplicationContext(), HomePageSomministratore.class);
                startActivity(HomePageSomministratoreRicarica);
                overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                finish();
            }
        });
        errorDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menuLogOut) {
            mAuth.signOut();
            updateUI();
            return true;
        } else if (item.getItemId() == R.id.menuDownload) {
            Intent pdfGuida = new Intent(this, PDFViewer.class);
            pdfGuida.putExtra("guideOrPDF", "kek");
            startActivity(pdfGuida);
            overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
        } else if (item.getItemId() == R.id.addSomm) {
            Intent intentAddSomm = new Intent(this, GestioneSomministratore.class);
            startActivity(intentAddSomm);
            overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateUI() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null){
            Intent intentLogout = new Intent(this, LoginSomministratore.class);
            startActivity(intentLogout);
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
            finish();
            }
        }


    @SuppressLint("StaticFieldLeak")
    public class DownloadProgettiTask extends AsyncTask<Void, Void, Void> {
        public void onPreExecute() {
            progressDialog.show();
        }
        public Void doInBackground(Void... unused) {
            return null;
        }
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), HomePage.class));
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        finish();
    }

}

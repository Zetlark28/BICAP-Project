package it.unimib.bicap;

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

import it.unimib.bicap.constanti.ActivityConstants;
import it.unimib.bicap.databinding.ActivityHomepageSomministratoreBinding;

// TODO: Creare il menu a discesa coi vari pulsanti tra cui il LOGOUT/ Gestire il login
// TODO: user & psw -> admin admin

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

    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() == null)
            mAuth.signInWithEmailAndPassword(ActivityConstants.AUTHORIZED_EMAIL, ActivityConstants.AUTHORIZED_PASSWORD);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Log.d(TAG, "current user: " + currentUser.getEmail());
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mAuth = FirebaseAuth.getInstance();
         if(mAuth == null)
             Log.d(TAG, "mauth è null");

        binding = ActivityHomepageSomministratoreBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //TODO : metodi mancanti
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
                    json = new String(bytes, "UTF-8");
                    Log.d("kek", json.toString());
                    progetti = new JSONObject(json);
                    SharedPreferences sharedPref = getSharedPreferences(ActivityConstants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
//                    editor.putString("file",progetti.toString());
//                    editor.commit();
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
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                progressDialog.dismiss();
                cancelTimer();
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean fromHome = false;
                Intent intentLogProf = new Intent(getApplicationContext(), LoginProfessore.class);
                intentLogProf.putExtra(ActivityConstants.INTENT_FROM_HOME, fromHome);
                startActivity(intentLogProf);
                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                finish();
            }
        });

        binding.btnElimina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentEliminaProgetto = new Intent (getApplicationContext(), EliminaProgetti.class);
                intentEliminaProgetto.putExtra(ActivityConstants.INTENT_LISTA_PROGETTI_AUTORE, progettiAutore.toString());
                intentEliminaProgetto.putExtra(ActivityConstants.INTENT_LISTA_PROGETTI, progetti.toString());
                intentEliminaProgetto.putExtra(ActivityConstants.INTENT_RETURN, false);
                startActivity(intentEliminaProgetto);
                overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
            }
        });


        // TODO: Progress bar al download dei progetti
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

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        MenuItem item = menu.findItem(R.id.addSomm);
        if (! email.equals(ActivityConstants.AUTHORIZED_EMAIL))
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

        } else if (item.getItemId() == R.id.addSomm) {
            String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            Intent intentAddSomm = new Intent(this, GestioneSomministratore.class);
            startActivity(intentAddSomm);
            overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    //TODO : cambiare logica
    private void updateUI() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        boolean fromHome = false;

        if (currentUser == null){
            Intent intentLogout = new Intent(this, LoginProfessore.class);
            intentLogout.putExtra(ActivityConstants.INTENT_FROM_HOME, fromHome);
            if(fromHome == false){
                startActivity(intentLogout);
                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                finish();
            }else{
                startActivity(intentLogout);
            }
        }
    }

    public class DownloadProgettiTask extends AsyncTask<Void, Void, Void> {
        public void onPreExecute() {
            progressDialog.show();
        }
        public Void doInBackground(Void... unused) {
            return null;
        }
    }

    //controllo su navigationBar
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), HomePage.class));
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        finish();
    }

}

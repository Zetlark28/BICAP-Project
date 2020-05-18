package it.unimib.bicap;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import java.util.Objects;

import it.unimib.bicap.databinding.ActivityCreazioneProgettoBinding;

import it.unimib.bicap.service.JsonBuilder;

public class CreazioneProgetto extends AppCompatActivity {

    private static final String TAG = "CreazioneProgetto";
    private ActivityCreazioneProgettoBinding binding;
    private static JsonBuilder jsonBuilder = JsonBuilder.getJsonBuilder();

    @SuppressLint("SourceLockedOrientationActivity")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        binding = ActivityCreazioneProgettoBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        toolbar.setTitle("Creazione progetto");
        //TODO: inserire titolo questionario tramite metodo get
        setSupportActionBar(toolbar);

        //aggiunto metodo navigazione toolbar - elena
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentHomeProfessore = new Intent(getApplicationContext(), HomePageSomministratore.class);
                startActivity(intentHomeProfessore);
                finish();
            }
        });

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);

        binding.imInsertData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nomeProgetto = binding.etNomeProgetto.getText().toString();
                String autoreProgetto = binding.etAutore.getText().toString();
                String descrizioneProgetto = binding.etDescrizione.getText().toString();

                if (nomeProgetto.equals("")){
                    Snackbar.make(v, "Attenziona, manca il nome del progetto !", Snackbar.LENGTH_SHORT).show();
                }
                else if (autoreProgetto.equals("")){
                    Snackbar.make(v, "Attenzione, manca l'autore del progetto !", Snackbar.LENGTH_SHORT).show();
                }
                else if (descrizioneProgetto.equals("")){
                    Snackbar.make(v, "Attenzione, manca la descrizione del progetto !", Snackbar.LENGTH_SHORT).show();
                }
                else{
                    JSONObject progetto = jsonBuilder.creaProgetto(nomeProgetto,descrizioneProgetto,autoreProgetto);
                    Intent intentDettaglioProgetto = new Intent(getApplicationContext(), DettaglioQuestionario.class);
                    Log.d(TAG, progetto.toString());
                    intentDettaglioProgetto.putExtra("progetto", progetto.toString());
                    startActivity(intentDettaglioProgetto);
                }
            }
        });
    }

    //override startActivity
    @Override
    public void startActivity(Intent intent){
        super.startActivity(intent);
        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
    }

    //override finish con animazione slide indietro
    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }

}

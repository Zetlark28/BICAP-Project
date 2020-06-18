package it.unimib.bicap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import java.util.Objects;

import it.unimib.bicap.constanti.ActivityConstants;
import it.unimib.bicap.databinding.ActivityCreazioneProgettoBinding;

import it.unimib.bicap.service.JsonBuilder;

public class CreazioneProgetto extends AppCompatActivity {

    private static final String TAG = "CreazioneProgetto";
    private ActivityCreazioneProgettoBinding binding;
    private static JsonBuilder jsonBuilder = JsonBuilder.getJsonBuilder();
    private String progettiJSON;
    @SuppressLint({"SourceLockedOrientationActivity", "ClickableViewAccessibility"})
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        progettiJSON = getIntent().getStringExtra(ActivityConstants.INTENT_LISTA_PROGETTI);
        binding = ActivityCreazioneProgettoBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        toolbar.setTitle(ActivityConstants.CREAZIONE_PROGETTO_TOOLBAR_TITLE);
        //TODO: inserire titolo questionario tramite metodo get
        setSupportActionBar(toolbar);

        final String autore = getSharedPreferences(ActivityConstants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE).getString(ActivityConstants.SHARED_PREFERENCE_AUTORE_KEY, null);

        Log.d(TAG, "autore: " + autore);

        binding.tvAutoreFinale.setText(autore);

        //aggiunto metodo navigazione toolbar - elena
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentHomeProfessore = new Intent(getApplicationContext(), HomePageSomministratore.class);
                startActivity(intentHomeProfessore);
                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                finish();
            }
        });

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);

        binding.etDescrizione.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (v.getId() == R.id.etDescrizione) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_UP:
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                }
                return false;
            }
        });

        binding.imInsertData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nomeProgetto = binding.etNomeProgetto.getText().toString();
                //String autoreProgetto = binding.etAutore.getText().toString();
                String descrizioneProgetto = binding.etDescrizione.getText().toString();

                if (nomeProgetto.equals("")){
                    Snackbar.make(v, "Attenziona, manca il nome del progetto!", Snackbar.LENGTH_SHORT).show();
                }
                else if (nomeProgetto.length()>31)
                    Snackbar.make(v, "Attenziona, non puoi inserire un titolo più lungo di 30 caratteri!", Snackbar.LENGTH_SHORT).show();
                //else if (autoreProgetto.equals("")){
                    //Snackbar.make(v, "Attenzione, manca l'autore del progetto !", Snackbar.LENGTH_SHORT).show();
                //}
                else if (descrizioneProgetto.equals("")){
                    Snackbar.make(v, "Attenzione, manca la descrizione del progetto!", Snackbar.LENGTH_SHORT).show();
                }
                else if (descrizioneProgetto.length()>141){
                    Snackbar.make(v, "Attenzione, non puoi inserire una descrizione più lunga di 140 caratteri!", Snackbar.LENGTH_SHORT).show();
                }
                else{
                    JSONObject progetto = jsonBuilder.creaProgetto(nomeProgetto,descrizioneProgetto,autore);
                    Intent intentDettaglioProgetto = new Intent(getApplicationContext(), DettaglioQuestionario.class);
                    Log.d(TAG, progetto.toString());
                    intentDettaglioProgetto.putExtra(ActivityConstants.INTENT_PROGETTO, progetto.toString());
                    intentDettaglioProgetto.putExtra(ActivityConstants.INTENT_LISTA_PROGETTI, progettiJSON);
                    intentDettaglioProgetto.putExtra(ActivityConstants.INTENT_NOME_PROGETTO, nomeProgetto);
                    startActivity(intentDettaglioProgetto);
                    overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                    finish();
                }
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), HomePageSomministratore.class));
        finish();

    }
}

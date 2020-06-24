package it.unimib.bicap.activity.somministratore;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import it.unimib.bicap.R;
import it.unimib.bicap.constanti.ActivityConstants;
import it.unimib.bicap.databinding.ActivityCreazioneProgettoBinding;
import it.unimib.bicap.exception.CreazioneProgettoException;
import it.unimib.bicap.service.JsonBuilder;

public class CreazioneProgetto extends AppCompatActivity {

    private ActivityCreazioneProgettoBinding binding;
    private static JsonBuilder jsonBuilder = JsonBuilder.getJsonBuilder();
    private String progettiJSON;
    @SuppressLint({"SourceLockedOrientationActivity", "ClickableViewAccessibility"})
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
        setSupportActionBar(toolbar);

        final String autore = getSharedPreferences(ActivityConstants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE).getString(ActivityConstants.SHARED_PREFERENCE_AUTORE_KEY, null);

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

        if(getSupportActionBar()==null){
            throw CreazioneProgettoException.CREAZIONE_PROGETTO_VIEW_FAIL;
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        binding.etDescrizione.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (v.getId() == R.id.etDescrizione) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                    }
                }
                return false;
            }
        });

        binding.imInsertData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(binding.etNomeProgetto.getText() == null || binding.etDescrizione.getText() == null){
                    throw CreazioneProgettoException.CREAZIONE_PROGETTO_VIEW_FAIL;
                }
                String nomeProgetto = binding.etNomeProgetto.getText().toString();
                String descrizioneProgetto = binding.etDescrizione.getText().toString();

                if (nomeProgetto.equals("")){
                    Snackbar.make(v, "Attenzione, manca il nome del progetto!", Snackbar.LENGTH_SHORT).show();
                }
                else if (nomeProgetto.length()>31)
                    Snackbar.make(v, "Attenzione, non puoi inserire un titolo più lungo di 30 caratteri!", Snackbar.LENGTH_SHORT).show();
                else if (descrizioneProgetto.equals("")){
                    Snackbar.make(v, "Attenzione, manca la descrizione del progetto!", Snackbar.LENGTH_SHORT).show();
                }
                else if (descrizioneProgetto.length()>141){
                    Snackbar.make(v, "Attenzione, non puoi inserire una descrizione più lunga di 140 caratteri!", Snackbar.LENGTH_SHORT).show();
                }
                else{
                    JSONObject progetto = jsonBuilder.creaProgetto(nomeProgetto,descrizioneProgetto,autore);
                    Intent intentDettaglioProgetto = new Intent(getApplicationContext(), DettaglioQuestionario.class);
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

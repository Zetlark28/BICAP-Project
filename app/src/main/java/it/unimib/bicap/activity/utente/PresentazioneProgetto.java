package it.unimib.bicap.activity.utente;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.unimib.bicap.R;
import it.unimib.bicap.constanti.ActivityConstants;
import it.unimib.bicap.databinding.ActivityPresentazioneProgettoBinding;
import it.unimib.bicap.service.GetterInfo;
import it.unimib.bicap.service.GetterLocal;

public class PresentazioneProgetto extends AppCompatActivity {
    private static final String TAG = "PresentazioneProgetto";
    private ActivityPresentazioneProgettoBinding binding;
    GetterInfo getterInfo = new GetterLocal();

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPresentazioneProgettoBinding.inflate(getLayoutInflater());
        View v = binding.getRoot();
        setContentView(v);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        JSONObject obj = null;
        try {
            obj = new JSONObject(getIntent().getStringExtra(ActivityConstants.INTENT_PROGETTO));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        final String nomeProgetto = getterInfo.getNomeProgetto(obj);
        Toolbar toolbar = findViewById(R.id.toolbar_main);
        toolbar.setTitle(nomeProgetto);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentQuestionari = new Intent(getApplicationContext(), ListaProgetti.class);
                startActivity(intentQuestionari);
                finish();
                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
            }
        });

        init(obj);

        final JSONObject finalObj = obj;

        final int id = getterInfo.getIdProgetto(finalObj);
        final JSONArray passi = getterInfo.getPassi(finalObj);
        Log.d(TAG, "passi: " + passi.toString());

        final String modalita = getIntent().getStringExtra(ActivityConstants.INTENT_MODALITA);

        binding.btnStartProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentIntermediate = new Intent(getApplicationContext(), Intermediate.class);
                intentIntermediate.putExtra(ActivityConstants.INTENT_LISTA_PASSI, passi.toString());
                intentIntermediate.putExtra(ActivityConstants.INTENT_ID_PROGETTO, String.valueOf(id));
                intentIntermediate.putExtra(ActivityConstants.INTENT_NOME_PROGETTO, nomeProgetto);
                intentIntermediate.putExtra(ActivityConstants.INTENT_MODALITA, modalita);
                startActivity(intentIntermediate);
            }
        });
    }

    private void init(JSONObject obj){
        binding.tvNomeProgettoFinale.setText(getterInfo.getNomeProgetto(obj));
        binding.tvAutore.setText(getterInfo.getAutore(obj));
        binding.tvNumeroQuestionariFinale.setText(Integer.toString(getterInfo.getNPassi(getterInfo.getPassi(obj))));
        binding.tvDescrizioneFinale.setText(getterInfo.getDescrizione(obj));
    }

    @Override
    public void startActivity(Intent intent){
        super.startActivity(intent);
        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
    }

    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), ListaProgetti.class));
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        finish();

    }
}

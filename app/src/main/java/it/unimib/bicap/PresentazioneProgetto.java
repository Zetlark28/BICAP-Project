package it.unimib.bicap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import it.unimib.bicap.databinding.ActivityPresentazioneProgettoBinding;
import it.unimib.bicap.service.GetterInfo;
import it.unimib.bicap.service.GetterLocal;

public class PresentazioneProgetto extends AppCompatActivity {

    private ActivityPresentazioneProgettoBinding binding;
    GetterInfo getterInfo = new GetterLocal();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPresentazioneProgettoBinding.inflate(getLayoutInflater());
        View v = binding.getRoot();
        setContentView(v);

        //JSONObject obj = (JSONObject) getIntent().getSerializableExtra("obj");
        JSONObject obj = null;
        try {
            obj = new JSONObject(getIntent().getStringExtra("obj"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        toolbar.setTitle(getterInfo.getNomeProgetto(obj));
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
        binding.btnStartProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject passo = getterInfo.getPasso(getterInfo.getPassi(finalObj), 0);
                String tipo = "";
                try {
                    tipo = getterInfo.getTipo(passo);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Snackbar.make(v, "Tipo: " + tipo, Snackbar.LENGTH_SHORT).show();
                if (tipo.equals("video")){

                } else if (tipo.equals("pdf")){

                } else if (tipo.equals("quiz")){

                }
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
}

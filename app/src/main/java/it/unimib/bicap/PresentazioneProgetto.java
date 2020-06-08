package it.unimib.bicap;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONException;
import org.json.JSONObject;

import it.unimib.bicap.databinding.ActivityPresentazioneProgettoBinding;
import it.unimib.bicap.service.GetterInfo;
import it.unimib.bicap.service.GetterLocal;
import it.unimib.bicap.service.Intermediate;
import it.unimib.bicap.domain.Progetto;

public class PresentazioneProgetto extends AppCompatActivity {
    private static final String TAG = "PresentazioneProgetto";
    private ActivityPresentazioneProgettoBinding binding;
    private Progetto progetto;
    GetterInfo getterInfo = new GetterLocal();

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPresentazioneProgettoBinding.inflate(getLayoutInflater());
        View v = binding.getRoot();
        setContentView(v);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //JSONObject obj = (JSONObject) getIntent().getSerializableExtra("obj");
        JSONObject obj = null;
        try {
            obj = new JSONObject(getIntent().getStringExtra("obj"));
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
        binding.btnStartProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentIntermediate = new Intent(getApplicationContext(), Intermediate.class);
                intentIntermediate.putExtra("Progetto", finalObj.toString());
                startActivity(intentIntermediate);



                /*JSONObject passo = getterInfo.getPasso(getterInfo.getPassi(finalObj), 0);
                Log.d(TAG, "passo: " + passo.toString());
                String tipo = "";
                String tipo1 = "";
                try {
                    tipo = getterInfo.getTipo(passo);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Snackbar.make(v, "Tipo: " + tipo, Snackbar.LENGTH_SHORT).show();

                tipo = "pdf";

                if (tipo.equals("video")){



                    // TODO: Qui sotto ci andrà il link parsato del video
                    *//*Intent intentVideo = new Intent(getApplicationContext(), ExoPlayerStream.class);
                    intentVideo.putExtra("linkVideo", "https://firebasestorage.googleapis.com/v0/b/videoupload-c8474.appspot.com/o/Video%2Fvideoplayback.mp4?alt=media&token=89437c18-758c-4482-9fe3-23698d3c277f");
                    startActivity(intentVideo);*//*

                } else if (tipo.equals("pdf")){

                    // TODO: Qui sotto ci andrà il link parsato del PDF
                    boolean finito = Utility.downloadPDF("https://firebasestorage.googleapis.com/v0/b/bicap-ffecb.appspot.com/o/Documenti%2FFile-6?alt=media&token=12840198-bfd8-4fa2-aa4b-0ab871ba0bb3");
                    while (!finito){
                    }
                   // TODO: Dopo aver scaricato il PDF si può aprirlo in PDFViewer
                    Intent intentPDF = new Intent(getApplicationContext(), PDFViewer.class);
                    intentPDF.putExtra("guideOrPDF", "PDF");
                    intentPDF.putExtra("NomeProgetto", nomeProgetto);
                    startActivity(intentPDF);


                } else if (tipo.equals("questionario")){
                    // TODO: Aggiungere il reindirizzamento all'activity web view
                    tipo1 = getterInfo.getLink(passo);
                    Intent intentWeb = new Intent(getApplicationContext(), Survey.class);
                    intentWeb.putExtra("web", tipo1);
                    startActivity(intentWeb);
                    finish();
                }*/
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

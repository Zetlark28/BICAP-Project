package it.unimib.bicap.service;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import it.unimib.bicap.service.GetterInfo;
import it.unimib.bicap.service.GetterLocal;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import it.unimib.bicap.Survey;
import it.unimib.bicap.databinding.ActivityDettaglioQuestionarioBinding;
import it.unimib.bicap.databinding.ActivityIntermediateBinding;

public class Intermediate extends AppCompatActivity {

    private static final String TAG = "Intermediate";
    private ActivityIntermediateBinding binding;
    private JSONObject finalObj;
    GetterInfo getterInfo = new GetterLocal();

    @SuppressLint("SourceLockedOrientationActivity")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityIntermediateBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        String progetto = getIntent().getStringExtra("Progetto");

        try {
            finalObj = new JSONObject(progetto);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        binding.tvDettaglioPasso.setText("Descrizione passo");
        final String nomeProgetto = getterInfo.getNomeProgetto(finalObj);
        binding.btnAvanti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject passo = getterInfo.getPasso(getterInfo.getPassi(finalObj), 0);
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
                    /*Intent intentVideo = new Intent(getApplicationContext(), ExoPlayerStream.class);
                    intentVideo.putExtra("linkVideo", "https://firebasestorage.googleapis.com/v0/b/videoupload-c8474.appspot.com/o/Video%2Fvideoplayback.mp4?alt=media&token=89437c18-758c-4482-9fe3-23698d3c277f");
                    startActivity(intentVideo);*/

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
                }
            }
        });
    }
}

package it.unimib.bicap.service;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.unimib.bicap.Survey;
import it.unimib.bicap.databinding.ActivityIntermediateBinding;
import it.unimib.bicap.db.DBManager;

public class Intermediate extends AppCompatActivity {

    private static final String TAG = "Intermediate";
    private ActivityIntermediateBinding binding;
    private JSONArray arrayPassi;
    GetterInfo getterInfo = new GetterLocal();
    DBManager dbManager;

    @SuppressLint("SourceLockedOrientationActivity")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = ActivityIntermediateBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        String passi = getIntent().getStringExtra("Passi");
        final String nomeProgetto = getIntent().getStringExtra("NomeProgetto");
        final String idProgetto = getIntent().getStringExtra("Id");

        try {
            arrayPassi = new JSONArray(passi);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final JSONObject passo = getterInfo.getPasso(arrayPassi, 0);
        Log.d(TAG, "passo: " + passo.toString());
        String tipo = "";
         String link = "";
        try {
             link = getterInfo.getLink(passo);
             tipo = getterInfo.getTipo(passo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        tipo = "video";

        //Snackbar.make(v, "Tipo: " + tipo, Snackbar.LENGTH_SHORT).show();

        dbManager = new DBManager(getApplicationContext());
        dbManager.saveDaCompletare(Integer.parseInt(idProgetto));
        if (tipo.equals("video")) {
            binding.tvTitolo.setText("Stai per visualizzare un video");
            binding.tvDettaglioPasso.setText("In questo passo stai per visualizzare un contenuto video.\n" +
                                             "Ricordati che, una volta finito di visionare il video, verrai subito reindirizzato al prossimo passo quindi guarda il video con attenzione.\"");
        } else if (tipo.equals("pdf")) {
            binding.tvTitolo.setText("Stai per visualizzare un PDF");
            binding.tvDettaglioPasso.setText("In questo passo stai per visualizzare un contenuto PDF.\n" +
                                             "Ricordati che, una volta finito di visionare il PDF, dovrai premere sul pulsante 'avanti' in alto a destra della toolbar\"");
        } else if (tipo.equals("questionario")) {
            binding.tvTitolo.setText("Stai per svolgere un questionario");
            binding.tvDettaglioPasso.setText("In questo passo stai per rispondere al questionario.\n" +
                                             "Ricordati che, se non terminerai il questionario quest'ultimo verrà inserito nella sezione 'survey sospesi'.\n" +
                                             "Negli altri casi invece, non potrai più rispondere alle domande quindi, prima di completarlo pensaci bene.\"");
        }

        final String finalLink = link;
        final String finalTipo = tipo;
        binding.btnAvanti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (finalTipo.equals("video")){

                    // TODO: Qui sotto ci andrà il link parsato del video
                    Intent intentVideo = new Intent(getApplicationContext(), ExoPlayerStream.class);
                    intentVideo.putExtra("linkVideo", finalLink);
                    startActivity(intentVideo);

                } else if (finalTipo.equals("pdf")){

                    // TODO: Qui sotto ci andrà il link parsato del PDF
                    boolean finito = Utility.downloadPDF(finalLink);
                    while (!finito){
                    }
                    // TODO: Dopo aver scaricato il PDF si può aprirlo in PDFViewer
                    Intent intentPDF = new Intent(getApplicationContext(), PDFViewer.class);
                    intentPDF.putExtra("guideOrPDF", "PDF");
                    intentPDF.putExtra("NomeProgetto", nomeProgetto);
                    startActivity(intentPDF);


                } else if (finalTipo.equals("questionario")){
                    // TODO: Aggiungere il reindirizzamento all'activity web view
                    Intent intentWeb = new Intent(getApplicationContext(), Survey.class);
                    intentWeb.putExtra("web", finalLink);
                    startActivity(intentWeb);
                    finish();
                }
            }
        });
    }
}

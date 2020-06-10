package it.unimib.bicap.service;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
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

import it.unimib.bicap.GrazieScreen;
import it.unimib.bicap.Survey;
import it.unimib.bicap.constanti.DBConstants;
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
        final String passi = getIntent().getStringExtra("listaPassi");
        final String nomeProgetto = getIntent().getStringExtra("NomeProgetto");
        final String idProgetto = getIntent().getStringExtra("idProgetto");

        try {
            arrayPassi = new JSONArray(passi);
        } catch (JSONException e) {
            e.printStackTrace();
        }



        String modalita = getIntent().getStringExtra("mode");
        final JSONObject passo;
        String tipo = "";
        String link = "";
        Integer nPasso=0;

        dbManager = new DBManager(getApplicationContext());
        if(modalita.equals("daTerminare")){
            Cursor crs = dbManager.selectNPasso(Integer.parseInt(idProgetto));
            int columnIndex = crs.getColumnIndex(DBConstants.FIELD_N_PASSO);
            Log.d("cursor", crs.toString());
            crs.moveToFirst();
            Log.d("cursor2", crs.toString());
            nPasso = crs.getInt(columnIndex);
            try {
                passo = arrayPassi.getJSONObject(nPasso);
                link = getterInfo.getLink(passo);
                tipo = getterInfo.getTipo(passo);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else if(modalita.equals("daFare")){
             dbManager.saveDaCompletare(Integer.parseInt(idProgetto));
            passo = getterInfo.getPasso(arrayPassi, 0);
            Log.d(TAG, "passo: " + passo.toString());
            try {
                link = getterInfo.getLink(passo);
                tipo = getterInfo.getTipo(passo);
                dbManager.saveProgettoPasso(Integer.parseInt(idProgetto),0);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            nPasso = 0;
        }
        if(nPasso >= arrayPassi.length()){
            modalita = "completato";
        }


        //Snackbar.make(v, "Tipo: " + tipo, Snackbar.LENGTH_SHORT).show();
        if(modalita.equals("completato")){
            dbManager.deleteDaCompletare(Integer.parseInt(idProgetto));
            dbManager.saveCompletati(Integer.parseInt(idProgetto));
            Intent intentFine = new Intent(getApplicationContext(), GrazieScreen.class);
            startActivity(intentFine);
        }else if(modalita.equals("Thanos")){
            binding.tvTitolo.setText("Non sei idoneo al progetto");
            binding.tvDettaglioPasso.setText("Mi dispiace non sei idoneo al progetto");
        }else if (tipo.equals("video")) {
            binding.tvTitolo.setText("Stai per visualizzare un video");
            binding.tvDettaglioPasso.setText("In questo passo stai per visualizzare un contenuto video.\n" +
                                             "Ricordati che, una volta finito di visionare il video, verrai automaticamente reindirizzato al passo successivo dopo che il video finisce.\"");
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
        final String modalitaFinal = modalita;
        final Integer finalNPasso = nPasso;
        binding.btnAvanti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(modalitaFinal.equals("Thanos")){

                }else if (finalTipo.equals("video")){

                    // TODO: Qui sotto ci andrà il link parsato del video
                    Intent intentVideo = new Intent(getApplicationContext(), ExoPlayerStream.class);
                    intentVideo.putExtra("linkVideo", finalLink);
                    intentVideo.putExtra("idProgetto",idProgetto);
                    intentVideo.putExtra("nomeProgetto", nomeProgetto);
                    intentVideo.putExtra("listaPassi", passi);
                    intentVideo.putExtra("nPasso", finalNPasso.toString());
                    startActivity(intentVideo);

                } else if (finalTipo.equals("pdf")){

                    // TODO: Controllo fine download pdf
                    boolean finito = Utility.downloadPDF(finalLink);
                    while (!finito){
                    }
                    // TODO: Dopo aver scaricato il PDF si può aprirlo in PDFViewer
                    Intent intentPDF = new Intent(getApplicationContext(), PDFViewer.class);
                    intentPDF.putExtra("guideOrPDF", "PDF");
                    intentPDF.putExtra("NomeProgetto", nomeProgetto);
                    intentPDF.putExtra("idProgetto",idProgetto);
                    intentPDF.putExtra("listaPassi", passi);
                    intentPDF.putExtra("nPasso", finalNPasso.toString());
                    startActivity(intentPDF);


                } else if (finalTipo.equals("questionario")){
                    // TODO: Aggiungere il reindirizzamento all'activity web view
                    Intent intentWeb = new Intent(getApplicationContext(), Survey.class);
                    intentWeb.putExtra("web", finalLink);
                    intentWeb.putExtra("idProgetto",idProgetto);
                    intentWeb.putExtra("listaPassi", passi);
                    intentWeb.putExtra("nPasso", finalNPasso.toString());
                    intentWeb.putExtra("nomeProgetto", nomeProgetto);
                    startActivity(intentWeb);
                    finish();
                }
            }
        });
    }
}

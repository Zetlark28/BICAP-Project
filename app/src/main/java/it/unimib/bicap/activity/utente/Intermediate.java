package it.unimib.bicap.activity.utente;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.unimib.bicap.activity.HomePage;
import it.unimib.bicap.R;
import it.unimib.bicap.constanti.ActivityConstants;
import it.unimib.bicap.constanti.DBConstants;
import it.unimib.bicap.databinding.ActivityIntermediateBinding;
import it.unimib.bicap.db.DBManager;
import it.unimib.bicap.exception.IntermediateException;
import it.unimib.bicap.service.ExoPlayerStream;
import it.unimib.bicap.service.GetterInfo;
import it.unimib.bicap.service.GetterLocal;
import it.unimib.bicap.service.PDFViewer;
import it.unimib.bicap.service.Utility;

public class Intermediate extends AppCompatActivity {

    private static final String TAG = "Intermediate";
    private JSONArray arrayPassi;
    public ProgressDialog progressDialog;
    GetterInfo getterInfo = new GetterLocal();
    DBManager dbManager;

    @SuppressLint("SourceLockedOrientationActivity")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        it.unimib.bicap.databinding.ActivityIntermediateBinding binding = ActivityIntermediateBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        final String passi = getIntent().getStringExtra(ActivityConstants.INTENT_LISTA_PASSI);
        final String nomeProgetto = getIntent().getStringExtra(ActivityConstants.INTENT_NOME_PROGETTO);
        final String idProgetto = getIntent().getStringExtra(ActivityConstants.INTENT_ID_PROGETTO);

        if(passi == null){
            throw IntermediateException.INTERMEDIATE_PASSI_NULL;
        }else if(nomeProgetto == null){
            throw IntermediateException.INTERMEDIATE_NOME_PROGETTO_NULL;
        }else if(idProgetto == null){
            throw IntermediateException.INTERMEDIATE_ID_PROGETTO_NULL;
        }

        try {
            arrayPassi = new JSONArray(passi);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        String modalita = getIntent().getStringExtra(ActivityConstants.INTENT_MODALITA);
        if(modalita == null){
            throw IntermediateException.INTERMEDIATE_MODALITA_NULL;
        }
        final JSONObject passo;
        String tipo = "";
        String link = "";
        int nPasso=0;

        dbManager = new DBManager(getApplicationContext());
        if(modalita.equals("daTerminare")){
            Cursor crs = dbManager.selectNPasso(Integer.parseInt(idProgetto));
            int columnIndex = crs.getColumnIndex(DBConstants.FIELD_N_PASSO);
            crs.moveToFirst();
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
            binding.tvTitolo.setText(R.string.intermediate_tvTitolo_nonIdoneo);
            binding.tvDettaglioPasso.setText(R.string.intermediate_tvDettaglioPasso_nonIdoneo);
            binding.btnAvanti.setText(R.string.intermediate_btnAvanti_nonIdoneo);
        }else if (tipo.equals("video")) {
            binding.tvTitolo.setText(R.string.intermediate_tvTitolo_video);
            binding.tvDettaglioPasso.setText(getString(R.string.intermediate_tvDettaglioPasso_video));
        } else if (tipo.equals("pdf")) {
            Log.d("pdf", "kek");
            binding.tvTitolo.setText(R.string.intermediate_tvTitolo_PDF);
            binding.tvDettaglioPasso.setText(R.string.intermediate_tvDettaglio_PDF);

            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Caricamento...");
            progressDialog.setProgress(10);
            progressDialog.setMax(100);
            progressDialog.setMessage("Attendi!");
            progressDialog.show();
            boolean finito = Utility.downloadPDF(link, progressDialog);

            //progressDialog.dismiss();
        } else if (tipo.equals("questionario")) {
            binding.tvTitolo.setText(R.string.intermediate_tvTitolo_questionario);
            binding.tvDettaglioPasso.setText(getString(R.string.intermediate_tvDettaglioPasso_questionario));
        }

        final String finalLink = link;
        final String finalTipo = tipo;
        final String modalitaFinal = modalita;
        final Integer finalNPasso = nPasso;
        binding.btnAvanti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //TODO: CAMBIARE IL THANOS
                if(modalitaFinal.equals("Thanos")){
                    Intent tornaHomePage = new Intent(getApplicationContext(), HomePage.class);
                    startActivity(tornaHomePage);
                    overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                    finish();
                }else if (finalTipo.equals("video")){

                    Intent intentVideo = new Intent(getApplicationContext(), ExoPlayerStream.class);
                    intentVideo.putExtra(ActivityConstants.INTENT_LINK_VIDEO, finalLink);
                    intentVideo.putExtra(ActivityConstants.INTENT_ID_PROGETTO,idProgetto);
                    intentVideo.putExtra(ActivityConstants.INTENT_NOME_PROGETTO, nomeProgetto);
                    intentVideo.putExtra(ActivityConstants.INTENT_LISTA_PASSI, passi);
                    intentVideo.putExtra(ActivityConstants.INTENT_N_PASSO, finalNPasso.toString());
                    startActivity(intentVideo);
                    overridePendingTransition(R.anim.activity_in, R.anim.activity_out);

                } else if (finalTipo.equals("pdf")){

                    // TODO: Controllo fine download pdf

                    // TODO: Dopo aver scaricato il PDF si può aprirlo in PDFViewer
                    Intent intentPDF = new Intent(getApplicationContext(), PDFViewer.class);
                    intentPDF.putExtra(ActivityConstants.INTENT_GUIDE_OR_PDF, "PDF");
                    intentPDF.putExtra(ActivityConstants.INTENT_NOME_PROGETTO, nomeProgetto);
                    intentPDF.putExtra(ActivityConstants.INTENT_ID_PROGETTO,idProgetto);
                    intentPDF.putExtra(ActivityConstants.INTENT_LISTA_PASSI, passi);
                    intentPDF.putExtra(ActivityConstants.INTENT_N_PASSO, finalNPasso.toString());
                    startActivity(intentPDF);
                    overridePendingTransition(R.anim.activity_in, R.anim.activity_out);

                } else if (finalTipo.equals("questionario")){
                    Intent intentWeb = new Intent(getApplicationContext(), Survey.class);
                    intentWeb.putExtra(ActivityConstants.INTENT_WEB, finalLink);
                    intentWeb.putExtra(ActivityConstants.INTENT_ID_PROGETTO,idProgetto);
                    intentWeb.putExtra(ActivityConstants.INTENT_LISTA_PASSI, passi);
                    intentWeb.putExtra(ActivityConstants.INTENT_N_PASSO, finalNPasso.toString());
                    intentWeb.putExtra(ActivityConstants.INTENT_NOME_PROGETTO, nomeProgetto);
                    startActivity(intentWeb);
                    overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                    finish();
                }
            }
        });
    }
    public void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.intermediate_dialogTitle);
        builder.setMessage(getString(R.string.intermediate_dialogMessage));
        builder.setPositiveButton(R.string.dialog_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent HomePageSomministratoreRicarica = new Intent(getApplicationContext(), HomePage.class);
                startActivity(HomePageSomministratoreRicarica);
                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                finish();
            }
        });
        builder.setNegativeButton(R.string.dialog_negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public void onBackPressed(){
        showDialog();
    }
}

package it.unimib.bicap.activity.utente;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import it.unimib.bicap.activity.HomePage;
import it.unimib.bicap.R;
import it.unimib.bicap.constanti.ActivityConstants;
import it.unimib.bicap.databinding.ActivitySurveyBinding;
import it.unimib.bicap.db.DBManager;

public class Survey extends AppCompatActivity {

    private DBManager dbManager;
    private String nomeProgetto;
    private String idProgetto;
    private String passi;
    private String nPasso;

    @SuppressLint("SetJavaScriptEnabled")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        it.unimib.bicap.databinding.ActivitySurveyBinding binding = ActivitySurveyBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        dbManager = new DBManager(getApplicationContext());
        idProgetto = getIntent().getStringExtra(ActivityConstants.INTENT_ID_PROGETTO);
        passi = getIntent().getStringExtra(ActivityConstants.INTENT_LISTA_PASSI);
        nPasso = getIntent().getStringExtra(ActivityConstants.INTENT_N_PASSO);
        String url = getIntent().getStringExtra(ActivityConstants.INTENT_WEB);

        nomeProgetto = getIntent().getStringExtra(ActivityConstants.INTENT_NOME_PROGETTO);
        Toolbar toolbar = findViewById(R.id.toolbar_main);
        toolbar.setTitle(nomeProgetto);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });


        //"https://psicologiaunimib.eu.qualtrics.com/jfe/form/SV_5mr178vYfm3V3XD"

        binding.surveyWebView.loadUrl(url);
        WebSettings webSettings = binding.surveyWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        binding.surveyWebView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.i("URL", url);
                if (url.contains(ActivityConstants.URL_ERROR)) {


                    dbManager.deleteDaCompletare(Integer.parseInt(idProgetto));
                    dbManager.saveCompletati(Integer.parseInt(idProgetto));
                    Intent intentIntermediate = new Intent(getApplicationContext(), Intermediate.class);
                    intentIntermediate.putExtra(ActivityConstants.INTENT_MODALITA, "notFit");
                    intentIntermediate.putExtra(ActivityConstants.INTENT_ID_PROGETTO, idProgetto);
                    intentIntermediate.putExtra(ActivityConstants.INTENT_LISTA_PASSI, passi);
                    intentIntermediate.putExtra(ActivityConstants.INTENT_NOME_PROGETTO, nomeProgetto);
                    startActivity(intentIntermediate);
                    finish();


                }

                else if (url.contains(ActivityConstants.URL_EXIT)) {
                    dbManager.updatePasso(Integer.parseInt(idProgetto), Integer.parseInt(nPasso)+1);
                    Intent intentIntermediate = new Intent(getApplicationContext(), Intermediate.class);
                    intentIntermediate.putExtra(ActivityConstants.INTENT_MODALITA, "daTerminare");
                    intentIntermediate.putExtra(ActivityConstants.INTENT_ID_PROGETTO, idProgetto);
                    intentIntermediate.putExtra(ActivityConstants.INTENT_LISTA_PASSI, passi);
                    intentIntermediate.putExtra(ActivityConstants.INTENT_NOME_PROGETTO, nomeProgetto);
                    startActivity(intentIntermediate);
                    finish();
                }
                else
                    view.loadUrl(url);
                return false;
            }
        });
    }

    public void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Torna alla Homepage");
        builder.setMessage("Sicuro di voler tornare indietro?\n" + "Questo renderà visibile il questionario nella sezione \"Survey Sospesi\"");
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

    public void onBackPressed()
    {
        showDialog();
    }
}

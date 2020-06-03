package it.unimib.bicap;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.Objects;

import it.unimib.bicap.constanti.ActivityConstants;
import it.unimib.bicap.databinding.ActivitySurveyBinding;

public class Survey extends AppCompatActivity {

    private static final String TAG = "SurveyWebView";
    private ActivitySurveyBinding binding;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySurveyBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        String url = getIntent().getStringExtra("web");

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        toolbar.setTitle("Prova");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });


        //"https://psicologiaunimib.eu.qualtrics.com/jfe/form/SV_5mr178vYfm3V3XD"

        binding.surveyWebView.loadUrl(url); //INSERIRE LINK DEL QUESTIONARIO (quindi fare un metodo che lo infila)
        WebSettings webSettings = binding.surveyWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        binding.surveyWebView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.i("URL", url);
                if (url.contains(ActivityConstants.URL_ERROR)) {
                    Intent nextSurvey = new Intent(getApplicationContext(), LoginProfessore.class);
                    //TODO: sostituire con schermata dove sono presenti i restanti questionari e aggiornare la lista
                    startActivity(nextSurvey);
                    finish(); //METTERE INTENT PER ANDARE ALLA SCHERMATA CHE FOTTE A ME
                }
                else if (url.contains(ActivityConstants.URL_EXIT)) {
                    Intent exitError = new Intent(getApplicationContext(), LoginProfessore.class);
                    //TODO: sostituire con schermata dove sono presenti tutti i progetti
                    startActivity(exitError);
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
        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent HomePageSomministratoreRicarica = new Intent(getApplicationContext(), HomePage.class);
                startActivity(HomePageSomministratoreRicarica);
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}

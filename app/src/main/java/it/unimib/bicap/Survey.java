package it.unimib.bicap;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.Objects;

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

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        toolbar.setTitle("Prova");
        //TODO: inserire metodo get titolo from JSON File
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);

        binding.surveyWebView.loadUrl("https://psicologiaunimib.eu.qualtrics.com/jfe/form/SV_5mr178vYfm3V3XD"); //INSERIRE LINK DEL QUESTIONARIO (quindi fare un metodo che lo infila)
        WebSettings webSettings = binding.surveyWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        binding.surveyWebView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.i("URL", url);
                if (url.contains("google")) {
                    Intent nextSurvey = new Intent(getApplicationContext(), LoginProfessore.class);
                    //TODO: sostituire con schermata dove sono presenti i restanti questionari e aggiornare la lista
                    startActivity(nextSurvey);
                    finish(); //METTERE INTENT PER ANDARE ALLA SCHERMATA CHE FOTTE A ME
                }
                else if (url.contains("facebook")) {
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
}

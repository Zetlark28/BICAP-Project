package it.unimib.bicap.service;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.github.barteksc.pdfviewer.PDFView;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.FileInputStream;

import it.unimib.bicap.GestioneSomministratore;
import it.unimib.bicap.HomePage;
import it.unimib.bicap.R;
import it.unimib.bicap.databinding.ActivityPdfViewerBinding;

// TODO: creare if-else per capire se sto aprendo la guida o un file da firebase
public class PDFViewer extends AppCompatActivity {

    private final static String PDF_UNIQUE_PATH = "/data/data/it.unimib.bicap/cache/PDF.pdf";
    private String guideOrPDF;
    private PDFView pdfView;
    private String nomeProgetto;
    private Toolbar mTopToolbar;
    private ActivityPdfViewerBinding binding;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPdfViewerBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        mTopToolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(mTopToolbar);

        pdfView = binding.pdfView;
        guideOrPDF = getIntent().getStringExtra("guideOrPDF");
        nomeProgetto = getIntent().getStringExtra("NomeProgetto");
        Toolbar toolbar = findViewById(R.id.toolbar_main);
        toolbar.setTitle(nomeProgetto);
        setSupportActionBar(toolbar);
        // TODO: passo qui una stringa, se è PDF vuol dire che devo aprire il documento PDF con link, viceversa apro la guida
        if (guideOrPDF.equals("PDF")) {
            openPDF();
        } else {
            openGuida();
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intendHomePage = new Intent(getApplicationContext(), HomePage.class);
                startActivity(intendHomePage);
                finish();
            }
        });
        
    }


    private void openGuida(){
        try {
            pdfView.fromAsset("Guida.pdf")
                    .enableSwipe(true)
                    .swipeHorizontal(false)
                    .enableAntialiasing(true)
                    .spacing(0)
                    .load();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openPDF(){
        try {
            FileInputStream fis = new FileInputStream (new File(PDF_UNIQUE_PATH));
            pdfView.fromStream(fis)
                    .enableSwipe(true)
                    .swipeHorizontal(false)
                    .enableAntialiasing(true)
                    .spacing(0)
                    .load();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), HomePage.class));
        finish();

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_favorite) {
            //Aggiungere Intent
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}

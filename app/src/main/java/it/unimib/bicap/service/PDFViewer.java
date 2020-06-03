package it.unimib.bicap.service;
import android.content.Intent;
import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;
import com.github.barteksc.pdfviewer.PDFView;
import java.io.File;
import java.io.FileInputStream;

import it.unimib.bicap.HomePage;
import it.unimib.bicap.R;

// TODO: creare if-else per capire se sto aprendo la guida o un file da firebase
public class PDFViewer extends AppCompatActivity {

    private final static String PDF_UNIQUE_PATH = "/data/data/it.unimib.bicap/cache/PDF.pdf";
    private String guideOrPDF;
    private PDFView pdfView;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);

        pdfView = findViewById(R.id.pdfView);
        guideOrPDF = getIntent().getStringExtra("guideOrPDF");
        // TODO: passo qui una stringa, se è PDF vuol dire che devo aprire il documento PDF con link, viceversa apro la guida
        if (guideOrPDF.equals("PDF")) {
            openPDF(PDF_UNIQUE_PATH);
        } else {
            openGuida();
        }
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

    private void openPDF(String path){
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
}

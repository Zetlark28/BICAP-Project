package it.unimib.bicap.service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.DialogInterface;

import com.github.barteksc.pdfviewer.PDFView;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;

import java.io.File;
import java.io.FileInputStream;

import it.unimib.bicap.GestioneSomministratore;
import it.unimib.bicap.HomePage;
import it.unimib.bicap.R;
import it.unimib.bicap.databinding.ActivityPdfViewerBinding;
import it.unimib.bicap.db.DBManager;

// TODO: creare if-else per capire se sto aprendo la guida o un file da firebase
public class PDFViewer extends AppCompatActivity {

    private final static String PDF_UNIQUE_PATH = "/data/data/it.unimib.bicap/cache/PDF.pdf";
    private String guideOrPDF;
    private DBManager dbManager;
    private PDFView pdfView;
    private Toolbar mTopToolbar;
    private String nomeProgetto;
    private String idProgetto;
    private String passi;
    private String nPasso;
    private ActivityPdfViewerBinding binding;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbManager = new DBManager(getApplicationContext());
        binding = ActivityPdfViewerBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        showDialogCaricamento(this, "ciao","mamma", true);

        mTopToolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(mTopToolbar);

        pdfView = binding.pdfView;
        guideOrPDF = getIntent().getStringExtra("guideOrPDF");
        idProgetto = getIntent().getStringExtra("idProgetto");
        passi = getIntent().getStringExtra("listaPassi");
        nomeProgetto = getIntent().getStringExtra("NomeProgetto");
        nPasso = getIntent().getStringExtra("nPasso");
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
                showDialog();
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


    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_favorite) {
            //TODO: Aggiornare DataBase

            dbManager.updatePasso(Integer.parseInt(idProgetto), Integer.parseInt(nPasso)+1);
            Intent intentIntermediate = new Intent(getApplicationContext(), Intermediate.class);
            intentIntermediate.putExtra("mode", "daTerminare");
            intentIntermediate.putExtra("idProgetto", idProgetto);
            intentIntermediate.putExtra("listaPassi", passi);
            intentIntermediate.putExtra("NomeProgetto", nomeProgetto);
            startActivity(intentIntermediate);
            overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
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

    public void onBackPressed(){
        showDialog();
    }

    public void showDialogCaricamento(final Context context, String title, String message,
                                Boolean status) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Caricamento in corso")
                .setMessage("Attendere...")
                .create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            private static final int AUTO_DISMISS_MILLIS = 3000;
            @Override
            public void onShow(final DialogInterface dialog) {
                new CountDownTimer(AUTO_DISMISS_MILLIS, 100) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }
                    @Override
                    public void onFinish() {
                        if (((AlertDialog) dialog).isShowing()) {
                            dialog.dismiss();
                        }
                    }
                }.start();
            }
        });
        dialog.show();
    }


}

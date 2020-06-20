package it.unimib.bicap.activity.somministratore;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.unimib.bicap.activity.HomePageSomministratore;
import it.unimib.bicap.R;
import it.unimib.bicap.constanti.ActivityConstants;
import it.unimib.bicap.databinding.ActivityDettaglioQuestionarioBinding;
import it.unimib.bicap.service.JsonBuilder;
import it.unimib.bicap.service.Utility;

// TODO: (Arthur) quando il somministratore clicca su Salva Progetto ma la variabile path contiene qualcosa o la text ha un link si deve chiedere al somministratore la conferma
// TODO: La conferma dev'essere chiesta in generale anche
// TODO: Aggiungere tanti ma tantissimi controlli

public class DettaglioQuestionario extends AppCompatActivity {

    private static final int CODE_VIDEO = 1;
    private static final int CODE_PDF = 2;
    private Uri filePath;
    private String type;
    private static String linkToJoinJSON;
    private static JsonBuilder jsonBuilder = JsonBuilder.getJsonBuilder();
    private JSONObject progetto = new JSONObject();
    private JSONArray listaPassi = new JSONArray();
    private String progettiJSON;
    private DettaglioQuestionario instance;
    private boolean firstTime;
    private ActivityDettaglioQuestionarioBinding binding;

    public static void setLinkToJoinJSON(String linkToJoinJSON) {
        DettaglioQuestionario.linkToJoinJSON = linkToJoinJSON;
    }

    @SuppressLint("SourceLockedOrientationActivity")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        firstTime = true;
        Utility.getKeyValue();
        FirebaseApp.initializeApp(this);
        progettiJSON = getIntent().getStringExtra(ActivityConstants.INTENT_LISTA_PROGETTI);
        String nomeProgetto = getIntent().getStringExtra(ActivityConstants.INTENT_NOME_PROGETTO);
        binding = ActivityDettaglioQuestionarioBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        instance = this;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        toolbar.setTitle(nomeProgetto);


        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.menu_cancella);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        toolbar.setTitle(getIntent().getStringExtra(ActivityConstants.INTENT_NOME_PROGETTO));

        final String progettoString = getIntent().getStringExtra(ActivityConstants.INTENT_PROGETTO);
        try {
            assert progettoString != null;
            progetto = new JSONObject(progettoString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("OGGETTO JSON", progetto.toString());


        //TODO : settare il colore dei bottoni disabilitati
        binding.btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String key = Utility.setKeyValue();
                if (type != null) {

                    binding.imSaveProject.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    binding.imNextStep.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    binding.imCaricaVideo.setBackgroundColor(getResources().getColor(R.color.disabilita));
                    binding.imInsertPdf.setBackgroundColor(getResources().getColor(R.color.disabilita));
                    binding.btnAnnulla.setBackgroundColor(getResources().getColor(R.color.disabilita));


                    binding.imSaveProject.setClickable(true);
                    binding.imNextStep.setClickable(true);
                    binding.imCaricaVideo.setClickable(false);
                    binding.imInsertPdf.setClickable(false);
                    binding.btnAnnulla.setClickable(false);

                    if (type.equals("Video")) {
                        Utility.uploadFile(filePath,"Video/" + key,instance,binding);
                        Log.d("oggetto", progetto.toString() + 2);
                        Snackbar.make(v, "Hai inserito un video", Snackbar.LENGTH_SHORT).show();
                    } else if (type.equals("PDF")) {
                        Utility.uploadFile(filePath,"Documenti/" + key,instance,binding);
                        Snackbar.make(v, "Hai inserito un PDF", Snackbar.LENGTH_SHORT).show();
                    }

                } else {

                    binding.imCaricaVideo.setBackgroundColor(Color.WHITE);
                    binding.imInsertPdf.setBackgroundColor(Color.WHITE);
                    binding.imNextStep.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    binding.imSaveProject.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    binding.btnAnnulla.setBackgroundColor(Color.WHITE);

                    binding.imInsertPdf.setClickable(true);
                    binding.imCaricaVideo.setClickable(true);
                    binding.imNextStep.setClickable(true);
                    binding.imSaveProject.setClickable(true);
                    binding.etLink.setEnabled(true);
                    binding.btnAnnulla.setClickable(true);

                    Snackbar.make(v, "Attenzione, non hai selezionato alcun file", Snackbar.LENGTH_SHORT).show();
                }

                //TODO: disabilitare la textInput per inserire il link del questionario e i bottoni upload, magari inserire bottone annulla upload
            }
        });


        binding.imNextStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((filePath == null) && binding.etLink.getText().toString().equals("")) {
                    Snackbar.make(v, "Attenzione, non puoi passare al passo successivo senza aver inserito del contenuto! \nAggiungi qualcosa e riprova!", Snackbar.LENGTH_LONG).show();
                } else {
                    binding.imInsertPdf.setBackgroundColor(Color.WHITE);
                    binding.imSaveProject.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    binding.imCaricaVideo.setBackgroundColor(Color.WHITE);
                    binding.imNextStep.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    binding.btnAnnulla.setBackgroundColor(Color.WHITE);

                    binding.imInsertPdf.setClickable(true);
                    binding.imSaveProject.setClickable(true);
                    binding.imCaricaVideo.setClickable(true);
                    binding.imNextStep.setClickable(true);
                    binding.etLink.setEnabled(true);
                    binding.btnAnnulla.setClickable(true);

                    Utility.getKeyValue();
                    //Svolgo il controllo sul fatto che deve essere scelto solo un'opzione tra le tre disponibili
                    aggiungiPassi();
                    filePath = null;
                    type = null;
                    binding.etLink.setText("");
                    binding.pbUpload.setProgress(0);
                    Snackbar.make(v, "Sei passato al passaggio successivo", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        binding.btnAnnulla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.imInsertPdf.setBackgroundColor(Color.WHITE);
                binding.imSaveProject.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                binding.imCaricaVideo.setBackgroundColor(Color.WHITE);
                binding.imNextStep.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                binding.imInsertPdf.setClickable(true);
                binding.imSaveProject.setClickable(true);
                binding.imCaricaVideo.setClickable(true);
                binding.imNextStep.setClickable(true);
                binding.etLink.setEnabled(true);

                filePath = null;
                type=null;
                binding.etLink.setText("");
            }

        });

        binding.imSaveProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!(filePath == null) || !binding.etLink.getText().toString().equals("")) {
                    aggiungiPassi();
                } else if (firstTime) {
                    Snackbar.make(v, "Attenzione, non puoi creare un progetto senza nessun contenuto! \nAggiungi qualcosa e riprova!", Snackbar.LENGTH_LONG).show();
                }
                if(!firstTime) {
                    jsonBuilder.aggiungiListaPassi(progetto, listaPassi);
                    Log.d("oggetto", progetto.toString());
                    try {
                        JSONArray listaProgetti = new JSONArray(progettiJSON);
                        JSONObject progetti = jsonBuilder.aggiungiProgettoInLista(listaProgetti, progetto);
                        progetti.put("progetti", listaProgetti);
                        Log.d("oggetto", progetti.toString());
                        Utility.write(progetti, instance, binding);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Intent congratScreem = new Intent(getApplicationContext(), CongratulazioniScreen.class);
                    startActivity(congratScreem);
                    overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                    finish();
                }
            }
        });

        //TODO : settare il colore dei bottoni disabilitati
        binding.imCaricaVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                binding.imNextStep.setBackgroundColor(getResources().getColor(R.color.disabilita));
                binding.imInsertPdf.setBackgroundColor(getResources().getColor(R.color.disabilita));
                binding.imSaveProject.setBackgroundColor(getResources().getColor(R.color.disabilita));

                binding.imNextStep.setClickable(false);
                binding.imInsertPdf.setClickable(false);
                binding.imSaveProject.setClickable(false);
                binding.etLink.setEnabled(false);

                startActivityForResult(Intent.createChooser(new Intent()
                                .setAction(Intent.ACTION_GET_CONTENT)
                                .setType("video/mp4"),
                        "Seleziona un video"), CODE_VIDEO);
            }
        });


        //TODO : settare il colore dei bottoni disabilitati
        binding.imInsertPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                binding.imNextStep.setBackgroundColor(getResources().getColor(R.color.disabilita));
                binding.imCaricaVideo.setBackgroundColor(getResources().getColor(R.color.disabilita));
                binding.imSaveProject.setBackgroundColor(getResources().getColor(R.color.disabilita));

                binding.imNextStep.setClickable(false);
                binding.imCaricaVideo.setClickable(false);
                binding.imSaveProject.setClickable(false);
                binding.etLink.setEnabled(false);

                // Toast.makeText(getApplicationContext(), "Prova", Toast.LENGTH_SHORT).show();
                startActivityForResult(Intent.createChooser(new Intent()
                                .setAction(Intent.ACTION_GET_CONTENT)
                                .setType("application/pdf"),
                        "Seleziona un PDF"), CODE_PDF);
            }
        });

        binding.imInsertPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                binding.imNextStep.setBackgroundColor(getResources().getColor(R.color.disabilita));
                binding.imCaricaVideo.setBackgroundColor(getResources().getColor(R.color.disabilita));
                binding.imSaveProject.setBackgroundColor(getResources().getColor(R.color.disabilita));

                binding.imNextStep.setClickable(false);
                binding.imCaricaVideo.setClickable(false);
                binding.imSaveProject.setClickable(false);
                binding.etLink.setEnabled(false);

                Toast.makeText(getApplicationContext(), "Prova", Toast.LENGTH_SHORT).show();
                startActivityForResult(Intent.createChooser(new Intent()
                                .setAction(Intent.ACTION_GET_CONTENT)
                                .setType("application/pdf"),
                        "Seleziona un PDF"), CODE_PDF);
            }
        });
    }



    private void aggiungiPassi() {
        firstTime = false;
        if (filePath == null) {
            jsonBuilder.aggiungiPassoAllaLista(listaPassi, jsonBuilder.creaPasso("questionario", binding.etLink.getText().toString()));

        } else if (type.contains("Video")) {
            jsonBuilder.aggiungiPassoAllaLista(listaPassi, jsonBuilder.creaPasso("video", String.valueOf(linkToJoinJSON)));
            Log.d("oggetto", listaPassi.toString());

        } else if (type.contains("PDF")) {
            jsonBuilder.aggiungiPassoAllaLista(listaPassi, jsonBuilder.creaPasso("pdf", String.valueOf(linkToJoinJSON)));
            Log.d("oggetto", listaPassi.toString());
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_cancella, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menuCancella){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Cancella tutto");
            builder.setMessage("Sicuro di voler tornare indietro?\n" + "Questo eliminerà tutti i passaggi fatti");
            builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent HomePageSomministratoreRicarica = new Intent(getApplicationContext(), HomePageSomministratore.class);
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
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CODE_VIDEO && resultCode == RESULT_OK && data != null && data.getData() != null) {
            this.filePath = data.getData();
            this.type = "Video";
             Snackbar.make(findViewById(android.R.id.content), "Hai selezionato un video", Snackbar.LENGTH_SHORT).show();
        } else if (requestCode == CODE_PDF && resultCode == RESULT_OK && data != null && data.getData() != null) {
            this.filePath = data.getData();
            //Log.d("oggetto", filePath.toString());
            this.type = "PDF";
            Snackbar.make(findViewById(android.R.id.content), "Hai selezionato un file PDF", Snackbar.LENGTH_SHORT).show();
        } else {
            Snackbar.make(findViewById(android.R.id.content), "Non hai selezionato nulla", Snackbar.LENGTH_SHORT).show();
        }
    }

    public void onBackPressed()
    {
        showDialog();
    }
    public void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Attenzione");
        builder.setMessage("Sicuro di voler tornare indietro?\n" + "Questo eliminerà tutti i passaggi fatti");
        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent HomePageSomministratoreRicarica = new Intent(getApplicationContext(), HomePageSomministratore.class);
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

}

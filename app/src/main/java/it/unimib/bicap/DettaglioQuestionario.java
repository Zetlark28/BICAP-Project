package it.unimib.bicap;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import it.unimib.bicap.service.JsonBuilder;

import it.unimib.bicap.databinding.ActivityDettaglioQuestionarioBinding;

public class DettaglioQuestionario extends AppCompatActivity {

    private static final int CODE_VIDEO = 1;
    private static final int CODE_PDF = 2;
    private StorageReference mStorageRef;
    private Uri filePath;
    String type;
    TextView linkQuestionario;
    private String linkToJoinJSON;
    private static JsonBuilder jsonBuilder = JsonBuilder.getJsonBuilder();
    private JSONObject progetto = new JSONObject();
    private JSONArray listaPassi = new JSONArray();

    private static final String TAG = "DettaglioQuestionario";
    private ActivityDettaglioQuestionarioBinding binding;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dettaglio_questionario);
        FirebaseApp.initializeApp(this);
        linkQuestionario = (TextView) findViewById(R.id.etLink);
        mStorageRef = FirebaseStorage.getInstance().getReference();

        binding = ActivityDettaglioQuestionarioBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        toolbar.setTitle("Nome Progetto");
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);

        final String progettoString = getIntent().getStringExtra("progetto");
        try {
            progetto = new JSONObject(progettoString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("OGGETTO JSON", progetto.toString() );

        findViewById(R.id.imCaricaVideo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(Intent.createChooser(new Intent()
                                .setAction(Intent.ACTION_GET_CONTENT)
                                .setType("video/mp4"),
                        "Seleziona un video"), CODE_VIDEO);
            }
        });

        findViewById(R.id.imInsertPdf).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(Intent.createChooser(new Intent()
                                .setAction(Intent.ACTION_GET_CONTENT)
                                .setType("application/pdf"),
                        "Seleziona un PDF"), CODE_PDF);
            }
        });

        findViewById(R.id.imNextStep).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Svolgo il controllo sul fatto che deve essere scelto solo un'opzione tra le tre disponibili
                if (filePath == null && linkQuestionario.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "Devi scegliere un'opzione", Toast.LENGTH_SHORT).show();
                } else if (!(filePath==null) && linkQuestionario.getText().toString().equals("")) {
                    if (type.equals("Video")) {
                        uploadFile("Video/");
                        // TODO: quando faccio l'upload del video/pdf devo dargli un nome a caso nello storage che non si ripete
                    } else {
                        uploadFile("Documenti/");
                    }
                }
                else if (!linkQuestionario.getText().toString().equals("") && filePath==null){

                    if (!linkQuestionario.getText().toString().contains("http://")){
                        Toast.makeText(getApplicationContext(), "Errore: il link deve iniziare per http:// o https://", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        jsonBuilder.aggiungiPassoAllaLista(listaPassi,jsonBuilder.creaPasso("questionario", linkQuestionario.getText().toString()));
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(), "Devi selezionare solo un'opzione tra le tre proposte", Toast.LENGTH_SHORT).show();
                }
                //Setto filePath e etLinkQuestionario a null, così si possono recompilare coi valori nuovi
                filePath = null;
                linkQuestionario.setText("");
            }
        });

        findViewById(R.id.imSaveProject).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Capire se sto uploadando PDF/Video o se sto inserendo il link del questionario
                if(type != null) {
                    if (type.equals("Video")) {
                        uploadFile("Video MP4/file");
                        Log.d("oggetto", progetto.toString() + 2);
                    } else if (type.equals("PDF")) {
                        uploadFile("Documenti PDF");
                    }
                }
                else {
                    jsonBuilder.aggiungiPassoAllaLista(listaPassi,jsonBuilder.creaPasso("questionario", linkQuestionario.getText().toString()));
                    Log.d("oggetto", listaPassi.toString());
                }

                //TODO: fixare il flusso, quando carico un video o pdf prima salva il progetto e poi crea il passo!Controlla con debug
                jsonBuilder.aggiungiListaPassi(progetto,listaPassi);
                Log.d("oggetto", progetto.toString()+1);

                // TODO:  sovrascrittura del file, il JSON è nella variabile progetto -> upload del file -> salvataggio nuovo link sul DB
                // TODO: Reindirizzare l'utente ad un'activity dove ci sarà scritto "Progetto salvato con successo"
                Intent home = new Intent (getApplicationContext(),HomePageSomministratore.class);
                startActivity(home);
            }
        });
    }

    private void uploadFile (final String directory) {
        if (filePath != null) {
            final StorageReference fileRef = mStorageRef.child(directory);
            fileRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(getApplicationContext(), "Hai aggiunto un passo", Toast.LENGTH_SHORT).show();

                            fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    linkToJoinJSON = String.valueOf(uri);
                                    if(directory.contains("Video")) {
                                        jsonBuilder.aggiungiPassoAllaLista(listaPassi,jsonBuilder.creaPasso("video", linkToJoinJSON));
                                    }
                                    else
                                    {
                                        jsonBuilder.aggiungiPassoAllaLista(listaPassi,jsonBuilder.creaPasso("pdf",linkToJoinJSON));
                                        Log.d("oggetto", listaPassi.toString() +3);
                                    }
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(getApplicationContext(), "Errore nell'upload", Toast.LENGTH_SHORT).show();

                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            updateProgress(taskSnapshot);
                        }
                    });
        } else {
            Toast.makeText(this, "Non hai scelto nessun file", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateProgress(UploadTask.TaskSnapshot taskSnapshot) {
        long fileSize = taskSnapshot.getTotalByteCount();
        long uploadBytes = taskSnapshot.getBytesTransferred();
        long progress = (100 * uploadBytes) / fileSize;
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.pbUpload);
        progressBar.setProgress((int) progress);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CODE_VIDEO && resultCode == RESULT_OK && data != null && data.getData() != null){
            this.filePath = data.getData();
            this.type = "Video";
            Toast.makeText(getApplicationContext(), "Hai selezionato un video", Toast.LENGTH_SHORT).show();
        }else if (requestCode == CODE_PDF && resultCode == RESULT_OK && data != null && data.getData() != null){
            this.filePath = data.getData();
            this.type = "PDF";
            Toast.makeText(getApplicationContext(), "Hai selezionato un file PDF", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getApplicationContext(), "Non hai selezionato nulla", Toast.LENGTH_SHORT).show();
        }

        binding.btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        binding.imNextStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        binding.imSaveProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        binding.imCaricaVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        binding.imInsertPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}

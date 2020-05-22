package it.unimib.bicap;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;


import it.unimib.bicap.service.JsonBuilder;
import it.unimib.bicap.databinding.ActivityDettaglioQuestionarioBinding;

// TODO: (Arthur) quando il somministratore clicca su Salva Progetto ma la variabile path contiene qualcosa o la text ha un link si deve chiedere al somministratore la conferma
// TODO: La conferma dev'essere chiesta in generale anche
// TODO: Aggiungere tanti ma tantissimi controlli
// TODO: Creare il fire writer

public class DettaglioQuestionario extends AppCompatActivity {

    private static final int CODE_VIDEO = 1;
    private static final int CODE_PDF = 2;
    private static final String FILE_NAME = "progetti.json";
    private static final String TAG = "DettaglioQuestionario";
    private StorageReference mStorageRef;
    private StorageReference ref;
    private Uri filePath;
    private String type;
    private String linkToJoinJSON;
    private static JsonBuilder jsonBuilder = JsonBuilder.getJsonBuilder();
    private JSONObject progetto = new JSONObject();
    private JSONArray listaPassi = new JSONArray();
    private JSONArray listaProgetti = new JSONArray();
    private String progettiJSON;

    private ActivityDettaglioQuestionarioBinding binding;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        progettiJSON = getIntent().getStringExtra("progetti");
        binding = ActivityDettaglioQuestionarioBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        toolbar.setTitle("Nome Progetto");
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentHomeProf = new Intent(getApplicationContext(), HomePageSomministratore.class);
                startActivity(intentHomeProf);
                finish();
            }
        });

        final String progettoString = getIntent().getStringExtra("progetto");
        try {
            assert progettoString != null;
            progetto = new JSONObject(progettoString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("OGGETTO JSON", progetto.toString());

        binding.btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Capire se sto uploadando PDF/Video o se sto inserendo il link del questionario
                if (type != null) {
                    if (type.equals("Video")) {
                        uploadFile("Video/file");
                        Log.d("oggetto", progetto.toString() + 2);
                        Snackbar.make(v, "Hai inserito un video", Snackbar.LENGTH_SHORT).show();
                    } else if (type.equals("PDF")) {
                        uploadFile("Documenti/");
                        Snackbar.make(v, "Hai inserito un PDF", Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    Snackbar.make(v, "Attenzione, non hai selezionato alcun file !", Snackbar.LENGTH_SHORT).show();
                }
            }
        });


        binding.imNextStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Svolgo il controllo sul fatto che deve essere scelto solo un'opzione tra le tre disponibili
                aggiungiPassi();
                filePath = null;
                binding.etLink.setText("");
                binding.pbUpload.setProgress(0);
                Snackbar.make(v, "Sei passato al passaggio successivo !", Snackbar.LENGTH_SHORT).show();
            }
        });

        binding.imSaveProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(filePath == null) || !binding.etLink.getText().toString().equals("")) {
                    aggiungiPassi();
                }

                jsonBuilder.aggiungiListaPassi(progetto, listaPassi);
                Log.d("oggetto", progetto.toString());
                try {
                    JSONObject jsonObject = new JSONObject(progettiJSON);
                    listaProgetti = jsonObject.getJSONArray("progetti");
                    JSONObject progetti = jsonBuilder.aggiungiProgettoInLista(listaProgetti,progetto);
                    progetti.put("progetti", listaProgetti);
                    Log.d("oggetto", progetti.toString());

                    write(progetti);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                // TODO:  sovrascrittura del file, il JSON è nella variabile progetto -> upload del file
                // TODO: Reindirizzare l'utente ad un'activity dove ci sarà scritto "Progetto salvato con successo"
                //Intent home = new Intent(getApplicationContext(), HomePageSomministratore.class);
                //startActivity(home);
            }
        });

        binding.imCaricaVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(Intent.createChooser(new Intent()
                                .setAction(Intent.ACTION_GET_CONTENT)
                                .setType("video/mp4"),
                        "Seleziona un video"), CODE_VIDEO);
            }
        });

        binding.imInsertPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Prova", Toast.LENGTH_SHORT).show();
                startActivityForResult(Intent.createChooser(new Intent()
                                .setAction(Intent.ACTION_GET_CONTENT)
                                .setType("application/pdf"),
                        "Seleziona un PDF"), CODE_PDF);
            }
        });
    }

    private void aggiungiPassi() {
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

    private void uploadFile(final String directory) {
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
                                    Log.d("oggetto", "Upload completato");
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(getApplicationContext(), "Errore nell'upload", Toast.LENGTH_SHORT).show();
                            Log.d("oggetto", "Errore nell'upload");
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
        binding.pbUpload.setProgress((int) progress);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CODE_VIDEO && resultCode == RESULT_OK && data != null && data.getData() != null) {
            this.filePath = data.getData();
            this.type = "Video";
            Toast.makeText(getApplicationContext(), "Hai selezionato un video", Toast.LENGTH_SHORT).show();
        } else if (requestCode == CODE_PDF && resultCode == RESULT_OK && data != null && data.getData() != null) {
            this.filePath = data.getData();
            Log.d("oggetto", filePath.toString());
            this.type = "PDF";
            Toast.makeText(getApplicationContext(), "Hai selezionato un file PDF", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Non hai selezionato nulla", Toast.LENGTH_SHORT).show();
        }
    }


    public void write(JSONObject progetti){
        try {
            Writer output;
            FileOutputStream fOut = openFileOutput(FILE_NAME, MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(fOut);
            osw.write(progetti.toString());
            osw.flush();
            Toast.makeText(getApplicationContext(), "Composition saved", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
        filePath = Uri.parse("file:///data/data/it.unimib.bicap/files/progetti.json");
        uploadFile("Progetti/progetti.json");

    }
}

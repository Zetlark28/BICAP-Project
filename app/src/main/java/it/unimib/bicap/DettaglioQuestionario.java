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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

public class DettaglioQuestionario extends AppCompatActivity {

    private static final int CODE_VIDEO = 1;
    private static final int CODE_PDF = 2;
    private StorageReference mStorageRef;
    private Uri filePath;
    String type;
    TextView linkQuestionario;
    private String linkToJoinJSON;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dettaglio_questionario);

        linkQuestionario = (TextView) findViewById(R.id.etLink);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        Toolbar toolbar = findViewById(R.id.toolbar_main);
        toolbar.setTitle("Nome Progetto");
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);

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
                        // TODO: Operazione per il salvataggio del link del questionario nel JSON, la variabile che contiene il link è linkQuestionario
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

                if (type.equals("Video")){
                    uploadFile("Video MP4/file");
                }
                else if (type.equals("PDF")) {
                    uploadFile("Documenti PDF");
                }
                else {
                    // TODO: Operazione per il salvataggio del link del questionario nel JSON
                }
                // TODO: Final step di creazione JSON -> sovrascrittura del file -> upload del file -> salvataggio nuovo link sul DB
                // TODO: Reindirizzare l'utente ad un'activity dove ci sarà scritto "Progetto salvato con successo"

                Log.i("url", String.valueOf(linkToJoinJSON));
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
                                    // TODO: In base alla directory scopriamo se è PDF o Video
                                    if(directory.contains("Video")) {
                                        // TODO: Creo il passo di tipo Video con il link salvato nella variabile linkToJoinJSON
                                    }
                                    else
                                    {
                                        // TODO: Creo il passo di tipo PDF con il link salvato nella variabile linkToJoinJSON
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
    }
}

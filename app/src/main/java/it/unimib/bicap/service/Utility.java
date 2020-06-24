package it.unimib.bicap.service;



import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import it.unimib.bicap.activity.somministratore.DettaglioQuestionario;
import it.unimib.bicap.activity.somministratore.EliminaProgetti;
import it.unimib.bicap.databinding.ActivityDettaglioQuestionarioBinding;
import it.unimib.bicap.exception.UtilityException;

import static android.content.Context.MODE_PRIVATE;

public class Utility {

     private static final String QUESTIONNAIRE_FILE_PATH_DIR_LOCAL = "file:///data/data/it.unimib.bicap/files/progetti.json";
     private static final String FILE_NAME =  "progetti.json";
     private static final String FIREBASE_PATH_PROJECT = "Progetti/progetti.json";
     private static final int ONE_MB = 50 * 1024 * 1024;
    private static String keyValue;

     //scrittura del jsonObject convertito a stringa su un file
     //carica il file su firebase
     public static void write(JSONObject progetti, Object activityInstance, ActivityDettaglioQuestionarioBinding binding){
         Context context;
         Context baseContext;
         Boolean writing = Boolean.FALSE;
         //in base all'activity passata inizializzo le variabili
         if(activityInstance instanceof DettaglioQuestionario) {
             DettaglioQuestionario activity = (DettaglioQuestionario) activityInstance;
             context = activity.getApplicationContext();
             baseContext = activity.getBaseContext();
             writing=Boolean.TRUE;
         }else if (activityInstance instanceof EliminaProgetti){
             EliminaProgetti activity = (EliminaProgetti) activityInstance;
             context = activity.getApplicationContext();
             baseContext = activity.getBaseContext();
         }else{
             throw UtilityException.UTILITY_ACTIVITY_CONTEXT_UNAUTHORIZED;
         }
         final Context finalContext = context;
         final Context finalBaseContext = baseContext;
        try {
            FileOutputStream fOut = finalContext.openFileOutput(FILE_NAME, MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(fOut);
            osw.write(progetti.toString());
            osw.flush();
        } catch (Exception e) {
            Toast.makeText(finalBaseContext, e.getMessage(), Toast.LENGTH_LONG).show();
        }

         if(writing)
             uploadFile(Uri.parse(QUESTIONNAIRE_FILE_PATH_DIR_LOCAL),FIREBASE_PATH_PROJECT, finalContext, binding);
         else
             updateFile(Uri.parse(QUESTIONNAIRE_FILE_PATH_DIR_LOCAL),FIREBASE_PATH_PROJECT, finalContext);
     }

    //scarica la chiave associata ad un determinato oggetto su firebase
    public static void getKeyValue(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("chiave");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Object key = dataSnapshot.child("valoreChiave").getValue();
                if(key==null)
                    throw UtilityException.UTILITY_FIREBASE_KEY_NOT_FOUND;
                keyValue = key.toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                throw UtilityException.UTILITY_FIREBASE_DB_READ_VALUE_FAIL;
            }
        });
    }
    //setta la chiave di un oggetto (file generico) su firebase rendendola univoca aumentando di 1
    public static String setKeyValue() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("chiave");
        String[] id = keyValue.split("-");
        int newNumber = Integer.parseInt(id[1]) + 1;
        String chiave = "File-" + newNumber;
        myRef.child("valoreChiave").setValue(chiave);
        return chiave;
    }

    //carica un file contenuto nella memoria del telefono su firebase
    public static void uploadFile(final Uri filepath, final String directory, final Context context, final ActivityDettaglioQuestionarioBinding binding) {
        FirebaseApp.initializeApp(context);
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        final StorageReference fileRef = mStorageRef.child(directory);
        //se ha successo carica il percorso del file sul json
        fileRef.putFile(filepath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    DettaglioQuestionario.setLinkToJoinJSON(String.valueOf(uri));
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            throw UtilityException.UTILITY_FIREBASE_UPLOAD_FAIL;
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            updateProgress(taskSnapshot,binding);
                        }
                    });
        }



        //scarica il pdf da firebase in locale
        public static void downloadPDF(String link, ProgressDialog progressDialog) {
            StorageReference mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl(link);



            mStorageRef.getBytes(ONE_MB).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    // Data for "images/island.jpg" is returns, use this as needed
                        try {
                            @SuppressLint("SdCardPath") String path = "/data/data/it.unimib.bicap/cache/PDF.pdf";
                            FileOutputStream stream = new FileOutputStream(path);
                            try {
                                stream.write(bytes);
                            } catch (IOException e) {
                                throw UtilityException.UTILITY_ACTIVITY_DOWNLOAD_FAIL;
                            }
                        } catch (FileNotFoundException e) {
                            throw UtilityException.UTILITY_ACTIVITY_DOWNLOAD_FAIL;
                        }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
            progressDialog.dismiss();
        }

    //update barra di caricamento
    private static void updateProgress(UploadTask.TaskSnapshot taskSnapshot, ActivityDettaglioQuestionarioBinding binding) {
        long fileSize = taskSnapshot.getTotalByteCount();
        long uploadBytes = taskSnapshot.getBytesTransferred();
        long progress = (100 * uploadBytes) / fileSize;
        binding.pbUpload.setProgress((int) progress);
    }



    //aggiorna un file nello storage
    public static void updateFile(final Uri filepath, final String directory, final Context context){
        FirebaseApp.initializeApp(context);
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        final StorageReference fileRef = mStorageRef.child(directory);
        fileRef.putFile(filepath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                DettaglioQuestionario.setLinkToJoinJSON(String.valueOf(uri));
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        throw UtilityException.UTILITY_FIREBASE_UPLOAD_FAIL;
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    }
                });
    }
}

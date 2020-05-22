package it.unimib.bicap.service;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import it.unimib.bicap.DettaglioQuestionario;
import it.unimib.bicap.EliminaProgetti;
import it.unimib.bicap.databinding.ActivityDettaglioQuestionarioBinding;

import static android.content.Context.MODE_PRIVATE;

public class Utility {

     private static final String QUESTIONNAIRE_FILE_PATH_DIR_LOCAL = "file:///data/data/it.unimib.bicap/files/progetti.json";
     private static final String FILE_NAME =  "progetti.json";
     private static final String FIREBASE_PATH_PROJECT = "Progetti/progetti.json";
     private static final int ONE_MB = 1024 * 1024;

     public static void write(JSONObject progetti, Object activityInstance, ActivityDettaglioQuestionarioBinding binding){
         Context context = null;
         Context baseContext = null;
         Boolean writing = Boolean.FALSE;
         if(activityInstance instanceof DettaglioQuestionario) {
             DettaglioQuestionario activity = (DettaglioQuestionario) activityInstance;
             context = activity.getApplicationContext();
             baseContext = activity.getBaseContext();
             writing=Boolean.TRUE;
         }else if (activityInstance instanceof EliminaProgetti){
             EliminaProgetti activity = (EliminaProgetti) activityInstance;
             context = activity.getApplicationContext();
             baseContext = activity.getBaseContext();
         }
         final Context finalContext = context;
         final Context finalBaseContext = baseContext;
        try {
            FileOutputStream fOut = finalContext.openFileOutput(FILE_NAME, MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(fOut);
            osw.write(progetti.toString());
            osw.flush();
            Toast.makeText(finalContext, "Composition saved", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Toast.makeText(finalBaseContext, e.getMessage(), Toast.LENGTH_LONG).show();
        }

         if(writing)
             uploadFile(Uri.parse(QUESTIONNAIRE_FILE_PATH_DIR_LOCAL),FIREBASE_PATH_PROJECT, finalContext, binding);
         else
             updateFile(Uri.parse(QUESTIONNAIRE_FILE_PATH_DIR_LOCAL),FIREBASE_PATH_PROJECT, finalContext);
     }


    public static void uploadFile(final Uri filepath, final String directory, final Context context, final ActivityDettaglioQuestionarioBinding binding) {

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
                                    Log.d("oggetto", "Upload completato");
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(context, "Errore nell'upload", Toast.LENGTH_SHORT).show();
                            Log.d("oggetto", "Errore nell'upload");
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            updateProgress(taskSnapshot,binding);
                        }
                    });


        }
    private static void updateProgress(UploadTask.TaskSnapshot taskSnapshot, ActivityDettaglioQuestionarioBinding binding) {
        long fileSize = taskSnapshot.getTotalByteCount();
        long uploadBytes = taskSnapshot.getBytesTransferred();
        long progress = (100 * uploadBytes) / fileSize;
        binding.pbUpload.setProgress((int) progress);
    }

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
                                Log.d("oggetto", "Upload completato");
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(context, "Errore nell'upload", Toast.LENGTH_SHORT).show();
                        Log.d("oggetto", "Errore nell'upload");
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                       //TODO : dialog in corso
                    }
                });
    }

//    public static JSONObject getProgetti(Context context){
//        FirebaseApp.initializeApp(context);
//        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
//        StorageReference ref = mStorageRef.child("/Progetti/progetti.json");
//        final JSONObject[] progetti = {null};
//        ref.getBytes(ONE_MB).addOnSuccessListener(new OnSuccessListener<byte[]>() {
//            @Override
//            public void onSuccess(byte[] bytes) {
//                String json = null;
//                try {
//                    json = new String(bytes, "UTF-8");
//                    progetti[0] = new JSONObject(json);
//                } catch (JSONException ex) {
//                    ex.printStackTrace();
//                } catch (UnsupportedEncodingException ex) {
//                    ex.printStackTrace();
//                }
//            }
//        });
//        return progetti[0];
//    }
}

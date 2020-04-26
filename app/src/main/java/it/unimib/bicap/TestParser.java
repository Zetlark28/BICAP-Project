package it.unimib.bicap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import it.unimib.bicap.service.GetterInfo;
import it.unimib.bicap.service.GetterLocal;

public class TestParser extends AppCompatActivity {

    //per salvataggio dello username
    private static final String SHARED_PREFERENCE_CODE = "it.unimib.bicap";
    private StorageReference mStorageRef;
    private StorageReference ref;
    private static final int ONE_MB = 1024 * 1024;
    //oggetto da passare nelle activity per visualizzazione e per fare i questionari
    private static JSONArray progetti;
    private Button bottone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_parser);

        //Da usare  quando si entra nell'area utente che fa i questionari
        mStorageRef = FirebaseStorage.getInstance().getReference();
        //cartella e file su firebase, se lo vuoi testare collega il tuo account firebase e cambia o sua lo stesso path
        ref = mStorageRef.child("/somministratore/structure.json");

        ref.getBytes(ONE_MB).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                String json = null;
                try {
                    json = new String(bytes, "UTF-8");
                    Log.d("succes", json);
                    JSONObject progettiToParse = new JSONObject(json);
                    progetti = progettiToParse.getJSONArray("progetti");
                    Log.d("success", progetti.toString());
                    GetterInfo getter = new GetterLocal(TestParser.this);
                    JSONObject progetto = getter.getProgetto(progetti, 0);
                    String id = getter.getIdProgetto(progetto);
                    Log.d("success",id);
                    String nomeProgetto = getter.getNomeProgetto(progetto);
                    Log.d("success",nomeProgetto);
                    JSONObject progettoPrelevato = getter.getProgetto(progetti,0);
                    Log.d("success",progettoPrelevato.toString());
                    //Vai alla prossima pagina
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("error", "json not parse");
            }
        });
    }






    //se voglio salvare il file  nel cellulare in cartella download(al momento non è usato)
    public void downloadFile (Context context, String fileName, String fileExtension, String
            destinationDirectory, String url){
        DownloadManager downloadManager = (DownloadManager) getApplicationContext().getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(getApplicationContext(), destinationDirectory, fileName + fileExtension);
        downloadManager.enqueue(request);

    }
}

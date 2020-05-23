package it.unimib.bicap;

import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import adapter.ProgettiAdapterRV;
import it.unimib.bicap.db.DBConstants;
import it.unimib.bicap.db.DBManager;

public class QuestionariDaFare extends Fragment {

    private static final String TAG = "QuestionariDaFare";
    private StorageReference mStorageRef;
    private StorageReference ref;
    private static final int ONE_MB = 1024 * 1024;
    private static JSONArray progetti;
    private DBManager db=null;

    private ProgettiAdapterRV progettiAdapterRV;
    private String from = "daFare";
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_lista_progetti, container, false);
        final RecyclerView recyclerView = rootView.findViewById(R.id.rvProgetti);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(linearLayoutManager);

        //TODO:  getUtenteReale
        final Integer idUtente = 0;
        mStorageRef = FirebaseStorage.getInstance().getReference();
        ref = mStorageRef.child("/Progetti/progetti.json");
        ref.getBytes(ONE_MB).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            String jsonString = null;
            @Override
            public void onSuccess(byte[] bytes) {
                try {
                    jsonString = new String(bytes, "UTF-8");
                    JSONObject progettiToParse = new JSONObject(jsonString);
                    progetti = progettiToParse.getJSONArray("progetti");

                    db = new DBManager(getContext());
                    Cursor progettiCompletati = db.selectCompletati(idUtente);
                    Cursor progettiDaCompletare = db.selectDaCompletare(idUtente);

                    JSONArray progettiDaFare = new JSONArray();
                    for(int i = 0; i<progetti.length(); i++){
                        if(!DBManager.isCompletato(progettiCompletati,progetti.getJSONObject(i).getInt("id")))
                            if(!DBManager.isDaCompletare(progettiDaCompletare, progetti.getJSONObject(i).getInt("id")))
                                 progettiDaFare.put(progetti.getJSONObject(i));
                    }
                    progettiAdapterRV = new ProgettiAdapterRV(getContext(), progettiDaFare,  from);
                    recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
                    recyclerView.setAdapter(progettiAdapterRV);
                } catch (UnsupportedEncodingException | JSONException e){
                    e.printStackTrace();
                }

            }
        });



        return rootView;
    }
}

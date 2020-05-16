package it.unimib.bicap;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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

public class QuestionariDaTerminare extends Fragment {

    private static final String TAG = "QuestionariDaTerminare";
    private StorageReference mStorageRef;
    private StorageReference ref;
    private static final int ONE_MB = 1024 * 1024;
    private static JSONArray progetti;
    //private RecyclerView recyclerView;
    private ProgettiAdapterRV progettiAdapterRV;
    private String [] titoli = {"Questionario 1", "Questionario 2"};
    private String from = "daTerminare";
    private ImageView immagine;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.activity_lista_progetti, container, false);
        final RecyclerView recyclerView = rootView.findViewById(R.id.rvProgetti);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(linearLayoutManager);

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
                    //TODO: selezione dei questionari da terminare
                    progettiAdapterRV = new ProgettiAdapterRV(getContext(), progetti,  from);
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

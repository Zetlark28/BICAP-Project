package it.unimib.bicap.activity.utente;

import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import it.unimib.bicap.ItemSearch;
import it.unimib.bicap.R;
import it.unimib.bicap.adapter.ProgettiDaTerminareAdapterRV;
import it.unimib.bicap.db.DBManager;
import it.unimib.bicap.service.GetterInfo;
import it.unimib.bicap.service.GetterLocal;

public class QuestionariDaTerminare extends Fragment {

    private static final String TAG = "QuestionariDaTerminare";
    private StorageReference mStorageRef;
    private StorageReference ref;
    private static final int ONE_MB = 1024 * 1024;
    private static JSONArray progetti;
    private DBManager db;

    //private RecyclerView recyclerView;
    private String [] titoli = {"Questionario 1", "Questionario 2"};
    private String from = "daTerminare";
    private ImageView immagine;
    private JSONObject progettiTot;
    private RecyclerView recyclerView;
    private View rootView;
    private EditText ricercadafare;
    private GetterInfo getterInfo = new GetterLocal();
    private List<ItemSearch> exampleList = new ArrayList();
    private ProgettiDaTerminareAdapterRV progettiAdapterTerminare;


    public QuestionariDaTerminare(JSONObject progettiTot) {
        this.progettiTot = progettiTot;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.activity_lista_progetti, container, false);
        final RecyclerView recyclerView = rootView.findViewById(R.id.rvProgetti);
        ricercadafare = rootView.findViewById(R.id.ricercadafare);


        db = new DBManager(getContext());

        final String idUtente = "prova";
        try {
            progetti = progettiTot.getJSONArray("progetti");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONArray progDaCompletare= new JSONArray();
        try {
            for(int i = 0; i<progetti.length(); i++) {
                if (db.isDaCompletare(progetti.getJSONObject(i).getInt("id"))) {
                    progDaCompletare.put(progetti.getJSONObject(i));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int i = 0;i<progDaCompletare.length();i++){
            try {
                exampleList.add(new ItemSearch(getterInfo.getNomeProgetto(progDaCompletare.getJSONObject(i)), getterInfo.getDescrizione(progDaCompletare.getJSONObject(i))));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        progettiAdapterTerminare= new ProgettiDaTerminareAdapterRV(getContext(), progDaCompletare, exampleList, from);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(progettiAdapterTerminare);



        ricercadafare.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                ricercadafare.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                progettiAdapterTerminare.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
                ricercadafare.setError(null);
            }
        });

        return rootView;
    }
}

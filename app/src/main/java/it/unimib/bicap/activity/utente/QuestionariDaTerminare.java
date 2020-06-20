package it.unimib.bicap.activity.utente;

import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import it.unimib.bicap.activity.somministratore.ExampleItem;
import it.unimib.bicap.R;
import it.unimib.bicap.adapter.ProgettiDaTerminareAdapterRV;
import it.unimib.bicap.db.DBManager;
import it.unimib.bicap.service.GetterInfo;
import it.unimib.bicap.service.GetterLocal;

public class QuestionariDaTerminare extends Fragment {

    private static final String TAG = "QuestionariDaTerminare";
    private static JSONArray progetti;
    private JSONObject progettiTot;
    private EditText ricercaDaFare;
    private GetterInfo getterInfo = new GetterLocal();
    private List<ExampleItem> exampleList = new ArrayList<>();
    private ProgettiDaTerminareAdapterRV progettiAdapterTerminare;


    public QuestionariDaTerminare(JSONObject progettiTot) {
        this.progettiTot = progettiTot;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.activity_lista_progetti, container, false);
        final RecyclerView recyclerView = rootView.findViewById(R.id.rvProgetti);
        ricercaDaFare = rootView.findViewById(R.id.ricercadafare);


        DBManager db = new DBManager(getContext());

        try {
            progetti = progettiTot.getJSONArray("progetti");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONArray progDaCompletare= new JSONArray();
        try {
            for(int i = 0; i<progetti.length(); i++) {
                if (db.isDaCompletare(getterInfo.getIdProgetto(progetti.getJSONObject(i)))){
                    progDaCompletare.put(progetti.getJSONObject(i));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int i = 0;i<progDaCompletare.length();i++){
            try {
                exampleList.add(new ExampleItem(getterInfo.getNomeProgetto(progDaCompletare.getJSONObject(i)),
                        getterInfo.getDescrizione(progDaCompletare.getJSONObject(i))));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        String from = "daTerminare";
        progettiAdapterTerminare= new ProgettiDaTerminareAdapterRV(getContext(), progDaCompletare, exampleList, from);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(progettiAdapterTerminare);



        ricercaDaFare.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                ricercaDaFare.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                progettiAdapterTerminare.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
                ricercaDaFare.setError(null);
            }
        });

        return rootView;
    }
}

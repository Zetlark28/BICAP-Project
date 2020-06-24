package it.unimib.bicap.activity.utente;

import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SearchView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import it.unimib.bicap.ItemSearch;
import it.unimib.bicap.R;
import it.unimib.bicap.adapter.ProgettiAdapterRV;
import it.unimib.bicap.db.DBManager;
import it.unimib.bicap.service.GetterInfo;
import it.unimib.bicap.service.GetterLocal;

public class QuestionariDaFare extends Fragment {

    private static JSONArray progetti;
    private DBManager db = null;
    private JSONObject progettiTot;
    private ProgettiAdapterRV progettiAdapterRV;
    private String from = "daFare";
    private JSONArray progettiDaFare;
    private String nomeProgetto;
    private List<ItemSearch> exampleList = new ArrayList<>();
    private GetterInfo getterInfo = new GetterLocal();
    private RecyclerView recyclerView;
    private View rootView;
    private EditText ricercadafare;

    public QuestionariDaFare(JSONObject progettiTot) {
        this.progettiTot = progettiTot;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.activity_lista_progetti, container, false);
        recyclerView = rootView.findViewById(R.id.rvProgetti);
        ricercadafare = rootView.findViewById(R.id.ricercadafare);

        try {
            progetti = progettiTot.getJSONArray("progetti");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        db = new DBManager(getContext());
        progettiDaFare = new JSONArray();

        progettiDaFare = cerca("");

        for (int i = 0;i<progettiDaFare.length();i++){
            try {
                exampleList.add(new ItemSearch(getterInfo.getNomeProgetto(progettiDaFare.getJSONObject(i)), getterInfo.getDescrizione(progettiDaFare.getJSONObject(i))));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        progettiAdapterRV = new ProgettiAdapterRV(getContext(), progettiDaFare, exampleList, from);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(progettiAdapterRV);

        ricercadafare.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                 ricercadafare.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                progettiAdapterRV.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
                ricercadafare.setError(null);
            }
        });


        return rootView;

    }

    public JSONArray cerca(String query) {
        try {
            for (int i = 0; i < progetti.length(); i++) {
                nomeProgetto = getterInfo.getNomeProgetto(getterInfo.getProgetto(progetti, i)).toLowerCase();
                if (!db.isCompletato(progetti.getJSONObject(i).getInt("id")))
                    if (!db.isDaCompletare(progetti.getJSONObject(i).getInt("id")))
                        if (nomeProgetto.contains(query.toLowerCase())) {
                            progettiDaFare.put(progetti.getJSONObject(i));
                        }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return progettiDaFare;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);


    }
}

package it.unimib.bicap;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import it.unimib.bicap.adapter.ProgettiDaEliminareAdapterRV;
import it.unimib.bicap.constanti.ActivityConstants;
import it.unimib.bicap.databinding.ActivityEliminaProgettiBinding;
import it.unimib.bicap.service.GetterInfo;
import it.unimib.bicap.service.GetterLocal;

public class EliminaProgetti extends AppCompatActivity {

    private static final String TAG = "EliminaProgetti";
    private ActivityEliminaProgettiBinding binding;
    private static JSONObject progetti;
    private static JSONArray progettiAutore;
    private SearchView searchView;
    ProgettiDaEliminareAdapterRV progettiAdapter;
    private List <ExampleItem> exampleList = new ArrayList();
    GetterInfo getterInfo = new GetterLocal();

    public static void setProgetti(JSONObject progetti) {
        EliminaProgetti.progetti = progetti;
    }

    public static void setProgettiAutore(JSONArray progettiAutore) {
        EliminaProgetti.progettiAutore = progettiAutore;
    }

    String from = "eliminaProgetti";

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityEliminaProgettiBinding.inflate(getLayoutInflater());
        View v = binding.getRoot();
        setContentView(v);

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        toolbar.setTitle(ActivityConstants.ELIMINA_PROGETTI_TOOLBAR_TITLE);
        setSupportActionBar(toolbar);
        //final EliminaProgetti instance = this;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentHomeProfessore = new Intent(getApplicationContext(), HomePageSomministratore.class);
                startActivity(intentHomeProfessore);
                EliminaProgetti.this.finish();
            }
        });
        try {
//            if(progettiAutore==null)

            //TODO: check extras not null
            boolean ritorno = getIntent().getBooleanExtra("return", false);
            if (! ritorno) {
                progettiAutore = new JSONArray(getIntent().getExtras().getString("listaProgettiAutore"));
                progetti = new JSONObject(Objects.requireNonNull(getIntent().getExtras().getString("listaProgetti")));
            } else {
                progettiAutore = new JSONArray(getIntent().getExtras().getString("newList"));
                progetti = new JSONObject(Objects.requireNonNull(getIntent().getExtras().getString("newObject")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int i = 0;i<progettiAutore.length();i++){
            try {
                exampleList.add(new ExampleItem(getterInfo.getNomeProgetto(progettiAutore.getJSONObject(i)), getterInfo.getDescrizione(progettiAutore.getJSONObject(i))));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        binding.rvProgettiDaEliminare.setLayoutManager(linearLayoutManager);
        progettiAdapter = new ProgettiDaEliminareAdapterRV(getApplicationContext(), progettiAutore, exampleList, progetti ,this);
        binding.rvProgettiDaEliminare.setAdapter(progettiAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_ricerca, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                progettiAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

    //override startActivity con animazione slide avanti
    @Override
    public void startActivity(Intent intent){
        super.startActivity(intent);
        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
    }

    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), HomePageSomministratore.class));
        finish();

    }

    //override finish con animazione slide indietro
    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }

}

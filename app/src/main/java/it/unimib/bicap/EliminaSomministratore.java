package it.unimib.bicap;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import it.unimib.bicap.adapter.ProgettiDaEliminareAdapterRV;
import it.unimib.bicap.constanti.ActivityConstants;
import it.unimib.bicap.databinding.ActivityEliminaSomministratoreBinding;

public class EliminaSomministratore extends AppCompatActivity {

    private ActivityEliminaSomministratoreBinding binding;
    private static final String TAG = "EliminaSomministratore";
    ProgettiDaEliminareAdapterRV progettiAdapter;
    private List <ExampleItem> exampleList = new ArrayList();
    private static boolean ritorno = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Log.d(TAG, "Intent: " + intent.toString());
        HashMap<String, String> nomiSomm;
        List<String> somministratori = new ArrayList<>();
        nomiSomm = (HashMap<String, String>) intent.getSerializableExtra("somministratori");
        for (Map.Entry<String, String> entry : nomiSomm.entrySet()) {
            this.exampleList.add(new ExampleItem(entry.getValue(), entry.getKey()));
        }
        List<String> emails = intent.getStringArrayListExtra("emails");
        Log.d(TAG, "Hashmap elimina: " + nomiSomm);

        binding = ActivityEliminaSomministratoreBinding.inflate(getLayoutInflater());
        View v = binding.getRoot();
        setContentView(v);

        for (ExampleItem item : exampleList){
            Log.d(TAG, "Nome: " + item.getTextNome());
            Log.d(TAG, "Email: " + item.getTextEmail());
        }

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        toolbar.setTitle("Elimina Somministatore");
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentHomeProfessore = new Intent(getApplicationContext(), GestioneSomministratore.class);
                startActivity(intentHomeProfessore);
                overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                EliminaSomministratore.this.finish();
                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        binding.rvSomministratoreDaEliminare.setLayoutManager(linearLayoutManager);
        //progettiAdapter = new ProgettiDaEliminareAdapterRV(getApplicationContext(), nomiSomm, emails, this);
        progettiAdapter = new ProgettiDaEliminareAdapterRV(getApplicationContext(), exampleList, this);
        binding.rvSomministratoreDaEliminare.setAdapter(progettiAdapter);
    }

    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), GestioneSomministratore.class));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_ricerca, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();

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
}

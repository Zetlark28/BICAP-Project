package it.unimib.bicap;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import it.unimib.bicap.adapter.ProgettiDaEliminareAdapterRV;
import it.unimib.bicap.databinding.ActivityEliminaProgettiBinding;
import it.unimib.bicap.service.GetterInfo;
import it.unimib.bicap.service.GetterLocal;

public class EliminaProgetti extends AppCompatActivity {

    private static final String TAG = "EliminaProgetti";
    private ActivityEliminaProgettiBinding binding;
    private static JSONObject progetti;
    private static JSONArray progettiAutore;


    String from = "eliminaProgetti";

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityEliminaProgettiBinding.inflate(getLayoutInflater());
        View v = binding.getRoot();
        setContentView(v);

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        toolbar.setTitle("Eliminazione progetto");
        setSupportActionBar(toolbar);
        //final EliminaProgetti instance = this;

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
            progettiAutore = new JSONArray(getIntent().getExtras().getString("listaProgettiAutore"));
            progetti = new JSONObject(Objects.requireNonNull(getIntent().getExtras().getString("listaProgetti")));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        binding.rvProgettiDaEliminare.setLayoutManager(linearLayoutManager);
        ProgettiDaEliminareAdapterRV progettiAdapter = new ProgettiDaEliminareAdapterRV(getApplicationContext(), progettiAutore, progetti ,this);
        binding.rvProgettiDaEliminare.setAdapter(progettiAdapter);
        binding.rvProgettiDaEliminare.addItemDecoration(new DividerItemDecoration(binding.rvProgettiDaEliminare.getContext(), DividerItemDecoration.VERTICAL));

    }

    //override startActivity con animazione slide avanti
    @Override
    public void startActivity(Intent intent){
        super.startActivity(intent);
        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
    }

    //override finish con animazione slide indietro
    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }

}

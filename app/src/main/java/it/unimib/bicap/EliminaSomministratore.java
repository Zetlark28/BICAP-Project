package it.unimib.bicap;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import it.unimib.bicap.adapter.ProgettiDaEliminareAdapterRV;
import it.unimib.bicap.constanti.ActivityConstants;
import it.unimib.bicap.databinding.ActivityEliminaSomministratoreBinding;

public class EliminaSomministratore extends AppCompatActivity {

    private ActivityEliminaSomministratoreBinding binding;
    private static final String TAG = "EliminaSomministratore";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Log.d(TAG, "Intent: " + intent.toString());
        HashMap<String, String> nomiSomm;
        nomiSomm = (HashMap<String, String>) intent.getSerializableExtra("somministratori");
        List<String> emails = intent.getStringArrayListExtra("emails");
        Log.d(TAG, "Hashmap elimina: " + nomiSomm);

        binding = ActivityEliminaSomministratoreBinding.inflate(getLayoutInflater());
        View v = binding.getRoot();
        setContentView(v);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        toolbar.setTitle("Elimina Somministatore");
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentHomeProfessore = new Intent(getApplicationContext(), GestioneSomministratore.class);
                startActivity(intentHomeProfessore);
                EliminaSomministratore.this.finish();
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        binding.rvSomministratoreDaEliminare.setLayoutManager(linearLayoutManager);
        ProgettiDaEliminareAdapterRV progettiAdapter = new ProgettiDaEliminareAdapterRV(getApplicationContext(), nomiSomm, emails, this);
        binding.rvSomministratoreDaEliminare.setAdapter(progettiAdapter);
    }

    @Override
    public void startActivity(Intent intent){
        super.startActivity(intent);
        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
    }
    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }

    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), GestioneSomministratore.class));
        finish();
    }
}

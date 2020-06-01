package it.unimib.bicap;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.HashMap;

import it.unimib.bicap.adapter.ProgettiDaEliminareAdapterRV;
import it.unimib.bicap.databinding.ActivityEliminaSomministratoreBinding;

public class EliminaSomministratore extends AppCompatActivity {

    private ActivityEliminaSomministratoreBinding binding;
    private static final String TAG = "EliminaSomministratore";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        HashMap<String, String> nomiSomm = (HashMap<String, String>) intent.getSerializableExtra("somministratori");
        Log.d(TAG, "Hashmap elimina: " + nomiSomm);

        binding = ActivityEliminaSomministratoreBinding.inflate(getLayoutInflater());
        View v = binding.getRoot();
        setContentView(v);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        binding.rvSomministratoreDaEliminare.setLayoutManager(linearLayoutManager);
        ProgettiDaEliminareAdapterRV progettiAdapter = new ProgettiDaEliminareAdapterRV(getApplicationContext(), nomiSomm, this);
        binding.rvSomministratoreDaEliminare.setAdapter(progettiAdapter);
    }
}

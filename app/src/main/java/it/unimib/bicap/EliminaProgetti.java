package it.unimib.bicap;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.Objects;

import adapter.ProgettiAdapterRV;
import it.unimib.bicap.databinding.ActivityEliminaProgettiBinding;

public class EliminaProgetti extends AppCompatActivity {

    private static final String TAG = "EliminaProgetti";
    private ActivityEliminaProgettiBinding binding;

    String [] nomi = {"Elimina 1", "Elimina 2", "Elimina 3", "Elimina 4"};
    String from = "eliminaProgetti";

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityEliminaProgettiBinding.inflate(getLayoutInflater());
        View v = binding.getRoot();
        setContentView(v);

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        binding.rvProgettiDaEliminare.setLayoutManager(linearLayoutManager);

        ProgettiAdapterRV progettiAdapter = new ProgettiAdapterRV(getApplicationContext(), nomi, from);
        binding.rvProgettiDaEliminare.setAdapter(progettiAdapter);
        binding.rvProgettiDaEliminare.addItemDecoration(new DividerItemDecoration(binding.rvProgettiDaEliminare.getContext(), DividerItemDecoration.VERTICAL));
    }
}

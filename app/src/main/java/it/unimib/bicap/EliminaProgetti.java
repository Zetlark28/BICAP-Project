package it.unimib.bicap;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Objects;

import adapter.ProgettiAdapterRV;

public class EliminaProgetti extends AppCompatActivity {

    RecyclerView mProgettiDaEliminare;

    String [] nomi = {"Elimina 1", "Elimina 2", "Elimina 3", "Elimina 4"};
    String from = "eliminaProgetti";

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elimina_progetti);

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);

        init();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        mProgettiDaEliminare.setLayoutManager(linearLayoutManager);

        ProgettiAdapterRV progettiAdapter = new ProgettiAdapterRV(getApplicationContext(), nomi, from);
        mProgettiDaEliminare.setAdapter(progettiAdapter);
        mProgettiDaEliminare.addItemDecoration(new DividerItemDecoration(mProgettiDaEliminare.getContext(), DividerItemDecoration.VERTICAL));
    }

    private void init() {
        mProgettiDaEliminare = findViewById(R.id.rvProgettiDaEliminare);
    }
}

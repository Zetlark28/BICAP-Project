package it.unimib.bicap;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
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

public class ListaProgetti extends AppCompatActivity {

    RecyclerView mRecyclerView;

    String [] nomi = {"Progetto 1", "Progetto2", "Progetto 3"};
    DividerItemDecoration itemDecor;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_progetti);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        toolbar.setTitle("Lista Progetti Disponibili");
        //TODO: inserire titolo questionario tramite metodo get
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);

        init();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);

        ProgettiAdapterRV progettiAdapter = new ProgettiAdapterRV(getApplicationContext(), nomi);
        mRecyclerView.setAdapter(progettiAdapter);
        mRecyclerView.addItemDecoration(itemDecor);
    }

    private void init() {
        mRecyclerView = findViewById(R.id.rvProgetti);
        itemDecor = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
    }
}

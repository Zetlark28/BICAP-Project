package it.unimib.bicap;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Objects;

public class HomePageSomministratore extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage_somministratore);

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
    }

    public void eliminaProgetto(View view) {
        Intent intentEliminaProgetto = new Intent (this, EliminaProgetti.class);
        startActivity(intentEliminaProgetto);
    }

    public void creaProgetto(View view) {
        Intent intentCreaProgetto = new Intent (this, CreazioneProgetto.class);
        startActivity(intentCreaProgetto);
    }
}

package it.unimib.bicap;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

import it.unimib.bicap.databinding.ActivityHomepageSomministratoreBinding;

// TODO: Creare il menu a discesa coi vari pulsanti tra cui il LOGOUT/ Gestire il login
// TODO: user & psw -> admin admin

public class HomePageSomministratore extends AppCompatActivity {

    private static final String TAG = "HomePageSomministratore";
    private ActivityHomepageSomministratoreBinding binding;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomepageSomministratoreBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        //Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        //Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean fromHome = false;
                Intent intentLogProf = new Intent(getApplicationContext(), LoginProfessore.class);
                intentLogProf.putExtra("fromHome", fromHome);
                startActivity(intentLogProf);
                finish();
            }
        });

        binding.btnElimina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentEliminaProgetto = new Intent (getApplicationContext(), EliminaProgetti.class);
                startActivity(intentEliminaProgetto);
            }
        });

        binding.btnCrea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentCreaProgetto = new Intent (getApplicationContext(), CreazioneProgetto.class);
                startActivity(intentCreaProgetto);
            }
        });
    }
}

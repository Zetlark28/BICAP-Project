package it.unimib.bicap;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import it.unimib.bicap.databinding.ActivityHomepageSomministratoreBinding;

// TODO: Creare il menu a discesa coi vari pulsanti tra cui il LOGOUT/ Gestire il login
// TODO: user & psw -> admin admin

public class HomePageSomministratore extends AppCompatActivity {

    private static final String TAG = "HomePageSomministratore";
    private ActivityHomepageSomministratoreBinding binding;
    private FirebaseAuth mAuth;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        binding = ActivityHomepageSomministratoreBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.main_menu);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menuLogOut){
            mAuth.signOut();
            updateUI();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateUI() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        boolean fromHome = false;

        if (currentUser == null){
            Intent intentLogout = new Intent(this, LoginProfessore.class);
            intentLogout.putExtra("fromHome", fromHome);
            finish();
            startActivity(intentLogout);
        }
    }
}

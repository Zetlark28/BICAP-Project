package it.unimib.bicap;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Objects;

public class CreazioneProgetto extends AppCompatActivity {

    EditText mNome;
    EditText mAutore;
    EditText mDescrizione;

    @SuppressLint("SourceLockedOrientationActivity")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_creazione_progetto);

        init();

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        toolbar.setTitle("Dettagli CreazioneProgetto");
        //TODO: inserire titolo questionario tramite metodo get
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);


    }

    private void init() {
        mNome = findViewById(R.id.etNomeProgetto);
        mAutore = findViewById(R.id.etAutore);
        mDescrizione = findViewById(R.id.etDescrizione);
    }


    public void inserisciDati(View view) {
        if (mNome.getText().toString().equals("")){
            Toast.makeText(this, "Attenzione, manca il nome del progetto !", Toast.LENGTH_LONG).show();
        }
        else if (mAutore.getText().toString().equals("")){
            Toast.makeText(this, "Attenzione, manca l'autore del progetto !", Toast.LENGTH_LONG).show();
        }
        else if (mDescrizione.getText().toString().equals("")){
            Toast.makeText(this, "Attenzione, manca la descrizione del progetto !", Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(this, "Bravo", Toast.LENGTH_LONG).show();
        }
    }
}

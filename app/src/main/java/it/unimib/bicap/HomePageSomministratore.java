package it.unimib.bicap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import it.unimib.bicap.databinding.ActivityHomepageSomministratoreBinding;

// TODO: Creare il menu a discesa coi vari pulsanti tra cui il LOGOUT/ Gestire il login
// TODO: user & psw -> admin admin

public class HomePageSomministratore extends AppCompatActivity {
    private static final int ONE_MB = 1024 * 1024;
    private static JSONObject progetti;
    private static JSONArray progettiAutore;
    private static JSONArray progettiDaSelezionare;


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
        toolbar.setTitle("Homepage Somministratore");
        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.main_menu);


        final String autore = getSharedPreferences("author", Context.MODE_PRIVATE).getString("autore", null);
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        StorageReference ref = mStorageRef.child("/Progetti/progetti.json");
        ref.getBytes(ONE_MB).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                String json;
                try {
                    json = new String(bytes, "UTF-8");
                    Log.d("kek", json.toString());
                    progetti = new JSONObject(json);
                    SharedPreferences sharedPref = getSharedPreferences("author", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("file",progetti.toString());
                    editor.commit();
                    progettiDaSelezionare = progetti.getJSONArray("progetti");
                    progettiAutore = new JSONArray();
                    for (int i = 0; i < progettiDaSelezionare.length(); i++) {
                        if (progettiDaSelezionare.getJSONObject(i).getString("autore").equals(autore)) {
                            progettiAutore.put(progettiDaSelezionare.getJSONObject(i));
                        }
                    }
                    editor.putString("progettiTot", progettiAutore.toString());
                    editor.commit();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });

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
                intentEliminaProgetto.putExtra("listaProgettiAutore", progettiAutore.toString());
                intentEliminaProgetto.putExtra("listaProgetti", progetti.toString());
                startActivity(intentEliminaProgetto);
            }
        });


        // TODO: Progress bar al download dei progetti
        binding.btnCrea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (progettiAutore != null) {
                    Intent intentCreaProgetto = new Intent(getApplicationContext(), CreazioneProgetto.class);
                    intentCreaProgetto.putExtra("progetti", progettiDaSelezionare.toString());
                    startActivity(intentCreaProgetto);
                } else {
                    Snackbar.make(v, "Attento, è in corso un processo interno!", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);


        /*MenuItem itemLogOut = menu.getItem(0);
        SpannableString sLogOut = new SpannableString("LogOut");
        sLogOut.setSpan(new ForegroundColorSpan(Color.BLACK), 0, sLogOut.length(), 0);
        itemLogOut.setTitle(sLogOut);

        MenuItem itemGuide = menu.getItem(1);
        SpannableString sGuide = new SpannableString("Scarica la Guida");
        sGuide.setSpan(new ForegroundColorSpan(Color.BLACK), 0, sGuide.length(), 0);
        itemGuide.setTitle(sGuide);*/

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menuLogOut){
            mAuth.signOut();
            updateUI();
            return true;
        }
        else if (item.getItemId() == R.id.menuDownload){
            
        }
        return super.onOptionsItemSelected(item);
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

    private void updateUI() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        boolean fromHome = false;

        if (currentUser == null){
            Intent intentLogout = new Intent(this, LoginProfessore.class);
            intentLogout.putExtra("fromHome", fromHome);
            if(fromHome == false){
                startActivity(intentLogout);
                finish();
            }else{
                startActivity(intentLogout);
            }
        }
    }
}

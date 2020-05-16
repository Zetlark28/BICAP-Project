package it.unimib.bicap;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Objects;

import adapter.ProgettiAdapterRV;
import it.unimib.bicap.databinding.ActivityEliminaProgettiBinding;
import it.unimib.bicap.service.GetterInfo;
import it.unimib.bicap.service.GetterLocal;

public class EliminaProgetti extends AppCompatActivity {

    private GetterInfo getterInfo = new GetterLocal();
    private static final String TAG = "EliminaProgetti";
    private ActivityEliminaProgettiBinding binding;
    private StorageReference mStorageRef;
    private StorageReference ref;
    private static final int ONE_MB = 1024 * 1024;
    private static JSONArray progetti;
    private Button bottone;

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

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentHomeProfessore = new Intent(getApplicationContext(), HomePageSomministratore.class);
                startActivity(intentHomeProfessore);
                finish();
            }
        });

        //TODO: nome autore da settare correttamente
        final String nomeAutore = "Nome autore";
        mStorageRef = FirebaseStorage.getInstance().getReference();
        ref = mStorageRef.child("/Progetti/progetti.json");
        ref.getBytes(ONE_MB).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                String json = null;
                try {
                    json = new String(bytes, "UTF-8");
                    JSONObject progettiToParse = new JSONObject(json);
                    progetti = progettiToParse.getJSONArray("progetti");
                    JSONArray  progettiAutore = new JSONArray();
                    for(int i=0; i<progetti.length(); i++){
                        if(progetti.getJSONObject(i).getString("autore").equals(nomeAutore))
                            progettiAutore.put(progetti.getJSONObject(i));
                    }

                    List<String> nomiProgetti = getterInfo.getNomiProgetti(progettiAutore);


                    nomi = nomiProgetti.toArray(new String[nomiProgetti.size()]);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                    binding.rvProgettiDaEliminare.setLayoutManager(linearLayoutManager);
                    ProgettiAdapterRV progettiAdapter = new ProgettiAdapterRV(getApplicationContext(), nomi, from);
                    binding.rvProgettiDaEliminare.setAdapter(progettiAdapter);
                    binding.rvProgettiDaEliminare.addItemDecoration(new DividerItemDecoration(binding.rvProgettiDaEliminare.getContext(), DividerItemDecoration.VERTICAL));

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("errore", "json not parse");
            }
        });


    }


}

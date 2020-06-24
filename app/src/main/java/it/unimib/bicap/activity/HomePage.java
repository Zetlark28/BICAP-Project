package it.unimib.bicap.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import it.unimib.bicap.R;
import it.unimib.bicap.activity.somministratore.LoginProfessore;
import it.unimib.bicap.activity.utente.ListaProgetti;
import it.unimib.bicap.constanti.ActivityConstants;
import it.unimib.bicap.databinding.ActivityHomepageBinding;

public class HomePage extends AppCompatActivity {

    private ActivityHomepageBinding binding;
    private boolean esisteMail;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser utenteCorrenteFirebase = mAuth.getCurrentUser() ;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("utenti");
    SharedPreferences sharedPref;
    private ProgressDialog dialog;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomepageBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        sharedPref = getSharedPreferences(ActivityConstants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);

        dialog = new ProgressDialog(this);
        dialog.setTitle("Caricamento in corso");
        dialog.setMessage("Attendere...");
        dialog.setCancelable(false);
        dialog.show();

        final SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove("esisteMail");
        editor.apply();
        editor.remove("sommAttivi");
        editor.apply();

        if (utenteCorrenteFirebase != null){
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot d : dataSnapshot.getChildren()){
                        String attivo = (String) d.child("attivo").getValue();
                        String email = (String) d.child("email").getValue();
                        if (attivo != null && email != null && attivo.equals("true") && email.equals(utenteCorrenteFirebase.getEmail())){
                            esisteMail = true;
                            editor.putBoolean("esisteMail", esisteMail);
                            editor.commit();
                        }
                    }
                    dialog.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }

        leggiSomministratori();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        toolbar.setTitle("SurveyMiB");
        toolbar.setNavigationIcon(null);

        binding.btnProf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentLogInProf = new Intent(getApplicationContext(), LoginProfessore.class);
                intentLogInProf.putExtra(ActivityConstants.INTENT_FROM_HOME, true);
                startActivity(intentLogInProf);
                finish();
            }
        });

        binding.btnQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentQuiz = new Intent(getApplicationContext(), ListaProgetti.class);
                startActivity(intentQuiz);
                finish();
            }
        });
    }


    private void leggiSomministratori() {
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> sommAttivi = new ArrayList<>();
                for (DataSnapshot d : dataSnapshot.getChildren()){
                    String attivo = (String) d.child("attivo").getValue();
                    String email = (String) d.child("email").getValue();
                    if (attivo != null && attivo.equals("true") && email != null){
                        sommAttivi.add(email);
                    }
                }
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i<sommAttivi.size();i++){
                    sb.append(sommAttivi.get(i)).append(",");
                }
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("sommAttivi", sb.toString());
                editor.apply();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public void startActivity(Intent intent){
        super.startActivity(intent);
        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        this.finishAffinity();
    }
}

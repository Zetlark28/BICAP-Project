package it.unimib.bicap.activity.somministratore;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import it.unimib.bicap.R;
import it.unimib.bicap.activity.HomePage;
import it.unimib.bicap.constanti.ActivityConstants;
import it.unimib.bicap.databinding.ActivityLoginProfessoreBinding;
import it.unimib.bicap.exception.LoginSomministratoreException;

public class LoginSomministratore extends AppCompatActivity {

    private ActivityLoginProfessoreBinding binding;
    private FirebaseAuth mAuth;
    private ProgressDialog dialogCaricamento;
    FirebaseUser utenteCorrente;
    private List<String> sommAttivi = new ArrayList<>();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("utenti");

    @Override
    public void onStart() {
        super.onStart();
        utenteCorrente = mAuth.getCurrentUser();

        final SharedPreferences sharedPref = getSharedPreferences("author", Context.MODE_PRIVATE);

        String sb = sharedPref.getString("sommAttivi", null);

        if (sb != null) {
            sommAttivi = Arrays.asList(sb.split(","));
        }

        updateUI(utenteCorrente);
    }

    @SuppressLint("CommitPrefEdits")
    private void updateUI(final FirebaseUser currentUser) {
        this.getSharedPreferences(ActivityConstants.SHARED_PREFERENCE_NAME, 0).edit().remove("autore");
        if (currentUser != null) {
            String email = currentUser.getEmail();
            controllaEmail(email);

            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String autore = (String) dataSnapshot.child(currentUser.getUid()).child("autore").getValue();
                        SharedPreferences.Editor editor = getSharedPreferences("author", Context.MODE_PRIVATE).edit();
                        editor.putString("autore", autore);
                        editor.apply();
                        }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @SuppressLint("SourceLockedOrientationActivity")
        @Override
        protected void onCreate (@Nullable Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            binding = ActivityLoginProfessoreBinding.inflate(getLayoutInflater());
            View view = binding.getRoot();
            setContentView(view);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            dialogCaricamento = new ProgressDialog(this);
            dialogCaricamento.setTitle("Caricamento");
            dialogCaricamento.setMessage("Attendere...");
            dialogCaricamento.setCancelable(false);

            Toolbar toolbar = findViewById(R.id.toolbar_main);
            toolbar.setTitle("");
            setSupportActionBar(toolbar);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intentHome = new Intent(getApplicationContext(), HomePage.class);
                    startActivity(intentHome);
                    finish();
                    overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                }
            });

            mAuth = FirebaseAuth.getInstance();

            binding.imAccedi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (binding.etEmail.getText() == null || binding.etPassword.getText() == null){
                        throw LoginSomministratoreException.LOGIN_VIEW_FAIL;
                    }
                    String email = binding.etEmail.getText().toString();
                    String password = binding.etPassword.getText().toString();

                    if (email.equals("")) {
                        Snackbar.make(v, "Attenzione, email non valida", Snackbar.LENGTH_SHORT).show();
                    } else if (password.equals("")) {
                        Snackbar.make(v, "Attenzione, password non valida", Snackbar.LENGTH_SHORT).show();
                    } else {
                        dialogCaricamento.show();
                        loginUser(email, password);
                    }
                }
            });

            binding.tvPassDimenticata.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intentPassDimenticata = new Intent(getApplicationContext(), RecuperaPassword.class);
                    startActivity(intentPassDimenticata);
                    finish();
                }
            });
        }

        private void loginUser (final String email, String password){
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                            } else {
                                dialogCaricamento.dismiss();
                                Snackbar.make(binding.linearlayout, "Attenzione, credenziali non valide !", Snackbar.LENGTH_SHORT).show();
                                updateUI(null);
                            }
                        }
                    });
            }

    @Override
    public void startActivity(Intent intent){
        super.startActivity(intent);
        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
    }

    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), HomePage.class));
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        finish();

    }

    public void controllaEmail (final String email){
        if (sommAttivi.contains(email)){
            Intent intentLogged = new Intent(getApplicationContext(), HomePageSomministratore.class);
            intentLogged.putExtra(ActivityConstants.INTENT_EMAIL, email);
            startActivity(intentLogged);
            finish();
        } else{
            dialogCaricamento.dismiss();
            mostraDialogUtenteNotAttivo();
        }
    }

    private void mostraDialogUtenteNotAttivo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Attenzzione");
        if (binding.etEmail.getText() == null)
            throw LoginSomministratoreException.LOGIN_VIEW_FAIL;
        builder.setMessage("Il somministratore con email: " + binding.etEmail.getText().toString() + "\n è stato disattivato.");
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        mAuth.signOut();
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}



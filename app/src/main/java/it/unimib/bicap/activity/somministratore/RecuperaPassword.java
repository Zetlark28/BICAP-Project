package it.unimib.bicap.activity.somministratore;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import it.unimib.bicap.activity.HomePage;
import it.unimib.bicap.R;
import it.unimib.bicap.constanti.ActivityConstants;
import it.unimib.bicap.databinding.ActivityRecuperaPasswordBinding;

public class RecuperaPassword extends AppCompatActivity {

    private ActivityRecuperaPasswordBinding binding;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRecuperaPasswordBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        toolbar.setTitle("Recupero Password");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(null);

        progressDialog = new ProgressDialog(this);

        binding.tvAccedi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent logInSomministratore = new Intent(getApplicationContext(), LoginProfessore.class);
                logInSomministratore.putExtra(ActivityConstants.INTENT_FROM_HOME, true);
                startActivity(logInSomministratore);
                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                finish();
            }
        });

        binding.btnRecuperaPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.etEmailRecuperata.getText().toString();
                if (email.equals("")){
                    Snackbar.make(v, "Attenzione, il testo inserito non è valido", Snackbar.LENGTH_SHORT).show();
                } else{
                    progressDialog.setMessage("Caricamento...");
                    progressDialog.show();
                    mandaEmail(email);
                }
            }
        });
    }

    private void mandaEmail(String email) {
        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()){
                    //Toast.makeText(getApplicationContext(), "Password mandata via email", Toast.LENGTH_SHORT).show();
                    Snackbar.make(findViewById(android.R.id.content), "Password mandata via email", Snackbar.LENGTH_SHORT).show();
                } else {
                    //Toast.makeText(getApplicationContext(), "Email Errata", Toast.LENGTH_SHORT).show();
                    Snackbar.make(findViewById(android.R.id.content), "Email Errata", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), HomePage.class));
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        finish();

    }
}

package it.unimib.bicap;

import android.annotation.SuppressLint;
import android.content.Intent;
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

import java.util.Objects;

import it.unimib.bicap.databinding.ActivityLoginProfessoreBinding;

public class LoginProfessore extends AppCompatActivity {

    private static final String TAG = "LoginProfessore";
    private ActivityLoginProfessoreBinding binding;
    private FirebaseAuth mAuth;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        boolean fromHome = getIntent().getExtras().getBoolean("fromHome");

        if (user != null) {
            if (fromHome) {
                Intent intentHomeProf = new Intent(this, HomePageSomministratore.class);
                intentHomeProf.putExtra("fromHome", fromHome);
                startActivity(intentHomeProf);
                finish();
            } else if (!fromHome) {
                Intent intentHome = new Intent(this, HomePage.class);
                startActivity(intentHome);
                finish();
            } else {
                String email = user.getEmail();

                Intent intentLogged = new Intent(this, HomePageSomministratore.class);
                intentLogged.putExtra("Email", email);
                startActivity(intentLogged);
            }
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

            Toolbar toolbar = findViewById(R.id.toolbar_main);
            toolbar.setTitle("Area Professore");
            setSupportActionBar(toolbar);

            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);

            mAuth = FirebaseAuth.getInstance();

            binding.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String email = binding.etEmail.getText().toString();
                    String password = binding.etPassword.getText().toString();

                    if (email.equals("")) {
                        Snackbar.make(v, "Attenzione, email non valida", Snackbar.LENGTH_SHORT).show();
                    } else if (password.equals("")) {
                        Snackbar.make(v, "Attenzione, password non valida", Snackbar.LENGTH_SHORT).show();
                    } else
                        loginUser(email, password);
                    //Email: admin@admin.com --> Password: BicapAdmin
                }
            });
        }

        private void loginUser (String email, String password){
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.i(TAG, "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.i(TAG, "createUserWithEmail:failure", task.getException());
                                Snackbar.make(binding.constraintLayout, "Attenzione, credenziali non valide !", Snackbar.LENGTH_SHORT).show();
                                updateUI(null);
                            }
                        }
                    });
        }

}

//TODO: Email: admin@admin.com Password:alessio



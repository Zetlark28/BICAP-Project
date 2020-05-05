package it.unimib.bicap;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Objects;

import it.unimib.bicap.databinding.ActivityLoginProfessoreBinding;

public class LoginProfessore extends AppCompatActivity {

    private static final String TAG = "LoginProfessore";
    private ActivityLoginProfessoreBinding binding;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
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

        binding.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAccesso = new Intent(getApplicationContext(), HomePageSomministratore.class);
                startActivity(intentAccesso);
            }
        });
    }
}

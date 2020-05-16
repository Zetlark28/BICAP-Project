package it.unimib.bicap;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import it.unimib.bicap.databinding.ActivityHomepageBinding;

public class HomePage extends AppCompatActivity {

    private static final String TAG = "HomePage";
    private ActivityHomepageBinding binding;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomepageBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        toolbar.setTitle("SurveyMiB");
        toolbar.setNavigationIcon(null);

        binding.btnProfessore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentLogInProf = new Intent(getApplicationContext(), LoginProfessore.class);
                intentLogInProf.putExtra("fromHome", true);
                startActivity(intentLogInProf);
                overridePendingTransition(R.anim.slide_on_right, R.anim.slide_out_left);
            }
        });

        binding.btnQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentQuiz = new Intent(getApplicationContext(), ListaProgetti.class);
                startActivity(intentQuiz);
                overridePendingTransition(R.anim.slide_on_right, R.anim.slide_out_left);
            }
        });
    }
}

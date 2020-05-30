package it.unimib.bicap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;

import it.unimib.bicap.databinding.ActivityHomepage2Binding;
import it.unimib.bicap.databinding.ActivityHomepageBinding;

public class HomePage extends AppCompatActivity {

    private static final String TAG = "HomePage";
    private ActivityHomepageBinding binding;
    private boolean esisteMail;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentFirebaseUser = mAuth.getCurrentUser() ;
    SharedPreferences sharedPref;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomepageBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        sharedPref = getSharedPreferences("author", Context.MODE_PRIVATE);

        if (currentFirebaseUser != null){
            String email = currentFirebaseUser.getEmail();
            mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                @Override
                public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                    Log.d(TAG,"numero utenti con la mail: "+task.getResult().getSignInMethods().size());
                    if (task.getResult().getSignInMethods().size() == 0) {
                        esisteMail = false;
                    } else{
                        esisteMail = true;
                    }
                    SharedPreferences.Editor editor = sharedPref.edit();

                    editor.putBoolean("esisteMail", esisteMail);
                    editor.commit();
                    Log.d(TAG, "Esiste mail Serio: " + esisteMail);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                }
            });
        }

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        toolbar.setTitle("SurveyMiB");
        toolbar.setNavigationIcon(null);

        binding.btnProf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentLogInProf = new Intent(getApplicationContext(), LoginProfessore.class);
                intentLogInProf.putExtra("fromHome", true);
                startActivity(intentLogInProf);
            }
        });

        binding.btnQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentQuiz = new Intent(getApplicationContext(), ListaProgetti.class);
                startActivity(intentQuiz);
            }
        });
    }

    //override startActivity con animazione slide avanti
    @Override
    public void startActivity(Intent intent){
        super.startActivity(intent);
        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
    }


}

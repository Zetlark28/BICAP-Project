package it.unimib.bicap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import it.unimib.bicap.databinding.ActivityHomepageBinding;

public class HomePage extends AppCompatActivity {

    private static final String TAG = "HomePage";
    private ActivityHomepageBinding binding;
    private boolean esisteMail;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentFirebaseUser = mAuth.getCurrentUser() ;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("utenti");
    SharedPreferences sharedPref;
    private String key = "";

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomepageBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        sharedPref = getSharedPreferences("author", Context.MODE_PRIVATE);

        if (currentFirebaseUser != null){
            final String idUser = currentFirebaseUser.getUid();
            // Read from the database
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    for (DataSnapshot d : dataSnapshot.getChildren()){
                        key = d.getKey();
                        if (key.equals(idUser)){
                            esisteMail = true;
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putBoolean("esisteMail", esisteMail);
                            editor.commit();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException());
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

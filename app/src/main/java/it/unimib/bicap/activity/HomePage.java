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

    private static final String TAG = "HomePage";
    private ActivityHomepageBinding binding;
    private boolean esisteMail;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentFirebaseUser = mAuth.getCurrentUser() ;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("utenti");
    SharedPreferences sharedPref;
    private String key = "";
    private boolean valore = false;
    private ProgressDialog dialog;
    private CountDownTimer cTimer;

    //alertDialog Homepage
    /*public void showAlertDialog(final Context context, String title, String message,
                                Boolean status) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Caricamento in corso")
                .setMessage("Attendere...")
                .setCancelable(false)
                .create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            private static final int AUTO_DISMISS_MILLIS = 3000;
            @Override
            public void onShow(final DialogInterface dialog) {
                new CountDownTimer(AUTO_DISMISS_MILLIS, 100) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }
                    @Override
                    public void onFinish() {
                        if (((AlertDialog) dialog).isShowing()) {
                            dialog.dismiss();
                        }
                    }
                }.start();
            }
        });
        dialog.show();
    }*/

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomepageBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        sharedPref = getSharedPreferences("author", Context.MODE_PRIVATE);

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

        if (currentFirebaseUser != null){
            Log.d(TAG, "utente: " + currentFirebaseUser.getEmail());
            //final String idUser = currentFirebaseUser.getUid();
            //final CountDownLatch latch = new CountDownLatch(1);
            // Read from the database
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    for (DataSnapshot d : dataSnapshot.getChildren()){
                        Log.d(TAG, "prova");
                        //Log.d(TAG, "email ciclo: " + d.child("email").getValue().toString() + " attivo? " + d.child("attivo").getValue().toString());
                        key = d.getKey();
                        Log.d(TAG, "email somm: " + currentFirebaseUser.getEmail());
                        if (d.child("attivo").getValue() != null && d.child("email").getValue() != null && d.child("attivo").getValue().equals("true") && d.child("email").getValue().equals(currentFirebaseUser.getEmail())){
                            Log.d(TAG, "entro email trovata");
                            esisteMail = true;
                            Log.d(TAG, "email: " + d.child("email").getValue().toString() + ", attivo: " + d.child("attivo").getValue().toString());
                            editor.putBoolean("esisteMail", esisteMail);
                            editor.commit();
                        }
                    }
                    dialog.dismiss();
                    //latch.countDown();
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException());
                }
            });
           /* try {
                //latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
        }

        valore = leggiSomministratori();

        while(!valore){
            binding.btnProf.setEnabled(false);
        }

        //binding.btnProf.setEnabled(true);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        toolbar.setTitle("SurveyMiB");
        toolbar.setNavigationIcon(null);

        final HomePage instance = this;

        binding.btnProf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentLogInProf = new Intent(getApplicationContext(), LoginProfessore.class);
                intentLogInProf.putExtra(ActivityConstants.INTENT_FROM_HOME, true);
                instance.finish();
                startActivity(intentLogInProf);
            }
        });

        binding.btnQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentQuiz = new Intent(getApplicationContext(), ListaProgetti.class);
                startActivity(intentQuiz);
                instance.finish();
            }
        });

        //showAlertDialog(this, "ciao","mamma", true);
    }


    private boolean leggiSomministratori() {
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> sommAttivi = new ArrayList<>();
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (DataSnapshot d : dataSnapshot.getChildren()){
                    if (d.child("attivo").getValue() != null && d.child("attivo").getValue().equals("true")){
                        sommAttivi.add(d.child("email").getValue().toString());
                    }
                }
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i<sommAttivi.size();i++){
                    sb.append(sommAttivi.get(i)).append(",");
                }
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("sommAttivi", sb.toString());
                editor.commit();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        return true;
    }

    //override startActivity con animazione slide avanti
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

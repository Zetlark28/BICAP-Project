package it.unimib.bicap;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import it.unimib.bicap.adapter.SectionsPagerAdapter;
import it.unimib.bicap.constanti.ActivityConstants;

public class ListaProgetti extends AppCompatActivity {

    private static final String TAG = "ListaProgetti";
    private static final int ONE_MB = 1024 * 1024;
    private  JSONObject progettiTot;
    ProgressDialog progressDialog;


    private SectionsPagerAdapter mSectionsPageAdapter;

    private ViewPager myViewPager;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progetti_utente);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        Toolbar toolbar = findViewById(R.id.toolbar_main);
        toolbar.setTitle(ActivityConstants.LISTA_PROGETTI_TOOLBAR_TITLE);
        setSupportActionBar(toolbar);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Attendi");
        progressDialog.setProgress(10);
        progressDialog.setMax(100);
        progressDialog.setMessage("Caricamento Progetti");
        new DownloadProgettiTask().execute();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentHome = new Intent(getApplicationContext(), HomePage.class);
                startActivity(intentHome);
                finish();
                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
            }
        });

        //Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);

        mSectionsPageAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        myViewPager = findViewById(R.id.project_view_pager);
        setUpViewPager(myViewPager);

        TabLayout tabLayout = findViewById(R.id.project_tab);
        tabLayout.setupWithViewPager(myViewPager);
        

    }

    private void setUpViewPager(final ViewPager viewPager){
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        StorageReference ref = mStorageRef.child(ActivityConstants.FIREBASE_STORAGE_CHILD_PROGETTI);
        ref.getBytes(ONE_MB).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                try {
                    String json = new String(bytes, "UTF-8");
                    progettiTot = new JSONObject(json);
                    SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
                    adapter.addFragment(new QuestionariDaFare(progettiTot), "SURVEY ATTIVI");
                    adapter.addFragment(new QuestionariDaTerminare(progettiTot), "SURVEY SOSPESI");
                    viewPager.setAdapter(adapter);
                    progressDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    //override startActivity con animazione slide avanti
    @Override
    public void startActivity(Intent intent){
        super.startActivity(intent);
        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
    }

    public class DownloadProgettiTask extends AsyncTask<Void, Void, Void> {
        public void onPreExecute() {
            progressDialog.show();
        }
        public Void doInBackground(Void... unused) {
            return null;
        }
    }

    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), HomePage.class));
        finish();

    }

}

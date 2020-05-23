package it.unimib.bicap;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.tabs.TabLayout;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

import adapter.SectionsPagerAdapter;

public class ListaProgetti extends AppCompatActivity {

    private static final String TAG = "ListaProgetti";

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

        //Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);

        mSectionsPageAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        myViewPager = findViewById(R.id.project_view_pager);
        setUpViewPager(myViewPager);

        TabLayout tabLayout = findViewById(R.id.project_tab);
        tabLayout.setupWithViewPager(myViewPager);
        

    }

    private void setUpViewPager(ViewPager viewPager){
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new QuestionariDaFare(), "QUESTIONARI DISPONIBILI");
        adapter.addFragment(new QuestionariDaTerminare(), "QUESTIONARI DA FINIRE");
        viewPager.setAdapter(adapter);
    }

    //override startActivity con animazione slide avanti
    @Override
    public void startActivity(Intent intent){
        super.startActivity(intent);
        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
    }

}

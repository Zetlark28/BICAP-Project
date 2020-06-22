package it.unimib.bicap.activity.somministratore;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.SyncStateContract;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import it.unimib.bicap.activity.HomePage;
import it.unimib.bicap.R;
import it.unimib.bicap.constanti.ActivityConstants;

public class LaunchScreen extends AppCompatActivity {
    Handler handler;
    private static final int ONE_MB = 1024 * 1024;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_screen);

        SharedPreferences sharedPref = getSharedPreferences(ActivityConstants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);

        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        StorageReference ref = mStorageRef.child(ActivityConstants.FIREBASE_STORAGE_CHILD_PROGETTI);
        ref.getBytes(ONE_MB).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                String json = null;
                try {
                    json = new String(bytes, "UTF-8");
                    JSONObject progetti = new JSONObject(json);
                    SharedPreferences sharedPref = getSharedPreferences(ActivityConstants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("file",progetti.toString());
                    editor.commit();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        /*final int flags;

        int currentAPIVersion = Build.VERSION.SDK_INT;

        if(currentAPIVersion >= Build.VERSION_CODES.KITKAT){
            flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            getWindow().getDecorView().setSystemUiVisibility(flags);
        }else{
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }*/

        handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(LaunchScreen.this, HomePage.class);
                startActivity(intent);
                finish();
            }
        },3750);

    }

    //override startActivity con animazione slide avanti
    @Override
    public void startActivity(Intent intent){
        super.startActivity(intent);
        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
    }


}

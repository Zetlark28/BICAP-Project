package it.unimib.bicap.service;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.unimib.bicap.EliminaProgetti;
import it.unimib.bicap.EliminaSomministratore;
import it.unimib.bicap.ExampleItem;
import it.unimib.bicap.adapter.ProgettiDaEliminareAdapterRV;

public class EliminaDialogSomministratore extends AppCompatDialogFragment{

    private final String TAG = "EliminaDialogSomministratore";
    private Integer index;
    private String key;
    private List<ExampleItem> exampleList;
    private ProgettiDaEliminareAdapterRV progettiAdapterSommRV;
    private EliminaSomministratore activityDelSomm;
    private String message;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("utenti");


    @SuppressLint("LongLogTag")
    public EliminaDialogSomministratore(List<ExampleItem> exampleList, String key, Integer index, ProgettiDaEliminareAdapterRV istanzaProgettiDaEliminareAdapterRV, EliminaSomministratore eliminaActivity, String message){
       //super(eliminaActivity);
        this.exampleList = exampleList;
        for (ExampleItem item : exampleList){
            Log.d(TAG, "nome: " + item.getTextNome() + ", email: " + item.getTextEmail());
        }
        this.key = key;
        this.index = index;
        this.progettiAdapterSommRV = istanzaProgettiDaEliminareAdapterRV;
        this.activityDelSomm = eliminaActivity;
        this.message = message;
    }

    @NonNull
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activityDelSomm);
        builder.setTitle("Elimina")
                .setMessage(message)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String autore = exampleList.get(index).getTextNome();
                        final String email = exampleList.get(index).getTextEmail();
                        final List<ExampleItem> exampleListNew = new ArrayList<>();
                        myRef.addValueEventListener(new ValueEventListener() {
                            @SuppressLint("LongLogTag")
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot d : dataSnapshot.getChildren()) {
                                    if (d.child("email").getValue() != null && d.child("autore").getValue() != null && d.child("attivo").getValue() != null) {
                                        if (d.child("autore").getValue().equals(autore) && d.child("email").getValue().equals(email)) {
                                            exampleList.remove(index);
                                            Log.d(TAG, "exampleList: " + exampleList.toString());
                                            HashMap<String, Object> map = new HashMap<>();
                                            map.put("attivo", "false");
                                            d.getRef().updateChildren(map);
                                        } else {
                                            if (d.child("attivo").getValue().equals("true") && !d.child("email").getValue().equals("admin@admin.com")) {
                                                            /*String autore1 = d.child("autore").getValue().toString();
                                                            Log.d(TAG, "autore New: " + autore1);
                                                            String email1 = d.child("email").getValue().toString();
                                                            Log.d(TAG, "email New: " + email1);*/
                                                boolean valore = false;
                                                for (ExampleItem item : exampleListNew) {
                                                    if (item.getTextNome().equals(d.child("autore").getValue().toString()) && item.getTextEmail().equals(d.child("email").getValue().toString())) {
                                                        valore = true;
                                                    }
                                                }
                                                if (!valore)
                                                    exampleListNew.add(new ExampleItem(d.child("autore").getValue().toString(), d.child("email").getValue().toString()));
                                            }
                                        }
                                    }
                                }
                                Log.d(TAG, "exampleListNew: " + exampleListNew.toString());
                                for (ExampleItem item : exampleListNew) {
                                    Log.d(TAG, "new List: " + "Nome: " + item.getTextNome() + ", Email: " + item.getTextEmail());
                                }
                                HashMap<String, String> map = new HashMap<>();
                                for (ExampleItem item : exampleListNew) {
                                    map.put(item.getTextEmail(), item.getTextNome());
                                }
                                Intent intentPazzo = new Intent(activityDelSomm, EliminaSomministratore.class);
                                intentPazzo.putExtra("pazzi", map);
                                intentPazzo.putExtra("home", false);
                                activityDelSomm.startActivity(intentPazzo);
                                activityDelSomm.finish();
                                            /*progettiAdapterSommRV.setExampleList(exampleListNew);
                                            progettiAdapterSommRV.setExampleListFull(exampleListNew);
                                            //progettiAdapterSommRV.notifyItemRemoved(index);
                                            progettiAdapterSommRV.notifyDataSetChanged();
                                            dismiss();*/
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        return  builder.create();
    }
}

package it.unimib.bicap.service;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import it.unimib.bicap.ItemSearch;
import it.unimib.bicap.R;
import it.unimib.bicap.activity.somministratore.EliminaProgetti;
import it.unimib.bicap.activity.somministratore.EliminaSomministratore;
import it.unimib.bicap.activity.somministratore.GestioneSomministratore;
import it.unimib.bicap.adapter.EliminaAdapterRV;
import it.unimib.bicap.constanti.ActivityConstants;

public class EliminaDialog extends AppCompatDialogFragment {

    private JSONArray listaProgetti;
    private Integer index;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("utenti");
    private JSONObject listaProgettiTot;
    private EliminaProgetti activity;
    private EliminaSomministratore activityDelSomm;
    private GetterInfo getterInfo = new GetterLocal();
    private String message;
    private List<ItemSearch> exampleList;
    private final String TAG = "EliminaDialog";


    public EliminaDialog(List<ItemSearch> exampleList, JSONArray listaProgetti, JSONObject listaProgettiTot, Integer index, EliminaAdapterRV istanzaProgettiAdapter, EliminaProgetti eliminaActivity, String message){
            this.exampleList = exampleList;
            this.listaProgetti=listaProgetti;
            this.index = index;
            this.listaProgettiTot = listaProgettiTot;
            this.activity = eliminaActivity;
            this.message = message;
    }


    public EliminaDialog(List<ItemSearch> exampleList, String key, Integer index, EliminaAdapterRV istanzaProgettiDaEliminareAdapterRV, EliminaSomministratore eliminaActivity, String message){
        this.exampleList = exampleList;
        this.index = index;
        this.activityDelSomm = eliminaActivity;
        this.message = message;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Elimina")
                .setMessage(message)
                .setPositiveButton(R.string.dialog_positive, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (activityDelSomm == null) {
                                    JSONArray listaNuova = new JSONArray();
                                    JSONObject nuoviProgetti = null;
                                    Integer idElimina = null;
                                    //primo for serve  per identificare il json da eliminare all'interno della lista intera di progetti
                                    //secondo for serve per inizializzare una nuova lista di jsonObject che l'utente vedrà senza inserire il json identificato precedentemente
                                    try {
                                        for (int i = 0; i < listaProgetti.length(); i++) {
                                            if (i != index) {
                                                listaNuova.put(listaProgetti.getJSONObject(i));
                                            } else
                                                idElimina = listaProgetti.getJSONObject(i).getInt("id");
                                        }
                                        JSONArray lista = listaProgettiTot.getJSONArray("progetti");
                                        JSONArray nuovaListaTotProgetti = new JSONArray();
                                        for (int j = 0; j < lista.length() &&  idElimina!=null; j++) {
                                            int idnuovo = lista.getJSONObject(j).getInt("id");
                                            if (idnuovo != idElimina) {
                                                nuovaListaTotProgetti.put(lista.getJSONObject(j));
                                            }
                                        }
                                        //crea un nuovo jsonObject dove inserisce il JsonArray ricavato precedentemente
                                        nuoviProgetti = new JSONObject();
                                        nuoviProgetti.put("progetti", nuovaListaTotProgetti);
                                        Log.d("oggetto", nuoviProgetti.toString());
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    Intent intentDelProj = new Intent(getContext(), EliminaProgetti.class);
                                    intentDelProj.putExtra(ActivityConstants.INTENT_RETURN, true);
                                    intentDelProj.putExtra(ActivityConstants.INTENT_NEW_LIST_AUTORE, listaNuova.toString());
                                    intentDelProj.putExtra(ActivityConstants.INTENT_NEW_LIST_TOT, listaProgettiTot.toString());
                                    showAlertDialog();
                                    Utility.write(nuoviProgetti, activity, null);
                                    dismiss();
                                    startActivity(intentDelProj);
                                    activity.overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                                    Log.d(TAG, "eliminato");
                                }
                                else{
                                    final String autore = exampleList.get(index).getTextNome();
                                    final String email = exampleList.get(index).getTextEmail();
                                    final List<ItemSearch> exampleListNew = new ArrayList<>();
                                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot d : dataSnapshot.getChildren()){
                                                String autoreFirebase = (String) d.child("autore").getValue();
                                                String emailFirebase = (String) d.child("email").getValue();
                                                String attivoFirebase = (String) d.child("attivo").getValue();
                                                if(emailFirebase!= null && autoreFirebase!=null &&attivoFirebase!=null) {
                                                    if (autoreFirebase.equals(autore) && emailFirebase.equals(email)) {
                                                        exampleList.remove(index.intValue());
                                                        HashMap<String, Object> map = new HashMap<>();
                                                        map.put("attivo", "false");
                                                        d.getRef().updateChildren(map);
                                                    } else {
                                                        if (attivoFirebase.equals("true") && !emailFirebase.equals(ActivityConstants.EMAIL_ADMIN) &&
                                                                !emailFirebase.equals(ActivityConstants.EMAIL_PROF) && !emailFirebase.equals(ActivityConstants.AUTHORIZED_EMAIL)) {
                                                            boolean valore = false;
                                                            for(ItemSearch item : exampleListNew){
                                                                if (item.getTextNome().equals(autoreFirebase) && item.getTextEmail().equals(emailFirebase)) {
                                                                    valore = true;
                                                                    break;
                                                                }
                                                            }
                                                            if (! valore)
                                                                exampleListNew.add(new ItemSearch(autoreFirebase, emailFirebase));
                                                        }
                                                    }
                                                }
                                            }
                                            Log.d(TAG, "exampleListNew: " + exampleListNew.toString());
                                            for (ItemSearch item : exampleListNew){
                                                Log.d(TAG, "new List: " + "Nome: " + item.getTextNome() + ", Email: " + item.getTextEmail());
                                            }
                                            HashMap<String, String> map = new HashMap<>();
                                            for(ItemSearch item : exampleListNew){
                                                map.put(item.getTextEmail(), item.getTextNome());
                                            }
                                            Intent intentGestioneSomm = new Intent(activityDelSomm, GestioneSomministratore.class);
                                            startActivity(intentGestioneSomm);
                                            activityDelSomm.finish();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                }
                            }
                        }
                )
                .setNegativeButton(R.string.dialog_negative, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        return  builder.create();
    }

    public void showAlertDialog() {
        androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(this.activity)
                .setTitle("Caricamento in corso")
                .setMessage("Attendere...")
                .create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog){

            }
        });
        dialog.show();
    }
}

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
import java.util.Map;

import it.unimib.bicap.EliminaProgetti;
import it.unimib.bicap.EliminaSomministratore;
import it.unimib.bicap.ExampleItem;
import it.unimib.bicap.GestioneSomministratore;
import it.unimib.bicap.adapter.ProgettiDaEliminareAdapterRV;
import it.unimib.bicap.constanti.ActivityConstants;

public class EliminaDialog extends AppCompatDialogFragment {

    private String nomeProgetto;
    private JSONArray listaProgetti;
    private HashMap<String, String> nomiSomministratori;
    private Integer index;
    private String key;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("utenti");
    private ProgettiDaEliminareAdapterRV progettiAdapterRV;
    private ProgettiDaEliminareAdapterRV progettiAdapterSommRV;
    private JSONObject listaProgettiTot;
    private EliminaProgetti activity;
    private EliminaSomministratore activityDelSomm;
    private GetterInfo getterInfo = new GetterLocal();
    private String message;
    private List<ExampleItem> exampleList;
    private final String TAG = "EliminaDialog";
    public EliminaDialog(List<ExampleItem> exampleList, JSONArray listaProgetti, JSONObject listaProgettiTot, Integer index, ProgettiDaEliminareAdapterRV istanzaProgettiAdapter, EliminaProgetti eliminaActivity, String message){
        try {
            this.exampleList = exampleList;
            this.nomeProgetto=listaProgetti.getJSONObject(index).getString("nome");
            this.listaProgetti=listaProgetti;
            this.index = index;
            this.progettiAdapterRV = istanzaProgettiAdapter;
            this.listaProgettiTot = listaProgettiTot;
            this.activity = eliminaActivity;
            this.message = message;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*public EliminaDialog(HashMap<String, String> nomiSomministratori, String key, Integer index, ProgettiDaEliminareAdapterRV istanzaProgettiDaEliminareAdapterRV, EliminaSomministratore eliminaActivity, String message){
        this.nomiSomministratori = nomiSomministratori;
        this.key = key;
        this.index = index;
        this.progettiAdapterSommRV = istanzaProgettiDaEliminareAdapterRV;
        this.activityDelSomm = eliminaActivity;
        this.message = message;
    }*/

    public EliminaDialog(List<ExampleItem> exampleList, String key, Integer index, ProgettiDaEliminareAdapterRV istanzaProgettiDaEliminareAdapterRV, EliminaSomministratore eliminaActivity, String message){
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
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Elimina")
                .setMessage(message)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (activityDelSomm == null) {
                                    JSONArray listaNuova = new JSONArray();
                                    JSONObject nuoviProgetti = null;
                                    Integer idElimina = null;
                                    //List<ExampleItem> exampleListNew = new ArrayList<>();
                                    //primo for serve  per identificare il json da eliminare all'interno della lista intera di progetti
                                    //secondo for serve per inizializzare una nuova lista di jsonObject che l'utente vedrà senza inserire il json identificato precedentemente
                                    try {
                                        for (int i = 0; i < listaProgetti.length(); i++) {
                                            if (i != index) {
                                                listaNuova.put(listaProgetti.getJSONObject(i));
                                                //exampleListNew.add(new ExampleItem(getterInfo.getNomeProgetto(listaProgetti.getJSONObject(i)), getterInfo.getDescrizione(listaProgetti.getJSONObject(i))));
                                            } else
                                                idElimina = listaProgetti.getJSONObject(i).getInt("id");
                                        }
                                        JSONArray lista = listaProgettiTot.getJSONArray("progetti");
                                        JSONArray nuovaListaTotProgetti = new JSONArray();
                                        for (int j = 0; j < lista.length(); j++) {
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
                                    //TODO: dialog on process e metodo di riscrittura file progetti.json
                                    Utility.write(nuoviProgetti, activity, null);
                                    Log.d(TAG, "eliminato");
                                    //ProgettiDaEliminareAdapterRV.setListaProgettiTot(nuoviProgetti);
                                    //ProgettiDaEliminareAdapterRV.setExampleListFull(exampleListNew);
                                    //ProgettiDaEliminareAdapterRV.setNomi(getterInfo.getNomiProgetti(listaNuova));
                                    //ProgettiDaEliminareAdapterRV.setListaProgetti(listaNuova);
                                    //HashMap<String, Object> map = new HashMap<>();
                                    //map.put("listaFinale", listaNuova);
                                    //HashMap<String, Object> secondMap = new HashMap<>();
                                    //map.put("oggettoFinale", listaProgettiTot);
                                    //progettiAdapterRV.notifyDataSetChanged();
                                    Intent intentDelProj = new Intent(getContext(), EliminaProgetti.class);
                                    intentDelProj.putExtra(ActivityConstants.INTENT_RETURN, true);
                                    intentDelProj.putExtra(ActivityConstants.INTENT_NEW_LIST, listaNuova.toString());
                                    intentDelProj.putExtra(ActivityConstants.INTENT_NEW_OBJECT, listaProgettiTot.toString());
                                    showAlertDialog(activity, "ciao","mamma", true, intentDelProj);
                                    //dismiss();
                                }
                                else{/*
                                    final String email = key;
                                    final List<String> emailSomm = new ArrayList<>();
                                    final String autore = nomiSomministratori.get(email);
                                    final HashMap<String, String> nuovaHashMap = new HashMap<>();
                                    final List<String> nomiSomm = new ArrayList<>();

                                    myRef.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            // This method is called once with the initial value and again
                                            // whenever data at this location is updated.
                                            for (DataSnapshot d : dataSnapshot.getChildren()){
                                                if(d.child("email").getValue()!= null && d.child("autore").getValue()!=null && d.child("attivo").getValue()!=null) {
                                                    if (d.child("autore").getValue().equals(autore) && d.child("email").getValue().equals(email)) {
                                                        nomiSomministratori.remove(email);
                                                        HashMap<String, Object> map = new HashMap<>();
                                                        map.put("attivo", "false");
                                                        d.getRef().updateChildren(map);
                                                    } else {
                                                        if (d.child("attivo").getValue().equals("true") && ! d.child("email").getValue().equals("admin@admin.com")) {
                                                            nuovaHashMap.put(d.child("email").getValue().toString(), d.child("autore").getValue().toString());
                                                            nomiSomm.add(d.child("autore").getValue().toString());
                                                            emailSomm.add(d.child("email").getValue().toString());
                                                        }
                                                    }
                                                }
                                                //TODO: reinizializzare anche lista delle email
                                            }
                                            //String value = dataSnapshot.getValue(String.class);
                                            //Log.d(TAG, "Value is: " + value);
                                            //ProgettiDaEliminareAdapterRV.setSomministratori(nuovaHashMap);
                                            //progettiAdapterSommRV.notifyItemRemoved(index);
                                            ProgettiDaEliminareAdapterRV.setNomiSomm(nomiSomm);
                                            ProgettiDaEliminareAdapterRV.setNomiSomministratori(nuovaHashMap);
                                            Log.d(TAG, "nuova Hashmap: " + nuovaHashMap.toString());
                                            ProgettiDaEliminareAdapterRV.setEmailSomm(emailSomm);
                                            Log.d(TAG, emailSomm.toString());
                                            //progettiAdapterSommRV.notifyItemRemoved(index);
                                            progettiAdapterSommRV.notifyDataSetChanged();
                                            //progettiAdapterSommRV.notifyDataSetChanged();
                                            dismiss();
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError error) {
                                            // Failed to read value
                                            //Log.w(TAG, "Failed to read value.", error.toException());
                                        }
                                    });*/
                                    final String autore = exampleList.get(index).getTextNome();
                                    final String email = exampleList.get(index).getTextEmail();
                                    final List<ExampleItem> exampleListNew = new ArrayList<>();
                                    myRef.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot d : dataSnapshot.getChildren()){
                                                if(d.child("email").getValue()!= null && d.child("autore").getValue()!=null && d.child("attivo").getValue()!=null) {
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
                                                            for(ExampleItem item : exampleListNew){
                                                                if (item.getTextNome().equals(d.child("autore").getValue().toString()) && item.getTextEmail().equals(d.child("email").getValue().toString())){
                                                                    valore = true;
                                                                }
                                                            }
                                                            if (! valore)
                                                                exampleListNew.add(new ExampleItem(d.child("autore").getValue().toString(), d.child("email").getValue().toString()));
                                                        }
                                                    }
                                                }
                                            }
                                            Log.d(TAG, "exampleListNew: " + exampleListNew.toString());
                                            for (ExampleItem item : exampleListNew){
                                                Log.d(TAG, "new List: " + "Nome: " + item.getTextNome() + ", Email: " + item.getTextEmail());
                                            }
                                            HashMap<String, String> map = new HashMap<>();
                                            for(ExampleItem item : exampleListNew){
                                                map.put(item.getTextEmail(), item.getTextNome());
                                            }
                                            Intent intentPazzo = new Intent(activityDelSomm, GestioneSomministratore.class);
//                                            intentPazzo.putExtra("pazzi", map);
//                                            intentPazzo.putExtra(ActivityConstants.INTENT_HOME, false);
                                            startActivity(intentPazzo);
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
                            }
                        }
                )
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        return  builder.create();
    }

    public void showAlertDialog(final Context context, String title, String message,
                                Boolean status, final Intent intent) {
        androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(this.activity)
                .setTitle("Caricamento in corso")
                .setMessage("Attendere...")
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
                        if (((androidx.appcompat.app.AlertDialog) dialog).isShowing()) {
                            dialog.dismiss();
                            activity.startActivity(intent);
                            activity.finish();
                        }
                    }
                }.start();
            }
        });
        dialog.show();
    }
}

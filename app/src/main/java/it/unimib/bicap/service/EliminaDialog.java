package it.unimib.bicap.service;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
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

import it.unimib.bicap.EliminaProgetti;
import it.unimib.bicap.EliminaSomministratore;
import it.unimib.bicap.adapter.ProgettiDaEliminareAdapterRV;

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
    public EliminaDialog(JSONArray listaProgetti, JSONObject listaProgettiTot, Integer index, ProgettiDaEliminareAdapterRV istanzaProgettiAdapter, EliminaProgetti eliminaActivity, String message){
        try {
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

    public EliminaDialog(HashMap<String, String> nomiSomministratori, String key, Integer index, ProgettiDaEliminareAdapterRV istanzaProgettiDaEliminareAdapterRV, EliminaSomministratore eliminaActivity, String message){
        this.nomiSomministratori = nomiSomministratori;
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
                            //primo for serve  per identificare il json da eliminare all'interno della lista intera di progetti
                            //secondo for serve per inizializzare una nuova lista di jsonObject che l'utente vedrà senza inserire il json identificato precedentemente
                            try {
                                for (int i = 0; i < listaProgetti.length(); i++) {
                                    if (i != index)
                                        listaNuova.put(listaProgetti.getJSONObject(i));
                                    else
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
                            //ProgettiDaEliminareAdapterRV.setListaProgettiTot(nuoviProgetti);
                            ProgettiDaEliminareAdapterRV.setNomi(getterInfo.getNomiProgetti(listaNuova));
                            ProgettiDaEliminareAdapterRV.setListaProgetti(listaNuova);
                            progettiAdapterRV.notifyDataSetChanged();
                            dismiss();
                        }
                        else{
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
                                        if (d.child("autore").getValue().equals(autore) && d.child("email").getValue().equals(email)){
                                            d.getRef().removeValue();
                                        }
                                        else {
                                            nuovaHashMap.put(d.child("email").toString(), d.child("autore").toString());
                                            nomiSomm.add(d.child("autore").getValue().toString());
                                            emailSomm.add(d.child("email").getValue().toString());
                                        }
                                            //TODO: reinizializzare anche lista delle email
                                    }
                                    //String value = dataSnapshot.getValue(String.class);
                                    //Log.d(TAG, "Value is: " + value);
                                    //ProgettiDaEliminareAdapterRV.setSomministratori(nuovaHashMap);
                                    nomiSomministratori.remove(email);
                                    //progettiAdapterSommRV.notifyItemRemoved(index);
                                    ProgettiDaEliminareAdapterRV.setNomiSomm(nomiSomm);
                                    ProgettiDaEliminareAdapterRV.setNomiSomministratori(nuovaHashMap);
                                    ProgettiDaEliminareAdapterRV.setEmailSomm(emailSomm);
                                    progettiAdapterSommRV.notifyDataSetChanged();
                                    //progettiAdapterSommRV.notifyDataSetChanged();
                                    dismiss();
                                }

                                @Override
                                public void onCancelled(DatabaseError error) {
                                    // Failed to read value
                                    //Log.w(TAG, "Failed to read value.", error.toException());
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
}

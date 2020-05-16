package it.unimib.bicap;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import adapter.ProgettiAdapterRV;

public class QuestionariDaFare extends Fragment {

    private static final String TAG = "QuestionariDaFare";

    private ProgettiAdapterRV progettiAdapterRV;
    private String [] titoli = {"Questionario 1", "Questionario 2"};
    private String from = "daFare";
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_lista_progetti, container, false);
        RecyclerView recyclerView = rootView.findViewById(R.id.rvProgetti);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(linearLayoutManager);

        progettiAdapterRV = new ProgettiAdapterRV(getContext(), null, from);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(progettiAdapterRV);

        return rootView;
    }
}

package no.hiof.fridgebro.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import no.hiof.fridgebro.R;
import no.hiof.fridgebro.adapters.RecyclerViewAdapter;
import no.hiof.fridgebro.models.Item;

public class FridgeFragment extends Fragment {

    private RecyclerView rvProduct;
    private ArrayList<Item> productList = new ArrayList<>();
    private RecyclerViewAdapter recyclerViewAdapter;


    public FridgeFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fridge, container, false);
        rvProduct = (RecyclerView) view.findViewById(R.id.recyclerView);
        rvProduct.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        rvProduct.setLayoutManager(manager);
        recyclerViewAdapter = new RecyclerViewAdapter(productList, getActivity());
        rvProduct.setAdapter(recyclerViewAdapter);

        return view;
    }

}

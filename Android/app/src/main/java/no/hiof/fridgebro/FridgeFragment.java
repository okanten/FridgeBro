package no.hiof.fridgebro;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class FridgeFragment extends Fragment {

    private RecyclerView rvProduct;
     ArrayList<String> productList = new ArrayList<>();
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
        recyclerViewAdapter = new RecyclerViewAdapter(productList, productList, getActivity());
        rvProduct.setAdapter(recyclerViewAdapter);

        return view;
    }

}

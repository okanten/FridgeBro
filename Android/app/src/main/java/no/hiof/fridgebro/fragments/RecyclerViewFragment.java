package no.hiof.fridgebro.fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.util.ArrayList;
import java.util.Objects;

import no.hiof.fridgebro.BarcodeScanner;
import no.hiof.fridgebro.R;
import no.hiof.fridgebro.adapters.RecyclerViewAdapter;
import no.hiof.fridgebro.activities.AddActivity;
import no.hiof.fridgebro.models.Item;


public class RecyclerViewFragment extends Fragment {


    private ArrayList<Item> productList = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;


    public RecyclerViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (productList.isEmpty()) {
            getImageBitmaps();
        }

        View v =  inflater.inflate(R.layout.fragment_recycler_view, container, false);
        recyclerView = v.findViewById(R.id.recyclerView);
        adapter = new RecyclerViewAdapter(productList, getContext(), this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        final FloatingActionsMenu mainFab = v.findViewById(R.id.myFab);
        FloatingActionButton fabManual = v.findViewById(R.id.fabManual);
        FloatingActionButton fabScanner = v.findViewById(R.id.fabScanner);
        fabManual.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mainFab.collapseImmediately();
                Intent intent = new Intent(getContext(), AddActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("productList", productList);
                //bundle.putSerializable("productList", productList);
                intent.putExtras(bundle);
                ((Activity) Objects.requireNonNull(getContext())).startActivityForResult(intent, 200);
            }
        });
        fabScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainFab.collapseImmediately();
                Intent intent = new Intent(getContext(), BarcodeScanner.class);
                Bundle bundle = new Bundle();
                RecyclerViewFragment.this.startActivity(intent, bundle);

            }
        });
        return v;
    }

    private void getImageBitmaps(){
        productList.add(new Item("Test 1", "1", "11111", "https://i.redd.it/oir304dowbs11.jpg", "TestBrand", "03/03/2019"));
        productList.add(new Item("Test 2", "1", "11111", "https://i.redd.it/oir304dowbs11.jpg", "TestBrand", "03/03/2019"));
        productList.add(new Item("Test 3", "1", "11111", "https://i.redd.it/oir304dowbs11.jpg", "TestBrand", "03/03/2019"));
        productList.add(new Item("Test 4", "1", "11111", "https://i.redd.it/oir304dowbs11.jpg", "TestBrand", "03/03/2019"));
        productList.add(new Item("Test 5", "1", "11111", "https://i.redd.it/oir304dowbs11.jpg", "TestBrand", "03/03/2019"));
        productList.add(new Item("Test 6", "1", "11111", "https://i.redd.it/oir304dowbs11.jpg", "TestBrand", "03/03/2019"));
        productList.add(new Item("Test 7", "1", "11111", "https://i.redd.it/oir304dowbs11.jpg", "TestBrand", "03/03/2019"));
        productList.add(new Item("Test 8", "1", "11111", "https://i.redd.it/oir304dowbs11.jpg", "TestBrand", "03/03/2019"));
    }

    public RecyclerViewAdapter getAdapter() {
        return adapter;
    }

    //TODO: Trenger muligens fix?
    public void updateItem(int position, String newImg, String newName) {
        //this.productList.set(position, newImg);
        //this.productNames.set(position, newName);
        System.out.println(this.productList.get(position));
        adapter.notifyItemChanged(position);
    }

    //TODO: Trenger muligens fix?
    public void deleteItem(int position) {
        this.productList.remove(position);
        //this.productImages.remove(position);
        adapter.notifyItemRemoved(position);
    }

    public void updateAdapter(ArrayList<Item> productList) {
        this.productList.clear();
        this.productList.addAll(productList);

        adapter.notifyDataSetChanged();
    }

    public ArrayList<Item> getProductList() {
        return productList;
    }



}

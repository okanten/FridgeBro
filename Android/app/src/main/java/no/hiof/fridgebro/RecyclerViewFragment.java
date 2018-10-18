package no.hiof.fridgebro;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.util.ArrayList;
import java.util.Arrays;

import static android.app.Activity.RESULT_OK;


public class RecyclerViewFragment extends Fragment {

    private ArrayList<String> productNames = new ArrayList<>();
    private ArrayList<String> productImages = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;


    public RecyclerViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (productNames.isEmpty() && productImages.isEmpty()) {
            getImageBitmaps();
        }

        View v =  inflater.inflate(R.layout.fragment_recycler_view, container, false);
        recyclerView = v.findViewById(R.id.recyclerView);
        adapter = new RecyclerViewAdapter(productNames, productImages, getContext(), this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        final FloatingActionsMenu mainFab = v.findViewById(R.id.myFab);
        FloatingActionButton fabManual = v.findViewById(R.id.fabManual);
        fabManual.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mainFab.collapseImmediately();
                Intent intent = new Intent(getContext(), AddActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("productName", productNames);
                bundle.putSerializable("productImage", productImages);
                intent.putExtras(bundle);
                ((Activity) getContext()).startActivityForResult(intent, 200);
            }
        });

        return v;
    }

    private void getImageBitmaps(){

        productImages.add("https://i.redd.it/oir304dowbs11.jpg");
        productNames.add("TEST 1");

        productImages.add("https://i.redd.it/87uk9o66ges11.jpg");
        productNames.add("TEST 2");

        productImages.add("https://i.imgur.com/KQCevT6.jpg");
        productNames.add("TEST 3");

        productImages.add("https://i.redd.it/mz978u71ncs11.jpg");
        productNames.add("TEST 4");

        productImages.add("https://i.redd.it/0pidjjktjcs11.jpg");
        productNames.add("TEST 5");
 /*
        productImages.add("https://i.redd.it/oir304dowbs11.jpg");
        productNames.add("TEST 6");

        productImages.add("https://i.redd.it/87uk9o66ges11.jpg");
        productNames.add("TEST 7");

        productImages.add("https://i.imgur.com/KQCevT6.jpg");
        productNames.add("TEST 8");

        productImages.add("https://i.redd.it/mz978u71ncs11.jpg");
        productNames.add("TEST 9");


        productImages.add("https://i.redd.it/0pidjjktjcs11.jpg");
        productNames.add("TEST 10");*/

    }

    public RecyclerViewAdapter getAdapter() {
        return adapter;
    }

    //TODO: Trenger muligens fix?
    public void updateItem(Integer position, String newImg, String newName) {
        this.productImages.set(position, newImg);
        this.productNames.set(position, newName);
        System.out.println(this.productNames.get(position));
        adapter.notifyItemChanged(position);
    }

    //TODO: Trenger muligens fix?
    public void deleteItem(Integer position) {
        this.productNames.remove(position);
        this.productImages.remove(position);
        // TODO: Finne ut hvorfor i helvete dette ikke funker uten å slette siste element også :-))) - Virker som det spawnes et nytt (dupe) element etter det siste
        this.productNames.remove(this.productNames.size() - 1);
        this.productImages.remove(this.productImages.size() - 1);

        adapter.notifyItemRemoved(position);
    }

    public void updateAdapter(ArrayList<String> productImages, ArrayList<String> productNames) {
        this.productNames.clear();
        this.productNames.addAll(productNames);
        this.productImages.clear();
        this.productImages.addAll(productImages);
        adapter.notifyDataSetChanged();
    }

    public ArrayList<String> getProductNames() {
        return productNames;
    }

    public ArrayList<String> getProductImages() {
        return productImages;
    }


}

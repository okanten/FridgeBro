package no.hiof.fridgebro;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;


public class RecyclerViewFragment extends Fragment {

    private ArrayList<String> productNames = new ArrayList<>();
    private ArrayList<String> productImages = new ArrayList<>();
    View v;

    public RecyclerViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getImageBitmaps();
        View v =  inflater.inflate(R.layout.fragment_recycler_view, container, false);
        RecyclerView recyclerView = v.findViewById(R.id.recyclerView);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(productNames,productImages,getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
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
        productImages.add("https://i.redd.it/oir304dowbs11.jpg");
        productNames.add("TEST 6");

        productImages.add("https://i.redd.it/87uk9o66ges11.jpg");
        productNames.add("TEST 7");

        productImages.add("https://i.imgur.com/KQCevT6.jpg");
        productNames.add("TEST 8");

        productImages.add("https://i.redd.it/mz978u71ncs11.jpg");
        productNames.add("TEST 9");

        productImages.add("https://i.redd.it/0pidjjktjcs11.jpg");
        productNames.add("TEST 10");

        productImages.add("https://i.redd.it/oir304dowbs11.jpg");
        productNames.add("TEST 11");

        productImages.add("https://i.redd.it/87uk9o66ges11.jpg");
        productNames.add("TEST 12");

        productImages.add("https://i.imgur.com/KQCevT6.jpg");
        productNames.add("TEST 13");

        productImages.add("https://i.redd.it/mz978u71ncs11.jpg");
        productNames.add("TEST 14");

        productImages.add("https://i.redd.it/0pidjjktjcs11.jpg");
        productNames.add("TEST 15");

        productImages.add("https://i.redd.it/oir304dowbs11.jpg");
        productNames.add("TEST 16");

        productImages.add("https://i.redd.it/87uk9o66ges11.jpg");
        productNames.add("TEST 17");

        productImages.add("https://i.imgur.com/KQCevT6.jpg");
        productNames.add("TEST 18");

        productImages.add("https://i.redd.it/mz978u71ncs11.jpg");
        productNames.add("TEST 19");

        productImages.add("https://i.redd.it/0pidjjktjcs11.jpg");
        productNames.add("TEST 20");

        productImages.add("https://i.redd.it/oir304dowbs11.jpg");
        productNames.add("TEST 21");

        productImages.add("https://i.redd.it/87uk9o66ges11.jpg");
        productNames.add("TEST 22");

        productImages.add("https://i.imgur.com/KQCevT6.jpg");
        productNames.add("TEST 23");

        productImages.add("https://i.redd.it/mz978u71ncs11.jpg");
        productNames.add("TEST 24");

        productImages.add("https://i.redd.it/0pidjjktjcs11.jpg");
        productNames.add("TEST 25");
    }


}

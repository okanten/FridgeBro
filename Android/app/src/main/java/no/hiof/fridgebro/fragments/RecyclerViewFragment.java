package no.hiof.fridgebro.fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.auth.data.model.User;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import no.hiof.fridgebro.activities.BarcodeScanner;
import no.hiof.fridgebro.R;
import no.hiof.fridgebro.activities.MainActivity;
import no.hiof.fridgebro.adapters.RecyclerViewAdapter;
import no.hiof.fridgebro.activities.AddActivity;
import no.hiof.fridgebro.models.Item;


public class RecyclerViewFragment extends Fragment {


    private ArrayList<Item> productList = new ArrayList<>();

    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
    private boolean isOnShoppingList;
    private boolean priceSortedAsc = false;
    private boolean alphaSortedAsc = false;

    private ChildEventListener childEventListener;
    private List<String> productListKeys = new ArrayList<>();
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference dataReference;



    public RecyclerViewFragment() {
        // Required empty public constructor
    }


    public RecyclerViewFragment newInstance(boolean isOnShoppingList) {
        RecyclerViewFragment rcvFrag = new RecyclerViewFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("shoppingList", isOnShoppingList);
        rcvFrag.setArguments(bundle);
        return rcvFrag;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (productList.isEmpty()) {
            createDatabaseReadListener();
            //getImageBitmaps();
        }

        isOnShoppingList = getArguments().getBoolean("shoppingList", false);
        View v;
        if (isOnShoppingList) {
            v = inflater.inflate(R.layout.fragment_shoppinglist, container, false);
        } else {
            v = inflater.inflate(R.layout.fragment_recycler_view, container, false);
        }

        recyclerView = v.findViewById(R.id.recyclerView);
        adapter = new RecyclerViewAdapter(productList, getContext(), this, isOnShoppingList);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        firebaseDatabase = FirebaseDatabase.getInstance();
        dataReference = firebaseDatabase.getReference("Users");

        final FloatingActionsMenu mainFab = v.findViewById(R.id.myFab);
        FloatingActionButton fabManual = v.findViewById(R.id.fabManual);
        FloatingActionButton fabScanner = v.findViewById(R.id.fabScanner);
        fabManual.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mainFab.collapseImmediately();
                Intent intent = new Intent(getContext(), AddActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("productList", productList);
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
                bundle.putParcelableArrayList("productList", productList);
                intent.putExtras(bundle);
                ((Activity) Objects.requireNonNull(getContext())).startActivityForResult(intent, 300);
            }
        });
        return v;
    }

    private void createDatabaseReadListener(){
        childEventListener = new ChildEventListener(){
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Item item = dataSnapshot.getValue(Item.class);
                String itemKey = dataSnapshot.getKey();
                item.setItemName(itemKey);

                if (!productList.contains(item)){
                    productList.add(item);
                    productListKeys.add(itemKey);
                    adapter.notifyItemInserted(productList.size()-1);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Item item = dataSnapshot.getValue(Item.class);
                String itemKey = dataSnapshot.getKey();
                item.setItemName(itemKey);

                int postition = productList.indexOf(itemKey);

                productList.set(postition,item);
                adapter.notifyItemChanged(postition);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Item removedItem = dataSnapshot.getValue(Item.class);
                String itemkey = dataSnapshot.getKey();
                removedItem.setItemName(itemkey);
                int position = productListKeys.indexOf(itemkey);
                productList.remove(removedItem);
                productListKeys.remove(position);
                adapter.notifyItemRemoved(position);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    private void getImageBitmaps(){
        productList.add(new Item("Test 1", "1", "11111", "https://i.redd.it/oir304dowbs11.jpg", "TestBrand", "03/03/2019"));
        productList.add(new Item("Test 2", "1", "11111", "https://i.redd.it/oir304dowbs11.jpg", "TestBrand", "03/03/2019"));
        productList.add(new Item("Test 3", "242", "11111", "https://i.redd.it/oir304dowbs11.jpg", "TestBrand", "03/03/2019"));
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
        adapter.notifyItemRemoved(position);
    }

    public void sortListByPrice() {
        if(!priceSortedAsc) {
            Collections.sort(productList);
            priceSortedAsc = true;
        } else {
            Collections.reverse(productList);
            priceSortedAsc = false;
        }
        adapter.notifyDataSetChanged();
    }

    public void sortListAlphabetically() {
        if (!alphaSortedAsc) {
            Collections.sort(productList, new Comparator<Item>() {
                @Override
                public int compare(Item o1, Item o2) {
                    return o1.getItemName().compareTo(o2.getItemName());
                }
            });
            alphaSortedAsc = true;
        } else {
            Collections.sort(productList, new Comparator<Item>() {
                @Override
                public int compare(Item o1, Item o2) {
                    return o2.getItemName().compareTo(o1.getItemName());
                }
            });
            alphaSortedAsc = false;
        }
        adapter.notifyDataSetChanged();

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

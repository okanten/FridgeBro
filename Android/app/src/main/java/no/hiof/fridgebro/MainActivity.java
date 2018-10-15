package no.hiof.fridgebro;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity  {

    private ArrayList<String> productNames = new ArrayList<>();
    private ArrayList<String> productImages = new ArrayList<>();
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getImageBitmaps();
        setUpRecyclerView();

        /*Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //må byttes ut med det som skal starte når vi kjører appen(recycleviewet)
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new ShoppingListFragemnt()).commit();
            navigationView.setCheckedItem(R.id.nav_shoppinglist);
        }

        */


    }
    /*
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_shoppinglist:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ShoppingListFragemnt()).commit();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    */

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

    private void setUpRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(productNames,productImages,this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}

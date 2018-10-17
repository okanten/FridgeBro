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
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
// denne må etter appcompat for at koden skal funke med fragments:  implements NavigationView.OnNavigationItemSelectedListener
    private ArrayList<String> productNames = new ArrayList<>();
    private ArrayList<String> productImages = new ArrayList<>();
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = findViewById(R.id.nav_action);
        setSupportActionBar(mToolbar);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //getImageBitmaps();
        //setUpRecyclerView();


        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.drawer_layout,
                    new RecyclerViewFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_fridgelist);
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);




        // for at den implementerte klassen skal funke må denne inn og


        //Fragmentet som vises når appen startes, savedinstance


    }



    // denne overriden skal være være i stedet for onOptionsItemSelected override med fragments , all koden skal være goodie

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_shoppinglist:
                getSupportFragmentManager().beginTransaction().replace(R.id.drawer_layout,
                        new ShoppingListFragemnt()).commit();
                break;
            case R.id.nav_fridgelist:
                getSupportFragmentManager().beginTransaction().replace(R.id.drawer_layout,
                        new FridgeFragment()).commit();
                break;
        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }



    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    /*private void getImageBitmaps(){
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
    }*/

    /*private void setUpRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(productNames,productImages,this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }*/
}

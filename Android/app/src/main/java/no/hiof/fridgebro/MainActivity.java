package no.hiof.fridgebro;

import android.content.Intent;
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
import android.view.View;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
// denne må etter appcompat for at koden skal funke med fragments:  implements NavigationView.OnNavigationItemSelectedListener
    private ArrayList<String> productNames = new ArrayList<>();
    private ArrayList<String> productImages = new ArrayList<>();
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    private Toolbar mToolbar;
    private RecyclerViewFragment recyclerViewFragment;

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
            if (recyclerViewFragment == null) {
                recyclerViewFragment = new RecyclerViewFragment();
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    recyclerViewFragment).commit();
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
    public boolean onNavigationItemSelected(@NonNull MenuItem Item) {
        switch (Item.getItemId()) {

            case R.id.nav_fridgelist:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        recyclerViewFragment).commit();
                break;
            case R.id.nav_shoppinglist:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ShoppingListFragemnt()).commit();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 200) {
            if (resultCode == RESULT_OK) {
                productImages = (ArrayList<String>) data.getSerializableExtra("productImages");
                productNames = (ArrayList<String>) data.getSerializableExtra("productNames");
                System.out.println(productImages.get(productImages.size() - 1));
                ArrayList<String> test = recyclerViewFragment.getProductNames();
                System.out.println(test.get(test.size() - 1));
                recyclerViewFragment.updateAdapter(productImages, productNames);
            }
        }
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                productImages = (ArrayList<String>) data.getSerializableExtra("productImages");
                productNames = (ArrayList<String>) data.getSerializableExtra("productNames");
                Integer position = data.getIntExtra("pos", 0);
                // TODO: Gjøre dette ordentlig.
                productImages.set(position, productImages.get(productImages.size() - 1));
                productNames.set(position, productNames.get(productNames.size() - 1));
                productImages.remove(productImages.size() - 1);
                productNames.remove(productNames.size() - 1);
                recyclerViewFragment.updateAdapter(productImages, productNames);
            }
        }
    }
}

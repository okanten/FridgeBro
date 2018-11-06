package no.hiof.fridgebro.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import no.hiof.fridgebro.R;
import no.hiof.fridgebro.fragments.RecyclerViewFragment;
import no.hiof.fridgebro.fragments.ShoppingListFragment;
import no.hiof.fridgebro.models.Item;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
// denne må etter appcompat for at koden skal funke med fragments:  implements NavigationView.OnNavigationItemSelectedListener
    private ArrayList<Item> productList = new ArrayList<>();
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

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Test for å skrive til database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");

        myRef.setValue("test");


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
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem Item) {
        switch (Item.getItemId()) {

            case R.id.nav_fridgelist:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        recyclerViewFragment).commit();
                break;
            case R.id.nav_shoppinglist:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ShoppingListFragment()).commit();
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
                productList = data.getParcelableArrayListExtra("productList");
                recyclerViewFragment.updateAdapter(productList);
            }
        }
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                productList = data.getParcelableArrayListExtra("productList");
                Integer position = data.getIntExtra("pos", 0);
                // TODO: Gjøre dette ordentlig.
                productList.set(position, productList.get(productList.size() - 1));
                productList.remove(productList.size() - 1);
                recyclerViewFragment.updateAdapter(productList);
            }
        }
    }
}

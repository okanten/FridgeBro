package no.hiof.fridgebro.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;

import no.hiof.fridgebro.R;
import no.hiof.fridgebro.fragments.RecyclerViewFragment;
import no.hiof.fridgebro.fragments.ShoppingListFragment;
import no.hiof.fridgebro.models.Item;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private ArrayList<Item> productList = new ArrayList<>();
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    private Toolbar mToolbar;
    private RecyclerViewFragment recyclerViewFragment;


    private static final int RC_SIGN_IN = 1;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //testing for firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        createAuthenticationListener();

        //test for firebase realtime database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");
        myRef.setValue("Hello, World!");



        mToolbar = findViewById(R.id.nav_action);
        setSupportActionBar(mToolbar);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

    private void createAuthenticationListener() {
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser == null) {
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(
                                            Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build(),
                                                    new AuthUI.IdpConfig.GoogleBuilder().build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (firebaseAuthStateListener != null)
            firebaseAuth.addAuthStateListener(firebaseAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (firebaseAuthStateListener != null)
            firebaseAuth.removeAuthStateListener(firebaseAuthStateListener);
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

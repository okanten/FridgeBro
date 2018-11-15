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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import no.hiof.fridgebro.R;
import no.hiof.fridgebro.fragments.RecyclerViewFragment;
import no.hiof.fridgebro.models.Item;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private ArrayList<Item> productList = new ArrayList<>();
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    private Toolbar mToolbar;
    private RecyclerViewFragment recyclerViewFragment;
    private RecyclerViewFragment shoppingListFragment;

    private boolean isOnShoppingList = false;

    private static final int RC_SIGN_IN = 1;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //testing for firebase auth
       /*firebaseAuth = FirebaseAuth.getInstance();
        createAuthenticationListener();


        //Test for firebase realtime database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");
        myRef.setValue("Hello, World!");
*/

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
            if (recyclerViewFragment == null && !isOnShoppingList) {
                recyclerViewFragment = new RecyclerViewFragment().newInstance(isOnShoppingList);
            } else if (shoppingListFragment == null && isOnShoppingList) {
                shoppingListFragment = new RecyclerViewFragment().newInstance(isOnShoppingList);
            }
            if (isOnShoppingList) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        shoppingListFragment).commit();
                navigationView.setCheckedItem(R.id.nav_shoppinglist);
            } else {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        recyclerViewFragment).commit();
                navigationView.setCheckedItem(R.id.nav_fridgelist);
            }

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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_buttons, menu);
        if (isOnShoppingList) {
            menu.getItem(1).setVisible(false);
            menu.findItem(R.id.sortPrice).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sortPrice:
                if (isOnShoppingList) {
                    shoppingListFragment.sortListByPrice();
                } else {
                    recyclerViewFragment.sortListByPrice();
                }
                break;
            case R.id.sortAlphabetical:
                if (isOnShoppingList) {
                    shoppingListFragment.sortListAlphabetically();
                } else {
                    recyclerViewFragment.sortListAlphabetically();
                }
                break;
        }

        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem Item) {
        switch (Item.getItemId()) {
            case R.id.nav_fridgelist:
                isOnShoppingList = false;
                // Må sjekke om rcv er null i tilfelle brukeren roterer telefonen og bytter fra shoppingList til fridge.
                if (recyclerViewFragment == null) {
                    recyclerViewFragment = new RecyclerViewFragment().newInstance(isOnShoppingList);
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        recyclerViewFragment).commit();
                break;
            case R.id.nav_shoppinglist:
                isOnShoppingList = true;
                if (shoppingListFragment == null) {
                    shoppingListFragment = new RecyclerViewFragment().newInstance(isOnShoppingList);
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        shoppingListFragment).commit();
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
        if (requestCode == 200 || requestCode == 300) {
            if (resultCode == RESULT_OK) {
                productList = data.getParcelableArrayListExtra("productList");
                if (isOnShoppingList) {
                    shoppingListFragment.updateAdapter(productList);
                } else {
                    recyclerViewFragment.updateAdapter(productList);
                }
            }
        }
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                productList = data.getParcelableArrayListExtra("productList");
                Integer position = data.getIntExtra("pos", 0);
                // TODO: Se på en annen løsning
                productList.set(position, productList.get(productList.size() - 1));
                productList.remove(productList.size() - 1);
                if (isOnShoppingList) {
                    shoppingListFragment.updateAdapter(productList);
                } else {
                    recyclerViewFragment.updateAdapter(productList);
                }
            }
        }

    }
}

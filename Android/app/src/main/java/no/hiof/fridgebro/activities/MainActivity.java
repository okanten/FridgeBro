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
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;

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

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference dataReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i("Triggered", "onCreate triggered");


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

        if (savedInstanceState == null) {
            if (recyclerViewFragment == null && !isOnShoppingList) {
                recyclerViewFragment = new RecyclerViewFragment().newInstance(isOnShoppingList);
            } else if (shoppingListFragment == null && isOnShoppingList) {
                shoppingListFragment = new RecyclerViewFragment().newInstance(isOnShoppingList);
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    getRecyclerView()).commit();
            if (isOnShoppingList) {
                navigationView.setCheckedItem(R.id.nav_shoppinglist);
            } else {
                navigationView.setCheckedItem(R.id.nav_fridgelist);
            }

        }

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        createAuthenticationListener();


    }

    private void createAuthenticationListener(){
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser == null){
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
    protected void onResume(){
        super.onResume();
        if (firebaseAuthStateListener != null){
            firebaseAuth.addAuthStateListener(firebaseAuthStateListener);
            Log.i("Triggered", "" + getRecyclerView().toString());
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        if (firebaseAuthStateListener != null){
            firebaseAuth.removeAuthStateListener(firebaseAuthStateListener);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_buttons, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sortDate:
                getRecyclerView().sortListByPrice();
                break;
            case R.id.sortAlphabetical:
                getRecyclerView().sortListAlphabetically();
                break;
            case R.id.moveToFridge:
                moveToFridge();
        }

        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void moveToFridge() {
        for (Item item: shoppingListFragment.getAdapter().getCurrentSelectedItems()) {
            pushToFirebase(item, getDataReferenceProductlist());
            if (shoppingListFragment.getProductList().contains(item)) {
                shoppingListFragment.deleteItem(item);
                Log.i("movefridge", String.valueOf(shoppingListFragment.getProductList().size()));
            }
        }
    }

    private void removeFromFirebase(Item item, DatabaseReference dataReference) {
        DatabaseReference newRef;

        if (item.getItemUid() != null) {
            newRef = dataReference.child(item.getItemUid());
            newRef.removeValue();
            getRecyclerView().getAdapter().notifyDataSetChanged();
        }

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
                break;
            case R.id.nav_shoppinglist:
                isOnShoppingList = true;
                if (shoppingListFragment == null) {
                    shoppingListFragment = new RecyclerViewFragment().newInstance(isOnShoppingList);
                }
                break;
            case R.id.nav_signout:
                mDrawerLayout.closeDrawers();
                AuthUI.getInstance().signOut(this);
                return true;
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                getRecyclerView()).commit();

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
                Item recentlyAddedItem = productList.get(productList.size() - 1);
                firebaseAuth = FirebaseAuth.getInstance();
                firebaseDatabase = FirebaseDatabase.getInstance();
                // Vi trenger en placeholder verdi for at sortering skal funke. Derfor setter vi alle items som ikke har expDate til å være 99/99/9999
                if (recentlyAddedItem.getExpDate().equals("")) {
                    recentlyAddedItem.setExpDate("99/99/9999");
                }
                Log.i("dateAsDate", String.valueOf(recentlyAddedItem.getExpDateAsDate()));
                pushToFirebase(recentlyAddedItem, getDataReference());

            }
        }
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                productList = data.getParcelableArrayListExtra("productList");
                Integer position = data.getIntExtra("pos", 0);
                // TODO: Se på en annen løsning
                productList.set(position, productList.get(productList.size() - 1));
                productList.remove(productList.size() - 1);
                Item editItem = productList.get(position);
                // Vi trenger en placeholder verdi for at sortering skal funke. Derfor setter vi alle items som ikke har expDate til å være 99/99/9999
                if (editItem.getExpDate().equals("")) {
                    editItem.setExpDate("99/99/9999");
                }
                pushToFirebase(editItem, getDataReference());
                getRecyclerView().updateAdapter(productList);
            }
        }
        if (requestCode == RC_SIGN_IN){
            if (resultCode == RESULT_OK) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                Intent refresh = new Intent(this, MainActivity.class);
                startActivity(refresh);
                this.finish();
                Toast.makeText(this,"Logget inn som " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
            } else if(resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Innlogging avbrutt", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

    }

    private void pushToFirebase(Item item, DatabaseReference dataReference) {
        DatabaseReference newRef;
        // If new
        if (item.getItemUid() == null) {
            newRef = dataReference.push();
            item.setItemUid(newRef.getKey());
        }
        else {
            newRef = dataReference.child(item.getItemUid());
        }
        // Vi trenger ikke oppdatere adapteren her lenger siden firebase-implementasjonen (onChildAdded i fragment) legger til den nye varen i den lokale listen
        newRef.setValue(item);
    }

    /***
     * Denne returnerer riktig fragment ut i fra verdien til isOnShoppingList.
     * Vi slipper haugevis med if tester i koden om vi bruker denne
     * @return RecyclerViewFragment
     */
    private RecyclerViewFragment getRecyclerView() {
        if (isOnShoppingList) {
            return shoppingListFragment;
        } else {
            return recyclerViewFragment;
        }
    }


    /***
     * Denne returnerer riktig referanse til lista i Firebase ut i fra verdien til isOnShoppingList
     * Vi slipper if tester i koden om vi bruker denne
     * @return DatabaseReference
     */
    private DatabaseReference getDataReference() {
        if (isOnShoppingList) {
            return firebaseDatabase.getReference("Users").child(firebaseAuth.getCurrentUser().getUid()).child("Shoppinglist");
        } else {
            return firebaseDatabase.getReference("Users").child(firebaseAuth.getCurrentUser().getUid()).child("Productlist");
        }
    }

    private DatabaseReference getDataReferenceShoppinglist() {
        return firebaseDatabase.getReference("Users").child(firebaseAuth.getCurrentUser().getUid()).child("Shoppinglist");
    }

    private DatabaseReference getDataReferenceProductlist() {
        return firebaseDatabase.getReference("Users").child(firebaseAuth.getCurrentUser().getUid()).child("Productlist");
    }

}

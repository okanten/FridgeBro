package no.hiof.fridgebro.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import no.hiof.fridgebro.R;
import no.hiof.fridgebro.fragments.RecyclerViewFragment;
import no.hiof.fridgebro.interfaces.InternetCheck;
import no.hiof.fridgebro.models.Item;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, InternetCheck{
    public static final String SHARED_PREF_FILE = "SharedPrefs";
    public static final int REQUEST_CODE_EDIT_ITEM = 100;
    public static final int REQUEST_CODE_NEW_ITEM_MANUAL = 200;
    public static final int REQUEST_CODE_NEW_ITEM_SCANNER = 300;
    public static final int REQUEST_CODE_ADD_DATE = 400;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private ArrayList<Item> productList = new ArrayList<>();
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    private Toolbar mToolbar;
    private RecyclerViewFragment fridgeListFragment;
    private RecyclerViewFragment shoppingListFragment;

    private boolean isOnShoppingList;

    private static final int RC_SIGN_IN = 1;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference dataReference;

    private ArrayList<Item> queuedItems = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i("Triggered", "onCreate triggered");

        if (!isInternetEnabled()) {
            Toast.makeText(this, R.string.no_internet_message, Toast.LENGTH_SHORT).show();
        }

        Log.d("prefs", "getting prefs");
        pref = getApplicationContext().getSharedPreferences(SHARED_PREF_FILE, MODE_PRIVATE);
        Log.d("prefs", pref.toString());

        isOnShoppingList = pref.getBoolean("isOnShoppingList", false);
        Log.d("prefs", "isOnShoppingList: " + isOnShoppingList);
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
            if (fridgeListFragment == null && !isOnShoppingList) {
                fridgeListFragment = new RecyclerViewFragment();
            } else if (shoppingListFragment == null && isOnShoppingList) {
                shoppingListFragment = new RecyclerViewFragment();
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
            Log.i("Triggered", "" + getRecyclerView());
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
                getRecyclerView().sortListByDate();
                pref.edit().putBoolean("sortedByDate", true).apply();
                break;
            case R.id.sortAlphabetical:
                getRecyclerView().sortListAlphabetically();
                pref.edit().putBoolean("sortedByAlphabetical", true).apply();
                break;
            case R.id.moveToFridge:
                moveToFridge();
                break;
        }

        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void moveToFridge() {
        queuedItems.clear();
        for (Item item: shoppingListFragment.getAdapter().getCurrentSelectedItems()) {
            if(item.getExpDate().equals("99/99/9999")){
                queuedItems.add(item);
            } else {
                pushToFirebase(item, getDataReferenceProductlist());
                shoppingListFragment.deleteItem(item);
            }
        }

        if (!queuedItems.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.move_set_title);
            String grammar = (queuedItems.size() > 1) ? "Noen av varene" : "Én av varene";
            builder.setMessage(String.format(Locale.getDefault(), getResources().getString(R.string.move_set_message), grammar));
            builder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    setDateForItemsBeforeSending();
                }
            });

            builder.setNegativeButton("Nei", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    for (Item item: queuedItems) {
                        pushToFirebase(item, getDataReferenceProductlist());
                        shoppingListFragment.deleteItem(item);
                    }
                }
            });


            AlertDialog dialog = builder.create();
            dialog.show();

        }
        shoppingListFragment.getAdapter().getCurrentSelectedItems().clear();
        shoppingListFragment.getAdapter().notifyDataSetChanged();
    }

    private void removeFromFirebase(Item item, DatabaseReference dataReference) {
        DatabaseReference newRef;

        if (item.getItemUid() != null) {
            newRef = dataReference.child(item.getItemUid());
            newRef.removeValue();
            getRecyclerView().getAdapter().notifyDataSetChanged();
        }

    }

    private void setDateForItemsBeforeSending() {
        if(!queuedItems.isEmpty()) {
            Item item = queuedItems.get(0);
            Intent intent = new Intent(getApplicationContext(), AddActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("productList", queuedItems);
            bundle.putInt("position", queuedItems.indexOf(item));
            bundle.putInt("requestCode", REQUEST_CODE_ADD_DATE);
            intent.putExtras(bundle);
            startActivityForResult(intent, REQUEST_CODE_ADD_DATE);
            queuedItems.remove(item);
            shoppingListFragment.deleteItem(item);
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem Item) {
        switch (Item.getItemId()) {
            case R.id.nav_fridgelist:
                pref.edit().putBoolean("isOnShoppingList", false).apply();
                // Må sjekke om rcv er null i tilfelle brukeren roterer telefonen og bytter fra shoppingList til fridge.
                if (fridgeListFragment == null) {
                    fridgeListFragment = new RecyclerViewFragment();
                }
                break;
            case R.id.nav_shoppinglist:
                pref.edit().putBoolean("isOnShoppingList", true).apply();
                if (shoppingListFragment == null) {
                    shoppingListFragment = new RecyclerViewFragment();
                }
                break;
            case R.id.nav_signout:
                mDrawerLayout.closeDrawers();
                AuthUI.getInstance().signOut(this);
                return true;
        }

        isOnShoppingList = pref.getBoolean("isOnShoppingList", false);


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
        if (resultCode == RESULT_OK) {

            switch (requestCode) {
                case REQUEST_CODE_EDIT_ITEM:
                    productList = data.getParcelableArrayListExtra("productList");
                    Integer position = data.getIntExtra("pos", 0);
                    productList.set(position, productList.get(productList.size() - 1));
                    productList.remove(productList.size() - 1);
                    Item editItem = productList.get(position);
                    // Vi trenger en placeholder verdi for at sortering skal funke.
                    // Derfor setter vi alle items som ikke har expDate til å være 99/99/9999
                    if (editItem.getExpDate().equals("")) {
                        editItem.setExpDate("99/99/9999");
                    }
                    pushToFirebase(editItem, getDataReference());
                    getRecyclerView().updateAdapter(productList);
                    break;

                case REQUEST_CODE_NEW_ITEM_MANUAL:
                case REQUEST_CODE_NEW_ITEM_SCANNER:
                    productList = data.getParcelableArrayListExtra("productList");
                    Item recentlyAddedItem = productList.get(productList.size() - 1);
                    firebaseAuth = FirebaseAuth.getInstance();
                    firebaseDatabase = FirebaseDatabase.getInstance();
                    // Vi trenger en placeholder verdi for at sortering skal funke.
                    // Derfor setter vi alle items som ikke har expDate til å være 99/99/9999
                    if (recentlyAddedItem.getExpDate().equals("")) {
                        recentlyAddedItem.setExpDate("99/99/9999");
                    }
                    Log.i("dateAsDate", String.valueOf(recentlyAddedItem.getExpDateAsDate()));
                    pushToFirebase(recentlyAddedItem, getDataReference());
                    break;

                case REQUEST_CODE_ADD_DATE:
                    Item hasDateSet = data.getParcelableExtra("modifiedItem");
                    pushToFirebase(hasDateSet, getDataReferenceProductlist());
                    setDateForItemsBeforeSending();
                    break;

                case RC_SIGN_IN:
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    Intent refresh = new Intent(this, MainActivity.class);
                    startActivity(refresh);
                    this.finish();
                    Toast.makeText(this, "Logget inn som " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
                    break;

            }
        } else {
            switch (requestCode) {
                case RC_SIGN_IN:
                    Toast.makeText(this, "Innlogging avbrutt", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
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
            return fridgeListFragment;
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

    @Override
    public boolean isInternetEnabled() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }


}

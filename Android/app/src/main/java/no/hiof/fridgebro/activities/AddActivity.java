package no.hiof.fridgebro.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.IDNA;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.request.RequestOptions;

import com.google.android.gms.common.api.Api;
import com.google.gson.JsonObject;

import net.dongliu.requests.Requests;
import net.dongliu.requests.Session;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import no.hiof.fridgebro.R;
import no.hiof.fridgebro.adapters.RecyclerViewAdapter;
import no.hiof.fridgebro.fragments.ContextMenuFragment;
import no.hiof.fridgebro.fragments.InfoFragment;
import no.hiof.fridgebro.fragments.RecyclerViewFragment;
import no.hiof.fridgebro.interfaces.InternetCheck;
import no.hiof.fridgebro.models.Item;
import no.hiof.olaka.*;


public class AddActivity extends AppCompatActivity implements DialogInterface.OnDismissListener, InternetCheck {

    private EditText txtISBN;
    private EditText txtPrice;
    private ImageView imageViewForItem;
    private TextView lblProductName;
    private EditText expDate;
    private ImageButton btnPickDate;
    private ImageButton btnSearch;
    private ImageButton btnShowInfo;
    private Button btnSave;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private JsonObject ngJson;
    private RecyclerViewFragment rcFrag;
    private ArrayList<Item> productList;
    private RecyclerViewAdapter mAdapter;
    private NorgesGruppenAPI ng = new NorgesGruppenAPI(1300);
    private Integer position;
    private Item newItem;
    private String itemDate;
    private Boolean fromScanner = false;
    private ContextMenuFragment contextMenuFragment;
    private InfoFragment infoFragment;
    private Item itemBeforeEdit;
    private ProgressBar loadingJsonProgressbar;
    private int requestCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_add);
        txtISBN = (EditText) findViewById(R.id.txtISBN);
        txtPrice = (EditText) findViewById(R.id.txtPrice);
        imageViewForItem = (ImageView) findViewById(R.id.imgItem);
        lblProductName = (TextView) findViewById(R.id.lblProductName);
        expDate = findViewById(R.id.expDate);
        btnSearch = findViewById(R.id.btnSearch);
        btnPickDate = findViewById(R.id.btnPickDate);
        btnShowInfo = findViewById(R.id.btnShowInfo);
        btnSave = findViewById(R.id.btnSave);
        productList = getIntent().getParcelableArrayListExtra("productList");
        position = getIntent().getIntExtra("position", -1);
        loadingJsonProgressbar = findViewById(R.id.loadingJsonProgressbar);

        Log.d("lolipop", "onCreate: started");

        try {
            requestCode = getIntent().getIntExtra("requestCode", 0);
        } catch (Exception e) {
            Log.d("RQE", e.getMessage());
        }

        if (position > -1) {
            Item getSentItem = productList.get(position);
            setTitle(R.string.add_activity_title_edit);

            itemBeforeEdit = new Item(productList.get(position).getItemName(), productList.get(position).getItemPrice(),
                    productList.get(position).getBarcode(), productList.get(position).getImageUrl(),
                    productList.get(position).getItemBrand(), productList.get(position).getExpDate());
            itemBeforeEdit = productList.get(position);

            txtISBN.setText(productList.get(position).getBarcode());
            txtPrice.setText(productList.get(position).getItemPrice());
            lblProductName.setText(productList.get(position).getItemName());
            if (!getSentItem.getExpDate().equals("99/99/9999")) {
                expDate.setText(productList.get(position).getExpDate());
            }
            Glide.with(getApplicationContext())
                    .load(productList.get(position).getImageUrl())
                    .apply(new RequestOptions().transform(new FitCenter()))
                    .into(imageViewForItem);
        } else {
            itemBeforeEdit = null;
        }



        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPriceFromNg();
            }
        });

        btnSearch.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                makeToast(btnSearch.getContentDescription());
                return true;
            }
        });

        btnPickDate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dateDialog = new DatePickerDialog(AddActivity.this, R.style.DialogTheme, mDateSetListener, year, month, day);
                dateDialog.show();
            }
        });

        btnPickDate.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                makeToast(btnPickDate.getContentDescription());
                return true;
            }
        });


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateListOfItems(v);
            }
        });

        btnSave.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                makeToast(btnSave.getContentDescription());
                return true;
            }
        });

        btnShowInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                infoFragment = InfoFragment.newInstance(R.layout.fragment_info);
                infoFragment.show(fm, "info_fragment");
            }
        });

        btnShowInfo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                makeToast(btnShowInfo.getContentDescription());
                return true;
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month++;
                itemDate = String.format(Locale.getDefault(), "%02d/%02d/%02d", dayOfMonth, month, year);
                expDate.setText(itemDate);
            }
        };

        txtISBN.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    lblProductName.setText(txtISBN.getText());
                }
            }
        });

        txtISBN.addTextChangedListener(new TextWatcher() {


            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                lblProductName.setText(txtISBN.getText());
            }
        });

        if (getIntent().getBooleanExtra("scanner", false)) {
            fromScanner = true;
            String isbn = getIntent().getStringExtra("scannerResult");
            Toast.makeText(this, isbn, Toast.LENGTH_SHORT).show();
            txtISBN.setText(isbn);
            try {
                new asyncLoadJson().execute(isbn);
            } catch (Exception e) {
                Log.d("AddActivity", e.toString());
            }
        }

    }

    public void getPriceFromNg() {
        Log.d("lolipop", "getPriceFromNg called");
        String isbn = String.valueOf(txtISBN.getText());
        Log.d("lolipop", isbn);
        new asyncLoadJson().execute(isbn);
        Log.d("lolipop", "asyncLoadJson executed");
    }

    public void makeToast(CharSequence msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void openItemPickerDialog(ArrayList<Item> queryResult) {
        FragmentManager fm = getSupportFragmentManager();
        contextMenuFragment = ContextMenuFragment.newInstance(queryResult);
        contextMenuFragment.show(fm, "fragment_context_menu");
    }

    public void setNewValues(ArrayList<Item> queryResult, int pos) {
        newItem = queryResult.get(pos);
        txtISBN.setText(newItem.getBarcode());
        lblProductName.setText(newItem.getItemName());
        txtPrice.setText(newItem.getItemPrice());
        String imgUrl = newItem.getImageUrl();
        Glide.with(getApplicationContext())
                .load(imgUrl)
                .apply(new RequestOptions().transform(new FitCenter()))
                .into(imageViewForItem);
    }

    public void updateListOfItems(View view) {
        Item modifiedItem = null;
        // Kjør denne om query har blitt brukt (se setNewValues)
        if (newItem != null) {
            modifiedItem = new Item(String.valueOf(lblProductName.getText()), String.valueOf(txtPrice.getText()), String.valueOf(txtISBN.getText()), String.valueOf(newItem.getImageUrl()), String.valueOf(newItem.getItemBrand()), String.valueOf(expDate.getText()));
            if (itemBeforeEdit != null) {
                try {
                    modifiedItem.setItemUid(itemBeforeEdit.getItemUid());
                } catch (NullPointerException npe){
                    npe.printStackTrace();
                }
            }
            // Kjør denne om det er gjort forandringer, men søk ikke har blitt brukt.
        } else if (itemBeforeEdit != null && !fieldsNotChanged()) {
            modifiedItem = new Item(String.valueOf(lblProductName.getText()), String.valueOf(txtPrice.getText()), String.valueOf(txtISBN.getText()), String.valueOf(itemBeforeEdit.getImageUrl()), String.valueOf(itemBeforeEdit.getItemBrand()), String.valueOf(expDate.getText()), itemBeforeEdit.getItemUid());
        } else if (itemBeforeEdit == null && !fieldsEmpty()) {
            modifiedItem = new Item(String.valueOf(lblProductName.getText()), String.valueOf(txtPrice.getText()), String.valueOf(txtISBN.getText()), null, null, String.valueOf(expDate.getText()), null);
        }
        if (modifiedItem != null) {
            productList.add(modifiedItem);
            Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt("pos", position);
            bundle.putParcelable("modifiedItem", modifiedItem);
            bundle.putParcelableArrayList("productList", productList);
            resultIntent.putExtras(bundle);
            setResult(Activity.RESULT_OK, resultIntent);
        }
        finish();
    }

    private boolean fieldsEmpty() {
        return lblProductName.getText().toString().isEmpty()
                && txtPrice.getText().toString().isEmpty()
                && txtISBN.getText().toString().isEmpty()
                && expDate.getText().toString().isEmpty();
    }

    private boolean fieldsNotChanged() {
        return lblProductName.getText().toString().equals(itemBeforeEdit.getItemName())
                && txtPrice.getText().toString().equals(itemBeforeEdit.getItemPrice())
                && txtISBN.getText().toString().equals(itemBeforeEdit.getBarcode())
                && expDate.getText().toString().equals(itemBeforeEdit.getExpDate());
    }

    public Item getNewItem() {
        return newItem;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        /* Henter ut valgt element og setter nye verdier i viewet. */
        setNewValues(contextMenuFragment.getQueryResult(), contextMenuFragment.getContextMenuAdapter().getPos());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean isInternetEnabled() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }


    public class asyncLoadJson extends AsyncTask<String, Integer, ArrayList<JsonObject>> {
        private ArrayList<Item> queryResult = new ArrayList<>();
        private ArrayList<JsonObject> rJson = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingJsonProgressbar.setVisibility(View.VISIBLE);
            btnSearch.setEnabled(false);
            Log.d("lolipop", "progressbar enabled");
            if (!isInternetEnabled())
                cancel(true);
        }

        @Override
        protected void onCancelled() {
            makeToast("Ingen internett-tilgang. Kan ikke gjennomføre søk.");
            loadingJsonProgressbar.setVisibility(View.GONE);
            btnSearch.setEnabled(true);
            super.onCancelled();
        }



        @Override
        protected ArrayList<JsonObject> doInBackground(String... strings) {
            try {
                for (String isbn : strings) {
                    try {
                        rJson = ng.getFullJson(isbn);
                    } catch (Exception e) {
                        Log.d("NullPointerException", e.getMessage());
                        Toast.makeText(AddActivity.this,
                                "Noe galt skjedde under søk. Vennligst redefiner søkeord",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                return rJson;
            } catch (RuntimeException re) {
                return null;
            }
        }

        protected void onPostExecute(ArrayList<JsonObject> rJson) {
            loadingJsonProgressbar.setVisibility(View.GONE);
            btnSearch.setEnabled(true);
            if (rJson != null) {
                for (int i = 0; i < rJson.size(); i++) {
                    try {
                        queryResult.add(new Item(ng.getTitle(null, rJson.get(i)),
                                ng.getPrice(null, rJson.get(i)),
                                ng.getISBN(null, rJson.get(i)),
                                ng.getImageURL(null, rJson.get(i)),
                                ng.getBrand(null, rJson.get(i))));
                    } catch (Exception npe) {
                        Log.d("NullPointerException", npe.getMessage());
                    }
                }
                if (queryResult != null && queryResult.size() == 1) {
                    setNewValues(queryResult, 0);
                } else if (queryResult.size() > 1) {
                    openItemPickerDialog(queryResult);
                } else {
                    makeToast("Kunne ikke finne produkt/strekkode");
                    if (fromScanner) {
                        Intent failedIntent = new Intent();
                        setResult(Activity.RESULT_CANCELED, failedIntent);
                        finish();
                    }
                }
            } else {
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP
                        || Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP_MR1) {
                    Toast.makeText(AddActivity.this, "Kunne ikke finne produkt/strekkode. " +
                            "Lolipop har for øyeblikket problemer med denne funksjonen",
                            Toast.LENGTH_LONG).show();
                } else {
                    makeToast("Kunne ikke finne produkt/strekkode");
                }
            }
        }
    }
}

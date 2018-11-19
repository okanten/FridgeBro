package no.hiof.fridgebro.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
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

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import no.hiof.fridgebro.R;
import no.hiof.fridgebro.adapters.RecyclerViewAdapter;
import no.hiof.fridgebro.fragments.ContextMenuFragment;
import no.hiof.fridgebro.fragments.RecyclerViewFragment;
import no.hiof.fridgebro.models.Item;
import no.hiof.olaka.*;


public class AddActivity extends AppCompatActivity implements DialogInterface.OnDismissListener {

    private EditText txtISBN;
    private EditText txtPrice;
    private ImageView imgItem;
    private TextView lblProductName;
    private EditText expDate;
    private ImageButton btnPickDate;
    private ImageButton btnSearch;
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
    private Item itemBeforeEdit;
    private ProgressBar loadingJsonProgressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_add);
        txtISBN = (EditText) findViewById(R.id.txtISBN);
        txtPrice = (EditText) findViewById(R.id.txtPrice);
        imgItem = (ImageView) findViewById(R.id.imgItem);
        lblProductName = (TextView) findViewById(R.id.lblProductName);
        expDate = findViewById(R.id.expDate);
        btnScan = findViewById(R.id.btnScan);
        btnSearch = findViewById(R.id.btnSearch);
        btnPickDate = findViewById(R.id.btnPickDate);
        btnSave = findViewById(R.id.btnSave);
        productList = getIntent().getParcelableArrayListExtra("productList");
        position = getIntent().getIntExtra("position", -1);
        loadingJsonProgressbar = findViewById(R.id.loadingJsonProgressbar);


        //     public Item(String itemName, String itemPrice, String barcode, String imageUrl, String itemBrand, String expDate) {

        if (position > -1) {
            setTitle(R.string.add_activity_title_edit);

            itemBeforeEdit = new Item(productList.get(position).getItemName(), productList.get(position).getItemPrice(),
                    productList.get(position).getBarcode(), productList.get(position).getImageUrl(),
                    productList.get(position).getItemBrand(), productList.get(position).getExpDate());
            itemBeforeEdit = productList.get(position);

            txtISBN.setText(productList.get(position).getBarcode());
            txtPrice.setText(productList.get(position).getItemPrice());
            lblProductName.setText(productList.get(position).getItemName());
            expDate.setText(productList.get(position).getExpDate());
            Glide.with(getApplicationContext())
                    .load(productList.get(position).getImageUrl())
                    .apply(new RequestOptions().transform(new FitCenter()))
                    .into(imgItem);
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
        String isbn = String.valueOf(txtISBN.getText());
        new asyncLoadJson().execute(isbn);
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
        lblProductName.setText(queryResult.get(pos).getItemName());
        txtPrice.setText(queryResult.get(pos).getItemPrice());
        String imgUrl = queryResult.get(pos).getImageUrl();
        Glide.with(getApplicationContext())
                .load(imgUrl)
                .apply(new RequestOptions().transform(new FitCenter()))
                .into(imgItem);
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
        } else if (itemBeforeEdit == null && !fieldsNotEmpty()) {
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

    private boolean fieldsNotEmpty() {
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


    public class asyncLoadJson extends AsyncTask<String, Integer, ArrayList<JsonObject>> {
        private ArrayList<Item> queryResult = new ArrayList<>();
        private ArrayList<JsonObject> rJson = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingJsonProgressbar.setVisibility(View.VISIBLE);
            btnSearch.setEnabled(false);
        }

        @Override
        protected ArrayList<JsonObject> doInBackground(String... strings) {
            try {
                for (String isbn : strings) {
                    try {
                        rJson = ng.getFullJson(isbn);
                        System.out.println(rJson.size());
                    } catch (Exception e) {
                        Toast.makeText(AddActivity.this, "Noe galt skjedde under søk. Vennligst redefiner søkeord", Toast.LENGTH_SHORT).show();
                        Log.d("NullPointerException", e.getMessage());
                    }
                }
                System.out.println(rJson.size());
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
                    //productList.add(new Item("Test 1", "1", "11111", "https://i.redd.it/oir304dowbs11.jpg", "TestBrand", "03/03/2019"));
                    try {
                        queryResult.add(new Item(ng.getTitle(null, rJson.get(i)), ng.getPrice(null, rJson.get(i)), ng.getISBN(null, rJson.get(i)), ng.getImageURL(null, rJson.get(i)), ng.getBrand(null, rJson.get(i))));
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
                Intent failedIntent = new Intent();
                setResult(50, failedIntent);
                finish();
            }
        }
    }
}

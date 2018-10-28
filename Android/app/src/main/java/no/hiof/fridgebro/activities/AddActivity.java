package no.hiof.fridgebro.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Calendar;

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
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private JsonObject ngJson;
    private RecyclerViewFragment rcFrag;
    private ArrayList<Item> productList;
    private RecyclerViewAdapter mAdapter;
    private NorgesGruppenAPI ng = new NorgesGruppenAPI(1300);
    private MainActivity mainActivity;
    private Integer position;
    private Item newItem;
    private String itemDate;
    private ContextMenuFragment contextMenuFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        // TODO: Fikse up-activity/parentactivity

        txtISBN = (EditText) findViewById(R.id.txtISBN);
        txtPrice = (EditText) findViewById(R.id.contextPrice);
        imgItem = (ImageView) findViewById(R.id.imgItem);
        lblProductName = (TextView) findViewById(R.id.lblProductName);
        expDate = findViewById(R.id.expDate);
        btnPickDate = findViewById(R.id.btnPickDate);
        productList = getIntent().getParcelableArrayListExtra("productList");
        position = getIntent().getIntExtra("position", -1);
        if (position > -1) {
            txtISBN.setText(productList.get(position).getBarcode());
            txtPrice.setText(productList.get(position).getItemPrice());
            lblProductName.setText(productList.get(position).getItemName());
            expDate.setText(productList.get(position).getExpDate());
            Glide.with(getApplicationContext())
                    .load(productList.get(position).getImageUrl())
                    .apply(new RequestOptions().transform(new FitCenter()))
                    .into(imgItem);
        }

        btnPickDate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dateDialog = new DatePickerDialog(AddActivity.this, R.style.AppTheme, mDateSetListener, year, month, day);
                dateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(R.style.AppTheme));
                dateDialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month++;
                itemDate = new String();
                itemDate = dayOfMonth + "/" + month + "/" + year;
                expDate.setText(itemDate);
            }
        };

     }

    public void getPriceFromNg(View view) {
        String isbn = String.valueOf(txtISBN.getText());
        new asyncLoadJson().execute(isbn);
    }

    public void makeToast() {
        Toast.makeText(this, "Kunne ikke finne strekkode", Toast.LENGTH_SHORT).show();
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
        if (newItem != null) {
            productList.add(newItem);
            Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt("pos", position);
            bundle.putParcelableArrayList("productList", productList);
            resultIntent.putExtras(bundle);
            setResult(Activity.RESULT_OK, resultIntent);
        }
        finish();

    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        /* Henter ut valgt element og setter nye verdier i viewet. */
        setNewValues(contextMenuFragment.getQueryResult(), contextMenuFragment.getContextMenuAdapter().getPos());
    }


    public class asyncLoadJson extends AsyncTask<String, Integer, ArrayList<JsonObject>> {
        private ArrayList<Item> queryResult = new ArrayList<>();
        private ArrayList<JsonObject> rJson = new ArrayList<>();

        @Override
        protected ArrayList<JsonObject> doInBackground(String... strings) {
            try {
                for (String isbn : strings) {
                    rJson = ng.getFullJson(isbn);
                    System.out.println(rJson.size());
                }
                System.out.println(rJson.size());
                return rJson;
            } catch (RuntimeException re) {
                return null;
            }
        }

        protected void onPostExecute(ArrayList<JsonObject> rJson) {
            System.out.println(ng.getImageURL(null, rJson.get(0)));
            for (int i = 0; i < rJson.size(); i++) {
                //productList.add(new Item("Test 1", "1", "11111", "https://i.redd.it/oir304dowbs11.jpg", "TestBrand", "03/03/2019"));
                try {
                    queryResult.add(new Item(ng.getTitle(null, rJson.get(i)), ng.getPrice(null, rJson.get(i)), ng.getISBN(null, rJson.get(i)), ng.getImageURL(null, rJson.get(i)), ng.getBrand(null, rJson.get(i))));
                } catch (NullPointerException npe) {
                    Toast.makeText(AddActivity.this, "Noe galt skjedde under søk. Vennligst redefiner søkeord", Toast.LENGTH_SHORT).show();
                    Log.d("NullPointerException", npe.getMessage());
                }
            }
            if (queryResult != null && queryResult.size() == 1) {
                setNewValues(queryResult, 0);
            } else if (queryResult.size() > 1) {
                openItemPickerDialog(queryResult);
            } else {
                makeToast();
            }

        }
    }
}

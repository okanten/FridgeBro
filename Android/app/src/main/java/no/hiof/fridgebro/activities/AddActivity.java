package no.hiof.fridgebro.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;

import no.hiof.fridgebro.R;
import no.hiof.fridgebro.adapters.RecyclerViewAdapter;
import no.hiof.fridgebro.fragments.RecyclerViewFragment;
import no.hiof.fridgebro.models.Item;
import no.hiof.olaka.*;


public class AddActivity extends AppCompatActivity {

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        // TODO: Fikse up-activity/parentactivity

        txtISBN = (EditText) findViewById(R.id.txtISBN);
        txtPrice = (EditText) findViewById(R.id.txtPrice);
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
        //String isbn = String.valueOf(txtISBN.getText());

        new asyncLoadJson().execute(isbn);
    }

    public void makeToast() {
        Toast.makeText(this, "Kunne ikke finne strekkode", Toast.LENGTH_SHORT).show();
    }

    // TODO: Return metode / en måte å skille mellom shoppinglist og fridgelist.
    // TODO: Vurdere alternativer til ngJson != null
    public void updateListOfItems(View view) {
        if (ngJson != null) {
            productList.add(new Item(ng.getCategoryName(null, ngJson), ng.getTitle(null, ngJson), String.valueOf(txtPrice.getText()), String.valueOf(txtISBN.getText()), ng.getImageURL(null, ngJson), String.valueOf(expDate.getText())));
            Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt("pos", position);
            bundle.putParcelableArrayList("productList", productList);
            resultIntent.putExtras(bundle);
            setResult(Activity.RESULT_OK, resultIntent);
        }
        finish();

    }



    public class asyncLoadJson extends AsyncTask<String, Integer, JsonObject> {
        private JsonObject rJson;

        @Override
        protected JsonObject doInBackground(String... strings) {
            try {
                rJson = new JsonObject();
                /* Klar over at det blir overskrevet om flere blir requestet */
                //for (String isbn: strings) {
                rJson = ng.getJson(strings[0]);
                newItem = new Item(ng.getCategoryName(null, rJson), ng.getTitle(null, rJson), ng.getPrice(null, rJson), strings[0], ng.getImageURL(null, rJson));
                if (itemDate != null) {
                    newItem.setExpDate(itemDate);
                }
                System.out.println(rJson);
                //}
                return rJson;
            } catch (RuntimeException re) {
                return null;
            }
        }

        protected void onPostExecute(JsonObject json) {
            if (json != null) {
                ngJson = json;
                lblProductName.setText(ng.getTitle(null, json));
                txtPrice.setText(ng.getPrice(null, json));
                String imgUrl = ng.getImageURL(null, json);
                Glide.with(getApplicationContext())
                        .load(imgUrl)
                        .apply(new RequestOptions().transform(new FitCenter()))
                        .into(imgItem);
                if (expDate != null) {
                    //newItem.setExpDate();
                }
            } else {
                makeToast();
            }
        }

    }
}

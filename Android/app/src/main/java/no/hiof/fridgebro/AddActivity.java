package no.hiof.fridgebro;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import no.hiof.olaka.*;


public class AddActivity extends AppCompatActivity {

    private EditText txtISBN;
    private EditText txtPrice;
    private ImageView imgItem;
    private TextView lblProductName;
    private JsonObject ngJson;
    private RecyclerViewFragment rcFrag;
    private ArrayList<String> productNames;
    private ArrayList<String> productImages;
    private RecyclerViewAdapter mAdapter;
    private NorgesGruppenAPI ng = new NorgesGruppenAPI(1300);
    private MainActivity mainActivity;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        // TODO: Fikse up-activity/parentactivity

        //getActionBar().setDisplayHomeAsUpEnabled(true);
        txtISBN = (EditText) findViewById(R.id.txtISBN);
        txtPrice = (EditText) findViewById(R.id.txtPrice);
        imgItem = (ImageView) findViewById(R.id.imgItem);
        lblProductName = (TextView) findViewById(R.id.lblProductName);

        productNames = (ArrayList<String>) getIntent().getSerializableExtra("productName");
        productImages = (ArrayList<String>) getIntent().getSerializableExtra("productImage");
        Integer position = getIntent().getIntExtra("position", 0);
        lblProductName.setText(productNames.get(position));
        Glide.with(getApplicationContext())
                .load(productImages.get(position))
                .apply(new RequestOptions().transform(new FitCenter()))
                .into(imgItem);
     }

    public void getPriceFromNg(View view) {
        String isbn = String.valueOf(txtISBN.getText());
        //String isbn = String.valueOf(txtISBN.getText());
        new asyncLoadJson().execute(isbn);
    }

    // TODO: Return metode / en måte å skille mellom shoppinglist og fridgelist.
    // TODO: Vurdere alternativer til ngJson != null
    public void updateListOfItems(View view) {
        if (ngJson != null) {
            productNames.add(ng.getTitle(null, ngJson));
            productImages.add(ng.getImageURL(null, ngJson));
            Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putSerializable("productNames", productNames);
            bundle.putSerializable("productImages", productImages);
            resultIntent.putExtras(bundle);
            setResult(Activity.RESULT_OK, resultIntent);
        }
        finish();

    }


    // TODO: Error handling - Appen kræsjer om den returnerer null.
    public class asyncLoadJson extends AsyncTask<String, Integer, JsonObject> {
        private JsonObject rJson;

        @Override
        protected JsonObject doInBackground(String... strings) {
            rJson = new JsonObject();
            /* Klar over at det blir overskrevet om flere blir requestet */
            //for (String isbn: strings) {
            rJson = ng.getJson(strings[0]);
            System.out.println(rJson);
            //}
            return rJson;
        }

        protected void onPostExecute(JsonObject json) {
            ngJson = json;
            lblProductName.setText(ng.getTitle(null, json));
            txtPrice.setText(ng.getPrice(null, json));
            String imgUrl = ng.getImageURL(null, json);
            Glide.with(getApplicationContext())
                    .load(imgUrl)
                    .apply(new RequestOptions().transform(new FitCenter()))
                    .into(imgItem);
        }

    }
}

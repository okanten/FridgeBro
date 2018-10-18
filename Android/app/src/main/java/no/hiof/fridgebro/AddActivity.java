package no.hiof.fridgebro;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.JsonObject;

import no.hiof.olaka.*;


public class AddActivity extends AppCompatActivity {

    private TextView lblDate;
    private EditText txtISBN;
    private EditText txtPrice;
    private ImageView imgItem;
    private JsonObject ngJson;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        txtISBN = (EditText) findViewById(R.id.txtISBN);
        txtPrice = (EditText) findViewById(R.id.txtPrice);
        imgItem = (ImageView) findViewById(R.id.imgItem);

    }

    public void getPriceFromNg(View view) {
        String isbn = String.valueOf(txtISBN.getText());
        //String isbn = String.valueOf(txtISBN.getText());
        new asyncLoadJson().execute(isbn);
    }

    public class asyncLoadJson extends AsyncTask<String, Integer, JsonObject> {
        private JsonObject rJson;
        private NorgesGruppenAPI ng = new NorgesGruppenAPI(1300);

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
            txtPrice.setText(ng.getPrice(null, json));
            String imgUrl = ng.getImageURL(null, json);
            Glide.with(getApplicationContext())
                    .load(imgUrl)
                    .apply(new RequestOptions().transform(new FitCenter()))
                    .into(imgItem);
        }

    }
}

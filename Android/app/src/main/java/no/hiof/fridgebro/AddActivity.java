package no.hiof.fridgebro;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.RequestOptions;

import no.hiof.olaka.*;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public class AddActivity extends AppCompatActivity {

    private TextView lblDate;
    private EditText txtISBN;
    private EditText txtPrice;
    private ImageView imgItem;

    //private NorgesGruppenAPI ng = new NorgesGruppenAPI(1300);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        txtISBN = (EditText) findViewById(R.id.txtISBN);
        txtPrice = (EditText) findViewById(R.id.txtPrice);
        imgItem = (ImageView) findViewById(R.id.imgItem);

    }

    public void getPriceFromNg(View view) {
        final String[] imgUrl = {null};
        new Thread() {
            public void run() {
                String isbn = String.valueOf(txtISBN.getText());
                NorgesGruppenAPI ng = new NorgesGruppenAPI(1300);
                System.out.println("################");
                System.out.println(ng.getTitle("2000301700003"));
                System.out.println(ng.getImageURL(isbn));
                txtPrice.setText(ng.getPrice(isbn));
                imgUrl[0] = ng.getImageURL(isbn);
            }
        }.start();

        // TODO: Finne en bedre metode å vente på at bildet er lastet ferdig (strengen). Atm henger hele GUI'et.
        while (imgUrl[0] == null);

        Glide.with(getApplicationContext())
                .load(imgUrl[0])
                .apply(new RequestOptions().transform(new FitCenter()))
                .into(imgItem);
    }


}

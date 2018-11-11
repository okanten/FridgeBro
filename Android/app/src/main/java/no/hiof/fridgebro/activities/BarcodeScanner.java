package no.hiof.fridgebro.activities;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.zxing.Result;

import java.util.ArrayList;
import java.util.Objects;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import no.hiof.fridgebro.models.Item;

import static android.Manifest.permission.CAMERA;

public class BarcodeScanner extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView scannerView;
    private ArrayList<Item> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        productList = getIntent().getParcelableArrayListExtra("productList");
        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);
        int currentApiVersion = Build.VERSION.SDK_INT;

        if(currentApiVersion >=  Build.VERSION_CODES.M)
        {
            if(checkPermission())
            {
                Toast.makeText(getApplicationContext(), "Permission already granted!", Toast.LENGTH_LONG).show();
            }
            else
            {
                requestPermission();
            }
        } else {
            openScannerView();
        }



        // Bruk denne om du vil debugge på virtual device.
        /*
        Intent intent = new Intent(this, AddActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("productList", productList);
        bundle.putString("scannerResult", "5060466516304");
        bundle.putBoolean("scanner", true);
        intent.putExtras(bundle);
        ((Activity) Objects.requireNonNull(this)).startActivityForResult(intent, 300);*/

    }

    private boolean checkPermission()
    {
        return (ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission()
    {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, REQUEST_CAMERA);
    }

    @Override
    public void onResume() {
        super.onResume();

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.M) {
            // Denne returnerer alltid false på lollipop / API 21.
            if (checkPermission()) {
                openScannerView();
            } else {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                    requestPermission();
                } else {
                    openScannerView();
                }
            }
        }
    }

    public void openScannerView() {
        if(scannerView == null) {
            scannerView = new ZXingScannerView(this);
            setContentView(scannerView);
        }
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        scannerView.stopCamera();
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA:
                if (grantResults.length > 0) {

                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted){
                        Toast.makeText(getApplicationContext(), "Permission Granted, Now you can access camera", Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(getApplicationContext(), "Permission Denied, You cannot access and camera", Toast.LENGTH_LONG).show();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(CAMERA)) {
                                showMessageOKCancel("You need to allow access to both the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @RequiresApi(api = Build.VERSION_CODES.M)
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                    requestPermissions(new String[]{CAMERA},
                                                            REQUEST_CAMERA);

                                            }
                                        });
                                return;
                            }
                        }
                    }
                }
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new android.support.v7.app.AlertDialog.Builder(BarcodeScanner.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void handleResult(Result result) {
        final String myResult = result.getText();

        Intent intent = new Intent(this, AddActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("productList", productList);
        bundle.putString("scannerResult", myResult);
        bundle.putBoolean("scanner", true);
        intent.putExtras(bundle);
        ((Activity) Objects.requireNonNull(this)).startActivityForResult(intent, 300);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 300) {
            if (resultCode == RESULT_OK) {
                productList = data.getParcelableArrayListExtra("productList");
                Intent resultIntent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("productList", productList);
                resultIntent.putExtras(bundle);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Vennligst ta bilde på nytt", Toast.LENGTH_SHORT).show();
            } else if (resultCode == 50) {
                Toast.makeText(this, "rJson er tom?", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Noe gikk galt", Toast.LENGTH_SHORT).show();
            }
        }

    }


}


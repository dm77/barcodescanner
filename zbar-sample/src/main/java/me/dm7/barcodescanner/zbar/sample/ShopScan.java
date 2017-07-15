package me.dm7.barcodescanner.zbar.sample;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.EditText;

public class ShopScan extends FragmentActivity implements ItemScannedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_scan);
    }

    @Override
    public void itemScanned(String barcode) {
        ((EditText)findViewById(R.id.itemName)).setText("Doom Bar");

    }
}

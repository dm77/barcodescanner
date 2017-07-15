package me.dm7.barcodescanner.zbar.sample;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class ShopScan extends FragmentActivity implements ItemScannedListener {

    long lastPurchaseDate = 0;
    double totalPrice = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_scan);
        findViewById(R.id.donebutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShopScan.this,ShopComplete.class);
                intent.putExtra("price",totalPrice);
                startActivity(intent);
            }
        });
    }

    @Override
    public void itemScanned(String barcode) {
        Log.d("ShopScan",barcode);

        if (System.currentTimeMillis() - lastPurchaseDate > 2000){
            for (ShopItem item:ItemList.items){
                if (item.getBarcode().contains(barcode)){
                    ((EditText)findViewById(R.id.itemName)).setText(item.getName());
                    ((EditText)findViewById(R.id.price)).setText("£" + Double.toString(item.getPrice()));
                    ((EditText)findViewById(R.id.itemnum)).setText("2/34");
                    totalPrice += item.getPrice();
                    ((TextView)findViewById(R.id.totalPrice)).setText("Total Price: £" + totalPrice);
                }

            }
            lastPurchaseDate = System.currentTimeMillis();
        }

    }
}

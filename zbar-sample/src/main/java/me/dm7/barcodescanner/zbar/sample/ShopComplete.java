package me.dm7.barcodescanner.zbar.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class ShopComplete extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_complete);
        Intent intent = getIntent();
        double totalPrice = intent.getDoubleExtra("price",0);
        ((TextView)findViewById(R.id.completeprice)).setText("£" + totalPrice);
    }
}

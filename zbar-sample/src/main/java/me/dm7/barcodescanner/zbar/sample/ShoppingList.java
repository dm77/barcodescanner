package me.dm7.barcodescanner.zbar.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ShoppingList extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);
        findViewById(R.id.findroute).setOnClickListener( new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Intent toNav = new Intent(ShoppingList.this, Nav.class);
                 startActivity(toNav);
             }
        }
        );

    }
}

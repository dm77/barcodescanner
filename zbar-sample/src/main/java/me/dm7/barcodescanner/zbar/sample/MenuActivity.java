package me.dm7.barcodescanner.zbar.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MenuActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        findViewById(R.id.slist).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toShopping = new Intent(MenuActivity.this, ShoppingList.class);
                startActivity(toShopping);
            }
        });
    }

}

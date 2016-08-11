package me.dm7.barcodescanner.zxing.sample;

import android.os.Bundle;

public class SimpleScannerNoViewFinderFragmentActivity extends BaseScannerActivity {
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_simple_scanner_no_view_finder_fragment);
        setupToolbar();
    }
}
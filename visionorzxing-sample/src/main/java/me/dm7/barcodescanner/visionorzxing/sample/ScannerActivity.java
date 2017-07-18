package me.dm7.barcodescanner.visionorzxing.sample;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import me.dm7.barcodescanner.visionorzxing.VisionOrZXingFullScannerFragment;

public class ScannerActivity extends AppCompatActivity implements VisionOrZXingFullScannerFragment.Listener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);


        final VisionOrZXingFullScannerFragment fragment = VisionOrZXingFullScannerFragment.newInstance();
        fragment.setListener(this);
        getFragmentManager().beginTransaction()
                .add(R.id.fl_top_fragment_view_holder, fragment)
                .commit();


    }

    @Override
    public void updateTitle(boolean isVisionOperational) {

        final ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setTitle(isVisionOperational ? "Vision Scanner" : "Google ZXing Scanner");
        }
    }
}

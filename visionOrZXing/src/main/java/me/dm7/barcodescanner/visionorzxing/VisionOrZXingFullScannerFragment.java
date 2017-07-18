package me.dm7.barcodescanner.visionorzxing;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.vision.barcode.BarcodeDetector;

public class VisionOrZXingFullScannerFragment extends Fragment {

    private static final String TAG_VISION_OR_ZXING = "vision_or_zxing";
    private Listener listener;

    public static VisionOrZXingFullScannerFragment newInstance() {

        return new VisionOrZXingFullScannerFragment();
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        final View view = inflater.inflate(R.layout.fragment_vision_or_zxing_holder, container, false);
        getChildFragmentManager().beginTransaction()
                .add(R.id.fl_scanner_fragment, getScanner(), "scanner")
                .commit();
        return view;
    }

    /**
     * @return An instance of {@link VisionFullScannerFragment} If it is ready. Otherwise, it falls back to {@link ZXingFullScannerFragment}.
     */
    public Fragment getScanner() {
        final boolean operational = isOperational();

        if (listener != null) {
            listener.updateTitle(operational);
        }

        if (operational) {
            Log.d(TAG_VISION_OR_ZXING, "VisionFullScannerFragment will be applied");
            return VisionFullScannerFragment.newInstance();
        } else {
            Log.d(TAG_VISION_OR_ZXING, "ZXingFullScannerFragment will be applied");
            return ZXingFullScannerFragment.newInstance();
        }


    }

    public boolean isOperational() {
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(getActivity()).build();
        return barcodeDetector.isOperational();
    }

    public interface Listener {

        void updateTitle(boolean isVisionOperational);
    }
}
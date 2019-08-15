package me.dm7.barcodescanner.zxing.sample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import com.google.zxing.Result
import kotlinx.android.synthetic.main.activity_my_scanner.*
import me.dm7.barcodescanner.zxing.ZXingScannerView

class MyScannerActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {

    override fun handleResult(rawResult: Result) {
        Toast.makeText(this, "Contents = " + rawResult.getText() +
                ", Format = " + rawResult.getBarcodeFormat().toString(), Toast.LENGTH_SHORT).show()

        val handler = Handler()
        handler.postDelayed({ scanner.resumeCameraPreview(this@MyScannerActivity) }, 2000)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_scanner)
    }

    override fun onResume() {
        super.onResume()
        scanner.setResultHandler(this)
        scanner.startCamera()
    }

    override fun onPause() {
        super.onPause()
        scanner.stopCamera()
    }
}

package me.dm7.barcodescanner.vision;


import com.google.android.gms.vision.barcode.Barcode;

import java.util.ArrayList;
import java.util.List;

public class BarcodeFormat {
    public static final BarcodeFormat CODE_128 = new BarcodeFormat(Barcode.CODE_128, "CODE_128");
    public static final BarcodeFormat CODE_39 = new BarcodeFormat(Barcode.CODE_39, "CODE_39");
    public static final BarcodeFormat CODE_93 = new BarcodeFormat(Barcode.CODE_93, "CODE_93");
    public static final BarcodeFormat CODABAR = new BarcodeFormat(Barcode.CODABAR, "CODABAR");
    public static final BarcodeFormat DATA_MATRIX = new BarcodeFormat(Barcode.DATA_MATRIX, "DATA_MATRIX");
    public static final BarcodeFormat EAN_13 = new BarcodeFormat(Barcode.EAN_13, "EAN_13");
    public static final BarcodeFormat EAN_8 = new BarcodeFormat(Barcode.EAN_8, "EAN_8");
    public static final BarcodeFormat ITF = new BarcodeFormat(Barcode.ITF, "ITF");
    public static final BarcodeFormat QR_CODE = new BarcodeFormat(Barcode.QR_CODE, "QR_CODE");
    public static final BarcodeFormat UPC_A = new BarcodeFormat(Barcode.UPC_A, "UPC_A");
    public static final BarcodeFormat UPC_E = new BarcodeFormat(Barcode.UPC_E, "UPC_E");
    public static final BarcodeFormat PDF417 = new BarcodeFormat(Barcode.PDF417, "PDF417");
    public static final BarcodeFormat AZTEC = new BarcodeFormat(Barcode.AZTEC, "AZTEC");
    public static final List<BarcodeFormat> ALL_FORMATS = new ArrayList<BarcodeFormat>();

    static {
        ALL_FORMATS.add(BarcodeFormat.CODE_128);
        ALL_FORMATS.add(BarcodeFormat.CODE_39);
        ALL_FORMATS.add(BarcodeFormat.CODE_93);
        ALL_FORMATS.add(BarcodeFormat.CODABAR);
        ALL_FORMATS.add(BarcodeFormat.DATA_MATRIX);
        ALL_FORMATS.add(BarcodeFormat.EAN_13);
        ALL_FORMATS.add(BarcodeFormat.EAN_8);
        ALL_FORMATS.add(BarcodeFormat.ITF);
        ALL_FORMATS.add(BarcodeFormat.QR_CODE);
        ALL_FORMATS.add(BarcodeFormat.UPC_A);
        ALL_FORMATS.add(BarcodeFormat.UPC_E);
        ALL_FORMATS.add(BarcodeFormat.PDF417);
        ALL_FORMATS.add(BarcodeFormat.AZTEC);
    }

    private int mId;
    private String mName;

    public BarcodeFormat(int id, String name) {
        mId = id;
        mName = name;
    }

    public static BarcodeFormat getFormatById(int id) {
        for (BarcodeFormat format : ALL_FORMATS) {
            if (format.getId() == id) {
                return format;
            }
        }
        throw new IllegalArgumentException("Invalid Barcode Format id : " + id);
    }

    public int getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }
}
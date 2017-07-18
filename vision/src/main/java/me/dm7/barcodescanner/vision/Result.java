package me.dm7.barcodescanner.vision;

import com.google.android.gms.vision.barcode.Barcode;

public class Result {
    private Barcode barcode;

    private Result(Builder builder) {
        this.barcode = builder.barcode;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public Barcode getBarcode() {
        return barcode;
    }

    public static final class Builder {
        private Barcode barcode;

        private Builder() {
        }

        public Builder withBarcode(Barcode val) {
            barcode = val;
            return this;
        }

        public Result build() {
            return new Result(this);
        }
    }
}
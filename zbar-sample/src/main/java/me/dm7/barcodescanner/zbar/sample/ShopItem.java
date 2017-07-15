package me.dm7.barcodescanner.zbar.sample;

/**
 * Created by opostlethwaite on 7/15/2017.
 */

public class ShopItem {
    private String barcode;
    private String name;
    private String price;
    public ShopItem(String barcode, String name, String price){
        this.barcode = barcode;
        this.name = name;
        this.price = price;
    }
    public String getBarcode(){
        return barcode;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }
}

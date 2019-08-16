package me.dm7.barcodescanner.zbar

import net.sourceforge.zbar.Symbol

class BarcodeFormat constructor(var id: Int, var name: String) {

    companion object {
        val NONE: BarcodeFormat = BarcodeFormat(Symbol.NONE, "NONE")
        val PARTIAL: BarcodeFormat = BarcodeFormat(Symbol.PARTIAL, "PARTIAL")
        val EAN8: BarcodeFormat = BarcodeFormat(Symbol.EAN8, "EAN8")
        val UPCE: BarcodeFormat = BarcodeFormat(Symbol.UPCE, "UPCE")
        val ISBN10: BarcodeFormat = BarcodeFormat(Symbol.ISBN10, "ISBN10")
        val UPCA: BarcodeFormat = BarcodeFormat(Symbol.UPCA, "UPCA")
        val EAN13: BarcodeFormat = BarcodeFormat(Symbol.EAN13, "EAN13")
        val ISBN13: BarcodeFormat = BarcodeFormat(Symbol.ISBN13, "ISBN13")
        val I25: BarcodeFormat = BarcodeFormat(Symbol.I25, "I25")
        val DATABAR: BarcodeFormat = BarcodeFormat(Symbol.DATABAR, "DATABAR")
        val DATABAR_EXP: BarcodeFormat = BarcodeFormat(Symbol.DATABAR_EXP, "DATABAR_EXP")
        val CODABAR: BarcodeFormat = BarcodeFormat(Symbol.CODABAR, "CODABAR")
        val CODE39: BarcodeFormat = BarcodeFormat(Symbol.CODE39, "CODE39")
        val PDF417: BarcodeFormat = BarcodeFormat(Symbol.PDF417, "PDF417")
        val QRCODE: BarcodeFormat = BarcodeFormat(Symbol.QRCODE, "QRCODE")
        val CODE93: BarcodeFormat = BarcodeFormat(Symbol.CODE93, "CODE93")
        val CODE128: BarcodeFormat = BarcodeFormat(Symbol.CODE128, "CODE128")

        val ALL_FORMATS = mutableListOf<BarcodeFormat>(
                PARTIAL,
                EAN8,
                UPCE,
                ISBN10,
                UPCA,
                EAN13,
                ISBN13,
                I25,
                DATABAR,
                DATABAR_EXP,
                CODABAR,
                CODE39,
                PDF417,
                QRCODE,
                CODE93,
                CODE128
        )

        fun getFormatById(id: Int): BarcodeFormat {
            for (format in ALL_FORMATS) {
                if (format.id == id) {
                    return format
                }
            }
            return NONE
        }
    }
}
package me.dm7.barcodescanner.zbar.sample;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import java.util.ArrayList;

import me.dm7.barcodescanner.zbar.BarcodeFormat;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

public class FormatSelectorDialogFragment extends DialogFragment {
    public interface FormatSelectorDialogListener {
        public void onFormatsSaved(ArrayList<Integer> selectedIndices);
    }

    private ArrayList<Integer> mSelectedIndices;
    private FormatSelectorDialogListener mListener;

    public void onCreate(Bundle state) {
        super.onCreate(state);
        setRetainInstance(true);
    }

    public static FormatSelectorDialogFragment newInstance(FormatSelectorDialogListener listener, ArrayList<Integer> selectedIndices) {
        FormatSelectorDialogFragment fragment = new FormatSelectorDialogFragment();
        if(selectedIndices == null) {
            selectedIndices = new ArrayList<Integer>();
        }
        fragment.mSelectedIndices = new ArrayList<Integer>(selectedIndices);
        fragment.mListener = listener;
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if(mSelectedIndices == null || mListener == null) {
            dismiss();
            return null;
        }

        String[] formats = new String[BarcodeFormat.ALL_FORMATS.size()];
        boolean[] checkedIndices = new boolean[BarcodeFormat.ALL_FORMATS.size()];
        int i = 0;
        for(BarcodeFormat format : BarcodeFormat.ALL_FORMATS) {
            formats[i] = format.getName();
            if(mSelectedIndices.contains(i)) {
                checkedIndices[i] = true;
            } else {
                checkedIndices[i] = false;
            }
            i++;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Set the dialog title
        builder.setTitle(R.string.choose_formats)
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setMultiChoiceItems(formats, checkedIndices,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                if (isChecked) {
                                    // If the user checked the item, add it to the selected items
                                    mSelectedIndices.add(which);
                                } else if (mSelectedIndices.contains(which)) {
                                    // Else, if the item is already in the array, remove it
                                    mSelectedIndices.remove(mSelectedIndices.indexOf(which));
                                }
                            }
                        })
                        // Set the action buttons
                .setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK, so save the mSelectedIndices results somewhere
                        // or return them to the component that opened the dialog
                        if (mListener != null) {
                            mListener.onFormatsSaved(mSelectedIndices);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

        return builder.create();
    }
}

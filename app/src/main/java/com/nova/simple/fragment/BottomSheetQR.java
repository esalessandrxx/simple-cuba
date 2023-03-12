package com.nova.simple.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.nova.simple.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.nova.simple.databinding.BottomSheetComprasBinding;
import com.nova.simple.databinding.BottomSheetQrcodeBinding;

public class BottomSheetQR extends BottomSheetDialogFragment {

    BottomSheetQrcodeBinding binding;
    BottomSheetBehavior behavior;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        View view = View.inflate(getContext(), R.layout.bottom_sheet_qrcode, null);
        binding = BottomSheetQrcodeBinding.bind(view);
        dialog.setContentView(view);
        behavior = BottomSheetBehavior.from((View) view.getParent());

        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    /* @Override
    public void onItemClick(Item item) {
        mBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }*/
}

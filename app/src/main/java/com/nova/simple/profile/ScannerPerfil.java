package com.nova.simple.profile;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.telecom.PhoneAccountHandle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.material.elevation.SurfaceColors;
import com.nova.simple.R;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.nova.simple.databinding.BottomSheetQrcodeBinding;
import com.nova.simple.databinding.LayoutScannerPerfilBinding;
import java.util.List;

public class ScannerPerfil extends AppCompatActivity {

    private LayoutScannerPerfilBinding binding;
    private BottomSheetQrcodeBinding qrBinding;

    // TODO: account handle and sim slot
    private List<PhoneAccountHandle> phoneAccountHandleList;
    private static final String simSlotName[] = {
        "extra_asus_dial_use_dualsim",
        "com.android.phone.extra.slot",
        "slot",
        "simslot",
        "sim_slot",
        "subscription",
        "Subscription",
        "phone",
        "com.android.phone.DialingMode",
        "simSlot",
        "slot_id",
        "simId",
        "simnum",
        "phone_type",
        "slotId",
        "slotIdx"
    };
    // TODO: preference dualSim
    SharedPreferences sp_sim;
    String sim;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = LayoutScannerPerfilBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // NavigationBar color
        getWindow().setNavigationBarColor(SurfaceColors.SURFACE_0.getColor(this));

        // TODO: Preferences DualSIM
        sp_sim = PreferenceManager.getDefaultSharedPreferences(this);
        sim = (sp_sim.getString(getString(R.string.sim_key), "0"));

        // TODO: Brillo en pantalla
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = 100 / 100.0f;
        getWindow().setAttributes(lp);

        try {
            SharedPreferences sp_perfil = getSharedPreferences("profile", Context.MODE_PRIVATE);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            bitmap =
                    barcodeEncoder.encodeBitmap(
                            sp_perfil.getString("numero", "").toString(),
                            BarcodeFormat.QR_CODE,
                            400,
                            400);
            binding.qrCodePerfil.setImageBitmap(bitmap);
        } catch (Exception e) {

        }

        // escanear código qr
        binding.scannerPerfil.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        ScanOptions scanner = new ScanOptions();
                        scanner.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
                        scanner.setPrompt("Enfoque el código QR");
                        scanner.setOrientationLocked(true);
                        barcodeLauncher.launch(scanner);
                    }
                });
        // share codigo qr
        binding.shareQrPerfil.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        String bitmapPath =
                                MediaStore.Images.Media.insertImage(
                                        getContentResolver(), bitmap, "codeQR", null);
                        if (bitmapPath == null) {
                            Toast.makeText(
                                            getApplicationContext(),
                                            "Aquí no hay nada que compartir",
                                            Toast.LENGTH_LONG)
                                    .show();
                        } else {
                            Uri bitmapUri = Uri.parse(bitmapPath);
                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.setType("image/png");
                            intent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
                            startActivity(Intent.createChooser(intent, "Compartir"));
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, PerfilActivity.class));
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        binding = null;
        super.onDestroy();
    }

    private void USSD(String code) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this, new String[] {Manifest.permission.CALL_PHONE}, 0);
            } else {
                Intent saldo =
                        new Intent(Intent.ACTION_CALL).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                saldo.setData(Uri.parse("tel:" + code));
                saldo.putExtra("com.android.phone.force.slot", true);
                saldo.putExtra("Cdma_Supp", true);
                if (sim.equals("0")) {
                    for (String s : simSlotName) {
                        saldo.putExtra(s, 0);
                    }
                    if (phoneAccountHandleList != null && phoneAccountHandleList.size() > 0) {
                        saldo.putExtra(
                                "android.telecom.extra.PHONE_ACCOUNT_HANDLE",
                                phoneAccountHandleList.get(0));
                    }

                } else if (sim.equals("1")) {
                    for (String s : simSlotName) {
                        saldo.putExtra(s, 1);
                    }
                    if (phoneAccountHandleList != null && phoneAccountHandleList.size() > 1) {
                        saldo.putExtra(
                                "android.telecom.extra.PHONE_ACCOUNT_HANDLE",
                                phoneAccountHandleList.get(1));
                    }
                }
                startActivity(saldo);
            }
        }
    }

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher =
            registerForActivityResult(
                    new ScanContract(),
                    result -> {
                        if (result.getContents() == null) {
                            Toast.makeText(this, "Cancelado!", Toast.LENGTH_LONG).show();
                        } else {
                            BottomSheetDialog dialog = new BottomSheetDialog(this);
                            qrBinding = BottomSheetQrcodeBinding.inflate(LayoutInflater.from(this));
                            dialog.setContentView(qrBinding.getRoot());

                            // buttom transferir
                            qrBinding.buttomScannerTransferir.setOnClickListener(
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View arg0) {

                                            String numero = result.getContents().toString();
                                            String monto =
                                                    qrBinding.monto.getText().toString().trim();
                                            String clave =
                                                    qrBinding.clave.getText().toString().trim();
                                            USSD(
                                                    "*234*1*"
                                                            + numero
                                                            + "*"
                                                            + clave
                                                            + "*"
                                                            + monto
                                                            + Uri.encode("#"));
                                            dialog.dismiss();
                                        }
                                    });
                            dialog.show();
                        }
                    });
}

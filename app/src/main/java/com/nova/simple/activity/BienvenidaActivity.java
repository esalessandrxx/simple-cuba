package com.nova.simple.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import androidx.core.app.ActivityCompat;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.elevation.SurfaceColors;
import com.nova.simple.R;
import android.view.LayoutInflater;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.nova.simple.activity.BienvenidaActivity;
import com.nova.simple.databinding.ActivityBienvenidaBinding;

public class BienvenidaActivity extends AppCompatActivity {

    private ActivityBienvenidaBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBienvenidaBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        getWindow().setNavigationBarColor(SurfaceColors.SURFACE_0.getColor(this));
        comprobar_permisos();

        assert binding.btnNext != null;
        binding.btnNext.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(BienvenidaActivity.this, MainActivity.class));
                        finish();
                        CargarPreferencias(BienvenidaActivity.this, "Inciado");
                    }
                });

        // TODO: Terminos y Condiciones de uso
        binding.textTerminos.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        new MaterialAlertDialogBuilder(BienvenidaActivity.this)
                                .setTitle("Términos de Uso")
                                .setMessage(getString(R.string.terminos_de_uso))
                                .setPositiveButton("Ok", null)
                                .show();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        binding = null;
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    public void CargarPreferencias(Activity contex, String inicio) {
        SharedPreferences Preferencias =
                contex.getSharedPreferences("Permisos", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = Preferencias.edit();
        editor.putString("inicio", inicio);
        editor.apply();
    }

    public void comprobar_permisos() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                            + ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                            + ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                            + ContextCompat.checkSelfPermission(
                                    this, Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {
                binding.btnNext.setEnabled(false);
            } else {
                binding.btnNext.setEnabled(true);
            }
        }
    }

    public void permiso_llamada() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED) {
            } else {
                binding.btnLlamadas.setEnabled(false);
                binding.btnLlamadas.setIconResource(R.drawable.ic_permiso_check);
            }
            binding.btnLlamadas.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(
                                    BienvenidaActivity.this,
                                    new String[] {Manifest.permission.CALL_PHONE},
                                    0);
                        }
                    });
        }
    }

    public void permiso_contacto() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {
            } else {
                binding.btnContactos.setEnabled(false);
                binding.btnContactos.setIconResource(R.drawable.ic_permiso_check);
            }
            binding.btnContactos.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(
                                    BienvenidaActivity.this,
                                    new String[] {Manifest.permission.READ_CONTACTS},
                                    0);
                        }
                    });
        }
    }

    public void permiso_camara() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
            } else {
                binding.btnCamara.setEnabled(false);
                binding.btnCamara.setIconResource(R.drawable.ic_permiso_check);
            }
            binding.btnCamara.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(
                                    BienvenidaActivity.this,
                                    new String[] {Manifest.permission.CAMERA},
                                    0);
                        }
                    });
        }
    }

    public void permiso_sms() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED) {
            } else {
                binding.btnSms.setEnabled(false);
                binding.btnSms.setIconResource(R.drawable.ic_permiso_check);
            }
            binding.btnSms.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(
                                    BienvenidaActivity.this,
                                    new String[] {Manifest.permission.SEND_SMS},
                                    0);
                        }
                    });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        comprobar_permisos();
        permiso_llamada();
        permiso_contacto();
        permiso_camara();
        permiso_sms();
    }
}

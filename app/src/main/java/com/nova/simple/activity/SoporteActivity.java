package com.nova.simple.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.elevation.SurfaceColors;
import com.nova.simple.BuildConfig;
import com.nova.simple.R;
import com.nova.simple.activity.MainActivity;
import com.nova.simple.databinding.ActivitySoporteBinding;

public class SoporteActivity extends AppCompatActivity {

    private ActivitySoporteBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySoporteBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        // TODO: navigationview color
        getWindow().setNavigationBarColor(SurfaceColors.SURFACE_0.getColor(this));

        // TODO: Reportar
        binding.buttonSendReporte.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        sendReporte();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
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

    private void sendReporte() {
        String versionName = BuildConfig.VERSION_NAME;
        int versionCode = BuildConfig.VERSION_CODE;
        StringBuilder info = new StringBuilder();
        info.append("VERSION: ");
        info.append(versionName);
        info.append("\n");
        info.append("VERSION CODE: ");
        info.append(Integer.toString(versionCode));
        info.append("\n");
        info.append("SDK: ");
        info.append(Build.VERSION.SDK);
        String[] tipo = getResources().getStringArray(R.array.reporte);
        ArrayAdapter adapter =
                new ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, tipo);
        binding.autocompleteReporte.setAdapter(adapter);
        String message = binding.editMessageReporte.getText().toString().trim();
        String report = binding.autocompleteReporte.getText().toString().trim();

        // TODO: SEND EMAIL
        Intent send = new Intent("android.intent.action.SENDTO");
        send.putExtra("android.intent.extra.EMAIL", new String[] {"simpleapp@zohomail.com"});
        send.putExtra("android.intent.extra.SUBJECT", report);
        send.putExtra("android.intent.extra.TEXT", info + "\n\nMensaje: " + message);
        send.setType("text/plain");
        send.setData(Uri.parse("mailto:"));
        startActivity(send);
    }
}

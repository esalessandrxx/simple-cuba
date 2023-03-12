package com.nova.simple.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.nova.simple.activity.CrashActivity;
import com.nova.simple.crashapp.ExceptionHandler;
import com.nova.simple.databinding.LayoutCrashReportBinding;

public class CrashActivity extends AppCompatActivity {

    private LayoutCrashReportBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        super.onCreate(savedInstanceState);
        binding = LayoutCrashReportBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        binding.logText.setText(getIntent().getStringExtra("error"));

        // TODO: Close App
        binding.closeButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        CrashActivity.this.finish();
                        System.exit(0);
                    }
                });

        // TODO: Enviar el error por correo
        binding.reportButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        Intent send = new Intent("android.intent.action.SENDTO");
                        send.putExtra(
                                "android.intent.extra.EMAIL",
                                new String[] {"simpleapp@zohomail.com"});
                        send.putExtra("android.intent.extra.SUBJECT", "BUG REPORT");
                        send.putExtra(
                                "android.intent.extra.TEXT", getIntent().getStringExtra("error"));
                        send.setType("text/plain");
                        send.setData(Uri.parse("mailto:"));
                        startActivity(send);
                    }
                });
    }
}

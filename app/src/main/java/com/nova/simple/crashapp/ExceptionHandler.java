package com.nova.simple.crashapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import com.nova.simple.BuildConfig;
import com.nova.simple.activity.CrashActivity;
import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionHandler implements java.lang.Thread.UncaughtExceptionHandler {

    private final Activity myContext;
    private final String LINE_SEPARATOR = "\n";

    public ExceptionHandler(Activity context) {
        myContext = context;
    }

    public void uncaughtException(Thread thread, Throwable exception) {
        StringWriter stackTrace = new StringWriter();
        exception.printStackTrace(new PrintWriter(stackTrace));
        StringBuilder errorReport = new StringBuilder();
        errorReport.append("************ CAUSA DEL ERROR ************\n\n");
        errorReport.append(stackTrace.toString());

        errorReport.append("\n************ INFORMACIÓN ***********\n");
        errorReport.append("Brand: ");
        errorReport.append(Build.BRAND);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Model: ");
        errorReport.append(Build.MODEL);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("SDK: ");
        errorReport.append(Build.VERSION.SDK);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Release: ");
        errorReport.append(BuildConfig.VERSION_NAME);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Version Code: ");
        errorReport.append(BuildConfig.VERSION_CODE);
        errorReport.append(LINE_SEPARATOR);

        Intent intent = new Intent(myContext, CrashActivity.class);
        intent.putExtra("error", errorReport.toString());
        myContext.startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(10);
    }
}

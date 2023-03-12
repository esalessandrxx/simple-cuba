package com.nova.simple.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.TrafficStats;
import android.os.Build;
import com.nova.simple.R;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import androidx.annotation.Nullable;
import com.nova.simple.databinding.LayoutFloatingWindowBinding;

public class FloatingWindow extends Service {

    private WindowManager wm;
    private View floatingView, collapsedView, expandedView;
    WindowManager.LayoutParams params;
    private LayoutFloatingWindowBinding binding;

    // SpeedNetwork
    private Handler handler = new Handler(Looper.getMainLooper());
    private long lastRxBytes = 0;
    private long lastTxBytes = 0;
    private long lastTime = 0;

    private final Runnable runnable =
            new Runnable() {
                public void run() {
                    long currentRxBytes = TrafficStats.getTotalRxBytes();
                    long currentTxBytes = TrafficStats.getTotalTxBytes();
                    long usedRxBytes = currentRxBytes - lastRxBytes;
                    long usedTxBytes = currentTxBytes - lastTxBytes;
                    long currentTime = System.currentTimeMillis();
                    long usedTime = currentTime - lastTime;

                    lastRxBytes = currentRxBytes;
                    lastTxBytes = currentTxBytes;
                    lastTime = currentTime;

                    binding.textTraficData.setText(
                            calculateSpeed(usedTime, usedRxBytes, usedTxBytes));
                    handler.postDelayed(runnable, 1000);
                }

                public String calculateSpeed(long timeTaken, long downBytes, long upBytes) {
                    long downSpeed = 0;
                    long upSpeed = 0;

                    if (timeTaken > 0) {
                        downSpeed = downBytes * 1000 / timeTaken;
                        upSpeed = upBytes * 1000 / timeTaken;
                    }
                    final long mDownSpeed = downSpeed;
                    final long mUpSpeed = upSpeed;

                    String down = setSpeed(mDownSpeed);
                    String up = setSpeed(mUpSpeed);

                    StringBuilder sb = new StringBuilder();
                    sb.append("↓ ").append(down).append(" ↑ ").append(up);
                    binding.textTraficData.setText(sb.toString());
                    return sb.toString();
                }

                private String setSpeed(long speed) {
                    if (speed < 1000000) {
                        return String.format("%.1f KB", (speed / 1000.0));
                    } else if (speed >= 1000000) {
                        if (speed < 10000000) {
                            return String.format("%.1f MB", (speed / 1000000.0));
                        } else if (speed < 100000000) {
                            return String.format("%.1f MB", (speed / 1000000.0));
                        } else {
                            return "+99 MB";
                        }
                    } else {
                        return "";
                    }
                }
            };

    public FloatingWindow() {}

    @Override
    public void onCreate() {
        super.onCreate();
        lastRxBytes = TrafficStats.getTotalRxBytes();
        lastTxBytes = TrafficStats.getTotalTxBytes();
        lastTime = System.currentTimeMillis();
        initFloatingWindow();
        handler.post(runnable);

        // TODO: Comprobar tipo de red
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    binding.imageRed.setImageResource(R.drawable.ic_signal_datos);
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    binding.imageRed.setImageResource(R.drawable.ic_signal_wifi);
                } else {
                    binding.imageRed.setImageResource(R.drawable.ic_signal_datos);
                }
            }
        }
    }

    private void initFloatingWindow() {
        /*    DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;*/

        binding = LayoutFloatingWindowBinding.inflate(LayoutInflater.from(this));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            params =
                    new WindowManager.LayoutParams(
                            WindowManager.LayoutParams.WRAP_CONTENT,
                            WindowManager.LayoutParams.WRAP_CONTENT,
                            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                            PixelFormat.TRANSLUCENT);
        } else {
            params =
                    new WindowManager.LayoutParams(
                            WindowManager.LayoutParams.WRAP_CONTENT,
                            WindowManager.LayoutParams.WRAP_CONTENT,
                            WindowManager.LayoutParams.TYPE_PHONE,
                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                            PixelFormat.TRANSLUCENT);
        }
        params.gravity = Gravity.TOP;
        params.x = 0;
        params.y = 100;
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        wm.addView(binding.getRoot(), params);

        // TODO: TouchListener
        binding.floatingWindow.setOnTouchListener(
                new View.OnTouchListener() {
                    int x = 0;
                    int y = 0;

                    float touchX;
                    float touchY;

                    @Override
                    public boolean onTouch(View view, MotionEvent event) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                x = params.x;
                                y = params.y;
                                touchX = event.getRawX();
                                touchY = event.getRawY();
                                return true;
                            case MotionEvent.ACTION_MOVE:
                                params.x = x + (int) (event.getRawX() - touchX);
                                params.y = y + (int) (event.getRawY() - touchY);
                                wm.updateViewLayout(binding.getRoot(), params);
                                return true;
                        }
                        return false;
                    }
                });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (binding != null) {
            wm.removeView(binding.getRoot());
        }
    }
}

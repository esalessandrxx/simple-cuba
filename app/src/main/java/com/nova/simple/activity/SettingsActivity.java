package com.nova.simple.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;

import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.preference.ListPreference;
import androidx.preference.MultiSelectListPreference;
import androidx.preference.PreferenceFragmentCompat;

import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;
import com.google.android.material.elevation.SurfaceColors;
import com.google.zxing.integration.android.IntentIntegrator;
import com.nova.simple.R;
import com.nova.simple.UiTheme;
import com.nova.simple.databinding.ActivitySettingsBinding;
import com.nova.simple.services.FloatingWindow;
import java.util.concurrent.Executor;

public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        // TODO: navigationview color
        getWindow().setNavigationBarColor(SurfaceColors.SURFACE_0.getColor(this));

        // TODO: cargar fragment xml con el preference screen
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.frame_layout, new SettingsPreference())
                    .commit();
        }
    }

    public static class SettingsPreference extends PreferenceFragmentCompat
            implements SharedPreferences.OnSharedPreferenceChangeListener {

        // TODO: Biometric

        @Override
        public void onCreatePreferences(Bundle arg0, String arg1) {
            setPreferencesFromResource(R.xml.ajustes_preferences, arg1);

            // TODO: MultiSelectListPreference
            MultiSelectListPreference home =
                    getPreferenceManager().findPreference(getString(R.string.home_key));
            home.setEnabled(false);

            // TODO: Autocompletado
            SwitchPreferenceCompat autocomplete =
                    getPreferenceManager().findPreference(getString(R.string.autocomplete_key));
            autocomplete.setEnabled(false);

            // TODO: Seguridad
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                SwitchPreferenceCompat bloqueo =
                        getPreferenceManager().findPreference(getString(R.string.biometric_key));
                bloqueo.setEnabled(true);
            } else {
                SwitchPreferenceCompat bloqueo =
                        getPreferenceManager().findPreference(getString(R.string.biometric_key));
                bloqueo.setEnabled(false);
            }

            // TODO: Theme UI
            ListPreference theme =
                    getPreferenceManager().findPreference(getString(R.string.ui_theme_key));
            if (theme.getValue() == null) {
                theme.setValue(UiTheme.Mode.DEFAULT.name());
            }
            theme.setOnPreferenceChangeListener(
                    (preference, newValue) -> {
                        UiTheme.applyTheme(UiTheme.Mode.valueOf((String) newValue));
                        return true;
                    });
        }

        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(getString(R.string.floating_key))) {
                boolean isChecked =
                        sharedPreferences.getBoolean(getString(R.string.floating_key), false);
                if (isChecked) {
                    requestPermissionDrawOverlay();
                    iniciarServicio();
                } else {
                    stopServicio();
                }
                return;
            }

            // TODO: Seguridad
            if (key.equals(getString(R.string.biometric_key))) {
                boolean isCheck =
                        sharedPreferences.getBoolean(getString(R.string.biometric_key), false);
                if (isCheck) {

                } else {
                }
            }
        }

        private void iniciarServicio() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // chequear si hay permiso de superpicision
                if (Settings.canDrawOverlays(getActivity())) {
                    getActivity().startService(new Intent(getActivity(), FloatingWindow.class));
                }
            }
        }

        private void stopServicio() {
            Intent intent = new Intent(getActivity(), FloatingWindow.class);
            getActivity().stopService(intent);
        }

        private void requestPermissionDrawOverlay() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(getActivity())) {
                    Intent intent =
                            new Intent(
                                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                    Uri.parse("package:" + getActivity().getPackageName()));
                    activityResult.launch(intent);
                } else {
                    if (!Settings.canDrawOverlays(getActivity())) {
                        getActivity().startService(new Intent(getActivity(), FloatingWindow.class));
                    }
                }
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            SharedPreferences sp_floating =
                    PreferenceManager.getDefaultSharedPreferences(getActivity());
            if (sp_floating.getBoolean(getString(R.string.floating_key), false)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (Settings.canDrawOverlays(getContext())) {
                        getActivity().startService(new Intent(getActivity(), FloatingWindow.class));
                    }
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!Settings.canDrawOverlays(getActivity())) {
                        getActivity().startService(new Intent(getActivity(), FloatingWindow.class));
                    }
                }
            }

            getPreferenceScreen()
                    .getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);
        }

        private boolean getDrawPermissionState() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (Settings.canDrawOverlays(getActivity()));
            }
            return true;
        }

        @Override
        public void onStart() {
            super.onStart();
            getPreferenceScreen()
                    .getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onStop() {
            super.onStop();
            getPreferenceScreen()
                    .getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
        }

        ActivityResultLauncher<Intent> activityResult =
                registerForActivityResult(
                        new ActivityResultContracts.StartActivityForResult(),
                        new ActivityResultCallback<ActivityResult>() {
                            @Override
                            public void onActivityResult(ActivityResult result) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    if (!Settings.canDrawOverlays(getActivity())) {
                                        getActivity()
                                                .startService(
                                                        new Intent(
                                                                getActivity(),
                                                                FloatingWindow.class));
                                    }
                                }
                            }
                        });

        @Override
        public void onDestroyView() {

            super.onDestroyView();
        }
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
}

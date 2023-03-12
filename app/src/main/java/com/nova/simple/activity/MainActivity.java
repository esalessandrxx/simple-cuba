package com.nova.simple.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telecom.PhoneAccountHandle;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.elevation.SurfaceColors;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.slider.Slider;
import com.google.zxing.integration.android.IntentIntegrator;
import com.itsaky.androidide.logsender.LogSender;
import com.nova.simple.BuildConfig;
import com.nova.simple.R;
import com.nova.simple.activity.AboutActivity;
import com.nova.simple.crashapp.ExceptionHandler;
import com.nova.simple.databinding.ActivityMainBinding;
import com.nova.simple.databinding.LayoutDrawerHeaderBinding;
import com.nova.simple.fragment.ComprasFragment;
import com.nova.simple.fragment.CuentasFragment;
import com.nova.simple.fragment.EnTuMovilFragment;
import com.nova.simple.fragment.HomeFragment;
import com.nova.simple.fragment.LlamadasFragment;
import com.nova.simple.fragment.ServiciosFragment;
import com.nova.simple.fragment.TerminosFragment;
import com.nova.simple.nauta.LoginFragment;
import com.nova.simple.profile.ImageSaver;
import com.nova.simple.fragment.PoliticasFragment;
import com.nova.simple.profile.PerfilActivity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, DrawerLayout.DrawerListener {

    private ActivityMainBinding binding;
    private LayoutDrawerHeaderBinding bindingHeader;

    // TODO: Biometric
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo info;
    private Executor executor;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        // TODO: Preferences DualSIM
        sp_sim = PreferenceManager.getDefaultSharedPreferences(this);
        sim = (sp_sim.getString(getString(R.string.sim_key), "0"));

        // TODO: NavigationView color surface
        getWindow().setNavigationBarColor(SurfaceColors.SURFACE_2.getColor(this));

        // TODO: Ocultar teclado al iniciar la app
        // getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt =
                new BiometricPrompt(
                        this,
                        executor,
                        new BiometricPrompt.AuthenticationCallback() {
                            @Override
                            public void onAuthenticationError(
                                    int errorCode, @NonNull CharSequence errString) {
                                super.onAuthenticationError(errorCode, errString);
                            }

                            @Override
                            public void onAuthenticationSucceeded(
                                    @NonNull BiometricPrompt.AuthenticationResult result) {
                                startActivity(new Intent(MainActivity.this, PerfilActivity.class));
                                finish();
                                super.onAuthenticationSucceeded(result);
                            }

                            @Override
                            public void onAuthenticationFailed() {
                                super.onAuthenticationFailed();
                            }
                        });

        info =
                new BiometricPrompt.PromptInfo.Builder()
                        .setTitle(getString(R.string.title_dialog_huella))
                        .setDescription(getString(R.string.subtitle_dialog_huella))
                        .setAllowedAuthenticators(
                                BiometricManager.Authenticators.BIOMETRIC_WEAK
                                        | BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                        .setConfirmationRequired(false)
                        .build();

        // TODO: DrawerLayout
        ActionBarDrawerToggle toggle =
                new ActionBarDrawerToggle(
                        this,
                        binding.drawerLayout,
                        binding.toolbar,
                        R.string.open_drawer,
                        R.string.close_drawer);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        binding.toolbar.setNavigationIcon(R.drawable.ic_menu_drawer);
        binding.navigationView.setNavigationItemSelectedListener(this);
        MenuItem menuItem = binding.navigationView.getMenu().getItem(0);
        onNavigationItemSelected(menuItem);
        menuItem.setChecked(true);
        binding.drawerLayout.addDrawerListener(this);

        // header
        View view = binding.navigationView.getHeaderView(0);
        bindingHeader = LayoutDrawerHeaderBinding.bind(view);
        // TODO: Mostrar saludo con el nombre del usuario
        SharedPreferences sp_perfil = getSharedPreferences("profile", Context.MODE_PRIVATE);
        String name = sp_perfil.getString("nombre", "").toString();
        if (name.isEmpty()) {
            bindingHeader.textUserPerfil.setText("Usuario");
        } else {
            bindingHeader.textUserPerfil.setText(name);
        }

        // saludo
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDateTime currentTime = LocalDateTime.now();
            if (currentTime.getHour() < 12) {
                bindingHeader.textSaludoPerfil.setText(getString(R.string.title_good_morning));
            } else if (currentTime.getHour() >= 12 && currentTime.getHour() < 18) {
                bindingHeader.textSaludoPerfil.setText(getString(R.string.title_good_afternoon));
            } else {
                bindingHeader.textSaludoPerfil.setText(getString(R.string.title_good_night));
            }
        } else {
            bindingHeader.textSaludoPerfil.setText("Hola");
        }
        bindingHeader = LayoutDrawerHeaderBinding.bind(view);
        bindingHeader.imgDrawerPerfil.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        SharedPreferences sp_huella =
                                PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                        if (sp_huella.getBoolean(getString(R.string.biometric_key), false)) {
                            // ejecuta
                            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                                biometricPrompt.authenticate(info);
                            } else {
                                startActivity(new Intent(MainActivity.this, PerfilActivity.class));
                                finish();
                            }
                            biometricPromptDialog();
                        } else {
                            // quita
                            startActivity(new Intent(MainActivity.this, PerfilActivity.class));
                            finish();
                        }
                    }
                });
        // TODO: Perfil en el header
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Bitmap load =
                    new ImageSaver(getApplicationContext())
                            .setFileName("IMG.png")
                            .setDirectoryName("Simple")
                            .load();
            if (load == null) {
                bindingHeader.imgDrawerPerfil.setImageResource(R.drawable.ic_perfil_user);
            } else {
                bindingHeader.imgDrawerPerfil.setImageBitmap(load);
            }
        } else {
            Bitmap load =
                    new ImageSaver(getApplicationContext())
                            .setExternal(true)
                            .setFileName("IMG.png")
                            .setDirectoryName("Simple")
                            .load();
            if (load == null) {
                bindingHeader.imgDrawerPerfil.setImageResource(R.drawable.ic_perfil_user);
            } else {
                bindingHeader.imgDrawerPerfil.setImageBitmap(load);
            }
        }
        // TODO: BottomNavigationView
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            binding.bottomNavigationView.getMenu().removeItem(R.id.cuenta);
        }
        binding.bottomNavigationView.setOnItemSelectedListener(
                new BottomNavigationView.OnItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.home:
                                binding.navigationView.getMenu().getItem(0).setChecked(true);
                                Fragment home = new HomeFragment();
                                addFragment(home);
                                break;
                            case R.id.cuenta:
                                Fragment cuentas = new CuentasFragment();
                                loadFragment(cuentas);
                                break;
                            case R.id.compras:
                                Fragment compras = new ComprasFragment();
                                loadFragment(compras);
                                break;
                            case R.id.llamadas:
                                Fragment llamadas = new LlamadasFragment();
                                loadFragment(llamadas);
                                break;
                            case R.id.nauta:
                                Fragment login = new LoginFragment();
                                loadFragment(login);
                                break;
                            default:
                                return false;
                        }
                        return true;
                    }
                });

        binding.bottomNavigationView.setOnItemReselectedListener(
                new BottomNavigationView.OnItemReselectedListener() {
                    @Override
                    public void onNavigationItemReselected(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.home:
                                break;
                            case R.id.cuenta:
                                break;
                            case R.id.compras:
                                break;
                            case R.id.llamadas:
                                break;
                            case R.id.nauta:
                                break;
                        }
                    }
                });

        // TODO: Shorcuts
        shorcut();
    }
    // TODO: DrawerLayout onNavigationItemSelected
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.home:
                binding.bottomNavigationView.setSelectedItemId(R.id.home);
                Fragment home = new HomeFragment();
                addFragment(home);
                break;
            case R.id.servicios:
                binding.bottomNavigationView.getMenu().getItem(0).setChecked(true);
                Fragment servicios = new ServiciosFragment();
                loadFragment(servicios);
                break;
            case R.id.entumovil:
                binding.bottomNavigationView.getMenu().getItem(0).setChecked(true);
                Fragment entumovil = new EnTuMovilFragment();
                loadFragment(entumovil);
                break;
            case R.id.telepuntos:
                openGoogleMap();
                break;
            case R.id.soporte:
                startActivity(new Intent(this, SoporteActivity.class));
                finish();
                break;
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                finish();
                break;
            case R.id.terminos:
                binding.bottomNavigationView.getMenu().getItem(0).setChecked(true);
                Fragment terminos = new TerminosFragment();
                loadFragment(terminos);
                break;
            case R.id.politicas:
                binding.bottomNavigationView.getMenu().getItem(0).setChecked(true);
                Fragment pol = new PoliticasFragment();
                loadFragment(pol);
                break;
            case R.id.invite:
                inviteUser();
                break;
            case R.id.about:
                startActivity(new Intent(this, AboutActivity.class));
                finish();
                break;
            default:
                throw new IllegalArgumentException("Error");
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .commit();
    }

    private void addFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .commit();
    }

    private void inviteUser() {
        new ShareCompat.IntentBuilder(this)
                .setText(getString(R.string.invite_user) + getPackageName())
                .setType("text/plain")
                .setChooserTitle("Compartir:")
                .startChooser();
    }

    private void openGoogleMap() {
        startActivity(
                new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.view_telepuntos))));
    }

    private void biometricPromptDialog() {
        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate(
                BiometricManager.Authenticators.BIOMETRIC_WEAK
                        | BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                Log.d("MY_APP_TAG", "App can authenticate using biometrics.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Log.e("MY_APP_TAG", "No biometric features available on this device.");
                // b.biometricCardView.setVisibility(View.GONE);
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Log.e("MY_APP_TAG", "Biometric features are currently unavailable.");
                // b.biometricCardView.setVisibility(View.GONE);
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                // Prompts the user to create credentials that your app accepts.
                final Intent enrollIntent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
                enrollIntent.putExtra(
                        Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BiometricManager.Authenticators.BIOMETRIC_WEAK
                                | BiometricManager.Authenticators.DEVICE_CREDENTIAL);
                startActivityForResult(enrollIntent, IntentIntegrator.REQUEST_CODE);
                break;
        }
    }

    private void shorcut() {
        if (android.os.Build.VERSION.SDK_INT >= 25) {
            ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:*222" + Uri.encode("#")));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("com.android.phone.force.slot", true);
            intent.putExtra("Cdma_Supp", true);
            if (sim.equals("0")) {
                for (String s : simSlotName) {
                    intent.putExtra(s, 0);
                    intent.putExtra("com.android.phone.extra.slot", 0);
                }
            } else if (sim.equals("1")) {
                for (String s : simSlotName) {
                    intent.putExtra(s, 1);
                    intent.putExtra("com.android.phone.extra.slot", 1);
                }
            }
            ShortcutInfo saldoShortcut =
                    new ShortcutInfo.Builder(this, "shortcut_saldo")
                            .setShortLabel("Saldo")
                            .setLongLabel("Saldo")
                            .setIcon(Icon.createWithResource(this, R.drawable.ic_shortcut_saldo))
                            .setIntent(intent)
                            .setRank(2)
                            .build();
            // shrtcuts bonos
            Intent bonos =
                    new Intent(Intent.ACTION_CALL, Uri.parse("tel:*222*266" + Uri.encode("#")));
            bonos.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            bonos.putExtra("com.android.phone.force.slot", true);
            bonos.putExtra("Cdma_Supp", true);
            if (sim.equals("0")) {
                for (String s : simSlotName) {
                    bonos.putExtra(s, 0);
                    bonos.putExtra("com.android.phone.extra.slot", 0);
                }
            } else if (sim.equals("1")) {
                for (String s : simSlotName) {
                    bonos.putExtra(s, 1);
                    bonos.putExtra("com.android.phone.extra.slot", 1);
                }
            }
            ShortcutInfo bonosShortcut =
                    new ShortcutInfo.Builder(this, "shortcut_bono")
                            .setShortLabel("Bonos")
                            .setLongLabel("Bonos")
                            .setIcon(Icon.createWithResource(this, R.drawable.ic_shortcut_bonos))
                            .setIntent(bonos)
                            .setRank(1)
                            .build();
            // shrtcuts datos
            Intent datos =
                    new Intent(Intent.ACTION_CALL, Uri.parse("tel:*222*328" + Uri.encode("#")));
            datos.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            datos.putExtra("com.android.phone.force.slot", true);
            datos.putExtra("Cdma_Supp", true);
            if (sim.equals("0")) {
                for (String s : simSlotName) {
                    datos.putExtra(s, 0);
                    datos.putExtra("com.android.phone.extra.slot", 0);
                }
            } else if (sim.equals("1")) {
                for (String s : simSlotName) {
                    datos.putExtra(s, 1);
                    datos.putExtra("com.android.phone.extra.slot", 1);
                }
            }
            ShortcutInfo datosShortcut =
                    new ShortcutInfo.Builder(this, "shortcut_datos")
                            .setShortLabel("Datos")
                            .setLongLabel("Datos")
                            .setIcon(Icon.createWithResource(this, R.drawable.ic_shortcut_datos))
                            .setIntent(datos)
                            .setRank(0)
                            .build();
            shortcutManager.setDynamicShortcuts(
                    Arrays.asList(saldoShortcut, bonosShortcut, datosShortcut));
        }
    }

    @Override
    public void onDrawerSlide(@NonNull View view, float v) {
        // cambio en la posición del drawer
    }

    @Override
    public void onDrawerOpened(@NonNull View view) {
        // el drawer se ha abierto completamente
    }

    @Override
    public void onDrawerClosed(@NonNull View view) {
        // el drawer se ha cerrado completamente
    }

    @Override
    public void onDrawerStateChanged(int i) {
        // cambio de estado, puede ser STATE_IDLE, STATE_DRAGGING or STATE_SETTLING
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        binding.bottomNavigationView.setSelectedItemId(R.id.home);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!getPreferencia(MainActivity.this).equals("Inciado")) {
            Intent acceso = new Intent(this, BienvenidaActivity.class);
            startActivity(acceso);
            finish();
        }
    }

    public String getPreferencia(Activity contex) {
        SharedPreferences prefs = contex.getSharedPreferences("Permisos", Context.MODE_PRIVATE);
        return prefs.getString("inicio", "");
    }

    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        binding = null;
        super.onDestroy();
    }
}

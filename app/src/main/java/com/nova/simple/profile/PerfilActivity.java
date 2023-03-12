package com.nova.simple.profile;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.DrawableUtils;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.elevation.SurfaceColors;
import android.R;
import com.google.android.material.snackbar.Snackbar;
import com.nova.simple.R.drawable;
import com.nova.simple.R.menu;
import com.nova.simple.R.id;
import com.nova.simple.R.style;
import androidx.appcompat.app.AppCompatActivity;
import com.nova.simple.activity.MainActivity;
import com.nova.simple.databinding.ActivityPerfilBinding;
import com.nova.simple.fragment.BottomSheetQR;
import com.nova.simple.profile.ImageSaver;
import com.nova.simple.profile.PerfilActivity;
import com.nova.simple.profile.ScannerPerfil;
import java.io.IOException;

public class PerfilActivity extends AppCompatActivity {

    private ActivityPerfilBinding binding;
    SharedPreferences sp_perfil;
    SharedPreferences.Editor editor;
    Menu optionMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPerfilBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        binding.appbar.addOnOffsetChangedListener(
                new AppBarLayout.OnOffsetChangedListener() {
                    boolean isShow;
                    int scrollRange = -1;

                    @Override
                    public void onOffsetChanged(AppBarLayout appbar, int verticalOffset) {
                        if (scrollRange == -1) {
                            scrollRange = binding.appbar.getTotalScrollRange();
                        }
                        if (scrollRange + verticalOffset == 0) {
                            isShow = true;
                            TypedValue value = new TypedValue();
                            getTheme().resolveAttribute(R.attr.colorControlNormal, value, true);
                            int color =
                                    ContextCompat.getColor(
                                            getApplicationContext(), value.resourceId);
                            binding.toolbar.setNavigationIconTint(color);

                            // TODO: collapsed color icon delete
                            MenuItem delete = optionMenu.findItem(id.delete);
                            Drawable deleteIcon = (Drawable) delete.getIcon();
                            deleteIcon.mutate().setColorFilter(color, PorterDuff.Mode.SRC_IN);
                            delete.setIcon(deleteIcon);

                            // TODO: collapsed color icon code qr
                            MenuItem codeQR = optionMenu.findItem(id.codeqr);
                            Drawable qrIcon = (Drawable) codeQR.getIcon();
                            qrIcon.mutate().setColorFilter(color, PorterDuff.Mode.SRC_IN);
                            codeQR.setIcon(qrIcon);
                        } else if (isShow) {
                            isShow = false;
                            binding.toolbar.setNavigationIconTint(Color.WHITE);

                            // TODO: collapsed color icon delete
                            MenuItem delete = optionMenu.findItem(id.delete);
                            Drawable deleteIcon = (Drawable) delete.getIcon();
                            deleteIcon.mutate().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
                            delete.setIcon(deleteIcon);

                            // TODO: collapsed color icon code qr
                            MenuItem codeQR = optionMenu.findItem(id.codeqr);
                            Drawable qrIcon = (Drawable) codeQR.getIcon();
                            qrIcon.mutate().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
                            codeQR.setIcon(qrIcon);
                        }
                    }
                });

        // TODO: SharedPreferences info perfil
        sp_perfil = getSharedPreferences("profile", Context.MODE_PRIVATE);
        editor = sp_perfil.edit();

        // TODO: navigation bar color
        getWindow().setNavigationBarColor(SurfaceColors.SURFACE_0.getColor(this));

        // TODO: select picture of profile
        binding.fab.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        select_profile();
                    }
                });

        // TODO: load pic
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // solicitar si no
                ActivityCompat.requestPermissions(
                        this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 20);
            } else {
                Bitmap load =
                        new ImageSaver(getApplicationContext())
                                .setExternal(true)
                                .setFileName("IMG.png")
                                .setDirectoryName("Simple")
                                .load();
                if (load == null) {
                    binding.imagePerfil.setImageResource(drawable.ic_perfil_user);
                } else {
                    binding.imagePerfil.setImageBitmap(load);
                }
            }
        } else {
            // SDK > 29 ejecutar
            Bitmap load =
                    new ImageSaver(getApplicationContext())
                            .setFileName("IMG.png")
                            .setDirectoryName("Simple")
                            .load();
            if (load == null) {
                binding.imagePerfil.setImageResource(drawable.ic_perfil_user);
            } else {
                binding.imagePerfil.setImageBitmap(load);
            }
        }

        // TODO:  Save info
        binding.editPerfilNombre.setText(sp_perfil.getString("nombre", "").toString());
        binding.editPerfilNumero.setText(sp_perfil.getString("numero", "").toString());
        binding.editPerfilClave.setText(sp_perfil.getString("clave", "").toString());
        binding.editPerfilNauta.setText(sp_perfil.getString("nauta", "").toString());
        binding.collapsingTbl.setTitle(sp_perfil.getString("nombre", "Usuario").toString());
        // buttom
        binding.buttomSavePerfil.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        Snackbar.make(arg0, "Guardado!", Snackbar.LENGTH_LONG).show();

                        editor.putString(
                                "nombre", binding.collapsingTbl.getTitle().toString().trim());
                        editor.putString(
                                "nombre", binding.editPerfilNombre.getText().toString().trim());
                        editor.putString(
                                "numero", binding.editPerfilNumero.getText().toString().trim());
                        editor.putString(
                                "clave", binding.editPerfilClave.getText().toString().trim());
                        editor.putString(
                                "nauta", binding.editPerfilNauta.getText().toString().trim());
                        editor.commit();
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu men) {
        getMenuInflater().inflate(menu.menu_perfil, men);
        optionMenu = men;
        return super.onCreateOptionsMenu(men);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                onBackPressed();
                break;
            case id.codeqr:
                startActivity(new Intent(this, ScannerPerfil.class));
                finish();
                break;
            case id.delete:
                deletePerfil();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void select_profile() {
        if (Build.VERSION.SDK_INT < 32) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // solicitar permisosn
                if (ContextCompat.checkSelfPermission(
                                this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 22);
                } else {
                    // esta dado, continuar
                    getPicture.launch("image/*");
                }

            } else {
                // < VERAION_CODES.M no solicitar permiso continuar
                getPicture.launch("image/*");
            }
        } else {
            // SDK 32, no solicitar permisos y continuar
            getPicture.launch("image/*");
        }
    }

    private void deletePerfil() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            new ImageSaver(getApplicationContext())
                    .setFileName("IMG.png")
                    .setDirectoryName("Simple")
                    .deleteFile();
            binding.imagePerfil.setImageResource(drawable.ic_perfil_user);
        } else {
            new ImageSaver(getApplicationContext())
                    .setExternal(true)
                    .setFileName("IMG.png")
                    .setDirectoryName("Simple")
                    .deleteFile();
            binding.imagePerfil.setImageResource(drawable.ic_perfil_user);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        binding = null;
        super.onDestroy();
    }

    private void loadCodeQR() {
        BottomSheetQR qr = new BottomSheetQR();
        qr.show(getSupportFragmentManager(), qr.getTag());
    }

    public void saveImage(String fileName, String directory, Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            new ImageSaver(getApplicationContext())
                    .setFileName(fileName)
                    .setDirectoryName(directory)
                    .save(bitmap);
        } else {
            new ImageSaver(getApplicationContext())
                    .setFileName(fileName)
                    .setDirectoryName(directory)
                    .setExternal(true)
                    .save(bitmap);
        }
    }

    ActivityResultLauncher<String> getPicture =
            registerForActivityResult(
                    new ActivityResultContracts.GetContent(),
                    new ActivityResultCallback<Uri>() {
                        @Override
                        public void onActivityResult(Uri uri) {
                            if (uri != null) {
                                try {
                                    if (Build.VERSION.SDK_INT < 29) {
                                        Bitmap bitmap =
                                                MediaStore.Images.Media.getBitmap(
                                                        PerfilActivity.this.getContentResolver(),
                                                        uri);
                                        saveImage("IMG.png", "Simple", bitmap);
                                        binding.imagePerfil.setImageBitmap(bitmap);
                                    } else {
                                        ImageDecoder.Source source =
                                                ImageDecoder.createSource(
                                                        PerfilActivity.this.getContentResolver(),
                                                        uri);
                                        Bitmap bitmap = ImageDecoder.decodeBitmap(source);
                                        saveImage("IMG.png", "Simple", bitmap);
                                        binding.imagePerfil.setImageBitmap(bitmap);
                                    }

                                } catch (IOException e) {

                                }
                            }
                        }
                    });
}

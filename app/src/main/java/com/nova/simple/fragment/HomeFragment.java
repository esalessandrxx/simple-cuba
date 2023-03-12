package com.nova.simple.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.telecom.PhoneAccountHandle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.nova.simple.R;
import com.nova.simple.databinding.ContentFragmentLayoutHomeBinding;
import com.nova.simple.databinding.FragmentHomeBinding;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private ContentFragmentLayoutHomeBinding homeBinding;

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
    public View onCreateView(LayoutInflater inflate, ViewGroup parent, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflate, parent, false);
        homeBinding = binding.home;
        ((TabLayout) getActivity().findViewById(R.id.tab_layout)).setVisibility(View.GONE);
        ((CollapsingToolbarLayout) getActivity().findViewById(R.id.collapsingTbl))
                .setTitle(getString(R.string.app_name));
        // TODO: Comprobación de TextInputEditText
        homeBinding.editRecarga.addTextChangedListener(
                new ValidationTextWatcher(homeBinding.editRecarga));
        homeBinding.editTransferirNumero.addTextChangedListener(
                new ValidationTextWatcher(homeBinding.editTransferirNumero));
        homeBinding.editTransferirMonto.addTextChangedListener(
                new ValidationTextWatcher(homeBinding.editTransferirMonto));
        homeBinding.editTransferirPassword.addTextChangedListener(
                new ValidationTextWatcher(homeBinding.editTransferirPassword));
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // TODO: Preferences DualSIM
        sp_sim = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sim = (sp_sim.getString(getString(R.string.sim_key), "0"));

        // TODO: Scanner QR endIcon
        homeBinding.inputRecarga.setEndIconOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ScanOptions scanner = new ScanOptions();
                        scanner.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
                        scanner.setPrompt("Escannee el código QR");
                        scanner.setOrientationLocked(true);
                        barcodeLauncher.launch(scanner);
                    }
                });

        // TODO: Consultar Saldo
        binding.cardConsultaSaldo.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        USSD("*222" + Uri.encode("#"));
                    }
                });

        // TODO: Consultar saldo pospago
        binding.cardConsultaPospago.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        pospagoUSSD("*111" + Uri.encode("#"));
                    }
                });

        // TODO: Consultar bono
        binding.cardConsultaBonos.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        USSD("*222*266" + Uri.encode("#"));
                    }
                });

        // TODO: Consultar datos
        binding.cardConsultaDatos.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        USSD("*222*328" + Uri.encode("#"));
                    }
                });

        // TODO: Recarga saldo

        homeBinding.inputRecarga.setEndIconOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ScanOptions scanner = new ScanOptions();
                        scanner.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
                        scanner.setPrompt("Escannee el código QR");
                        scanner.setOrientationLocked(true);
                        barcodeLauncher.launch(scanner);
                    }
                });
        homeBinding.buttonRecarga.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        if (!validateRecarga()) {
                            return;
                        } else {
                            String code = homeBinding.buttonRecarga.getText().toString().trim();
                            USSD("*662*" + code + Uri.encode("#"));
                        }
                        homeBinding.editRecarga.getText().clear();
                    }
                });

        // TODO: Transferir saldo
/*
        SharedPreferences sp_autocomplete =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences sp_perfil =
                getActivity().getSharedPreferences("profile", Context.MODE_PRIVATE);
        String pas = sp_perfil.getString("clave", null).toString();
        Toast.makeText(getActivity(), pas, Toast.LENGTH_LONG).show();

        if (sp_autocomplete.getBoolean(getString(R.string.autocomplete_key), false)) {
            String pass = sp_perfil.getString("clave", null).toString();
            if (pass == "") {
                homeBinding.editTransferirPassword.setFocusable(false);
            } else {
                homeBinding.editTransferirPassword.setText(pass);
            }
        } else {

        }*/
        homeBinding.buttonTransferir.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        if (!validateNumeroTransferir()) {
                            return;
                        } else if (!validateMontoTransferir()) {
                            return;
                        } else if (!validateClaveTransferir()) {
                            return;
                        } else {
                            String numero =
                                    homeBinding.editTransferirNumero.getText().toString().trim();
                            String cantidad =
                                    homeBinding.editTransferirMonto.getText().toString().trim();
                            String clave =
                                    homeBinding.editTransferirPassword.getText().toString().trim();
                            USSD(
                                    "*234*1*"
                                            + numero
                                            + "*"
                                            + clave
                                            + "*"
                                            + cantidad
                                            + Uri.encode("#"));
                        }
                    }
                });
        homeBinding.inputTransferirNumero.setEndIconOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (ContextCompat.checkSelfPermission(
                                        getActivity(), Manifest.permission.READ_CONTACTS)
                                != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(
                                    getActivity(),
                                    new String[] {Manifest.permission.READ_CONTACTS},
                                    2);
                        } else {
                            pickContact.launch(null);
                        }
                    }
                });

        // TODO: Adelanta Saldo
        String[] adelanta = getResources().getStringArray(R.array.array_adelanta);
        ArrayAdapter adapter =
                new ArrayAdapter(
                        getContext(), android.R.layout.simple_dropdown_item_1line, adelanta);
        homeBinding.autocompleteAdelanta.setAdapter(adapter);
        homeBinding.autocompleteAdelanta.addTextChangedListener(
                new ValidationTextWatcher(homeBinding.autocompleteAdelanta));
        homeBinding.buttonAdelanta.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        if (!validateAdelanta()) {
                            return;
                        } else {
                            String monto =
                                    homeBinding
                                            .autocompleteAdelanta
                                            .getText()
                                            .toString()
                                            .replace("25.00 cup", "25")
                                            .replace("50.00 cup", "50")
                                            .trim();
                            USSD("*234*3*1*" + monto + "*1" + Uri.encode("#"));
                        }
                        homeBinding.autocompleteAdelanta.getText().clear();
                    }
                });
    }

    private void USSD(String code) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        getActivity(), new String[] {Manifest.permission.CALL_PHONE}, 0);
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

    private void pospagoUSSD(String code) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        getActivity(), new String[] {Manifest.permission.CALL_PHONE}, 0);
            } else {
                Intent saldo =
                        new Intent(Intent.ACTION_CALL).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                saldo.setData(Uri.parse("tel:" + code));
                saldo.putExtra("com.android.phone.force.slot", true);
                saldo.putExtra("Cdma_Supp", true);
                if (sim.equals("0")) {
                    for (String s : simSlotName) {
                        saldo.putExtra(s, 1);
                    }
                    if (phoneAccountHandleList != null && phoneAccountHandleList.size() > 1) {
                        saldo.putExtra(
                                "android.telecom.extra.PHONE_ACCOUNT_HANDLE",
                                phoneAccountHandleList.get(1));
                    }

                } else if (sim.equals("1")) {
                    for (String s : simSlotName) {
                        saldo.putExtra(s, 0);
                    }
                    if (phoneAccountHandleList != null && phoneAccountHandleList.size() > 0) {
                        saldo.putExtra(
                                "android.telecom.extra.PHONE_ACCOUNT_HANDLE",
                                phoneAccountHandleList.get(0));
                    }
                }
                startActivity(saldo);
            }
        }
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity()
                    .getWindow()
                    .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private boolean validateRecarga() {
        if (homeBinding.editRecarga.getText().toString().isEmpty()) {
            homeBinding.inputRecarga.setError("Inserte su código");
            requestFocus(homeBinding.editRecarga);
            return false;
        } else if (homeBinding.editRecarga.getText().toString().length() < 16) {
            homeBinding.inputRecarga.setError("El código debe de ser de 16 dígitos");
            requestFocus(homeBinding.editRecarga);
            return false;
        } else if (homeBinding.editRecarga.getText().toString().length() == 16) {
            homeBinding.inputRecarga.setError(null);
            homeBinding.inputRecarga.setErrorEnabled(true);
        }
        return true;
    }

    private boolean validateNumeroTransferir() {
        if (homeBinding.editTransferirNumero.getText().toString().isEmpty()) {
            homeBinding.inputTransferirNumero.setError(
                    getString(R.string.error_input_transfer_numero_vacio));
            requestFocus(homeBinding.editTransferirNumero);
            return false;
        } else if (homeBinding.editTransferirNumero.getText().toString().length() < 8) {
            homeBinding.inputTransferirNumero.setError(
                    getString(R.string.error_input_transfer_numero_digitos));
            requestFocus(homeBinding.editTransferirNumero);
            return false;
        } else if (homeBinding.editTransferirNumero.getText().toString().length() == 8) {
            homeBinding.inputTransferirNumero.setError(null);
            homeBinding.inputTransferirNumero.setErrorEnabled(true);
        }
        return true;
    }

    private boolean validateMontoTransferir() {
        if (homeBinding.editTransferirMonto.getText().toString().trim().contains(".")) {
            homeBinding.editTransferirMonto.getText().clear();
            Toast.makeText(
                            getActivity(),
                            getString(R.string.error_transferir_centavos),
                            Toast.LENGTH_LONG)
                    .show();
            return false;
        }

        return true;
    }

    private boolean validateClaveTransferir() {
        if (homeBinding.editTransferirPassword.getText().toString().isEmpty()) {
            homeBinding.inputTransferirPassword.setError(
                    getString(R.string.error_input_transfer_numero_vacio));
            requestFocus(homeBinding.editTransferirPassword);
            return false;
        } else if (homeBinding.editTransferirPassword.getText().toString().length() < 4) {
            homeBinding.inputTransferirPassword.setError(
                    getString(R.string.error_input_transfer_clave_digitos));
            requestFocus(homeBinding.editTransferirPassword);
            return false;
        } else if (homeBinding.editTransferirPassword.getText().toString().length() == 4) {
            homeBinding.inputTransferirPassword.setError(null);
            homeBinding.inputTransferirPassword.setErrorEnabled(true);
        }

        return true;
    }

    private boolean validateAdelanta() {
        if (homeBinding.autocompleteAdelanta.getText().toString().isEmpty()) {
            homeBinding.inputAdelanta.setError(
                    getString(R.string.error_input_transfer_numero_vacio));
            requestFocus(homeBinding.autocompleteAdelanta);
            return false;
        }
        return true;
    }

    private class ValidationTextWatcher implements TextWatcher {

        private View view;

        private ValidationTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}

        @Override
        public void afterTextChanged(Editable arg0) {
            switch (view.getId()) {
                case R.id.edit_recarga:
                    validateRecarga();
                    break;
                case R.id.edit_transferir_numero:
                    validateNumeroTransferir();
                    break;
                case R.id.edit_transferir_monto:
                    validateMontoTransferir();
                    break;
                case R.id.edit_transferir_password:
                    validateClaveTransferir();
                    break;
                case R.id.autocompleteAdelanta:
                    validateAdelanta();
                    break;
            }
        }
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }

    // TODO: Scanner QR
    private final ActivityResultLauncher<ScanOptions> barcodeLauncher =
            registerForActivityResult(
                    new ScanContract(),
                    result -> {
                        if (result.getContents() == null) {
                            Toast.makeText(getActivity(), "Cancelado!", Toast.LENGTH_LONG).show();
                        } else {
                            String code = result.getContents().toString();
                            homeBinding.editRecarga.setText(code);
                        }
                    });

    private final ActivityResultLauncher<Void> pickContact =
            registerForActivityResult(
                    new ActivityResultContracts.PickContact(),
                    new ActivityResultCallback<Uri>() {
                        @Override
                        public void onActivityResult(Uri uri) {
                            if (uri != null) {
                                contact(uri);
                            }
                        }
                    });

    private void contact(Uri uri) {
        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name =
                        cursor.getString(
                                cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (Integer.parseInt(
                                cursor.getString(
                                        cursor.getColumnIndex(
                                                ContactsContract.Contacts.HAS_PHONE_NUMBER)))
                        > 0) {
                    homeBinding.inputTransferirNumero.setHelperText(name);

                    // phone number
                    Cursor phoneCursor =
                            getActivity()
                                    .getContentResolver()
                                    .query(
                                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                            null,
                                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                                    + " = ?",
                                            new String[] {id},
                                            null);
                    while (phoneCursor.moveToNext()) {
                        String number =
                                phoneCursor.getString(
                                        phoneCursor.getColumnIndex(
                                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        String replace =
                                number.replace("-", "")
                                        .replace(" ", "")
                                        .replace("(", "")
                                        .replace(")", "");
                        replace = replace.substring(replace.length() - 8);
                        if (replace.startsWith("5") && replace.length() == 8) {
                            homeBinding.editTransferirNumero.setText(replace);
                        }
                        Toast.makeText(
                                        getActivity(),
                                        name + " no es un número de Cuba",
                                        Toast.LENGTH_LONG)
                                .show();
                        homeBinding.inputTransferirNumero.setHelperText(null);
                    }
                    phoneCursor.close();
                }
            }
        }
    }
}

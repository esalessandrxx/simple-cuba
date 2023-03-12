package com.nova.simple.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telecom.PhoneAccountHandle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;
import com.nova.simple.R;
import com.nova.simple.adapter.ItemTouchListener;
import com.nova.simple.adapter.ItemsAdapter;
import com.nova.simple.databinding.FragmentPaquetesBinding;
import com.nova.simple.model.GridItem;
import com.nova.simple.model.HeaderItem;
import com.nova.simple.model.Items;
import java.util.ArrayList;
import java.util.List;

public class LlamadasFragment extends Fragment {

    private FragmentPaquetesBinding binding;
    private ItemsAdapter adapter;
    private ArrayList<Items> item = new ArrayList<>();

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
        binding = FragmentPaquetesBinding.inflate(inflate, parent, false);
        ((TabLayout) getActivity().findViewById(R.id.tab_layout)).setVisibility(View.GONE);
        ((CollapsingToolbarLayout) getActivity().findViewById(R.id.collapsingTbl))
                .setTitle(getString(R.string.title_llamada));
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // TODO: Preferences DualSIM
        sp_sim = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sim = (sp_sim.getString(getString(R.string.sim_key), "0"));

        // TODO: Recyclerview
        binding.recyclerView.setHasFixedSize(true);
        adapter = new ItemsAdapter(getContext(), item);
        binding.recyclerView.setAdapter(adapter);
        GridLayoutManager manager = new GridLayoutManager(getContext(), 2);
        manager.setSpanSizeLookup(
                new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return adapter.getItemViewType(position) == HeaderItem.VIEW_HEADER ? 2 : 1;
                    }
                });
        binding.recyclerView.setLayoutManager(manager);
        loadItems();

        // TODO: items recyclerview touch
        binding.recyclerView.addOnItemTouchListener(
                new ItemTouchListener(
                        getActivity(),
                        binding.recyclerView,
                        new ItemTouchListener.ClickListener() {
                            @Override
                            public void onClick(View view, int position) {
                                switch (position) {
                                    case 0:
                                        asterisco.launch(null);
                                        break;
                                    case 1:
                                        privado.launch(null);
                                        break;
                                    case 2:
                                        USSD("52642266");
                                        break;
                                    case 3:
                                        USSD("80043434");
                                        break;
                                    case 4:
                                        USSD("114");
                                        break;
                                    case 5:
                                        USSD("118");
                                        break;
                                    case 6:
                                        // header
                                        break;
                                    case 7:
                                        USSD("103");
                                        break;
                                    case 8:
                                        USSD("104");
                                        break;
                                    case 9:
                                        USSD("105");
                                        break;
                                    case 10:
                                        USSD("106");
                                        break;
                                    case 11:
                                        USSD("107");
                                        break;
                                }
                            }
                        }));
    }

    private void loadItems() {
        item.add(
                new GridItem(
                        getString(R.string.title_llamadas_asterisco),
                        getString(R.string.subtitle_llamadas_asterisco),
                        R.drawable.ic_llamadas_asterisco));
        item.add(
                new GridItem(
                        getString(R.string.title_llamadas_privado),
                        getString(R.string.subtitle_llamadas_privado),
                        R.drawable.ic_llamadas_privado));
        item.add(
                new GridItem(
                        getString(R.string.title_llamadas_atencion_cliente),
                        null,
                        R.drawable.ic_llamadas_operadora));
        item.add(
                new GridItem(
                        getString(R.string.title_llamadas_nauta_hogar),
                        null,
                        R.drawable.ic_llamadas_nautahogar));
        item.add(
                new GridItem(
                        getString(R.string.title_llamadas_reporte_tfa),
                        null,
                        R.drawable.ic_llamadas_reporte_tfa));
        item.add(
                new GridItem(
                        getString(R.string.title_llamadas_quejas),
                        null,
                        R.drawable.ic_llamadas_quejas));
        // TODO: Emergencias
        item.add(new HeaderItem(getString(R.string.title_categoria_llamadas)));
        item.add(
                new GridItem(
                        getString(R.string.title_llamadas_antidroga),
                        null,
                        R.drawable.ic_llamadas_antidroga));
        item.add(
                new GridItem(
                        getString(R.string.title_llamadas_ambulancia),
                        null,
                        R.drawable.ic_llamadas_ambulance));
        item.add(
                new GridItem(
                        getString(R.string.title_llamadas_bomberos),
                        null,
                        R.drawable.ic_llamadas_bomberos));
        item.add(
                new GridItem(
                        getString(R.string.title_llamadas_policia),
                        null,
                        R.drawable.ic_llamadas_policia));
        item.add(
                new GridItem(
                        getString(R.string.title_llamadas_maritima),
                        null,
                        R.drawable.ic_llamadas_em_marit));
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

    private final ActivityResultLauncher<Void> asterisco =
            registerForActivityResult(
                    new ActivityResultContracts.PickContact(),
                    new ActivityResultCallback<Uri>() {
                        @Override
                        public void onActivityResult(Uri uri) {
                            if (uri != null) {
                                Cursor cursor =
                                        getActivity()
                                                .getContentResolver()
                                                .query(uri, null, null, null, null);
                                if (cursor.getCount() > 0) {
                                    while (cursor.moveToNext()) {
                                        String id =
                                                cursor.getString(
                                                        cursor.getColumnIndex(
                                                                ContactsContract.Contacts._ID));
                                        String name =
                                                cursor.getString(
                                                        cursor.getColumnIndex(
                                                                ContactsContract.Contacts
                                                                        .DISPLAY_NAME));
                                        if (Integer.parseInt(
                                                        cursor.getString(
                                                                cursor.getColumnIndex(
                                                                        ContactsContract.Contacts
                                                                                .HAS_PHONE_NUMBER)))
                                                > 0) {
                                            // llamando a: nombre
                                            // phone number
                                            Cursor phoneCursor =
                                                    getActivity()
                                                            .getContentResolver()
                                                            .query(
                                                                    ContactsContract.CommonDataKinds
                                                                            .Phone.CONTENT_URI,
                                                                    null,
                                                                    ContactsContract.CommonDataKinds
                                                                                    .Phone
                                                                                    .CONTACT_ID
                                                                            + " = ?",
                                                                    new String[] {id},
                                                                    null);
                                            while (phoneCursor.moveToNext()) {
                                                String number =
                                                        phoneCursor.getString(
                                                                phoneCursor.getColumnIndex(
                                                                        ContactsContract
                                                                                .CommonDataKinds
                                                                                .Phone.NUMBER));
                                                String replace =
                                                        number.replace("-", "")
                                                                .replace(" ", "")
                                                                .replace("(", "")
                                                                .replace(")", "");
                                                replace = replace.substring(replace.length() - 8);
                                                if (replace.startsWith("5")
                                                        && replace.length() == 8) {
                                                    // llamar
                                                    USSD("*99" + replace);
                                                }
                                            }
                                            phoneCursor.close();
                                        }
                                    }
                                }
                            }
                        }
                    });

    // TODO: Llamar con número privado
    private final ActivityResultLauncher<Void> privado =
            registerForActivityResult(
                    new ActivityResultContracts.PickContact(),
                    new ActivityResultCallback<Uri>() {
                        @Override
                        public void onActivityResult(Uri uri) {
                            if (uri != null) {
                                Cursor cursor =
                                        getActivity()
                                                .getContentResolver()
                                                .query(uri, null, null, null, null);
                                if (cursor.getCount() > 0) {
                                    while (cursor.moveToNext()) {
                                        String id =
                                                cursor.getString(
                                                        cursor.getColumnIndex(
                                                                ContactsContract.Contacts._ID));
                                        String name =
                                                cursor.getString(
                                                        cursor.getColumnIndex(
                                                                ContactsContract.Contacts
                                                                        .DISPLAY_NAME));
                                        if (Integer.parseInt(
                                                        cursor.getString(
                                                                cursor.getColumnIndex(
                                                                        ContactsContract.Contacts
                                                                                .HAS_PHONE_NUMBER)))
                                                > 0) {
                                            // llamando a: nombre
                                            // phone number
                                            Cursor phoneCursor =
                                                    getActivity()
                                                            .getContentResolver()
                                                            .query(
                                                                    ContactsContract.CommonDataKinds
                                                                            .Phone.CONTENT_URI,
                                                                    null,
                                                                    ContactsContract.CommonDataKinds
                                                                                    .Phone
                                                                                    .CONTACT_ID
                                                                            + " = ?",
                                                                    new String[] {id},
                                                                    null);
                                            while (phoneCursor.moveToNext()) {
                                                String number =
                                                        phoneCursor.getString(
                                                                phoneCursor.getColumnIndex(
                                                                        ContactsContract
                                                                                .CommonDataKinds
                                                                                .Phone.NUMBER));
                                                String replace =
                                                        number.replace("-", "")
                                                                .replace(" ", "")
                                                                .replace("(", "")
                                                                .replace(")", "");
                                                replace = replace.substring(replace.length() - 8);
                                                if (replace.startsWith("5")
                                                        && replace.length() == 8) {
                                                    // llamar
                                                    USSD(
                                                            Uri.encode("#")
                                                                    + "31"
                                                                    + Uri.encode("#")
                                                                    + replace);
                                                }
                                            }
                                            phoneCursor.close();
                                        }
                                    }
                                }
                            }
                        }
                    });
}

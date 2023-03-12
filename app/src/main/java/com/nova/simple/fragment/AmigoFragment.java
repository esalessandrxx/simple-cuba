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
import android.widget.Toast;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.nova.simple.R;
import com.nova.simple.adapter.ItemTouchListener;
import com.nova.simple.adapter.ItemsAdapter;
import com.nova.simple.databinding.BottomSheetComprasBinding;
import com.nova.simple.databinding.FragmentPaquetesBinding;
import com.nova.simple.model.GridItem;
import com.nova.simple.model.HeaderItem;
import com.nova.simple.model.Items;
import java.util.ArrayList;
import java.util.List;

public class AmigoFragment extends Fragment {

    private FragmentPaquetesBinding binding;
    private BottomSheetComprasBinding bottomSheetBinding;
    private TabLayout tab;
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
        tab = (TabLayout) getActivity().findViewById(R.id.tab_layout);
        tab.setVisibility(View.VISIBLE);
        ((CollapsingToolbarLayout) getActivity().findViewById(R.id.collapsingTbl))
                .setTitle(getString(R.string.title_compra));
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View arg0, Bundle arg1) {
        super.onViewCreated(arg0, arg1);

        // TODO: Preferences DualSIM
        sp_sim = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sim = (sp_sim.getString(getString(R.string.sim_key), "0"));

        // TODO: RecyclerView
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
                                        USSD("*222*264" + Uri.encode("#"));
                                        break;
                                    case 1:
                                        USSD("*133*4*3" + Uri.encode("#"));
                                        break;
                                    case 2:
                                        activar_plan();
                                        break;
                                    case 3:
                                        desactivar_plan();
                                        break;
                                    case 4:
                                        agregar_contacto();
                                        break;
                                    case 5:
                                        eliminar_contacto();
                                        break;
                                }
                                return;
                            }
                        }));
    }

    private void loadItems() {
        item.add(
                new GridItem(
                        getString(R.string.title_estado_amigo),
                        getString(R.string.subtitle_estado_amigo),
                        R.drawable.ic_amigo_estado));
        item.add(
                new GridItem(
                        getString(R.string.title_lista_amigo),
                        getString(R.string.subtitle_lista_amigo),
                        R.drawable.ic_amigo_lista));
        item.add(
                new GridItem(
                        getString(R.string.title_activar_amigo),
                        getString(R.string.subtitle_activar_amigo),
                        R.drawable.ic_amigo_activar));
        item.add(
                new GridItem(
                        getString(R.string.title_desactivar_amigo),
                        getString(R.string.subtitle_desactivar_amigo),
                        R.drawable.ic_amigo_desactivar));
        item.add(
                new GridItem(
                        getString(R.string.title_agregar_amigo),
                        getString(R.string.subtitle_agregar_amigo),
                        R.drawable.ic_drawer_invite));
        item.add(
                new GridItem(
                        getString(R.string.title_eliminar_amigo),
                        getString(R.string.subtitle_eliminar_amigo),
                        R.drawable.ic_amigo_eliminar));
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
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

    private void activar_plan() {
        BottomSheetDialog dialog = new BottomSheetDialog(getContext());
        bottomSheetBinding = BottomSheetComprasBinding.inflate(LayoutInflater.from(getContext()));
        dialog.setContentView(bottomSheetBinding.getRoot());
        // info paquetes
        bottomSheetBinding.textTitle.setText("Activar");
        bottomSheetBinding.textPlanPrecio.setText(getString(R.string.activar_amigo_precio));
        bottomSheetBinding.textMessage.setText(getString(R.string.activar_amigo));
        bottomSheetBinding.buttonCompra.setText("Activar");
        bottomSheetBinding.buttonCompra.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        USSD("*133*4*1*1*1" + Uri.encode("#"));
                        dialog.dismiss();
                    }
                });
        dialog.show();
    }

    private void desactivar_plan() {
        BottomSheetDialog dialog = new BottomSheetDialog(getContext());
        bottomSheetBinding = BottomSheetComprasBinding.inflate(LayoutInflater.from(getContext()));
        dialog.setContentView(bottomSheetBinding.getRoot());
        // info paquetes
        bottomSheetBinding.textTitle.setText("Desactivar");
        bottomSheetBinding.textPlanPrecio.setText(getString(R.string.desactivar_amigo_precio));
        bottomSheetBinding.textMessage.setText(getString(R.string.desactivar_amigo));
        bottomSheetBinding.buttonCompra.setText("Desactivar");
        bottomSheetBinding.buttonCompra.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        USSD("*133*4*1*2*1" + Uri.encode("#"));
                        dialog.dismiss();
                    }
                });
        dialog.show();
    }

    private void agregar_contacto() {
        BottomSheetDialog dialog = new BottomSheetDialog(getContext());
        bottomSheetBinding = BottomSheetComprasBinding.inflate(LayoutInflater.from(getContext()));
        dialog.setContentView(bottomSheetBinding.getRoot());
        // info paquetes
        bottomSheetBinding.textTitle.setText("Añadir Contacto");
        bottomSheetBinding.textPlanPrecio.setText(getString(R.string.agregar_amigo_precio));
        bottomSheetBinding.textMessage.setText(getString(R.string.agregar_amigo));
        bottomSheetBinding.inputAmigoNumero.setVisibility(View.VISIBLE);
        bottomSheetBinding.inputAmigoNumero.setEndIconOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        agregarContacto.launch(null);
                    }
                });
        bottomSheetBinding.buttonCompra.setText("Agregar");
        bottomSheetBinding.buttonCompra.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        String contacto =
                                bottomSheetBinding.editAmigoNumero.getText().toString().trim();
                        USSD("*133*4*2*1*" + contacto + Uri.encode("#"));
                        dialog.dismiss();
                    }
                });
        dialog.show();
    }

    private void eliminar_contacto() {
        BottomSheetDialog dialog = new BottomSheetDialog(getContext());
        bottomSheetBinding = BottomSheetComprasBinding.inflate(LayoutInflater.from(getContext()));
        dialog.setContentView(bottomSheetBinding.getRoot());
        // info paquetes
        bottomSheetBinding.textTitle.setText("Eliminar Contacto");
        bottomSheetBinding.textPlanPrecio.setText(getString(R.string.eliminar_amigo_precio));
        bottomSheetBinding.textMessage.setText(getString(R.string.eliminar_amigo));
        bottomSheetBinding.inputAmigoNumero.setVisibility(View.VISIBLE);
        bottomSheetBinding.inputAmigoNumero.setEndIconOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        agregarContacto.launch(null);
                    }
                });
        bottomSheetBinding.buttonCompra.setText("Eliminar");
        bottomSheetBinding.buttonCompra.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        String contacto =
                                bottomSheetBinding.editAmigoNumero.getText().toString().trim();
                        USSD("*133*4*2*2*" + contacto + Uri.encode("#"));
                        dialog.dismiss();
                    }
                });
        dialog.show();
    }

    private final ActivityResultLauncher<Void> agregarContacto =
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
                                            bottomSheetBinding.inputAmigoNumero.setHelperText(name);
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
                                                    //
                                                    bottomSheetBinding.editAmigoNumero.setText(
                                                            replace);
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

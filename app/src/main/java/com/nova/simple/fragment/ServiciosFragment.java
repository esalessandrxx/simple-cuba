package com.nova.simple.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.nova.simple.R;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.google.android.material.tabs.TabLayout;
import com.nova.simple.adapter.ItemTouchListener;
import com.nova.simple.adapter.ItemsAdapter;
import com.nova.simple.databinding.BottomSheetServiciosBinding;
import com.nova.simple.databinding.FragmentPaquetesBinding;
import com.nova.simple.model.GridItem;
import com.nova.simple.model.HeaderItem;
import com.nova.simple.model.Items;
import java.util.ArrayList;

public class ServiciosFragment extends Fragment {

    private FragmentPaquetesBinding binding;
    private BottomSheetServiciosBinding bSheetBinding;
    private ItemsAdapter adapter;
    private ArrayList<Items> item = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflate, ViewGroup parent, Bundle savedInstanceState) {
        binding = FragmentPaquetesBinding.inflate(inflate, parent, false);
        ((TabLayout) getActivity().findViewById(R.id.tab_layout)).setVisibility(View.GONE);
        ((CollapsingToolbarLayout) getActivity().findViewById(R.id.collapsingTbl))
                .setTitle(getString(R.string.title_servicios));
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
                                        tipo_de_red();
                                        break;
                                    case 1:
                                        SMS("2266", "LTE");
                                        break;
                                    case 2:
                                        SMS("2266", "SIM");
                                        break;
                                    case 3:
                                        SMS("8000", "TURISMO");
                                        break;
                                    case 4:
                                        SMS("2266", "OFERTA");
                                        break;
                                    case 5:
                                        SMS("2266", "TARIFA");
                                        break;
                                    case 6:
                                        SMS("8000", "CANCELAR INFOMOVIL");
                                        break;
                                }
                                return;
                            }
                        }));
    }

    private void loadItems() {
        item.add(
                new GridItem(
                        getString(R.string.title_servicio_tipo_red),
                        null,
                        R.drawable.ic_servicios_tipo_red));
        item.add(
                new GridItem(
                        getString(R.string.title_servicio_activa_4g),
                        null,
                        R.drawable.ic_planes_lte));
        item.add(
                new GridItem(
                        getString(R.string.title_servicio_sim),
                        null,
                        R.drawable.ic_servicios_sim_card));
        item.add(
                new GridItem(
                        getString(R.string.title_servicio_turismo),
                        null,
                        R.drawable.ic_servicios_turismo));
        item.add(
                new GridItem(
                        getString(R.string.title_servicio_oferta),
                        null,
                        R.drawable.ic_servicios_ofertas));
        item.add(
                new GridItem(
                        getString(R.string.title_servicio_tarifa),
                        null,
                        R.drawable.ic_servicios_tarifas));
        item.add(
                new GridItem(
                        getString(R.string.title_servicio_cancelar_infomovil),
                        null,
                        R.drawable.ic_servicios_baja_infomovil));
    }

    private void SMS(String number, String message) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED) {
            } else {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S) {
                    SmsManager smsManager = getContext().getSystemService(SmsManager.class);
                    smsManager.sendTextMessage(number, null, message, null, null);
                } else {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(number, null, message, null, null);
                }
            }
        }
    }

    private void tipo_de_red() {
        BottomSheetDialog dialog = new BottomSheetDialog(getContext());
        bSheetBinding = BottomSheetServiciosBinding.inflate(LayoutInflater.from(getContext()));
        dialog.setContentView(bSheetBinding.getRoot());

        //
        bSheetBinding.textTitleServicio.setText("Tipo de Red");
        bSheetBinding.textMessageService.setText(
                "Inserte los primeros 8 dígitos de su IMEI, puede obtenerlo marcando *#06#");
        bSheetBinding.inputServiceNumero.setHint("IMEI");
        bSheetBinding.buttonService.setText("Enviar");
        bSheetBinding.buttonService.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        String imei = bSheetBinding.editServiceNumero.getText().toString().trim();
                        SMS("2266", imei);
                    }
                });
        dialog.show();
    }
}

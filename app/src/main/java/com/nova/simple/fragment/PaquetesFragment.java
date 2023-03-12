package com.nova.simple.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telecom.PhoneAccountHandle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

public class PaquetesFragment extends Fragment {

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
                                        USSD("*222*869" + Uri.encode("#"));
                                        break;
                                    case 1:
                                        USSD("*222*767" + Uri.encode("#"));
                                        break;
                                    case 2:
                                        // header
                                        break;
                                    case 3:
                                        minutosA();
                                        break;
                                    case 4:
                                        minutosB();
                                        break;
                                    case 5:
                                        minutosC();
                                        break;
                                    case 6:
                                        minutosD();
                                        break;
                                    case 7:
                                        minutosE();
                                        break;
                                    case 8:
                                        // header
                                        break;
                                    case 9:
                                        mensajesA();
                                        break;
                                    case 10:
                                        mensajesB();
                                        break;
                                    case 11:
                                        mensajesC();
                                        break;
                                    case 12:
                                        mensajesD();
                                        break;
                                }
                                return;
                            }
                        }));
    }

    private void loadItems() {
        item.add(
                new GridItem(
                        getString(R.string.title_estado_minutos),
                        getString(R.string.subtitle_estado_minutos),
                        R.drawable.unselect_llamadas));
        item.add(
                new GridItem(
                        getString(R.string.title_estado_mensajes),
                        getString(R.string.subtitle_estado_mensajes),
                        R.drawable.ic_paquetes_sms));

        // TODO: minutos items
        item.add(new HeaderItem(getString(R.string.paquete_categoria_minutos)));
        item.add(
                new GridItem(
                        getString(R.string.title_voz_a),
                        getString(R.string.paquete_categoria_minutos),
                        R.drawable.unselect_llamadas));
        item.add(
                new GridItem(
                        getString(R.string.title_voz_b),
                        getString(R.string.paquete_categoria_minutos),
                        R.drawable.unselect_llamadas));
        item.add(
                new GridItem(
                        getString(R.string.title_voz_c),
                        getString(R.string.paquete_categoria_minutos),
                        R.drawable.unselect_llamadas));
        item.add(
                new GridItem(
                        getString(R.string.title_voz_d),
                        getString(R.string.paquete_categoria_minutos),
                        R.drawable.unselect_llamadas));
        item.add(
                new GridItem(
                        getString(R.string.title_voz_e),
                        getString(R.string.paquete_categoria_minutos),
                        R.drawable.unselect_llamadas));

        // TODO: Mensajes items
        item.add(new HeaderItem(getString(R.string.paquete_categoria_mensajes)));
        item.add(
                new GridItem(
                        getString(R.string.title_sms_a),
                        getString(R.string.paquete_categoria_mensajes),
                        R.drawable.ic_paquetes_sms));
        item.add(
                new GridItem(
                        getString(R.string.title_sms_b),
                        getString(R.string.paquete_categoria_mensajes),
                        R.drawable.ic_paquetes_sms));
        item.add(
                new GridItem(
                        getString(R.string.title_sms_c),
                        getString(R.string.paquete_categoria_mensajes),
                        R.drawable.ic_paquetes_sms));
        item.add(
                new GridItem(
                        getString(R.string.title_sms_d),
                        getString(R.string.paquete_categoria_mensajes),
                        R.drawable.ic_paquetes_sms));
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

    private void minutosA() {
        BottomSheetDialog dialog = new BottomSheetDialog(getContext());
        bottomSheetBinding = BottomSheetComprasBinding.inflate(LayoutInflater.from(getContext()));
        dialog.setContentView(bottomSheetBinding.getRoot());
        // info paquetes
        bottomSheetBinding.textTitle.setText("5 Minutos");
        bottomSheetBinding.textPlanPrecio.setText(getString(R.string.minutos_5_precio));
        bottomSheetBinding.textMessage.setText(getString(R.string.minutos_5));
        bottomSheetBinding.buttonCompra.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        USSD("*133*3*1*1" + Uri.encode("#"));
                        dialog.dismiss();
                    }
                });
        dialog.show();
    }

    private void minutosB() {
        BottomSheetDialog dialog = new BottomSheetDialog(getContext());
        bottomSheetBinding = BottomSheetComprasBinding.inflate(LayoutInflater.from(getContext()));
        dialog.setContentView(bottomSheetBinding.getRoot());
        // info paquetes
        bottomSheetBinding.textTitle.setText("10 Minutos");
        bottomSheetBinding.textPlanPrecio.setText(getString(R.string.minutos_10_precio));
        bottomSheetBinding.textMessage.setText(getString(R.string.minutos_10));
        bottomSheetBinding.buttonCompra.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        USSD("*133*3*2*1" + Uri.encode("#"));
                        dialog.dismiss();
                    }
                });
        dialog.show();
    }

    private void minutosC() {
        BottomSheetDialog dialog = new BottomSheetDialog(getContext());
        bottomSheetBinding = BottomSheetComprasBinding.inflate(LayoutInflater.from(getContext()));
        dialog.setContentView(bottomSheetBinding.getRoot());
        // info paquetes
        bottomSheetBinding.textTitle.setText("15 Minutos");
        bottomSheetBinding.textPlanPrecio.setText(getString(R.string.minutos_15_precio));
        bottomSheetBinding.textMessage.setText(getString(R.string.minutos_15));
        bottomSheetBinding.buttonCompra.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        USSD("*133*3*3*1" + Uri.encode("#"));
                        dialog.dismiss();
                    }
                });
        dialog.show();
    }

    private void minutosD() {
        BottomSheetDialog dialog = new BottomSheetDialog(getContext());
        bottomSheetBinding = BottomSheetComprasBinding.inflate(LayoutInflater.from(getContext()));
        dialog.setContentView(bottomSheetBinding.getRoot());
        // info paquetes
        bottomSheetBinding.textTitle.setText("25 Minutos");
        bottomSheetBinding.textPlanPrecio.setText(getString(R.string.minutos_25_precio));
        bottomSheetBinding.textMessage.setText(getString(R.string.minutos_25));
        bottomSheetBinding.buttonCompra.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        USSD("*133*3*4*1" + Uri.encode("#"));
                        dialog.dismiss();
                    }
                });
        dialog.show();
    }

    private void minutosE() {
        BottomSheetDialog dialog = new BottomSheetDialog(getContext());
        bottomSheetBinding = BottomSheetComprasBinding.inflate(LayoutInflater.from(getContext()));
        dialog.setContentView(bottomSheetBinding.getRoot());
        // info paquetes
        bottomSheetBinding.textTitle.setText("40 Minutos");
        bottomSheetBinding.textPlanPrecio.setText(getString(R.string.minutos_40_precio));
        bottomSheetBinding.textMessage.setText(getString(R.string.minutos_40));
        bottomSheetBinding.buttonCompra.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        USSD("*133*3*5*1" + Uri.encode("#"));
                        dialog.dismiss();
                    }
                });
        dialog.show();
    }

    // TODO: Mensajes
    private void mensajesA() {
        BottomSheetDialog dialog = new BottomSheetDialog(getContext());
        bottomSheetBinding = BottomSheetComprasBinding.inflate(LayoutInflater.from(getContext()));
        dialog.setContentView(bottomSheetBinding.getRoot());
        // info paquetes
        bottomSheetBinding.textTitle.setText("20 Mensajes");
        bottomSheetBinding.textPlanPrecio.setText(getString(R.string.plan_sms_20_precio));
        bottomSheetBinding.textMessage.setText(getString(R.string.plan_sms_20));
        bottomSheetBinding.buttonCompra.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        USSD("*133*2*1*1" + Uri.encode("#"));
                        dialog.dismiss();
                    }
                });
        dialog.show();
    }

    private void mensajesB() {
        BottomSheetDialog dialog = new BottomSheetDialog(getContext());
        bottomSheetBinding = BottomSheetComprasBinding.inflate(LayoutInflater.from(getContext()));
        dialog.setContentView(bottomSheetBinding.getRoot());
        // info paquetes
        bottomSheetBinding.textTitle.setText("50 Mensajes");
        bottomSheetBinding.textPlanPrecio.setText(getString(R.string.plan_sms_50_precio));
        bottomSheetBinding.textMessage.setText(getString(R.string.plan_sms_50));
        bottomSheetBinding.buttonCompra.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        USSD("*133*2*2*1" + Uri.encode("#"));
                        dialog.dismiss();
                    }
                });
        dialog.show();
    }

    private void mensajesC() {
        BottomSheetDialog dialog = new BottomSheetDialog(getContext());
        bottomSheetBinding = BottomSheetComprasBinding.inflate(LayoutInflater.from(getContext()));
        dialog.setContentView(bottomSheetBinding.getRoot());
        // info paquetes
        bottomSheetBinding.textTitle.setText("90 Mensajes");
        bottomSheetBinding.textPlanPrecio.setText(getString(R.string.plan_sms_90_precio));
        bottomSheetBinding.textMessage.setText(getString(R.string.plan_sms_90));
        bottomSheetBinding.buttonCompra.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        USSD("*133*2*3*1" + Uri.encode("#"));
                        dialog.dismiss();
                    }
                });
        dialog.show();
    }

    private void mensajesD() {
        BottomSheetDialog dialog = new BottomSheetDialog(getContext());
        bottomSheetBinding = BottomSheetComprasBinding.inflate(LayoutInflater.from(getContext()));
        dialog.setContentView(bottomSheetBinding.getRoot());
        // info paquetes
        bottomSheetBinding.textTitle.setText("120 Mensajes");
        bottomSheetBinding.textPlanPrecio.setText(getString(R.string.plan_sms_120_precio));
        bottomSheetBinding.textMessage.setText(getString(R.string.plan_sms_120));
        bottomSheetBinding.buttonCompra.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        USSD("*133*2*4*1" + Uri.encode("#"));
                        dialog.dismiss();
                    }
                });
        dialog.show();
    }
}

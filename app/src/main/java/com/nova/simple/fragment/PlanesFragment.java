package com.nova.simple.fragment;

import android.Manifest;
import android.content.Context;
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
import android.widget.LinearLayout;
import android.widget.SlidingDrawer;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.nova.simple.R;
import com.nova.simple.adapter.ItemTouchListener;
import com.nova.simple.adapter.ItemsAdapter;
import com.nova.simple.databinding.BottomSheetComprasBinding;
import com.nova.simple.databinding.FragmentPlanesBinding;
import com.nova.simple.model.GridItem;
import com.nova.simple.model.HeaderItem;
import com.nova.simple.model.Items;
import java.util.ArrayList;
import java.util.List;

public class PlanesFragment extends Fragment {

    private FragmentPlanesBinding binding;
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
        binding = FragmentPlanesBinding.inflate(inflate, parent, false);
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

        // TODO: RecyclerView items
        binding.recyclerViewPlanes.setHasFixedSize(true);
        binding.recyclerViewPlanes.setNestedScrollingEnabled(false);
        adapter = new ItemsAdapter(getContext(), item);
        binding.recyclerViewPlanes.setAdapter(adapter);
        GridLayoutManager manager = new GridLayoutManager(getContext(), 2);
        manager.setSpanSizeLookup(
                new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return adapter.getItemViewType(position) == HeaderItem.VIEW_HEADER ? 2 : 1;
                    }
                });
        binding.recyclerViewPlanes.setLayoutManager(manager);
        loadItems();

        // TODO: Tarifa por consumo
        SharedPreferences sp_tarifa =
                getActivity().getSharedPreferences("tarifa", Context.MODE_PRIVATE);
        binding.switchTarifaPorConsumo.setChecked(sp_tarifa.getBoolean("isChecked", false));
        binding.switchTarifaPorConsumo.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        if (binding.switchTarifaPorConsumo.isChecked()) {
                            USSD("*133*1*1*1" + Uri.encode("#"));
                            SharedPreferences.Editor edit =
                                    getActivity()
                                            .getSharedPreferences("tarifa", Context.MODE_PRIVATE)
                                            .edit();
                            edit.putBoolean("isChecked", true);
                            edit.apply();
                            binding.switchTarifaPorConsumo.setChecked(true);
                            Snackbar.make(arg0, "Activando...", Snackbar.LENGTH_LONG).show();
                        } else {
                            USSD("*133*1*1*2" + Uri.encode("#"));
                            SharedPreferences.Editor edit =
                                    getActivity()
                                            .getSharedPreferences("tarifa", Context.MODE_PRIVATE)
                                            .edit();
                            edit.putBoolean("isChecked", false);
                            edit.apply();
                            binding.switchTarifaPorConsumo.setChecked(false);
                            Snackbar.make(arg0, "Desactivando...", Snackbar.LENGTH_LONG).show();
                        }
                    }
                });

        // TODO: items recyclerview touch
        binding.recyclerViewPlanes.addOnItemTouchListener(
                new ItemTouchListener(
                        getActivity(),
                        binding.recyclerViewPlanes,
                        new ItemTouchListener.ClickListener() {
                            @Override
                            public void onClick(View view, int position) {
                                switch (position) {
                                    case 0:
                                        // header
                                        break;
                                    case 1:
                                        planBasico();
                                        break;
                                    case 2:
                                        planMedio();
                                        break;
                                    case 3:
                                        planExtra();
                                        break;
                                    case 4:
                                        // header
                                        break;
                                    case 5:
                                        paqueteA();
                                        break;
                                    case 6:
                                        paqueteB();
                                        break;
                                    case 7:
                                        paqueteC();
                                        break;
                                    case 8:
                                        // header
                                        break;
                                    case 9:
                                        bolsaDiaria();
                                        break;
                                    case 10:
                                        bolsaMensajeria();
                                        break;
                                }
                                return;
                            }
                        }));
    }

    private void loadItems() {
        item.add(new HeaderItem(getString(R.string.title_categoria_combinados)));
        item.add(
                new GridItem(
                        getString(R.string.title_plan_basico),
                        getString(R.string.subtitle_plan_basico),
                        R.drawable.ic_planes_combinados));
        item.add(
                new GridItem(
                        getString(R.string.title_plan_medio),
                        getString(R.string.subtitle_plan_medio),
                        R.drawable.ic_planes_combinados));
        item.add(
                new GridItem(
                        getString(R.string.title_plan_extra),
                        getString(R.string.subtitle_plan_extra),
                        R.drawable.ic_planes_combinados));
        item.add(new HeaderItem(getString(R.string.title_categoria_lte)));
        item.add(
                new GridItem(
                        getString(R.string.title_lte_a),
                        getString(R.string.subtitle_lte_a),
                        R.drawable.ic_planes_lte));
        item.add(
                new GridItem(
                        getString(R.string.title_lte_b),
                        getString(R.string.subtitle_lte_b),
                        R.drawable.ic_planes_lte));
        item.add(
                new GridItem(
                        getString(R.string.title_lte_c),
                        getString(R.string.subtitle_lte_c),
                        R.drawable.ic_planes_lte));
        item.add(new HeaderItem(getString(R.string.title_categoria_bolsas)));
        item.add(
                new GridItem(
                        getString(R.string.title_diaria),
                        getString(R.string.subtitle_diaria),
                        R.drawable.ic_planes_diaria));
        item.add(
                new GridItem(
                        getString(R.string.title_mensajeria),
                        getString(R.string.subtitle_mensajeria),
                        R.drawable.ic_planes_mensajeria));
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

    private void planBasico() {
        BottomSheetDialog dialog = new BottomSheetDialog(getContext());
        bottomSheetBinding = BottomSheetComprasBinding.inflate(LayoutInflater.from(getContext()));
        dialog.setContentView(bottomSheetBinding.getRoot());
        // info paquetes
        bottomSheetBinding.textTitle.setText("Plan Básico");
        bottomSheetBinding.textPlanPrecio.setText(getString(R.string.plan_basico_precio));
        bottomSheetBinding.textMessage.setText(getString(R.string.plan_basico));
        bottomSheetBinding.buttonCompra.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        USSD("*133*5*1*1" + Uri.encode("#"));
                        dialog.dismiss();
                    }
                });
        dialog.show();
    }

    private void planMedio() {
        BottomSheetDialog dialog = new BottomSheetDialog(getContext());
        bottomSheetBinding = BottomSheetComprasBinding.inflate(LayoutInflater.from(getContext()));
        dialog.setContentView(bottomSheetBinding.getRoot());
        // info paquetes
        bottomSheetBinding.textTitle.setText("Plan Medio");
        bottomSheetBinding.textPlanPrecio.setText(getString(R.string.plan_medio_precio));
        bottomSheetBinding.textMessage.setText(getString(R.string.plan_medio));
        bottomSheetBinding.buttonCompra.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        USSD("*133*5*2*1" + Uri.encode("#"));
                        dialog.dismiss();
                    }
                });
        dialog.show();
    }

    private void planExtra() {
        BottomSheetDialog dialog = new BottomSheetDialog(getContext());
        bottomSheetBinding = BottomSheetComprasBinding.inflate(LayoutInflater.from(getContext()));
        dialog.setContentView(bottomSheetBinding.getRoot());
        // info paquetes
        bottomSheetBinding.textTitle.setText("Plan Extra");
        bottomSheetBinding.textPlanPrecio.setText(getString(R.string.plan_extra_precio));
        bottomSheetBinding.textMessage.setText(getString(R.string.plan_extra));
        bottomSheetBinding.buttonCompra.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        USSD("*133*5*3*1" + Uri.encode("#"));
                        dialog.dismiss();
                    }
                });
        dialog.show();
    }

    private void paqueteA() {
        BottomSheetDialog dialog = new BottomSheetDialog(getContext());
        bottomSheetBinding = BottomSheetComprasBinding.inflate(LayoutInflater.from(getContext()));
        dialog.setContentView(bottomSheetBinding.getRoot());
        // info paquetes
        bottomSheetBinding.textTitle.setText("Paquete");
        bottomSheetBinding.textPlanPrecio.setText(getString(R.string.paquete_a_precio));
        bottomSheetBinding.textMessage.setText(getString(R.string.paquete_a));
        bottomSheetBinding.buttonCompra.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        USSD("*133*1*4*1*1" + Uri.encode("#"));
                        dialog.dismiss();
                    }
                });
        dialog.show();
    }

    private void paqueteB() {
        BottomSheetDialog dialog = new BottomSheetDialog(getContext());
        bottomSheetBinding = BottomSheetComprasBinding.inflate(LayoutInflater.from(getContext()));
        dialog.setContentView(bottomSheetBinding.getRoot());
        // info paquetes
        bottomSheetBinding.textTitle.setText("Paquete");
        bottomSheetBinding.textPlanPrecio.setText(getString(R.string.paquete_b_precio));
        bottomSheetBinding.textMessage.setText(getString(R.string.paquete_b));
        bottomSheetBinding.buttonCompra.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        USSD("*133*1*4*2*1" + Uri.encode("#"));
                        dialog.dismiss();
                    }
                });
        dialog.show();
    }

    private void paqueteC() {
        BottomSheetDialog dialog = new BottomSheetDialog(getContext());
        bottomSheetBinding = BottomSheetComprasBinding.inflate(LayoutInflater.from(getContext()));
        dialog.setContentView(bottomSheetBinding.getRoot());
        // info paquetes
        bottomSheetBinding.textTitle.setText("Paquete");
        bottomSheetBinding.textPlanPrecio.setText(getString(R.string.paquete_c_precio));
        bottomSheetBinding.textMessage.setText(getString(R.string.paquete_c));
        bottomSheetBinding.buttonCompra.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        USSD("*133*1*4*3*1" + Uri.encode("#"));
                        dialog.dismiss();
                    }
                });
        dialog.show();
    }

    private void bolsaDiaria() {
        BottomSheetDialog dialog = new BottomSheetDialog(getContext());
        bottomSheetBinding = BottomSheetComprasBinding.inflate(LayoutInflater.from(getContext()));
        dialog.setContentView(bottomSheetBinding.getRoot());
        // info paquetes
        bottomSheetBinding.textTitle.setText("Bolsa Diaria");
        bottomSheetBinding.textPlanPrecio.setText(getString(R.string.diaria_precio));
        bottomSheetBinding.textMessage.setText(getString(R.string.diaria));
        bottomSheetBinding.buttonCompra.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        USSD("*133*1*3*1" + Uri.encode("#"));
                        dialog.dismiss();
                    }
                });
        dialog.show();
    }

    private void bolsaMensajeria() {
        BottomSheetDialog dialog = new BottomSheetDialog(getContext());
        bottomSheetBinding = BottomSheetComprasBinding.inflate(LayoutInflater.from(getContext()));
        dialog.setContentView(bottomSheetBinding.getRoot());
        // info paquetes
        bottomSheetBinding.textTitle.setText("Mensajería");
        bottomSheetBinding.textPlanPrecio.setText(getString(R.string.mensajeria_precio));
        bottomSheetBinding.textMessage.setText(getString(R.string.mensajeria));
        bottomSheetBinding.buttonCompra.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        USSD("*133*1*2*1" + Uri.encode("#"));
                        dialog.dismiss();
                    }
                });
        dialog.show();
    }
}

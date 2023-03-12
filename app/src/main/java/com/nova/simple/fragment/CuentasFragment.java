package com.nova.simple.fragment;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.telecom.PhoneAccountHandle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.ThemeUtils;

import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.elevation.SurfaceColors;
import com.nova.simple.R;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import com.google.android.material.tabs.TabLayout;
import com.nova.simple.R;
import com.nova.simple.databinding.CustomProgressDialogBinding;
import com.nova.simple.databinding.FragmentCuentasBinding;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/* Agregué: replace("Tarifa: No activa. Diaria: No dispone de MB.","0 MB")
a los paquetes para probar si el usuario solo tiene una bolsa diaria y se le vence,
que no se vea afectado la informacion de paquetes, LTE y vencimiento de paquetes
 */
public class CuentasFragment extends Fragment {

    private FragmentCuentasBinding binding;
    TelephonyManager manager, manager2, managerMain;
    SharedPreferences sp_cuentas;
    SharedPreferences.Editor editor;

    AlertDialog alertDialog;
    private CustomProgressDialogBinding dialog;

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
        binding = FragmentCuentasBinding.inflate(inflate, parent, false);
        ((TabLayout) getActivity().findViewById(R.id.tab_layout)).setVisibility(View.GONE);
        ((CollapsingToolbarLayout) getActivity().findViewById(R.id.collapsingTbl))
                .setTitle(getString(R.string.title_cuenta));
        setHasOptionsMenu(true);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }

    @Override
    public void onViewCreated(View arg0, Bundle arg1) {
        super.onViewCreated(arg0, arg1);

        // TODO: SharedPreferences para guardar datos de cuentas
        sp_cuentas = getActivity().getSharedPreferences("cuentas", Context.MODE_PRIVATE);
        editor = sp_cuentas.edit();

        // TODO: Preferences DualSIM
        sp_sim = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sim = (sp_sim.getString(getString(R.string.sim_key), "0"));

        // TODO: Ver bonos promocionales
        binding.buttonCuentasBono.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String promo = sp_cuentas.getString("bonos", null);
                        new MaterialAlertDialogBuilder(getContext())
                                .setMessage(promo)
                                .setPositiveButton("Ok", null)
                                .show();
                    }
                });

        // TODO: SwipeRefresh
        binding.swipeRefresh.setColorSchemeResources(R.color.md_theme_light_primary);
        binding.swipeRefresh.setProgressBackgroundColorSchemeColor(
                SurfaceColors.SURFACE_1.getColor(getActivity()));
        binding.swipeRefresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        // Progress dialog
                        dialog =
                                CustomProgressDialogBinding.inflate(
                                        LayoutInflater.from(getContext()));
                        alertDialog =
                                new MaterialAlertDialogBuilder(getActivity())
                                        .setView(dialog.getRoot())
                                        .setCancelable(false)
                                        .create();
                        alertDialog.show();

                        // UPDATE
                        new Handler(Looper.getMainLooper())
                                .postDelayed(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                if (sim.equals("0")) {
                                                    for (String s : simSlotName) {
                                                        consultaSaldo("*222#", 0);
                                                    }
                                                } else if (sim.equals("1")) {
                                                    for (String s : simSlotName) {
                                                        consultaSaldo("*222#", 1);
                                                    }
                                                }
                                                dialog.textProgressCuenta.setText(
                                                        "Actualizando saldo");
                                                // cancel swipe
                                                // binding.swipeRefresh.setRefreshing(false);
                                                // updateMinutos();

                                                //// MINUTOS
                                                new Handler(Looper.getMainLooper())
                                                        .postDelayed(
                                                                new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        if (sim.equals("0")) {
                                                                            for (String s :
                                                                                    simSlotName) {
                                                                                consultaMinutos(
                                                                                        "*222*869#",
                                                                                        0);
                                                                            }
                                                                        } else if (sim.equals(
                                                                                "1")) {
                                                                            for (String s :
                                                                                    simSlotName) {
                                                                                consultaMinutos(
                                                                                        "*222*869#",
                                                                                        1);
                                                                            }
                                                                        }
                                                                        dialog.textProgressCuenta
                                                                                .setText(
                                                                                        "Actualizando minutos");

                                                                        // MENSAJES
                                                                        new Handler(
                                                                                        Looper
                                                                                                .getMainLooper())
                                                                                .postDelayed(
                                                                                        new Runnable() {
                                                                                            @Override
                                                                                            public
                                                                                            void
                                                                                                    run() {
                                                                                                if (sim
                                                                                                        .equals(
                                                                                                                "0")) {
                                                                                                    for (String
                                                                                                            s :
                                                                                                                    simSlotName) {
                                                                                                        consultaMensajes(
                                                                                                                "*222*767#",
                                                                                                                0);
                                                                                                    }
                                                                                                } else if (sim
                                                                                                        .equals(
                                                                                                                "1")) {
                                                                                                    for (String
                                                                                                            s :
                                                                                                                    simSlotName) {
                                                                                                        consultaMensajes(
                                                                                                                "*222*767#",
                                                                                                                1);
                                                                                                    }
                                                                                                }
                                                                                                dialog
                                                                                                        .textProgressCuenta
                                                                                                        .setText(
                                                                                                                "Actualizando mensajes");
                                                                                                // BONOS
                                                                                                new Handler(
                                                                                                                Looper
                                                                                                                        .getMainLooper())
                                                                                                        .postDelayed(
                                                                                                                new Runnable() {
                                                                                                                    @Override
                                                                                                                    public
                                                                                                                    void
                                                                                                                            run() {
                                                                                                                        if (sim
                                                                                                                                .equals(
                                                                                                                                        "0")) {
                                                                                                                            for (String
                                                                                                                                    s :
                                                                                                                                            simSlotName) {
                                                                                                                                consultaBonos(
                                                                                                                                        "*222*266#",
                                                                                                                                        0);
                                                                                                                            }
                                                                                                                        } else if (sim
                                                                                                                                .equals(
                                                                                                                                        "1")) {
                                                                                                                            for (String
                                                                                                                                    s :
                                                                                                                                            simSlotName) {
                                                                                                                                consultaBonos(
                                                                                                                                        "*222*266#",
                                                                                                                                        1);
                                                                                                                            }
                                                                                                                        }
                                                                                                                        dialog
                                                                                                                                .textProgressCuenta
                                                                                                                                .setText(
                                                                                                                                        "Actualizando bonos");

                                                                                                                        /// DATOS
                                                                                                                        new Handler(
                                                                                                                                        Looper
                                                                                                                                                .getMainLooper())
                                                                                                                                .postDelayed(
                                                                                                                                        new Runnable() {
                                                                                                                                            @Override
                                                                                                                                            public
                                                                                                                                            void
                                                                                                                                                    run() {
                                                                                                                                                if (sim
                                                                                                                                                        .equals(
                                                                                                                                                                "0")) {
                                                                                                                                                    for (String
                                                                                                                                                            s :
                                                                                                                                                                    simSlotName) {
                                                                                                                                                        consultaDatos(
                                                                                                                                                                "*222*328#",
                                                                                                                                                                0);
                                                                                                                                                    }
                                                                                                                                                } else if (sim
                                                                                                                                                        .equals(
                                                                                                                                                                "1")) {
                                                                                                                                                    for (String
                                                                                                                                                            s :
                                                                                                                                                                    simSlotName) {
                                                                                                                                                        consultaDatos(
                                                                                                                                                                "*222*328#",
                                                                                                                                                                1);
                                                                                                                                                    }
                                                                                                                                                }
                                                                                                                                                dialog
                                                                                                                                                        .textProgressCuenta
                                                                                                                                                        .setText(
                                                                                                                                                                "Actualizando datos");
                                                                                                                                                binding
                                                                                                                                                        .swipeRefresh
                                                                                                                                                        .setRefreshing(
                                                                                                                                                                false);
                                                                                                                                                // hora
                                                                                                                                                SimpleDateFormat
                                                                                                                                                        hora =
                                                                                                                                                                new SimpleDateFormat(
                                                                                                                                                                        "h:mm a");
                                                                                                                                                String
                                                                                                                                                        time =
                                                                                                                                                                hora
                                                                                                                                                                        .format(
                                                                                                                                                                                new Date());
                                                                                                                                                editor
                                                                                                                                                        .putString(
                                                                                                                                                                "hora",
                                                                                                                                                                time
                                                                                                                                                                        .toString());
                                                                                                                                                editor
                                                                                                                                                        .commit();
                                                                                                                                                binding
                                                                                                                                                        .textHoraUpdate
                                                                                                                                                        .setText(
                                                                                                                                                                "Actualizado: "
                                                                                                                                                                        + time);

                                                                                                                                                new Handler(
                                                                                                                                                                Looper
                                                                                                                                                                        .getMainLooper())
                                                                                                                                                        .postDelayed(
                                                                                                                                                                new Runnable() {
                                                                                                                                                                    @Override
                                                                                                                                                                    public
                                                                                                                                                                    void
                                                                                                                                                                            run() {
                                                                                                                                                                        alertDialog
                                                                                                                                                                                .dismiss();
                                                                                                                                                                    }
                                                                                                                                                                },
                                                                                                                                                                5000);
                                                                                                                                            }
                                                                                                                                        },
                                                                                                                                        5000);
                                                                                                                    }
                                                                                                                },
                                                                                                                5000);
                                                                                            }
                                                                                        },
                                                                                        5000);
                                                                    }
                                                                },
                                                                4000);
                                            }
                                        },
                                        1000);
                    }
                });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_cuentas, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.update:
                dialogInformation();
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    private void dialogInformation() {
        new MaterialAlertDialogBuilder(getActivity())
                .setTitle("Atención")
                .setMessage(getString(R.string.cuentas_message))
                .setPositiveButton("Ok", null)
                .show();
    }

    public void consultaSaldo(String ussdCode, int sim) {
        if (ussdCode.equalsIgnoreCase("")) return;
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.CALL_PHONE}, 234);
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
            manager2 = manager.createForSubscriptionId(2);
            managerMain = (sim == 0) ? manager : manager2;
            managerMain.sendUssdRequest(
                    ussdCode,
                    new TelephonyManager.UssdResponseCallback() {
                        @Override
                        public void onReceiveUssdResponse(
                                TelephonyManager telephonyManager,
                                String request,
                                CharSequence response) {
                            super.onReceiveUssdResponse(telephonyManager, request, response);

                            String message_saldo =
                                    response.toString()
                                            .replace("Saldo:", "")
                                            .replaceFirst("CUP(.*)", "")
                                            .trim();
                            StringBuilder sb1 = new StringBuilder();
                            sb1.append(message_saldo);
                            sb1.append(" CUP");
                            binding.textCuentasSaldo.setText(sb1);
                            editor.putString("saldo", sb1.toString());
                            editor.commit();

                            // TODO: String mostrar fecha de vencimiento de saldo principal
                            String message_vence =
                                    response.toString()
                                            .replaceFirst("(.*)vence", "")
                                            .replace("-", "/")
                                            .replace(".", "")
                                            .trim();
                            StringBuilder sb2 = new StringBuilder();
                            sb2.append("Expira: ");
                            sb2.append(message_vence);
                            binding.textCuentasVenceSim.setText(sb2);
                            editor.putString("vence_saldo", sb2.toString());
                            editor.commit();
                        }

                        @Override
                        public void onReceiveUssdResponseFailed(
                                TelephonyManager telephonyManager,
                                String request,
                                int failureCode) {
                            super.onReceiveUssdResponseFailed(
                                    telephonyManager, request, failureCode);

                            Log.e(
                                    "TAG",
                                    "onReceiveUssdResponseFailed: " + "" + failureCode + request);
                        }
                    },
                    new Handler(Looper.getMainLooper()));
        }
    }

    public void consultaMinutos(String ussdCode, int sim) {
        if (ussdCode.equalsIgnoreCase("")) return;
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.CALL_PHONE}, 234);
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
            manager2 = manager.createForSubscriptionId(2);
            managerMain = (sim == 0) ? manager : manager2;
            managerMain.sendUssdRequest(
                    ussdCode,
                    new TelephonyManager.UssdResponseCallback() {
                        @Override
                        public void onReceiveUssdResponse(
                                TelephonyManager telephonyManager,
                                String request,
                                CharSequence response) {
                            super.onReceiveUssdResponse(telephonyManager, request, response);
                            String minutos =
                                    response.toString()
                                            .replace(
                                                    "Usted debe adquirir un plan de minutos. Para una nueva compra marque *133#",
                                                    "00.00.00")
                                            .replace("Usted dispone de", "")
                                            .replaceFirst("MIN(.*)", "")
                                            .trim();
                            binding.textCuentasMinutos.setText(minutos);
                            editor.putString("minutos", minutos.toString());
                            editor.commit();
                        }

                        @Override
                        public void onReceiveUssdResponseFailed(
                                TelephonyManager telephonyManager,
                                String request,
                                int failureCode) {
                            super.onReceiveUssdResponseFailed(
                                    telephonyManager, request, failureCode);

                            Log.e(
                                    "TAG",
                                    "onReceiveUssdResponseFailed: " + "" + failureCode + request);
                        }
                    },
                    new Handler(Looper.getMainLooper()));
        }
    }

    public void consultaMensajes(String ussdCode, int sim) {
        if (ussdCode.equalsIgnoreCase("")) return;
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.CALL_PHONE}, 234);
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
            manager2 = manager.createForSubscriptionId(2);
            managerMain = (sim == 0) ? manager : manager2;
            managerMain.sendUssdRequest(
                    ussdCode,
                    new TelephonyManager.UssdResponseCallback() {
                        @Override
                        public void onReceiveUssdResponse(
                                TelephonyManager telephonyManager,
                                String request,
                                CharSequence response) {
                            super.onReceiveUssdResponse(telephonyManager, request, response);
                            // cantidad de mensajes
                            String message_sms =
                                    response.toString()
                                            .replace(
                                                    "Usted debe adquirir un plan de SMS. Para una nueva compra marque *133#",
                                                    "0")
                                            .replace(
                                                    "Usted no dispone de SMS. Para una nueva compra marque *133#",
                                                    "0")
                                            .replace("Usted dispone de", "")
                                            .replace("no activos.", "")
                                            .replaceFirst("SMS(.*)", "")
                                            .trim();
                            binding.textCuentasMensajes.setText(message_sms);
                            editor.putString("sms", message_sms.toString());
                            editor.commit();

                            // Fecha vencimiento sms y voz
                            String message_vence_sms =
                                    response.toString()
                                            .replace(
                                                    "Usted debe adquirir un plan de SMS. Para una nueva compra marque *133#",
                                                    "0 días")
                                            .replace(
                                                    "Usted no dispone de SMS. Para una nueva compra marque *133#",
                                                    "0 días")
                                            .replaceFirst("(.*)por", "")
                                            .replace("dias", "días")
                                            .trim();
                            editor.putString(
                                    "vence_sms",
                                    message_vence_sms
                                            .toString()
                                            .replaceFirst("(.*)no activos.", "0 días"));
                            editor.commit();
                            binding.textCuentasVenceMinSms.setText(message_vence_sms);

                            // no usp
                            String vence_sms_min =
                                    sp_cuentas.getString("vence_sms", message_vence_sms.toString());
                        }

                        @Override
                        public void onReceiveUssdResponseFailed(
                                TelephonyManager telephonyManager,
                                String request,
                                int failureCode) {
                            super.onReceiveUssdResponseFailed(
                                    telephonyManager, request, failureCode);

                            Log.e(
                                    "TAG",
                                    "onReceiveUssdResponseFailed: " + "" + failureCode + request);
                        }
                    },
                    new Handler(Looper.getMainLooper()));
        }
    }

    public void consultaDatos(String ussdCode, int sim) {
        if (ussdCode.equalsIgnoreCase("")) return;
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.CALL_PHONE}, 234);
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
            manager2 = manager.createForSubscriptionId(2);
            managerMain = (sim == 0) ? manager : manager2;
            managerMain.sendUssdRequest(
                    ussdCode,
                    new TelephonyManager.UssdResponseCallback() {
                        @Override
                        public void onReceiveUssdResponse(
                                TelephonyManager telephonyManager,
                                String request,
                                CharSequence response) {
                            super.onReceiveUssdResponse(telephonyManager, request, response);
                            String todos_los_paquetes =
                                    "Tarifa: No activa. Diaria: 200 MB no activos. Mensajeria: 599.53 MB validos 8 dias. Paquetes: 3.57 GB + 3.10 GB LTE no activos.";

                            // bolsa diaria
                            String diaria =
                                    response.toString()
                                            .replace(
                                                    "Tarifa: No activa. Ud debe adquirir una oferta. Para una nueva compra marque *133#",
                                                    "0 MB")
                                            .replace(
                                                    "Tarifa: No activa. Paquetes: No dispone de MB.",
                                                    "0 MB")
                                            .replace("Tarifa: No activa. Diaria:", "")
                                            .replace(
                                                    "Tarifa: No activa. Diaria: No dispone de MB.",
                                                    "0 MB")
                                            .replace("Tarifa: No activa.", "0 MB")
                                            .replaceFirst("Mensajeria:(.*)", "")
                                            .replaceFirst("Paquetes:(.*)", "")
                                            .trim();
                            editor.putString(
                                    "diaria",
                                    diaria.toString()
                                            .replace("no activos.", "")
                                            .replaceFirst("validos(.*)", "")
                                            .replace("No dispone de MB.", "0 MB"));
                            editor.commit();
                            String bolsa_diaria = sp_cuentas.getString("diaria", diaria.toString());
                            binding.textCuentasDiaria.setText(bolsa_diaria);

                            // vencimiento de bolsa diaria
                            String message_vence_diaria =
                                    response.toString()
                                            .replace(
                                                    "Tarifa: No activa. Ud debe adquirir una oferta. Para una nueva compra marque *133#",
                                                    "Debe adquirir un paquete")
                                            .replace(
                                                    "Tarifa: No activa. Paquetes: No dispone de MB.",
                                                    "0 horas")
                                            .replace("Tarifa: No activa. Diaria:", "")
                                            .replace("Tarifa: No activa.", "0 horas")
                                            .replaceFirst("Mensajeria:(.*)", "")
                                            .replaceFirst("Paquetes:(.*)", "")
                                            .trim();
                            editor.putString(
                                    "vence_diaria",
                                    message_vence_diaria
                                            .toString()
                                            .replaceFirst("(.*)no activos.", "sin consumir")
                                            .replaceFirst("(.*)validos", "")
                                            .replace("No dispone de MB.", "0 horas")
                                            .replace("horas.", "horas"));
                            editor.commit();
                            String vence_diaria =
                                    sp_cuentas.getString(
                                            "vence_diaria", message_vence_diaria.toString());
                            binding.textCuentasVenceDiaria.setText(vence_diaria);

                            // bolsa de mensajeria
                            String message_mensajeria =
                                    response.toString()
                                            .replace(
                                                    "Tarifa: No activa. Ud debe adquirir una oferta. Para una nueva compra marque *133#",
                                                    "0 MB")
                                            .replace(
                                                    "Tarifa: No activa. Paquetes: No dispone de MB.",
                                                    "0 MB")
                                            .replace(
                                                    "Tarifa: No activa. Diaria: No dispone de MB. Mensajeria:",
                                                    "")
                                            .replaceFirst("(.*)no activos. Mensajeria:", "")
                                            .replaceFirst("(.*)horas. Mensajeria:", "")
                                            .replace(
                                                    "Tarifa: No activa. Mensajeria: No dispone de MB.",
                                                    "0 MB")
                                            .replace("Tarifa: No activa. Mensajeria:", "")
                                            .replace("Tarifa: No activa.", "0 MB")
                                            .replaceFirst("Diaria:(.*)", "")
                                            .replaceFirst("Paquetes:(.*)", "")
                                            .trim();

                            editor.putString(
                                    "mensajeria",
                                    message_mensajeria
                                            .toString()
                                            .replace("vencen hoy.", "")
                                            .replaceFirst("validos(.*)", ""));
                            editor.commit();
                            String bolsa_msg =
                                    sp_cuentas.getString(
                                            "mensajeria", message_mensajeria.toString());
                            binding.textCuentasMensajeria.setText(bolsa_msg);

                            // vence mensajeria
                            String message_vence_mensajeria =
                                    response.toString()
                                            .replace(
                                                    "Tarifa: No activa. Ud debe adquirir una oferta. Para una nueva compra marque *133#",
                                                    "0 días")
                                            .replace(
                                                    "Tarifa: No activa. Paquetes: No dispone de MB.",
                                                    "0 días")
                                            .replace(
                                                    "Tarifa: No activa. Diaria: No dispone de MB. Mensajeria:",
                                                    "")
                                            .replaceFirst("(.*)no activos. Mensajeria:", "")
                                            .replaceFirst("(.*)horas. Mensajeria:", "")
                                            .replace(
                                                    "Tarifa: No activa. Mensajeria: No dispone de MB.",
                                                    "0 días")
                                            .replace("Tarifa: No activa. Mensajeria:", "")
                                            .replace("Tarifa: No activa.", "0 días")
                                            .replaceFirst("Diaria:(.*)", "")
                                            .replaceFirst("Paquetes:(.*)", "")
                                            .trim();
                            editor.putString(
                                    "vence_mensajeria",
                                    message_vence_mensajeria
                                            .toString()
                                            .replaceFirst("(.*)validos", "")
                                            .replaceFirst("(.*)vencen hoy.", "vence hoy")
                                            .replace("dias.", "días"));
                            editor.commit();
                            String vence_mensajeria =
                                    sp_cuentas.getString("vence_mensajeria", "0 días");
                            binding.textCuentasVenceMensajeria.setText(vence_mensajeria);

                            // paquetes
                            String message_paquete =
                                    response.toString()
                                            .replace(
                                                    "Tarifa: No activa. Ud debe adquirir una oferta. Para una nueva compra marque *133#",
                                                    "0 MB")
                                            .replace(
                                                    "Tarifa: No activa. Diaria: No dispone de MB.",
                                                    "0 MB")
                                            .replaceFirst("(.*)Paquetes:", "")
                                            .replaceFirst("\\+(.*)", "")
                                            .replaceFirst("Tarifa: No activa. Diaria:(.*)", "0 MB")
                                            .replaceFirst(
                                                    "Tarifa: No activa. Mensajeria:(.*)", "0 MB")
                                            .trim();
                            editor.putString(
                                    "paquete",
                                    message_paquete
                                            .toString()
                                            .replaceFirst("(.*)LTE(.*)", "0 MB")
                                            .replaceFirst("validos(.*)", ""));
                            editor.commit();
                            String paquete =
                                    sp_cuentas.getString("paquete", message_paquete.toString());
                            binding.textCuentasDatos.setText(paquete);

                            // TODO: message paquete LTE
                            String message_lte =
                                    response.toString()
                                            .replace(
                                                    "Tarifa: No activa. Ud debe adquirir una oferta. Para una nueva compra marque *133#",
                                                    "0 MB")
                                            .replace(
                                                    "Tarifa: No activa. Diaria: No dispone de MB.",
                                                    "0 MB")
                                            .replaceFirst("(.*)Paquetes:", "")
                                            .replaceFirst("(.*)\\+", "")
                                            .replaceFirst("Tarifa: No activa. Diaria:(.*)", "0 MB")
                                            .replaceFirst(
                                                    "Tarifa: No activa. Mensajeria:(.*)", "0 MB")
                                            .trim();
                            String lte =
                                    message_lte
                                            .toString()
                                            .replaceFirst("LTE(.*)", "")
                                            .replaceFirst("(.*)validos(.*)", "0 MB")
                                            .replace("no activos.", "");
                            editor.putString("lte", lte.toString());
                            editor.commit();
                            binding.textCuentasDatosLte.setText(lte);

                            // TODO: vencimiento de datos
                            String vencimiento_datos =
                                    response.toString()
                                            .replace(
                                                    "Tarifa: No activa. Ud debe adquirir una oferta. Para una nueva compra marque *133#",
                                                    "0 días")
                                            .replace(
                                                    "Tarifa: No activa. Diaria: No dispone de MB.",
                                                    "0 MB")
                                            .replaceFirst("(.*)Paquetes:", "")
                                            .replaceFirst("Tarifa: No activa. Diaria:(.*)", "0 MB")
                                            .replaceFirst(
                                                    "Tarifa: No activa. Mensajeria:(.*)", "0 MB")
                                            .trim();
                            String vence =
                                    vencimiento_datos
                                            .toString()
                                            .replaceFirst("(.*)validos", "")
                                            .replace("dias.", "días")
                                            .replace("no activos", "0 días")
                                            .replaceFirst("(.*)LTE", "");
                            editor.putString("vence_datos", vence.toString());
                            editor.commit();
                            binding.textCuentasVenceDatos.setText(vence);
                        }

                        @Override
                        public void onReceiveUssdResponseFailed(
                                TelephonyManager telephonyManager,
                                String request,
                                int failureCode) {
                            super.onReceiveUssdResponseFailed(
                                    telephonyManager, request, failureCode);

                            Log.e(
                                    "TAG",
                                    "onReceiveUssdResponseFailed: " + "" + failureCode + request);
                        }
                    },
                    new Handler(Looper.getMainLooper()));
        }
    }

    public void consultaBonos(String ussdCode, int sim) {
        if (ussdCode.equalsIgnoreCase("")) return;
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.CALL_PHONE}, 234);
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
            manager2 = manager.createForSubscriptionId(2);
            managerMain = (sim == 0) ? manager : manager2;
            managerMain.sendUssdRequest(
                    ussdCode,
                    new TelephonyManager.UssdResponseCallback() {
                        @Override
                        public void onReceiveUssdResponse(
                                TelephonyManager telephonyManager,
                                String request,
                                CharSequence response) {
                            super.onReceiveUssdResponse(telephonyManager, request, response);
                            String datos_nacionales =
                                    response.toString()
                                            .replace("Usted no dispone de bonos activos.", "0 MB")
                                            .replaceFirst("(.*)Datos.cu ", "")
                                            .replaceFirst("vence(.*)", "")
                                            .trim();
                            editor.putString("datos_nacionales", datos_nacionales.toString());
                            editor.commit();
                            binding.textCuentasDatosNacionales.setText(datos_nacionales);

                            // TODO: Bonos en promoción
                            String bonos =
                                    response.toString()
                                            .replace("Usted no dispone de bonos activos.", "")
                                            .replaceFirst("Datos.cu(.*)", "")
                                            .trim();
                            String string_bono = bonos.toString();
                            if (!TextUtils.isEmpty(string_bono)) {
                                binding.buttonCuentasBono.setVisibility(View.VISIBLE);
                            } else {
                                binding.buttonCuentasBono.setVisibility(View.GONE);
                            }
                            editor.putString("bonos", string_bono.toString());
                            editor.commit();
                        }

                        @Override
                        public void onReceiveUssdResponseFailed(
                                TelephonyManager telephonyManager,
                                String request,
                                int failureCode) {
                            super.onReceiveUssdResponseFailed(
                                    telephonyManager, request, failureCode);

                            Log.e(
                                    "TAG",
                                    "onReceiveUssdResponseFailed: " + "" + failureCode + request);
                        }
                    },
                    new Handler(Looper.getMainLooper()));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // saldo movil
        String saldo = sp_cuentas.getString("saldo", "0.00 CUP");
        binding.textCuentasSaldo.setText(saldo);
        // vence saldo movil
        String vence_saldo = sp_cuentas.getString("vence_saldo", "Expira: 00/00/00");
        binding.textCuentasVenceSim.setText(vence_saldo);
        // minutos
        String minutos = sp_cuentas.getString("minutos", "00:00:00");
        binding.textCuentasMinutos.setText(minutos);
        // mensajes
        String mensajes = sp_cuentas.getString("sms", "0");
        binding.textCuentasMensajes.setText(mensajes);
        // vence sms y voz
        String vence_min_sms = sp_cuentas.getString("vence_sms", "0 días");
        binding.textCuentasVenceMinSms.setText(vence_min_sms);
        // bonos en promo
        String promo = sp_cuentas.getString("bonos", null);
        if (!TextUtils.isEmpty(promo)) {
            binding.buttonCuentasBono.setVisibility(View.VISIBLE);
        } else {
            binding.buttonCuentasBono.setVisibility(View.GONE);
        }
        // datos nacionales
        String nacionales = sp_cuentas.getString("datos_nacionales", "0 MB");
        binding.textCuentasDatosNacionales.setText(nacionales);
        // paquetes
        String datos = sp_cuentas.getString("paquete", "0 MB");
        binding.textCuentasDatos.setText(datos);
        // lte
        String lte = sp_cuentas.getString("lte", "0 MB");
        binding.textCuentasDatosLte.setText(lte);
        // vence datos
        String vence_datos = sp_cuentas.getString("vence_datos", "0 días");
        binding.textCuentasVenceDatos.setText(vence_datos);
        // mensajeria y vencimiento
        String mensajeria = sp_cuentas.getString("mensajeria", "0 MB");
        String vence_mensajeria = sp_cuentas.getString("vence_mensajeria", "0 días");
        binding.textCuentasMensajeria.setText(mensajeria);
        binding.textCuentasVenceMensajeria.setText(vence_mensajeria);
        // diaria y vencimiento
        String diaria = sp_cuentas.getString("diaria", "0 MB");
        String vence_diaria = sp_cuentas.getString("vence_diaria", "0 horas");
        binding.textCuentasDiaria.setText(diaria);
        binding.textCuentasVenceDiaria.setText(vence_diaria);
        // update hora
        String time = sp_cuentas.getString("hora", "00:00");
        binding.textHoraUpdate.setText("Actualizado: " + time);
    }
}

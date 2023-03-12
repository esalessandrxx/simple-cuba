package com.nova.simple.fragment;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.GridLayoutManager;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import com.nova.simple.R;
import com.nova.simple.adapter.ItemTouchListener;
import com.nova.simple.adapter.ItemsAdapter;
import com.nova.simple.databinding.BottomSheetServiciosBinding;
import com.nova.simple.databinding.FragmentPaquetesBinding;
import com.nova.simple.model.GridItem;
import com.nova.simple.model.Items;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class EnTuMovilFragment extends Fragment {

    private FragmentPaquetesBinding binding;
    private BottomSheetServiciosBinding bSheetBinding;
    private ItemsAdapter adapter;
    private ArrayList<Items> item = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflate, ViewGroup parent, Bundle savedInstanceState) {
        binding = FragmentPaquetesBinding.inflate(inflate, parent, false);
        ((TabLayout) getActivity().findViewById(R.id.tab_layout)).setVisibility(View.GONE);
        ((CollapsingToolbarLayout) getActivity().findViewById(R.id.collapsingTbl))
                .setTitle(getString(R.string.title_entumovil));
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
        binding.recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
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
                                        pelota();
                                        break;
                                    case 1:
                                        horoscopo();
                                        break;
                                    case 2:
                                        cubadebate();
                                        break;
                                    case 3:
                                        prensa_latina();
                                        break;
                                    case 4:
                                        granma();
                                        break;
                                    case 5:
                                        rastreoDHL();
                                        break;
                                    case 6:
                                        marti();
                                        break;
                                    case 7:
                                        mlb();
                                        break;
                                    case 8:
                                        clima();
                                        break;
                                    case 9:
                                        vuelos();
                                        break;
                                    case 10:
                                        futbol();
                                        break;
                                    case 11:
                                        embajadas();
                                        break;
                                    case 12:
                                        tasa_cambio();
                                        break;
                                    case 13:
                                        apagon();
                                        break;
                                }
                                return;
                            }
                        }));
    }

    private void loadItems() {
        item.add(
                new GridItem(
                        getString(R.string.title_entumovil_pelota),
                        null,
                        R.drawable.ic_en_tu_movil_pelota));
        item.add(
                new GridItem(
                        getString(R.string.title_entumovil_horoscopo),
                        null,
                        R.drawable.ic_en_tu_movil_horoscopo));
        item.add(
                new GridItem(
                        getString(R.string.title_entumovil_cubadebate),
                        null,
                        R.drawable.ic_en_tu_movil_cubadebate));
        item.add(
                new GridItem(
                        getString(R.string.title_entumovil_prensa_latina),
                        null,
                        R.drawable.ic_en_tu_movil_prensa_latina));
        item.add(
                new GridItem(
                        getString(R.string.title_entumovil_granma),
                        null,
                        R.drawable.ic_en_tu_movil_granma));
        item.add(
                new GridItem(
                        getString(R.string.title_entumovil_dhl),
                        null,
                        R.drawable.ic_en_tu_movil_dhl));
        item.add(
                new GridItem(
                        getString(R.string.title_entumovil_frases_martianas),
                        null,
                        R.drawable.ic_en_tu_movil_marti));
        item.add(
                new GridItem(
                        getString(R.string.title_entumovil_mlb),
                        null,
                        R.drawable.ic_en_tu_movil_mlb));
        item.add(
                new GridItem(
                        getString(R.string.title_entumovil_clima),
                        null,
                        R.drawable.ic_en_tu_movil_tiempo));
        item.add(
                new GridItem(
                        getString(R.string.title_entumovil_vuelos),
                        null,
                        R.drawable.ic_en_tu_movil_vuelos));
        item.add(
                new GridItem(
                        getString(R.string.title_entumovil_futbol),
                        null,
                        R.drawable.ic_en_tu_movil_futbol));
        item.add(
                new GridItem(
                        getString(R.string.title_entumovil_embajada),
                        null,
                        R.drawable.ic_en_tu_movil_embajada));
        item.add(
                new GridItem(
                        getString(R.string.title_entumovil_cambio),
                        null,
                        R.drawable.ic_en_tu_movil_cambio));
        item.add(
                new GridItem(
                        getString(R.string.title_entumovil_apagon),
                        null,
                        R.drawable.ic_en_tu_movil_cambio));
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

    private void rastreoDHL() {
        BottomSheetDialog dialog = new BottomSheetDialog(getContext());
        bSheetBinding = BottomSheetServiciosBinding.inflate(LayoutInflater.from(getContext()));
        dialog.setContentView(bSheetBinding.getRoot());

        //
        bSheetBinding.textTitleServicio.setText("DHL");
        bSheetBinding.textPrecioService.setText("4.00 CUP");
        bSheetBinding.textMessageService.setText(
                "Consulte la situación de cualquier envío remitido por la vía DHL");
        //  bSheetBinding.editServiceNumero.setInputType(InputType.TYPE_NUMBER_VARIATION_NORMAL);
        bSheetBinding.inputServiceNumero.setHint("Código de Identificación");
        bSheetBinding.buttonService.setText("Consultar");
        bSheetBinding.buttonService.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        String codigo = bSheetBinding.editServiceNumero.getText().toString().trim();
                        SMS("8888", "DHL " + codigo);
                    }
                });
        dialog.show();
    }

    private void vuelos() {
        BottomSheetDialog dialog = new BottomSheetDialog(getContext());
        bSheetBinding = BottomSheetServiciosBinding.inflate(LayoutInflater.from(getContext()));
        dialog.setContentView(bSheetBinding.getRoot());

        //
        bSheetBinding.textTitleServicio.setText("Vuelos");
        bSheetBinding.textPrecioService.setText("4.00 CUP");
        bSheetBinding.textMessageService.setText(
                "Consultar horarios de salidas y arribos de vuelos nacionales e internacionales que operan desde y hasta las terminales aéreas de la capital del país");
        //  bSheetBinding.editServiceNumero.setInputType(InputType.TYPE_NUMBER_VARIATION_NORMAL);
        bSheetBinding.inputServiceNumero.setHint("Número de vuelo");
        bSheetBinding.buttonService.setText("Consultar");
        bSheetBinding.buttonService.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        String codigo = bSheetBinding.editServiceNumero.getText().toString().trim();
                        SMS("8888", "VUELO " + codigo);
                    }
                });
        dialog.show();
    }

    private void embajadas() {
        BottomSheetDialog dialog = new BottomSheetDialog(getContext());
        bSheetBinding = BottomSheetServiciosBinding.inflate(LayoutInflater.from(getContext()));
        dialog.setContentView(bSheetBinding.getRoot());

        //
        bSheetBinding.textTitleServicio.setText("Embajadas");
        bSheetBinding.textPrecioService.setText("4.00 CUP");
        bSheetBinding.textMessageService.setText(
                "Consultar la información relacionada con las embajadas y consulados radicados en La Habana. Para referirse a un país, no se utilizarán acentos (á, é, í, ó, ú) y las ñ se sustituirán por (n) o (nn).");
        //  bSheetBinding.editServiceNumero.setInputType(InputType.TYPE_NUMBER_VARIATION_NORMAL);
        bSheetBinding.inputServiceNumero.setHint("País");
        bSheetBinding.buttonService.setText("Consultar");
        bSheetBinding.buttonService.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        String codigo = bSheetBinding.editServiceNumero.getText().toString().trim();
                        SMS("8888", "EMBAJADA " + codigo);
                    }
                });
        dialog.show();
    }

    private void pelota() {
        CharSequence[] items = getResources().getStringArray(R.array.pelota);
        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(getActivity());
        dialog.setTitle("Pelota");
        dialog.setItems(
                items,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dia, int which) {
                        switch (which) {
                            case 0:
                                SMS("8888", "PELOTA");
                                break;
                            case 1:
                                SMS("8888", "PELOTA POS");
                                break;
                        }
                    }
                });
        dialog.setPositiveButton("Aceptar", null);
        dialog.show();
    }

    private void horoscopo() {
        CharSequence[] items = getResources().getStringArray(R.array.horozcopo);
        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(getActivity());
        dialog.setTitle("Horóscopo");
        dialog.setItems(
                items,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dia, int which) {
                        switch (which) {
                            case 0:
                                SMS("8888", "ARIES");
                                break;
                            case 1:
                                SMS("8888", "TAURO");
                                break;
                            case 2:
                                SMS("8888", "GEMINIS");
                                break;
                            case 3:
                                SMS("8888", "CANCER");
                                break;
                            case 4:
                                SMS("8888", "LEO");
                                break;
                            case 5:
                                SMS("8888", "VIRGO");
                                break;
                            case 6:
                                SMS("8888", "LIBRA");
                                break;
                            case 7:
                                SMS("8888", "SCORPIO");
                                break;
                            case 8:
                                SMS("8888", "SAGITARIO");
                                break;
                            case 9:
                                SMS("8888", "CAPRICORNIO");
                                break;
                            case 10:
                                SMS("8888", "ACUARIO");
                                break;
                            case 11:
                                SMS("8888", "PISCIS");
                                break;
                        }
                    }
                });
        dialog.setPositiveButton("Aceptar", null);
        dialog.show();
    }

    private void cubadebate() {
        CharSequence[] items = getResources().getStringArray(R.array.periodicos);
        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(getActivity());
        dialog.setTitle("Cubadebate");
        dialog.setItems(
                items,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dia, int which) {
                        switch (which) {
                            case 0:
                                SMS("8100", "CUBADEBATE");
                                break;
                            case 1:
                                SMS("8888", "CUBADEBATE BAJA");
                                break;
                        }
                    }
                });
        dialog.setPositiveButton("Aceptar", null);
        dialog.show();
    }

    private void prensa_latina() {
        CharSequence[] items = getResources().getStringArray(R.array.periodicos);
        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(getActivity());
        dialog.setTitle("Prensa Latina");
        dialog.setItems(
                items,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dia, int which) {
                        switch (which) {
                            case 0:
                                SMS("8100", "PL");
                                break;
                            case 1:
                                SMS("8888", "PL BAJA");
                                break;
                        }
                    }
                });
        dialog.setPositiveButton("Aceptar", null);
        dialog.show();
    }

    private void granma() {
        CharSequence[] items = getResources().getStringArray(R.array.periodicos);
        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(getActivity());
        dialog.setTitle("Granma");
        dialog.setItems(
                items,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dia, int which) {
                        switch (which) {
                            case 0:
                                SMS("8100", "GRANMA");
                                break;
                            case 1:
                                SMS("8888", "GRANMA BAJA");
                                break;
                        }
                    }
                });
        dialog.setPositiveButton("Aceptar", null);
        dialog.show();
    }

    private void marti() {
        CharSequence[] items = getResources().getStringArray(R.array.periodicos);
        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(getActivity());
        dialog.setTitle("José Martí");
        dialog.setItems(
                items,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dia, int which) {
                        switch (which) {
                            case 0:
                                SMS("8100", "MARTI");
                                break;
                            case 1:
                                SMS("8888", "MARTI BAJA");
                                break;
                        }
                    }
                });
        dialog.setPositiveButton("Aceptar", null);
        dialog.show();
    }

    private void mlb() {
        CharSequence[] items = getResources().getStringArray(R.array.pelota);
        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(getActivity());
        dialog.setTitle("MLB");
        dialog.setItems(
                items,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dia, int which) {
                        switch (which) {
                            case 0:
                                SMS("8888", "MLB");
                                break;
                            case 1:
                                SMS("8888", "MLB POS");
                                break;
                        }
                    }
                });
        dialog.setPositiveButton("Aceptar", null);
        dialog.show();
    }

    private void clima() {
        CharSequence[] items = getResources().getStringArray(R.array.tiempo);
        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(getActivity());
        dialog.setTitle("Tiempo");
        dialog.setItems(
                items,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dia, int which) {
                        switch (which) {
                            case 0:
                                SMS("8888", "VCL");
                                break;
                            case 1:
                                SMS("8888", "GTM");
                                break;
                            case 2:
                                SMS("8888", "HLG");
                                break;
                            case 3:
                                SMS("8888", "ART");
                                break;
                            case 4:
                                SMS("8888", "GRM");
                                break;
                            case 5:
                                SMS("8888", "LHA");
                                break;
                            case 6:
                                SMS("8888", "MAY");
                                break;
                            case 7:
                                SMS("8888", "IJU");
                                break;
                            case 8:
                                SMS("8888", "CMG");
                                break;
                            case 9:
                                SMS("8888", "CFG");
                                break;
                            case 10:
                                SMS("8888", "PRL");
                                break;
                            case 11:
                                SMS("8888", "SCU");
                                break;
                            case 12:
                                SMS("8888", "SSP");
                                break;
                            case 13:
                                SMS("8888", "LTU");
                                break;
                            case 14:
                                SMS("8888", "MTZ");
                                break;
                            case 15:
                                SMS("8888", "CAV");
                                break;
                        }
                    }
                });
        dialog.setPositiveButton("Aceptar", null);
        dialog.show();
    }

    private void futbol() {
        CharSequence[] items = getResources().getStringArray(R.array.futbol);
        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(getActivity());
        dialog.setTitle("Fútbol");
        dialog.setItems(
                items,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dia, int which) {
                        switch (which) {
                            case 0:
                                SMS("8888", "BUNDESLIGA");
                                break;
                            case 1:
                                SMS("8888", "CHAMPION");
                                break;
                            case 2:
                                SMS("8888", "REY");
                                break;
                            case 3:
                                SMS("8888", "LA LIGA");
                                break;
                            case 4:
                                SMS("8888", "LIGA1");
                                break;
                            case 5:
                                SMS("8888", "SERIE A");
                                break;
                            case 6:
                                SMS("8888", "PREMIER");
                                break;
                        }
                    }
                });
        dialog.setPositiveButton("Aceptar", null);
        dialog.show();
    }

    private void tasa_cambio() {
        CharSequence[] items = getResources().getStringArray(R.array.cambio);
        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(getActivity());
        dialog.setTitle("Tasa de Cambio");
        dialog.setItems(
                items,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dia, int which) {
                        switch (which) {
                            case 0:
                                SMS("8888", "EUR");
                                break;
                            case 1:
                                SMS("8888", "USD");
                                break;
                            case 2:
                                SMS("8888", "MXN");
                                break;
                            case 3:
                                SMS("8888", "CAD");
                                break;
                            case 4:
                                SMS("8888", "GBP");
                                break;
                            case 5:
                                SMS("8888", "JPY");
                                break;
                            case 6:
                                SMS("8888", "CHF");
                                break;
                            case 7:
                                SMS("8888", "DKK");
                                break;
                            case 8:
                                SMS("8888", "NOK");
                                break;
                            case 9:
                                SMS("8888", "SEK");
                                break;
                        }
                    }
                });
        dialog.setPositiveButton("Aceptar", null);
        dialog.show();
    }

    private void apagon() {
        CharSequence[] items = getResources().getStringArray(R.array.apagon);
        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(getActivity());
        dialog.setTitle("Afectación Habana");
        dialog.setItems(
                items,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dia, int which) {
                        switch (which) {
                            case 0:
                                SMS("8888", "APAGON B1");
                                break;
                            case 1:
                                SMS("8888", "APAGON B2");
                                break;
                            case 2:
                                SMS("8888", "APAGON B3");
                                break;
                            case 3:
                                SMS("8888", "APAGON B4");
                                break;
                        }
                    }
                });
        dialog.setPositiveButton("Aceptar", null);
        dialog.show();
    }
}

package com.nova.simple.nauta;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.nova.simple.R;
import com.nova.simple.activity.MainActivity;
import com.nova.simple.databinding.FragmentConectadoBinding;
import com.nova.simple.nauta.User;

import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.FileOutputStream;

public class ConectadoFragment extends Fragment {

    private FragmentConectadoBinding binding;
    public User my_user;
    public Intent main;
    public StringBuilder builder;

    @Override
    public View onCreateView(LayoutInflater inflate, ViewGroup parent, Bundle savedInstanceState) {
        binding = FragmentConectadoBinding.inflate(inflate, parent, false);
        ((CollapsingToolbarLayout) getActivity().findViewById(R.id.collapsingTbl))
                .setTitle(getString(R.string.title_nauta));
        ((TabLayout) getActivity().findViewById(R.id.tab_layout)).setVisibility(View.GONE);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        my_user = User.getUser();

        // TODO: view saldo
        binding.textNautaSaldo.setText(my_user.getSaldoCuenta());

        // main
        main = new Intent(getActivity(), MainActivity.class);

        // string builder
        builder = new StringBuilder();
        sendLeftTime();

        // TODO: Chronometro
        binding.chronometerTranscurrido.setOnChronometerTickListener(
                new Chronometer.OnChronometerTickListener() {
                    @Override
                    public void onChronometerTick(Chronometer cArg) {
                        long time = SystemClock.elapsedRealtime() - cArg.getBase();
                        int h = (int) (time / 3600000);
                        int m = (int) (time - h * 3600000) / 60000;
                        int s = (int) (time - h * 3600000 - m * 60000) / 1000;
                        String hh = h < 10 ? "0" + h : h + "";
                        String mm = m < 10 ? "0" + m : m + "";
                        String ss = s < 10 ? "0" + s : s + "";
                        cArg.setText(String.format("%s:%s:%s", hh, mm, ss));
                    }
                });
        binding.chronometerTranscurrido.setBase(SystemClock.elapsedRealtime());
        binding.chronometerTranscurrido.start();

        // TODO: Buttom disconect
        binding.buttomNautaDesconectar.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        sendDisconnect();
                    }
                });
    }

    public void saveUser() {
        Gson g = new Gson();
        String data = g.toJson(my_user);
        try {
            FileOutputStream fos = getActivity().openFileOutput("nauta.dat", Context.MODE_PRIVATE);
            fos.write(data.getBytes());
            fos.close();
            Toast.makeText(getActivity(), "Usuario guardado!", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Error al guardar su usuario", Toast.LENGTH_SHORT).show();
        }
    }

    public void Disconnect(View v) {
        sendDisconnect();
    }

    public void leftTime(View v) {
        sendLeftTime();
    }

    public void countDown(String time) {
        long milisecondsLeft = 0;

        try {
            milisecondsLeft =
                    Integer.parseInt(time.split(":")[0]) * 60 * 60 * 1000
                            + Integer.parseInt(time.split(":")[1]) * 60 * 1000
                            + Integer.parseInt(time.split(":")[2]) * 1000;
        } catch (Exception e) {
            e.printStackTrace();
        }
        new CountDownTimer(milisecondsLeft, 1000) {
            public void onTick(long millisUntilFinished) {
                int h = (int) (millisUntilFinished / 3600000);
                int m = (int) (millisUntilFinished - h * 3600000) / 60000;
                int s = (int) (millisUntilFinished - h * 3600000 - m * 60000) / 1000;
                String hh = h < 10 ? "0" + h : h + "";
                String mm = m < 10 ? "0" + m : m + "";
                String ss = s < 10 ? "0" + s : s + "";
                binding.textNautaConsumido.setText(String.format("%s:%s:%s", hh, mm, ss));
            }

            public void onFinish() {
                binding.textNautaConsumido.setText("00:00:00");
            }
        }.start();
    }

    public void sendLeftTime() {
        new Thread(
                        new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Document leftTimeDocument =
                                            Jsoup.connect(
                                                            "https://secure.etecsa.net:8443//EtecsaQueryServlet")
                                                    .data("username", my_user.getUsername())
                                                    .data(
                                                            "ATTRIBUTE_UUID",
                                                            my_user.getATTRIBUTE_UUID())
                                                    .data("op", "getLeftTime")
                                                    .followRedirects(true)
                                                    .post();
                                    my_user.setLeftTime(leftTimeDocument.select("body").text());

                                    builder.append(my_user.getLeftTime());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                if (getActivity() == null) return;
                                getActivity()
                                        .runOnUiThread(
                                                new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        countDown(builder.toString());
                                                        saveUser();
                                                    }
                                                });
                            }
                        })
                .start();
    }

    public void sendDisconnect() {
        new Thread(
                        new Runnable() {

                            @Override
                            public void run() {
                                Document loggin = null;
                                try {
                                    loggin =
                                            Jsoup.connect(
                                                            "https://secure.etecsa.net:8443/LogoutServlet")
                                                    .data("username", my_user.getUsername())
                                                    .data(
                                                            "ATTRIBUTE_UUID",
                                                            my_user.getATTRIBUTE_UUID())
                                                    .followRedirects(true)
                                                    .post();
                                    System.out.println(loggin.body().toString().split("'")[1]);
                                    startActivity(main);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                if (getActivity() == null) return;
                                getActivity()
                                        .runOnUiThread(
                                                new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(
                                                                        getActivity(),
                                                                        "Desconectado",
                                                                        Toast.LENGTH_SHORT)
                                                                .show();
                                                    }
                                                });
                            }
                        })
                .start();
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }
}

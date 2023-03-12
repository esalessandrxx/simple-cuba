package com.nova.simple.nauta;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.nova.simple.R;
import com.nova.simple.databinding.FragmentLoginBinding;
import com.nova.simple.nauta.ConectadoFragment;
import com.nova.simple.nauta.User;

import java.text.SimpleDateFormat;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;

import java.io.FileInputStream;
import java.util.Calendar;
import org.jsoup.nodes.Document;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    public User my_user;
    public boolean user_password_error;

    @Override
    public View onCreateView(LayoutInflater inflate, ViewGroup parent, Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflate, parent, false);
        ((TabLayout) getActivity().findViewById(R.id.tab_layout)).setVisibility(View.GONE);
        ((CollapsingToolbarLayout) getActivity().findViewById(R.id.collapsingTbl))
                .setTitle(getString(R.string.title_nauta));
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // TODO: Buttom login
        binding.buttonConectar.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sendConnect();
                    }
                });

        user_password_error = false;
        this.my_user = User.getUser();
        loadUser();
        if (my_user.getUsername() != "prp") {
            binding.autocompleteUser.setText(my_user.getUsername());
            binding.editTextPassword.setText(my_user.getPassword());
        } else {
            binding.autocompleteUser.setText("");
            binding.editTextPassword.setText("");
        }
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }

    public void loadUser() {
        String data = "";
        try {
            FileInputStream fis = getActivity().openFileInput("nauta.dat");
            int size = fis.available();
            byte[] buffer = new byte[size];
            fis.read(buffer);
            fis.close();
            data = new String(buffer);
            if (data != "") {
                Gson gson = new Gson();
                User new_user = gson.fromJson(data, User.class);
                my_user.setUsername(new_user.getUsername());
                my_user.setPassword(new_user.getPassword());
                // Toast.makeText(getActivity(), "Se ha cargado su último usuario",
                // Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            // No se cargaron usuarios
        }
    }

    private void sendConnect() {
        new Thread(
                        new Runnable() {
                            @Override
                            public void run() {
                                final StringBuilder builder = new StringBuilder();

                                try {
                                    String str = "td";
                                    String str2 = "";
                                    String format =
                                            new SimpleDateFormat("yyyyMMddHHmmssSSS")
                                                    .format(Calendar.getInstance().getTime());
                                    Response execute =
                                            Jsoup.connect("https://secure.etecsa.net:8443")
                                                    .method(Method.GET)
                                                    .execute();

                                    my_user.setCSRFHW(
                                            execute.parse()
                                                    .select("input[name=CSRFHW]")
                                                    .first()
                                                    .val());
                                    my_user.setUsername(
                                            binding.autocompleteUser.getText().toString());
                                    my_user.setPassword(
                                            binding.editTextPassword.getText().toString());

                                    Document post =
                                            Jsoup.connect(
                                                            "https://secure.etecsa.net:8443/EtecsaQueryServlet")
                                                    .cookies(execute.cookies())
                                                    .data("wlanacname", str2)
                                                    .data("wlanmac", str2)
                                                    .data("firsturl", "notFound.jsp")
                                                    .data("ssid", str2)
                                                    .data("usertype", str2)
                                                    .data(
                                                            "gotopage",
                                                            "/nauta_etecsa/LoginURL/mobile_login.jsp")
                                                    .data(
                                                            "successpage",
                                                            "/nauta_etecsa/OnlineURL/mobile_index.jsp")
                                                    .data("loggerId", format)
                                                    .data("lang", "es_ES")
                                                    .data("username", my_user.getUsername())
                                                    .data("password", my_user.getPassword())
                                                    .data("CSRFHW", my_user.getCSRFHW())
                                                    .followRedirects(true)
                                                    .post();
                                    if (!post.select("script")
                                            .last()
                                            .toString()
                                            .contains("alert(\"return null\");")) {
                                        my_user.setSaldoCuenta(
                                                post.select("table#sessioninfo > tbody > tr > td")
                                                        .get(3)
                                                        .text());
                                        my_user.setEstadoCuenta(
                                                post.select("table#sessioninfo > tbody > tr > td")
                                                        .get(1)
                                                        .text());

                                        Document loggin =
                                                Jsoup.connect(
                                                                "https://secure.etecsa.net:8443/LoginServlet")
                                                        .data("username", my_user.getUsername())
                                                        .data("password", my_user.getPassword())
                                                        .followRedirects(true)
                                                        .post();

                                        my_user.setATTRIBUTE_UUID(
                                                loggin.select("script")
                                                        .first()
                                                        .toString()
                                                        .split("ATTRIBUTE_UUID=")[1]
                                                        .split("&")[0]);

                                        System.out.println(my_user);
                                        loadFragment(null);
                                    } else {
                                        user_password_error = true;
                                    }

                                } catch (Exception e) {
                                    System.out.println(e.getMessage());
                                    builder.append("error: ").append(e.getMessage()).append("\n");
                                }
                                if (getActivity() == null) return;
                                getActivity()
                                        .runOnUiThread(
                                                new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        System.out.println(builder.toString());
                                                        if (user_password_error) {
                                                            Toast.makeText(
                                                                            getContext(),
                                                                            getString(
                                                                                    R.string
                                                                                            .user_passw_error),
                                                                            Toast.LENGTH_LONG)
                                                                    .show();
                                                            binding.autocompleteUser.setText("");
                                                            binding.editTextPassword.setText("");
                                                        }
                                                    }
                                                });
                            }
                        })
                .start();
    }

    private void loadFragment(View view) {
        Fragment fragment = new ConectadoFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}

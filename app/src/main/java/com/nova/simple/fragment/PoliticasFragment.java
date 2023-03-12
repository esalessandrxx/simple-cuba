package com.nova.simple.fragment;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.nova.simple.R;
import androidx.fragment.app.Fragment;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;
import com.nova.simple.databinding.FragmentLegalBinding;

public class PoliticasFragment extends Fragment {

    private FragmentLegalBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflate, ViewGroup parent, Bundle savedInstanceState) {
        binding = FragmentLegalBinding.inflate(inflate, parent, false);
        ((CollapsingToolbarLayout) getActivity().findViewById(R.id.collapsingTbl))
                .setTitle("Políticas de Privacidad");
        ((TabLayout) getActivity().findViewById(R.id.tab_layout)).setVisibility(View.GONE);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceStaate) {
        super.onViewCreated(view, savedInstanceStaate);

        binding.textLegal.setText(getString(R.string.politicas_de_privacidad));
        binding.textLegal.setMovementMethod(LinkMovementMethod.getInstance());
    }
}

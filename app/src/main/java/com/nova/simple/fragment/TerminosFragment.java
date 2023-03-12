package com.nova.simple.fragment;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;
import com.nova.simple.R;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.nova.simple.databinding.FragmentLegalBinding;

public class TerminosFragment extends Fragment {

    private FragmentLegalBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflate, ViewGroup parent, Bundle savedInstanceState) {
        binding = FragmentLegalBinding.inflate(inflate, parent, false);
        ((CollapsingToolbarLayout) getActivity().findViewById(R.id.collapsingTbl))
                .setTitle("Términos de Uso");
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

        binding.textLegal.setText(getString(R.string.terminos_de_uso));
        if (binding.textLegal != null) {
            binding.textLegal.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }
}

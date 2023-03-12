package com.nova.simple.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayout.Tab;
import com.google.android.material.tabs.TabLayoutMediator;
import com.nova.simple.R;
import com.nova.simple.adapter.TabAdapter;
import com.nova.simple.databinding.FragmentComprasBinding;
import com.nova.simple.fragment.AmigoFragment;
import com.nova.simple.fragment.PaquetesFragment;
import com.nova.simple.fragment.PlanesFragment;

public class ComprasFragment extends Fragment {

    private FragmentComprasBinding binding;
    private TabLayout tab;
    TabAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        binding = FragmentComprasBinding.inflate(inflater, parent, false);
        tab = (TabLayout) getActivity().findViewById(R.id.tab_layout);
        tab.setVisibility(View.VISIBLE);
        ((CollapsingToolbarLayout) getActivity().findViewById(R.id.collapsingTbl))
                .setTitle(getString(R.string.title_compra));
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // TODO: cargar el fragment en el viewpage2
        adapter = new TabAdapter(getChildFragmentManager(), getLifecycle());
        adapter.addFragment(new PlanesFragment());
        adapter.addFragment(new PaquetesFragment());
        adapter.addFragment(new AmigoFragment());

        // orientation viewpager2
        binding.viewPage.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        binding.viewPage.setAdapter(adapter);

        TabLayoutMediator tabMediator =
                new TabLayoutMediator(
                        tab,
                        binding.viewPage,
                        new TabLayoutMediator.TabConfigurationStrategy() {
                            @Override
                            public void onConfigureTab(TabLayout.Tab arg0, int position) {
                                switch (position) {
                                    case 0:
                                        arg0.setText(getString(R.string.title_tab_planes));
                                        break;
                                    case 1:
                                        arg0.setText(getString(R.string.title_tab_paquete));
                                        break;
                                    case 2:
                                        arg0.setText(getString(R.string.title_tab_amigo));
                                        break;
                                }
                            }
                        });
        tabMediator.attach();
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }
}

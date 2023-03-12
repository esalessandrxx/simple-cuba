package com.nova.simple.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;

import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.elevation.SurfaceColors;
import com.nova.simple.BuildConfig;
import com.nova.simple.R;
import com.nova.simple.activity.MainActivity;
import com.nova.simple.adapter.AboutAdapter;
import com.nova.simple.adapter.ItemTouchListener;
import com.nova.simple.databinding.ActivityAboutBinding;
import com.nova.simple.databinding.ContentLayoutAboutActivityBinding;
import com.nova.simple.model.About;

import java.util.ArrayList;

public class AboutActivity extends AppCompatActivity {

    private ActivityAboutBinding binding;
    private ContentLayoutAboutActivityBinding about;
    private AboutAdapter adapter;
    private ArrayList<About> item = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAboutBinding.inflate(LayoutInflater.from(this));
        about = binding.layoutAbout;
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // TODO: NavigationBar Color
        getWindow().setNavigationBarColor(SurfaceColors.SURFACE_0.getColor(this));

        // TODO: version app
        String name = BuildConfig.VERSION_NAME;
        int vCode = BuildConfig.VERSION_CODE;
        String code = String.valueOf(vCode);
        about.textAboutVersion.setText("v" + name + "(" + code + ")");

        // TODO: RecyclerView
        about.recyclerViewAbout.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AboutAdapter(this, item);
        about.recyclerViewAbout.setAdapter(adapter);

        // list
        item.add(new About(getString(R.string.title_list1), getString(R.string.subtitle_list1)));
        item.add(new About(getString(R.string.title_list2), getString(R.string.subtitle_list2)));
        item.add(new About(getString(R.string.title_list3), getString(R.string.subtitle_list3)));
        item.add(new About(getString(R.string.title_list4), getString(R.string.subtitle_list4)));
        item.add(new About(getString(R.string.title_list5), getString(R.string.subtitle_list5)));

        // onclick
        about.recyclerViewAbout.addOnItemTouchListener(
                new ItemTouchListener(
                        this,
                        about.recyclerViewAbout,
                        new ItemTouchListener.ClickListener() {
                            @Override
                            public void onClick(View view, int position) {
                                switch (position) {
                                    case 0:
                                        startActivity(
                                                new Intent(
                                                        Intent.ACTION_VIEW,
                                                        Uri.parse(getString(R.string.link_list1))));
                                        break;
                                    case 1:
                                        startActivity(
                                                new Intent(
                                                        Intent.ACTION_VIEW,
                                                        Uri.parse(getString(R.string.link_list2))));
                                        break;
                                    case 2:
                                        startActivity(
                                                new Intent(
                                                        Intent.ACTION_VIEW,
                                                        Uri.parse(getString(R.string.link_list3))));
                                        break;
                                    case 3:
                                        startActivity(
                                                new Intent(
                                                        Intent.ACTION_VIEW,
                                                        Uri.parse(getString(R.string.link_list4))));
                                        break;
                                }
                                return;
                            }
                        }));
        // TODO: Link a GitHub
        about.cardGithub.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        startActivity(
                                    new Intent(
                                            Intent.ACTION_VIEW,
                                            Uri.parse(getString(R.string.about_link_github))));
                    }
                });

        // link a facebook
        about.cardFacebook.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        startActivity(
                                new Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse(getString(R.string.about_link_facebook))));
                    }
                });

        // link a twitter
        about.cardTwitter.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        startActivity(
                                new Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse(getString(R.string.about_link_twitter))));
                    }
                });

        // link a telegram
        about.cardTelegram.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        startActivity(
                                new Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse(getString(R.string.about_link_telegram))));
                    }
                });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        binding = null;
        super.onDestroy();
    }
}

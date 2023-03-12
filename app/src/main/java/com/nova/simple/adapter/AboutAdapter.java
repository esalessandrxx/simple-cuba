package com.nova.simple.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import com.nova.simple.adapter.AboutAdapter;
import com.nova.simple.databinding.LayoutAboutAdapterBinding;
import com.nova.simple.model.About;
import java.util.ArrayList;

public class AboutAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<About> item = new ArrayList<>();

    public AboutAdapter(Context context, ArrayList<About> item) {
        this.context = context;
        this.item = item;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutAboutAdapterBinding itemBinding =
                LayoutAboutAdapterBinding.inflate(
                        LayoutInflater.from(parent.getContext()), parent, false);
        return new AboutView(itemBinding);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        About about = item.get(position);
        AboutView view = (AboutView) holder;
        view.binding.textName.setText(about.getName());
        view.binding.textDescription.setText(about.getDescription());
    }

    @Override
    public int getItemCount() {
        return item.size();
    }

    class AboutView extends RecyclerView.ViewHolder {

        LayoutAboutAdapterBinding binding;

        public AboutView(LayoutAboutAdapterBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            
        }
    }
}

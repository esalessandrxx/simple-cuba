package com.nova.simple.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import com.nova.simple.adapter.ItemsAdapter;
import com.nova.simple.databinding.LayoutGridItemBinding;
import com.nova.simple.databinding.LayoutHeaderItemBinding;
import com.nova.simple.model.GridItem;
import com.nova.simple.model.HeaderItem;
import com.nova.simple.model.Items;
import java.util.ArrayList;
import java.util.List;

public class ItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<Items> item;

    public ItemsAdapter(Context context, ArrayList<Items> item) {
        this.context = context;
        this.item = item;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewtype) {
        if (viewtype == GridItem.VIEW_GRID) {
            LayoutGridItemBinding itemBinding =
                    LayoutGridItemBinding.inflate(
                            LayoutInflater.from(parent.getContext()), parent, false);
            return new ViewGrid(itemBinding);

        } else if (viewtype == HeaderItem.VIEW_HEADER) {
            LayoutHeaderItemBinding itemBinding =
                    LayoutHeaderItemBinding.inflate(
                            LayoutInflater.from(parent.getContext()), parent, false);
            return new ViewHeader(itemBinding);
        }
        throw new RuntimeException("Error ocurred");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewGrid) {
            GridItem grid = (GridItem) item.get(position);
            ViewGrid vGrid = (ViewGrid) holder;
            vGrid.binding.textTitle.setText(grid.getTitle());
            vGrid.binding.textSubtitle.setText(grid.getSubtitle());
            vGrid.binding.imageIcon.setImageResource(grid.getIcon());
        } else if (holder instanceof ViewHeader) {
            HeaderItem header = (HeaderItem) item.get(position);
            ViewHeader vHeader = (ViewHeader) holder;
            vHeader.binding.textHeader.setText(header.getTitle());
        }
    }

    @Override
    public int getItemCount() {
        return item.size();
    }

    @Override
    public int getItemViewType(int position) {
        return item.get(position).getViewType();
    }

    public static class ViewGrid extends RecyclerView.ViewHolder {

        private LayoutGridItemBinding binding;

        public ViewGrid(LayoutGridItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    class ViewHeader extends RecyclerView.ViewHolder {

        LayoutHeaderItemBinding binding;

        public ViewHeader(LayoutHeaderItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

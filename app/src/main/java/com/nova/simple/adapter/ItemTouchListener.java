package com.nova.simple.adapter;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import com.nova.simple.adapter.ItemTouchListener;

public class ItemTouchListener implements RecyclerView.OnItemTouchListener {

    private GestureDetector gesture;
    private ClickListener click;

    public ItemTouchListener(
            Context context, final RecyclerView recycler, final ClickListener click) {
        this.click = click;
        gesture =
                new GestureDetector(
                        context,
                        new GestureDetector.SimpleOnGestureListener() {
                            @Override
                            public boolean onSingleTapUp(MotionEvent arg0) {

                                View child = recycler.findChildViewUnder(arg0.getX(), arg0.getX());
                                if (child != null && click != null) {}

                                return true;
                            }
                        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

        View child = rv.findChildViewUnder(e.getX(), e.getY());

        if (child != null && click != null && gesture.onTouchEvent(e)) {

            click.onClick(child, rv.getChildAdapterPosition(child));
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {}

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {}

    public interface ClickListener {
        void onClick(View view, int position);

        //  void onLongClick(View view, int position);
    }
}

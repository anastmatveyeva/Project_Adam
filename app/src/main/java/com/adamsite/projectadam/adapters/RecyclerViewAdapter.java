package com.adamsite.projectadam.adapters;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.adamsite.projectadam.R;
import com.adamsite.projectadam.model.VKAudio;
import com.danil.recyclerbindableadapter.library.FilterBindableAdapter;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends FilterBindableAdapter<VKAudio, RecyclerViewHolder> {

    private RecyclerViewHolder.ActionListener actionListener;

    @Override
    protected String itemToString(VKAudio audio) {
        return audio.getAudioTitle() + " " + audio.getAudioArtist();
    }

    @Override
    protected int layoutId(int type) {
        return R.layout.recyclerview_item;
    }

    @Override
    protected void onBindItemViewHolder(RecyclerViewHolder viewHolder, final int position, int type) {
        viewHolder.bindView(getItem(position), position, actionListener);
    }

    @Override
    protected RecyclerViewHolder viewHolder(View view, int type) {
        return new RecyclerViewHolder(view);
    }

    public void setActionListener(RecyclerViewHolder.ActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public void onSaveInstanceState(Bundle outState) {
        List<VKAudio> myFilteredAudioList = new ArrayList<>();
        for(int i=0; i<getRealItemCount(); i++) {
            myFilteredAudioList.add(getItem(i));
        }
        List<VKAudio> myAudioList = getItems();
        outState.putParcelableArrayList("myFilteredAudioList", (ArrayList<VKAudio>) myFilteredAudioList);
        outState.putParcelableArrayList("myAudioList", (ArrayList<VKAudio>) myAudioList);
    }

    public static class DividerItemDecoration extends RecyclerView.ItemDecoration {

        private static final int[] ATTRS = new int[]{android.R.attr.listDivider};

        private Drawable mDivider;

        public DividerItemDecoration(Context context) {
            final TypedArray styledAttributes = context.obtainStyledAttributes(ATTRS);
            mDivider = styledAttributes.getDrawable(0);
            styledAttributes.recycle();
        }

        public DividerItemDecoration(Context context, int resId) {
            mDivider = ContextCompat.getDrawable(context, resId);
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();

            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + mDivider.getIntrinsicHeight();

                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }
}
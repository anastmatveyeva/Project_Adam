package com.adamsite.projectadam.adapters;

import android.os.Bundle;
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
        List<VKAudio> myAudioList = new ArrayList<>();
        for(int i=0; i<getRealItemCount(); i++) {
            myAudioList.add(getItem(i));
        }
        //List<VKAudio> myAudioList = getItems();
        outState.putParcelableArrayList("savedList", (ArrayList<VKAudio>) myAudioList);
    }
}
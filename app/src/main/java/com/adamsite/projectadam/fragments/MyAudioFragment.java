package com.adamsite.projectadam.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;
import android.widget.Toast;

import com.adamsite.projectadam.Const;
import com.adamsite.projectadam.R;
import com.adamsite.projectadam.adapters.RecyclerViewAdapter;
import com.adamsite.projectadam.adapters.RecyclerViewHolder;
import com.adamsite.projectadam.model.VKAudio;
import com.adamsite.projectadam.service.AudioService;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiAudio;
import com.vk.sdk.api.model.VkAudioArray;

import java.util.ArrayList;
import java.util.List;

public class MyAudioFragment extends android.support.v4.app.Fragment implements
        RecyclerViewHolder.ActionListener, Filter.FilterListener {

    private RecyclerView recyclerView;
    private TextView tvEmptyView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerViewAdapter recyclerViewAdapter;

    public interface onShowMyAudio {

        void showMyAudioFragment();
    }

    public MyAudioFragment() {
        super();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        recyclerViewAdapter.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_audio, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initRecyclerView(view);
        initSwipeRefreshLayout(view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new RecyclerViewAdapter.DividerItemDecoration(getActivity()));
        recyclerView.setHasFixedSize(true);

        recyclerViewAdapter = new RecyclerViewAdapter();
        recyclerViewAdapter.setActionListener(this);
        recyclerView.setAdapter(recyclerViewAdapter);

        if (savedInstanceState != null) {
            List<VKAudio> myAudioList = savedInstanceState.getParcelableArrayList("myFilteredAudioList");
            recyclerViewAdapter.clear();
            recyclerViewAdapter.addAll(myAudioList);
            Toast.makeText(getContext(), "loaded from bundle " + recyclerViewAdapter.getRealItemCount(), Toast.LENGTH_SHORT).show();
        } else {
            audioGet();
        }
    }

    private void initRecyclerView(View rootView) {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);
        tvEmptyView = (TextView) rootView.findViewById(R.id.tv_recycler_view_emptyview);
    }

    private void initSwipeRefreshLayout(View rootView) {
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                audioGet();
            }
        });
    }

    public void audioGet() {
        VKRequest request = VKApi.audio().get();
        request.executeWithListener(rlMyAudioShow);
    }

    public void audioSearch(String query) {
        VKRequest request = VKApi.audio().search(VKParameters.from(
                Const.Q, query,
                Const.AUTO_COMPLETE, 1,
                Const.SORT, 2,
                Const.OFFSET, 0,
                Const.COUNT, 200
        ));
        request.executeWithListener(rlMyAudioShow);
    }

    public void myAudioSearch(String query) {
        recyclerViewAdapter.getFilter().filter(query, MyAudioFragment.this);
    }

    @Override
    public void onFilterComplete(int count) {
        checkIfEmpty(count);
    }

    public void checkIfEmpty() {
        if (recyclerViewAdapter.getRealItemCount() > 0) {
            tvEmptyView.setVisibility(View.GONE);
        } else {
            tvEmptyView.setVisibility(View.VISIBLE);
        }
    }

    public void checkIfEmpty(int count) {
        if (count > 0) {
            tvEmptyView.setVisibility(View.GONE);
        } else {
            tvEmptyView.setVisibility(View.VISIBLE);
        }
    }

    VKRequest.VKRequestListener rlMyAudioShow = new VKRequest.VKRequestListener() {
        @Override
        public void onComplete(VKResponse response) {
            List<VKAudio> myAudioList = new ArrayList<>();

            VkAudioArray audioArray = (VkAudioArray) response.parsedModel;
            for (VKApiAudio audio : audioArray) {
                myAudioList.add(new VKAudio(audio.artist, audio.title, audio.getId(), audio.url, audio.duration));
            }

            recyclerViewAdapter.clear();
            recyclerViewAdapter.addAll(myAudioList);
            swipeRefreshLayout.setRefreshing(false);
            checkIfEmpty();
            Toast.makeText(getContext(), "loaded from response " + recyclerViewAdapter.getRealItemCount(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProgress(VKRequest.VKProgressType progressType, long bytesLoaded, long bytesTotal) {
            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        public void onError(VKError error) {
            Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
            Log.e(Const.LOG_TAG_APP, error.toString());
            swipeRefreshLayout.setRefreshing(false);
            checkIfEmpty();
        }
    };

    @Override
    public void onClick(int position) {
//        TODO:move setAudioStatus() to service
//        recyclerViewAdapter.getItem(position).setAudioStatus(VKAudio.Status.PLAYING);
//
//        ArrayList<VKAudio> currentDisplayedTracks = new ArrayList<>();
//        for(int i=0; i< recyclerViewAdapter.getRealItemCount(); i++) {
//            if (i != position) {
//                recyclerViewAdapter.getItem(i).setAudioStatus(VKAudio.Status.OFF);
//            }
//            currentDisplayedTracks.add(recyclerViewAdapter.getItem(i));
//        }
//        recyclerViewAdapter.notifyDataSetChanged();


        ArrayList<VKAudio> currentDisplayedTracks = new ArrayList<>();
        for(int i=0; i< recyclerViewAdapter.getRealItemCount(); i++) {
            currentDisplayedTracks.add(recyclerViewAdapter.getItem(i));
        }

        Intent intent = new Intent(getActivity().getApplicationContext(), AudioService.class);
        intent.setAction(AudioService.ACTION_PLAY);
//        intent.putParcelableArrayListExtra("tracklist", (ArrayList<VKAudio>) recyclerViewAdapter.getItems());
        intent.putParcelableArrayListExtra("tracklist", currentDisplayedTracks);
        intent.putExtra("position", position);
        intent.putExtra("id", currentDisplayedTracks.get(position).getAudioID());
        getActivity().getApplicationContext().startService(intent);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_myaudio, menu);

        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                audioSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                myAudioSearch(newText);
                return true;
            }
        });
    }
}
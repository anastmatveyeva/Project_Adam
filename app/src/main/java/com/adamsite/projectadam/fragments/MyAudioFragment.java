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
import android.widget.TextView;
import android.widget.Toast;

import com.adamsite.projectadam.Const;
import com.adamsite.projectadam.R;
import com.adamsite.projectadam.adapters.RecyclerViewHolder;
import com.adamsite.projectadam.adapters.RecyclerViewAdapter;
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

public class MyAudioFragment extends android.support.v4.app.Fragment implements RecyclerViewHolder.ActionListener{

    private RecyclerView recyclerView;
    private TextView tvEmptyView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerViewAdapter filterExampleAdapter;

    public interface onShowMyAudio {

        void showMyAudioFragment();
    }
    public MyAudioFragment() {
        super();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        filterExampleAdapter.onSaveInstanceState(outState);
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

        filterExampleAdapter = new RecyclerViewAdapter();
        filterExampleAdapter.setActionListener(this);
        recyclerView.setAdapter(filterExampleAdapter);

        if (savedInstanceState != null) {
            Toast.makeText(getContext(), "loaded from bundle", Toast.LENGTH_SHORT).show();
            List<VKAudio> myAudioList = savedInstanceState.getParcelableArrayList("savedList");
            filterExampleAdapter.clear();
            filterExampleAdapter.notifyDataSetChanged();
            filterExampleAdapter.addAll(myAudioList);
        } else {
            Toast.makeText(getContext(), "loaded from response", Toast.LENGTH_SHORT).show();
            myAudioShow();
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
                myAudioShow();
            }
        });
    }

    public void myAudioShow() {
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
        filterExampleAdapter.getFilter().filter(query);
    }

    public void checkIfEmpty() {
        if (filterExampleAdapter.getRealItemCount() == 0) {
            tvEmptyView.setVisibility(View.VISIBLE);
        } else {
            tvEmptyView.setVisibility(View.GONE);
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

            filterExampleAdapter.clear();
            filterExampleAdapter.notifyDataSetChanged();
            filterExampleAdapter.addAll(myAudioList);
            swipeRefreshLayout.setRefreshing(false);
            checkIfEmpty();
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
        Intent intent = new Intent(getActivity().getApplicationContext(), AudioService.class);
        intent.setAction(AudioService.ACTION_PLAY);
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
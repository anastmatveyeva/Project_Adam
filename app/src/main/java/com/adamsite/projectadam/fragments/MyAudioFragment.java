package com.adamsite.projectadam.fragments;


import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.adamsite.projectadam.Const;
import com.adamsite.projectadam.R;
import com.adamsite.projectadam.adapter.RecyclerViewAdapter;
import com.adamsite.projectadam.interfaces.FragmentInterface;
import com.adamsite.projectadam.model.VKAudio;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiAudio;
import com.vk.sdk.api.model.VkAudioArray;

import java.util.ArrayList;
import java.util.List;

public class MyAudioFragment extends android.support.v4.app.Fragment implements FragmentInterface {

    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    private List<VKAudio> myAudioList = new ArrayList<>();
    private List<VKAudio> searchAudioList = new ArrayList<>();

    public interface onShowMyAudio {
        void showMyAudioFragment();
        void myAudioSearch(String query);
        void audioSearch(String query);
    }

    public MyAudioFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_audio, container, false);

        toolbarSetTitle();
        searchViewSetVisibility();
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        initSwipeRefreshLayout(rootView);

        return rootView;
    }

    @Override
    public void toolbarSetTitle() {
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.action_my_audio);
    }

    @Override
    public void searchViewSetVisibility() {
        SearchView searchView = (SearchView) getActivity().findViewById(R.id.search_view);
        searchView.setVisibility(View.VISIBLE);
    }

    private void initSwipeRefreshLayout(View rootView) {
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                myAudioShow();
            }
        });
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new RecyclerViewAdapter.RecyclerViewItemDecoration(getActivity()));

        myAudioShow();
        recyclerAdapter = new RecyclerViewAdapter(getActivity(), searchAudioList);
        recyclerView.setAdapter(recyclerAdapter);
    }

    public void myAudioShow() {
        VKRequest request = VKApi.audio().get();
        request.executeWithListener(audioShowRL);
    }

    public void myAudioSearch(String query) {
        query = query.toLowerCase();
        final List<VKAudio> filteredAudioList = new ArrayList<>();

        for (VKAudio audio : myAudioList) {
            if (audio.getAudioArtist().toLowerCase().contains(query) ||
                    audio.getAudioTitle().toLowerCase().contains(query)) {
                filteredAudioList.add(audio);
            }
        }
        recyclerAdapter.animateTo(filteredAudioList);
        recyclerView.scrollToPosition(0);
    }

    public void audioSearch(String query) {
        VKRequest request = VKApi.audio().search(VKParameters.from(
                Const.Q, query,
                Const.AUTO_COMPLETE, 1,
                Const.SORT, 2,
                Const.OFFSET, 0,
                Const.COUNT, 50
        ));
        request.executeWithListener(audioShowRL);
    }

    VKRequest.VKRequestListener audioShowRL = new VKRequest.VKRequestListener() {
        @Override
        public void onComplete(VKResponse response) {
            myAudioList.clear();
            int size = searchAudioList.size();

            if (size > 0) {
                for (int i=0; i<size; i++) {
                    searchAudioList.remove(0);
                    recyclerAdapter.notifyItemRemoved(0);
                }
            }

            VkAudioArray audioArray = (VkAudioArray) response.parsedModel;
            for (VKApiAudio audio : audioArray) {
                myAudioList.add(new VKAudio(audio.artist, audio.title, audio.getId(), audio.url));
                searchAudioList.add(new VKAudio(audio.artist, audio.title, audio.getId(), audio.url));
                recyclerAdapter.notifyItemInserted(audioArray.indexOf(audio));
            }
            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        public void onProgress(VKRequest.VKProgressType progressType, long bytesLoaded, long bytesTotal) {
            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        public void onError(VKError error) {
            Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
            Log.e(Const.LOG_TAG, error.toString());
            swipeRefreshLayout.setRefreshing(false);
        }
    };
}

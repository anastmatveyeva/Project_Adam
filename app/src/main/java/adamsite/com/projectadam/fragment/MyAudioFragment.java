package adamsite.com.projectadam.fragment;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiAudio;
import com.vk.sdk.api.model.VkAudioArray;

import java.util.ArrayList;
import java.util.List;

import adamsite.com.projectadam.Const;
import adamsite.com.projectadam.R;
import adamsite.com.projectadam.VKAudio;
import adamsite.com.projectadam.adapter.RecyclerViewAdapter;
import adamsite.com.projectadam.interfaces.FragmentInterface;

public class MyAudioFragment extends android.support.v4.app.Fragment implements FragmentInterface {

    private static RecyclerViewAdapter recyclerAdapter;

    public static List<VKAudio> audioList = new ArrayList<>();

    public interface onShowMyAudio {
        void showMyAudioFragment();
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
        initRecyclerView(rootView);

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
        searchView.onActionViewCollapsed();
        searchView.setVisibility(View.VISIBLE);
    }

    private void initRecyclerView(View rootView) {
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        recyclerAdapter = new RecyclerViewAdapter(getActivity(), audioList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new RecyclerViewAdapter.RecyclerViewItemDecoration(getActivity()));
        recyclerView.setAdapter(recyclerAdapter);
    }

    public void audioSearch(String query) {
        VKRequest request = VKApi.audio().search(VKParameters.from(
                Const.Q, query,
                Const.AUTO_COMPLETE, 1,
                Const.SORT, 2,
                Const.OFFSET, 0,
                Const.COUNT, 30
        ));
        request.executeWithListener(audioSearchRL);
    }

    VKRequest.VKRequestListener audioSearchRL = new VKRequest.VKRequestListener() {
        @Override
        public void onComplete(VKResponse response) {
            //super.onComplete(response);
            VkAudioArray audioArray = (VkAudioArray) response.parsedModel;
            audioList.clear();

            for (VKApiAudio audio : audioArray) {
                recyclerAdapter.notifyItemRemoved(audioArray.indexOf(audio));
                audioList.add(new VKAudio(audio.artist, audio.title, audio.getId()));
                recyclerAdapter.notifyItemInserted(audioArray.indexOf(audio));
            }
        }

        @Override
        public void onError(VKError error) {
            //super.onError(error);
            Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
        }
    };
}

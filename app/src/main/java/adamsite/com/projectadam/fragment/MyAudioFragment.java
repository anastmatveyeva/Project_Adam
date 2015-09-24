package adamsite.com.projectadam.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import adamsite.com.projectadam.Const;
import adamsite.com.projectadam.R;

public class MyAudioFragment extends android.support.v4.app.Fragment {

    private TextView tv;

    public interface onSearch {
        void audioSearch(String query);
    }

    public MyAudioFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_audio, container, false);
        tv = (TextView) rootView.findViewById(R.id.tv);
        return rootView;
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
            tv.setText(response.json.toString());
        }

        @Override
        public void onError(VKError error) {
            //super.onError(error);
            Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
        }
    };
}

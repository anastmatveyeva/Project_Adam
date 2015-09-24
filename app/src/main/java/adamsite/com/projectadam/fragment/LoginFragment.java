package adamsite.com.projectadam.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vk.sdk.VKSdk;

import adamsite.com.projectadam.Const;
import adamsite.com.projectadam.R;

public class LoginFragment extends android.support.v4.app.Fragment {

    public LoginFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        v.findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VKSdk.login(getActivity(), Const.SCOPE);
            }
        });
        return v;
    }
}

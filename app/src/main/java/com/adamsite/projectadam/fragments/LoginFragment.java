package com.adamsite.projectadam.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adamsite.projectadam.Const;
import com.adamsite.projectadam.activity.MainActivity;
import com.adamsite.projectadam.R;
import com.vk.sdk.VKSdk;

public class LoginFragment extends android.support.v4.app.Fragment {

    public interface onShowLogin {
        void showLoginFragment();
    }

    public LoginFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        rootView.findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VKSdk.login(getActivity(), Const.SCOPE);
                ((MainActivity) getActivity()).navViewItemVisibility(true);
            }
        });
        return rootView;
    }
}

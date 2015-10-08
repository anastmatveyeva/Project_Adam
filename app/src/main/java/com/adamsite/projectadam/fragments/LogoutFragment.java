package com.adamsite.projectadam.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adamsite.projectadam.MainActivity;
import com.adamsite.projectadam.R;
import com.vk.sdk.VKSdk;

public class LogoutFragment extends android.support.v4.app.Fragment {

    public interface onShowLogout {
        void showLogoutFragment();
    }

    public LogoutFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_logout, container, false);
        rootView.findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VKSdk.logout();
                if (!VKSdk.isLoggedIn()) {
                    ((MainActivity) getActivity()).showLoginFragment();
                    ((MainActivity) getActivity()).navViewItemVisibility(false);
                }
            }
        });
        return rootView;
    }
}

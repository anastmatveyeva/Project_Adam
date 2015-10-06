package com.adamsite.projectadam.fragments;


import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adamsite.projectadam.MainActivity;
import com.adamsite.projectadam.R;
import com.adamsite.projectadam.interfaces.IFragment;
import com.vk.sdk.VKSdk;

public class LogoutFragment extends android.support.v4.app.Fragment implements IFragment {

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

        searchViewSetVisibility();

        return rootView;
    }

    @Override
    public void searchViewSetVisibility() {
        SearchView searchView = (SearchView) getActivity().findViewById(R.id.search_view);
        searchView.onActionViewCollapsed();
        searchView.setVisibility(View.GONE);
    }
}

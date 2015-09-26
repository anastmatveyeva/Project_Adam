package com.adamsite.projectadam.fragments;


import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vk.sdk.VKSdk;

import com.adamsite.projectadam.Const;
import com.adamsite.projectadam.MainActivity;
import com.adamsite.projectadam.R;
import com.adamsite.projectadam.interfaces.FragmentInterface;

public class LoginFragment extends android.support.v4.app.Fragment implements FragmentInterface {

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

        toolbarSetTitle();
        searchViewSetVisibility();

        return rootView;
    }

    @Override
    public void toolbarSetTitle() {
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.action_login);
    }

    @Override
    public void searchViewSetVisibility() {
        SearchView searchView = (SearchView) getActivity().findViewById(R.id.search_view);
        searchView.onActionViewCollapsed();
        searchView.setVisibility(View.INVISIBLE);
    }
}

package adamsite.com.projectadam.fragments;


import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vk.sdk.VKSdk;

import adamsite.com.projectadam.MainActivity;
import adamsite.com.projectadam.R;
import adamsite.com.projectadam.interfaces.FragmentInterface;

public class LogoutFragment extends android.support.v4.app.Fragment implements FragmentInterface {

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

        toolbarSetTitle();
        searchViewSetVisibility();

        return rootView;
    }

    @Override
    public void toolbarSetTitle() {
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.action_logout);
    }

    @Override
    public void searchViewSetVisibility() {
        SearchView searchView = (SearchView) getActivity().findViewById(R.id.search_view);
        searchView.onActionViewCollapsed();
        searchView.setVisibility(View.INVISIBLE);
    }
}

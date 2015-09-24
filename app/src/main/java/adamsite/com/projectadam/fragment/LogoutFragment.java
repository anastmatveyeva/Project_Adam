package adamsite.com.projectadam.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vk.sdk.VKSdk;

import adamsite.com.projectadam.MainActivity;
import adamsite.com.projectadam.R;

public class LogoutFragment extends android.support.v4.app.Fragment {

    public interface onShowLogout {
        void showLogoutFragment();
    }

    public LogoutFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_logout, container, false);
        v.findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VKSdk.logout();
                if (!VKSdk.isLoggedIn())
                    ((MainActivity) getActivity()).showLoginFragment();
            }
        });
        return v;
    }
}

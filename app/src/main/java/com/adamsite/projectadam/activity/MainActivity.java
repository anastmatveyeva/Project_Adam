package com.adamsite.projectadam.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.adamsite.projectadam.Const;
import com.adamsite.projectadam.R;
import com.adamsite.projectadam.fragments.LoginFragment;
import com.adamsite.projectadam.fragments.LogoutFragment;
import com.adamsite.projectadam.fragments.MyAudioFragment;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

public class MainActivity extends AppCompatActivity implements MyAudioFragment.onShowMyAudio, LoginFragment.onShowLogin, LogoutFragment.onShowLogout {

    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private MyAudioFragment myAudioFragment;
    private LoginFragment loginFragment;
    private LogoutFragment logoutFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            myAudioFragment = (MyAudioFragment) getSupportFragmentManager().findFragmentByTag(Const.MY_AUDIO_FRAGMENT);
            loginFragment = (LoginFragment) getSupportFragmentManager().findFragmentByTag(Const.LOGIN_FRAGMENT);
            logoutFragment = (LogoutFragment) getSupportFragmentManager().findFragmentByTag(Const.LOGOUT_FRAGMENT);
        }

        initToolbar();
        initNavigationView();

        wakeUpSession();
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void initNavigationView() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.setDrawerListener(drawerToggle);

        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                selectDrawerItem(menuItem);
                return true;
            }
        });
    }

    private void selectDrawerItem(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_my_audio:
                showMyAudioFragment();
                break;
            case R.id.action_settings:
                break;
            case R.id.action_vk_login:
                showLoginFragment();
                break;
            case R.id.action_vk_logout:
                showLogoutFragment();
                break;
        }
        menuItem.setChecked(true);
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    private void wakeUpSession() {
        VKSdk.wakeUpSession(this, new VKCallback<VKSdk.LoginState>() {
            @Override
            public void onResult(VKSdk.LoginState res) {
                switch (res) {
                    case LoggedOut:
                        showLoginFragment();
                        navViewItemVisibility(false);
                        break;
                    case LoggedIn:
                        showMyAudioFragment();
                        navViewItemVisibility(true);
                        break;
                    case Pending:
                        break;
                    case Unknown:
                        break;
                }
            }

            @Override
            public void onError(VKError error) {
                Log.e(Const.LOG_TAG_APP, error.toString());
            }
        });
    }

    public void navViewItemVisibility(boolean isLoggedIn) {
        if (isLoggedIn) {
            navigationView.getMenu().findItem(R.id.action_vk_login).setVisible(false);
            navigationView.getMenu().findItem(R.id.action_vk_logout).setVisible(true);
        } else {
            navigationView.getMenu().findItem(R.id.action_vk_login).setVisible(true);
            navigationView.getMenu().findItem(R.id.action_vk_logout).setVisible(false);
        }
    }

    public void toolbarSetTitle(String title) {
        toolbar.setTitle(title);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        VKCallback<VKAccessToken> callback = new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                // User passed Authorization
                //showMyAudioFragment();
            }

            @Override
            public void onError(VKError error) {
                // User didn't pass Authorization
            }
        };

        if (!VKSdk.onActivityResult(requestCode, resultCode, data, callback)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void showMyAudioFragment() {
        toolbarSetTitle(getResources().getString(R.string.action_my_audio));

        if (myAudioFragment == null) {
            myAudioFragment = new MyAudioFragment();
        }
        if (!myAudioFragment.isInLayout()) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, myAudioFragment, Const.MY_AUDIO_FRAGMENT)
                    .commitAllowingStateLoss();
        }
    }

    @Override
    public void showLoginFragment() {
        toolbarSetTitle(getResources().getString(R.string.action_login));

        if (loginFragment == null) {
            loginFragment = new LoginFragment();
        }

        if (!loginFragment.isInLayout()) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, loginFragment, Const.LOGIN_FRAGMENT)
                    .commitAllowingStateLoss();
        }
    }

    @Override
    public void showLogoutFragment() {
        toolbarSetTitle(getResources().getString(R.string.action_logout));

        if (logoutFragment == null) {
            logoutFragment = new LogoutFragment();
        }

        if (!logoutFragment.isInLayout()) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, logoutFragment, Const.LOGOUT_FRAGMENT)
                    .commitAllowingStateLoss();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }
}

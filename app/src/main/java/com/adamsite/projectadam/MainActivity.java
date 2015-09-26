package com.adamsite.projectadam;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

import com.adamsite.projectadam.fragments.LoginFragment;
import com.adamsite.projectadam.fragments.LogoutFragment;
import com.adamsite.projectadam.fragments.MyAudioFragment;

public class MainActivity extends AppCompatActivity implements MyAudioFragment.onShowMyAudio, LoginFragment.onShowLogin, LogoutFragment.onShowLogout {

    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initToolbar();
        initNavigationView();
        initSearchView();

        wakeUpSession();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    private void initSearchView() {
        searchView = (SearchView) findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                audioSearch(searchView.getQuery().toString());
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                myAudioSearch(searchView.getQuery().toString());
                return true;
            }
        });
    }

    private void initNavigationView() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close) {

            public void onDrawerClosed(View view) {
                supportInvalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                supportInvalidateOptionsMenu();
            }
        };
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.setDrawerListener(drawerToggle);

        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                switch (menuItem.getItemId()) {
                    case R.id.action_my_audio:
                        showMyAudioFragment();
                        break;
                    case R.id.action_vk_login:
                        showLoginFragment();
                        break;
                    case R.id.action_vk_logout:
                        showLogoutFragment();
                        break;
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
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
                Log.e(Const.LOG_TAG, error.toString());
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        VKCallback<VKAccessToken> callback = new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                // User passed Authorization
                showMyAudioFragment();
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
    public void myAudioSearch(String query) {
        MyAudioFragment myAudioFragment = (MyAudioFragment) getSupportFragmentManager().findFragmentByTag(Const.MY_AUDIO_FRAGMENT);
        if (myAudioFragment != null)
            myAudioFragment.myAudioSearch(query);
    }

    @Override
    public void audioSearch(String query) {
        MyAudioFragment myAudioFragment = (MyAudioFragment) getSupportFragmentManager().findFragmentByTag(Const.MY_AUDIO_FRAGMENT);
        if (myAudioFragment != null)
            myAudioFragment.audioSearch(query);
    }

    @Override
    public void showMyAudioFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, new MyAudioFragment(), Const.MY_AUDIO_FRAGMENT)
                .commit();
    }

    @Override
    public void showLoginFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, new LoginFragment(), Const.LOGIN_FRAGMENT)
                .commit();
    }

    @Override
    public void showLogoutFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, new LogoutFragment(), Const.LOGOUT_FRAGMENT)
                .commit();
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

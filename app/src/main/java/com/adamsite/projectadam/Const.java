package com.adamsite.projectadam;

import com.vk.sdk.VKScope;

public class Const {

    //logTags
    public static final String LOG_TAG_APP = "PROJECT_ADAM";

    //vkScope
    public static final String[] SCOPE = new String[]
            {
                    VKScope.AUDIO
            };

    //vkQuerySearch
    public static final String Q = "q";
    public static final String AUTO_COMPLETE = "auto_complete";
    public static final String SORT = "sort";
    public static final String OFFSET = "offset";
    public static final String COUNT = "count";

    //fragmentTags
    public static final String MY_AUDIO_FRAGMENT = "my_audio_fragment";
    public static final String LOGIN_FRAGMENT = "login_fragment";
    public static final String LOGOUT_FRAGMENT = "logout_fragment";
}

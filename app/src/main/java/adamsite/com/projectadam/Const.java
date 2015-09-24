package adamsite.com.projectadam;

import com.vk.sdk.VKScope;

public class Const {

    //scope
    public static final String[] SCOPE = new String[]
            {
                    VKScope.AUDIO
            };

    //search
    public static final String Q = "q";
    public static final String AUTO_COMPLETE = "auto_complete";
    public static final String SORT = "sort";
    public static final String OFFSET = "offset";
    public static final String COUNT = "count";

    //intentExtras
    public static final String FROM_ANOTHER_ACTIVITY = "from_another_activity";

    //fragmentTags
    public static final String MY_AUDIO_FRAGMENT = "my_audio_fragment";
    public static final String LOGIN_FRAGMENT = "login_fragment";
    public static final String LOGOUT_FRAGMENT = "logout_fragment";
}

package cn.ucai.fulicenter.dao;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2016/10/24.
 */
public class SharedPreferenceUtils {
    private static final String SHARE_NAME = "saveUserInfo";
    private static SharedPreferenceUtils instance;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    public static final String SHARE_KEY_USER_NAME = "share_key_user_name";

    public SharedPreferenceUtils(Context context) {
         mSharedPreferences = context.getSharedPreferences(SHARE_NAME, Context.MODE_PRIVATE);
        mEditor =  mSharedPreferences.edit();
    }

    public static SharedPreferenceUtils getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPreferenceUtils(context);
        }
        return instance;
    }

    public void saveUser(String userName) {
        mEditor.putString(SHARE_KEY_USER_NAME, userName);
        mEditor.commit();
    }
    public String getUser(){
        return  mSharedPreferences.getString(SHARE_KEY_USER_NAME, null);
    }
    public void removeUser(){
        mEditor.remove(SHARE_KEY_USER_NAME);
        mEditor.commit();
    }
}

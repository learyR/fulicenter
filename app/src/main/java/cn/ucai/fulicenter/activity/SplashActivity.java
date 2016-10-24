package cn.ucai.fulicenter.activity;

import android.content.Intent;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.User;
import cn.ucai.fulicenter.dao.SharedPreferenceUtils;
import cn.ucai.fulicenter.dao.UserDao;
import cn.ucai.fulicenter.utils.L;
import cn.ucai.fulicenter.utils.MFGT;

public class SplashActivity extends AppCompatActivity {
    private final long sleepTime = 2000;
    SplashActivity mContext;
    private static final String TAG = SplashActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mContext = this;
    }

    @Override
    protected void onStart() {
        super.onStart();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                User user = FuLiCenterApplication.getUser();
                L.e(TAG,"userName" + user);
                String userName = SharedPreferenceUtils.getInstance(mContext).getUser();
                L.e(TAG,"userName" + userName);
                if (user == null && userName != null) {
                    UserDao dao = new UserDao(mContext);
                    user = dao.getUser(userName);
                    L.e(TAG,"user" + user);
                    if (user != null) {
                        FuLiCenterApplication.setUser(user);
                    }
                }
                MFGT.gotoMainActivity(SplashActivity.this);
                finish();
            }
        },sleepTime);

        /*new Thread(new Runnable() {
            @Override
            public void run() {
                long startTime = System.currentTimeMillis();
                //create db
                long endTime = System.currentTimeMillis();
                long costTime = endTime - startTime;
                if (costTime < sleepTime) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                MFGT.startActivity(SplashActivity.this,MainActivity.class);
                MFGT.finish(SplashActivity.this);
            }
        }).start();*/
    }
}

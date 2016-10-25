package cn.ucai.fulicenter.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.User;
import cn.ucai.fulicenter.dao.SharedPreferenceUtils;
import cn.ucai.fulicenter.utils.CommonUtils;
import cn.ucai.fulicenter.utils.ImageLoader;
import cn.ucai.fulicenter.utils.MFGT;
import cn.ucai.fulicenter.views.DisplayUtils;

public class SettingActivity extends BaseActivity {
    SettingActivity mContext;
    @Bind(R.id.tvUserName)
    TextView tvUserName;
    @Bind(R.id.ivAvatar)
    ImageView ivAvatar;
    @Bind(R.id.tvUserNick)
    TextView tvUserNick;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void initData() {
        user= FuLiCenterApplication.getUser();
        if (user != null) {
            tvUserName.setText(user.getMuserName());
            tvUserNick.setText(user.getMuserNick());
            ImageLoader.setAvatar(ImageLoader.getAvatarUrl(user), mContext, ivAvatar);
        } else {
            MFGT.finish(mContext);
        }
    }

    @Override
    protected void initView() {
        mContext = this;
        DisplayUtils.initBackWithTitle(mContext, "个人资料");
    }

    @OnClick(R.id.btnExit)
    public void onClick() {
        if (user != null) {
            SharedPreferenceUtils.getInstance(mContext).removeUser();
            FuLiCenterApplication.setUser(null);
            MFGT.gotoLoginActivity(mContext);
        }
        MFGT.finish(mContext);
    }


    @OnClick({R.id.linearAvatar, R.id.linearUserName, R.id.linearUserNick})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.linearAvatar:
                changeAvatar();
                break;
            case R.id.linearUserName:
                CommonUtils.showShortToast("不能更改用户名");
                break;
            case R.id.linearUserNick:
                changeNick();
                break;
        }
    }

    private void changeNick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("请输入新用户昵称")
                .setMessage(user.getMuserNick())
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
    }

    private void changeAvatar() {

    }
}

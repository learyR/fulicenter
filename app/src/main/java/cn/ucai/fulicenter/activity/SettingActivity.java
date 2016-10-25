package cn.ucai.fulicenter.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.Result;
import cn.ucai.fulicenter.bean.User;
import cn.ucai.fulicenter.dao.SharedPreferenceUtils;
import cn.ucai.fulicenter.dao.UserDao;
import cn.ucai.fulicenter.net.NetDao;
import cn.ucai.fulicenter.net.OkHttpUtils;
import cn.ucai.fulicenter.utils.CommonUtils;
import cn.ucai.fulicenter.utils.ImageLoader;
import cn.ucai.fulicenter.utils.L;
import cn.ucai.fulicenter.utils.MFGT;
import cn.ucai.fulicenter.utils.ResultUtils;
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
        View view = View.inflate(mContext, R.layout.custom_dialog, null);
        final EditText etCustom = (EditText) view.findViewById(R.id.tvCustom);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("设置昵称")
                .setIcon(R.drawable.icon_account)
                .setView(view)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (user != null) {
                            String nick = etCustom.getText().toString().trim();
                            if (nick.equals(user.getMuserNick())) {
                                CommonUtils.showLongToast(R.string.update_nick_fail_unmodify);
                            } else if (TextUtils.isEmpty(nick)) {
                                CommonUtils.showLongToast(R.string.nick_name_connot_be_empty);
                            } else {
                                updateNick(nick);
                            }
                        }
                    }
                }).setNegativeButton("取消",null).create().show();
    }

    private void updateNick(final String nick) {
        final ProgressDialog pd = new ProgressDialog(mContext);
        pd.setMessage(getResources().getString(R.string.logining));
        pd.show();
        NetDao.updateNick(mContext, user.getMuserName(), nick, new OkHttpUtils.OnCompleteListener<String>() {
            @Override
            public void onSuccess(String str) {
                Result result = ResultUtils.getResultFromJson(str, User.class);
                if (result.getRetCode() !=0) {
                    CommonUtils.showShortToast(R.string.update_nick_fail);
                } else {
                    if (result.isRetMsg()) {
                        User user= (User) result.getRetData();
                        UserDao dao = new UserDao(mContext);
                        boolean isSuccess= dao.updateUser(user);
                        if (isSuccess) {
//                            SharedPreferenceUtils.getInstance(mContext).saveUser(user.getMuserName());
                            FuLiCenterApplication.setUser(user);
                            user.setMuserNick(nick);
                            CommonUtils.showLongToast(R.string.update_nick_success);
                        } else {
                            CommonUtils.showLongToast(R.string.update_database_error);
                        }
                    }
                }
                pd.dismiss();
            }

            @Override
            public void onError(String error) {
                CommonUtils.showShortToast("updateNick" + error);
                L.e("updateNick error" + error);
            }
        });
    }

}

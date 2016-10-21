package cn.ucai.fulicenter.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.Result;
import cn.ucai.fulicenter.net.NetDao;
import cn.ucai.fulicenter.net.OkHttpUtils;
import cn.ucai.fulicenter.utils.CommonUtils;
import cn.ucai.fulicenter.utils.L;
import cn.ucai.fulicenter.utils.MFGT;
import cn.ucai.fulicenter.views.DisplayUtils;

public class LoginActivity extends BaseActivity {

    @Bind(R.id.etUserName)
    EditText etUserName;
    @Bind(R.id.etPassword)
    EditText etPassword;
    LoginActivity mContext;
    String userName;
    String password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        mContext = this;
        DisplayUtils.initBackWithTitle(this,"账户登录");
    }

    @OnClick({R.id.btnLogin, R.id.btnRegister})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogin:

                checkedInput();

                break;
            case R.id.btnRegister:
                MFGT.gotoRegisterActivity(this);
                break;
        }
    }

    private void checkedInput() {
        userName = etUserName.getText().toString().trim();
        password = etPassword.getText().toString().trim();
        if (userName.isEmpty()) {
            CommonUtils.showShortToast(R.string.user_name_connot_be_empty);
            etUserName.requestFocus();
            return;
        } else if (password.isEmpty()) {
            CommonUtils.showShortToast(R.string.password_connot_be_empty);
            etPassword.requestFocus();
            return;
        }
        login();
    }

    private void login() {
        final ProgressDialog pd = new ProgressDialog(mContext);
        pd.setMessage(getResources().getString(R.string.logining));
        pd.show();
        NetDao.login(mContext, userName, password, new OkHttpUtils.OnCompleteListener<Result>() {
            @Override
            public void onSuccess(Result result) {
                pd.dismiss();
                if (result == null) {
                    CommonUtils.showShortToast(R.string.login_fail);
                } else {
                    if (result.isRetMsg()) {
                        CommonUtils.showLongToast(R.string.login_success);
                        MFGT.finish(mContext);
                    } else {
                        if (result.getRetCode() == I.MSG_LOGIN_UNKNOW_USER) {
                            CommonUtils.showLongToast(R.string.login_fail_unknow_user);
                        } else if (result.getRetCode() == I.MSG_LOGIN_ERROR_PASSWORD) {
                            CommonUtils.showLongToast(R.string.login_fail_error_password);
                        }else {
                            CommonUtils.showLongToast(R.string.login_fail);
                        }
                    }
                }
            }

            @Override
            public void onError(String error) {
                CommonUtils.showShortToast("login" + error);
                L.e("login error" + error);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == I.REQUEST_CODE_REGISTER) {
            String name = data.getStringExtra(I.User.USER_NAME);
            etUserName.setText(name);
        }
    }
}

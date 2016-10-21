package cn.ucai.fulicenter.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.Result;
import cn.ucai.fulicenter.net.NetDao;
import cn.ucai.fulicenter.net.OkHttpUtils;
import cn.ucai.fulicenter.utils.CommonUtils;
import cn.ucai.fulicenter.utils.ConvertUtils;
import cn.ucai.fulicenter.utils.L;
import cn.ucai.fulicenter.utils.MFGT;
import cn.ucai.fulicenter.views.DisplayUtils;

public class RegisterActivity extends BaseActivity {


    @Bind(R.id.etUserName)
    EditText etUserName;
    @Bind(R.id.etNick)
    EditText etNick;
    @Bind(R.id.etPassword)
    EditText etPassword;
    @Bind(R.id.etCheckedPassword)
    EditText etCheckedPassword;


    String userName;
    String userNick;
    String password;
    String checkedPassword;
    RegisterActivity mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_register);
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
        DisplayUtils.initBackWithTitle(this, "账户注册");

    }

    @OnClick({R.id.ivBack, R.id.btnRegister_free})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivBack:
                break;
            case R.id.btnRegister_free:
                userName = etUserName.getText().toString().trim();
                userNick = etNick.getText().toString().trim();
                password = etPassword.getText().toString().trim();
                checkedPassword = etCheckedPassword.getText().toString().trim();
                if (userName == null || userName.length() == 0) {
                    CommonUtils.showShortToast(R.string.user_name_connot_be_empty);
                    etUserName.requestFocus();
                    return;
                } else if (!userName.matches("[a-zA-Z]\\w{5,15}")) {
                    CommonUtils.showShortToast(R.string.illegal_user_name);
                    etUserName.requestFocus();
                    return;
                }else if (userNick.isEmpty()) {
                    CommonUtils.showShortToast(R.string.nick_name_connot_be_empty);
                    etNick.requestFocus();
                    return;
                }else if (password.isEmpty()) {
                    CommonUtils.showShortToast(R.string.password_connot_be_empty);
                    etPassword.requestFocus();
                    return;
                } else if (checkedPassword.isEmpty()) {
                    CommonUtils.showShortToast(R.string.confirm_password_connot_be_empty);
                    etCheckedPassword.requestFocus();
                    return;
                }else if (!password.equals(checkedPassword)) {
                    CommonUtils.showShortToast(R.string.two_input_password);
                    etCheckedPassword.requestFocus();
                    return;
                }
                register(userName, userNick, password);
                break;
        }
    }

    private void register(final String userName, String userNick, String password) {
        final ProgressDialog pd = new ProgressDialog(mContext);
        pd.setMessage(getResources().getString(R.string.registering));
        pd.show();
        NetDao.register(mContext, userName, userNick, password, new OkHttpUtils.OnCompleteListener<Result>() {
            @Override
            public void onSuccess(Result result) {
                pd.dismiss();
                if (result == null) {
                    CommonUtils.showShortToast(R.string.register_fail);
                } else {
                    if (result.isRetMsg()) {
                        CommonUtils.showLongToast(R.string.register_success);
                        setResult(RESULT_OK, new Intent().putExtra(I.User.USER_NAME, userName));
                        MFGT.finish(mContext);
                    } else {
                        CommonUtils.showLongToast(R.string.register_fail_exists);
                        etUserName.requestFocus();
                    }
                }
            }

            @Override
            public void onError(String error) {
                pd.dismiss();
                CommonUtils.showShortToast(error);
                L.e("register error"+error);
            }
        });
    }


}

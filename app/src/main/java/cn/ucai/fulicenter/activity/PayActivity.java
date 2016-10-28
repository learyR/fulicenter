package cn.ucai.fulicenter.activity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.utils.L;
import cn.ucai.fulicenter.views.DisplayUtils;

public class PayActivity extends BaseActivity {
    PayActivity mContext;
    @Bind(R.id.etCustom)
    EditText etCustom;
    @Bind(R.id.etPhoneNum)
    EditText etPhoneNum;
    @Bind(R.id.spArea)
    Spinner spArea;
    @Bind(R.id.etStreet)
    EditText etStreet;
    @Bind(R.id.tvPrice)
    TextView tvPrice;
    @Bind(R.id.tvOrder)
    TextView tvOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_pay);
        ButterKnife.bind(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void initData() {
        String s = getIntent().getStringExtra(I.Cart.ID);
        L.e(s);
    }

    @Override
    protected void initView() {
        mContext = this;
        DisplayUtils.initBackWithTitle(mContext,"确认订单");
    }
}

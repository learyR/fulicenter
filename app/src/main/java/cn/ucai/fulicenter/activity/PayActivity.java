package cn.ucai.fulicenter.activity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.CartBean;
import cn.ucai.fulicenter.bean.User;
import cn.ucai.fulicenter.net.NetDao;
import cn.ucai.fulicenter.net.OkHttpUtils;
import cn.ucai.fulicenter.utils.L;
import cn.ucai.fulicenter.utils.ResultUtils;
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

    User user =null ;
    String cartsIds = "";
    ArrayList<CartBean> mList = null;
    String[] ids = new String[]{};
    int rankPrice;
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
       cartsIds= getIntent().getStringExtra(I.Cart.ID);
        user = FuLiCenterApplication.getUser();
        L.e(cartsIds);
        if (cartsIds == null || cartsIds.equals("") || user == null) {
            finish();
        }
        ids = cartsIds.split(",");
        getOrderList();
    }

    private void getOrderList() {
        NetDao.downloadCart(mContext, user.getMuserName(), new OkHttpUtils.OnCompleteListener<String>() {
            @Override
            public void onSuccess(String str) {
                ArrayList<CartBean> list = ResultUtils.getCartFromJson(str);
                if (list == null || list.size() == 0) {
                    finish();
                } else {
                    mList.addAll(list);
                    sumPrice();
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    private void sumPrice(){
        rankPrice = 0;
        cartsIds = "";
        if (mList != null && mList.size() > 0) {
            for (CartBean c : mList) {
                for (String id : ids) {
                    if (id.equals(c.getId() + "")) {
                        rankPrice += getPrice(c.getGoods().getRankPrice()) * c.getCount();
                    }

                }
            }
        }
        tvPrice.setText("合计: ￥" + Double.valueOf(rankPrice));
    }
    private int getPrice(String price) {
        price = price.substring(price.indexOf("￥") + 1);
        return Integer.valueOf(price);
    }

    @Override
    protected void initView() {
        mContext = this;
        mList = new ArrayList<>();
        DisplayUtils.initBackWithTitle(mContext, "确认订单");
    }

    @OnClick(R.id.tvOrder)
    public void onClickOrder() {
        String receiveName = etCustom.getText().toString();

        if (receiveName.isEmpty()) {
            etCustom.setError("收货人姓名不能为空");
            etCustom.requestFocus();
            return;
        }
        String mobile = etPhoneNum.getText().toString();
        if (mobile.isEmpty()) {
            etPhoneNum.setError("手机号码不能为空");
            etPhoneNum.requestFocus();
            return;
        }
        if (!mobile.matches("[\\d]{11}")) {
            etPhoneNum.setError("手机号码格式错误");
            etPhoneNum.requestFocus();
            return;
        }
        String eqra = spArea.getSelectedItem().toString();
        if (eqra.isEmpty()) {
            Toast.makeText(PayActivity.this, "收货地区不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        String address = etStreet.getText().toString();
        if (address.isEmpty()) {
            etStreet.setError("街道地址不能为空");
            etStreet.requestFocus();
            return;
        }
        gotoStatements();
    }
    private void gotoStatements() {
        L.e("我是传过来的价格" + rankPrice);
    }
}

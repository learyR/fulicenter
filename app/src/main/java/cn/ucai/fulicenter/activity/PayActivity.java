package cn.ucai.fulicenter.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.pingplusplus.android.PingppLog;
import com.pingplusplus.libone.PaymentHandler;
import com.pingplusplus.libone.PingppOne;


import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.CartBean;
import cn.ucai.fulicenter.bean.MessageBean;
import cn.ucai.fulicenter.bean.User;
import cn.ucai.fulicenter.net.NetDao;
import cn.ucai.fulicenter.net.OkHttpUtils;
import cn.ucai.fulicenter.utils.CommonUtils;
import cn.ucai.fulicenter.utils.L;
import cn.ucai.fulicenter.utils.ResultUtils;
import cn.ucai.fulicenter.views.DisplayUtils;

public class PayActivity extends BaseActivity implements PaymentHandler{
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
    private static String URL = "http://218.244.151.190/demo/charge";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_pay);
        ButterKnife.bind(this);
        super.onCreate(savedInstanceState);
        //设置需要使用的支付方式
        PingppOne.enableChannels(new String[]{"wx", "alipay", "upacp", "bfb", "jdpay_wap"});

        // 提交数据的格式，默认格式为json
        // PingppOne.CONTENT_TYPE = "application/x-www-form-urlencoded";
        PingppOne.CONTENT_TYPE = "application/json";

        PingppLog.DEBUG = true;
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
        String area = spArea.getSelectedItem().toString();
        if (area.isEmpty()) {
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
        // 产生个订单号
        String orderNo = new SimpleDateFormat("yyyyMMddhhmmss")
                .format(new Date());

        // 构建账单json对象
        JSONObject bill = new JSONObject();

        // 自定义的额外信息 选填
        JSONObject extras = new JSONObject();
        try {
            extras.put("extra1", "extra1");
            extras.put("extra2", "extra2");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            bill.put("order_no", orderNo);
            bill.put("amount", rankPrice*100);
            bill.put("extras", extras);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //壹收款: 创建支付通道的对话框
        PingppOne.showPaymentChannels(getSupportFragmentManager(), bill.toString(), URL, this);
    }

    @Override
    public void handlePaymentResult(Intent data) {
        if (data != null) {

            // result：支付结果信息
            // code：支付结果码
            //-2:用户自定义错误
            //-1：失败
            // 0：取消
            // 1：成功
            // 2:应用内快捷支付支付结果
            L.e("它的代码+" + data.getExtras().getInt("code"));
            if (data.getExtras().getInt("code") != 2) {
                PingppLog.d(data.getExtras().getString("result") + "  " + data.getExtras().getInt("code"));
            } else {
                String result = data.getStringExtra("result");
                try {
                    JSONObject resultJson = new JSONObject(result);
                    if (resultJson.has("error")) {
                        result = resultJson.optJSONObject("error").toString();
                    } else if (resultJson.has("success")) {
                        result = resultJson.optJSONObject("success").toString();
                    }
                    L.e("它的代码" + result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            int resultCode = data.getExtras().getInt("code");
            switch (resultCode) {
                case 1:
                    patSuccess();
                    CommonUtils.showLongToast(R.string.pingpp_title_activity_pay_sucessed);
                    break;
                case -1:
                    CommonUtils.showLongToast(R.string.pingpp_pay_failed);
                    finish();
                    break;
            }
        }
    }

    private void patSuccess() {
        for (String id : ids) {
            NetDao.deleteCart(mContext, Integer.valueOf(id), new OkHttpUtils.OnCompleteListener<MessageBean>() {
                @Override
                public void onSuccess(MessageBean result) {

                }

                @Override
                public void onError(String error) {

                }
            });
        }
        finish();
    }
}

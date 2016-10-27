package cn.ucai.fulicenter.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.MainActivity;
import cn.ucai.fulicenter.adapter.CartAdapter;
import cn.ucai.fulicenter.bean.CartBean;
import cn.ucai.fulicenter.bean.User;
import cn.ucai.fulicenter.net.NetDao;
import cn.ucai.fulicenter.net.OkHttpUtils;
import cn.ucai.fulicenter.utils.CommonUtils;
import cn.ucai.fulicenter.utils.L;
import cn.ucai.fulicenter.utils.ResultUtils;
import cn.ucai.fulicenter.views.SpaceItemDecoration;

/**
 * A simple {@link Fragment} subclass.
 */
public class CartFragment extends BaseFragment {

    MainActivity mContext;
    LinearLayoutManager mLayoutManager;
    ArrayList<CartBean> boutiqueList;
    CartAdapter boutiqueAdapter;
    int pageId = 1;
    @Bind(R.id.tvRefresh)
    TextView tvRefresh;
    @Bind(R.id.rv)
    RecyclerView rv;
    @Bind(R.id.srl)
    SwipeRefreshLayout srl;
    @Bind(R.id.tvNothing)
    TextView tvNothing;
    @Bind(R.id.tvSum)
    TextView tvSum;
    @Bind(R.id.tvSumPrice)
    TextView tvSumPrice;
    @Bind(R.id.RankPrice)
    TextView tvRankPrice;
    @Bind(R.id.layoutCart)
    LinearLayout layoutCart;


    updateCartReceiver mReceiver;

    public CartFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        ButterKnife.bind(this, view);
        super.onCreateView(inflater, container, savedInstanceState);
        return view;
    }

    @Override
    protected void setListener() {
        setPullUpListener();
        setPullDownListener();


        mReceiver = new updateCartReceiver();
        IntentFilter filter = new IntentFilter(I.BROADCAST_UPDATE_CAST);
        mContext.registerReceiver(mReceiver,filter);
    }

    private void setPullDownListener() {
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageId = 1;
                srl.setRefreshing(true);
                tvRefresh.setVisibility(View.VISIBLE);
                downloadCart(I.ACTION_PULL_DOWN);
            }
        });
    }


    private void downloadCart(final int action) {
        User user = FuLiCenterApplication.getUser();
        if (user != null) {
            NetDao.downloadCart(mContext, user.getMuserName(), new OkHttpUtils.OnCompleteListener<String>() {
                @Override
                public void onSuccess(String str) {
                    ArrayList<CartBean> list = ResultUtils.getCartFromJson(str);
                    srl.setRefreshing(false);
                    tvRefresh.setVisibility(View.GONE);
                    boutiqueAdapter.setMore(true);
                    if (list != null && list.size() > 0) {
                        boutiqueList.clear();
                        boutiqueList.addAll(list);
                        if (action == I.ACTION_DOWNLOAD || action == I.ACTION_PULL_DOWN) {
                            boutiqueAdapter.initData(boutiqueList);
                        } else {
                            boutiqueAdapter.addData(boutiqueList);
                        }
                        if (list.size() < I.PAGE_SIZE_DEFAULT) {
                            boutiqueAdapter.setMore(false);
                        }
                        setCartLayout(true);
                    } else {
                        boutiqueAdapter.setMore(false);
                        setCartLayout(false);
                    }
                }

                @Override
                public void onError(String error) {
                    setCartLayout(false);
                    srl.setRefreshing(false);
                    tvRefresh.setVisibility(View.GONE);
                    boutiqueAdapter.setMore(false);
                    CommonUtils.showShortToast(error);
                    L.e("error" + error);
                    L.e("boutique:" + error);
                }
            });
        }
    }

    private void setPullUpListener() {
        rv.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int lastPosition = mLayoutManager.findLastVisibleItemPosition();
                if (lastPosition == boutiqueAdapter.getItemCount() - 1 && newState == RecyclerView.SCROLL_STATE_IDLE && boutiqueAdapter.isMore()) {
                    pageId++;
                    downloadCart(I.ACTION_PULL_UP);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstPosition = mLayoutManager.findFirstVisibleItemPosition();
                srl.setEnabled(firstPosition == 0);
            }
        });
    }

    @Override
    protected void initData() {
        downloadCart(I.ACTION_DOWNLOAD);
    }

    @Override
    protected void initView() {
        mContext = (MainActivity) getContext();
        boutiqueList = new ArrayList<>();
        boutiqueAdapter = new CartAdapter(mContext, boutiqueList);
        mLayoutManager = new LinearLayoutManager(mContext);

        srl.setColorSchemeColors(
                getResources().getColor(R.color.google_blue),
                getResources().getColor(R.color.google_green),
                getResources().getColor(R.color.google_red),
                getResources().getColor(R.color.google_yellow)
        );
        rv.setLayoutManager(mLayoutManager);
        rv.setHasFixedSize(true);
        rv.setAdapter(boutiqueAdapter);
        rv.addItemDecoration(new SpaceItemDecoration(15));

        setCartLayout(false);

    }

    private void setCartLayout(boolean hasCart) {
       /* if (boutiqueList != null && boutiqueList.size() > 0) {
            hasCart = true;
        }*/
        layoutCart.setVisibility(hasCart ? View.VISIBLE : View.GONE);
        tvNothing.setVisibility(hasCart ? View.GONE : View.VISIBLE);
        rv.setVisibility(hasCart ? View.VISIBLE : View.GONE);
        sumPrice();
    }

    private void sumPrice(){
        int sumPrice = 0;
        int rankPrice = 0;
        if (boutiqueList != null && boutiqueList.size() > 0) {
            for (CartBean c : boutiqueList) {
                if (c.isChecked()) {
                    sumPrice += getPrice(c.getGoods().getCurrencyPrice()) * c.getCount();
                    rankPrice += getPrice(c.getGoods().getRankPrice()) * c.getCount();
                }
            }
            tvSumPrice.setText("合计: ￥" + sumPrice);
            tvRankPrice.setText("节省: ￥" +(sumPrice-rankPrice));
        } else {
            tvSumPrice.setText("合计: ￥0.0");
            tvRankPrice.setText("节省: ￥0.0" );
        }
    }
    private int getPrice(String price) {
        price = price.substring(price.indexOf("￥") + 1);
        return Integer.valueOf(price);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.tvSum)
    public void onClick() {
    }

    class updateCartReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            sumPrice();
            setCartLayout(boutiqueList != null && boutiqueList.size() > 0);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            mContext.unregisterReceiver(mReceiver);
        }
    }
}

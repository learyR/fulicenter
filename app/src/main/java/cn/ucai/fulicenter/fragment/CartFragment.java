package cn.ucai.fulicenter.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.MainActivity;
import cn.ucai.fulicenter.adapter.CartAdapter;
import cn.ucai.fulicenter.bean.BoutiqueBean;
import cn.ucai.fulicenter.bean.CartBean;
import cn.ucai.fulicenter.bean.User;
import cn.ucai.fulicenter.net.NetDao;
import cn.ucai.fulicenter.net.OkHttpUtils;
import cn.ucai.fulicenter.utils.CommonUtils;
import cn.ucai.fulicenter.utils.ConvertUtils;
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
            NetDao.downloadCart(mContext,user.getMuserName(), new OkHttpUtils.OnCompleteListener<String>() {
                @Override
                public void onSuccess(String str) {
                    ArrayList<CartBean> list = ResultUtils.getCartFromJson(str);
                    srl.setRefreshing(false);
                    tvRefresh.setVisibility(View.GONE);
                    boutiqueAdapter.setMore(true);
                    if (list != null && list.size() > 0) {
                        if (action == I.ACTION_DOWNLOAD || action == I.ACTION_PULL_DOWN) {
                            boutiqueAdapter.initData(list);
                        } else {
                            boutiqueAdapter.addData(list);
                        }
                        if (list.size() < I.PAGE_SIZE_DEFAULT) {
                            boutiqueAdapter.setMore(false);
                        }
                    } else {
                        boutiqueAdapter.setMore(false);
                    }
                }

                @Override
                public void onError(String error) {
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
        boutiqueAdapter = new CartAdapter(mContext,boutiqueList);
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

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}

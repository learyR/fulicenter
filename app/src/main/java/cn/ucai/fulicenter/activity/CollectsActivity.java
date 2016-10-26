package cn.ucai.fulicenter.activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.adapter.CollectsAdapter;
import cn.ucai.fulicenter.bean.CollectBean;
import cn.ucai.fulicenter.bean.User;
import cn.ucai.fulicenter.net.NetDao;
import cn.ucai.fulicenter.net.OkHttpUtils;
import cn.ucai.fulicenter.utils.CommonUtils;
import cn.ucai.fulicenter.utils.ConvertUtils;
import cn.ucai.fulicenter.utils.L;
import cn.ucai.fulicenter.views.DisplayUtils;
import cn.ucai.fulicenter.views.SpaceItemDecoration;

public class CollectsActivity extends BaseActivity {
    CollectsActivity mContext;
    @Bind(R.id.tvRefresh)
    TextView tvRefresh;
    @Bind(R.id.rv)
    RecyclerView rv;
    @Bind(R.id.srl)
    SwipeRefreshLayout srl;

    CollectsAdapter mAdapter;
    ArrayList<CollectBean> mNewGoodsList;
    int pageId = 1;
    GridLayoutManager mLayoutManager;
    User user = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_collection);
        ButterKnife.bind(this);
        super.onCreate(savedInstanceState);
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
                tvRefresh.setVisibility(View.VISIBLE) ;
                downloadCollection(I.ACTION_PULL_DOWN);
            }
        });
    }

    private void setPullUpListener() {
        rv.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int lastPosition = mLayoutManager.findLastVisibleItemPosition();
                if (lastPosition == mAdapter.getItemCount() - 1 && newState == RecyclerView.SCROLL_STATE_IDLE && mAdapter.isMore()) {
                    pageId++;
                    downloadCollection(I.ACTION_PULL_UP);
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
        user= FuLiCenterApplication.getUser();
        if (user == null) {
            finish();
        }
        downloadCollection(I.ACTION_DOWNLOAD);
    }

    private void downloadCollection(final int action) {
        NetDao.downloadCollects(mContext,user.getMuserName(), pageId, new OkHttpUtils.OnCompleteListener<CollectBean[]>() {
            @Override
            public void onSuccess(CollectBean[] result) {
                srl.setRefreshing(false);
                tvRefresh.setVisibility(View.GONE);
                mAdapter.setMore(true);
                if (result != null && result.length > 0) {
                    ArrayList<CollectBean> goodsList = ConvertUtils.array2List(result);
                    L.e(result.toString());
                    if (action == I.ACTION_DOWNLOAD || action == I.ACTION_PULL_DOWN) {
                        mAdapter.initData(goodsList);
                    } else {
                        mAdapter.addData(goodsList);
                    }

                    if (goodsList.size() < I.PAGE_SIZE_DEFAULT) {
                        mAdapter.setMore(false);
                    }
                } else {
                    mAdapter.setMore(false);
                }
            }

            @Override
            public void onError(String error) {
                srl.setRefreshing(false);
                tvRefresh.setVisibility(View.GONE);
                mAdapter.setMore(false);
                CommonUtils.showShortToast(error);
                L.e("error"+error);
            }
        });
    }

    @Override
    protected void initView() {
        mContext = this;
        DisplayUtils.initBackWithTitle(mContext, "收藏的宝贝");
        mNewGoodsList = new ArrayList<>();
        mAdapter = new CollectsAdapter(mNewGoodsList,mContext);
        srl.setColorSchemeColors(
                getResources().getColor(R.color.google_blue),
                getResources().getColor(R.color.google_green),
                getResources().getColor(R.color.google_red),
                getResources().getColor(R.color.google_yellow)
        );
        mLayoutManager =new GridLayoutManager(mContext, I.COLUM_NUM);
        rv.setLayoutManager(mLayoutManager);
        rv.setHasFixedSize(true);
        rv.addItemDecoration(new SpaceItemDecoration(15));
        rv.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }
}

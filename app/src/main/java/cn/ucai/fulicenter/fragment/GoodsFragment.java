package cn.ucai.fulicenter.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.MainActivity;
import cn.ucai.fulicenter.adapter.NewGoodsAdapter;
import cn.ucai.fulicenter.bean.NewGoodsBean;
import cn.ucai.fulicenter.net.NetDao;
import cn.ucai.fulicenter.net.OkHttpUtils;
import cn.ucai.fulicenter.utils.CommonUtils;
import cn.ucai.fulicenter.utils.ConvertUtils;
import cn.ucai.fulicenter.utils.L;
import cn.ucai.fulicenter.views.SpaceItemDecoration;

/**
 * A simple {@link Fragment} subclass.
 */
public class GoodsFragment extends Fragment {


    @Bind(R.id.tvRefresh)
    TextView mtvRefresh;
    @Bind(R.id.rvNewGoods)
    RecyclerView mrvNewGoods;
    @Bind(R.id.srlNewGoods)
    SwipeRefreshLayout mSrlNewGoods;


    MainActivity mContext;
    NewGoodsAdapter mAdapter;
    ArrayList<NewGoodsBean> mNewGoodsList;
    int pageId = 1;
    GridLayoutManager mLayoutManager;


    public GoodsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_goods, container, false);
        ButterKnife.bind(this, view);
        initView();
        initData();
        setListener();
        return view;
    }

    private void setListener() {
        setPullUpListener();
        setPullDownListener();
    }

    private void setPullDownListener() {
        mSrlNewGoods.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageId = 1;
                mSrlNewGoods.setRefreshing(true);
                mtvRefresh.setVisibility(View.VISIBLE);
                downloadNewGoods(I.ACTION_PULL_DOWN);
            }
        });
    }

    private void setPullUpListener() {
        mrvNewGoods.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int lastPosition = mLayoutManager.findLastVisibleItemPosition();
                if (lastPosition == mAdapter.getItemCount() - 1 && newState == RecyclerView.SCROLL_STATE_IDLE && mAdapter.isMore()) {
                    pageId++;
                    downloadNewGoods(I.ACTION_PULL_UP);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstPosition = mLayoutManager.findFirstVisibleItemPosition();
                mSrlNewGoods.setEnabled(firstPosition == 0);
            }
        });
    }

    private void downloadNewGoods(final int action) {
        NetDao.downloadNewGoods(mContext, pageId, new OkHttpUtils.OnCompleteListener<NewGoodsBean[]>() {
            @Override
            public void onSuccess(NewGoodsBean[] result) {
                mSrlNewGoods.setRefreshing(false);
                mtvRefresh.setVisibility(View.GONE);
                mAdapter.setMore(true);
                if (result != null && result.length > 0) {
                    ArrayList<NewGoodsBean> goodsList = ConvertUtils.array2List(result);
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
                mSrlNewGoods.setRefreshing(false);
                mtvRefresh.setVisibility(View.GONE);
                mAdapter.setMore(false);
                CommonUtils.showShortToast(error);
                L.e("error"+error);
            }
        });
    }

    private void initData() {
       downloadNewGoods(I.ACTION_DOWNLOAD);
    }

    private void initView() {
        mContext = (MainActivity) getContext();
        mNewGoodsList = new ArrayList<>();
        mAdapter = new NewGoodsAdapter(mNewGoodsList, mContext);
        mSrlNewGoods.setColorSchemeColors(
                getResources().getColor(R.color.google_blue),
                getResources().getColor(R.color.google_green),
                getResources().getColor(R.color.google_red),
                getResources().getColor(R.color.google_yellow)
        );
        mLayoutManager =new GridLayoutManager(mContext, I.COLUM_NUM);
        mrvNewGoods.setLayoutManager(mLayoutManager);
        mrvNewGoods.setHasFixedSize(true);
        mrvNewGoods.addItemDecoration(new SpaceItemDecoration(15));
        mrvNewGoods.setAdapter(mAdapter);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}

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
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.MainActivity;
import cn.ucai.fulicenter.adapter.BoutiqueAdapter;
import cn.ucai.fulicenter.bean.BoutiqueBean;
import cn.ucai.fulicenter.net.NetDao;
import cn.ucai.fulicenter.net.OkHttpUtils;
import cn.ucai.fulicenter.utils.CommonUtils;
import cn.ucai.fulicenter.utils.ConvertUtils;
import cn.ucai.fulicenter.utils.L;
import cn.ucai.fulicenter.views.SpaceItemDecoration;

/**
 * A simple {@link Fragment} subclass.
 */
public class BoutiqueFragment extends BaseFragment {


    @Bind(R.id.tv_boutique_Refresh)
    TextView tvBoutiqueRefresh;
    @Bind(R.id.rvBoutique)
    RecyclerView rvBoutique;
    @Bind(R.id.srlBoutique)
    SwipeRefreshLayout srlBoutique;
    MainActivity mContext;
    LinearLayoutManager mLayoutManager;
    ArrayList<BoutiqueBean> boutiqueList;
    BoutiqueAdapter boutiqueAdapter;
    int pageId = 1;

    public BoutiqueFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_boutique, container, false);
        ButterKnife.bind(this, view);
        /*initView();
        initData();
        setListener();*/
        super.onCreateView(inflater, container, savedInstanceState);
        return view;
    }
    @Override
    protected void setListener() {
        setPullUpListener();
        setPullDownListener();
    }

    private void setPullDownListener() {
        srlBoutique.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageId = 1;
                srlBoutique.setRefreshing(true);
                tvBoutiqueRefresh.setVisibility(View.VISIBLE);
                downloadNewGoods(I.ACTION_PULL_DOWN);
            }
        });
    }


    private   void downloadNewGoods(final int action) {
        NetDao.downloadBoutique(mContext, new OkHttpUtils.OnCompleteListener<BoutiqueBean[]>() {
            @Override
            public void onSuccess(BoutiqueBean[] result) {
                srlBoutique.setRefreshing(false);
                tvBoutiqueRefresh.setVisibility(View.GONE);
                boutiqueAdapter.setMore(true);
                if (result != null && result.length > 0) {
                    ArrayList<BoutiqueBean> goodsList = ConvertUtils.array2List(result);
                    if (action == I.ACTION_DOWNLOAD || action == I.ACTION_PULL_DOWN) {
                        boutiqueAdapter.initData(goodsList);
                    } else {
                        boutiqueAdapter.addData(goodsList);
                    }

                    if (goodsList.size() < I.PAGE_SIZE_DEFAULT) {
                        boutiqueAdapter.setMore(false);
                    }
                } else {
                    boutiqueAdapter.setMore(false);
                }
            }

            @Override
            public void onError(String error) {
                srlBoutique.setRefreshing(false);
                tvBoutiqueRefresh.setVisibility(View.GONE);
                boutiqueAdapter.setMore(false);
                CommonUtils.showShortToast(error);
                L.e("error"+error);
                L.e("boutique:"+error);
            }
        });
    }

    private void setPullUpListener() {
        rvBoutique.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int lastPosition = mLayoutManager.findLastVisibleItemPosition();
                if (lastPosition == boutiqueAdapter.getItemCount() - 1 && newState == RecyclerView.SCROLL_STATE_IDLE && boutiqueAdapter.isMore()) {
                    pageId++;
                    downloadNewGoods(I.ACTION_PULL_UP);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstPosition = mLayoutManager.findFirstVisibleItemPosition();
                srlBoutique.setEnabled(firstPosition == 0);
            }
        });
    }
    @Override
    protected void initData() {
        downloadNewGoods(I.ACTION_DOWNLOAD);
    }

    @Override
    protected void initView() {
        mContext = (MainActivity) getContext();
        boutiqueList = new ArrayList<>();
        boutiqueAdapter = new BoutiqueAdapter(mContext,boutiqueList);
        mLayoutManager = new LinearLayoutManager(mContext);

        srlBoutique.setColorSchemeColors(
                getResources().getColor(R.color.google_blue),
                getResources().getColor(R.color.google_green),
                getResources().getColor(R.color.google_red),
                getResources().getColor(R.color.google_yellow)
        );
        rvBoutique.setLayoutManager(mLayoutManager);
        rvBoutique.setHasFixedSize(true);
        rvBoutique.setAdapter(boutiqueAdapter);
        rvBoutique.addItemDecoration(new SpaceItemDecoration(15));

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}

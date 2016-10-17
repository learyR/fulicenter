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

        return view;
    }

    private void initData() {
        NetDao.downloadNewGoods(mContext, pageId, new OkHttpUtils.OnCompleteListener<NewGoodsBean[]>() {
            @Override
            public void onSuccess(NewGoodsBean[] result) {

            }

            @Override
            public void onError(String error) {

            }
        });
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
        mrvNewGoods.setAdapter(mAdapter);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
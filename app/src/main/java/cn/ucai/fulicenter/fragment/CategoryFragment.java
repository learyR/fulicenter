package cn.ucai.fulicenter.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
public class CategoryFragment extends BaseFragment {


    @Bind(R.id.tvRefresh)
    TextView tvRefresh;
    @Bind(R.id.rv)
    RecyclerView rv;
    @Bind(R.id.srl)
    SwipeRefreshLayout srl;
    MainActivity mContext;
    NewGoodsAdapter mAdapter;
    ArrayList<NewGoodsBean> mNewGoodsList;
    int pageId = 1;
    LinearLayoutManager mLayoutManager;

    public CategoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        ButterKnife.bind(this, view);

        super.onCreateView(inflater, container, savedInstanceState);
        return view;
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void initData() {
        downloadCategory(I.ACTION_DOWNLOAD);
    }

    private void downloadCategory(final int action) {

    }

    @Override
    protected void initView() {
        mContext = (MainActivity) getContext();
        mNewGoodsList = new ArrayList<>();
        mAdapter = new NewGoodsAdapter(mNewGoodsList, mContext);
        srl.setColorSchemeColors(
                getResources().getColor(R.color.google_blue),
                getResources().getColor(R.color.google_green),
                getResources().getColor(R.color.google_red),
                getResources().getColor(R.color.google_yellow)
        );
        mLayoutManager =new LinearLayoutManager(mContext);
        rv.setLayoutManager(mLayoutManager);
        rv.setHasFixedSize(true);
        rv.addItemDecoration(new SpaceItemDecoration(15));
        rv.setAdapter(mAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}

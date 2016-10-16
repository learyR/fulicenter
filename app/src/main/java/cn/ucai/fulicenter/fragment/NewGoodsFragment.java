package cn.ucai.fulicenter.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.NewGoodsBean;
import cn.ucai.fulicenter.utils.I;
import cn.ucai.fulicenter.utils.ImageLoader;
import cn.ucai.fulicenter.utils.L;
import cn.ucai.fulicenter.utils.OkHttpUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewGoodsFragment extends Fragment {
    int mNewState;
    static final int ACTION_DOWNLOAD = 0;
    static final int ACTION_PULL_UP = 1;
    static final int ACTION_PULL_DOWN = 2;
    static final int PAGE_SIZE = 10;
    int mPageId = 1;


    ArrayList<NewGoodsBean> mNewGoodsList;
    NewGoodsAdapter mNewGoodsAdapter;

    SwipeRefreshLayout mSwipeRefreshNewGoods;
    TextView mtvRefresh;
    RecyclerView mrvNewGoods;

    GridLayoutManager mLayoutManager;


    public NewGoodsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_goods, container, false);
        initView(view);
        downloadNewGoodsList(ACTION_DOWNLOAD, mPageId);
        setListener();
        return view;
    }

    private void setListener() {
        setOnPullUpListener();
        setOnPullDownListener();

    }

    private void setOnPullDownListener() {
        mSwipeRefreshNewGoods.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPageId = 1;
                mSwipeRefreshNewGoods.setEnabled(true);
                mSwipeRefreshNewGoods.setRefreshing(true);
                mtvRefresh.setVisibility(View.VISIBLE);
                downloadNewGoodsList(ACTION_PULL_DOWN,mPageId);
            }
        });

    }

    private void setOnPullUpListener() {
        mrvNewGoods.setOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastPosition;
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                mNewState = newState;
                lastPosition = mLayoutManager.findLastVisibleItemPosition();
                if (lastPosition >= mNewGoodsAdapter.getItemCount() - 1 && newState == RecyclerView.SCROLL_STATE_IDLE && mNewGoodsAdapter.isMore()) {
                    mPageId++;
                    downloadNewGoodsList(ACTION_PULL_UP, mPageId);
                }
                if (newState != RecyclerView.SCROLL_STATE_DRAGGING) {
                    mNewGoodsAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastPosition = mLayoutManager.findLastVisibleItemPosition();
            }
        });
    }

    private void downloadNewGoodsList(final int action, int pageId) {
        final OkHttpUtils<NewGoodsBean[]> utils = new OkHttpUtils<>();
        utils.url(I.SERVER_ROOT+I.REQUEST_FIND_NEW_BOUTIQUE_GOODS+I.QUESTION+"cat_id="+0)
                .addParam(I.PAGE_ID,pageId+"")
                .addParam(I.PAGE_SIZE,PAGE_SIZE+"")
                .targetClass(NewGoodsBean[].class)
                .execute(new OkHttpUtils.OnCompleteListener<NewGoodsBean[]>() {
                    @Override
                    public void onSuccess(NewGoodsBean[] result) {
                        for (NewGoodsBean n:result){
                          L.i(n.toString());}
                        ArrayList<NewGoodsBean> newGoodsList =  utils.array2List(result);
                        mNewGoodsAdapter.setMore(result != null && result.length > 0);
                        if (!mNewGoodsAdapter.isMore()) {
                            if (action == ACTION_PULL_UP) {
                                mNewGoodsAdapter.setTextFooter("没有更多新品...");

                            }
                            return;
                        }

                        mNewGoodsAdapter.setTextFooter("加载更多新品...");
                        switch (action) {
                            case ACTION_DOWNLOAD:
                                mNewGoodsAdapter.initNewGoodsList(newGoodsList);
                                break;
                            case ACTION_PULL_DOWN:
                                mNewGoodsAdapter.initNewGoodsList(newGoodsList);
                                mNewGoodsAdapter.setTextFooter("加载更多新品...");
                                mSwipeRefreshNewGoods.setRefreshing(false);
                                mtvRefresh.setVisibility(View.GONE);
                                ImageLoader.release();
                                break;
                            case ACTION_PULL_UP:
                                mNewGoodsAdapter.addNewGoodsList(newGoodsList);
                                break;
                        }
                    }

                    @Override
                    public void onError(String error) {

                    }
                });






    }

    private void initView(View view) {
        mSwipeRefreshNewGoods = (SwipeRefreshLayout) view.findViewById(R.id.srlNewGoods);
        mtvRefresh = (TextView) view.findViewById(R.id.tvRefresh);
        mrvNewGoods = (RecyclerView) view.findViewById(R.id.rvNewGoods);

        mNewGoodsList = new ArrayList<>();
        mNewGoodsAdapter = new NewGoodsAdapter(mNewGoodsList, getActivity());
        mLayoutManager = new GridLayoutManager(getActivity(), 2);
        mrvNewGoods.setAdapter(mNewGoodsAdapter);
        mrvNewGoods.setLayoutManager(mLayoutManager);

    }

    class FooterViewHolder extends RecyclerView.ViewHolder{
        TextView tvFooter;
        public FooterViewHolder(View itemView) {
            super(itemView);
            tvFooter = (TextView) itemView.findViewById(R.id.tvFooter);
        }
    }
    class NewGoodsViewHolder extends RecyclerView.ViewHolder{
        ImageView ivNewGoods;
        TextView tvGoodsName,tvGoodsPrice;
        public NewGoodsViewHolder(View itemView) {
            super(itemView);
            ivNewGoods = (ImageView) itemView.findViewById(R.id.ivNewGoods);
            tvGoodsName = (TextView) itemView.findViewById(R.id.tvGoodsName);
            tvGoodsPrice = (TextView) itemView.findViewById(R.id.tvGoodsPrice);
        }
    }
    class NewGoodsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        static final int TYPE_FOOTER = 1;
        static final int TYPE_TIME = 2;

        ArrayList<NewGoodsBean> newGoodsList;
        Context context;

        RecyclerView parent;
        String textFooter;
        boolean isMore;

        public NewGoodsAdapter(ArrayList<NewGoodsBean> newGoodsList, Context context) {
            this.newGoodsList = newGoodsList;
            this.context = context;
        }

        public boolean isMore() {
            return isMore;
        }

        public void setMore(boolean more) {
            isMore = more;
        }

        public void setTextFooter(String textFooter) {
            this.textFooter = textFooter;
            notifyDataSetChanged();
        }


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            this.parent = (RecyclerView) parent;
            LayoutInflater inflater = LayoutInflater.from(context);
            View layout=null;
            RecyclerView.ViewHolder holder=null;
            switch (viewType) {
                case TYPE_FOOTER:
                    layout = inflater.inflate(R.layout.item_footer, parent, false);
                    holder = new FooterViewHolder(layout);
                    break;
                case TYPE_TIME:
                    layout = inflater.inflate(R.layout.item_newgoods, parent, false);
                    holder = new NewGoodsViewHolder(layout);
                    break;

            }
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (position == getItemCount() - 1) {
                FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
                footerViewHolder.tvFooter.setText(textFooter);
                return;
            }
            NewGoodsViewHolder newGoodsViewHolder = (NewGoodsViewHolder) holder;
            NewGoodsBean newGoodsList = this.newGoodsList.get(position);
            newGoodsViewHolder.tvGoodsName.setText(newGoodsList.getGoodsName());
            newGoodsViewHolder.tvGoodsPrice.setText(newGoodsList.getCurrencyPrice());
            ImageLoader.build(I.DOWNLOAD_IMG_URL+newGoodsList.getGoodsImg())
                    .width(180)
                    .height(280)
                    .defaultPicture(R.mipmap.goods_thumb)
                    .listener(parent)
                    .imageView(newGoodsViewHolder.ivNewGoods)
                    .setDragging(mNewState!=RecyclerView.SCROLL_STATE_DRAGGING)
                    .showImage(context);

        }

        @Override
        public int getItemCount() {
            return newGoodsList == null ? 0 : newGoodsList.size() + 1;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == getItemCount() - 1) {
                return TYPE_FOOTER;
            }
                return TYPE_TIME;


        }

        public void initNewGoodsList(ArrayList<NewGoodsBean> newGoodsList) {
            this.newGoodsList.clear();
            this.newGoodsList.addAll(newGoodsList);
            notifyDataSetChanged();
        }
        public void addNewGoodsList(ArrayList<NewGoodsBean> newGoodsList) {
            this.newGoodsList.addAll(newGoodsList);
            notifyDataSetChanged();
        }
    }

}

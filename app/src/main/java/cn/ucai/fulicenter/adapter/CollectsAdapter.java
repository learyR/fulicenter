package cn.ucai.fulicenter.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.CollectBean;
import cn.ucai.fulicenter.utils.ImageLoader;
import cn.ucai.fulicenter.views.FooterViewHolder;

/**
 * Created by Administrator on 2016/10/25.
 */
public class CollectsAdapter extends RecyclerView.Adapter {
    ArrayList<CollectBean> newGoodsList;
    Context context;

    boolean isMore;
    RecyclerView parent;

    public boolean isMore() {
        return isMore;
    }

    public void setMore(boolean more) {
        isMore = more;
        notifyDataSetChanged();
    }

    public CollectsAdapter(ArrayList<CollectBean> newGoodsList, Context context) {
        this.newGoodsList = newGoodsList;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.parent = (RecyclerView) parent;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = null;
        RecyclerView.ViewHolder holder = null;
        switch (viewType) {
            case I.TYPE_FOOTER:
                view = inflater.inflate(R.layout.item_footer, parent, false);
                holder = new FooterViewHolder(view);
                break;
            case I.TYPE_ITEM:
                view = inflater.inflate(R.layout.item_collection_goods, parent, false);
                holder = new CollectsViewHolder(view);
                break;

        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position == getItemCount() - 1) {
            FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
            footerViewHolder.tvFooter.setText(getFooter());
            return;
        }
        CollectsViewHolder newGoodsViewHolder = (CollectsViewHolder) holder;
        CollectBean newGoods = newGoodsList.get(position);
        newGoodsViewHolder.tvGoodsName.setText(newGoods.getGoodsName());
        newGoodsViewHolder.ivDelete.setImageResource(R.mipmap.delete);
        ImageLoader.downloadImg(context, newGoodsViewHolder.ivNewGoods, newGoods.getGoodsThumb());
    }
    private int getFooter() {
        return isMore ? R.string.load_more : R.string.no_more;
    }

    @Override
    public int getItemCount() {
        return newGoodsList != null ? newGoodsList.size() + 1 : 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return I.TYPE_FOOTER;
        } else {
            return I.TYPE_ITEM;
        }
    }
    public void initData(ArrayList<CollectBean> goodsList) {
        if (newGoodsList != null) {
            this.newGoodsList.clear();
        }
        this.newGoodsList.addAll(goodsList);
        notifyDataSetChanged();
    }

    public void addData(ArrayList<CollectBean> goodsList) {
        this.newGoodsList.addAll(goodsList);
        notifyDataSetChanged();
    }
    class CollectsViewHolder extends RecyclerView.ViewHolder{
        @Bind(R.id.ivNewGoods)
        ImageView ivNewGoods;
        @Bind(R.id.tvGoodsName)
        TextView tvGoodsName;
        @Bind(R.id.ivDelete)
        ImageView ivDelete;

        public CollectsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

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
import cn.ucai.fulicenter.bean.BoutiqueBean;
import cn.ucai.fulicenter.utils.ImageLoader;
import cn.ucai.fulicenter.views.FooterViewHolder;

/**
 * Created by Administrator on 2016/10/18.
 */
public class BoutiqueAdapter extends RecyclerView.Adapter {
    Context context;
    ArrayList<BoutiqueBean> boutiqueList;

    boolean isMore;

    public boolean isMore() {
        return isMore;
    }

    public void setMore(boolean more) {
        isMore = more;
        notifyDataSetChanged();
    }

    public BoutiqueAdapter(Context context, ArrayList<BoutiqueBean> boutiqueList) {
        this.context = context;
        this.boutiqueList = boutiqueList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = null;
        RecyclerView.ViewHolder holder = null;
        switch (viewType) {
            case I.TYPE_FOOTER:
                view = inflater.inflate(R.layout.item_footer, parent, false);
                holder = new FooterViewHolder(view);
                break;
            case I.TYPE_ITEM:
                view = inflater.inflate(R.layout.item_boutique, parent, false);
                holder = new BoutiqueViewHolder(view);
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
        BoutiqueViewHolder boutiqueViewHolder = (BoutiqueViewHolder) holder;
        BoutiqueBean boutiqueBean = boutiqueList.get(position);
        boutiqueViewHolder.tvBoutiqueTitle.setText(boutiqueBean.getTitle());
        boutiqueViewHolder.tvBoutiqueName.setText(boutiqueBean.getName());
        boutiqueViewHolder.tvBoutiqueDescription.setText(boutiqueBean.getDescription());
        ImageLoader.downloadImg(context, boutiqueViewHolder.ivBoutique, boutiqueBean.getImageurl());
    }

    private int getFooter() {
        return isMore ? R.string.load_more : R.string.no_more;
    }

    @Override
    public int getItemCount() {
        return boutiqueList == null ? 0 : boutiqueList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return I.TYPE_FOOTER;
        } else {
            return I.TYPE_ITEM;
        }
    }

    public void initData(ArrayList<BoutiqueBean> boutiqueList) {
        if (this.boutiqueList != null) {
            this.boutiqueList.clear();
        }
        this.boutiqueList.addAll(boutiqueList);
        notifyDataSetChanged();
    }

    public void addData(ArrayList<BoutiqueBean> goodsList) {
        this.boutiqueList.addAll(goodsList);
        notifyDataSetChanged();
    }


    class BoutiqueViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_boutique_title)
        TextView tvBoutiqueTitle;
        @Bind(R.id.tv_boutique_name)
        TextView tvBoutiqueName;
        @Bind(R.id.tv_boutique_description)
        TextView tvBoutiqueDescription;
        @Bind(R.id.iv_boutique)
        ImageView ivBoutique;
        @Bind(R.id.layout_boutique)
        LinearLayout layoutBoutique;

        BoutiqueViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }
}

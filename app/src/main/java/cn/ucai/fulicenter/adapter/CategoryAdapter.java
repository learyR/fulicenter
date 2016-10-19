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
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.CategoryGroupBean;
import cn.ucai.fulicenter.utils.ImageLoader;

/**
 * Created by Administrator on 2016/10/19.
 */
public class CategoryAdapter extends RecyclerView.Adapter {
    Context context;
    ArrayList<CategoryGroupBean> mList;
    boolean isMore;

    public boolean isMore() {
        return isMore;
    }

    public void setMore(boolean more) {
        isMore = more;
        notifyDataSetChanged();
    }

    public CategoryAdapter(Context context, ArrayList<CategoryGroupBean> mList) {
        this.context = context;
        this.mList = mList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater in = LayoutInflater.from(context);
        View layout = in.inflate(R.layout.item_category, parent, false);
        RecyclerView.ViewHolder holder = new CategoryViewHolder(layout);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CategoryViewHolder categoryViewHolder = (CategoryViewHolder) holder;
        CategoryGroupBean categoryGroupBean = mList.get(position);
        categoryViewHolder.tvCategoryGroupName.setText(categoryGroupBean.getName());
        categoryViewHolder.ivCategoryGroupDown.setImageResource(R.drawable.arrow2_down);
        ImageLoader.downloadImg(context, categoryViewHolder.ivCategoryGroupImg, categoryGroupBean.getImageUrl());
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size() + 1;
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.ivCategory_Group_Img)
        ImageView ivCategoryGroupImg;
        @Bind(R.id.tvCategory_Group_Name)
        TextView tvCategoryGroupName;
        @Bind(R.id.ivCategory_Group_Down)
        ImageView ivCategoryGroupDown;
        @Bind(R.id.layout_category)
        LinearLayout layoutCategory;

        CategoryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

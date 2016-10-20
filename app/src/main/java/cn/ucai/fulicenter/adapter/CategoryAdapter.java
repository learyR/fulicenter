package cn.ucai.fulicenter.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.CategoryChildBean;
import cn.ucai.fulicenter.bean.CategoryGroupBean;
import cn.ucai.fulicenter.utils.ImageLoader;

/**
 * Created by Administrator on 2016/10/19.
 */
public class CategoryAdapter extends BaseExpandableListAdapter {
    Context context;
    ArrayList<CategoryGroupBean> groupList;
    ArrayList<ArrayList<CategoryChildBean>> childList;

    public CategoryAdapter(Context context, ArrayList<CategoryGroupBean> groupList, ArrayList<ArrayList<CategoryChildBean>> childList) {
        this.context = context;
        this.groupList = new ArrayList<>();
        this.groupList.addAll(groupList);
        this.childList = new ArrayList<>();
        this.childList.addAll(childList);
    }

    @Override
    public int getGroupCount() {
        return groupList != null ? groupList.size() : 0;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return childList != null && childList.get(groupPosition) != null ? childList.get(groupPosition).size() : 0;
    }

    @Override
    public CategoryGroupBean getGroup(int groupPosition) {
        return groupList != null ? groupList.get(groupPosition) : null;
    }

    @Override
    public CategoryChildBean getChild(int groupPosition, int childPosition) {
        return childList != null && childList.get(groupPosition) != null ? childList.get(groupPosition).get(childPosition) : null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        CategoryGroupViewHolder holder;
        if (convertView == null) {
            convertView = convertView.inflate(context, R.layout.item_category_group, null);
            holder = new CategoryGroupViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            convertView.getTag();
            holder = (CategoryGroupViewHolder) convertView.getTag();
        }
        CategoryGroupBean group = getGroup(groupPosition);
        if (group != null) {
            holder.ivCategoryGroupDown.setImageResource(isExpanded ? R.mipmap.expand_off : R.mipmap.expand_on);
            ImageLoader.downloadImg(context, holder.ivCategoryGroupImg, group.getImageUrl());
            holder.tvCategoryGroupName.setText(group.getName());
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        CategoryChildViewHolder holder;
        if (convertView == null) {
            convertView = convertView.inflate(context, R.layout.item_category_child, null);
            holder = new CategoryChildViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            convertView.getTag();
            holder = (CategoryChildViewHolder) convertView.getTag();
        }
        CategoryChildBean child = getChild(groupPosition, childPosition);
        if (child != null) {
            ImageLoader.downloadImg(context, holder.ivCategoryChildImg, child.getImageUrl());
            holder.tvCategoryChildName.setText(child.getName());
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    static class CategoryGroupViewHolder {
        @Bind(R.id.ivCategory_Group_Img)
        ImageView ivCategoryGroupImg;
        @Bind(R.id.tvCategory_Group_Name)
        TextView tvCategoryGroupName;
        @Bind(R.id.ivCategory_Group_Down)
        ImageView ivCategoryGroupDown;

        CategoryGroupViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    static class CategoryChildViewHolder {
        @Bind(R.id.ivCategory_child_Img)
        ImageView ivCategoryChildImg;
        @Bind(R.id.tvCategory_child_Name)
        TextView tvCategoryChildName;
        @Bind(R.id.layout_category_child)
        RelativeLayout layoutCategoryChild;

        CategoryChildViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

package cn.ucai.fulicenter.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.CartBean;
import cn.ucai.fulicenter.views.FooterViewHolder;


public class CartAdapter extends RecyclerView.Adapter {
    Context context;
    ArrayList<CartBean> cartList;

    boolean isMore;

    public boolean isMore() {
        return isMore;
    }

    public void setMore(boolean more) {
        isMore = more;
        notifyDataSetChanged();
    }

    public CartAdapter(Context context, ArrayList<CartBean> cartList) {
        this.context = context;
        this.cartList = cartList;
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
                view = inflater.inflate(R.layout.item_cart, parent, false);
                holder = new CartViewHolder(view);
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
        CartViewHolder boutiqueViewHolder = (CartViewHolder) holder;
        CartBean cartBean = cartList.get(position);

       /* boutiqueViewHolder.tvBoutiqueTitle.setText(boutiqueBean.getTitle());
        boutiqueViewHolder.tvBoutiqueName.setText(boutiqueBean.getName());
        boutiqueViewHolder.tvBoutiqueDescription.setText(boutiqueBean.getDescription());
        ImageLoader.downloadImg(context, boutiqueViewHolder.ivBoutique, boutiqueBean.getImageurl());
        boutiqueViewHolder.layoutBoutique.setTag(boutiqueBean);*/
    }

    private int getFooter() {
        return isMore ? R.string.load_more : R.string.no_more;
    }

    @Override
    public int getItemCount() {
        return cartList == null ? 0 : cartList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return I.TYPE_FOOTER;
        } else {
            return I.TYPE_ITEM;
        }
    }

    public void initData(ArrayList<CartBean> boutiqueList) {
        if (this.cartList != null) {
            this.cartList.clear();
        }
        this.cartList.addAll(boutiqueList);
        notifyDataSetChanged();
    }

    public void addData(ArrayList<CartBean> goodsList) {
        this.cartList.addAll(goodsList);
        notifyDataSetChanged();
    }


    class CartViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.cart_checkBox)
        CheckBox cartCheckBox;
        @Bind(R.id.ivCartGoods)
        ImageView ivCartGoods;
        @Bind(R.id.tvCartGoodsName)
        TextView tvCartGoodsName;
        @Bind(R.id.ivCartAdd)
        ImageView ivCartAdd;
        @Bind(R.id.tvCartCount)
        TextView tvCartCount;
        @Bind(R.id.ivCartDel)
        ImageView ivCartDel;
        @Bind(R.id.tvGoodsPrice)
        TextView tvGoodsPrice;
        @Bind(R.id.item_cart)
        RelativeLayout itemCart;

        CartViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
        @OnClick(R.id.item_cart)
        public void onCartClick() {
          /*  BoutiqueBean bean = (BoutiqueBean) layoutBoutique.getTag();
            MFGT.gotoBoutiqueChildActivity(context, bean);*/
        }
    }
}

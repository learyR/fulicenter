package cn.ucai.fulicenter.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import cn.ucai.fulicenter.bean.GoodsDetailsBean;
import cn.ucai.fulicenter.bean.MessageBean;
import cn.ucai.fulicenter.net.NetDao;
import cn.ucai.fulicenter.net.OkHttpUtils;
import cn.ucai.fulicenter.utils.ImageLoader;
import cn.ucai.fulicenter.utils.MFGT;
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
        CartViewHolder cartViewHolder = (CartViewHolder) holder;
        final CartBean cartBean = cartList.get(position);
        GoodsDetailsBean goods = cartBean.getGoods();
        if (goods != null) {
            ImageLoader.downloadImg(context, cartViewHolder.ivCartGoods, goods.getGoodsThumb());
            cartViewHolder.tvCartGoodsName.setText(goods.getGoodsName());
            cartViewHolder.tvGoodsPrice.setText(goods.getCurrencyPrice());
        }
        cartViewHolder.tvCartCount.setText(cartBean.getCount()+"");
        cartViewHolder.cartCheckBox.setChecked(cartBean.isChecked());
        cartViewHolder.cartCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                cartBean.setChecked(isChecked);
                context.sendBroadcast(new Intent(I.BROADCAST_UPDATE_CAST));
            }
        });
        cartViewHolder.ivCartAdd.setTag(position);
        cartViewHolder.ivCartDel.setTag(position);
        cartViewHolder.itemCart.setTag(cartBean.getGoodsId());
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
        this.cartList = boutiqueList;
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
        public void onItem(){
            int goodsId = (int) itemCart.getTag();
            MFGT.gotoGoodsDetailsActivity(context, goodsId);
        }
        @OnClick(R.id.ivCartAdd)
        public void onCartAdd() {
            final int position = (int) ivCartAdd.getTag();
            CartBean cart = cartList.get(position);
            NetDao.updateCart(context, cart.getId(), cart.getCount() + 1, new OkHttpUtils.OnCompleteListener<MessageBean>() {
                @Override
                public void onSuccess(MessageBean result) {
                    if (result.isSuccess()) {
                        cartList.get(position).setCount(cartList.get(position).getCount() + 1);
                        context.sendBroadcast(new Intent(I.BROADCAST_UPDATE_CAST));
                        tvCartCount.setText(cartList.get(position).getCount()+"");
                    }
                }

                @Override
                public void onError(String error) {

                }
            });
        }
        @OnClick(R.id.ivCartDel)
        public void onCartDel() {
            final int position = (int) ivCartDel.getTag();
            CartBean cart = cartList.get(position);
            if (cart.getCount() > 1) {
                NetDao.updateCart(context, cart.getId(), cart.getCount() - 1, new OkHttpUtils.OnCompleteListener<MessageBean>() {
                    @Override
                    public void onSuccess(MessageBean result) {
                        if (result.isSuccess()) {
                            cartList.get(position).setCount(cartList.get(position).getCount() - 1);
                            context.sendBroadcast(new Intent(I.BROADCAST_UPDATE_CAST));
                            tvCartCount.setText(cartList.get(position).getCount()+"");
                        }
                    }

                    @Override
                    public void onError(String error) {

                    }
                });
            } else {
                NetDao.deleteCart(context, cart.getId(), new OkHttpUtils.OnCompleteListener<MessageBean>() {
                    @Override
                    public void onSuccess(MessageBean result) {
                        if (result.isSuccess()) {
                            cartList.remove(position);
                            context.sendBroadcast(new Intent(I.BROADCAST_UPDATE_CAST));
                            notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onError(String error) {

                    }
                });
            }

        }
    }
}

package cn.ucai.fulicenter.net;

import android.content.Context;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.bean.BoutiqueBean;
import cn.ucai.fulicenter.bean.CategoryChildBean;
import cn.ucai.fulicenter.bean.CategoryGroupBean;
import cn.ucai.fulicenter.bean.GoodsDetailsBean;
import cn.ucai.fulicenter.bean.NewGoodsBean;
import cn.ucai.fulicenter.bean.Result;
import cn.ucai.fulicenter.utils.MD5;

public class NetDao {
    /**
     * 下载新品的网络请求
     * @param context
     * @param catId
     * @param pageId
     * @param listener
     */
    public static void downloadNewGoods(Context context,int catId, int pageId, OkHttpUtils.OnCompleteListener<NewGoodsBean[]> listener){
        OkHttpUtils<NewGoodsBean[]> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_FIND_NEW_BOUTIQUE_GOODS)
                .addParam(I.NewAndBoutiqueGoods.CAT_ID,String.valueOf(catId))
                .addParam(I.PAGE_ID,String.valueOf(pageId))
                .addParam(I.PAGE_SIZE,String.valueOf(I.PAGE_SIZE_DEFAULT))
                .targetClass(NewGoodsBean[].class)
                .execute(listener);
    }

    /**
     * 下载商品详情的网络请求
     * @param context
     * @param goodsId
     * @param listener
     */
    public static void downloadGoodsDetails(Context context, int goodsId, OkHttpUtils.OnCompleteListener<GoodsDetailsBean> listener){
        OkHttpUtils<GoodsDetailsBean> utils = new OkHttpUtils<>(context);
       utils.setRequestUrl(I.REQUEST_FIND_GOOD_DETAILS)
               .addParam(I.GoodsDetails.KEY_GOODS_ID,String.valueOf(goodsId))
               .targetClass(GoodsDetailsBean.class)
               .execute(listener);
    }

    /**
     * 下载精选的网络请求
     * @param context
     * @param listener
     */

    public static void downloadBoutique(Context context, OkHttpUtils.OnCompleteListener<BoutiqueBean[]> listener){
        OkHttpUtils<BoutiqueBean[]> utils = new OkHttpUtils<>(context);
       utils.setRequestUrl(I.REQUEST_FIND_BOUTIQUES)
               .targetClass(BoutiqueBean[].class)
               .execute(listener);
    }

    /**
     * 下载分类Group的请求
     * @param context
     * @param listener
     */
    public static void downloadCategoryGroup(Context context, OkHttpUtils.OnCompleteListener<CategoryGroupBean[]> listener){
        OkHttpUtils<CategoryGroupBean[]> utils = new OkHttpUtils<>(context);
       utils.setRequestUrl(I.REQUEST_FIND_CATEGORY_GROUP)
               .targetClass(CategoryGroupBean[].class)
               .execute(listener);
    }

    /**
     * 下载分类child的请求
     * @param context
     * @param parentID
     * @param listener
     */
    public static void downloadCategoryChild(Context context,int parentID, OkHttpUtils.OnCompleteListener<CategoryChildBean[]> listener){
        OkHttpUtils<CategoryChildBean[]> utils = new OkHttpUtils<>(context);
       utils.setRequestUrl(I.REQUEST_FIND_CATEGORY_CHILDREN)
               .addParam(I.CategoryChild.PARENT_ID,String.valueOf(parentID))
               .targetClass(CategoryChildBean[].class)
               .execute(listener);
    }

    /**
     * 下载分类child中物品详情的请求
     * @param context
     * @param catId
     * @param pageId
     * @param listener
     */
    public static void downloadCategoryChild(Context context,int catId, int pageId, OkHttpUtils.OnCompleteListener<NewGoodsBean[]> listener){
        OkHttpUtils<NewGoodsBean[]> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_FIND_GOODS_DETAILS)
                .addParam(I.NewAndBoutiqueGoods.CAT_ID,String.valueOf(catId))
                .addParam(I.PAGE_ID,String.valueOf(pageId))
                .addParam(I.PAGE_SIZE,String.valueOf(I.PAGE_SIZE_DEFAULT))
                .targetClass(NewGoodsBean[].class)
                .execute(listener);
    }

    /**
     * 注册账号
     * @param context
     * @param userName
     * @param nickName
     * @param password
     * @param listener
     */
    public static void register(Context context, String userName, String nickName, String password, OkHttpUtils.OnCompleteListener<Result> listener) {
        OkHttpUtils<Result> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_REGISTER)
                .addParam(I.User.USER_NAME, userName)
                .addParam(I.User.NICK, nickName)
                .addParam(I.User.PASSWORD, MD5.getMessageDigest(password))
                .targetClass(Result.class)
                .post()
                .execute(listener);
    }

    /**
     * 用户登录
     * @param context
     * @param userName
     * @param password
     * @param listener
     */
    public static void login(Context context, String userName, String password, OkHttpUtils.OnCompleteListener<String> listener) {
        OkHttpUtils<String> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_LOGIN)
                .addParam(I.User.USER_NAME, userName)
                .addParam(I.User.PASSWORD, MD5.getMessageDigest(password))
                .targetClass(String.class)
                .execute(listener);
    }
}

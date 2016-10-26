package cn.ucai.fulicenter.net;

import android.content.Context;

import java.io.File;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.bean.BoutiqueBean;
import cn.ucai.fulicenter.bean.CategoryChildBean;
import cn.ucai.fulicenter.bean.CategoryGroupBean;
import cn.ucai.fulicenter.bean.CollectBean;
import cn.ucai.fulicenter.bean.GoodsDetailsBean;
import cn.ucai.fulicenter.bean.MessageBean;
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

    /**
     * 更新用户昵称
     * @param context
     * @param userName
     * @param nick
     * @param listener
     */
    public static void updateNick(Context context, String userName, String nick, OkHttpUtils.OnCompleteListener<String> listener) {
        OkHttpUtils<String> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_UPDATE_USER_NICK)
                .addParam(I.User.USER_NAME, userName)
                .addParam(I.User.NICK, nick)
                .targetClass(String.class)
                .execute(listener);
    }

    /**
     * 跟新用户头像
     * @param context
     * @param userName
     * @param file
     * @param listener
     */
    public static void updateAvatar(Context context, String userName, File file, OkHttpUtils.OnCompleteListener<String> listener) {
        OkHttpUtils<String> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_UPDATE_AVATAR)
                .addParam(I.NAME_OR_HXID, userName)
                .addParam(I.AVATAR_TYPE,I.AVATAR_TYPE_USER_PATH)
                .addFile2(file)
                .targetClass(String.class)
                .post()
                .execute(listener);
    }

    /**
     * 更新用户信息
     * @param context
     * @param userName
     * @param listener
     */
    public static void syncUserInfo(Context context, String userName, OkHttpUtils.OnCompleteListener<String> listener) {
        OkHttpUtils<String> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_FIND_USER)
                .addParam(I.User.USER_NAME, userName)
                .targetClass(String.class)
                .execute(listener);
    }

    /**
     * 获取收藏数量
     * @param context
     * @param userName
     * @param listener
     */
    public static void getCollectsCount(Context context, String userName, OkHttpUtils.OnCompleteListener<MessageBean> listener) {
        OkHttpUtils<MessageBean> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_FIND_COLLECT_COUNT)
                .addParam(I.Collect.USER_NAME, userName)
                .targetClass(MessageBean.class)
                .execute(listener);
    }

    /**
     * 下载收藏的物品
     * @param context
     * @param userName
     * @param pageId
     * @param listener
     */
    public static void downloadCollects(Context context,String userName, int pageId, OkHttpUtils.OnCompleteListener<CollectBean[]> listener){
        OkHttpUtils<CollectBean[]> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_FIND_COLLECTS)
                .addParam(I.Collect.USER_NAME,userName)
                .addParam(I.PAGE_ID,String.valueOf(pageId))
                .addParam(I.PAGE_SIZE,String.valueOf(I.PAGE_SIZE_DEFAULT))
                .targetClass(CollectBean[].class)
                .execute(listener);
    }
}

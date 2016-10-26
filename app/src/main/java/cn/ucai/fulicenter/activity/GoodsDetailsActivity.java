package cn.ucai.fulicenter.activity;

import android.os.Bundle;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.AlbumsBean;
import cn.ucai.fulicenter.bean.GoodsDetailsBean;
import cn.ucai.fulicenter.bean.MessageBean;
import cn.ucai.fulicenter.bean.User;
import cn.ucai.fulicenter.net.NetDao;
import cn.ucai.fulicenter.net.OkHttpUtils;
import cn.ucai.fulicenter.utils.CommonUtils;
import cn.ucai.fulicenter.utils.L;
import cn.ucai.fulicenter.utils.MFGT;
import cn.ucai.fulicenter.views.FlowIndicator;
import cn.ucai.fulicenter.views.SlideAutoLoopView;

public class GoodsDetailsActivity extends BaseActivity {

    @Bind(R.id.tvEnglishName)
    TextView tvEnglishName;
    @Bind(R.id.tvGoodsName)
    TextView tvGoodsName;
    @Bind(R.id.tvGoodsPrice)
    TextView tvGoodsPrice;
    @Bind(R.id.salv)
    SlideAutoLoopView salv;
    @Bind(R.id.indicator)
    FlowIndicator indicator;
    @Bind(R.id.wbGoodsBrief)
    WebView wbGoodsBrief;
    int goodsId;
    GoodsDetailsActivity mContext;

    boolean isCollect = false;
    @Bind(R.id.ivGoodsDetailCollect)
    ImageView ivGoodsDetailCollect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_goods_details);
        ButterKnife.bind(this);
        goodsId = getIntent().getIntExtra(I.GoodsDetails.KEY_GOODS_ID, 0);
        L.i("details" + goodsId);
        if (goodsId == 0) {
            finish();
        }
        super.onCreate(savedInstanceState);
       /* initView();
        initData();
        setListener();
*/
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void initData() {
        NetDao.downloadGoodsDetails(mContext, goodsId, new OkHttpUtils.OnCompleteListener<GoodsDetailsBean>() {
            @Override
            public void onSuccess(GoodsDetailsBean result) {
                L.i("details" + result);
                if (result != null) {
                    showGoodDetails(result);
                } else {
                    finish();
                }
            }

            @Override
            public void onError(String error) {
                finish();
                L.e("details" + error);
                CommonUtils.showShortToast(error);
            }
        });
    }

    private void showGoodDetails(GoodsDetailsBean details) {
        tvEnglishName.setText(details.getGoodsEnglishName());
        tvGoodsName.setText(details.getGoodsName());
        tvGoodsPrice.setText(details.getCurrencyPrice());
        salv.startPlayLoop(indicator, getAlbumImgUrl(details), getAlbumImgCount(details));
        wbGoodsBrief.loadDataWithBaseURL(null, details.getGoodsBrief(), I.TEXT_HTML, I.UTF_8, null);
    }

    private int getAlbumImgCount(GoodsDetailsBean details) {
        if (details.getProperties() != null && details.getProperties().length > 0) {
            return details.getProperties()[0].getAlbums().length;
        }
        return 0;
    }

    private String[] getAlbumImgUrl(GoodsDetailsBean details) {
        String[] urls = new String[]{};
        if (details.getProperties() != null && details.getProperties().length > 0) {
            AlbumsBean[] albums = details.getProperties()[0].getAlbums();
            urls = new String[albums.length];
            for (int i = 0; i < albums.length; i++) {
                urls[i] = albums[i].getImgUrl();
            }
        }
        return urls;
    }

    @Override
    protected void initView() {
        mContext = this;
    }

    @OnClick(R.id.ivBack)
    public void onBackClick() {
        MFGT.finish(this);
    }

    //    public void onBackPressed(){
//        MFGT.finish(this);
//    }
    private void isCollect() {
        User user = FuLiCenterApplication.getUser();
        if (user != null) {
            NetDao.isCollect(mContext, user.getMuserName(), goodsId, new OkHttpUtils.OnCompleteListener<MessageBean>() {
                @Override
                public void onSuccess(MessageBean result) {
                    if (result != null && result.isSuccess()) {
                        isCollect = true;
                    } else {
                        isCollect = false;
                    }
                    updateGoodsCollectsStatus();
                }

                @Override
                public void onError(String error) {
                    isCollect = false;
                    updateGoodsCollectsStatus();
                }
            });
        }

    }

    private void updateGoodsCollectsStatus() {
        if (isCollect) {
            ivGoodsDetailCollect.setImageResource(R.mipmap.bg_collect_out);
        } else {
            ivGoodsDetailCollect.setImageResource(R.mipmap.bg_collect_in);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isCollect();
    }

    @OnClick(R.id.ivGoodsDetailCollect)
    public void onCollect() {
        User user = FuLiCenterApplication.getUser();
        if (user == null) {
            MFGT.gotoLoginActivity(mContext);
        } else {
            if (isCollect) {
                NetDao.deleteCollect(mContext, user.getMuserName(), goodsId, new OkHttpUtils.OnCompleteListener<MessageBean>() {
                    @Override
                    public void onSuccess(MessageBean result) {
                        if (result != null && result.isSuccess()) {
                            isCollect = !isCollect;
                            updateGoodsCollectsStatus();
                            CommonUtils.showShortToast("取消收藏成功");
                        }
                    }

                    @Override
                    public void onError(String error) {

                    }
                });
            } else {
                NetDao.addCollect(mContext, user.getMuserName(), goodsId, new OkHttpUtils.OnCompleteListener<MessageBean>() {
                    @Override
                    public void onSuccess(MessageBean result) {
                        if (result != null && result.isSuccess()) {
                            isCollect = !isCollect;
                            updateGoodsCollectsStatus();
                            CommonUtils.showShortToast("添加收藏成功");
                        }
                    }

                    @Override
                    public void onError(String error) {
                    }
                });
            }
        }
    }

    @OnClick(R.id.ivGoodsDetailShare)
    public void onClickShare() {
        showShare();
    }
    private void showShare() {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

// 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle("标题");
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("我是分享文本");
        //分享网络图片，新浪微博分享网络图片需要通过审核后申请高级写入接口，否则请注释掉测试新浪微博
        oks.setImageUrl("http://f1.sharesdk.cn/imgs/2014/02/26/owWpLZo_638x960.jpg");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite("ShareSDK");
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://sharesdk.cn");

// 启动分享GUI
        oks.show(this);
    }
}

package cn.ucai.fulicenter.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.AlbumsBean;
import cn.ucai.fulicenter.bean.GoodsDetailsBean;
import cn.ucai.fulicenter.net.NetDao;
import cn.ucai.fulicenter.net.OkHttpUtils;
import cn.ucai.fulicenter.utils.CommonUtils;
import cn.ucai.fulicenter.utils.L;
import cn.ucai.fulicenter.utils.MFGT;
import cn.ucai.fulicenter.views.FlowIndicator;
import cn.ucai.fulicenter.views.SlideAutoLoopView;

public class GoodsDetailsActivity extends AppCompatActivity {

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_details);
        ButterKnife.bind(this);
       goodsId = getIntent().getIntExtra(I.GoodsDetails.KEY_GOODS_ID, 0);
        L.i("details"+goodsId);
        if (goodsId == 0) {
            finish();
        }
        initView();
        initData();
        setListener();

    }

    private void setListener() {

    }

    private void initData() {
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
        salv.startPlayLoop(indicator,getAlbumImgUrl(details),getAlbumImgCount(details));
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
            for (int i=0;i<albums.length;i++) {
                urls[i] = albums[i].getImgUrl();
            }
        }
        return urls;
    }

    private void initView() {
        mContext = this;
    }
    @OnClick(R.id.ivBack)
    public  void onBackClick(){
        MFGT.finish(this);
    }
    public void onBackPressed(){
        MFGT.finish(this);
    }
}

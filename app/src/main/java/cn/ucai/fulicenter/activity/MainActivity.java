package cn.ucai.fulicenter.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.fragment.BoutiqueFragment;
import cn.ucai.fulicenter.fragment.GoodsFragment;
import cn.ucai.fulicenter.fragment.NewGoodsFragment;

public class MainActivity extends AppCompatActivity {



    @Bind(R.id.layout_new_good)
    RadioButton mLayoutNewGood;
    @Bind(R.id.layout_boutique)
    RadioButton mLayoutBoutique;
    @Bind(R.id.layout_category)
    RadioButton mLayoutCategory;
    @Bind(R.id.layout_cart)
    RadioButton mLayoutCart;
    @Bind(R.id.personal)
    RadioButton mPersonal;

    RadioButton[] mRbArray;
    int mIndex;

    Fragment[] mFragment;
    GoodsFragment mNewGoodsFragment;
    BoutiqueFragment mBoutiqueFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        //    L.i("MainActivity onCreate");
        initView();
        initFragment();

    }

    private void initFragment() {
        mFragment = new Fragment[5];
        mNewGoodsFragment = new GoodsFragment();
        mFragment[0] = mNewGoodsFragment;
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, mNewGoodsFragment)
                .show(mNewGoodsFragment)
                .commit();
        mBoutiqueFragment = new BoutiqueFragment();
        mFragment[1] = mBoutiqueFragment;

    }

    private void initView() {
        mRbArray=new RadioButton[5];
        mRbArray[0] = mLayoutNewGood;
        mRbArray[1] = mLayoutBoutique;
        mRbArray[2] = mLayoutCategory;
        mRbArray[3] = mLayoutCart;
        mRbArray[4] = mPersonal;

    }

    public void onCheckedChange(View view) {
        switch (view.getId()) {
            case R.id.layout_new_good:
                mIndex = 0;
               /* NewGoodsFragment newGoodsFragment = new NewGoodsFragment();
                mFragmentManager = getSupportFragmentManager();
                mFragmentTransaction = mFragmentManager.beginTransaction();
                mFragmentTransaction.replace(R.id.fragment_container, newGoodsFragment);
                mFragmentTransaction.commit();*/
                break;
            case R.id.layout_boutique:
                mIndex = 1;
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, mBoutiqueFragment)
                        .show(mBoutiqueFragment)
                        .commit();
                break;
            case R.id.layout_category:
                mIndex = 2;
                break;
            case R.id.layout_cart:
                mIndex = 3;
                break;
            case R.id.personal:
                mIndex = 4;
                break;
        }
        setRadioButtonStatus();
    }

    private void setRadioButtonStatus() {

        for (int i=0;i<mRbArray.length;i++) {
            if (i==mIndex) {
                mRbArray[i].setChecked(true);
            } else {
                mRbArray[i].setChecked(false);
            }
        }
    }
}

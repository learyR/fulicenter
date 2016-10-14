package cn.ucai.fulicenter.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.utils.L;

public class MainActivity extends AppCompatActivity {
    RadioButton mLayoutNewGood,mLayoutCart,mLayoutBoutique,mLayoutCategory, mLayoutPersonal;
    TextView mtvGoodsNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        L.i("MainActivity onCreate");
        initView();
    }

    private void initView() {
        mLayoutCart = (RadioButton) findViewById(R.id.layout_cart);
        mLayoutBoutique = (RadioButton) findViewById(R.id.layout_boutique);
        mLayoutCategory = (RadioButton) findViewById(R.id.layout_category);
        mLayoutPersonal = (RadioButton) findViewById(R.id.personal);
        mLayoutNewGood = (RadioButton) findViewById(R.id.layout_new_good);
        mtvGoodsNumber = (TextView) findViewById(R.id.tvGoodsNumber);
    }

    public void onCheckedChange(View view) {
        switch (view.getId()) {

        }
    }
}

package com.aosika.phonelive.player_lhc_costom;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.aosika.phonelive.R;
import com.aosika.phonelive.player_lhc_costom.bean.Videos;
import com.aosika.phonelive.player_lhc_costom.constant.Constant;
import com.aosika.phonelive.player_lhc_costom.fragment.UserInfoFragment;
import com.aosika.phonelive.player_lhc_costom.libs.adapter.MainAdapter;
import com.aosika.phonelive.player_lhc_costom.libs.fragment.VerticalNoLoopFragment;
import com.aosika.phonelive.player_lhc_costom.libs.fragment.VerticalNoLoopFragment3;
import com.aosika.phonelive.player_lhc_costom.libs.fragment.VerticalNoLoopFragment3One;
import com.aosika.phonelive.player_lhc_costom.libs.widght.HorizontalViewPager;
import com.aosika.phonelive.player_lhc_costom.simple.SimplePagerSelectListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mengyunfeng on 17/12/14.
 */

public class VerticalVideoPlayerActivity extends AppCompatActivity {

    private HorizontalViewPager viewpager;
    private MainAdapter mainAdapter;
    private List<Fragment> fragments=new ArrayList<>();
    private Videos mVideos;
    private Dialog mLoadingDialog;
    /**
     *
     * @param context
     * @param videos  Videobeans
     * @param flag  0:短视频；1：方言秀 2：我的视频
     */
    public static  void startVerticalVideoPlayerActivity(Context context,Videos videos,int flag){
        Intent intent = new Intent(context, com.aosika.phonelive.player_lhc_costom.VerticalVideoPlayerActivity.class);
        intent.putExtra("videos",videos);
        intent.putExtra("flag",flag);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_main);
        initDatas();
        initViewpagerDatas();
        initViewPager();
        initListener();
    }
    private void initDatas(){
        mVideos=getIntent().getParcelableExtra("videos");
        Constant.setFlag(getIntent().getIntExtra("flag",0));
    }
    private void initViewpagerDatas(){
        VerticalNoLoopFragment verticalLoopFragment=new VerticalNoLoopFragment();
        Bundle bundle=new Bundle();
        bundle.putParcelable("videos",mVideos);
        verticalLoopFragment.setArguments(bundle);
        fragments.add(verticalLoopFragment);
        UserInfoFragment userInfoFragment=new UserInfoFragment();
        Bundle bundleUser=new Bundle();
        bundleUser.putString("touid",mVideos.getVideoBeens().get(mVideos.getCurrentP()).getUid());
        userInfoFragment.setArguments(bundleUser);
        fragments.add(userInfoFragment);
    }

    private void initViewPager(){
        viewpager= (HorizontalViewPager) findViewById(R.id.viewpager);
        mainAdapter=new MainAdapter(getSupportFragmentManager(),fragments);
        viewpager.setAdapter(mainAdapter);
        viewpager.setOffscreenPageLimit(2);
    }
    private void initListener(){
        viewpager.addOnPageChangeListener(new SimplePagerSelectListener() {
            @Override
            public void onPageSelected(int position) {
                if(position==1){
                    (fragments.get(0)).onPause();
                }else if(position==0){
                    (fragments.get(0)).onResume();
                }
            }
        });
    }

    public void setCurrentItem(int item){
        viewpager.setCurrentItem(item,true);
    }
    public void setUserDatas(String touid){
        ((UserInfoFragment)fragments.get(1)).setDatas(touid);
    }



    @Override
    public void onBackPressed() {
        if(viewpager.getCurrentItem()==1){
            setCurrentItem(0);
        }else{
            super.onBackPressed();
        }

    }

    /**
     * 显示视频下载等待框
     */
    public void showLoadingDialog() {
        mLoadingDialog = new Dialog(this, R.style.loading_dialog);
        mLoadingDialog.setContentView(R.layout.dialog_loading);
        mLoadingDialog.setCanceledOnTouchOutside(false);
        mLoadingDialog.setCancelable(false);
        mLoadingDialog.show();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    /**
     * 隐藏视频下载等待框
     */
    public void hideLoadingDialog() {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
        }
        mLoadingDialog = null;
    }
}

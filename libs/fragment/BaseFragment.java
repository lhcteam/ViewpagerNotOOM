package com.aosika.phonelive.player_lhc_costom.libs.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by lhc on 17/12/16.
 * 懒加载
 */

public abstract class BaseFragment extends Fragment{

    protected boolean isViewCreated;//控件是否已初始化完成
    protected boolean isLoadDataCompleted;//数据是否加载完成
    protected boolean isDetachDataCompleted;//数据是否暂停完成

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.e("懒加载","-----setUserVisibleHint----"+isVisibleToUser+isViewCreated+!isLoadDataCompleted);
        if(isVisibleToUser&&isViewCreated){
            loadDatas();
        }else{
            detachDatas();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=onCreateView(inflater,container);
        Log.e("懒加载","-----onCreateView----");
        isViewCreated=true;
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.e("懒加载","-----onActivityCreated----"+getUserVisibleHint());
        if(getUserVisibleHint()){
            loadDatas();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("懒加载","-----onResume----");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("懒加载","-----onPause----");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e("懒加载","-----onDestroyView----");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.e("懒加载","-----onAttach----");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.e("懒加载","-----onDetach----");
    }

    /**
     * 播放
     */
    public void loadDatas(){
        isLoadDataCompleted=true;
        isDetachDataCompleted=false;
        loadData();
    }

    /**
     * 暂停
     */
    public void detachDatas(){
        isLoadDataCompleted=false;
        isDetachDataCompleted=true;
        detachData();
    }


    public abstract View onCreateView(LayoutInflater inflater, ViewGroup container);

    public abstract void loadData();

    public abstract void detachData();
}

package com.aosika.phonelive.player_lhc_costom.libs.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Administrator on 2017/12/15.
 */

public class VerticalNoLoopAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragments;

    public VerticalNoLoopAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.fragments=fragments;
    }


   /* public FPagerAdapter1(FragmentManager fm, List<Integer> types) {
        super(fm);
        this.mFragmentManager = fm;
        mFragmentList = new ArrayList<>();
        for (int i = 0, size = types.size(); i < size; i++) {
            mFragmentList.add(FragmentTest.instance(i));
        }
        setFragments(mFragmentList);
    }

    public void updateData(List<Integer> dataList) {
        ArrayList<Fragment> fragments = new ArrayList<>();
        for (int i = 0, size = dataList.size(); i < size; i++) {
            Log.e("FPagerAdapter1", dataList.get(i).toString());
            fragments.add(FragmentTest.instance(dataList.get(i)));
        }
        setFragments(fragments);
    }

    private void setFragments(ArrayList<Fragment> mFragmentList) {
        if(this.mFragmentList != null){
            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
            for(Fragment f:this.mFragmentList){
                fragmentTransaction.remove(f);
            }
            fragmentTransaction.commit();
            mFragmentManager.executePendingTransactions();
        }
        this.mFragmentList = mFragmentList;
        notifyDataSetChanged();
    }
*/


    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        System.out.println(">>>>>>>>>instantiateItem-----"+position);
        return super.instantiateItem(container, position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        System.out.println(">>>>>>>>>destroyItem-----"+position);
        super.destroyItem(container, position, object);

    }
}

package com.aosika.phonelive.player_lhc_costom.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.aosika.phonelive.bean.VideoBean;

import java.util.ArrayList;

/**
 * Created by mengyunfeng on 17/12/18.
 */

public class Videos implements Parcelable {
    private int page;
    private int currentP;
    private ArrayList<VideoBean> VideoBeens;

    public int getPage() {
        return page;
    }

    public Videos setPage(int page) {
        this.page = page;
        return this;
    }

    public int getCurrentP() {
        return currentP;
    }

    public Videos setCurrentP(int currentP) {
        this.currentP = currentP;
        return this;
    }

    public ArrayList<VideoBean> getVideoBeens() {
        return VideoBeens;
    }

    public Videos setVideoBeens(ArrayList<VideoBean> videoBeens) {
        VideoBeens = videoBeens;
        return this;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.page);
        dest.writeInt(this.currentP);
        dest.writeTypedList(this.VideoBeens);
    }

    public Videos() {
    }

    protected Videos(Parcel in) {
        this.page = in.readInt();
        this.currentP = in.readInt();
        this.VideoBeens = in.createTypedArrayList(VideoBean.CREATOR);
    }

    public static final Parcelable.Creator<Videos> CREATOR = new Parcelable.Creator<Videos>() {
        @Override
        public Videos createFromParcel(Parcel source) {
            return new Videos(source);
        }

        @Override
        public Videos[] newArray(int size) {
            return new Videos[size];
        }
    };
}

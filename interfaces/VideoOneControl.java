package com.aosika.phonelive.player_lhc_costom.interfaces;

import com.aosika.phonelive.bean.VideoBean;

/**
 * Created by mengyunfeng on 18/1/5.
 */

public interface VideoOneControl {

    /**
     * 播放
     */
    void play();

    /**
     * 重新播放
     */
    void replay();

    /**
     * 暂停
     */
    void pause();

    /**
     * 重新加载
     */
    void reloadDatas(VideoBean videoBean);
}

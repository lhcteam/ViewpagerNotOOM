package com.aosika.phonelive.player_lhc_costom.interfaces;

/**
 * Created by mengyunfeng on 17/12/21.
 */

public interface VideoControlListener{
    /**
     * 播放
     */
    void play();

    /**
     * 暂停
     */
    void pasue();

    /**
     * 停止，进度重置
     */
    void stop();

    /**
     * 重新从头播放
     */
    void replay();
}

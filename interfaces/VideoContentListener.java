package com.aosika.phonelive.player_lhc_costom.interfaces;

/**
 * Created by mengyunfeng on 17/12/21.
 */

public interface VideoContentListener extends VideoControlListener{

    /**
     * 获取是否点赞过  1:已点赞
     * @return
     */
    int getIsLike();
    /**
     * 点赞
     */
    void thumbsUp(ThumbUpListener thumbUpListener);

    /**
     * 分享
     * "qq", 0
     *"qzone", 1
     *"wx", 2
     *"wchat",  3
     *"facebook",  4
     *"twitter"  5
     */
    void share(int shareId);
}

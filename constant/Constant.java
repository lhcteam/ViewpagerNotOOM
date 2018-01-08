package com.aosika.phonelive.player_lhc_costom.constant;

/**
 * Created by mengyunfeng on 17/12/16.
 */

public class Constant {
    public static int mCurrentP=0;
    /**
     * 分享
     * "qq", 0
     *"qzone", 1
     *"wx", 2
     *"wchat",  3
     *"facebook",  4
     *"twitter"  5
     */
    public static final int wx=0;
    public static final int wchat=1;
    public static final int qzone=2;
    public static final int qq=3;
    public static final int twitter=4;
    public static final int facebook=5;


    private static  long  timePlay;//毫秒
    private static  int  flag;//0短视频；1方言秀 2:我的视频

    public static long getTimePlay() {
        System.out.println(">>>>>>----gettime"+timePlay);
        return timePlay;
    }
    public static void setTimePlay(long timePlay) {
        System.out.println(">>>>>>----settime"+timePlay);
        if(timePlay==0){
            return;
        }
        Constant.timePlay = timePlay;
    }
    public static void clearTime(){
        Constant.timePlay = 0;
    }

    public static int getFlag() {
        return flag;
    }

    public static void setFlag(int flag) {
        Constant.flag = flag;
    }


    public static int FLAG_VIDEO=0;//短视频
    public static int FLAG_DIALECT=1;//方言秀
    public static int FLAG_MYVIDEO=2;//我的视频列表
    public static int FLAG_OSCAR=3;//奥斯卡列表



    public static int FLAG_HOME=0;//首页
    public static int FLAG_MYUEGE=1;//我的催更


}

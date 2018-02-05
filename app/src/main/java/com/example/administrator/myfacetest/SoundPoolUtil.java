package com.example.administrator.myfacetest;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;


/**
 * Created by Administrator on 2017/12/27.
 */

public class SoundPoolUtil {
    private static SoundPoolUtil soundPoolUtil;
    private static SoundPool soundPool;


    //单例模式
    public static SoundPoolUtil getInstance(Context context) {
        if (soundPoolUtil == null)
            soundPoolUtil = new SoundPoolUtil(context);
        return soundPoolUtil;
    }

    private SoundPoolUtil(Context context) {
        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 10);
        //加载音频文件
        soundPool.load(context, R.raw.please_remake, 1);//4.请对准摄像头，再次拍照
        soundPool.load(context, R.raw.repeat_enter, 2);//3.重复刷卡，请退出通道
        soundPool.load(context, R.raw.ticket_error, 3);//2.无效票证，请退出通道
        soundPool.load(context, R.raw.ticket_right, 4);//1.验证通过，请通行

    }

    public static void play(int number) {
        Log.d("tag", "number " + number);
        //播放音频
        soundPool.play(number, 0.9f, 0.9f, 0, 0, 1);
    }
}

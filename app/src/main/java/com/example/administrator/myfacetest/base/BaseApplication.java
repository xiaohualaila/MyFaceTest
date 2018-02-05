/*
 * Copyright 2017 GcsSloop
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Last modified 2017-03-11 22:24:54
 *
 * GitHub:  https://github.com/GcsSloop
 * Website: http://www.gcssloop.com
 * Weibo:   http://weibo.com/GcsSloop
 */

package com.example.administrator.myfacetest.base;

import android.app.Application;
import android.content.Context;

import com.example.administrator.myfacetest.SoundPoolUtil;

import yuweifacecheck.YuweiFaceHelper;


public class BaseApplication extends Application {

    private static Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        SoundPoolUtil.getInstance(this);
        //初始化人脸识别Key
        YuweiFaceHelper.SetKey("DNPGqA8zJ5MHY6od3nS7yp9scsWovbDq9YNwrvHHP33r",
                "BWw95ct8iFUGVV1RwbPZEwYjoZvRrF6op6yTUwSzRq2Q",
                "BWw95ct8iFUGVV1RwbPZEwYrxyBdc6xgstu5zTamkXTQ",
                "BWw95ct8iFUGVV1RwbPZEwYz8NSnQ42BhTTaKqMHfKvG",
                "BWw95ct8iFUGVV1RwbPZEwZbwNkfPr2w3jZbMYyfUKAw",
                "BWw95ct8iFUGVV1RwbPZEwZj6n1no37RU3vw8UsPGPXZ");
    }
    public static Context getContext() {
        return mContext;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        System.out.println("App.onTerminate");
    }
}

package com.example.administrator.myfacetest.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.TextView;
import com.arcsoft.facerecognition.AFR_FSDKFace;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.cmm.rkgpiocontrol.rkGpioControlNative;
import com.decard.NDKMethod.BasicOper;
import com.example.administrator.myfacetest.FileUtil;
import com.example.administrator.myfacetest.MyUtil;
import com.example.administrator.myfacetest.R;
import com.example.administrator.myfacetest.RoundImageView;
import com.example.administrator.myfacetest.SoundPoolUtil;
import com.example.administrator.myfacetest.Ticket;
import com.example.administrator.myfacetest.base.BaseActivity;
import com.example.administrator.myfacetest.base.ViewHolder;
import com.example.administrator.myfacetest.retrofit.Api;
import com.example.administrator.myfacetest.retrofit.ConnectUrl;
import com.example.administrator.myfacetest.rx.RxBus;
import com.example.administrator.myfacetest.service.Service2;
import com.guo.android_extend.widget.CameraFrameData;
import com.guo.android_extend.widget.CameraGLSurfaceView;
import com.guo.android_extend.widget.CameraSurfaceView;
import org.json.JSONObject;
import java.io.File;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import yuweifacecheck.YuweiFaceHelper;

public class MainActivity extends BaseActivity implements SurfaceHolder.Callback, CameraSurfaceView.OnCameraListener, View.OnTouchListener, Camera.AutoFocusCallback {
    RoundImageView img_server;
    TextView flag_tag;
    boolean isCanRegister = false;
    CameraSurfaceView mSurfaceView;
    CameraGLSurfaceView mGLSurfaceView;
    private AFR_FSDKFace mAFR_FSDKFace;
    int mCameraRotate;
    YuweiFaceHelper.YuweiCheckLooper mYuweiLooper = null;
    YuweiFaceHelper mHelper;
    private IDCardHandler idCardHandler;
    private Rect src = new Rect();
    private Rect dst = new Rect();

    private boolean isRquest = false;
    private int type;
    private String ticketNum;
    private boolean isOpenDoor = false;
    private boolean isLight = false;

    private Handler handler = new Handler();

    private Bitmap bitmap;

    private boolean  isCheckSuccess = false;
    @Override
    protected void initViews(ViewHolder holder, View root) {
        img_server = holder.get(R.id.img_server);
        flag_tag = holder.get(R.id.state_tip);
        mSurfaceView = holder.get(R.id.surfaceView);
        mGLSurfaceView = holder.get(R.id.glsurfaceView);

        initFace();
        mGLSurfaceView.setOnTouchListener(this);
        mSurfaceView.setOnCameraListener(this);
        mSurfaceView.setupGLSurafceView(mGLSurfaceView, true, mHelper.getCameraMirror(), mHelper.getCameraRotate());
        mSurfaceView.debug_print_fps(true, false);

        mCameraRotate = mHelper.getCameraRotate();
        RxBus.getDefault().toObserverable(Ticket.class).subscribe(myMessage -> {
            if (!isRquest) {
                type = myMessage.getType();
                if (type != 2) {
                    BasicOper.dc_beep(5);
                }
                ticketNum = myMessage.getNum().trim();

                isRquest = true;
                Log.i("faceListSize", "faceList = " + mHelper.getRegisterList().size());
                mHelper.deleteAllFace();
                Log.i("faceListSize", "faceList = " + mHelper.getRegisterList().size());
                Log.i("sss",">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
           //     upload();

                /////////////////////////////////////////////////////////临时测试
                String imageStr = FileUtil.getPath() + File.separator +  "xiaohu.png";
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        doSuccess(imageStr);
                    }
                });

                isCheckSuccess = false;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //   mHelper.deleteName(ticketNum);
                        if(isCheckSuccess){
                            checkSuccess();
                        }else {
                            doFaceError();
                        }
                    }
                },4000);

                ///////////////////////////////////////////////
            }
        });

//        int memClass = ((ActivityManager)getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
//        Log.i("sss", "memClass:"+memClass);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }


    //初始化人脸识别
    private void initFace() {
        mHelper = YuweiFaceHelper.getInstance(this);
        //0 为后置摄像头， 1为前置
        mHelper.detecterInit(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        idCardHandler = new IDCardHandler();
        mYuweiLooper = mHelper.getLooper(idCardHandler);
        startService(new Intent(this,Service2.class));
    }

    @Override
    public void onAutoFocus(boolean success, Camera camera) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mHelper.setSurfaceHolder(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mHelper.registerSurfaceDestroyed();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    @Override
    public Camera setupCamera() {
        return mHelper.setupCameraHandle();
    }

    @Override
    public void setupChanged(int format, int width, int height) {

    }

    @Override
    public boolean startPreviewLater() {
        return false;
    }

    @Override
    public Object onPreview(byte[] data, int width, int height, int format, long timestamp) {
        return mHelper.onPreviewHandler(data, width, height, format, timestamp, idCardHandler);
    }

    @Override
    public void onBeforeRender(CameraFrameData data) {

    }

    @Override
    public void onAfterRender(CameraFrameData data) {
        mGLSurfaceView.getGLES2Render().draw_rect((Rect[]) data.getParams(), Color.GREEN, 2);
    }

    @Override
    public void onClick(View v) {

    }

    @SuppressLint("HandlerLeak")
    private class IDCardHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    isCanRegister = false;
                    Log.i("TAG","bitmapWidth = " + bitmap.getWidth() + "   bitmapHeight= " + bitmap.getHeight());
                    src.set(0, 0, bitmap.getWidth(), bitmap.getHeight());
                    mHelper.startRegisterThread(bitmap, idCardHandler, src, dst);
                    Log.i("TAG", "已启动注册");
                    break;
                case YuweiFaceHelper.MSG_CODE:
                    if (isCanRegister) return;
                    Log.i("TAG", "sg.arg1 = " + msg.arg1);
                    if (msg.arg1 == YuweiFaceHelper.MSG_EVENT_REG) {
                        isCanRegister = true;
                        mAFR_FSDKFace = (AFR_FSDKFace) msg.obj;
                            //调用人脸识别
                            startDetecter();
                    } else if (msg.arg1 == YuweiFaceHelper.MSG_EVENT_NO_FEATURE) {
                        toastShort("人脸特征无法检测，请换一张图片");
                    } else if (msg.arg1 == YuweiFaceHelper.MSG_EVENT_NO_FACE) {
                        toastShort("没有检测到人脸，请换一张图片");
                    } else if (msg.arg1 == YuweiFaceHelper.MSG_EVENT_FD_ERROR) {
                        toastShort("FD初始化失败，错误码：" + msg.arg2);
                    } else if (msg.arg1 == YuweiFaceHelper.MSG_EVENT_FR_ERROR) {
                        toastShort("FR初始化失败，错误码：" + msg.arg2);
                    }
                    break;

                case YuweiFaceHelper.CHECK_FACE_SUCCESS:
                    try {

                        Bundle bundle = msg.getData();
                        float max_score = bundle.getFloat(YuweiFaceHelper.MAX_SCORE);
                        String showName = bundle.getString(YuweiFaceHelper.SHOW_NAME);
                        Bitmap showBitmap = bundle.getParcelable(YuweiFaceHelper.SHOW_BITMAP);
                        Log.i("xxx", "max_score = " + max_score);
                        Log.i("xxx", "置信度：" + (float) ((int) (max_score * 1000)) / 1000.0);
                        isCheckSuccess = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case YuweiFaceHelper.CHECK_FACE_FAILURE:
                        Log.i("xxx", "验证失败 max_score = mMax_score》》》》》" );
                    break;

                case YuweiFaceHelper.POST_DELAYDE_HIDE:
                    // mHandler.postDelayed(hide, 3000);
                    break;
            }
        }
    }

    /**
     * 人脸特征对比
     */
    private void startDetecter() {
        mSurfaceView.setupGLSurafceView(mGLSurfaceView, true, mHelper.getCameraMirror(), mHelper.getCameraRotate());
        mSurfaceView.debug_print_fps(true, false);
        //启动比对
        mHelper.addRegisterFace(ticketNum, mAFR_FSDKFace);
        //启动比对
        mHelper.loadInfo();

    }

    @Override
    protected void onPause() {
        super.onPause();
        mYuweiLooper.shutdown();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, Service2.class));
    }


    /**
     * 上传信息
     */
    private void upload() {
        boolean isNetAble = MyUtil.isNetworkAvailable(this);
        if (!isNetAble) {
            toastLong("网路无法连接!");
            uploadFinish();
            return;
        }
        Api.getBaseApiWithOutFormat(ConnectUrl.URL)
                .uploadPhotoBase("1", type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<JSONObject>() {
                               @Override
                               public void call(JSONObject jsonObject) {
                                   jsonObjectResult(jsonObject);
                                   isCheckSuccess = false;
                                   handler.postDelayed(new Runnable() {
                                       @Override
                                       public void run() {
                                        //   mHelper.deleteName(ticketNum);
                                         if(isCheckSuccess){
                                             checkSuccess();
                                         }else {
                                             doFaceError();
                                         }
                                       }
                                   },3000);
                               }
                           }, new Action1<Throwable>() {
                               @Override
                               public void call(Throwable throwable) {
                                   doError();
                               }
                           }
                );
    }


    private void jsonObjectResult(JSONObject jsonObject) {
        if (jsonObject != null) {
            String result = jsonObject.optString("code");
            if (!TextUtils.isEmpty(result)) {
                if (result.equals("1")) {
                    String imageStr = jsonObject.optString("photo");
                    doSuccess(imageStr);
                }
            }
        }
    }

    public void doFaceError() {
        flag_tag.setText("人脸验证失败");
        flag_tag.setTextColor(getResources().getColor(R.color.red));
        rkGpioControlNative.ControlGpio(20, 0);//亮灯
        isLight = true;
        SoundPoolUtil.play(1);
        uploadFinish();
    }

    public void doError() {
        flag_tag.setText("验证失败");
        flag_tag.setTextColor(getResources().getColor(R.color.red));
        rkGpioControlNative.ControlGpio(20, 0);//亮灯
        isLight = true;
        SoundPoolUtil.play(3);
        uploadFinish();
    }

    public void doSuccess(String Face_path) {
        if (!TextUtils.isEmpty(Face_path)) {
            RequestOptions options = new RequestOptions()
                    .error(R.drawable.pic_bg)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE);
            Glide.with(this)
                    .asBitmap()
                    .load(Face_path)
                    .apply(options)
                    .into(target);
        }
    }

    private SimpleTarget target = new SimpleTarget<Bitmap>() {
        @Override
        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
            bitmap = resource;
            img_server.setImageBitmap(bitmap);
            idCardHandler.sendEmptyMessage(0);
        }
    };

    private void checkSuccess() {
        isOpenDoor = true;
        rkGpioControlNative.ControlGpio(1, 0);//开门
        SoundPoolUtil.play(4);
        flag_tag.setText("验证成功");
        flag_tag.setTextColor(getResources().getColor(R.color.green));
        uploadFinish();
    }

    /**
     * 0.5秒关门
     */
    private void uploadFinish() {
        if (isOpenDoor) {
            isOpenDoor = false;
            handler.postDelayed(runnable, 500);
        }

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                isRquest = false;
                img_server.setImageResource(R.drawable.pic_bg);
                flag_tag.setText("");
                //变灯
                if (isLight) {
                    rkGpioControlNative.ControlGpio(20, 1);
                    isLight = false;
                }
            }
        }, 2500);

    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            rkGpioControlNative.ControlGpio(1, 1);//关门
        }
    };






}

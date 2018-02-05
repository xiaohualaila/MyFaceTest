package com.example.administrator.myfacetest.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.arcsoft.facerecognition.AFR_FSDKFace;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.administrator.myfacetest.FileUtil;
import com.example.administrator.myfacetest.R;
import com.example.administrator.myfacetest.RoundImageView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import yuweifacecheck.YuweiFaceHelper;


/**
 * Created by dhht on 16/9/29.
 */

public class CameraActivity5 extends Activity implements SurfaceHolder.Callback {
    @BindView(R.id.camera_sf)
    SurfaceView camera_sf;
//    @BindView(R.id.img1)
//    RoundImageView img1;
    @BindView(R.id.img_server)
   RoundImageView img_server;

    @BindView(R.id.state_tip)
    TextView flag_tag;
    @BindView(R.id.dophoto)
    ImageView dophoto;
    private Camera camera;
    private String filePath;
    private SurfaceHolder holder;
    private boolean isFrontCamera = true;
    private int width = 640;
    private int height = 480;

    private boolean isOpenDoor = false;
    private boolean isLight = false;
    private Handler handler = new Handler();
    //串口
    private boolean isReading = false;

    YuweiFaceHelper mHelper;

    private Rect src = new Rect();
    private Rect dst = new Rect();

    private IDCardHandler idCardHandler;
    boolean isCanRegister = false;
    private AFR_FSDKFace mAFR_FSDKFace;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_camera5);
        ButterKnife.bind(this);

        holder = camera_sf.getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        initFace();
        dophoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isReading){
                    takePhoto();
                    isReading = true;
                }
            }
        });
    }

    //初始化人脸识别
    private void initFace() {
        mHelper = YuweiFaceHelper.getInstance(this);
        //0 为后置摄像头， 1为前置
        mHelper.detecterInit(0);
    }

    private void takePhoto() {
        camera.takePicture(null, null, jpeg);
    }

    private Camera.PictureCallback jpeg = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            stopPreview();
            filePath = FileUtil.getPath() + File.separator + FileUtil.getTime() + ".jpeg";
            Matrix matrix = new Matrix();
            matrix.reset();
            matrix.postRotate(0);
            BitmapFactory.Options factory = new BitmapFactory.Options();
            factory = setOptions(factory);
            Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length, factory);
            Bitmap bm1 = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
            BufferedOutputStream bos = null;
            try {
                bos = new BufferedOutputStream(new FileOutputStream(new File(filePath)));
                bm1.compress(Bitmap.CompressFormat.JPEG, 30, bos);
                bos.flush();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (bos != null) {
                    try {
                        bos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                bm.recycle();
                bm1.recycle();
               Message message =  new Message();
                message.what = 0;
                idCardHandler.handleMessage(message);

            }
        }
    };


    public static BitmapFactory.Options setOptions(BitmapFactory.Options opts) {
        opts.inJustDecodeBounds = false;
        opts.inPurgeable = true;
        opts.inInputShareable = true;
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
        opts.inSampleSize = 1;
        return opts;
    }

    @Override
    protected void onResume() {
        super.onResume();
        camera = openCamera();
        idCardHandler = new IDCardHandler();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeCamera();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException exception) {

        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stopPreview();
    }

    private Camera openCamera() {
        if (camera == null) {
            try {
                camera = Camera.open();
            } catch (Exception e) {
                camera = null;
                e.printStackTrace();
            }
        }
        return camera;
    }

    private void startPreview() {
        Camera.Parameters para;
        if (null != camera) {
            para = camera.getParameters();
        } else {
            return;
        }
        para.setPreviewSize(width, height);
        setPictureSize(para, 640, 480);
        para.setPictureFormat(ImageFormat.JPEG);//设置图片格式
        setCameraDisplayOrientation(isFrontCamera ? 0 : 1, camera);
        camera.setParameters(para);
        camera.startPreview();
    }

    /* 停止预览 */
    private void stopPreview() {
        if (camera != null) {
            try {
                camera.stopPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setCameraDisplayOrientation(int cameraId, Camera camera) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = this.getWindowManager().getDefaultDisplay().getRotation();
        rotation = 0;
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;
        } else {
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    private void setPictureSize(Camera.Parameters para, int width, int height) {
        int absWidth = 0;
        int absHeight = 0;
        List<Camera.Size> supportedPictureSizes = para.getSupportedPictureSizes();
        for (Camera.Size size : supportedPictureSizes) {
            if (Math.abs(width - size.width) < Math.abs(width - absWidth)) {
                absWidth = size.width;
            }
            if (Math.abs(height - size.height) < Math.abs(height - absHeight)) {
                absHeight = size.height;
            }
        }
        para.setPictureSize(absWidth, absHeight);
    }

    private void closeCamera() {
        if (null != camera) {
            try {
                camera.setPreviewDisplay(null);
                camera.setPreviewCallback(null);
                camera.release();
                camera = null;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadFinish() {

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startPreview();
                img_server.setImageResource(R.drawable.pic_bg);
                flag_tag.setText("");
                File file = new File(filePath);
                if (file.exists()) {
                    file.delete();
                }


            }
        }, 2500);

        isReading = false;
    }

    Bitmap bitmap;
    @SuppressLint("HandlerLeak")
    private class IDCardHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    isCanRegister = false;
                    mHelper.deleteAllFace();
                    bitmap = BitmapFactory.decodeFile(filePath);
                    src.set(0, 0, bitmap.getWidth(), bitmap.getHeight());
                    mHelper.startRegisterThread(bitmap, idCardHandler, src, dst);
                    Log.i("TAG", "已启动注册");
                    break;
                case YuweiFaceHelper.MSG_CODE:

                    if (isCanRegister) return;
                    Log.i("TAG", "sg.arg1 = " + msg.arg1);
                    if (msg.arg1 == YuweiFaceHelper.MSG_EVENT_REG) {
                        isCanRegister = true;
                        img_server.setImageBitmap(bitmap);
                        RequestOptions options = new RequestOptions()
                                .error(R.drawable.pic_bg);
                        Glide.with(CameraActivity5.this).load(filePath).apply(options).into(img_server);
                        //调用人脸识别
                        Toast.makeText(CameraActivity5.this, "人脸检测成功！", Toast.LENGTH_SHORT).show();
                    } else if (msg.arg1 == YuweiFaceHelper.MSG_EVENT_NO_FEATURE) {
                        Toast.makeText(CameraActivity5.this, "人脸特征无法检测，请换一张图片", Toast.LENGTH_SHORT).show();
                    } else if (msg.arg1 == YuweiFaceHelper.MSG_EVENT_NO_FACE) {
                        Toast.makeText(CameraActivity5.this, "没有检测到人脸，请换一张图片", Toast.LENGTH_SHORT).show();
                    } else if (msg.arg1 == YuweiFaceHelper.MSG_EVENT_FD_ERROR) {
                        Toast.makeText(CameraActivity5.this, "FD初始化失败，错误码：" + msg.arg2, Toast.LENGTH_SHORT).show();
                    } else if (msg.arg1 == YuweiFaceHelper.MSG_EVENT_FR_ERROR) {
                        Toast.makeText(CameraActivity5.this, "FR初始化失败，错误码：" + msg.arg2, Toast.LENGTH_SHORT).show();
                    }
                    uploadFinish();
                    break;

            }
        }
    }

}

package com.example.myapplication;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.Manifest;

import android.content.pm.PackageManager;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback  {
    private MediaRecorder mediaRecorder;
    private MediaPlayer mPlayer;
    private SurfaceView sv;
    private SurfaceHolder sh;
    Button cambtn;
    Button playBtn;
    TextView testmsg;
    private Camera mcam;
    String vidPath ;
    boolean recording = false; //초기는 녹화중이 아님
    boolean hasVideo = false;
    boolean isPlaying = false;

    //이 아래는 surface holder.callback 의 인터페이스들...
    public void surfaceCreated(SurfaceHolder holder) {
        if (mcam == null) {
            try {
                mcam.setPreviewDisplay(sh);
                mcam.startPreview();
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        if (sh.getSurface() == null) {
            // preview surface does not exist
            return;
        }
        // stop preview before making changes
        try {
            mcam.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }
        // set preview size and make any resize, rotate or
        // reformatting changes here
        // start preview with new settings
        try {
            mcam.setPreviewDisplay(sh);
            mcam.startPreview();
        } catch (Exception e) {
        }

    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //권한요구
        ActivityCompat.requestPermissions(this,new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA
        },100);

        setting();
        cambtn = (Button)findViewById(R.id.capbtn);
        cambtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                hasVideo = true;
                startVideoRecorder();
            }
        });
    }

    private  void setting(){
        mcam = Camera.open();
        mcam.setDisplayOrientation(90);
        sv = (SurfaceView)findViewById(R.id.surfaceView);
        sh = sv.getHolder();
        sh.addCallback(this);
        sh.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }


    void startVideoRecorder() {
            if (recording) { //녹화 중일때 버튼 클릭시 녹화 종료
                mediaRecorder.stop();
                mediaRecorder.release();//stop release세트임
                mediaRecorder = null;
                cambtn.setText("START"); //녹화시작버튼으로 표시
                mcam.lock();
                recording = false;
                Toast.makeText(MainActivity.this, "nice", Toast.LENGTH_LONG).show();

            } else {//녹화중이 아니므로 버튼 클릭시 녹화 시작
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            mediaRecorder = new MediaRecorder();
                            mcam.unlock();
                            mediaRecorder.setCamera(mcam);
                            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);//녹음 기능
                            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);//걍다하셈이거
                            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);//영상 오디오 인코딩 방식
                            mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);// 영상인코딩 방식
                            mediaRecorder.setVideoFrameRate(17);//고화질옵션 내컴터에뮬은 17에서 팅김
                            //mediaRecorder.setVideoSize(sh.getSurfaceFrame().width(), sh.getSurfaceFrame().height());//고화질 옵션
                            //그 외 고화질 옵션..
                            /*CamcorderProfile cpHigh = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
mMediaRecorder.setVideoSize(DISPLAY_WIDTH, DISPLAY_HEIGHT);
mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
mMediaRecorder.setVideoEncodingBitRate(cpHigh.videoBitRate);
mMediaRecorder.setVideoFrameRate(cpHigh.videoFrameRate);
int rotation = mWindowManager.getDefaultDisplay().getRotation();
int orientation = ORIENTATIONS.get(rotation + 90);
mMediaRecorder.setOrientationHint(orientation);*/

                            mediaRecorder.setOrientationHint(90);
                            vidPath = Environment.getExternalStorageDirectory() + "/record3.mp4";
                            //vidPath =   "/sdcard/Android/record.mp4";
                            //외부 디렉토리중 가장 상위 디렉토리를 찾아 리턴
                            //Log.d("MainActivity", "file path is " + vidPath);
                            mediaRecorder.setOutputFile(vidPath);
                            mediaRecorder.setPreviewDisplay(sh.getSurface());
                            mediaRecorder.prepare();
                            mediaRecorder.start();
                            }catch(IOException e){
                            Toast.makeText(MainActivity.this, "ioexeption", Toast.LENGTH_LONG).show();
                            }
                            recording = true;


                            cambtn.setText("stop");
                            //testmsg.setText("recoding");
                            //Toast.makeText(MainActivity.this, "start", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }

        if(mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }


    /*public void refreshCamera(Camera camera) {
        if (sh.getSurface() == null) {
            // preview surface does not exist
            return;
        }
        // stop preview before making changes
        try {
            mcam.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }
        // set preview size and make any resize, rotate or
        // reformatting changes here
        // start preview with new settings
        setCamera(camera);
        try {
            mcam.setPreviewDisplay(sh);
            mcam.startPreview();
        } catch (Exception e) {
        }
    }
    public void setCamera(Camera camera) {
        //method to set a camera instance
        mcam = camera;
    }*/
}


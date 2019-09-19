package player.rongyun.com.playsdk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.ListPopupWindow;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import me.kareluo.ui.OptionMenu;
import me.kareluo.ui.OptionMenuView;
import me.kareluo.ui.PopupMenuView;
import me.kareluo.ui.PopupView;
import player.rongyun.com.playsdk.Utils.CommonUtils;
import player.rongyun.com.playsdk.Utils.ScreenRotateUtil;

/**
 * Created by xdg on 2019/8/16.
 */

public class MediaPlayerActivity extends Activity{
    SurfaceView surfaceView;
    MediaPlayer mPlayer;
    private RelativeLayout mVideoLayout;
    private ImageView mFullScreenIv;
    private ImageView mPlayerIv;
    private ImageView mStretchIv;
    private ImageView mCircleIv;
    private ImageView mSwitchIv;
    private TextView mQuickIv;
    private TextView mResIv;
    private String mOrigin;
    private String m720;
    private String m360;
    private String m2K;
    private String m4K;
    private String mHDR;
    private boolean mIsClip = false;
    private boolean mIsQuick = false;
    private boolean mIsCircle = false;
    private ArrayList<String> resList = new ArrayList<>();
    private ArrayList<String> urlList = new ArrayList<>();
    private ListPopupWindow mListPopupWindow = null;
    private SortAadapter adapter = null;
    private String mUrlPlay;
    private String mInitRes;
    private Context mContext;
    int position;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mediaplayer);
        // 获取界面中的三个按钮
        mContext = this;
        mOrigin = getIntent().getStringExtra("orig");
        if (!TextUtils.isEmpty(mOrigin)) {
            mUrlPlay = mOrigin;
            urlList.add(mOrigin);
            resList.add(getString(R.string.str_original_image));
            mInitRes = getString(R.string.str_original_image);
        }
        m720 = getIntent().getStringExtra("720");
        if (!TextUtils.isEmpty(m720)) {
            if (mUrlPlay == null) {
                mUrlPlay = m720;
                mInitRes = getString(R.string.str_720P);
            }
            urlList.add(m720);
            resList.add(getString(R.string.str_720P));

        }
        m360 = getIntent().getStringExtra("360");
        if (!TextUtils.isEmpty(m360)) {
            if (mUrlPlay == null) {
                mUrlPlay = m360;
                mInitRes = getString(R.string.str_360P);
            }
            urlList.add(m360);
            resList.add(getString(R.string.str_360P));
        }
        m2K = getIntent().getStringExtra("2k");
        if (!TextUtils.isEmpty(m2K)) {
            if (mUrlPlay == null) {
                mUrlPlay = m2K;
                mInitRes = getString(R.string.str_2K);
            }
            urlList.add(m2K);
            resList.add(getString(R.string.str_2K));
        }
        m4K = getIntent().getStringExtra("4k");
        if (!TextUtils.isEmpty(m4K)) {
            if (mUrlPlay == null) {
                mUrlPlay = m4K;
                mInitRes = getString(R.string.str_4K);
            }
            urlList.add(m4K);
            resList.add(getString(R.string.str_4K));
        }
        mHDR = getIntent().getStringExtra("HDR");
        if (!TextUtils.isEmpty(mHDR)) {
            if (mUrlPlay == null) {
                mUrlPlay = mHDR;
                mInitRes = getString(R.string.str_HDR);
            }
            urlList.add(mHDR);
            resList.add(getString(R.string.str_HDR));
        }
        initUi();
        ScreenRotateUtil.getInstance(this).start(this);
    }

    @SuppressLint("RestrictedApi")
    private void initUi() {
        mVideoLayout = findViewById(R.id.video_layout);
        int width = CommonUtils.getScreenWidth(this);
        int height = CommonUtils.getScreenWidth(this) * 9 / 16;
        if (width < CommonUtils.getScreenHeight(this)) {
            mVideoLayout.setLayoutParams(new FrameLayout.LayoutParams(width, height));
        }
        mPlayerIv = findViewById(R.id.play_btn);
        mPlayerIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayer != null) {
                        if (mPlayer.isPlaying()) {
                            mPlayer.pause();
                            mPlayerIv.setImageResource(R.mipmap.play_icon);
                        } else {
                            mPlayer.start();
                            mPlayerIv.setImageResource(R.mipmap.pause_icon);
                        }
                    }

            }
        });
        mFullScreenIv = findViewById(R.id.full_screen_iv);
        mFullScreenIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScreenRotateUtil.getInstance(MediaPlayerActivity.this).toggleRotate();
            }
        });
        mStretchIv = findViewById(R.id.stretch_iv);
        mStretchIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayer != null) {
                    mIsClip = !mIsClip;

                }
            }
        });
        mCircleIv = findViewById(R.id.cycle_iv);
        mCircleIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsCircle = !mIsCircle;
            }
        });
        mSwitchIv = findViewById(R.id.switch_iv);
        mSwitchIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayer != null) {
                    String path = doShortcut();
                    if (path == null) {
                        Toast.makeText(getApplication(), "截图失败", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplication(), "截图成功", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
        mQuickIv = findViewById(R.id.quick_play_iv);
        mQuickIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenuView menuView = new PopupMenuView(mContext, R.menu.menu_pop,
                        new MenuBuilder(mContext));
                menuView.setOnMenuClickListener(new OptionMenuView.OnOptionMenuClickListener() {
                    @Override
                    public boolean onOptionMenuClick(int position, OptionMenu menu) {
                        Toast.makeText(MediaPlayerActivity.this, menu.getTitle(), Toast.LENGTH_SHORT)
                                .show();
                        if (mPlayer != null) {
                            if(position==0){
                                changeplayerSpeed(1);
                                mQuickIv.setText("1X");
                            }else if(position==1){
                                changeplayerSpeed((float)1.5);
                                mQuickIv.setText("1.5X");
                            }else if(position==2){
                                changeplayerSpeed((float)2);
                                mQuickIv.setText("2X");
                            }else if(position==3){
                                changeplayerSpeed((float)0.5);
                                mQuickIv.setText("0.5X");
                            }
                        }
                        return true;
                    }
                });
                menuView.setSites(PopupView.SITE_TOP);
                menuView.show(v);
            }
        });
        mResIv = findViewById(R.id.res_iv);
        mResIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showListPopupWindow(v);
            }
        });
        if (!TextUtils.isEmpty(mInitRes)) {
            mResIv.setText(mInitRes);
        }
        mPlayer = new MediaPlayer();
        surfaceView = (SurfaceView) this.findViewById(R.id.surfaceView);
        // 设置播放时打开屏幕
        surfaceView.getHolder().setKeepScreenOn(true);
        surfaceView.getHolder().addCallback(new SurfaceListener());

    }

    private void tryResetSurfaceSize(final View view, int videoWidth, int videoHeight){
        ViewGroup parent= (ViewGroup) view.getParent();
        int width=parent.getWidth();
        int height=parent.getHeight();
        if(width>0&&height>0){
            final FrameLayout.LayoutParams params= (FrameLayout.LayoutParams) view.getLayoutParams();
            if(mIsClip){
                float scaleVideo=videoWidth/(float)videoHeight;
                float scaleSurface=width/(float)height;
                if(scaleVideo<scaleSurface){
                    params.width=width;
                    params.height= (int) (width/scaleVideo);
                    params.setMargins(0,(height-params.height)/2,0,(height-params.height)/2);
                }else{
                    params.height=height;
                    params.width= (int) (height*scaleVideo);
                    params.setMargins((width-params.width)/2,0,(width-params.width)/2,0);
                }
            }else{
                if(videoWidth>width||videoHeight>height){
                    float scaleVideo=videoWidth/(float)videoHeight;
                    float scaleSurface=width/height;
                    if(scaleVideo>scaleSurface){
                        params.width=width;
                        params.height= (int) (width/scaleVideo);
                        params.setMargins(0,(height-params.height)/2,0,(height-params.height)/2);
                    }else{
                        params.height=height;
                        params.width= (int) (height*scaleVideo);
                        params.setMargins((width-params.width)/2,0,(width-params.width)/2,0);
                    }
                }
            }
            view.setLayoutParams(params);
        }
    }

    private void changeplayerSpeed(float speed) {
        // this checks on API 23 and up
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mPlayer.isPlaying()) {
                mPlayer.setPlaybackParams(mPlayer.getPlaybackParams().setSpeed(speed));
            } else {
                mPlayer.setPlaybackParams(mPlayer.getPlaybackParams().setSpeed(speed));
                mPlayer.pause();
            }
        }
    }
    public void showListPopupWindow(View view) {
        if (mListPopupWindow == null)
            mListPopupWindow = new ListPopupWindow(this);
        if (adapter == null) {
            adapter = new SortAadapter(this, android.R.layout.simple_list_item_1);
// ListView适配器
            mListPopupWindow.setAdapter(adapter);
// 选择item的监听事件
            mListPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                    String url = null;
                    if (resList.get(pos).equals(getString(R.string.str_720P))) {
                        url = m720;
                    }
                    if (resList.get(pos).equals(getString(R.string.str_original_image))) {
                        url = mOrigin;
                    }
                    if (resList.get(pos).equals(getString(R.string.str_360P))) {
                        url = m360;
                    }
                    if (resList.get(pos).equals(getString(R.string.str_2K))) {
                        url = m2K;
                    }
                    if (resList.get(pos).equals(getString(R.string.str_4K))) {
                        url = m4K;
                    }
                    if (resList.get(pos).equals(getString(R.string.str_HDR))) {
                        url = mHDR;
                    }
                    mResIv.setText(adapter.getItem(pos));

                    if (mPlayer != null) {
                        try {
                            mPlayer.stop();
                            position = 0;
                            // 开始播放
                            play();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    mListPopupWindow.dismiss();
                }
            });
            mListPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
//旋转0度是复位ImageView
                }
            });
        }
// ListPopupWindow的锚,弹出框的位置是相对当前View的位置
        mListPopupWindow.setAnchorView(view);
        mListPopupWindow.setVerticalOffset(CommonUtils.dp2px(this, 5));
// 对话框的宽高
        mListPopupWindow.setWidth(view.getWidth());
        mListPopupWindow.setModal(true);
        mListPopupWindow.show();
    }

    private class SortAadapter extends ArrayAdapter {

        private LayoutInflater inflater;
        private int res;

        public SortAadapter(Context context, int resource) {
            super(context, resource);
            inflater = LayoutInflater.from(context);
            res = resource;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = inflater.inflate(res, null);
            TextView text = (TextView) convertView.findViewById(android.R.id.text1);
            text.setText(getItem(position));
            text.setTextColor(Color.WHITE);
            text.setTextSize(12);
            convertView.setBackgroundColor(getColor(R.color.black_transparent));
            return convertView;
        }

        @Override
        public String getItem(int position) {
            return resList.get(position);
        }

        @Override
        public int getCount() {
            return resList.size();
        }
    }

    private void play() throws IOException {
        mPlayer.reset();
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        // 设置需要播放的视频
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if(mIsCircle){
                    mPlayer.start();
                }else{
                    mPlayer.stop();
                }

            }
        });
        mPlayer.setDataSource(mUrlPlay);
        // 把视频画面输出到SurfaceView
        mPlayer.setDisplay(surfaceView.getHolder());  // ①
        mPlayer.prepare();
        // 获取窗口管理器
        WindowManager wManager = getWindowManager();
        DisplayMetrics metrics = new DisplayMetrics();
        // 获取屏幕大小
        wManager.getDefaultDisplay().getMetrics(metrics);
        // 设置视频保持纵横比缩放到占满整个屏幕
        surfaceView.setLayoutParams(new RelativeLayout.LayoutParams(metrics.widthPixels
                , mPlayer.getVideoHeight() * metrics.widthPixels
                / mPlayer.getVideoWidth()));
        mPlayer.start();
    }
    public String doShortcut() {
        return getVideoFrame();
    }
    @SuppressLint("NewApi")
    public String getVideoFrame() {
        File qrCache = new File(MediaPlayerActivity.this.getExternalCacheDir(), "shotphoto");
        //储存路径可以存在内部存储
        if (!qrCache.exists()) {
            qrCache.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".png";//图片文件名字
        File picFile = new File(qrCache, fileName);
        Bitmap bmp = null;
        // android 9及其以上版本可以使用该方法
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(mUrlPlay);
            // 这一句是必须的
            String timeString =
                    retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            // 获取总长度,这一句也是必须的
            long titalTime = Long.parseLong(timeString) * 1000;

            long videoPosition = 0;
            try {
                mPlayer.setDataSource(mUrlPlay);
                if (mUrlPlay.startsWith("http")) {
                    mPlayer.prepareAsync();
                } else {
                    mPlayer.prepare();
                }
                int duration = mPlayer.getDuration();
                // 通过这个计算出想截取的画面所在的时间
                videoPosition = titalTime * videoPosition / duration;
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (videoPosition > 0) {
                bmp = retriever.getFrameAtTime(videoPosition,
                        MediaMetadataRetriever.OPTION_CLOSEST);
                MediaStore.Images.Media.insertImage(MediaPlayerActivity.this.getContentResolver(), bmp,
                        fileName, null);
//发送广播刷新图片
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri
                        .parse(picFile.getParent())));
                return fileName;
            }
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        } catch (RuntimeException ex) {
            ex.printStackTrace();
        }catch (Exception e){

        }
        finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
            }
        }
        return null;
    }

    private class SurfaceListener implements SurfaceHolder.Callback {
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format,
                                   int width, int height) {
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
          //  if (position > 0) {
                try {
                    // 开始播放
                    play();
                    // 并直接从指定位置开始播放
                    mPlayer.seekTo(position);
                    position = 0;
                } catch (Exception e) {
                    e.printStackTrace();
                }
           // }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
        }
    }

    // 当其他Activity被打开时，暂停播放
    @Override
    protected void onPause() {
        if (mPlayer.isPlaying()) {
            // 保存当前的播放位置
            position = mPlayer.getCurrentPosition();
            mPlayer.stop();
        }
        super.onPause();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int type = newConfig.orientation;
        if (type == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE || type == ActivityInfo
                .SCREEN_ORIENTATION_REVERSE_LANDSCAPE || !ScreenRotateUtil.getInstance(MediaPlayerActivity
                .this).isLandscape()) {
            //横屏
            setContentView(R.layout.activity_mediaplayer_land);
        } else if (type == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT || type == ActivityInfo
                .SCREEN_ORIENTATION_REVERSE_PORTRAIT || ScreenRotateUtil.getInstance(MediaPlayerActivity
                .this).isLandscape()) {
            //竖屏
            setContentView(R.layout.activity_mediaplayer);
        }
        initUi();
    }

    @Override
    protected void onDestroy() {
        // 停止播放
        if (mPlayer.isPlaying()) mPlayer.stop();
        // 释放资源
        mPlayer.release();
        super.onDestroy();
    }
}

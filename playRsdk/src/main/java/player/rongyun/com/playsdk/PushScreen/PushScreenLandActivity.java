package player.rongyun.com.playsdk.PushScreen;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.ListPopupWindow;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.rongyun.lib_screen.callback.ControlCallback;
import com.rongyun.lib_screen.entity.AVTransportInfo;
import com.rongyun.lib_screen.entity.LocalItem;
import com.rongyun.lib_screen.entity.RemoteItem;
import com.rongyun.lib_screen.event.ControlEvent;
import com.rongyun.lib_screen.manager.ControlManager;
import com.rongyun.lib_screen.utils.VMDate;


import java.util.ArrayList;

import me.kareluo.ui.OptionMenu;
import me.kareluo.ui.OptionMenuView;
import me.kareluo.ui.PopupMenuView;
import me.kareluo.ui.PopupView;
import player.rongyun.com.playsdk.R;
import player.rongyun.com.playsdk.Utils.CommonUtils;
import player.rongyun.com.playsdk.Utils.ScreenRotateUtil;

public class PushScreenLandActivity extends AppCompatActivity implements PushManager
        .PlayCallback,PushManager.RemoteControlCallback {

    private ImageView mFullScreenIv;
    private ImageView mPlayerIv;
    private ImageView mStretchIv;
    private ImageView mCircleIv;
    private ImageView mSwitchIv;
    private TextView mQuickIv;
    private ImageView mStopIv;
    private View mIvLeft;
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
    SeekBar progressSeekbar;
    TextView playTimeView;
    TextView playMaxTimeView;
    ImageView previousView;
    private String mSpeed = "1.0";
    public LocalItem localItem;
    public RemoteItem remoteItem;

    private int defaultVolume = 10;
    private int currVolume = defaultVolume;
    private boolean isMute = false;
    private int currProgress = 0;
    private int mDuration;

    public static void startSelf(Activity context) {
        Intent intent = new Intent(context, PushScreenLandActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager
                .LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_dlna_play_port);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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
        mDuration = getIntent().getIntExtra("duration",0);
        initUi();
        ScreenRotateUtil.getInstance(this).start(this);
        PushManager.getInstance().setPlayCallback(this);
    }

    @SuppressLint("RestrictedApi")
    private void initUi() {
        int width = CommonUtils.getScreenWidth(this);
        int height = CommonUtils.getScreenWidth(this) * 9 / 16;
//        if (width < CommonUtils.getScreenHeight(this)) {
//            mVideoLayout.setLayoutParams(new RelativeLayout.LayoutParams(width, height));
//        }
        mIvLeft = findViewById(R.id.image_title_left);
        mIvLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mPlayerIv = findViewById(R.id.play_btn);
        mPlayerIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(PushManager.getInstance().getState()== ControlManager.CastState.PLAYING){
                    PushManager.getInstance().pauseCast();
                }else{
                    PushManager.getInstance().play(mSpeed);
                }
            }
        });
        mFullScreenIv = findViewById(R.id.full_screen_iv);
        mFullScreenIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScreenRotateUtil.getInstance(PushScreenLandActivity.this).toggleRotate();
            }
        });
        mCircleIv = findViewById(R.id.cycle_iv);
        mCircleIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsCircle = !mIsCircle;
                if(mIsCircle){
                    showToast(String.format("进入循环播放模式"));
                }else{
                    showToast(String.format("退出循环播放模式"));
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
                        Toast.makeText(PushScreenLandActivity.this, menu.getTitle(), Toast
                                .LENGTH_SHORT)
                                .show();
                        if (position == 0) {
                            mSpeed = "1.0";
                            mQuickIv.setText("1X");
                        } else if (position == 1) {
                            mSpeed = "1.5";
                            mQuickIv.setText("1.5X");
                        } else if (position == 2) {
                            mSpeed = "2.0";
                            mQuickIv.setText("2X");
                        } else if (position == 3) {
                            mSpeed = "0.5";
                            mQuickIv.setText("0.5X");
                        }
                        PushManager.getInstance().pauseCast();
                        PushManager.getInstance().play(mSpeed);
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
        mStopIv = findViewById(R.id.img_stop);
        mStopIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PushManager.getInstance().stopCast();
            }
        });
        progressSeekbar = findViewById(R.id.seek_bar_progress);
        playTimeView = findViewById(R.id.text_play_time);
        playMaxTimeView = findViewById(R.id.text_play_max_time);
        RemoteItem itemurl = new RemoteItem("", "", "",
                0, VMDate.long2Time(mDuration), "", mUrlPlay);
        PushManager.getInstance().setRemoteItem(itemurl);
        localItem = PushManager.getInstance().getLocalItem();
        remoteItem = PushManager.getInstance().getRemoteItem();
        String url = "";
        String duration = "";
        if (localItem != null) {
            //mUrlPlay = localItem.getFirstResource().getValue();
            //duration = localItem.getFirstResource().getDuration();
        }
        if (remoteItem != null) {
            mUrlPlay = remoteItem.getUrl();
            duration = remoteItem.getDuration();
        }
        setProgressSeekListener();
    }


    /**
     * 设置播放进度拖动监听
     */
    private void setProgressSeekListener() {
        progressSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                currProgress = seekBar.getProgress();
                playTimeView.setText(VMDate.toTimeString(currProgress));
                PushManager.getInstance().seekCast(currProgress);
            }
        });
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
                    if (resList.get(pos).equals(getString(R.string.str_720P))) {
                        mUrlPlay = m720;
                    }
                    if (resList.get(pos).equals(getString(R.string.str_original_image))) {
                        mUrlPlay = mOrigin;
                    }
                    if (resList.get(pos).equals(getString(R.string.str_360P))) {
                        mUrlPlay = m360;
                    }
                    if (resList.get(pos).equals(getString(R.string.str_2K))) {
                        mUrlPlay = m2K;
                    }
                    if (resList.get(pos).equals(getString(R.string.str_4K))) {
                        mUrlPlay = m4K;
                    }
                    if (resList.get(pos).equals(getString(R.string.str_HDR))) {
                        mUrlPlay = mHDR;
                    }
                    RemoteItem itemurl = new RemoteItem("", "", "",
                            0, "", "", mUrlPlay);
                    PushManager.getInstance().setRemoteItem(itemurl);
                    localItem = PushManager.getInstance().getLocalItem();
                    remoteItem = PushManager.getInstance().getRemoteItem();
                    String duration = "";
                    if (localItem != null) {
                        //mUrlPlay = localItem.getFirstResource().getValue();
                        //duration = localItem.getFirstResource().getDuration();
                    }
                    if (remoteItem != null) {
                        mUrlPlay = remoteItem.getUrl();
                        duration = remoteItem.getDuration();
                    }
                    PushManager.getInstance().stopCast();
                    PushManager.getInstance().play(mSpeed);
                    mResIv.setText(adapter.getItem(pos));
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
    @Override
    public void onSuccess(final PushManager.PERFORM_TYPE type){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(type == PushManager.PERFORM_TYPE.onPlayCast){
                    mPlayerIv.setImageResource(R.mipmap.pause_icon);
                }else if(type == PushManager.PERFORM_TYPE.onPauseCast){
                    mPlayerIv.setImageResource(R.mipmap.play_icon);
                }else if(type == PushManager.PERFORM_TYPE.onStopCast){
                   finish();
                }else if(type == PushManager.PERFORM_TYPE.onMute){
                    if (PushManager.getInstance().getMute()) {
                        if (currVolume == 0) {
                            currVolume = defaultVolume;
                        }
                        setVolume(currVolume);
                    }
                }

            }
        });
    }
    @Override
    public void onError(PushManager.PERFORM_TYPE type, int code, String msg){

    }
    /**
     * 静音开关
     */
    private void mute() {
        // 先获取当前是否静音
        isMute = PushManager.getInstance().getMute();
        PushManager.getInstance().setMute(!isMute);
    }

    /**
     * 设置音量大小
     */
    private void setVolume(int volume) {
        currVolume = volume;
        PushManager.getInstance().setVolume(volume);
    }



    private void showToast(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        PushManager.getInstance().setControlCallback(this);

    }

    @Override
    public void onStop() {
        super.onStop();
        PushManager.getInstance().removeControlCallback(this);
    }

    public void onControlEvent(ControlEvent event) {
        AVTransportInfo avtInfo = event.getAvtInfo();
        if (avtInfo != null) {
            if (!TextUtils.isEmpty(avtInfo.getState())) {
                if (avtInfo.getState().equals("TRANSITIONING")) {
                    PushManager.getInstance().setState(ControlManager.CastState.TRANSITIONING);
                } else if (avtInfo.getState().equals("PLAYING")) {
                    PushManager.getInstance().setState(ControlManager.CastState.PLAYING);
                    mPlayerIv.setImageResource(R.mipmap.pause_icon);
                } else if (avtInfo.getState().equals("PAUSED_PLAYBACK")) {
                    PushManager.getInstance().setState(ControlManager.CastState.PAUSED);
                    mPlayerIv.setImageResource(R.mipmap.play_icon);
                } else if (avtInfo.getState().equals("STOPPED")) {
                    PushManager.getInstance().setState(ControlManager.CastState.STOPED);
                    mPlayerIv.setImageResource(R.mipmap.play_icon);
                    if (mIsCircle) {
                        PushManager.getInstance().play(mSpeed);
                    }
                } else {
                    PushManager.getInstance().setState(ControlManager.CastState.STOPED);
                }
            }
            if (!TextUtils.isEmpty(avtInfo.getMediaDuration())) {
                playMaxTimeView.setText(avtInfo.getMediaDuration());
                progressSeekbar.setMax((int)VMDate.fromTimeString(avtInfo.getMediaDuration()));
            }
            if (!TextUtils.isEmpty(avtInfo.getTimePosition())) {
                long progress = VMDate.fromTimeString(avtInfo.getTimePosition());
                progressSeekbar.setProgress((int) progress);
                playTimeView.setText(avtInfo.getTimePosition());
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        int type = newConfig.orientation;
        if (type == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE || type == ActivityInfo
                .SCREEN_ORIENTATION_REVERSE_LANDSCAPE || rotation == Surface.ROTATION_90 ||
                rotation == Surface.ROTATION_270) {
            //横屏
            setContentView(R.layout.activity_dlna_play_land);
        } else if (type == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT || type == ActivityInfo
                .SCREEN_ORIENTATION_REVERSE_PORTRAIT) {
            //竖屏
            setContentView(R.layout.activity_dlna_play_port);
        }
        initUi();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ScreenRotateUtil.getInstance(this).stop();
    }
}
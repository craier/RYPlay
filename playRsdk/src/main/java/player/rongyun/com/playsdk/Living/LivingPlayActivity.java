package player.rongyun.com.playsdk.Living;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuBuilder;
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
import android.widget.ListPopupWindow;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import me.kareluo.ui.OptionMenu;
import me.kareluo.ui.OptionMenuView;
import me.kareluo.ui.PopupMenuView;
import me.kareluo.ui.PopupView;
import player.rongyun.com.playsdk.PushScreen.DeviceListActivity;
import player.rongyun.com.playsdk.R;
import player.rongyun.com.playsdk.Utils.CommonUtils;
import player.rongyun.com.playsdk.Utils.FileUtils;
import player.rongyun.com.playsdk.Utils.ScreenRotateUtil;
import player.rongyun.com.playsdk.Utils.ToastUtils;
import player.rongyun.com.playsdk.VRMediaPlayer.MediaPlayerWrapper;
import player.rongyun.com.playsdk.media.PlayerManager;
import player.rongyun.com.playsdk.media.RYVideoView;
import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Created by xdg on 2019/9/11.
 */

public class LivingPlayActivity extends AppCompatActivity {

    //private IjkVideoView mVideoView;
    private RelativeLayout mVideoLayout;
    private ImageView mFullScreenIv;
    private ImageView mPlayerIv;
    private ImageView mStretchIv;
    private ImageView mCircleIv;
    private ImageView mSwitchIv;
    private ImageView mProjectIv;
    private ImageView mLeftBtn;
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
    private ArrayList<String> resList = new ArrayList<>();
    private ArrayList<String> urlList = new ArrayList<>();
    private ListPopupWindow mListPopupWindow = null;
    private SortAadapter adapter = null;
    private String mUrlPlay;
    private String mInitRes;
    private Context mContext;
    private int playerType = 0;
    private boolean mIsPause = false;
    private MediaPlayerWrapper mPlayerManager;
    private boolean mIsFullScreen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager
                .LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_player);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mContext = this;
        playerType = getIntent().getIntExtra("playerType", 0);
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
        //  ScreenRotateUtil.getInstance(this).start(this);
        /** 普通播放 end **/

    }

    @SuppressLint("RestrictedApi")
    private void initUi() {
        mVideoLayout = findViewById(R.id.video_layout);
        //mVideoView = (IjkVideoView) findViewById(R.id.video_view);
        //mVideoView.setPlayer(playerType);
        mPlayerManager = new MediaPlayerWrapper(LivingPlayActivity.this,(RYVideoView)findViewById(R.id
                .video_view));
        mPlayerManager.live(true);
        mPlayerManager.setOnErrorListener(new IMediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
                ToastUtils.ToastShort("播放失败");
                return false;
            }
        });

        int width = CommonUtils.getScreenWidth(this);
        int height = CommonUtils.getScreenWidth(this) * 9 / 16;
        if (width < CommonUtils.getScreenHeight(this)) {
            mVideoLayout.setLayoutParams(new FrameLayout.LayoutParams(width, height));
        }
        mPlayerIv = findViewById(R.id.play_btn);
        mPlayerIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayerManager != null) {
                    if (mPlayerManager.isPlaying()) {
                        mPlayerManager.pause();
                        mPlayerIv.setImageResource(R.mipmap.play_icon);
                    } else {
                        mPlayerManager.resume();
                        mPlayerIv.setImageResource(R.mipmap.pause_icon);
                    }

                }
            }
        });
        mFullScreenIv = findViewById(R.id.full_screen_iv);
        mFullScreenIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ScreenRotateUtil.getInstance(LivingPlayActivity.this).toggleRotate();
                if (mPlayerManager != null) {
                    mPlayerManager.playInFullScreen(!mIsFullScreen);
                    mIsFullScreen = !mIsFullScreen;
                }
            }
        });
        mStretchIv = findViewById(R.id.stretch_iv);
        mStretchIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayerManager != null) {
                    if (mIsClip) {
                        mPlayerManager.setScaleType(PlayerManager.SCALETYPE_FITPARENT);
                    } else {
                        mPlayerManager.setScaleType(PlayerManager.SCALETYPE_FILLPARENT);
                    }
                    mIsClip = !mIsClip;
                }
            }
        });
        mCircleIv = findViewById(R.id.cycle_iv);
        mCircleIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        mSwitchIv = findViewById(R.id.switch_iv);
        mSwitchIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayerManager != null) {
                    String fileName = FileUtils.getImageCacheDir(LivingPlayActivity.this)+System
                            .currentTimeMillis() + "" +
                            ".png";//图片文件名字
                    String path = mPlayerManager.doShortcut(fileName);
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
                        Toast.makeText(LivingPlayActivity.this, menu.getTitle(), Toast.LENGTH_SHORT)
                                .show();
                        if (mPlayerManager != null) {
                            if (position == 0) {
                                mPlayerManager.setQuickPlay(1);
                                mQuickIv.setText("1X");
                            } else if (position == 1) {
                                mPlayerManager.setQuickPlay((float) 1.5);
                                mQuickIv.setText("1.5X");
                            } else if (position == 2) {
                                mPlayerManager.setQuickPlay((float) 2);
                                mQuickIv.setText("2X");
                            } else if (position == 3) {
                                mPlayerManager.setQuickPlay((float) 0.5);
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
        mProjectIv = findViewById(R.id.projection_iv);
        mProjectIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //PushMainActivity.startSelf(MainActivity.this);
                Intent intent = new Intent(LivingPlayActivity.this, DeviceListActivity.class);
                intent.putExtra("4k", m4K);
                intent.putExtra("2k", m2K);
                intent.putExtra("720", m720);
                intent.putExtra("360", m360);
                intent.putExtra("playerType", 2);
                intent.putExtra("duration", mPlayerManager.getDuration());
                startActivity(intent);
            }
        });
        mLeftBtn = findViewById(R.id.image_title_left);
        mLeftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mPlayerManager.onBackPressed()) {
                    finish();
                }
            }
        });
        /** 普通播放 start **/
        mPlayerManager.setScaleType(PlayerManager.SCALETYPE_FITPARENT);
//        if(playerType ==1){
//            mVideoView.setVideoPath(mUrlPlay);
//        }
        mPlayerManager.play(mUrlPlay);

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

                    if (mPlayerManager != null) {
                        mPlayerManager.switchResolution(url);
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
        // mListPopupWindow.setDropDownGravity(Gravity.START);
        //mListPopupWindow.setVerticalOffset(CommonUtils.dp2px(this, 20));
// 对话框的宽高
        mListPopupWindow.setHorizontalOffset(CommonUtils.dp2px(this, -10));
        mListPopupWindow.setWidth(2 * view.getWidth() + 10);
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
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        int type = newConfig.orientation;
        if (type == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE || type == ActivityInfo
                .SCREEN_ORIENTATION_REVERSE_LANDSCAPE || rotation == Surface.ROTATION_90 ||
                rotation == Surface.ROTATION_270) {
            //横屏
            setContentView(R.layout.activity_player_land);
        } else if (type == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT || type == ActivityInfo
                .SCREEN_ORIENTATION_REVERSE_PORTRAIT) {
            //竖屏
            setContentView(R.layout.activity_player);
        }
        initUi();
    }

    @Override
    public void onBackPressed() {
        if (!mPlayerManager.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        if (mIsPause) {
            // 保存当前的播放位置
            mPlayerManager.resume();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (mPlayerManager.isPlaying()) {
            // 保存当前的播放位置
            mPlayerManager.pause();
            mIsPause = true;
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // ScreenRotateUtil.getInstance(this).stop();
    }
}


package player.rongyun.com.playsdk.VRMediaPlayer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import me.kareluo.ui.OptionMenu;
import me.kareluo.ui.OptionMenuView;
import me.kareluo.ui.PopupMenuView;
import me.kareluo.ui.PopupView;
import player.rongyun.com.playsdk.Living.LivingPlayActivity;
import player.rongyun.com.playsdk.PushScreen.DeviceListActivity;
import player.rongyun.com.playsdk.R;
import player.rongyun.com.playsdk.Utils.CommonUtils;
import player.rongyun.com.playsdk.Utils.FileUtils;
import player.rongyun.com.playsdk.Utils.ScreenRotateUtil;
import player.rongyun.com.playsdk.vod.VodPlayActivity;
import tv.danmaku.ijk.media.player.IMediaPlayer;

public class VRPlayerActivity extends Activity {

    private static final String TAG = "VRPlayerActivity";
    private FrameLayout mVideoLayout;
    private ImageView mFullScreenIv;
    private ImageView mPlayerIv;
    private ImageView mStretchIv;
    private ImageView mGlassIv;
    private ImageView mTouchIv;
    private ImageView mCircleIv;
    private ImageView mSwitchIv;
    private ImageView mProjectIv;
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
    private boolean isGlass = false;
    private boolean isTouch = false;
    private MediaPlayerWrapper mMediaPlayerWrapper = null;
    private boolean mIsFullScreen = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_full_mediaplayer);
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
    }

    @SuppressLint("RestrictedApi")
    private void initUi() {
        mMediaPlayerWrapper = new MediaPlayerWrapper(VRPlayerActivity.this, findViewById(R.id.gl_view));
        mMediaPlayerWrapper.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer mp) {
                cancelBusy();
            }
        });
        if (mUrlPlay != null) {
            mMediaPlayerWrapper.play(mUrlPlay);
            mMediaPlayerWrapper.getPlayer().setOnInfoListener(new IMediaPlayer.OnInfoListener() {
                @Override
                public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {
                    return false;
                }
            });

            mMediaPlayerWrapper.getPlayer().setOnErrorListener(new IMediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(IMediaPlayer mp, int what, int extra) {
                    String error = String.format("Play Error what=%d extra=%d", what, extra);
                    Toast.makeText(VRPlayerActivity.this, error, Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }
        mVideoLayout = findViewById(R.id.video_layout);
        int width = CommonUtils.getScreenWidth(this);
        int height = CommonUtils.getScreenWidth(this) * 9 / 16;
        if (width < CommonUtils.getScreenHeight(this)) {
            mVideoLayout.setLayoutParams(new RelativeLayout.LayoutParams(width, height));
        }
        mPlayerIv = findViewById(R.id.play_btn);
        mPlayerIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMediaPlayerWrapper != null) {
                    if (mMediaPlayerWrapper.getPlayer().isPlaying()) {
                        mMediaPlayerWrapper.pause();
                        mPlayerIv.setImageResource(R.mipmap.play_icon);
                    } else {
                        mMediaPlayerWrapper.resume();
                        mPlayerIv.setImageResource(R.mipmap.pause_icon);
                    }
                }
            }
        });
        mFullScreenIv = findViewById(R.id.full_screen_iv);
        mFullScreenIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMediaPlayerWrapper != null) {
                    mIsFullScreen = !mIsFullScreen;
                    mMediaPlayerWrapper.playInFullScreen(mIsFullScreen);
                }
            }
        });
        mStretchIv = findViewById(R.id.stretch_iv);
        mStretchIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(VRPlayerActivity.this, "暂不支持", Toast.LENGTH_SHORT)
                        .show();
            }
        });
        mCircleIv = findViewById(R.id.cycle_iv);
        mCircleIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsCircle = !mIsCircle;
                if (mMediaPlayerWrapper != null) {
                    mMediaPlayerWrapper.setCircle(mIsCircle);
                }
            }
        });
        mSwitchIv = findViewById(R.id.switch_iv);
        mSwitchIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMediaPlayerWrapper != null) {
                    String fileName = FileUtils.getImageCacheDir(VRPlayerActivity.this)+System
                            .currentTimeMillis() + "" +
                            ".png";//图片文件名字
                    String path = mMediaPlayerWrapper.doShortcut(fileName);
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
                        Toast.makeText(VRPlayerActivity.this, menu.getTitle(), Toast
                                .LENGTH_SHORT)
                                .show();
                        if (mMediaPlayerWrapper != null) {
                            if (position == 0) {
                                mMediaPlayerWrapper.setQuickPlay(1);
                                mQuickIv.setText("1X");
                            } else if (position == 1) {
                                mMediaPlayerWrapper.setQuickPlay((float) 1.5);
                                mQuickIv.setText("1.5X");
                            } else if (position == 2) {
                                mMediaPlayerWrapper.setQuickPlay((float) 2);
                                mQuickIv.setText("2X");
                            } else if (position == 3) {
                                mMediaPlayerWrapper.setQuickPlay((float) 0.5);
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
        mGlassIv = findViewById(R.id.glass_iv);
        mGlassIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isGlass = !isGlass;
                if (mMediaPlayerWrapper != null) {
                    mMediaPlayerWrapper.setGlass(isGlass);
                }
            }
        });
        mTouchIv = findViewById(R.id.touch_iv);
        mTouchIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isTouch = !isTouch;
                if (mMediaPlayerWrapper != null) {
                    mMediaPlayerWrapper.setTouchMode(isTouch);
                }

            }
        });
        mProjectIv = findViewById(R.id.projection_iv);
        mProjectIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //PushMainActivity.startSelf(MainActivity.this);
                Intent intent = new Intent(VRPlayerActivity.this, DeviceListActivity.class);
                intent.putExtra("4k", m4K);
                intent.putExtra("2k", m2K);
                intent.putExtra("720", m720);
                intent.putExtra("360", m360);
                intent.putExtra("playerType", 2);
                intent.putExtra("duration", mMediaPlayerWrapper.getDuration());
                startActivity(intent);
            }
        });
        if (!TextUtils.isEmpty(mInitRes)) {
            mResIv.setText(mInitRes);
        }

    }

    public void cancelBusy() {
        findViewById(R.id.progress).setVisibility(View.GONE);
    }

    public void busy() {
        findViewById(R.id.progress).setVisibility(View.VISIBLE);
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
                    if (mMediaPlayerWrapper != null) {
                        mMediaPlayerWrapper.switchResolution(mUrlPlay);
                    }
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
        // getVRLibrary().onOrientationChanged(this);
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        int type = newConfig.orientation;
        if (type == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE || type == ActivityInfo
                .SCREEN_ORIENTATION_REVERSE_LANDSCAPE || rotation == Surface.ROTATION_90 ||
                rotation == Surface.ROTATION_270) {
            //横屏
            setContentView(R.layout.activity_full_mediaplayer_land);
        } else if (type == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT || type == ActivityInfo
                .SCREEN_ORIENTATION_REVERSE_PORTRAIT) {
            //竖屏
            setContentView(R.layout.activity_full_mediaplayer);
        }
        mMediaPlayerWrapper.destroy();
        initUi();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMediaPlayerWrapper.destroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMediaPlayerWrapper.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMediaPlayerWrapper.resume();
    }

}

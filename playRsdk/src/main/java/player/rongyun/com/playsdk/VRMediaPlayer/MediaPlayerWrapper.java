package player.rongyun.com.playsdk.VRMediaPlayer;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.util.SimpleArrayMap;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.asha.vrlib.MD360Director;
import com.asha.vrlib.MD360DirectorFactory;
import com.asha.vrlib.MDVRLibrary;
import com.asha.vrlib.model.BarrelDistortionConfig;
import com.asha.vrlib.model.MDPinchConfig;
import com.asha.vrlib.model.MDPosition;
import com.asha.vrlib.model.position.MDMutablePosition;
import com.asha.vrlib.plugins.MDAbsPlugin;
import com.asha.vrlib.texture.MD360BitmapTexture;
import com.google.android.apps.muzei.render.GLTextureView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import player.rongyun.com.playsdk.App;
import player.rongyun.com.playsdk.R;
import player.rongyun.com.playsdk.Utils.LogUtils;
import player.rongyun.com.playsdk.media.IRenderView;
import player.rongyun.com.playsdk.media.PlayerManager;
import player.rongyun.com.playsdk.media.RYVideoView;
import player.rongyun.com.playsdk.media.TextureRenderView;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.misc.IMediaDataSource;
import tv.danmaku.ijk.media.player.pragma.DebugLog;

import static android.animation.PropertyValuesHolder.ofFloat;
import static com.asha.vrlib.MDVRLibrary.DISPLAY_MODE_NORMAL;
import static com.squareup.picasso.MemoryPolicy.NO_CACHE;
import static com.squareup.picasso.MemoryPolicy.NO_STORE;

public class MediaPlayerWrapper implements IMediaPlayer.OnPreparedListener {
    private final String TAG = this.getClass().getSimpleName();
    private static final SparseArray<String> sDisplayMode = new SparseArray<>();
    private static final SparseArray<String> sInteractiveMode = new SparseArray<>();
    private static final SparseArray<String> sProjectionMode = new SparseArray<>();
    private static final SparseArray<String> sAntiDistortion = new SparseArray<>();
    private static final SparseArray<String> sPitchFilter = new SparseArray<>();
    private static final SparseArray<String> sFlingEnabled = new SparseArray<>();

    static {
        sDisplayMode.put(MDVRLibrary.DISPLAY_MODE_NORMAL, "NORMAL");
        sDisplayMode.put(MDVRLibrary.DISPLAY_MODE_GLASS, "GLASS");

        sInteractiveMode.put(MDVRLibrary.INTERACTIVE_MODE_MOTION, "MOTION");
        sInteractiveMode.put(MDVRLibrary.INTERACTIVE_MODE_TOUCH, "TOUCH");
        sInteractiveMode.put(MDVRLibrary.INTERACTIVE_MODE_MOTION_WITH_TOUCH, "M & T");
        sInteractiveMode.put(MDVRLibrary.INTERACTIVE_MODE_CARDBORAD_MOTION, "CARDBOARD M");
        sInteractiveMode.put(MDVRLibrary.INTERACTIVE_MODE_CARDBORAD_MOTION_WITH_TOUCH, "CARDBOARD" +
                " M&T");

        sProjectionMode.put(MDVRLibrary.PROJECTION_MODE_SPHERE, "SPHERE");
        sProjectionMode.put(MDVRLibrary.PROJECTION_MODE_DOME180, "DOME 180");
        sProjectionMode.put(MDVRLibrary.PROJECTION_MODE_DOME230, "DOME 230");
        sProjectionMode.put(MDVRLibrary.PROJECTION_MODE_DOME180_UPPER, "DOME 180 UPPER");
        sProjectionMode.put(MDVRLibrary.PROJECTION_MODE_DOME230_UPPER, "DOME 230 UPPER");
        sProjectionMode.put(MDVRLibrary.PROJECTION_MODE_STEREO_SPHERE_HORIZONTAL, "STEREO H " +
                "SPHERE");
        sProjectionMode.put(MDVRLibrary.PROJECTION_MODE_STEREO_SPHERE_VERTICAL, "STEREO V SPHERE");
        sProjectionMode.put(MDVRLibrary.PROJECTION_MODE_PLANE_FIT, "PLANE FIT");
        sProjectionMode.put(MDVRLibrary.PROJECTION_MODE_PLANE_CROP, "PLANE CROP");
        sProjectionMode.put(MDVRLibrary.PROJECTION_MODE_PLANE_FULL, "PLANE FULL");
        sProjectionMode.put(MDVRLibrary.PROJECTION_MODE_MULTI_FISH_EYE_HORIZONTAL, "MULTI FISH " +
                "EYE HORIZONTAL");
        sProjectionMode.put(MDVRLibrary.PROJECTION_MODE_MULTI_FISH_EYE_VERTICAL, "MULTI FISH EYE " +
                "VERTICAL");
        sProjectionMode.put(CustomProjectionFactory.CUSTOM_PROJECTION_FISH_EYE_RADIUS_VERTICAL,
                "CUSTOM MULTI FISH EYE");

        sAntiDistortion.put(1, "ANTI-ENABLE");
        sAntiDistortion.put(0, "ANTI-DISABLE");

        sPitchFilter.put(1, "FILTER PITCH");
        sPitchFilter.put(0, "FILTER NOP");

        sFlingEnabled.put(1, "FLING ENABLED");
        sFlingEnabled.put(0, "FLING DISABLED");
    }

    private MDVRLibrary mVRLibrary;

    // load resource from android drawable and remote url.
    private MDVRLibrary.IImageLoadProvider mImageLoadProvider = null;

    // load resource from android drawable only.
    private MDVRLibrary.IImageLoadProvider mAndroidProvider;

    private List<MDAbsPlugin> plugins = new LinkedList<>();

    private MDPosition logoPosition = MDMutablePosition.newInstance().setY(-8.0f).setYaw(-90.0f);

    private MDPosition[] positions = new MDPosition[]{
            MDPosition.newInstance().setZ(-8.0f).setYaw(-45.0f),
            MDPosition.newInstance().setZ(-18.0f).setYaw(15.0f).setAngleX(15),
            MDPosition.newInstance().setZ(-10.0f).setYaw(-10.0f).setAngleX(-15),
            MDPosition.newInstance().setZ(-10.0f).setYaw(30.0f).setAngleX(30),
            MDPosition.newInstance().setZ(-10.0f).setYaw(-30.0f).setAngleX(-30),
            MDPosition.newInstance().setZ(-5.0f).setYaw(30.0f).setAngleX(60),
            MDPosition.newInstance().setZ(-3.0f).setYaw(15.0f).setAngleX(-45),
            MDPosition.newInstance().setZ(-3.0f).setYaw(15.0f).setAngleX(-45).setAngleY(45),
            MDPosition.newInstance().setZ(-3.0f).setYaw(0.0f).setAngleX(90),
    };
    protected IMediaPlayer mPlayer;
    private IjkMediaPlayer.OnPreparedListener mPreparedListener;
    private IjkMediaPlayer.OnCompletionListener mOnCompletionListener;
    private IjkMediaPlayer.OnBufferingUpdateListener mOnBufferingUpdateListener;
    private IjkMediaPlayer.OnErrorListener mOnErrorListener;
    private IjkMediaPlayer.OnInfoListener mOnInfoListener;
    private final int STATUS_ERROR = -1;
    private static final int STATUS_IDLE = 0;
    private static final int STATUS_PREPARING = 1;
    private static final int STATUS_PREPARED = 2;
    private static final int STATUS_STARTED = 3;
    private static final int STATUS_PAUSED = 4;
    private static final int STATUS_STOPPED = 5;
    private static final int STATUS_COMPLETED = 6;
    private int mStatus = STATUS_IDLE;
    private Activity mContext;
    private String mUri;
    private boolean mIsCircle = false;
    private boolean mIsGlass = false;
    private boolean mIsTouch = false;
    private View mGlView;
    public static final String SCALETYPE_FITPARENT = "fitParent";
    public static final String SCALETYPE_FILLPARENT = "fillParent";
    public static final String SCALETYPE_WRAPCONTENT = "wrapContent";
    public static final String SCALETYPE_FITXY = "fitXY";
    public static final String SCALETYPE_16_9 = "16:9";
    public static final String SCALETYPE_4_3 = "4:3";

    private RYVideoView videoView;
    private AudioManager audioManager;
    public GestureDetector gestureDetector;
    private boolean portrait;
    private boolean isLive = false;//是否为直播
    private int currentPosition;

    public void live(boolean isLive) {
        this.isLive = isLive;
        if(videoView!=null){
            if(isLive){
                videoView.setPlayer(2);
            }else{
                videoView.setPlayer(1);
            }
        }
        return;
    }
    public MediaPlayerWrapper(Activity context, View glView) {
        mContext = context;
        if (glView != null) {
            mGlView = glView;
            initLib();
        }
    }

    public MediaPlayerWrapper(Activity context, RYVideoView videoView) {
        mContext = context;
        this.videoView = videoView;
        videoView.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(IMediaPlayer mp) {
                if (mOnCompletionListener != null) {
                    mOnCompletionListener.onCompletion(mp);
                }
            }
        });
        videoView.setOnErrorListener(new IMediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(IMediaPlayer mp, int what, int extra) {
                if (mOnErrorListener != null) {
                    mOnErrorListener.onError(mp, what, extra);
                }
                return true;
            }
        });
        videoView.setOnInfoListener(new IMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(IMediaPlayer mp, int what, int extra) {
                if (mOnInfoListener != null) {
                    mOnInfoListener.onInfo(mp, what, extra);
                }
                return false;
            }
        });
        audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        portrait = getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

    }

    private int getScreenOrientation() {
        int rotation = mContext.getWindowManager().getDefaultDisplay().getRotation();
        DisplayMetrics dm = new DisplayMetrics();
        mContext.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        int orientation;
        // if the device's natural orientation is portrait:
        if ((rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) && height >
                width ||
                (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) && width >
                        height) {
            switch (rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_180:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                case Surface.ROTATION_270:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                default:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
            }
        }
        // if the device's natural orientation is landscape or if the device
        // is square:
        else {
            switch (rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_180:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                case Surface.ROTATION_270:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                default:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
            }
        }
        return orientation;
    }

    protected void initLib() {
        mImageLoadProvider = new ImageLoadProvider();
        mVRLibrary = createVRLibrary();
        mAndroidProvider = new AndroidProvider(mContext);
    }

    protected MDVRLibrary createVRLibrary() {
        return MDVRLibrary.with(mContext)
                .displayMode(DISPLAY_MODE_NORMAL)
                .interactiveMode(MDVRLibrary.INTERACTIVE_MODE_MOTION)
                .asVideo(new MDVRLibrary.IOnSurfaceReadyCallback() {
                    @Override
                    public void onSurfaceReady(Surface surface) {
                        setSurface(surface);
                    }
                })
                .ifNotSupport(new MDVRLibrary.INotSupportCallback() {
                    @Override
                    public void onNotSupport(int mode) {
                        String tip = mode == MDVRLibrary.INTERACTIVE_MODE_MOTION
                                ? "onNotSupport:MOTION" : "onNotSupport:" + String.valueOf(mode);
                        Toast.makeText(mContext, tip, Toast.LENGTH_SHORT).show();
                    }
                })
                .pinchConfig(new MDPinchConfig().setMin(1.0f).setMax(8.0f).setDefaultValue(0.1f))
                .pinchEnabled(true)
                .directorFactory(new MD360DirectorFactory() {
                    @Override
                    public MD360Director createDirector(int index) {
                        return MD360Director.builder().setPitch(90).build();
                    }
                })
                .projectionFactory(new CustomProjectionFactory())
                .barrelDistortionConfig(new BarrelDistortionConfig().setDefaultEnabled(false)
                        .setScale(0.95f))
                .build(mGlView);
    }

    public MDVRLibrary getVRLibrary() {
        return mVRLibrary;
    }


    private class ImageLoadProvider implements MDVRLibrary.IImageLoadProvider {

        private SimpleArrayMap<Uri, Target> targetMap = new SimpleArrayMap<>();

        @Override
        public void onProvideBitmap(final Uri uri, final MD360BitmapTexture.Callback callback) {

            final Target target = new Target() {

                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    // texture
                    callback.texture(bitmap);
                    targetMap.remove(uri);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    targetMap.remove(uri);
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            };
            targetMap.put(uri, target);
            Picasso.with(App.getContext()).load(uri).resize(callback.getMaxTextureSize(), callback
                    .getMaxTextureSize()).onlyScaleDown().centerInside().memoryPolicy(NO_CACHE,
                    NO_STORE).into(target);
        }
    }

    public void setQuickPlay(float speed) {
        if (mPlayer != null && mPlayer instanceof IjkMediaPlayer) {
            ((IjkMediaPlayer) mPlayer).setSpeed(speed);
            ((IjkMediaPlayer) mPlayer).setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER,
                    "soundtouch", 0);
        }
    }

    public void setCircle(boolean isCircle) {
        mIsCircle = isCircle;
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    public String doShortcut(String path) {
        if (path == null) {
            return null;
        }
        if (videoView != null) {
            return videoView.doShortcut(path);
        } else {
            Bitmap bitmap = null;
            if (mGlView instanceof GLTextureView) {
                bitmap = ((GLTextureView) mGlView).getBitmap();
            }
            if (bitmap != null) {
                return saveBitmap2Album(bitmap, path);
            }
        }
        return null;
    }

    private String saveBitmap2Album(Bitmap bitmap, String path) {
        //插入相册uri
        if (TextUtils.isEmpty(path)) {
            return null;
        }

        try {
            int splitIndex = path.lastIndexOf("/");
            String filePath = path.substring(0, splitIndex);
            String fileName = path.substring(splitIndex + 1, path.length());
            File qrCache = new File(filePath);//储存路径可以存在内部存储
            if (!qrCache.exists()) {
                qrCache.mkdir();
            }
            //String fileName = System.currentTimeMillis() + ".png";//图片文件名字
            File picFile = new File(qrCache, fileName);
            MediaStore.Images.Media.insertImage(mContext.getContentResolver(), bitmap,
                    fileName, null);
//发送广播刷新图片
            mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri
                    .parse(picFile.getParent())));
            return fileName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    private void enableHardwareDecoding() {
        if (mPlayer instanceof IjkMediaPlayer) {
            IjkMediaPlayer player = (IjkMediaPlayer) mPlayer;
            player.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);
            player.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 1);
            player.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", IjkMediaPlayer
                    .SDL_FCC_RV32);
            player.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 60);
            player.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "max-fps", 0);
            player.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48);
        }
    }

    public void setGlass(boolean isGlass) {
        mIsGlass = isGlass;
        int mode = isGlass ? MDVRLibrary.DISPLAY_MODE_GLASS : DISPLAY_MODE_NORMAL;
        if (getVRLibrary() != null) {
            getVRLibrary().switchDisplayMode(mContext, mode);
        } else {
            LogUtils.d(TAG, "VRLibrary is not initialized");
        }
    }

    public void setTouchMode(boolean isTouch) {
        mIsTouch = isTouch;
        int mode = isTouch ? MDVRLibrary.INTERACTIVE_MODE_TOUCH : MDVRLibrary
                .INTERACTIVE_MODE_MOTION;
        if (getVRLibrary() != null) {
            getVRLibrary().switchInteractiveMode(mContext, mode);
        } else {
            LogUtils.d(TAG, "VRLibrary is not initialized");
        }
    }

    public void setSurface(Surface surface) {
        if (getPlayer() != null) {
            getPlayer().setSurface(surface);
        }
    }

    public void play(String url) {
        mStatus = STATUS_IDLE;
        if (getVRLibrary() != null) {
            mPlayer = new IjkMediaPlayer();
            mPlayer.setOnPreparedListener(this);
            mPlayer.setOnErrorListener(new IMediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
                    if (mOnErrorListener != null) {
                        mOnErrorListener.onError(iMediaPlayer, i, i1);
                    }
                    return false;
                }
            });
            mPlayer.setOnInfoListener(new IMediaPlayer.OnInfoListener() {
                @Override
                public boolean onInfo(IMediaPlayer mp, int what, int extra) {
                    if (mOnInfoListener != null) {
                        mOnInfoListener.onInfo(mp, what, extra);
                    }
                    return false;
                }
            });
            mPlayer.setOnVideoSizeChangedListener(new IMediaPlayer.OnVideoSizeChangedListener() {
                @Override
                public void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int sar_num,
                                               int sar_den) {
                    getVRLibrary().onTextureResize(width, height);
                }
            });
            mPlayer.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(IMediaPlayer iMediaPlayer) {
                    if (mIsCircle) {
                        stop();
                        if (mUri != null) {
                            play(mUri);
                            prepare();
                        }
                    }
                    if (mOnCompletionListener != null) {
                        mOnCompletionListener.onCompletion(iMediaPlayer);
                    }
                }
            });
            enableHardwareDecoding();
            try {
                mPlayer.setDataSource(url);
                mUri = url;
                prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            mUri = url;
            if (videoView != null) {
                videoView.setVideoPath(url);
                start();
            }
        }
    }

    public void play(Context context, String assetPath) {
        try {
            AssetManager am = context.getResources().getAssets();
            final InputStream is = am.open(assetPath);
            mPlayer.setDataSource(new IMediaDataSource() {
                @Override
                public int readAt(long position, byte[] buffer, int offset, int size) throws
                        IOException {
                    return is.read(buffer, offset, size);
                }

                @Override
                public long getSize() throws IOException {
                    return is.available();
                }

                @Override
                public void close() throws IOException {
                    is.close();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public IMediaPlayer getPlayer() {
        return mPlayer;
    }

    public void prepare() {
        if (mPlayer == null) return;
        if (mStatus == STATUS_IDLE || mStatus == STATUS_STOPPED) {
            mPlayer.prepareAsync();
            mStatus = STATUS_PREPARING;
        }
    }

    public void stop() {
        if (mVRLibrary != null) {
            mVRLibrary.onPause(mContext);
        }
        if (mPlayer == null) return;
        if (mStatus == STATUS_STARTED || mStatus == STATUS_PAUSED) {
            mPlayer.stop();
            mStatus = STATUS_STOPPED;
        }
    }

    public boolean isPlaying() {
        if (mVRLibrary != null) {
            return mPlayer != null ? mPlayer.isPlaying() : false;
        } else {
            return videoView != null ? videoView.isPlaying() : false;
        }
    }

    public long getDuration() {
        if (videoView != null) {
            return videoView.getDuration();
        } else {
            if (mVRLibrary != null && mPlayer != null) {
                return mPlayer.getDuration();
            }
        }
        return 0;
    }

    public boolean onBackPressed() {
        if (getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            mContext.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            return true;
        }
        return false;
    }

    public void pause() {
        if (mVRLibrary != null) {
            mVRLibrary.onPause(mContext);
            if (mPlayer == null) return;
            if (mPlayer.isPlaying() && mStatus == STATUS_STARTED) {
                mPlayer.pause();
                mStatus = STATUS_PAUSED;
            }
        } else {
            if (videoView == null) return;
            if (videoView.isPlaying() && mStatus == STATUS_STARTED) {
                videoView.pause();
                if (!isLive) {
                    currentPosition = videoView.getCurrentPosition();
                }
                mStatus = STATUS_PAUSED;
            }
        }
    }

    public void resume() {
        if (mVRLibrary != null) {
            mVRLibrary.onResume(mContext);
        } else {
            if (videoView == null) {
                return;
            }
            if (mStatus == STATUS_STARTED) {
                if (isLive) {
                    videoView.seekTo(0);
                } else {
                    if (currentPosition > 0) {
                        videoView.seekTo(currentPosition);
                    }
                }
            }
        }
        start();

    }

    private void start() {
        if (mVRLibrary != null) {
            if (mPlayer == null) return;
            if (mStatus == STATUS_PREPARED || mStatus == STATUS_PAUSED) {
                mPlayer.start();
                mStatus = STATUS_STARTED;
            }
        } else {
            if (videoView != null) {
                videoView.start();
                mStatus = STATUS_STARTED;
            }
        }
    }

    public void setScaleType(String scaleType) {
        if (videoView == null) {
            return;
        }
        if (SCALETYPE_FITPARENT.equals(scaleType)) {
            videoView.setAspectRatio(IRenderView.AR_ASPECT_FIT_PARENT);
        } else if (SCALETYPE_FILLPARENT.equals(scaleType)) {
            videoView.setAspectRatio(IRenderView.AR_ASPECT_FILL_PARENT);
        } else if (SCALETYPE_WRAPCONTENT.equals(scaleType)) {
            videoView.setAspectRatio(IRenderView.AR_ASPECT_WRAP_CONTENT);
        } else if (SCALETYPE_FITXY.equals(scaleType)) {
            videoView.setAspectRatio(IRenderView.AR_MATCH_PARENT);
        } else if (SCALETYPE_16_9.equals(scaleType)) {
            videoView.setAspectRatio(IRenderView.AR_16_9_FIT_PARENT);
        } else if (SCALETYPE_4_3.equals(scaleType)) {
            videoView.setAspectRatio(IRenderView.AR_4_3_FIT_PARENT);
        }
    }

    public void setOnPreparedListener(IMediaPlayer.OnPreparedListener mPreparedListener) {
        this.mPreparedListener = mPreparedListener;
    }

    public void setOnCompletionListener(IMediaPlayer.OnCompletionListener mOnCompletionListener) {
        this.mOnCompletionListener = mOnCompletionListener;
    }

    public void setOnErrorListener(IMediaPlayer.OnErrorListener mOnErrorListener) {
        this.mOnErrorListener = mOnErrorListener;
    }

    public void setOnInfoListener(IMediaPlayer.OnInfoListener mOnInfoListener) {
        this.mOnInfoListener = mOnInfoListener;
    }

    @Override
    public void onPrepared(IMediaPlayer mp) {
        mStatus = STATUS_PREPARED;
        start();
        if (mPreparedListener != null) mPreparedListener.onPrepared(mp);
        if (getVRLibrary() != null) {
            getVRLibrary().notifyPlayerChanged();
            getVRLibrary().onResume(mContext);
        }
    }


    public void destroy() {
        if (mVRLibrary != null) {
            if (mPlayer != null) {
                mPlayer.setSurface(null);
                mPlayer.release();
            }
            mPlayer = null;
            stop();
            mVRLibrary.onDestroy();
        } else {
            if (videoView != null) {
                videoView.stopPlayback();
            }
        }
    }

    public void switchResolution(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (mVRLibrary != null) {
            if (mPlayer != null) {
                mPlayer.stop();
                mPlayer.release();
                play(url);
            }
        } else {
            if (videoView != null) {
                videoView.stopPlayback();
                videoView.release(true);
                videoView.refreshRender();
                videoView.setVideoURI(Uri.parse(url));
                videoView.start();
            }
        }
    }

    public void playInFullScreen(boolean fullScreen) {
        if (fullScreen) {
            mContext.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            mContext.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        return;
    }


    // android impl
    private class AndroidProvider implements MDVRLibrary.IImageLoadProvider {

        Activity activity;

        public AndroidProvider(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void onProvideBitmap(Uri uri, MD360BitmapTexture.Callback callback) {
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(activity.getContentResolver()
                        .openInputStream(uri));
                callback.texture(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
    }
}

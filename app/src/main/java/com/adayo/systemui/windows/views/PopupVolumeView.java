package com.adayo.systemui.windows.views;

import static com.adayo.proxy.setting.system.contants.SettingsContantsDef.SETTING_STREAM_BLUETOOTH_SCO;
import static com.adayo.proxy.setting.system.contants.SettingsContantsDef.SETTING_STREAM_MUSIC;
import static com.adayo.proxy.setting.system.contants.SettingsContantsDef.SETTING_STREAM_NAVI;
import static com.adayo.proxy.setting.system.contants.SettingsContantsDef.SETTING_STREAM_RING;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.systemui.bean.VolumeInfo;
import com.adayo.systemui.interfaces.BaseCallback;
import com.adayo.systemui.manager.VolumeControllerImpl;
import com.adayo.systemui.utils.LogUtil;
import com.android.systemui.R;

/**
 * @author ADAYO-4170
 */
public class PopupVolumeView extends LinearLayout implements BaseCallback<VolumeInfo> {
    private BlurTransitionView btv_volume;
    private VolumeSeekBar mediaVolume, ringVolume, phoneVolume, naviVolume;

//    private TextView mediaValue, ringValue, phoneValue, naviValue;

    public PopupVolumeView(Context context) {
        super(context);
        initView();
    }

    public PopupVolumeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public PopupVolumeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
        initData();
    }

    private void initData() {
        VolumeControllerImpl.getInstance().addCallback(this);
        btv_volume.setOnClickListener(v -> {

        });
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        VolumeInfo volumeInfo = VolumeControllerImpl.getInstance().getCurrentDataInfo();
        if (null != volumeInfo) {
            if (null != mediaVolume) {
                mediaVolume.setProgress(volumeInfo.getMediaVolume());
                //mediaValue.setText(String.valueOf(volumeInfo.getMediaVolume()));
            }
            if (null != ringVolume) {
                ringVolume.setProgress(volumeInfo.getBellVolume());
                //ringValue.setText(String.valueOf(volumeInfo.getBellVolume()));
            }
            if (null != phoneVolume) {
                phoneVolume.setProgress(volumeInfo.getBluetoothVolume());
                //phoneValue.setText(String.valueOf(volumeInfo.getBluetoothVolume()));
            }
            if (null != naviVolume) {
                naviVolume.setProgress(volumeInfo.getNaviVolume());
                //naviValue.setText(String.valueOf(volumeInfo.getNaviVolume()));
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @SuppressLint("MissingInflatedId")
    private void initView() {
        View mRootView = AAOP_HSkin.getLayoutInflater(getContext()).inflate(R.layout.volume_popup_layout, this, true);
        btv_volume = mRootView.findViewById(R.id.btv_volume);
        mediaVolume = mRootView.findViewById(R.id.media_volume);
        ringVolume = mRootView.findViewById(R.id.ring_volume);
        phoneVolume = mRootView.findViewById(R.id.phone_volume);
        naviVolume = mRootView.findViewById(R.id.navi_volume);
        //mediaValue = mRootView.findViewById(R.id.mediaValue);
        //ringValue = mRootView.findViewById(R.id.ringValue);
        //phoneValue = mRootView.findViewById(R.id.phoneValue);
        //naviValue = mRootView.findViewById(R.id.naviValue);
        mediaVolume.setMax(39);
        ringVolume.setMax(39);
        phoneVolume.setMax(39);
        naviVolume.setMax(39);
        mediaVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    type = SETTING_STREAM_MUSIC;
                    value = i;
                    //mediaValue.setText(String.valueOf(value));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                type = SETTING_STREAM_MUSIC;
                value = seekBar.getProgress();
                //mediaValue.setText(String.valueOf(value));
                mHandlerSeek.postDelayed(runnable, 200);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                type = SETTING_STREAM_MUSIC;
                value = seekBar.getProgress();
                //mediaValue.setText(String.valueOf(value));
                mHandlerSeek.removeCallbacks(runnable);
                VolumeControllerImpl.getInstance().setVolume(type, value);
            }
        });
        ringVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    type = SETTING_STREAM_RING;
                    value = i;
                    //ringValue.setText(String.valueOf(value));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                type = SETTING_STREAM_RING;
                value = seekBar.getProgress();
                //ringValue.setText(String.valueOf(value));
                mHandlerSeek.postDelayed(runnable, 200);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                type = SETTING_STREAM_RING;
                value = seekBar.getProgress();
                //ringValue.setText(String.valueOf(value));
                mHandlerSeek.removeCallbacks(runnable);
                VolumeControllerImpl.getInstance().setVolume(type, value);
            }
        });
        phoneVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    type = SETTING_STREAM_BLUETOOTH_SCO;
                    value = i;
                    //phoneValue.setText(String.valueOf(value));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                type = SETTING_STREAM_BLUETOOTH_SCO;
                value = seekBar.getProgress();
                //phoneValue.setText(String.valueOf(value));
                mHandlerSeek.postDelayed(runnable, 200);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                type = SETTING_STREAM_BLUETOOTH_SCO;
                value = seekBar.getProgress();
                //phoneValue.setText(String.valueOf(value));
                mHandlerSeek.removeCallbacks(runnable);
                VolumeControllerImpl.getInstance().setVolume(type, value);
            }
        });
        naviVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    type = SETTING_STREAM_NAVI;
                    value = i;
                    //naviValue.setText(String.valueOf(value));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                type = SETTING_STREAM_NAVI;
                value = seekBar.getProgress();
                //naviValue.setText(String.valueOf(value));
                mHandlerSeek.postDelayed(runnable, 200);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                type = SETTING_STREAM_NAVI;
                value = seekBar.getProgress();
                //naviValue.setText(String.valueOf(value));
                mHandlerSeek.removeCallbacks(runnable);
                VolumeControllerImpl.getInstance().setVolume(type, value);
            }
        });
    }

    private int type = -1;
    private int value = -1;
    private Handler mHandlerSeek = new Handler();
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            LogUtil.i("setDeviceFuncUniversalInterface runnable: type =  " + type + " ; value = " + value);
            if (value != -1 && type != -1) {
                VolumeControllerImpl.getInstance().setVolume(type, value);
                mHandlerSeek.postDelayed(runnable, 200);
            }
        }
    };

    @Override
    public void onDataChange(VolumeInfo data) {
        mediaVolume.setProgress(data.getMediaVolume());
        ringVolume.setProgress(data.getBellVolume());
        phoneVolume.setProgress(data.getBluetoothVolume());
        naviVolume.setProgress(data.getNaviVolume());
        //mediaValue.setText(String.valueOf(data.getMediaVolume()));
        //ringValue.setText(String.valueOf(data.getBellVolume()));
        //phoneValue.setText(String.valueOf(data.getBluetoothVolume()));
        //naviValue.setText(String.valueOf(data.getNaviVolume()));
    }
}

package com.adayo.systemui.windows.views;

import static com.adayo.proxy.setting.system.contants.SettingsContantsDef.SETTING_STREAM_BLUETOOTH_SCO;
import static com.adayo.proxy.setting.system.contants.SettingsContantsDef.SETTING_STREAM_MUSIC;
import static com.adayo.proxy.setting.system.contants.SettingsContantsDef.SETTING_STREAM_NAVI;
import static com.adayo.proxy.setting.system.contants.SettingsContantsDef.SETTING_STREAM_RING;

import android.content.Context;
import android.os.Handler;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.adayo.systemui.bean.VolumeInfo;
import com.adayo.systemui.interfaces.BaseCallback;
import com.adayo.systemui.manager.VolumeControllerImpl;
import com.adayo.systemui.utils.LogUtil;
import com.adayo.systemui.windows.dialogs.BaseDialog;
import com.android.systemui.R;

public class PopupVolumeDialog extends BaseDialog implements BaseCallback<VolumeInfo> {

    private ScreenVolumeSeekBar mediaVolume, ringVolume, phoneVolume, naviVolume;

    private TextView mediaValue, ringValue, phoneValue, naviValue;


    public PopupVolumeDialog(@NonNull Context context, int dialogWidth, int _dialogHeight) {
        super(context, dialogWidth, _dialogHeight);
    }

    @Override
    protected void initView() {
        mediaVolume = findViewById(R.id.media_volume);
        ringVolume = findViewById(R.id.ring_volume);
        phoneVolume = findViewById(R.id.phone_volume);
        naviVolume = findViewById(R.id.navi_volume);
        mediaValue = findViewById(R.id.mediaValue);
        ringValue = findViewById(R.id.ringValue);
        phoneValue = findViewById(R.id.phoneValue);
        naviValue = findViewById(R.id.naviValue);
        mediaVolume.setMax(39);
        ringVolume.setMax(39);
        phoneVolume.setMax(39);
        naviVolume.setMax(39);
    }

    @Override
    protected void initData() {
        VolumeControllerImpl.getInstance().addCallback(this);
    }

    @Override
    protected void initListener() {
        mediaVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(b){
                    type = SETTING_STREAM_MUSIC;
                    value = i;
                    mediaValue.setText(String.valueOf(value));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                type = SETTING_STREAM_MUSIC;
                value = seekBar.getProgress();
                mediaValue.setText(String.valueOf(value));
                mHandlerSeek.postDelayed(runnable, 200);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                type = SETTING_STREAM_MUSIC;
                value = seekBar.getProgress();
                mediaValue.setText(String.valueOf(value));
                mHandlerSeek.removeCallbacks(runnable);
                VolumeControllerImpl.getInstance().setVolume(type, value);
            }
        });
        ringVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(b){
                    type = SETTING_STREAM_RING;
                    value = i;
                    ringValue.setText(String.valueOf(value));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                type = SETTING_STREAM_RING;
                value = seekBar.getProgress();
                ringValue.setText(String.valueOf(value));
                mHandlerSeek.postDelayed(runnable, 200);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                type = SETTING_STREAM_RING;
                value = seekBar.getProgress();
                ringValue.setText(String.valueOf(value));
                mHandlerSeek.removeCallbacks(runnable);
                VolumeControllerImpl.getInstance().setVolume(type, value);
            }
        });
        phoneVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(b){
                    type = SETTING_STREAM_BLUETOOTH_SCO;
                    value = i;
                    phoneValue.setText(String.valueOf(value));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                type = SETTING_STREAM_BLUETOOTH_SCO;
                value = seekBar.getProgress();
                phoneValue.setText(String.valueOf(value));
                mHandlerSeek.postDelayed(runnable, 200);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                type = SETTING_STREAM_BLUETOOTH_SCO;
                value = seekBar.getProgress();
                phoneValue.setText(String.valueOf(value));
                mHandlerSeek.removeCallbacks(runnable);
                VolumeControllerImpl.getInstance().setVolume(type, value);
            }
        });
        naviVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(b){
                    type = SETTING_STREAM_NAVI;
                    value = i;
                    naviValue.setText(String.valueOf(value));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                type = SETTING_STREAM_NAVI;
                value = seekBar.getProgress();
                naviValue.setText(String.valueOf(value));
                mHandlerSeek.postDelayed(runnable, 200);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                type = SETTING_STREAM_NAVI;
                value = seekBar.getProgress();
                naviValue.setText(String.valueOf(value));
                mHandlerSeek.removeCallbacks(runnable);
                VolumeControllerImpl.getInstance().setVolume(type, value);
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.pop_volume_dialog_layout;
    }

    private int type = -1;
    private int value = -1;
    private Handler mHandlerSeek = new Handler();
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            LogUtil.i("setDeviceFuncUniversalInterface runnable: type =  " + type + " ; value = " + value);
            if (value != -1 && type != -1){
                VolumeControllerImpl.getInstance().setVolume(type, value);
                mHandlerSeek.postDelayed(runnable,200);
            }
        }
    };

    @Override
    public void onDataChange(VolumeInfo data) {
        mediaVolume.setProgress(data.getMediaVolume());
        ringVolume.setProgress(data.getBellVolume());
        phoneVolume.setProgress(data.getBluetoothVolume());
        naviVolume.setProgress(data.getNaviVolume());
        mediaValue.setText(String.valueOf(data.getMediaVolume()));
        ringValue.setText(String.valueOf(data.getBellVolume()));
        phoneValue.setText(String.valueOf(data.getBluetoothVolume()));
        naviValue.setText(String.valueOf(data.getNaviVolume()));
    }
}

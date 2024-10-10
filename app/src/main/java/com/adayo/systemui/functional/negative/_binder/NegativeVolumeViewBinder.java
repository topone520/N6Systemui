package com.adayo.systemui.functional.negative._binder;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.adayo.soavb.foundation.binder.AbstractViewBinder;
import com.adayo.soavb.foundation.provider.ViewBinderProviderInteger;
import com.adayo.systemui.bean.VolumeInfo;
import com.adayo.systemui.interfaces.BaseCallback;
import com.adayo.systemui.manager.ReportBcmManager;
import com.adayo.systemui.manager.VolumeControllerImpl;
import com.adayo.systemui.utils.LogUtil;
import com.adayo.systemui.windows.views.PopupVolumeDialog;
import com.adayo.systemui.windows.views.QsSeekBar;
import com.android.systemui.R;
import com.android.systemui.SystemUIApplication;

public class NegativeVolumeViewBinder extends AbstractViewBinder<Integer> implements BaseCallback<VolumeInfo> {

    public static final int SETTING_STREAM_MUSIC = 3;        //媒体音量（USB音乐音量）
    private QsSeekBar _volume_adjust;
    private ImageView _img_volume_edit;
    private Handler mHandlerSeek = new Handler();
    private int type = -1;
    private int value = -1;

    public NegativeVolumeViewBinder() {
        super(new ViewBinderProviderInteger.Builder().build());
    }


    @Override
    protected void _bind_view(View view) {
        _volume_adjust = view.findViewById(R.id.volume_adjust);
        _img_volume_edit = view.findViewById(R.id.img_volume_edit);
        _volume_adjust.setMax(39);
        VolumeControllerImpl.getInstance().addCallback(this);

        _volume_adjust.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                LogUtil.d("volumeBar---" + progress + " ,fromUser: " + fromUser);
                type = SETTING_STREAM_MUSIC;
                value = progress;
                VolumeControllerImpl.getInstance().setVolume(type, value);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        _img_volume_edit.setOnClickListener(v -> {
            PopupVolumeDialog popupVolumeDialog = new PopupVolumeDialog(SystemUIApplication.getSystemUIContext(), 1460, 508);
            popupVolumeDialog.show();
        });
    }

    @Override
    protected void _update_ui(Integer value) {

    }

    @Override
    protected void _update_ui(Bundle bundle) {

    }

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
        LogUtil.d("ccm------> onDataChange =  " + data.getMediaVolume());
        _volume_adjust.setProgress(data.getMediaVolume());
    }
}

package com.adayo.systemui.functional.statusbar._binder;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.soavb.foundation.binder.AbstractViewBinder;
import com.adayo.soavb.foundation.provider.ViewBinderProviderInteger;
import com.adayo.systemui.utils.OTAUpGradeUtil;
import com.adayo.systemui.windows.dialogs.OTAUpdateDialog;
import com.android.systemui.R;
import com.android.systemui.SystemUIApplication;

public class StatusOtaViewBinder extends AbstractViewBinder<Integer> {

    private ImageView _icon_ota;
    private String otaTitle;
    private CharSequence otaContent;
    private String otaTime;
    private int otaState = -1;
    public static final String TAG = "_StatusOtaViewBinder";

    public StatusOtaViewBinder() {
        super(new ViewBinderProviderInteger.Builder().build());
    }

    @Override
    protected void _bind_view(View view) {
        _icon_ota = view.findViewById(R.id.icon_ota);
        _icon_ota.setTag(false);

        _icon_ota.setOnClickListener(v -> {
            OTAUpdateDialog.createDialog(SystemUIApplication.getSystemUIContext(), R.style.TransparentStyle)
                    .setTitleName(otaTitle)
                    .setTips(otaContent)
                    .setTime(otaTime)
                    .show();
        });

        OTAUpGradeUtil.getInstance().init("msg_event_upgrade_status");
        if (OTAUpGradeUtil.isOnline){
            otaState = OTAUpGradeUtil.getInstance().getUpGradeStatus();
        }
        otaIconStatus(otaState);
    }

    public void otaIconSetSelected(String title,CharSequence content,String time){
        otaTitle = title;
        otaContent = content;
        otaTime = time;
        _icon_ota.setSelected(true);
    }

    public void otaIconStatus(int mOtaState){
        otaState = mOtaState;
        Log.i(TAG,"otaIconStatus otaState: " + otaState);
        switch (otaState){
            case 0:
                //待下载
                _icon_ota.setVisibility(View.VISIBLE);
                AAOP_HSkin.with(_icon_ota).addViewAttrs(AAOP_HSkin.ATTR_SRC,R.mipmap.ivi_top_icon_ota_message_n_1).applySkin(false);
                break;
            case 1:
                //下载中
                _icon_ota.setVisibility(View.VISIBLE);
                AAOP_HSkin.with(_icon_ota).addViewAttrs(AAOP_HSkin.ATTR_SRC,R.mipmap.ivi_top_icon_ota_download_n_1).applySkin(false);
                break;
            case 2:
                //下载异常
//                _icon_ota.setVisibility(View.VISIBLE);
//                AAOP_HSkin.with(_icon_ota).addViewAttrs(AAOP_HSkin.ATTR_SRC,R.mipmap.ivi_top_icon_ota_reservation_n).applySkin(false);
                break;
            case 3:
                //下载暂停
                _icon_ota.setVisibility(View.VISIBLE);
                AAOP_HSkin.with(_icon_ota).addViewAttrs(AAOP_HSkin.ATTR_SRC,R.mipmap.ivi_top_icon_ota_suspend_n_1).applySkin(false);
                break;
            case 4:
                //下载完成（可升级提醒）
                _icon_ota.setVisibility(View.VISIBLE);
                AAOP_HSkin.with(_icon_ota).addViewAttrs(AAOP_HSkin.ATTR_SRC,R.mipmap.ivi_top_icon_ota_complete_n_1).applySkin(false);
                break;
            case 5:
                //有预约安装
                _icon_ota.setVisibility(View.VISIBLE);
                AAOP_HSkin.with(_icon_ota).addViewAttrs(AAOP_HSkin.ATTR_SRC,R.mipmap.ivi_top_icon_ota_reservation_n_1).applySkin(false);
                break;
            case 6:
                //清除小狐狸
                _icon_ota.setVisibility(View.GONE);
                break;
            case 7:
                //第一次授权请求，弹出狐狸图标
                break;
            default:
                _icon_ota.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    protected void _update_ui(Integer integer) {

    }

    @Override
    protected void _update_ui(Bundle bundle) {

    }
}
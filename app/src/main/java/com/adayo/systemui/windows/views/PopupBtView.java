package com.adayo.systemui.windows.views;

import android.annotation.SuppressLint;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.proxy.infrastructure.adayosource.AdayoSource;
import com.adayo.proxy.infrastructure.sourcemng.Beans.AppConfigType;
import com.adayo.systemui.bean.BluetoothInfo;
import com.adayo.systemui.interfaces.BaseCallback;
import com.adayo.systemui.manager.BluetoothControllerImpl;
import com.adayo.systemui.manager.PopupsManager;
import com.adayo.systemui.manager.SourceControllerImpl;
import com.adayo.systemui.utils.LogUtil;
import com.adayo.systemui.windows.panels.HvacPanel;
import com.adayo.systemui.viewModel.BtViewModel;
import com.android.systemui.R;
import com.android.systemui.SystemUIApplication;

import java.util.HashMap;

public class PopupBtView extends LinearLayout {
    public PopupBtView(Context context) {
        super(context);
        initView();
    }

    public PopupBtView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public PopupBtView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        LogUtil.d("initView ");
        AAOP_HSkin.getLayoutInflater(getContext()).inflate(R.layout.popup_bt_layout, this, true);
    }


}

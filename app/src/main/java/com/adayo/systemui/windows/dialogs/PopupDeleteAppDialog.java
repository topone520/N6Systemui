package com.adayo.systemui.windows.dialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.adayo.systemui.bean.AppInfo;
import com.adayo.systemui.windows.dialogs.BaseDialog;
import com.adayo.systemui.windows.panels.ShowAppListDialog;
import com.android.systemui.R;
import com.android.systemui.SystemUIApplication;

public class PopupDeleteAppDialog extends BaseDialog implements View.OnClickListener{
    private TextView popTitle;
    private Button deleteBtn;
    private Button cancelBtn;

    private AppInfo appInfo;

    public PopupDeleteAppDialog(@NonNull Context context, int dialogWidth, int _dialogHeight) {
        super(context, dialogWidth, _dialogHeight);
    }

    @SuppressLint("MissingInflatedId")
    protected void initView() {
        popTitle = findViewById(R.id.pop_title);
        deleteBtn = findViewById(R.id.delete_btn);
        cancelBtn = findViewById(R.id.cancel_btn);
        deleteBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        popTitle.setText(SystemUIApplication.getSystemUIContext().getResources().getString(R.string.confirm_uninstallation) + appInfo.getAppName() + SystemUIApplication.getSystemUIContext().getResources().getString(R.string.screen_apply));
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.popup_delete_app_layout;
    }

    public void setAppInfo(AppInfo appInfo) {
        this.appInfo = appInfo;
    }

    public void setPopTitle(String text) {
        popTitle.setText(text);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.delete_btn) {
            ShowAppListDialog.getInstance().deleteApp(appInfo);
        } else if (viewId == R.id.cancel_btn) {
            ShowAppListDialog.getInstance().cancelDeleteApp();
        }
    }
}

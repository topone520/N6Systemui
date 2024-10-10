package com.adayo.systemui.windows.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.systemui.R;

public class IconView extends RelativeLayout {
    private ImageView foregroundIcon;
    private ImageView backgroundIcon;

    private boolean isAdded = false;

    public IconView(Context context) {
        super(context);
        initView();
    }

    public IconView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public IconView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        View mRootView = LayoutInflater.from(getContext()).inflate(R.layout.icon_view, this, true);
        foregroundIcon = mRootView.findViewById(R.id.foreground_icon);
        backgroundIcon = mRootView.findViewById(R.id.background_icon);
    }

    public void setForegroundIcon(int resId, int visibility){
        foregroundIcon.setVisibility(visibility);
        foregroundIcon.setImageResource(resId);
    }
    public void setForegroundIconVisibility(int visibility){
        foregroundIcon.setVisibility(visibility);
    }
    public void setBackgroundIcon(int resId){
        backgroundIcon.setImageResource(resId);
    }

    public void setAddTag(boolean isAdded){
        this.isAdded = isAdded;
    }
    public boolean getAddTag(){
        return isAdded;
    }
}

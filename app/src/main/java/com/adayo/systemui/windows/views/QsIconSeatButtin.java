package com.adayo.systemui.windows.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adayo.systemui.utils.LogUtil;
import com.android.systemui.R;

public class QsIconSeatButtin extends RelativeLayout {
    private ImageView iconImage;
    private TextView iconText;
    private ImageView iconSubscript;
    private LinearLayout iconLayout;
    public QsIconSeatButtin(Context context) {
        super(context);
        initView();
    }

    public QsIconSeatButtin(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public QsIconSeatButtin(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }
    private void initView() {
        View mRootView = LayoutInflater.from(getContext()).inflate(R.layout.qs_icon_seat_button, this, true);
        iconImage = mRootView.findViewById(R.id.icon_image);
        iconText = mRootView.findViewById(R.id.icon_text);
        iconSubscript = mRootView.findViewById(R.id.icon_subscript);
        iconLayout = mRootView.findViewById(R.id.icon_layout);
    }
    public void showIconSubscript() {
        iconSubscript.setVisibility(View.VISIBLE);
    }

    public void setIconImage(int resId) {
        iconImage.setImageResource(resId);
    }

    public void setIconText(String text) {
        iconText.setText(text);
    }

    public void setIconLayout() {
        iconLayout.setBackgroundResource(R.color.transparent);
    }
}

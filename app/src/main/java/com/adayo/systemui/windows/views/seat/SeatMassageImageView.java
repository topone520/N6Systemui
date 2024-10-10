package com.adayo.systemui.windows.views.seat;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.systemui.utils.LogUtil;
import com.android.systemui.R;


public class SeatMassageImageView extends FrameLayout {
    private final String TAG = SeatMassageImageView.class.getSimpleName();
    private ImageView _iv_massage;
    private TextView _tv_massage;
    private int[] _level_resource;

    public SeatMassageImageView(@NonNull Context context) {
        super(context);
    }

    public SeatMassageImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        _initialize_view(context);
    }

    private void _initialize_view(Context context) {
        addView(AAOP_HSkin.getLayoutInflater(context).inflate(R.layout.seat_massage_comm_layout, null));
        _iv_massage = (ImageView) findViewById(R.id.iv_massage);
        _tv_massage = (TextView) findViewById(R.id.tv_massage);
    }

    public void injectResource(int[] level_resources) {
        _level_resource = level_resources;
        initUpdateView(0);
    }

    public void _update_ui(int level) {
        LogUtil.d(TAG + "::_update_ui: " + level);
        initUpdateView(level);
    }

    private void initUpdateView(int level) {
        int MASSAGE_LEVEL_MAX = 3;
        if (level < 0 || level > MASSAGE_LEVEL_MAX) return;
        AAOP_HSkin.with(_iv_massage).addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, _level_resource[level]).applySkin(false);
        AAOP_HSkin.with(_tv_massage).addViewAttrs(AAOP_HSkin.ATTR_TEXT_COLOR, level != 0 ? R.color.seat_text_color1 : R.color.seat_text_color2).applySkin(false);

    }
}

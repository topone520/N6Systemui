package com.adayo.systemui.windows.views.hvac;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.systemui.contents.AreaConstant;
import com.android.systemui.R;

public class PMView extends FrameLayout {
    private final Context _context;
    private RelativeLayout relativeMild;
    private TextView tvSelf;
    private RelativeLayout relativeSevere;
    private TextView tvLevelSevere;


    public PMView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        _context = context;
        _initialize_view();
    }

    private void _initialize_view() {
        addView(AAOP_HSkin.getLayoutInflater(_context).inflate(R.layout.hvac_pm_layout, null));
        relativeMild = (RelativeLayout) findViewById(R.id.relative_mild);
        tvSelf = (TextView) findViewById(R.id.tv_self);
        relativeSevere = (RelativeLayout) findViewById(R.id.relative_severe);
        tvLevelSevere = (TextView) findViewById(R.id.tv_level_severe);
    }

    public void _update_ui(int number) {
        if (number < 0) return;
        if (number >= AreaConstant.PM_0 && number <= AreaConstant.PM_50) {
            relativeMild.setVisibility(VISIBLE);
            relativeSevere.setVisibility(GONE);
            tvSelf.setText(_context.getResources().getString(R.string.status_pm_excellent));
            AAOP_HSkin.with(tvSelf).addViewAttrs(AAOP_HSkin.ATTR_TEXT_COLOR, R.color.c41AC83).applySkin(false);

        } else if (number > AreaConstant.PM_50 && number <= AreaConstant.PM_100) {
            relativeMild.setVisibility(VISIBLE);
            relativeSevere.setVisibility(GONE);
            tvSelf.setText(_context.getResources().getString(R.string.status_pm_good));
            AAOP_HSkin.with(tvSelf).addViewAttrs(AAOP_HSkin.ATTR_TEXT_COLOR, R.color.ce1c556).applySkin(false);
        } else if (number > AreaConstant.PM_100 && number <= AreaConstant.PM_150) {
            relativeMild.setVisibility(GONE);
            relativeSevere.setVisibility(VISIBLE);
            tvLevelSevere.setText(_context.getResources().getString(R.string.status_pm_mild));
            AAOP_HSkin.with(tvSelf).addViewAttrs(AAOP_HSkin.ATTR_TEXT_COLOR, R.color.cCD8741).applySkin(false);
        } else if (number > AreaConstant.PM_150 && number <= AreaConstant.PM_200) {
            relativeMild.setVisibility(GONE);
            relativeSevere.setVisibility(VISIBLE);
            tvLevelSevere.setText(_context.getResources().getString(R.string.status_pm_moderate));
            AAOP_HSkin.with(tvSelf).addViewAttrs(AAOP_HSkin.ATTR_TEXT_COLOR, R.color.c_b9_45_3c).applySkin(false);
        } else if (number > AreaConstant.PM_200 && number <= AreaConstant.PM_300) {
            relativeMild.setVisibility(GONE);
            relativeSevere.setVisibility(VISIBLE);
            tvLevelSevere.setText(_context.getResources().getString(R.string.status_pm_severe));
            AAOP_HSkin.with(tvSelf).addViewAttrs(AAOP_HSkin.ATTR_TEXT_COLOR, R.color.c7744a).applySkin(false);
        } else {
            relativeMild.setVisibility(GONE);
            relativeSevere.setVisibility(VISIBLE);
            tvLevelSevere.setText(_context.getResources().getString(R.string.status_pm_serious));
            AAOP_HSkin.with(tvSelf).addViewAttrs(AAOP_HSkin.ATTR_TEXT_COLOR, R.color.c763123).applySkin(false);
        }
    }
}

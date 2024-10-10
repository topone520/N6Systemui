package com.adayo.systemui.windows.views.hvac;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.adayo.systemui.contents.AreaConstant;
import com.android.systemui.R;

public class VtpPMView extends FrameLayout {
    private Context _context;
    private RelativeLayout relativeMild;
    private TextView tvSelf;
    private RelativeLayout relativeSevere;
    private TextView tvLevelSevere;


    public VtpPMView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        _context = context;
        _initialize_view();
    }

    private void _initialize_view() {
        addView(LayoutInflater.from(_context).inflate(R.layout.hvac_pm_layout, null));
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
            tvSelf.setTextColor(_context.getResources().getColor(R.color.c41AC83));
        } else if (number > AreaConstant.PM_50 && number <= AreaConstant.PM_100) {
            relativeMild.setVisibility(VISIBLE);
            relativeSevere.setVisibility(GONE);
            tvSelf.setText(_context.getResources().getString(R.string.status_pm_good));
            tvSelf.setTextColor(_context.getResources().getColor(R.color.ce1c556));
        } else if (number > AreaConstant.PM_100 && number <= AreaConstant.PM_150) {
            relativeMild.setVisibility(GONE);
            relativeSevere.setVisibility(VISIBLE);
            tvLevelSevere.setText(_context.getResources().getString(R.string.vtp_status_pm_mild));
            tvLevelSevere.setTextColor(_context.getResources().getColor(R.color.cCD8741));
        } else if (number > AreaConstant.PM_150 && number <= AreaConstant.PM_200) {
            relativeMild.setVisibility(GONE);
            relativeSevere.setVisibility(VISIBLE);
            tvLevelSevere.setText(_context.getResources().getString(R.string.vtp_status_pm_moderate));
            tvLevelSevere.setTextColor(_context.getResources().getColor(R.color.c_b9_45_3c));
        } else if (number > AreaConstant.PM_200 && number <= AreaConstant.PM_300) {
            relativeMild.setVisibility(GONE);
            relativeSevere.setVisibility(VISIBLE);
            tvLevelSevere.setText(_context.getResources().getString(R.string.vtp_status_pm_severe));
            tvLevelSevere.setTextColor(_context.getResources().getColor(R.color.c7744a));
        } else {
            relativeMild.setVisibility(GONE);
            relativeSevere.setVisibility(VISIBLE);
            tvLevelSevere.setText(_context.getResources().getString(R.string.vtp_status_pm_serious));
            tvLevelSevere.setTextColor(_context.getResources().getColor(R.color.c763123));
        }
    }
}

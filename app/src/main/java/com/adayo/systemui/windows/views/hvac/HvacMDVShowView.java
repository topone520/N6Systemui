package com.adayo.systemui.windows.views.hvac;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SeekBar;

import com.android.systemui.R;

/**
 * MDV 两个seekbar显示的view
 */
public class HvacMDVShowView extends FrameLayout {
    private Context _context;
    private VerticalSeekBar ver_bar;
    private SeekBar hor_bar;

    public HvacMDVShowView(Context context) {
        super(context);
    }

    public HvacMDVShowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this._context = context;
        initView();
    }


    public HvacMDVShowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        _context = context;
        initView();
    }

    private void initView() {

        addView(LayoutInflater.from(_context).inflate(R.layout.hvac_outlet_layuot, null));

        hor_bar = findViewById(R.id.hor_bar);
        ver_bar = findViewById(R.id.ver_bar);
        onTouchBar(hor_bar);
        onTouchBar(ver_bar);
    }

    public void updateHorBar(int hor) {
        hor_bar.setProgress(hor);

    }

    public void updateVerBar(int ver) {
        ver_bar.setProgress(ver);

    }

    private void onTouchBar(View v) {
        v.setOnTouchListener((v1, event) -> {
            // 禁止用户手动滑动
            return true;
        });
    }
}

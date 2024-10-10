package com.adayo.systemui.windows.views.fragrance;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.systemui.R;

public class FragranceSlotBgView extends FrameLayout {
    private static final String TAG = FragranceSlotBgView.class.getSimpleName();

    private Context _context;
    private RelativeLayout fragSlotBg;
    private FrameLayout frameView;

    public FragranceSlotBgView(@NonNull Context context) {
        super(context);
    }

    public FragranceSlotBgView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        _context = context;
        initView();
    }

    public FragranceSlotBgView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        _context = context;
        initView();
    }

    private void initView() {
        addView(LayoutInflater.from(_context).inflate(R.layout.fragrance_slot_bg_view, null));
        fragSlotBg = (RelativeLayout) findViewById(R.id.frag_slot_bg);
        frameView = findViewById(R.id.frame_view);
        frameView.addView(LayoutInflater.from(_context).inflate(R.layout.fragrance_slot_copywriting_4, null));
    }

    public void setUpdateViewUI(int xml) {
        Log.d(TAG, "setUpdateViewUI: " + xml);
        try {
            fragSlotBg.setBackgroundResource(xml);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setUpdateWriteUI(int xml) {
        Log.d(TAG, "setUpdateWriteUI: " + xml);
        try {
            frameView.removeAllViews();
            frameView.addView(LayoutInflater.from(_context).inflate(xml, null));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}

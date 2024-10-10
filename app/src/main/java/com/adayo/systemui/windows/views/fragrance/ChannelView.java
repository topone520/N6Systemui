package com.adayo.systemui.windows.views.fragrance;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.adayo.systemui.contents.FragranceSOAConstant;
import com.android.systemui.R;

public class ChannelView extends FrameLayout {
    private static final String TAG = ChannelView.class.getSimpleName();
    private FragranceSeekBar fragranceAView;
    private FragranceSeekBar fragranceBView;
    private FragranceSeekBar fragranceCView;
    private FragranceSeekBar fragranceCommView;
    private final int NO_ACTION = 0;
    private ChannelSelectListener channelSelectListener;
    private boolean isOpen;
    private int channelSelect;
    private ChannelConcentrationListener channelConcentrationListener;
    private final int[] ints1 = new int[]{R.mipmap.ivi_ac_fragrance_bg_bule1, R.mipmap.ivi_ac_btn_fragrance_name1};
    private final int[] ints2 = new int[]{R.mipmap.ivi_ac_fragrance_bg_green, R.mipmap.ivi_ac_btn_fragrance_name2};
    private final int[] ints3 = new int[]{R.mipmap.ivi_ac_fragrance_bg_yellow, R.mipmap.ivi_ac_btn_fragrance_name3};

    public void setChannelConcentrationListener(ChannelConcentrationListener channelConcentrationListener) {
        this.channelConcentrationListener = channelConcentrationListener;
    }

    public void setChannelSelectListener(ChannelSelectListener channelSelectListener) {
        this.channelSelectListener = channelSelectListener;
    }

    public ChannelView(@NonNull Context context) {
        super(context);
    }

    public ChannelView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ChannelView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        addView(LayoutInflater.from(getContext()).inflate(R.layout.fragrance_channel_view, null));
        fragranceAView = (FragranceSeekBar) findViewById(R.id.fragrance_a_view);
        fragranceBView = (FragranceSeekBar) findViewById(R.id.fragrance_b_view);
        fragranceCView = (FragranceSeekBar) findViewById(R.id.fragrance_c_view);
        fragranceAView.hideThumb();
        fragranceBView.hideThumb();
        fragranceCView.hideThumb();
        fragranceCommView = fragranceAView;
        settingBg();
        fragranceAView.setListener(new FragranceSeekBar.FragranceProgressListener() {
            @Override
            public void level(int level) {
                if (channelConcentrationListener != null)
                    channelConcentrationListener.onConcentration(level);
            }

            @Override
            public void onClick() {
                selectChannel(FragranceSOAConstant.FRAGRANCE_A_POSITION);
            }

            @Override
            public void onLongClick() {

            }
        });
        fragranceBView.setListener(new FragranceSeekBar.FragranceProgressListener() {
            @Override
            public void level(int level) {
                if (channelConcentrationListener != null)
                    channelConcentrationListener.onConcentration(level);
            }

            @Override
            public void onClick() {
                selectChannel(FragranceSOAConstant.FRAGRANCE_B_POSITION);
            }

            @Override
            public void onLongClick() {

            }
        });
        fragranceCView.setListener(new FragranceSeekBar.FragranceProgressListener() {
            @Override
            public void level(int level) {
                if (channelConcentrationListener != null)
                    channelConcentrationListener.onConcentration(level);
            }

            @Override
            public void onClick() {
                selectChannel(FragranceSOAConstant.FRAGRANCE_C_POSITION);
            }

            @Override
            public void onLongClick() {

            }
        });
    }

    private void settingBg() {
        fragranceAView.setSeekbarTitle(getContext().getResources().getString(R.string.fragrance_title_a));
        fragranceAView.setBackgroundBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ivi_ac_fragrance_button_d1));
        fragranceAView.setProgressBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ivi_ac_fragrance_button_s1));
        fragranceBView.setSeekbarTitle(getContext().getResources().getString(R.string.fragrance_title_b));
        fragranceBView.setBackgroundBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ivi_ac_fragrance_button_d2));
        fragranceBView.setProgressBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ivi_ac_fragrance_button_s2));
        fragranceCView.setSeekbarTitle(getContext().getResources().getString(R.string.fragrance_title_c));
        fragranceCView.setBackgroundBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ivi_ac_fragrance_button_d3));
        fragranceCView.setProgressBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ivi_ac_fragrance_button_s3));
    }

    private void selectChannel(int channel) {
        if (channelSelect == channel) {
            isOpen = !isOpen;
            channelSelectListener.onSwitch(isOpen ? FragranceSOAConstant.OPEN : FragranceSOAConstant.CLOSE);
        }
        if (channel == FragranceSOAConstant.FRAGRANCE_A_POSITION) {
            channelSelect = FragranceSOAConstant.FRAGRANCE_A_POSITION;
            fragranceCommView = fragranceAView;
            if (isOpen) {
                fragranceAView.showThumb();
            } else {
                fragranceAView.hideThumb();
            }
            fragranceBView.hideThumb();
            fragranceCView.hideThumb();
            channelSelectListener.onChannelBg(ints1);
        } else if (channel == FragranceSOAConstant.FRAGRANCE_B_POSITION) {
            channelSelect = FragranceSOAConstant.FRAGRANCE_B_POSITION;
            fragranceCommView = fragranceBView;
            if (isOpen) {
                fragranceBView.showThumb();
            } else {
                fragranceBView.hideThumb();
            }
            fragranceAView.hideThumb();
            fragranceCView.hideThumb();
            channelSelectListener.onChannelBg(ints2);
        } else {
            channelSelect = FragranceSOAConstant.FRAGRANCE_C_POSITION;
            fragranceCommView = fragranceCView;
            if (isOpen) {
                fragranceCView.showThumb();
            } else {
                fragranceCView.hideThumb();
            }
            fragranceAView.hideThumb();
            fragranceBView.hideThumb();
            channelSelectListener.onChannelBg(ints3);
        }
        if (channelSelectListener != null) channelSelectListener.onSelectChannel(channel);
    }

    public void updateChannel(int status) {
        if (status == NO_ACTION) {
            fragranceCommView.hideThumb();
        } else {
            selectChannel(status);
        }
    }

    public void updateConcentration(int level) {
        if (level == -1) return;
        fragranceAView.setProgress(getConcentrationProgress(level));
        fragranceBView.setProgress(getConcentrationProgress(level));
        fragranceCView.setProgress(getConcentrationProgress(level));
//        fragranceAView.setConcentration(getConcentration(level));
//        fragranceBView.setConcentration(getConcentration(level));
//        fragranceCView.setConcentration(getConcentration(level));
    }

    private String getConcentration(int level) {
        if (level == FragranceSOAConstant.CONCENTRATION_LOW) {
            return getResources().getString(R.string.fragrance_concentration_low);
        } else if (level == FragranceSOAConstant.CONCENTRATION_MID) {
            return getResources().getString(R.string.fragrance_concentration_mid);
        }
        return getResources().getString(R.string.fragrance_concentration_high);
    }

    private int getConcentrationProgress(int level) {
        if (level == FragranceSOAConstant.CONCENTRATION_LOW) {
            return FragranceSOAConstant.SLIDE_LOW;
        } else if (level == FragranceSOAConstant.CONCENTRATION_MID) {
            return FragranceSOAConstant.SLIDE_MID;
        }
        return FragranceSOAConstant.SLIDE_HIGH;
    }

    public void updateClose() {
        fragranceAView.hideThumb();
        fragranceBView.hideThumb();
        fragranceCView.hideThumb();
    }

    public void updateSwitch(int value) {
        isOpen = value == FragranceSOAConstant.OPEN;
    }

    public interface ChannelSelectListener {
        void onSelectChannel(int slot);

        void onChannelBg(int[] bg);

        void onSwitch(int aSwitch);
    }

    public interface ChannelConcentrationListener {
        void onConcentration(int status);
    }
}

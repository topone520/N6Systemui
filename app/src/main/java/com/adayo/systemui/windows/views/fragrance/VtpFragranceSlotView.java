package com.adayo.systemui.windows.views.fragrance;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.adayo.systemui.contents.FragranceSOAConstant;
import com.adayo.systemui.eventbus.EventContents;
import com.adayo.systemui.eventbus.EventData;
import com.adayo.systemui.functional.fragrance._monitor.ChannelMonitor;
import com.adayo.systemui.functional.fragrance._monitor.TitleMonitor;
import com.adayo.systemui.room.bean.FragranceInfo;
import com.adayo.systemui.room.bean.VtpFragranceInfo;
import com.adayo.systemui.room.database.FragranceDatabase;
import com.adayo.systemui.utils.KeyboardUtils;
import com.android.systemui.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class VtpFragranceSlotView extends FrameLayout {

    public static final String TAG = VtpFragranceSlotView.class.getSimpleName();
    private int position;
    private Context _context;
    private VtpFragranceSeekBar seekBar;

    private VtpFragranceSlotView.ViewSlideListener slideListener;
    private VtpFragranceSlotView.ViewClickListener clickListener;

    public void setSlideListener(ViewSlideListener slideListener) {
        this.slideListener = slideListener;
    }

    public void setClickListener(ViewClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public VtpFragranceSlotView(@NonNull Context context) {
        super(context);
    }

    public VtpFragranceSlotView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        _context = context;
        initView();
        initEvent();
    }

    public VtpFragranceSlotView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        _context = context;
        initView();
        initEvent();
    }

    private void initView() {
        EventBus.getDefault().register(this);
        addView(LayoutInflater.from(_context).inflate(R.layout.vtp_fragrance_slot_view, null));
        seekBar = (VtpFragranceSeekBar) findViewById(R.id.seek_bar);
        seekBar.setBackgroundBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.drawable_fragrance_default));
        seekBar.setProgressBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.drawable_fragrance_default));
    }

    private void initEvent() {
        seekBar.setListener(new VtpFragranceSeekBar.FragranceProgressListener() {
            @Override
            public void level(int level) {
                slideListener.onSlide(level);
            }

            @Override
            public void onClick() {
                clickListener.onClick();
            }

            @Override
            public void onLongClick() {
                EventBus.getDefault().post(new EventData(EventContents.EVENT_TYPE_1, position));
            }
        });
    }

    private void updateEditView(String value) {
        //编辑结束并保存
        if (!TextUtils.isEmpty(value.trim())) {
            seekBar.setSeekbarTitle(value.trim());
            VtpFragranceInfo info = FragranceDatabase.getInstance().vtpFragranceDao().getFragranceInfoByPosition(position);
            info.setTitle(value.trim());
            info.setBackground(R.mipmap.ivi_ac_fragrance_bg_bule1);
            info.setWrite(R.layout.fragrance_slot_copywriting_4);
            FragranceDatabase.getInstance().vtpFragranceDao().updateFragranceInfo(info);
            TitleMonitor.getInstance().changeTitle(position);
        }

    }

    //更新香氛浓度
    public void updateConcentrationUI(int grep) {
        if (grep == FragranceSOAConstant.CONCENTRATION_LOW) {
            seekBar.setProgress(FragranceSOAConstant.SLIDE_LOW);
        } else if (grep == FragranceSOAConstant.CONCENTRATION_MID) {
            seekBar.setProgress(FragranceSOAConstant.SLIDE_MID);
        } else {
            seekBar.setProgress(FragranceSOAConstant.SLIDE_HIGH);
        }
    }

    //更新不同香型上报之后的UI
    public void updateTypeUI(VtpFragranceInfo info) {
        Log.d(TAG, "updateTypeUI: type" + info.getType());
        seekBar.setVisibility(info.getType() == FragranceSOAConstant.TYPE_NULL ? GONE : VISIBLE);
        seekBar.setBackgroundBitmap(BitmapFactory.decodeResource(getResources(), info.getSlide()));
        seekBar.setProgressBitmap(BitmapFactory.decodeResource(getResources(), info.getCover()));
        seekBar.setSeekbarTitle(info.getTitle());
    }

    ////香氛开启后更新UI
    public void updateSwitchUI(boolean isOpen) {
        if (isOpen)
            seekBar.showThumb();
        else
            seekBar.hideThumb();
    }

    public interface ViewSlideListener {
        void onSlide(int level);
    }

    public interface ViewClickListener {
        void onClick();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventData data) {
        //由 VtpFragranceViewFragment 点击保存传递过来的数据
        if (data.getType() == EventContents.EVENT_TYPE_2 && position == data.getValue()) {
            if (!((String) data.getData()).equals(seekBar.getSeekbarTile()))
                updateEditView((String) data.getData());
        }
    }

}

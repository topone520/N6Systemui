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

import com.adayo.systemui.room.bean.FragranceInfo;
import com.adayo.systemui.contents.FragranceSOAConstant;
import com.adayo.systemui.functional.fragrance._monitor.ChannelMonitor;
import com.adayo.systemui.functional.fragrance._monitor.TitleMonitor;
import com.adayo.systemui.room.database.FragranceDatabase;
import com.adayo.systemui.utils.KeyboardUtils;
import com.android.systemui.R;

public class FragranceSlotView extends FrameLayout {

    public static final String TAG = FragranceSlotView.class.getSimpleName();
    private int position;
    private Context _context;
    private FragranceSeekBar seekBar;
    private EditText editTitle;
    private Button btnSave;
    private ChannelMonitor monitor;

    private FragranceSlotView.ViewSlideListener slideListener;
    private FragranceSlotView.ViewClickListener clickListener;

    public void setSlideListener(ViewSlideListener slideListener) {
        this.slideListener = slideListener;
    }

    public void setClickListener(ViewClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public FragranceSlotView(@NonNull Context context) {
        super(context);
    }

    public FragranceSlotView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        _context = context;
        initView();
        initEvent();
    }

    public FragranceSlotView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        _context = context;
        initView();
        initEvent();
    }

    private void initView() {
        addView(LayoutInflater.from(_context).inflate(R.layout.fragrance_slot_view, null));
        seekBar = (FragranceSeekBar) findViewById(R.id.seek_bar);
        editTitle = (EditText) findViewById(R.id.edit_title);
        btnSave = (Button) findViewById(R.id.btn_name_save);
        seekBar.setBackgroundBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.drawable_fragrance_default));
        seekBar.setProgressBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.drawable_fragrance_default));
    }

    private void initEvent() {
        monitor = new ChannelMonitor();
        seekBar.setListener(new FragranceSeekBar.FragranceProgressListener() {
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
                updateEditView(true);
            }
        });

        btnSave.setOnClickListener(view -> {
            updateEditView(false);
            seekBar.cancelLongShowText();
        });
    }

    private void updateEditView(boolean isEditing) {
        if (isEditing) {
            //自定义香氛名称
            if (editTitle.getVisibility() == GONE) editTitle.setVisibility(VISIBLE);
            seekBar.hideThumb();
            btnSave.setVisibility(VISIBLE);
            editTitle.requestFocus(); //获取焦点
            editTitle.setText(seekBar.getSeekbarTitle());
        } else {
            //编辑结束并保存
            if (editTitle.getVisibility() == VISIBLE) editTitle.setVisibility(GONE);
            KeyboardUtils.hideKeyboard(_context, editTitle);
            btnSave.setVisibility(GONE);
            editTitle.clearFocus(); //释放焦点
            if (!TextUtils.isEmpty(editTitle.getText().toString().trim())) {
                seekBar.setSeekbarTitle(editTitle.getText().toString().trim());
                FragranceInfo info = FragranceDatabase.getInstance().fragranceDao().getFragranceInfoByPosition(position);
                info.setTitle(editTitle.getText().toString().trim());
                info.setBackground(R.mipmap.ivi_ac_fragrance_bg_bule1);
                info.setWrite(R.layout.fragrance_slot_copywriting_4);
                FragranceDatabase.getInstance().fragranceDao().updateFragranceInfo(info);
                TitleMonitor.getInstance().changeTitle(position);
            }
            seekBar.showThumb();
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
    public void updateTypeUI(FragranceInfo info) {
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

}

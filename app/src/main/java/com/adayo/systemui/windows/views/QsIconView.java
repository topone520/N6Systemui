package com.adayo.systemui.windows.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.soavb.foundation.message.MessageAction;
import com.adayo.systemui.contents.HvacSOAConstant;
import com.adayo.systemui.contents.SeatSOAConstant;
import com.adayo.systemui.utils.LogUtil;
import com.adayo.systemui.windows.dialogs.CommErrorDialog;
import com.android.systemui.R;

public class QsIconView extends RelativeLayout {

    private static final int VENTILATE_LEVEL_MIN = 0;
    private static final int VENTILATE_LEVEL_STS_MAX = 4;
    private AttributeSet attrs;
    private ImageView iconImage;
    private TextView iconText;
    private ImageView iconSubscript, iconEnable;
    private LinearLayout iconLayout;
    private int[] level_resource;
    private int _ventilate_level = VENTILATE_LEVEL_MIN;
    private int power_status = HvacSOAConstant.HVAC_POWER_MODE_IGN_ON;

    public QsIconView(Context context) {
        super(context);
        initView();
    }

    public QsIconView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.attrs = attrs;
        initView();
    }

    public QsIconView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.attrs = attrs;
        initView();
    }

    private void initView() {
        // 获取自定义属性值
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.QsIconView);
        int img_icon = a.getResourceId(R.styleable.QsIconView_img_icon, 0);
        String tv_title = a.getString(R.styleable.QsIconView_tv_title);
        a.recycle();

        View mRootView = AAOP_HSkin.getLayoutInflater(getContext()).inflate(R.layout.qs_icon_view, this, true);
        iconImage = mRootView.findViewById(R.id.icon_image);
        iconText = mRootView.findViewById(R.id.icon_text);
        iconSubscript = mRootView.findViewById(R.id.icon_subscript);
        iconLayout = mRootView.findViewById(R.id.icon_layou);
        iconEnable = mRootView.findViewById(R.id.icon_enable);

        if (img_icon != 0) {
            setIconImage(img_icon);
        }
        if (tv_title != null) {
            setIconText(tv_title);
        }

        iconLayout.setOnClickListener(v -> {
            LogUtil.d("iconLayout onclick" + level_resource);
            if (level_resource != null) {
                if (power_status != HvacSOAConstant.HVAC_POWER_MODE_IGN_ON) {
                    CommErrorDialog.getInstance().show();
                    return;
                }
                _ventilate_level--;
                if (_ventilate_level < VENTILATE_LEVEL_MIN) {
                    _ventilate_level = level_resource.length - 1;
                }
                initUpdateView();
            }
            if (listener != null) {
                listener.onVentilateLevelChanged(_ventilate_level);
            }

        });

        iconLayout.setOnLongClickListener(v -> {
            if (onLongListener != null) {
                onLongListener.onLongListener(v);
            }
            return true;
        });

        iconSubscript.setOnClickListener(v -> {
            LogUtil.d("iconSubscript onclick");
            onChildClickListener.onChildClicked(v);
        });

    }


    public void showIconSubscript() {
        iconSubscript.setVisibility(View.VISIBLE);
    }

    public void setIconImage(int resId) {
        AAOP_HSkin.with(iconImage).addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, resId).applySkin(false);
        iconImage.setTag(resId);
    }

    public void setIconText(String text) {
        iconText.setText(text);
    }

    public void setTextColor(boolean select) {
        iconText.setSelected(select);
    }

    public String getIconText() {
        return iconText.getText().toString();
    }

    public int getIcon() {
        return (int) iconImage.getTag();
    }

    public int get_level() {
        return _ventilate_level;
    }
    public void onPerformClick(){
        iconLayout.performClick();
    }

    public void setPowerStatus(int status) {
        power_status = status;
    }

    public void injectResource(int[] level_resources) {
        this.level_resource = level_resources;
        initUpdateView();
    }

    private void initUpdateView() {
        AAOP_HSkin.with(iconImage).addViewAttrs(AAOP_HSkin.ATTR_SRC, _ventilate_level <= 0 ? level_resource[0] : level_resource[_ventilate_level]).applySkin(false);
        AAOP_HSkin.with(iconText).addViewAttrs(AAOP_HSkin.ATTR_TEXT_COLOR, _ventilate_level <= 0 ? R.color.seat_text_color2 : R.color.seat_text_color1).applySkin(false);
    }

    public void _update_ui(int position) {
        LogUtil.d("setStsUpdateView: " + position);
        if (position < VENTILATE_LEVEL_MIN || position > VENTILATE_LEVEL_STS_MAX) return;
        if (position == VENTILATE_LEVEL_STS_MAX) position = VENTILATE_LEVEL_MIN;
        _ventilate_level = position;
        initUpdateView();
    }


    public void _update_ui2(Integer value, boolean is_open) {
        AAOP_LogUtils.d("ccm------>", "_update_ui2");
        is_open = value == SeatSOAConstant.SEAT_MASSAGE_OPEN;
        AAOP_HSkin.with(iconImage).addViewAttrs(AAOP_HSkin.ATTR_SRC, is_open ? R.mipmap.ivi_ac_seats_icon_wheel_hot_s3 : R.mipmap.ivi_ac_seats_icon_wheel_hot_n).applySkin(false);
        AAOP_HSkin.with(iconText).addViewAttrs(AAOP_HSkin.ATTR_TEXT_COLOR, is_open ? R.color.seat_text_color1 : R.color.seat_text_color2).applySkin(false);
    }

    private MessageAction<Integer> messageAction;

    public void injectAction(MessageAction<Integer> action) {
        messageAction = action;
        action.setListener(((bundle, value) -> {
            _update_uis(bundle, value);
        }));
    }

    private void _update_uis(Bundle bundle, Integer value) {
        if (value != null) {
            setSelected(value == 1);
            setTextColor(value == 1);
        }
    }

    public MessageAction<Integer> getMessageAction() {
        return messageAction;
    }

    private VentilateLevelListener listener;
    public void setListener(VentilateLevelListener listener) {
        this.listener = listener;
    }

    public void setIconEnableVisibility(int visible) {
        iconEnable.setVisibility(visible);
    }

    public interface VentilateLevelListener {
        void onVentilateLevelChanged(int grep);
    }


    private onChildClickListener onChildClickListener;
    public void setOnChildClickListener(onChildClickListener onChildClickListener) {
        this.onChildClickListener = onChildClickListener;
    }
    public interface onChildClickListener {
        void onChildClicked(View view);
    }

    private onLongListener onLongListener;
    public void setLongListener(onLongListener onLongListener) {
        this.onLongListener = onLongListener;
    }

    public interface onLongListener {
        void onLongListener(View view);
    }


    private onDragListener onDragListener;
    public void setonDragListener(onDragListener onDragListener) {
        this.onDragListener = onDragListener;
    }
    public interface onDragListener {
        void onDragListener(View view);
    }

}

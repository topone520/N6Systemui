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

public class QsSeatAdjustment extends RelativeLayout {
    private ImageView iconImage;
    private TextView iconText;
    private ImageView iconSubscript;
    private LinearLayout iconLayout;
    public QsSeatAdjustment(Context context) {
        super(context);
    }
    public QsSeatAdjustment(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public QsSeatAdjustment(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        View mRootView = LayoutInflater.from(getContext()).inflate(R.layout.qs_isco_adjustment, this, true);
        iconImage = mRootView.findViewById(R.id.icon_image);
        iconText = mRootView.findViewById(R.id.icon_text);
        iconSubscript = mRootView.findViewById(R.id.icon_adjustment);
        iconLayout = mRootView.findViewById(R.id.icon_layout);
        iconSubscript.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtil.d("iconSubscript onclick");
                _listener.onSeatClickListener(v);
            }
        });
        iconLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtil.d("QsSeatAdjustment::onClick()");

            }
        });
    }
    public interface SeatClickListener{
        void  onSeatClickListener(View view);
    }
   private SeatClickListener _listener =SeatonClickListener.getInstance();
    public void setAdjustmentSingleListener(SeatClickListener listener) {
        _listener = (listener == null) ?SeatonClickListener.getInstance() : listener;
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

    public void setTextColor(boolean select) {
        iconText.setSelected(select);
    }
}
class SeatonClickListener implements QsSeatAdjustment.SeatClickListener{

    private static volatile QsSeatAdjustment.SeatClickListener _instance;
    public static QsSeatAdjustment.SeatClickListener getInstance() {
        if (_instance == null) {
            synchronized (SeatonClickListener.class) {
                if (_instance == null) {
                    _instance = new SeatonClickListener();
                }
            }
        }
        return _instance;
    }


    @Override
    public void onSeatClickListener(View view) {

    }
}
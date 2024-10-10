package com.adayo.systemui.windows.panels;

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

public class QsMassageViewPanel  extends RelativeLayout {
    private ImageView iconImage;
    private TextView iconText;
    private ImageView iconSubscript;
    private LinearLayout iconLayout;
    public QsMassageViewPanel(Context context) {
        super(context);
    }
    public QsMassageViewPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public QsMassageViewPanel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        View mRootView = LayoutInflater.from(getContext()).inflate(R.layout.qs_icon_message_view, this, true);
        iconImage = mRootView.findViewById(R.id.icon_image);
        iconText = mRootView.findViewById(R.id.icon_text);
        iconSubscript = mRootView.findViewById(R.id.icon_triangle);
        iconLayout = mRootView.findViewById(R.id.icon_layout);
        iconSubscript.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtil.d("iconSubscript onclick");
                _listener.onSinglicked(v);
            }
        });
        iconLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtil.d("QsIconView::onClick()");

            }
        });
    }
    public interface SingletonListener{
        void  onSinglicked(View view);
    }
    private SingletonListener _listener=SingletonClickListener.getInstance();
    public void setSingleListener(SingletonListener listener) {
        _listener = (listener == null) ? SingletonClickListener.getInstance() : listener;
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
}
class SingletonClickListener implements QsMassageViewPanel.SingletonListener{

    @Override
    public void onSinglicked(View view) {

    }
    private static volatile QsMassageViewPanel.SingletonListener _instance;
    public static QsMassageViewPanel.SingletonListener getInstance() {
        if (_instance == null) {
            synchronized (SingletonClickListener.class) {
                if (_instance == null) {
                    _instance = new SingletonClickListener();
                }
            }
        }
        return _instance;
    }

}


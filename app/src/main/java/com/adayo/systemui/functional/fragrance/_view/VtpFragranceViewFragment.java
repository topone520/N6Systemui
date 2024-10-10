package com.adayo.systemui.functional.fragrance._view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.soavb.foundation.view.AbstractBindViewFramelayout;
import com.adayo.systemui.contents.FragranceSOAConstant;
import com.adayo.systemui.eventbus.EventContents;
import com.adayo.systemui.eventbus.EventData;
import com.adayo.systemui.functional.fragrance._binder.FragranceConcentrationViewBinder;
import com.adayo.systemui.functional.fragrance._binder.FragranceSlotViewBinder;
import com.adayo.systemui.functional.fragrance._binder.FragranceSystemErrorViewBinder;
import com.adayo.systemui.functional.fragrance._binder.FragranceTypeBinder;
import com.adayo.systemui.functional.fragrance._binder.VtpFragranceConcentrationViewBinder;
import com.adayo.systemui.functional.fragrance._binder.VtpFragranceSlotViewBinder;
import com.adayo.systemui.functional.fragrance._binder.VtpFragranceSystemErrorViewBinder;
import com.adayo.systemui.functional.fragrance._binder.VtpFragranceTypeBinder;
import com.adayo.systemui.functional.fragrance._monitor.ChannelMonitor;
import com.adayo.systemui.functional.fragrance._monitor.TitleMonitor;
import com.adayo.systemui.interfaces.HvacLayoutSwitchListener;
import com.adayo.systemui.room.bean.VtpFragranceInfo;
import com.adayo.systemui.room.database.FragranceDatabase;
import com.adayo.systemui.utils.LogUtil;
import com.adayo.systemui.utils.VtpFragranceMapUtils;
import com.android.systemui.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class VtpFragranceViewFragment extends AbstractBindViewFramelayout {

    private static final String TAG = VtpFragranceViewFragment.class.getSimpleName();
    private HvacLayoutSwitchListener listener;
    private static final ChannelMonitor _monitor = new ChannelMonitor();

    public VtpFragranceViewFragment(@NonNull Context context) {
        super(context);
        initView();
    }

    public VtpFragranceViewFragment(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public void setListener(HvacLayoutSwitchListener listener) {
        this.listener = listener;
    }

    private LinearLayout _lin_edit_view;
    private EditText _edit_title;
    private TextView _tv_save_title;
    private int _position;//记录哪个香氛通道传递过来的下标

    private void initView() {
        EventBus.getDefault().register(this);
        //按钮-关闭香氛
        findViewById(R.id.ivi_seat_close).setOnClickListener(view -> listener.onHvacLayout());
        _lin_edit_view = findViewById(R.id.lin_edit_view);
        _edit_title = findViewById(R.id.edit_title);
        _tv_save_title = findViewById(R.id.tv_save_title);

        _tv_save_title.setOnClickListener(v -> {
            EventBus.getDefault().post(new EventData(EventContents.EVENT_TYPE_2, _edit_title.getText().toString(), _position));
            _lin_edit_view.setVisibility(GONE);
        });
    }

    @Override
    protected void createViewBinder() {
        LogUtil.d(TAG + "::createViewBinder()");
        AAOP_LogUtils.d(TAG, "::createViewBinder() dialog - hvacPanel ---->");
        //香氛开关&&香氛通道
        VtpFragranceSlotViewBinder binder = new VtpFragranceSlotViewBinder(_monitor, FragranceSOAConstant.FRAGRANCE_A_POSITION, R.id.fragrance_a_view, R.id.fragrance_slot_bg);
        _monitor.insertObserver(binder);
        TitleMonitor.getInstance().insertObserver(binder);
        super.insertViewBinder(binder);

        binder = new VtpFragranceSlotViewBinder(_monitor, FragranceSOAConstant.FRAGRANCE_B_POSITION, R.id.fragrance_b_view, R.id.fragrance_slot_bg);
        _monitor.insertObserver(binder);
        TitleMonitor.getInstance().insertObserver(binder);
        super.insertViewBinder(binder);

        binder = new VtpFragranceSlotViewBinder(_monitor, FragranceSOAConstant.FRAGRANCE_C_POSITION, R.id.fragrance_c_view, R.id.fragrance_slot_bg);
        _monitor.insertObserver(binder);
        TitleMonitor.getInstance().insertObserver(binder);
        super.insertViewBinder(binder);

        //香氛香型
        super.insertViewBinder(new VtpFragranceTypeBinder(listener, FragranceSOAConstant.FRAGRANCE_A_POSITION, R.id.fragrance_a_view, R.id.frag_lin_error_view));
        super.insertViewBinder(new VtpFragranceTypeBinder(listener, FragranceSOAConstant.FRAGRANCE_B_POSITION, R.id.fragrance_b_view, R.id.frag_lin_error_view));
        super.insertViewBinder(new VtpFragranceTypeBinder(listener, FragranceSOAConstant.FRAGRANCE_C_POSITION, R.id.fragrance_c_view, R.id.frag_lin_error_view));
        //香氛浓度
        super.insertViewBinder(new VtpFragranceConcentrationViewBinder(R.id.fragrance_a_view));
        super.insertViewBinder(new VtpFragranceConcentrationViewBinder(R.id.fragrance_b_view));
        super.insertViewBinder(new VtpFragranceConcentrationViewBinder(R.id.fragrance_c_view));

        //香氛故障
        super.insertViewBinder(new VtpFragranceSystemErrorViewBinder(R.id.frag_system_error_view));
    }

    @Override
    protected int acquireResourceId() {
        return R.layout.vtp_fragrance_view_layout;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventData data) {
        //由 VtpFragranceSlotView 长按传递过来的数据
        if (data.getType() == EventContents.EVENT_TYPE_1) {
            _lin_edit_view.setVisibility(VISIBLE);
            _position = (int) data.getData();
            VtpFragranceInfo fragranceInfoByPosition = FragranceDatabase.getInstance().vtpFragranceDao().getFragranceInfoByPosition((int) data.getData());
            _edit_title.setBackgroundResource(fragranceInfoByPosition.getCover());
            _edit_title.setText(fragranceInfoByPosition.getTitle());
        }
    }
}

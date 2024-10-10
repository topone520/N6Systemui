package com.adayo.systemui.functional.fragrance._view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.adayo.soavb.foundation.view.AbstractBindViewFramelayout;
import com.adayo.systemui.contents.FragranceSOAConstant;
import com.adayo.systemui.functional.fragrance._binder.FragranceConcentrationViewBinder;
import com.adayo.systemui.functional.fragrance._binder.FragranceSlotViewBinder;
import com.adayo.systemui.functional.fragrance._binder.FragranceSystemErrorViewBinder;
import com.adayo.systemui.functional.fragrance._binder.FragranceTypeBinder;
import com.adayo.systemui.functional.fragrance._monitor.ChannelMonitor;
import com.adayo.systemui.functional.fragrance._monitor.TitleMonitor;
import com.adayo.systemui.interfaces.HvacLayoutSwitchListener;
import com.adayo.systemui.utils.LogUtil;
import com.android.systemui.R;

public class FragranceViewFragment extends AbstractBindViewFramelayout {

    private static final String TAG = FragranceViewFragment.class.getSimpleName();
    private HvacLayoutSwitchListener listener;
    private static final ChannelMonitor _monitor = new ChannelMonitor();

    public FragranceViewFragment(@NonNull Context context) {
        super(context);
        initView();
    }

    public FragranceViewFragment(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public void setListener(HvacLayoutSwitchListener listener) {
        this.listener = listener;
    }

    private void initView() {
        //按钮-关闭香氛
        ImageView _ivi_seat_close = findViewById(R.id.ivi_seat_close);
        _ivi_seat_close.setOnClickListener(view -> listener.onHvacLayout());
    }

    @Override
    protected void createViewBinder() {
        LogUtil.d(TAG + "::createViewBinder()");
        //香氛开关&&香氛通道
        FragranceSlotViewBinder binder = new FragranceSlotViewBinder(_monitor, FragranceSOAConstant.FRAGRANCE_A_POSITION, R.id.fragrance_a_view, R.id.fragrance_slot_bg);
        _monitor.insertObserver(binder);
        TitleMonitor.getInstance().insertObserver(binder);
        super.insertViewBinder(binder);

        binder = new FragranceSlotViewBinder(_monitor, FragranceSOAConstant.FRAGRANCE_B_POSITION, R.id.fragrance_b_view, R.id.fragrance_slot_bg);
        _monitor.insertObserver(binder);
        TitleMonitor.getInstance().insertObserver(binder);
        super.insertViewBinder(binder);

        binder = new FragranceSlotViewBinder(_monitor, FragranceSOAConstant.FRAGRANCE_C_POSITION, R.id.fragrance_c_view, R.id.fragrance_slot_bg);
        _monitor.insertObserver(binder);
        TitleMonitor.getInstance().insertObserver(binder);
        super.insertViewBinder(binder);

        //香氛香型
        super.insertViewBinder(new FragranceTypeBinder(listener, FragranceSOAConstant.FRAGRANCE_A_POSITION, R.id.fragrance_a_view, R.id.frag_lin_error_view));
        super.insertViewBinder(new FragranceTypeBinder(listener, FragranceSOAConstant.FRAGRANCE_B_POSITION, R.id.fragrance_b_view, R.id.frag_lin_error_view));
        super.insertViewBinder(new FragranceTypeBinder(listener, FragranceSOAConstant.FRAGRANCE_C_POSITION, R.id.fragrance_c_view, R.id.frag_lin_error_view));

        //香氛浓度
        super.insertViewBinder(new FragranceConcentrationViewBinder(R.id.fragrance_a_view));
        super.insertViewBinder(new FragranceConcentrationViewBinder(R.id.fragrance_b_view));
        super.insertViewBinder(new FragranceConcentrationViewBinder(R.id.fragrance_c_view));

        //香氛故障
        super.insertViewBinder(new FragranceSystemErrorViewBinder(R.id.frag_system_error_view));
    }

    @Override
    protected int acquireResourceId() {
        return R.layout.hvac_fragrance_view_layout;
    }
}

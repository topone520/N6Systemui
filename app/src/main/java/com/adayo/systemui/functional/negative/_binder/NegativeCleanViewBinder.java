package com.adayo.systemui.functional.negative._binder;

import android.os.Bundle;
import android.view.View;

import com.adayo.soavb.foundation.binder.AbstractViewBinder;
import com.adayo.soavb.foundation.provider.ViewBinderProviderInteger;
import com.adayo.soavb.foundation.view.AbstractBindViewDialog;
import com.adayo.systemui.windows.panels.QsViewPanel;
import com.adayo.systemui.windows.views.QsIconView;
import com.android.systemui.R;

public class NegativeCleanViewBinder extends AbstractViewBinder<Integer> {
    private QsIconView _qs_clean;
    private final AbstractBindViewDialog _dialog;

    public NegativeCleanViewBinder(AbstractBindViewDialog dialog) {
        super(new ViewBinderProviderInteger.Builder().build());
        _dialog = dialog;
    }

    @Override
    protected void _bind_view(View view) {
        _qs_clean = view.findViewById(R.id.qs_clean);
        _qs_clean.setListener(v -> {
//            _qs_clean.setSelected(!_qs_clean.isSelected());
//            _qs_clean.setTextColor(_qs_clean.isSelected());
            _dialog.show();
            QsViewPanel.getInstance().closeView();
        });
    }

    @Override
    protected void _update_ui(Integer value) {

    }

    @Override
    protected void _update_ui(Bundle bundle) {

    }
}
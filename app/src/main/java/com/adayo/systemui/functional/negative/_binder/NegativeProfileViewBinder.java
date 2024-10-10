package com.adayo.systemui.functional.negative._binder;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.adayo.proxy.infrastructure.sourcemng.Beans.AppConfigType;
import com.adayo.soavb.foundation.binder.AbstractViewBinder;
import com.adayo.soavb.foundation.provider.ViewBinderProviderInteger;
import com.adayo.systemui.functional.negative._messageAction.MessageActions;
import com.adayo.systemui.manager.SourceControllerImpl;
import com.adayo.systemui.utils.LogUtil;
import com.adayo.systemui.windows.dialogs.SceneDialog;
import com.adayo.systemui.windows.panels.QsViewPanel;
import com.android.systemui.R;
import com.android.systemui.SystemUIApplication;

import java.util.HashMap;

public class NegativeProfileViewBinder extends AbstractViewBinder<Integer> {

    TextView _profile_card;
//    ImageView _profile_card_subscript;

    public NegativeProfileViewBinder() {
        super(new ViewBinderProviderInteger.Builder().build());
    }


    @Override
    protected void _bind_view(View view) {
        _profile_card = view.findViewById(R.id.profile_card);
//        _profile_card_subscript = view.findViewById(R.id.profile_card_subscript);

        _profile_card.setOnClickListener(v -> {
            if (!MessageActions.getInstance().isEditType()){
                LogUtil.d("profile_card click");
                SourceControllerImpl.getInstance().requestSoureApp("ADAYO_SOURCE_SCENE_MODE",
                        "ADAYO_SOURCE_SCENE_MODE", AppConfigType.SourceSwitch.APP_ON.getValue(), new HashMap<>());
                QsViewPanel.getInstance().closeView();
            }

        });

//        _profile_card_subscript.setOnClickListener(v -> {
//            LogUtil.d("profile_card_subscript click");
//            SceneDialog sceneDialog = new SceneDialog(SystemUIApplication.getSystemUIContext(), 2268, 640);
//            sceneDialog.show();
//        });
    }

    @Override
    protected void _update_ui(Integer value) {

    }

    @Override
    protected void _update_ui(Bundle bundle) {

    }
}

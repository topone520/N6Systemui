package com.adayo.systemui.windows.dialogs;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adayo.systemui.adapters.SceneRecycleViewAdapter;
import com.adayo.systemui.bean.ScenariomodeBean;
import com.android.systemui.R;
import com.android.systemui.SystemUIApplication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SceneDialog extends BaseDialog {


    private RecyclerView mRecyclerView;
    private SceneRecycleViewAdapter sceneRecycleViewAdapter;

    private List<ScenariomodeBean> mList;
    private ScenariomodeBean scenariomodeBean;

    public SceneDialog(@NonNull Context context, int dialogWidth, int dialogHeight) {
        super(context, dialogWidth, dialogHeight);
    }

    @Override
    protected void initView() {
        initData();
        mRecyclerView = findViewById(R.id.rv_dialog_scene);
        LinearLayoutManager layoutManager = new LinearLayoutManager(_mContext);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(layoutManager);
        sceneRecycleViewAdapter = new SceneRecycleViewAdapter(_mContext, mList);
        mRecyclerView.setAdapter(sceneRecycleViewAdapter);
    }

    @Override
    protected void initData() {
        mList = new ArrayList<ScenariomodeBean>();
        scenariomodeBean = new ScenariomodeBean(R.mipmap.ivi_menu_down_image_rsting_mode, SystemUIApplication.getSystemUIContext().getResources().getString(R.string.screen_scene_take_a_nap), SystemUIApplication.getSystemUIContext().getResources().getString(R.string.screen_scene_forest_oxygen_bar_name));
        mList.add(scenariomodeBean);
        scenariomodeBean = new ScenariomodeBean(R.mipmap.ivi_menu_down_image_meditation_mode, SystemUIApplication.getSystemUIContext().getResources().getString(R.string.screen_scene_meditate), SystemUIApplication.getSystemUIContext().getResources().getString(R.string.screen_scene_forest_oxygen_bar_name));
        mList.add(scenariomodeBean);
        scenariomodeBean = new ScenariomodeBean(R.mipmap.ivi_menu_down_image_forest_mode, SystemUIApplication.getSystemUIContext().getResources().getString(R.string.screen_scene_forest_oxygen_bar), SystemUIApplication.getSystemUIContext().getResources().getString(R.string.screen_scene_forest_oxygen_bar_name));
        mList.add(scenariomodeBean);
        scenariomodeBean = new ScenariomodeBean(R.mipmap.ivi_menu_down_image_wake_mode, SystemUIApplication.getSystemUIContext().getResources().getString(R.string.screen_scene_awakening_mode), SystemUIApplication.getSystemUIContext().getResources().getString(R.string.screen_scene_forest_oxygen_bar_name));
        mList.add(scenariomodeBean);
        scenariomodeBean = new ScenariomodeBean(R.mipmap.ivi_menu_down_image_baby_mode, SystemUIApplication.getSystemUIContext().getResources().getString(R.string.screen_scene_awakening_baby), SystemUIApplication.getSystemUIContext().getResources().getString(R.string.screen_scene_awakening_mode_name));
        mList.add(scenariomodeBean);
        scenariomodeBean = new ScenariomodeBean(R.mipmap.ivi_menu_down_image_air_mode, SystemUIApplication.getSystemUIContext().getResources().getString(R.string.screen_scene_awakening_air), SystemUIApplication.getSystemUIContext().getResources().getString(R.string.screen_scene_forest_oxygen_bar_name));
        mList.add(scenariomodeBean);
        scenariomodeBean = new ScenariomodeBean(R.mipmap.ivi_menu_down_image_camp_mode,SystemUIApplication.getSystemUIContext().getResources().getString(R.string.screen_scene_awakening_camp),SystemUIApplication.getSystemUIContext().getResources().getString(R.string.screen_scene_forest_oxygen_bar_name));
        mList.add(scenariomodeBean);
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_scene;
    }

}

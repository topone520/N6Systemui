package com.adayo.systemui.windows.panels;

import android.content.ClipData;
import android.content.Context;
import android.graphics.PixelFormat;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.RelativeLayout;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.systemui.adapters.ShortcutAppChildAdapter;
import com.adayo.systemui.adapters.ShortcutAppEditAdapter;
import com.adayo.systemui.bean.AppInfo;
import com.adayo.systemui.manager.AllAppsManager;
import com.adayo.systemui.manager.ReportBcmManager;
import com.adayo.systemui.utils.SPUtils;
import com.adayo.systemui.windows.views.BlurTransitionView;
import com.android.systemui.R;
import com.android.systemui.SystemUIApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ShortcutDockEditDialog implements View.OnClickListener {
    private static final String TAG = ShortcutDockEditDialog.class.getSimpleName();
    private volatile static ShortcutDockEditDialog mShortcutDockEditDialog;
    private WindowManager mWindowManager;
    private RelativeLayout mFloatLayout;
    private WindowManager.LayoutParams mLayoutParams;
    private BlurTransitionView _btv_main;
    private RecyclerView _rv_shortcut_edit_app, _rv_shortcut_app;
    private CardView _cv_edit_enter;
    private ShortcutAppEditAdapter _shortcut_app_edit_adapter;
    private ShortcutAppChildAdapter _shortcut_app_child_adapter;

    private List<AppInfo> _app_info_edit_list = new ArrayList<>();
    private List<AppInfo> _app_info_none_list = new ArrayList<>();
    private PagerSnapHelper mPagerSnapHelper;
    private String mAppNameEdit;
    private SPUtils _sp_utils;
    private List<String> shortAppListEdit = new ArrayList<>();
    private List<String> shortAppListEdits = new ArrayList<>();
    private List<String> shortAppListNone = new ArrayList<>();


    public static ShortcutDockEditDialog getInstance() {
        if (mShortcutDockEditDialog == null) {
            synchronized (ShortcutDockEditDialog.class) {
                if (mShortcutDockEditDialog == null) {
                    mShortcutDockEditDialog = new ShortcutDockEditDialog();
                }
            }
        }
        return mShortcutDockEditDialog;
    }

    private ShortcutDockEditDialog() {
        initView();
        initData();
    }

    private void initView() {
        mWindowManager = (WindowManager) SystemUIApplication.getSystemUIContext().getSystemService(Context.WINDOW_SERVICE);
        mFloatLayout = (RelativeLayout) View.inflate(SystemUIApplication.getSystemUIContext(), R.layout.shortcut_dock_edit_layout, null);
        mLayoutParams = new WindowManager.LayoutParams(2000, 560, 2073, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        mLayoutParams.gravity = Gravity.RIGHT | Gravity.TOP;
        mLayoutParams.x = 28;
        mWindowManager.addView(mFloatLayout, mLayoutParams);

        _btv_main = mFloatLayout.findViewById(R.id.btv_main);
        _rv_shortcut_edit_app = mFloatLayout.findViewById(R.id.rv_shortcut_edit_app);
        _rv_shortcut_app = mFloatLayout.findViewById(R.id.rv_shortcut_app);
        _cv_edit_enter = mFloatLayout.findViewById(R.id.cv_edit_enter);

        _cv_edit_enter.setOnClickListener(this);
    }

    private void initData() {
        AllAppsManager allAppsManager = AllAppsManager.getInstance(_rv_shortcut_edit_app.getContext());
        _app_info_none_list = allAppsManager.getShortcutDockNoneApp();
        List<AppInfo> shortDockApp = allAppsManager.getFloatApp();
        _sp_utils = SPUtils.getInstance(_rv_shortcut_edit_app.getContext());
        shortAppListEdit = _sp_utils.getList("short_app_list_edit", null);
        AAOP_LogUtils.d(TAG, "shortAppListEdit: " + shortAppListEdit);
        if (shortAppListEdit.size() == 0) {
            for (AppInfo shortDockApps : shortDockApp) {
                shortAppListEdit.add(shortDockApps.getAppName());
            }
            _sp_utils.saveList("short_app_list_edit", shortAppListEdit);
            _app_info_edit_list.addAll(shortDockApp);
        } else {
            for (String appName : shortAppListEdit) {
                for (AppInfo shortDockApps : shortDockApp) {
                    if (shortDockApps.getAppName().equals(appName)) {
                        _app_info_edit_list.add(shortDockApps);
                        break;
                    }
                }
                for (AppInfo floatApps : _app_info_none_list) {
                    if (floatApps.getAppName().equals(appName)) {
                        _app_info_edit_list.add(floatApps);
                        break;
                    }
                }
            }
        }
        AAOP_LogUtils.d(TAG, "_app_info_edit_list: " + _app_info_edit_list);
        _rv_shortcut_edit_app.setLayoutManager(new LinearLayoutManager(_rv_shortcut_edit_app.getContext(), LinearLayoutManager.HORIZONTAL, false));
        mPagerSnapHelper = new PagerSnapHelper();
        mPagerSnapHelper.attachToRecyclerView(_rv_shortcut_edit_app);
        _shortcut_app_edit_adapter = new ShortcutAppEditAdapter(_app_info_edit_list);
        _rv_shortcut_edit_app.setAdapter(_shortcut_app_edit_adapter);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SystemUIApplication.getSystemUIContext(), LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        _rv_shortcut_app.setLayoutManager(linearLayoutManager);
        _shortcut_app_child_adapter = new ShortcutAppChildAdapter(_app_info_none_list);
        _rv_shortcut_app.setAdapter(_shortcut_app_child_adapter);

        _rv_shortcut_edit_app.setHasFixedSize(true);
        _rv_shortcut_app.setHasFixedSize(true);

        _shortcut_app_edit_adapter.setOnItemDragListener(viewHolder -> {

        });
        _shortcut_app_child_adapter.setOnItemDragListener(viewHolder -> {

        });

        _rv_shortcut_edit_app.setOnDragListener(this::onDrag);
        _rv_shortcut_app.setOnDragListener(this::onDrag2);

    }

    public void setShortcutEditVisibility(int visibility) {
        _btv_main.setVisibility(visibility);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.cv_edit_enter) {
            setShortcutEditVisibility(View.GONE);
            ShortcutDockPanel.getInstance().setShortcutBarVisibility(View.VISIBLE);
            ShortcutDockPanel.getInstance().upDataUI(_app_info_none_list);
        }
    }

    private boolean onDrag(View view, DragEvent dragevent) {
        if (dragevent.getAction() == DragEvent.ACTION_DROP) {
            ClipData.Item itemAt = dragevent.getClipData().getItemAt(0);
            String appView = itemAt.getIntent().getStringExtra("appView");
            String appName = itemAt.getIntent().getStringExtra("appName");
            int position = itemAt.getIntent().getIntExtra("position", 0);
            if (view instanceof RecyclerView) {
                RecyclerView recyclerView = (RecyclerView) view;
                float x = dragevent.getX();
                float y = dragevent.getY();
                View childViewUnder = recyclerView.findChildViewUnder(x, y);
                if (childViewUnder != null) {
                    if (Objects.requireNonNull(appView).equals("rv_shortcut_edit_app")) {
                        int recyclerViewPosition = recyclerView.getChildAdapterPosition(childViewUnder);
                        if (recyclerViewPosition != RecyclerView.NO_POSITION) {
                            RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(recyclerViewPosition);
                            if (viewHolder instanceof ShortcutAppEditAdapter.ViewHolder) {
                                ShortcutAppEditAdapter.ViewHolder itemViewHolder = (ShortcutAppEditAdapter.ViewHolder) viewHolder;
                                GridView gridView = itemViewHolder._grid_view;
                                int gridViewPosition = gridView.pointToPosition((int) x, (int) y);
                                int pos = recyclerViewPosition * 10 + gridViewPosition;
                                int pos1 = recyclerViewPosition * 10 + position;
                                AppInfo appInfo = _app_info_edit_list.get(pos);
                                AppInfo appInfo1 = _app_info_edit_list.get(pos1);
                                _app_info_edit_list.set(pos1, appInfo);
                                _app_info_edit_list.set(pos, appInfo1);
                                _shortcut_app_edit_adapter.setData(_app_info_edit_list);
                                addSpDataEdit(_app_info_edit_list);
                            }
                        }
                    } else {
                        int recyclerViewPosition = recyclerView.getChildAdapterPosition(childViewUnder);
                        if (recyclerViewPosition != RecyclerView.NO_POSITION) {
                            RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(recyclerViewPosition);
                            if (viewHolder instanceof ShortcutAppEditAdapter.ViewHolder) {
                                ShortcutAppEditAdapter.ViewHolder itemViewHolder = (ShortcutAppEditAdapter.ViewHolder) viewHolder;
                                GridView gridView = itemViewHolder._grid_view;
                                int gridViewPosition = gridView.pointToPosition((int) x, (int) y);
                                if (gridViewPosition >= 0 && gridViewPosition < _app_info_edit_list.size() && position >= 0 && position < _app_info_none_list.size()) {
                                    int pos = recyclerViewPosition * 10 + gridViewPosition;
                                    AppInfo appInfo = _app_info_edit_list.get(pos);
                                    _app_info_edit_list.set(pos, _app_info_none_list.get(position));
                                    _app_info_none_list.set(position, appInfo);
                                    _shortcut_app_edit_adapter.setData(_app_info_edit_list);
                                    _shortcut_app_child_adapter.setData(_app_info_none_list);
                                    addSpDataEdit(_app_info_edit_list);
                                    addSpDataNone(_app_info_none_list);
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean onDrag2(View view, DragEvent dragevent) {
        if (dragevent.getAction() == DragEvent.ACTION_DROP) {
            ClipData.Item itemAt = dragevent.getClipData().getItemAt(0);
            String appView = itemAt.getIntent().getStringExtra("appView");
            String appName = itemAt.getIntent().getStringExtra("appName");
            int position = itemAt.getIntent().getIntExtra("position", 0);
            if (view instanceof RecyclerView) {
                RecyclerView recyclerView = (RecyclerView) view;
                float x = dragevent.getX();
                float y = dragevent.getY();
                View childViewUnder = recyclerView.findChildViewUnder(x, y);
                if (childViewUnder != null) {
                    if (Objects.requireNonNull(appView).equals("rv_shortcut_app")) {
                        int childAdapterPosition = recyclerView.getChildAdapterPosition(childViewUnder);
                        AppInfo appInfo = _app_info_none_list.get(position);
                        AppInfo appInfo1 = _app_info_none_list.get(childAdapterPosition);
                        _app_info_none_list.set(childAdapterPosition, appInfo);
                        _app_info_none_list.set(position, appInfo1);
                        _shortcut_app_child_adapter.setData(_app_info_none_list);
                        addSpDataNone(_app_info_none_list);
                    } else {
                        // 获取当前拖动的位置
                        View childViewUnder1 = _rv_shortcut_edit_app.findChildViewUnder(_rv_shortcut_edit_app.getX(), _rv_shortcut_edit_app.getY());
                        int childAdapterPosition1 = _rv_shortcut_edit_app.getChildAdapterPosition(childViewUnder1);
                        int childAdapterPosition = recyclerView.getChildAdapterPosition(childViewUnder);
                        if (childAdapterPosition >= 0 && childAdapterPosition < _app_info_none_list.size() && position >= 0 && position < _app_info_edit_list.size()) {
                            int pos = childAdapterPosition1 * 10 + position;
                            AppInfo appInfo = _app_info_none_list.get(childAdapterPosition);
                            _app_info_none_list.set(childAdapterPosition, _app_info_edit_list.get(pos));
                            _app_info_edit_list.set(pos, appInfo);
                            _shortcut_app_edit_adapter.setData(_app_info_edit_list);
                            _shortcut_app_child_adapter.setData(_app_info_none_list);
                            addSpDataEdit(_app_info_edit_list);
                            addSpDataNone(_app_info_none_list);
                        }
                    }
                }
            }

        }
        return true;
    }

    private void addSpDataEdit(List<AppInfo> app_info_edit_list) {
        shortAppListEdits.clear();
        for (AppInfo app_info_edit_lists : app_info_edit_list) {
            shortAppListEdits.add(app_info_edit_lists.getAppName());
        }
        AAOP_LogUtils.d(TAG, "addSpData shortAppListEdits: " + shortAppListEdits);
        _sp_utils.saveList("short_app_list_edit", shortAppListEdits);
    }

    private void addSpDataNone(List<AppInfo> app_info_none_list) {
        shortAppListNone.clear();
        for (AppInfo app_info_none_lists : app_info_none_list) {
            shortAppListNone.add(app_info_none_lists.getAppName());
        }
        AAOP_LogUtils.d(TAG, "addSpData shortAppListNone: " + shortAppListNone);
        _sp_utils.saveList("short_app_list_none", shortAppListNone);
    }


}

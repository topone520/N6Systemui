package com.adayo.systemui.windows.dialogs;

import android.content.Context;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.adayo.systemui.adapters.BaseAdapter;
import com.adayo.systemui.adapters.GalleryLayoutManager;
import com.adayo.systemui.adapters.HorizontalDecoration;
import com.adayo.systemui.adapters.Picture;
import com.adayo.systemui.adapters.PictureAdapter;
import com.adayo.systemui.windows.views.GalleryRecyclerView;
import com.adayo.systemui.windows.views.ScreenVolumeSeekBar;
import com.adayo.systemui.windows.views.SwitchButtonVe;
import com.android.systemui.R;

import java.util.ArrayList;
import java.util.List;

public class AtmosphereLightDialog extends BaseDialog implements View.OnClickListener {

    private SwitchButtonVe lightSwitch;
    private TextView tvMoreSetting;
    private ImageView ivEnterMoreSetting;
    private RadioGroup typeGroup;
    private ScreenVolumeSeekBar brightnessAdjust;
    private GalleryRecyclerView _galleryRecyclerView;
    private PictureAdapter pictureAdapter;

    private Integer[] pictures = {
            R.mipmap.ivi_menu_color_card_red_s_day, R.mipmap.ivi_menu_color_card_orange_s_day,
            R.mipmap.ivi_menu_color_card_yellow_s_day, R.mipmap.ivi_menu_color_card_green_s_day,
            R.mipmap.ivi_menu_color_card_dark_green_s_day, R.mipmap.ivi_menu_color_card_azure_s_day,
            R.mipmap.ivi_menu_color_card_cyan_s_day, R.mipmap.ivi_menu_color_card_blue_s_day,
            R.mipmap.ivi_menu_color_card_purple_s_day_day, R.mipmap.ivi_menu_color_card_pink_s_day
    };

    private final String[] textViews = {
            getContext().getString(R.string.lava_red), getContext().getString(R.string.dawn_orange),
            getContext().getString(R.string.misty_yellow), getContext().getString(R.string.tundra_green),
            getContext().getString(R.string.cedar_green), getContext().getString(R.string.sky_blue),
            getContext().getString(R.string.glacier_blue), getContext().getString(R.string.darkness_night),
            getContext().getString(R.string.berry_purple), getContext().getString(R.string.rose_powder)
    };
    public AtmosphereLightDialog(@NonNull Context context, int dialogWidth, int _dialogHeight) {
        super(context, dialogWidth, _dialogHeight);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void initView() {
        lightSwitch = findViewById(R.id.sb_light_switch);
        tvMoreSetting = findViewById(R.id.tv_more_setting);
        ivEnterMoreSetting = findViewById(R.id.iv_enter_more_setting);
        typeGroup = findViewById(R.id.rg_type);
        brightnessAdjust = findViewById(R.id.sb_brightness_adjustment);
        _galleryRecyclerView = findViewById(R.id.rv_fwlightcolor);
        initRecyclerView();
    }

    private void initRecyclerView() {
        pictureAdapter = new PictureAdapter();
        _galleryRecyclerView.setAdapter(pictureAdapter);
        GalleryLayoutManager layoutManager = new GalleryLayoutManager(getContext(), RecyclerView.HORIZONTAL,false);
        _galleryRecyclerView.setLayoutManager(layoutManager);
        _galleryRecyclerView.addItemDecoration(new HorizontalDecoration(0));
        LinearSnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(_galleryRecyclerView);
        _galleryRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        pictureAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                _galleryRecyclerView.smoothScrollToPosition(pos);
            }
        });
        pictureAdapter.setDataList(getStarsList());
    }
    @Override
    protected void initData() {
        tvMoreSetting.setOnClickListener(this);
        ivEnterMoreSetting.setOnClickListener(this);
    }

    @Override
    protected void initListener() {
        lightSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });

        typeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.rb1) {

                } else if (checkedId == R.id.rb2) {

                } else if (checkedId == R.id.rb3) {

                } else if (checkedId == R.id.rb4) {

                }
            }
        });

        brightnessAdjust.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    private List<Picture> getStarsList() {
        List<Picture> pictureList = new ArrayList<>();
        for (int i = 0; i < pictures.length; i++) {
            pictureList.add(new Picture(i, pictures[i % pictures.length], textViews[i % textViews.length]));
        }
        return pictureList;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_atmosphere_light;
    }

}

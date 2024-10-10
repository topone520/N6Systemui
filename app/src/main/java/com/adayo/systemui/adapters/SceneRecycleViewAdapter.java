package com.adayo.systemui.adapters;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;

import com.adayo.systemui.bean.ScenariomodeBean;
import com.android.systemui.R;

import java.util.List;

public class SceneRecycleViewAdapter extends BaseRecyclerViewAdapter<ScenariomodeBean> {

    public SceneRecycleViewAdapter(Context mContext, List<ScenariomodeBean> mList) {
        super(mContext, mList);
    }

    @Override
    protected void showOnBindViewHolder(BaseRecyclerHolderView holder, int position) {
        HolderView holderView = (HolderView) holder;
        if (getItemViewType(position) == TYPE_NORMAL) {
            if (mHeaderView == null) {
                holderView.tvItemSceneName.setText(mList.get(position).getName());
                holderView.ivItemScenLevel.setText(mList.get(position).getScenar());


            } else {
                holderView.tvItemSceneName.setText(mList.get(position - 1).getName());
                holderView.ivItemScenLevel.setText(mList.get(position+1).getScenar());
            }
            holderView.rlSceneItem.setBackground(mContext.getResources().getDrawable(mList.get(position).getImage()));
        } else {
            if (mHeaderView == null) {
                holderView.tvItemSceneName.setText(mList.get(0).getName());
                holderView.rlSceneItem.setBackground(mContext.getResources().getDrawable(mList.get(position).getImage()));
                holderView.ivItemScenLevel.setText(mList.get(0).getScenar());
            }

        }
        //点击事件
        holderView.ivmusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if (holderView.ivmusic.isSelected()){
                   holderView.ivmusic.setSelected(false);
                   holderView.ivmusic.setText(mContext.getResources().getString(R.string.screen_scene_start));
               }else {
                   holderView.ivmusic.setSelected(true);
                   holderView.ivmusic.setText(mContext.getResources().getString(R.string.screen_scene_stop));
               }
            }
        });
    }

    @Override
    protected BaseRecyclerHolderView showOnCreateViewHolder(ViewGroup parent, int viewType) {
        if (mHeaderView != null && viewType == TYPE_HEADER) {
            return new HolderView(mHeaderView);
        }
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_scene_item, parent, false);
        return new HolderView(layout);
    }

    @Override
    public void setHeaderView(View headerView) {
        mHeaderView = headerView;
        notifyItemInserted(0);
    }

    public static class HolderView extends BaseRecyclerHolderView {
        AppCompatButton ivmusic;
        TextView tvItemSceneName;
        RelativeLayout rlSceneItem;
        TextView ivItemScenLevel;
        public HolderView(View itemView) {
            super(itemView);
            ivmusic=itemView.findViewById(R.id.iv_item_music);
            ivItemScenLevel=itemView.findViewById(R.id.iv_item_scene_Level);
            tvItemSceneName = itemView.findViewById(R.id.iv_item_scene_sel);
            rlSceneItem = itemView.findViewById(R.id.rl_scene_item);
        }
    }
    //1.定义变量接收接


}

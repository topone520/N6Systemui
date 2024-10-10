package com.adayo.systemui.adapters;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.systemui.bean.RefreshWarningMessageEvent;
import com.adayo.systemui.bean.WarningMessageInfo;
import com.android.systemui.R;
import com.android.systemui.SystemUIApplication;
import com.google.gson.Gson;


import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * The type Warning message adapter.
 */
public class WarningMessageAdapter extends RecyclerView.Adapter<WarningMessageAdapter.MyViewHolder> {
    private static String TAG = WarningMessageAdapter.class.getName();
    private List<WarningMessageInfo> warningMessageInfoList;
    private boolean isCheckedBtnShow;
    private boolean isSelectAll;
    private boolean isDelete;
    private OnCheckedListener onCheckedListener;
    private OnItemClickListener onItemClickListener;

    private int currentPos;
    private Context mContext;

    /**
     * 设置数据.
     *
     * @param mWarningMessageInfoList 告警消息实体类
     */
    public void setWarningMessageInfoList(List<WarningMessageInfo> mWarningMessageInfoList) {
        Gson gson = new Gson();
        this.warningMessageInfoList = mWarningMessageInfoList;
        Log.i(TAG, "warningMessageInfoList setWarningMessageInfoList" + gson.toJson(this.warningMessageInfoList));
        notifyDataSetChanged();
    }


    /**
     * check box点击事件.
     *
     * @param onCheckedListener the on checked listener
     */
    public void setOnCheckedListener(OnCheckedListener onCheckedListener) {
        this.onCheckedListener = onCheckedListener;
    }

    /**
     * item点击事件.
     *
     * @param onItemClickListener the on item click listener
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * 判断check box是否显示.
     *
     * @param checkedBtnShow the checked btn show
     */
    public void setCheckedBtnShow(boolean checkedBtnShow) {
        isCheckedBtnShow = checkedBtnShow;
        notifyDataSetChanged();
    }


    /**
     * Instantiates a new Warning message adapter.
     *
     * @param warningMessageInfoList 告警消息实体类
     * @param context                the context
     */
    public WarningMessageAdapter(List<WarningMessageInfo> warningMessageInfoList, Context context) {
        this.warningMessageInfoList = warningMessageInfoList;
        Gson gson = new Gson();
        Log.i(TAG, "warningMessageInfoList WarningMessageAdapter" + gson.toJson(this.warningMessageInfoList));

        mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = AAOP_HSkin.getLayoutInflater(SystemUIApplication.getSystemUIContext()).inflate(R.layout.item_warning_message, viewGroup, false);
        WarningMessageAdapter.MyViewHolder viewHolder = new WarningMessageAdapter.MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, int i) {
        Gson gson = new Gson();
        Log.i(TAG, "warningMessageInfoList onBindViewHolder" + gson.toJson(warningMessageInfoList));
        myViewHolder.tvTime.setText(getTime(i));
        WarningMessageInfo warningMessageInfo = warningMessageInfoList.get(i);
//        myViewHolder.tvTitle.setText(warningMessageInfo.getTitle());
        AAOP_HSkin.with(myViewHolder.tvTitle)
                .addViewAttrs(AAOP_HSkin.ATTR_TEXT,R.string.warning_message)
                .applyLanguage(false);

//        myViewHolder.tvTitle.setText("告警消息");
        myViewHolder.tvContent.setText(warningMessageInfo.getMessage());
        Log.i(TAG, "warningMessageInfoList warningMessageInfo" + gson.toJson(warningMessageInfo));

        boolean isCheck = warningMessageInfo.isChecked();
        if (isCheckedBtnShow) {
            myViewHolder.ivCheck.setVisibility(View.VISIBLE);
        } else {
            myViewHolder.ivCheck.setVisibility(View.GONE);
        }

        if (warningMessageInfo.isRead()) {
            myViewHolder.ivRead.setVisibility(View.GONE);
        } else {
            myViewHolder.ivRead.setVisibility(View.VISIBLE);
        }

        if (isCheck) {
            AAOP_HSkin.with(myViewHolder.ivCheck).addViewAttrs(AAOP_HSkin.ATTR_SRC,R.mipmap.ivi_new_select_1).applySkin(false);
//            myViewHolder.ivCheck.setImageResource(R.mipmap.ivi_new_select);
        } else {
            AAOP_HSkin.with(myViewHolder.ivCheck).addViewAttrs(AAOP_HSkin.ATTR_SRC,R.mipmap.ivi_new_notselect_1).applySkin(false);
//            myViewHolder.ivCheck.setImageResource(R.mipmap.ivi_new_notselect);
        }


        myViewHolder.ivCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newId = warningMessageInfo.getId();
                for (WarningMessageInfo info : warningMessageInfoList) {
                    String oldId = info.getId();
                    if (Objects.equals(newId, oldId)) {
                        boolean isCheck1 = info.isChecked();
                        if (isCheck1) {
                            info.setChecked(false);
                        } else {
                            info.setChecked(true);
                        }
                        break;
                    }
                }
                notifyDataSetChanged();
                onCheckedListener.onChecked(currentPos, warningMessageInfoList);
                Log.i(TAG, "warningMessageInfoList onChecked" + gson.toJson(warningMessageInfoList));

            }
        });

        myViewHolder.llItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return warningMessageInfoList.size();
    }

    /**
     * The interface On checked listener.
     */
    public interface OnCheckedListener {
        /**
         * On checked.
         *
         * @param pos                    the pos
         * @param warningMessageInfoList the warning message info list
         */
        void onChecked(int pos, List<WarningMessageInfo> warningMessageInfoList);
    }

    /**
     * The interface On item click listener.
     */
    public interface OnItemClickListener {
        /**
         * On item click.
         *
         * @param pos the pos
         */
        void onItemClick(int pos);
    }

    /**
     * The type My view holder.
     */
    class MyViewHolder extends RecyclerView.ViewHolder {
        /**
         * The Iv check.
         */
        ImageView ivCheck;
        /**
         * The Iv read.
         */
        ImageView ivRead;
        /**
         * The Tv title.
         */
        TextView tvTitle;
        /**
         * The Tv time.
         */
        TextView tvTime;
        /**
         * The Tv content.
         */
        TextView tvContent;
        /**
         * The Ll item.
         */
        LinearLayout llItem;

        /**
         * Instantiates a new My view holder.
         *
         * @param itemView the item view
         */
        public MyViewHolder(View itemView) {
            super(itemView);
            ivCheck = itemView.findViewById(R.id.iv_check);
            ivRead = itemView.findViewById(R.id.iv_unread);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvContent = itemView.findViewById(R.id.tv_content);
            llItem = itemView.findViewById(R.id.ll_item);
        }
    }

    /**
     * 删除item.
     *
     * @param position the position
     */
    public void deleteItem(int position) {
        warningMessageInfoList.remove(position);
        notifyItemRemoved(position);
//        mContext.sendBroadcast(new Intent("refresh_warning_message"));
//        Log.e("l---y", "sendBroadcast");
        EventBus.getDefault().post(new RefreshWarningMessageEvent());
    }

    /**
     * 获取时间.
     *
     * @param pos the pos
     * @return the time
     */
    public String getTime(int pos) {
        long currentTime = System.currentTimeMillis();
        Log.i(TAG,"currentTime : " + currentTime);
        long lastTime = warningMessageInfoList.get(pos).getReceiveTime();
        Log.i(TAG,"lastTime : " + lastTime);
        long time = currentTime - lastTime;
        SimpleDateFormat dateFormatHour = new SimpleDateFormat("HH:mm");
        SimpleDateFormat dateFormatMonth = new SimpleDateFormat("MM/dd HH:mm");
        SimpleDateFormat dateFormatYear = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        Date date = new Date(time);
        String timeText = "";
        if (time < 60000) {
            timeText = mContext.getString(R.string.a_moment_ago);
        } else if (time < 60000 * 60 * 60) {
            timeText = time / 60000 + mContext.getString(R.string.minutes_ago);
        } else if (time > 60000 * 60 * 60 && time < 24L * 60 * 60 * 60000) {
            timeText = time / 60000 / 60 / 60 + mContext.getString(R.string.hours_ago);
        } else if (time > 24L * 60 * 60 * 60000 && time < 48L * 60 * 60 * 60000) {
            timeText = mContext.getString(R.string.yesterday) + dateFormatHour.format(date);
        } else if (time > 48L * 60 * 60 * 60000 && time < 24L * 60 * 60 * 60000 * 365) {
            timeText = dateFormatMonth.format(date);
        } else if (time > 24L * 60 * 60 * 60000 * 365) {
            timeText = dateFormatYear.format(date);
        }

        return timeText;
    }
}

package com.adayo.systemui.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.systemui.bean.RefreshSystemMessageEvent;
import com.adayo.systemui.bean.RefreshWarningMessageEvent;
import com.adayo.systemui.bean.SystemMessageInfo;
import com.adayo.systemui.notification.TrackingEnum;
import com.adayo.systemui.utils.SPHelper;
import com.adayo.systemui.utils.TrackingMessageDataUtil;
import com.android.systemui.R;
import com.android.systemui.SystemUIApplication;
import com.google.gson.Gson;


import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * The type System message adapter.
 */
public class SystemMessageAdapter extends RecyclerView.Adapter<SystemMessageAdapter.MyViewHolder> {
    private List<SystemMessageInfo> systemMessageInfoList;
    private boolean isCheckedBtnShow;
    private boolean isSelectAll;
    private boolean isDelete;
    private OnCheckedListener onCheckedListener;
    private OnItemClickListener onItemClickListener;

    private int currentPos;
    private Context mContext;

    /**
     * 设置显示数据并更新UI.
     *
     * @param systemMessageInfoList 消息实体
     */
    public void setNotificationInfoList(List<SystemMessageInfo> systemMessageInfoList) {
        this.systemMessageInfoList = systemMessageInfoList;
        notifyDataSetChanged();
    }

    /**
     * check box点击回调.
     *
     * @param onCheckedListener the on checked listener
     */
    public void setOnCheckedListener(OnCheckedListener onCheckedListener) {
        this.onCheckedListener = onCheckedListener;
    }

    /**
     * item点击回调.
     *
     * @param onItemClickListener the on item click listener
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * 判断是否显示checkbox.
     *
     * @param checkedBtnShow the checked btn show
     */
    public void setCheckedBtnShow(boolean checkedBtnShow) {
        isCheckedBtnShow = checkedBtnShow;
        notifyDataSetChanged();
    }

    /**
     * Instantiates a new System message adapter.
     *
     * @param systemMessageInfoList the system message info list
     * @param context               the context
     */
    public SystemMessageAdapter(List<SystemMessageInfo> systemMessageInfoList, Context context) {
        this.systemMessageInfoList = systemMessageInfoList;
        mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = AAOP_HSkin.getLayoutInflater(SystemUIApplication.getSystemUIContext()).inflate(R.layout.item_system_message, viewGroup, false);
        SystemMessageAdapter.MyViewHolder viewHolder = new SystemMessageAdapter.MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, int i) {
        myViewHolder.tvTime.setText(getTime(i));
        SystemMessageInfo systemMessageInfo = systemMessageInfoList.get(i);
        myViewHolder.tvTitle.setText(systemMessageInfo.getTitle());
        myViewHolder.tvContent.setText(systemMessageInfo.getMessage());
        boolean isCheck = systemMessageInfo.isChecked();
        if (isCheckedBtnShow) {
            myViewHolder.ivCheck.setVisibility(View.VISIBLE);
        } else {
            myViewHolder.ivCheck.setVisibility(View.GONE);
        }


        if (isCheck) {
            AAOP_HSkin.with(myViewHolder.ivCheck).addViewAttrs(AAOP_HSkin.ATTR_SRC,R.mipmap.ivi_new_select_1).applySkin(false);
//            myViewHolder.ivCheck.setImageResource(R.mipmap.ivi_new_select);
        } else {
            AAOP_HSkin.with(myViewHolder.ivCheck).addViewAttrs(AAOP_HSkin.ATTR_SRC,R.mipmap.ivi_new_notselect_1).applySkin(false);
//            myViewHolder.ivCheck.setImageResource(R.mipmap.ivi_new_notselect);
        }

        if (systemMessageInfo.isRead()) {
            myViewHolder.ivRead.setVisibility(View.GONE);
        } else {
            myViewHolder.ivRead.setVisibility(View.VISIBLE);
        }

        myViewHolder.ivCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newId = systemMessageInfo.getId();
                for (SystemMessageInfo info : systemMessageInfoList) {
                    int oldId = info.getId();
                    if (newId == oldId) {
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
                onCheckedListener.onChecked(currentPos, systemMessageInfoList);
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
        return systemMessageInfoList.size();
    }

    /**
     * The interface On checked listener.
     */
    public interface OnCheckedListener {
        /**
         * On checked.
         *
         * @param pos                   the pos
         * @param systemMessageInfoList the system message info list
         */
        void onChecked(int pos, List<SystemMessageInfo> systemMessageInfoList);
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
        TextView tvContent;
        /**
         * The Tv time.
         */
        TextView tvTime;
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
            tvContent = itemView.findViewById(R.id.tv_content);
            tvTime = itemView.findViewById(R.id.tv_time);
            llItem = itemView.findViewById(R.id.ll_item);
        }
    }

    /**
     * 删除item.
     *
     * @param position the position
     */
    public void deleteItem(int position) {
        systemMessageInfoList.remove(position);
        notifyItemRemoved(position);
        Gson gson = new Gson();
        SPHelper spHelper = new SPHelper(SystemUIApplication.getSystemUIContext(), "NotifyService");
        String systemMessageInfoListJson = gson.toJson(systemMessageInfoList);
        spHelper.putValues(new SPHelper.ContentValue("systemMessageInfoJson", systemMessageInfoListJson));
//        mContext.sendBroadcast(new Intent("refresh_system_message"));
        TrackingMessageDataUtil.getInstance().trackingData(TrackingEnum.MESSAGE_SLIDE_UP.getBehaviorid());
        EventBus.getDefault().post(new RefreshSystemMessageEvent());
    }


    /**
     * 获取时间.
     *
     * @param pos the pos
     * @return the time
     */
    public String getTime(int pos) {
        long currentTime = System.currentTimeMillis();
        long lastTime = systemMessageInfoList.get(pos).getReceiveTime();
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

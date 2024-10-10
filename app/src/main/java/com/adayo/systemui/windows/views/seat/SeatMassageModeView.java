package com.adayo.systemui.windows.views.seat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.systemui.bean.SeatMassageBean;
import com.adayo.systemui.utils.GridSpacingItemDecoration;
import com.android.systemui.R;

import java.util.ArrayList;
import java.util.List;

public class SeatMassageModeView extends FrameLayout {
    private static String TAG = SeatMassageModeView.class.getSimpleName();
    private SeatMagAdapter seatMagAdapter;
    private List<SeatMassageBean> massageBeanList = null;
    private RecyclerView seatMassageRlv;

    private MassageMoveListener moveListener;

    public void setMoveListener(MassageMoveListener moveListener) {
        this.moveListener = moveListener;
    }

    public SeatMassageModeView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    private void initialize() {
        seatMassageRlv = new RecyclerView(getContext());
        addView(seatMassageRlv);
        init_add_data();
        init_rlv();
    }

    private void init_rlv() {
        seatMagAdapter = new SeatMagAdapter();
        seatMassageRlv.setLayoutManager(new GridLayoutManager(getContext(), 4));
        seatMassageRlv.setAdapter(seatMagAdapter);
        seatMassageRlv.addItemDecoration(new GridSpacingItemDecoration(4, 40, false));
    }

    private void init_add_data() {
        if (massageBeanList == null) massageBeanList = new ArrayList<>();
        massageBeanList.add(new SeatMassageBean(getContext().getResources().getString(R.string.seat_move1), true));
        massageBeanList.add(new SeatMassageBean(getContext().getResources().getString(R.string.seat_move2), false));
        massageBeanList.add(new SeatMassageBean(getContext().getResources().getString(R.string.seat_move3), false));
        massageBeanList.add(new SeatMassageBean(getContext().getResources().getString(R.string.seat_move4), false));
        massageBeanList.add(new SeatMassageBean(getContext().getResources().getString(R.string.seat_move5), false));
        massageBeanList.add(new SeatMassageBean(getContext().getResources().getString(R.string.seat_move6), false));
        massageBeanList.add(new SeatMassageBean(getContext().getResources().getString(R.string.seat_move7), false));
        massageBeanList.add(new SeatMassageBean(getContext().getResources().getString(R.string.seat_move8), false));
    }

    private int oldSelectPosition = 0;

    class SeatMagAdapter extends RecyclerView.Adapter<SeatMagAdapter.MagViewHolder> {
        @NonNull
        @Override
        public MagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MagViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.seat_move_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull MagViewHolder holder, @SuppressLint("RecyclerView") int position) {
            AAOP_HSkin.with(holder._tv_move).addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, massageBeanList.get(position).isMoveCheckBox() ? R.drawable.comm_c4972b8_bg : R.drawable.seat_message_no_select_bg).applySkin(false);
            holder._tv_move.setText(massageBeanList.get(position).getMode());
            AAOP_HSkin.with(holder._tv_move).addViewAttrs(AAOP_HSkin.ATTR_TEXT_COLOR, massageBeanList.get(position).isMoveCheckBox() ? R.color.text_color_select1 : R.color.text_color_normals1).applySkin(false);

            holder._tv_move.setOnClickListener(v -> {
                massageBeanList.get(position).setMoveCheckBox(!massageBeanList.get(position).isMoveCheckBox());
                massageBeanList.get(oldSelectPosition).setMoveCheckBox(!massageBeanList.get(oldSelectPosition).isMoveCheckBox());
                notifyItemChanged(oldSelectPosition);
                notifyItemChanged(position);
                oldSelectPosition = position;
                //坐标比信号小 1
                if (moveListener == null) return;
                moveListener.onSelectMassageMove(position);
            });
        }

        @Override
        public int getItemCount() {
            return massageBeanList.size();
        }

        class MagViewHolder extends RecyclerView.ViewHolder {
            TextView _tv_move;

            public MagViewHolder(@NonNull View itemView) {
                super(itemView);
                _tv_move = itemView.findViewById(R.id.tv_move);
            }
        }
    }

    public void update_ui(int position) {
        AAOP_LogUtils.d(TAG, "position = " + position);
        if (seatMagAdapter == null) return;
        if (position < 0 || position > massageBeanList.size() - 1) return;
        massageBeanList.set(oldSelectPosition, new SeatMassageBean(massageBeanList.get(oldSelectPosition).getMode(), false));
        massageBeanList.set(position, new SeatMassageBean(massageBeanList.get(position).getMode(), true));
        seatMagAdapter.notifyItemChanged(oldSelectPosition);
        seatMagAdapter.notifyItemChanged(position);
        oldSelectPosition = position;
        AAOP_LogUtils.d(TAG, "refresh end...");
    }

    public interface MassageMoveListener {
        void onSelectMassageMove(int pos);
    }
}

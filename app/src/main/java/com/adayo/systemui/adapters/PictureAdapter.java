package com.adayo.systemui.adapters;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import com.android.systemui.R;

public class PictureAdapter extends BaseAdapter<Picture>{
    private static final String TAG = "PictureAdapter";
    @Override
    public void setData(ViewHolder holder,int pos) {
        if(null != getDataList().get(pos)){
            //实例化图片显示
            View view = holder.itemView;
            ImageView imageView = holder.imageView;
            TextView textView = holder.textView;
            Log.d(TAG,"position ="+pos+",current size = "+getDataList().size());
            //为专辑图片设置倒影显示
//            new BitmapUtil().reflectionBitmap(getDataList().get(pos).url , imageView , new SimpleTarget<Bitmap>(){
//                @Override
//                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                    imageView.setImageBitmap(resource);
//                }
//            });
            imageView.setImageResource(getDataList().get(pos).url);
            textView.setText(getDataList().get(pos).text + "");
        }
    }

    /**
     * 设置子视图的布局id
     * @return
     */
    @Override
    public int getId() {
        return R.layout.item_picture;
    }

    @Override
    public boolean areItemsTheSame(@NonNull Picture oldItem, @NonNull Picture newItem) {
        return oldItem.id == (newItem.id);
    }

    @Override
    public boolean areContentsTheSame(@NonNull Picture oldItem, @NonNull Picture newItem) {
        return oldItem.url.equals(newItem.url);
    }
}


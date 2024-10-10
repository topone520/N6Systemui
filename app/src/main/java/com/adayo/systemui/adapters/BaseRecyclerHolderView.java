package com.adayo.systemui.adapters;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.StringRes;
import androidx.recyclerview.widget.RecyclerView;

public class BaseRecyclerHolderView extends RecyclerView.ViewHolder{

    private final SparseArray<View> views;

    public BaseRecyclerHolderView(View view) {
        super(view);
        this.views = new SparseArray<>();
    }

    public <T extends View> T getView(int viewId) {
        View view = views.get(viewId);
        if (view == null) {
            view = itemView.findViewById(viewId);
            views.put(viewId, view);
        }
        return (T) view;
    }

    public View getItemView() {
        return itemView;
    }

    public BaseRecyclerHolderView setText(@IdRes int viewId, CharSequence value) {
        TextView view = this.getView(viewId);
        view.setText(value);
        return this;
    }

    public BaseRecyclerHolderView setText(@IdRes int viewId, @StringRes int strId) {
        TextView view = this.getView(viewId);
        view.setText(strId);
        return this;
    }

    public BaseRecyclerHolderView setImageResource(@IdRes int viewId, @DrawableRes int imageResId) {
        ImageView view = this.getView(viewId);
        view.setImageResource(imageResId);
        return this;
    }

    public BaseRecyclerHolderView setBackgroundColor(@IdRes int viewId, @ColorInt int color) {
        View view = this.getView(viewId);
        view.setBackgroundColor(color);
        return this;
    }

    public BaseRecyclerHolderView setBackgroundRes(@IdRes int viewId, @DrawableRes int backgroundRes) {
        View view = this.getView(viewId);
        view.setBackgroundResource(backgroundRes);
        return this;
    }

    public BaseRecyclerHolderView setTextColor(@IdRes int viewId, @ColorInt int textColor) {
        TextView view = this.getView(viewId);
        view.setTextColor(textColor);
        return this;
    }

    public BaseRecyclerHolderView setImageDrawable(@IdRes int viewId, Drawable drawable) {
        ImageView view = this.getView(viewId);
        view.setImageDrawable(drawable);
        return this;
    }

    public BaseRecyclerHolderView setImageBitmap(@IdRes int viewId, Bitmap bitmap) {
        ImageView view = this.getView(viewId);
        view.setImageBitmap(bitmap);
        return this;
    }

    public BaseRecyclerHolderView setGone(@IdRes int viewId, boolean visible) {
        View view = this.getView(viewId);
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
        return this;
    }

    public BaseRecyclerHolderView setVisible(@IdRes int viewId, boolean visible) {
        View view = this.getView(viewId);
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
        return this;
    }
}
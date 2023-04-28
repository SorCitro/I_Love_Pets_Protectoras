package com.example.ilpp.classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.ilpp.classes.model.Model;

import java.util.List;

public class ModelAdapter<T extends Model> extends ArrayAdapter<T> {

    public interface OnCreateViewListener { void onCreateView(View view, int position); }
    private OnCreateViewListener onCreateListener;
    public void setOnCreateViewListener(OnCreateViewListener listener) {
        this.onCreateListener = listener;
    }

    private final int resLayout;
    public ModelAdapter(Context context, List<T> data, int resLayout) {
        super(context, 0, data);
        this.resLayout = resLayout;
    }

    public ModelAdapter(Context context, int resLayout) {
        super(context, 0);
        this.resLayout = resLayout;
    }

    public ModelAdapter(Context context) {
        super(context, 0);
        this.resLayout = 0;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (this.resLayout == 0) {
            return super.getView(position, convertView, parent);
        }

        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(this.resLayout, parent, false);
        }

        if (onCreateListener != null) {
            onCreateListener.onCreateView(view, position);
        }

        return view;
    }
}

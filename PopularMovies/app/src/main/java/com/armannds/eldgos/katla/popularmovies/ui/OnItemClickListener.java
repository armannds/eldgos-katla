package com.armannds.eldgos.katla.popularmovies.ui;

import android.view.View;

public interface OnItemClickListener<T> {
    void onItemClicked(View view, T itemClicked);
}

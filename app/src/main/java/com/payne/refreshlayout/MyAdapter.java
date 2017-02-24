package com.payne.refreshlayout;

import android.support.v7.widget.RecyclerView;

import cn.bingoogolapple.androidcommon.adapter.BGARecyclerViewAdapter;
import cn.bingoogolapple.androidcommon.adapter.BGAViewHolderHelper;


public class MyAdapter extends BGARecyclerViewAdapter<String> {
    public MyAdapter(RecyclerView recyclerView) {
        super(recyclerView, R.layout.item);
    }

    @Override
    protected void fillData(BGAViewHolderHelper helper, int position, String model) {
        helper.setText(R.id.tv, model);

    }
}

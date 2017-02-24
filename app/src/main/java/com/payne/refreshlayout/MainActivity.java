package com.payne.refreshlayout;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.payne.refreshandloadmore.RefreshCallBack;
import com.payne.refreshandloadmore.RefreshLayout;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements RefreshCallBack {
    private RefreshLayout mRefresh;
    private RecyclerView mRecyclerView;
    private Context mContext = this;
    private List<String> mList;
    private MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRefresh = (RefreshLayout) findViewById(R.id.refresh);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        View headView = LayoutInflater.from(mContext).inflate(R.layout.header_refresh_a, null);
        mRefresh.setHeadView(headView);
        mList = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            mList.add("item" + i);
        }
        mAdapter = new MyAdapter(mRecyclerView);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setData(mList);
        mRefresh.setRefreshCallBack(this);

    }


    @Override
    public void beginRefresh() {

    }

    @Override
    public void beginLoadMore() {
        mAdapter.addLastItem("sss");
        mAdapter.notifyDataSetChanged();

    }
}

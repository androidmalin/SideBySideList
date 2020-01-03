package com.malin.sidebyside.list;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.malin.sidebyside.R;

import java.util.ArrayList;

/**
 * Android两条并排RecyclerView实时联动滑动增强
 * <p>
 * https://blog.csdn.net/zhangphil/article/details/80068842
 */
public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView1;
    private RecyclerView mRecyclerView2;
    private ArrayList<Integer> mItems;


    /**
     * dispatchTouchEvent
     *
     * @param event event
     * @return 返回true，表示拦截事件。
     * 返回false，表示不做任何处理，交给子View处理。
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        //如果用户的手指同时放在屏幕上滑动，不要触发滚动事件。
        if (event.getPointerCount() >= 2) {
            return true;
        }

        //如果左侧的RecyclerView1在滚动中，但是此时用户又在RecyclerView2中触发滚动事件，则停止所有滚动，等待新一轮滚动。
        if (mRecyclerView1.getScrollState() != RecyclerView.SCROLL_STATE_IDLE) {
            //在滚动中
            if (touchEventInView(mRecyclerView2, event.getX(), event.getY())) {
                mRecyclerView1.stopScroll();
                mRecyclerView2.stopScroll();
                return true;
            }
        }

        //如果右侧的RecyclerView2在滚动中，但是此时用户又在RecyclerView1中触发滚动事件，则停止所有滚动，等待新一轮滚动。
        if (mRecyclerView2.getScrollState() != RecyclerView.SCROLL_STATE_IDLE) {
            //在滚动中
            if (touchEventInView(mRecyclerView1, event.getX(), event.getY())) {
                mRecyclerView2.stopScroll();
                mRecyclerView1.stopScroll();
                return true;
            }
        }

        return super.dispatchTouchEvent(event);
    }

    /**
     * 坐标(x,y)是否在View内
     */
    private boolean touchEventInView(View view, float x, float y) {
        if (view == null) return false;

        int[] location = new int[2];
        view.getLocationOnScreen(location);

        int left = location[0];
        int top = location[1];

        int right = left + view.getMeasuredWidth();
        int bottom = top + view.getMeasuredHeight();
        return y >= top && y <= bottom && x >= left && x <= right;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        initData();
        initView();
        initListener();
    }

    private void initData() {
        mItems = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            mItems.add(i);
        }
    }

    private void initView() {
        initRecyclerView1();
        initRecyclerView2();
    }

    private void initListener() {
        mRecyclerView1.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (recyclerView.getScrollState() != RecyclerView.SCROLL_STATE_IDLE) {
                    //在滚动中
                    mRecyclerView2.scrollBy(dx, dy);
                }
            }
        });
        mRecyclerView2.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (recyclerView.getScrollState() != RecyclerView.SCROLL_STATE_IDLE) {
                    //在滚动中
                    mRecyclerView1.scrollBy(dx, dy);
                }
            }
        });
    }

    private void initRecyclerView1() {
        mRecyclerView1 = findViewById(R.id.recycler_view_1);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView1.setLayoutManager(layoutManager);

        RecyclerViewAdapter mAdapter = new RecyclerViewAdapter(1);
        mRecyclerView1.setAdapter(mAdapter);
    }

    private void initRecyclerView2() {
        mRecyclerView2 = findViewById(R.id.recycler_view_2);

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(12, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView2.setLayoutManager(layoutManager);

        RecyclerViewAdapter mAdapter = new RecyclerViewAdapter(2);
        mRecyclerView2.setAdapter(mAdapter);
    }

    private class RecyclerViewAdapter extends RecyclerView.Adapter<MyVH> {
        private int id;

        public RecyclerViewAdapter(int id) {
            this.id = id;
        }

        @NonNull
        @Override
        public MyVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.rv_item, parent, false);
            return new MyVH(view);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull MyVH holder, int position) {
            holder.tvId.setText("RecyclerView:" + id);
            holder.tvContent.setText(mItems.get(position) + "");
            switch (id) {
                case 1:
                    holder.tvId.setBackgroundColor(Color.RED);
                    break;
                case 2:
                    holder.tvId.setBackgroundColor(Color.BLUE);
                    break;
            }
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }
    }

    private static class MyVH extends RecyclerView.ViewHolder {
        public TextView tvId;
        public TextView tvContent;

        public MyVH(View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.text1_item);
            tvId.setTextColor(Color.WHITE);
            tvContent = itemView.findViewById(R.id.text2_item);
            tvContent.setTextColor(Color.BLACK);
        }
    }
}

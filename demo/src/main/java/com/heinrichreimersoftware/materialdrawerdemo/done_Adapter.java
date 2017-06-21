package com.heinrichreimersoftware.materialdrawerdemo;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.TextView;

import com.heinrichreimersoftware.materialdrawerdemo.item.done_progress;
import com.heinrichreimersoftware.materialdrawerdemo.RoundedRectProgressBar;
import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class done_Adapter extends RecyclerView.Adapter<done_Adapter.ViewHolder> {
    public String[] name_array ;
    public int name_length;
    public String[][] hw_array;
    private static final int UNSELECTED = -1;
    private RecyclerView recyclerView;
    private int selectedItem = UNSELECTED;

    private Activity activity;
    List<done_progress> items = Collections.emptyList();

        /*public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView mHeadView;
            public TextView mDataView;
            public ViewHolder(View v) {
                super(v);
                RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.list_view);
                mHeadView = (TextView) v.findViewById(R.id.html_head);
                mDataView = (TextView) v.findViewById(R.id.html_content);
            }
        }*/

    //public MyAdapter(List<String> headdata, List<String> datedata, RecyclerView recyclerView) {
    public done_Adapter(Activity activity, List<done_progress> items, RecyclerView recyclerView) {
        //mHeadData = headdata;
        //mDateData = datedata;
        this.activity = activity;
        this.items = items;
        this.recyclerView = recyclerView;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.done_progress, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.expandButton1.setText(items.get(position).getCourse_head());
        holder.bind(position);
        //holder.mHeadView.setText(mHeadData.get(position));
        //holder.mDataView.setText(mDateData.get(position));

    }

    @Override
    public int getItemCount() {
        int gotlength = 0;
        int all_hw_count = 0;
        return items.size();
    }

    public void refreshItem(List<done_progress> newDatas) {
        //这里下拉刷新的数据是加到头部的
        List<done_progress> a1 = Collections.emptyList();
        a1.addAll(newDatas);
        a1.addAll(items);
        items = a1;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ExpandableLayout expandableLayout;
        private TextView expandButton1,expandButton2;
        private TextView hw_content;
        private int position;

        private RoundedRectProgressBar bar;
        private Button btn;
        private int progress;
        private Timer timer;

        private void reset(){
            progress = 0;
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run(){
                    bar.setProgress(progress);
                    progress++;
                    if(progress > 100){
                        timer.cancel();
                    }
                }
            },0 ,30);
        }

        public ViewHolder(View v) {
            super(v);
            /*for(int m = 0; m < name_length; m++){
                hw_array[m] = readInfo("course_hw","course"+m+"/");
            }*/
            expandableLayout = (ExpandableLayout) v.findViewById(R.id.expandable_layout);
            expandableLayout.setInterpolator(new OvershootInterpolator());
            expandButton1 = (TextView) v.findViewById(R.id.html_head_head);
            expandButton2 = (TextView) v.findViewById(R.id.html_content);
            hw_content = (TextView) v.findViewById(R.id.description) ;

            bar = (RoundedRectProgressBar) v.findViewById(R.id.bar);
            btn = (Button) v.findViewById(R.id.btn_progress);
            btn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    reset();
                }
            });

            expandButton1.setOnClickListener(this);
            expandButton2.setOnClickListener(this);


        }

        public void bind(int position) {
            this.position = position;
            //expandButton.setText("TESTING");
            //expandButton1.setText("TESTING");
            //expandButton2.setText("期限﹕ "+position);
            expandButton1.setSelected(false);
            expandButton2.setSelected(false);
            expandableLayout.collapse(false);
        }

        @Override
        public void onClick(View view) {
            ViewHolder holder = (ViewHolder) recyclerView.findViewHolderForAdapterPosition(selectedItem);
            if (holder != null) {
                holder.expandButton1.setSelected(false);
                holder.expandButton2.setSelected(false);
                holder.expandableLayout.collapse();
            }

            if (position == selectedItem) {
                selectedItem = UNSELECTED;
            } else {
                expandButton1.setSelected(true);
                expandButton2.setSelected(true);
                expandableLayout.expand();
                selectedItem = position;
            }
        }
    }
}

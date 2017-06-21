package com.ilmsplus.demo;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import com.ilmsplus.demo.item.Item;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.Collections;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    //private List<String> mHeadData;
    //private List<String> mDateData;
    public String[] name_array ;
    public int name_length;
    public String[][] hw_array;
    private static final int UNSELECTED = -1;
    private RecyclerView recyclerView;
    private int selectedItem = UNSELECTED;

    private Activity activity;
    List<Item> items = Collections.emptyList();

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
    public MyAdapter(Activity activity, List<Item> items, RecyclerView recyclerView) {
        //mHeadData = headdata;
        //mDateData = datedata;
        this.activity = activity;
        this.items = items;
        this.recyclerView = recyclerView;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.carditem, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.expandButton1.setText(items.get(position).getCourse_head());
        holder.expandButton.setText(items.get(position).getHw_head());
        holder.hw_content.setText(items.get(position).getHw_content());
        holder.expandButton2.setText("期限﹕ "+items.get(position).getHw_deadline());
        holder.bind(position);
        //holder.mHeadView.setText(mHeadData.get(position));
        //holder.mDataView.setText(mDateData.get(position));

    }

    @Override
    public int getItemCount() {
        //return mHeadData.size();
        int gotlength = 0;
        int all_hw_count = 0;
        //name_array = readInfo("course_name","");
            /*name_length = name_array.length;
            for(int m = 0; m < name_length; m++){
                hw_array[m] = readInfo("course_hw","course"+m+"/");
                gotlength = hw_array[m].length;
                for (int i = gotlength-1; i>=0; i--){
                    if(hw_array[m][i] != ""){
                        all_hw_count += i+1;
                        break;
                    }
                }
            }*/
        return items.size();
    }

    public void refreshItem(List<Item> newDatas) {
        //这里下拉刷新的数据是加到头部的
        List<Item> a1 = Collections.emptyList();
        a1.addAll(newDatas);
        a1.addAll(items);
        items = a1;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ExpandableLayout expandableLayout;
        private TextView expandButton,expandButton1,expandButton2;
        private TextView hw_content;
        private int position;

        public ViewHolder(View v) {
            super(v);
            /*for(int m = 0; m < name_length; m++){
                hw_array[m] = readInfo("course_hw","course"+m+"/");
            }*/
            expandableLayout = (ExpandableLayout) v.findViewById(R.id.expandable_layout);
            expandableLayout.setInterpolator(new OvershootInterpolator());
            expandButton = (TextView) v.findViewById(R.id.html_head);
            expandButton1 = (TextView) v.findViewById(R.id.html_head_head);
            expandButton2 = (TextView) v.findViewById(R.id.html_content);
            hw_content = (TextView) v.findViewById(R.id.description) ;
            expandButton.setOnClickListener(this);
            expandButton1.setOnClickListener(this);
            expandButton2.setOnClickListener(this);
        }

        public void bind(int position) {
            this.position = position;
            //expandButton.setText("TESTING");
            //expandButton1.setText("TESTING");
            //expandButton2.setText("期限﹕ "+position);
            expandButton.setSelected(false);
            expandButton1.setSelected(false);
            expandButton2.setSelected(false);
            expandableLayout.collapse(false);
        }

        @Override
        public void onClick(View view) {
            ViewHolder holder = (ViewHolder) recyclerView.findViewHolderForAdapterPosition(selectedItem);
            if (holder != null) {
                holder.expandButton.setSelected(false);
                holder.expandButton1.setSelected(false);
                holder.expandButton2.setSelected(false);
                holder.expandableLayout.collapse();
            }

            if (position == selectedItem) {
                selectedItem = UNSELECTED;
            } else {
                expandButton.setSelected(true);
                expandButton1.setSelected(true);
                expandButton2.setSelected(true);
                expandableLayout.expand();
                selectedItem = position;
            }
        }
    }
}

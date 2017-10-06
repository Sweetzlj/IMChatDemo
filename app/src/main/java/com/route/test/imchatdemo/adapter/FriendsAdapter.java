package com.route.test.imchatdemo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.route.test.imchatdemo.R;

import java.util.List;

/**
 * Created by my301s on 2017/9/25.
 */

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.MyViewHolder> implements View.OnClickListener {
    private OnRecyclerClick onRecyclerClick;

    public void setOnRecyclerClick(OnRecyclerClick onRecyclerClick) {
        this.onRecyclerClick = onRecyclerClick;
    }

    private Context context;
    private List<String> list;

    public FriendsAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_frineds, null);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.tv_name.setText(list.get(position));
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        private final TextView tv_name;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
        }
    }
    public interface OnRecyclerClick {
        public void onClick(int position);
    }

    @Override
    public void onClick(View view) {
        Integer position = (Integer) view.getTag();
        if(onRecyclerClick!=null){
            onRecyclerClick.onClick(position);
        }
    }

}

package com.wifidemoSonny;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.wifidemoSonny.bean.WifiListBean;

import java.util.List;

/**
 */
public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MyViewHolder> {
    private List<WifiListBean> wifiListBeanList;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public MainAdapter.OnItemClickListener mOnItemClickListerer;

    public void setmOnItemClickListerer(MainAdapter.OnItemClickListener listerer) {
        this.mOnItemClickListerer = listerer;
    }

    public MainAdapter(List<WifiListBean> wifiListBeanList) {
        this.wifiListBeanList = wifiListBeanList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_main, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.tv_name.setText("wifi name：" + wifiListBeanList.get(position).getName());
        holder.tv_encrypt.setText("encryption type：" + wifiListBeanList.get(position).getEncrypt());
        holder.btn_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListerer.onItemClick(v, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return wifiListBeanList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_name, tv_encrypt;
        Button btn_link;

        public MyViewHolder(View view) {
            super(view);
            tv_name = view.findViewById(R.id.tv_name);
            tv_encrypt = view.findViewById(R.id.tv_encrypt);
            btn_link = view.findViewById(R.id.btn_link);
        }
    }
}
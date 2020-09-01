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
public class WifiListAdapter extends RecyclerView.Adapter<WifiListAdapter.WifiViewHolder> {
    private List<WifiListBean> wifiListBeanList;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public WifiListAdapter.OnItemClickListener mOnItemClickListerer;

    public void setmOnItemClickListerer(WifiListAdapter.OnItemClickListener listerer) {
        this.mOnItemClickListerer = listerer;
    }

    public WifiListAdapter(List<WifiListBean> wifiListBeanList) {
        this.wifiListBeanList = wifiListBeanList;
    }

    @Override
    public WifiViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_main, parent, false);
        WifiViewHolder holder = new WifiViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(WifiViewHolder holder, int position) {
        holder.tv_name.setText("wifi name：" + wifiListBeanList.get(position).getName());
        holder.tv_encrypt.setText("encryption type：" + wifiListBeanList.get(position).getEncrypt());
        final int i = position;
        holder.btn_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListerer.onItemClick(v, i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return wifiListBeanList.size();
    }

    static class WifiViewHolder extends RecyclerView.ViewHolder {

        TextView tv_name, tv_encrypt;
        Button btn_link;

        public WifiViewHolder(View view) {
            super(view);
            tv_name = view.findViewById(R.id.tv_name);
            tv_encrypt = view.findViewById(R.id.tv_encrypt);
            btn_link = view.findViewById(R.id.btn_link);
        }
    }
}

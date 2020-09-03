package com.wifidemoSonny;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wifidemoSonny.bean.WifiListBean;
import com.wifidemoSonny.utils.PermissionsChecker;
import com.wifidemoSonny.utils.WifiManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sgao
 */
public class MainActivity extends AppCompatActivity {

    String[] permsLocation = {"android.permission.ACCESS_WIFI_STATE"
            , "android.permission.CHANGE_WIFI_STATE"
            , "android.permission.ACCESS_COARSE_LOCATION"
            , "android.permission.ACCESS_FINE_LOCATION"};

    RecyclerView recyclerView;
    Button btnGetWifi;
    Button btnCloseWifi;
    Button btnOpenWifi;
    WifiListAdapter adapter;
    private android.net.wifi.WifiManager mWifiManager;
    private List<ScanResult> mScanResultList;
    private List<WifiListBean> wifiListBeanList;
    private Dialog dialog;
    private WifiBroadcastReceiver wifiReceiver;
    private TextView tv_wifiState;

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiverWifi();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (mWifiManager == null) {
            mWifiManager = (android.net.wifi.WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        }
        getPerMission();
        initView();
        initClickListener();
        setAdapter();
    }

    private void registerReceiverWifi() {
        wifiReceiver = new WifiBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(android.net.wifi.WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(android.net.wifi.WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(android.net.wifi.WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(wifiReceiver, filter);
    }

    //setAdapter
    private void setAdapter() {
        adapter = new WifiListAdapter(wifiListBeanList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter.setmOnItemClickListerer(new WifiListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                showCentreDialog(wifiListBeanList.get(position).getName(), position);
            }
        });
    }

    private void getPerMission() {

        PermissionsChecker mPermissionsChecker = new PermissionsChecker(MainActivity.this);
        if (mPermissionsChecker.lacksPermissions(permsLocation)) {
            int RESULT_CODE_LOCATION = 0x001;
            ActivityCompat.requestPermissions(MainActivity.this, permsLocation, RESULT_CODE_LOCATION);
        }
    }

    private void initView() {
        recyclerView = findViewById(R.id.recyclerView);
        btnGetWifi = findViewById(R.id.btnGetWifi);

        btnOpenWifi = findViewById(R.id.btnOpen);
        btnCloseWifi = findViewById(R.id.btnClose);
        tv_wifiState = findViewById(R.id.tv_wifiState);
        wifiListBeanList = new ArrayList<>();
        mScanResultList = new ArrayList<>();
    }

    private void initClickListener() {

        btnOpenWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WifiManager.openWifi(mWifiManager);
            }
        });
        btnCloseWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WifiManager.closeWifi(mWifiManager);
            }
        });

        btnGetWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wifiListBeanList.clear();
                mScanResultList = WifiManager.getWifiList(mWifiManager);
                for (int i = 0; i < mScanResultList.size(); i++) {
                    WifiListBean wifiListBean = new WifiListBean();
                    wifiListBean.setName(mScanResultList.get(i).SSID);
                    wifiListBean.setEncrypt(WifiManager.getEncrypt(mWifiManager, mScanResultList.get(i)));
                    wifiListBeanList.add(wifiListBean);
                }

                if (wifiListBeanList.size() > 0) {
                    adapter.notifyDataSetChanged();
                    Toast.makeText(MainActivity.this, "get wifi list successfully", Toast.LENGTH_SHORT).show();
                } else {
                    adapter.notifyDataSetChanged();
                    Toast.makeText(MainActivity.this, "wifi is null，please check your wifi.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void showCentreDialog(final String wifiName, final int position) {
        View inflate = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_centre, null);
        dialog = new Dialog(MainActivity.this, R.style.DialogCentre);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(inflate);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(wlp);
        dialog.show();
        TextView tvName, tvMargin;
        final EditText et_password;
        tvName = dialog.findViewById(R.id.tvName);
        tvMargin = dialog.findViewById(R.id.tvMargin);
        et_password = dialog.findViewById(R.id.et_password);
        tvName.setText("wifi：" + wifiName);
        tvMargin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WifiManager.disconnectNetwork(mWifiManager);
                String type = WifiManager.getEncrypt(mWifiManager, mScanResultList.get(position));
                WifiManager.connectWifi(mWifiManager, wifiName, et_password.getText().toString(), type);
                dialog.dismiss();
            }
        });
    }



    public class WifiBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (android.net.wifi.WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                int state = intent.getIntExtra(android.net.wifi.WifiManager.EXTRA_WIFI_STATE, 0);
                switch (state) {
                    case android.net.wifi.WifiManager.WIFI_STATE_DISABLED: {

                        tv_wifiState.append("\n status：wifi already closed");
                        break;
                    }
                    case android.net.wifi.WifiManager.WIFI_STATE_DISABLING: {
                        tv_wifiState.append("\n status：wifi is closing");
                        break;
                    }
                    case android.net.wifi.WifiManager.WIFI_STATE_ENABLED: {
                        tv_wifiState.append("\n status：wifi already opened");
                        break;
                    }
                    case android.net.wifi.WifiManager.WIFI_STATE_ENABLING: {
                        tv_wifiState.append("\n status：wifi is opening");
                        break;
                    }
                    case android.net.wifi.WifiManager.WIFI_STATE_UNKNOWN: {

                        tv_wifiState.append("\n status：wifi unknown status");
                        break;
                    }
                    default:
                        break;
                }
            } else if (android.net.wifi.WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {

//                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//                final Network network = connectivityManager.getActiveNetwork();
//                final NetworkCapabilities nc = connectivityManager.getNetworkCapabilities(network);
//                nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);

                NetworkInfo info = intent.getParcelableExtra(android.net.wifi.WifiManager.EXTRA_NETWORK_INFO);if (NetworkInfo.State.DISCONNECTED == info.getState()) {//wifi没连接上
                    tv_wifiState.append("\n connection：wifi not connected");
                } else if (NetworkInfo.State.CONNECTED == info.getState()) {
                    tv_wifiState.append("\n connection：wifi already connected，wifi name：" + WifiManager.getWiFiName(mWifiManager));
                } else if (NetworkInfo.State.CONNECTING == info.getState()){
                    tv_wifiState.append("\n connection：wifi is connecting");
                }
            } else if (android.net.wifi.WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(intent.getAction())) {
                Log.e("=====", "wifi list changing");
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(wifiReceiver);
    }
}

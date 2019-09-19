package player.rongyun.com.playsdk.PushScreen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.rongyun.lib_screen.entity.ClingDevice;
import com.rongyun.lib_screen.event.DeviceEvent;
import com.rongyun.lib_screen.listener.ItemClickListener;

import player.rongyun.com.playsdk.Living.MainActivity;
import player.rongyun.com.playsdk.R;


public class DeviceListActivity extends AppCompatActivity implements PushManager.DeviceCallback {

    RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private ClingDeviceAdapter adapter;
    private Button mConnectBtn;
    private String mOrigin;
    private String m720;
    private String m360;
    private String m2K;
    private String m4K;
    private String mHDR;
    private int mDuration;
    String url1 = "http://hc.yinyuetai" +
            ".com/uploads/videos/common/44E4016521C693F23F7E9344AEBF5AF0.mp4?sc=5c4d956adf76a722" +
            "&br=781&vid=3266995&aid=35&area=ML&vst=0";

    public static void startSelf(Activity context) {
        Intent intent = new Intent(context, DeviceListActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_device_list);
        mOrigin = getIntent().getStringExtra("orig");
        m720 = getIntent().getStringExtra("720");
        m360 = getIntent().getStringExtra("360");
        m2K = getIntent().getStringExtra("2k");
        m4K = getIntent().getStringExtra("4k");
        mHDR = getIntent().getStringExtra("HDR");
        mDuration = getIntent().getIntExtra("duration",0);
        recyclerView = findViewById(R.id.recycler_view);
        PushManager.getInstance().startService();
        layoutManager = new LinearLayoutManager(this);
        adapter = new ClingDeviceAdapter(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        adapter.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemAction(int action, Object object) {
                ClingDevice device = (ClingDevice) object;
                PushManager.getInstance().selectClingDevice(device);
                Toast.makeText(getBaseContext(), "选择了设备 " + device.getDevice().getDetails()
                        .getFriendlyName(), Toast.LENGTH_LONG).show();
                refresh();
            }
        });
        mConnectBtn = findViewById(R.id.connect_btn);
        if (mConnectBtn != null) {
            mConnectBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(DeviceListActivity.this, PushScreenLandActivity.class);
                    intent.putExtra("4k", m4K);
                    intent.putExtra("2k", m2K);
                    intent.putExtra("720", m720);
                    intent.putExtra("360", m360);
                    intent.putExtra("duration",mDuration);
                    startActivity(intent);
                }
            });
        }
    }

    public void refresh() {
        if (adapter == null) {
            adapter = new ClingDeviceAdapter(this);
            recyclerView.setAdapter(adapter);
        }
        adapter.refresh();
    }
    @Override
    public void onDeviceEvent(DeviceEvent event) {
        refresh();
    }

    @Override
    public void onStart() {
        super.onStart();
        PushManager.getInstance().setDeviceCallback(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        PushManager.getInstance().removeDeviceCallback(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        PushManager.getInstance().stopService();
    }
}

package com.example.demo01;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;


public class SignalActivity extends AppCompatActivity {//센서 이벤트를 가져올 class
    private final String TAG = SignalActivity.class.getSimpleName();
    private static final UUID BT_MODULE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private LineChart lineChart;
    public LineData data;
    private Thread chartThread;
    private List<Integer> mainList;
    private List<Integer> intList;

    private static final int REQUEST_ENABLE_BT = 1;
    public final static int MESSAGE_READ = 2;
    public final static int MESSAGE_WRITE = 0;
    private final static int CONNECTING_STATUS = 3;

    private TextView viewBluetoothStatus;
    private ListView viewDeviceList;
    private BluetoothAdapter bluetoothAdapter;
    private ArrayAdapter<String> bluetoothArray;
    private Set<BluetoothDevice> pairedDevices;
    private Handler handler;

    private ConnectedThread connectedThread;
    private BluetoothSocket bluetoothSocket = null;

    public boolean flag = true;
    public int i = 0;
    public int j = 0;
    public int graphReady = 0;
    public String aaa = "";
    public int times = 0;
    public DataDao dao = new DataDao();
    public double bpm = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signal);

        viewBluetoothStatus = (TextView) findViewById(R.id.viewBluetoothStatus);

        Switch switchButton = findViewById(R.id.bt_switch);

        TextView setBpm = (TextView) findViewById(R.id.bpm);

        Button searchBT = (Button)findViewById(R.id.search);

        Button btnData = (Button) findViewById(R.id.play_btn);

        bluetoothArray = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        viewDeviceList = (ListView) findViewById(R.id.viewDeviceList);
        viewDeviceList.setAdapter(bluetoothArray);
        viewDeviceList.setOnItemClickListener(bluetoothDeviceClickListener);

        lineChart = (LineChart) findViewById(R.id.ecgChart);
        lineChart.setNoDataText("No chart data");
        lineChart.setHighlightPerDragEnabled(true);
        lineChart.setTouchEnabled(true);
        lineChart.setDrawGridBackground(true);
        lineChart.setScaleEnabled(true);
        lineChart.setDrawGridBackground(true);
        lineChart.setPinchZoom(true);
        lineChart.setBackgroundColor(Color.WHITE);

        data = new LineData();
        lineChart.setData(data);


        Legend l =lineChart.getLegend();
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.WHITE);

        XAxis x = lineChart.getXAxis();
        x.setTextColor(Color.WHITE);
        x.setDrawGridLines(false);
        x.setAvoidFirstLastClipping(true);

        YAxis yl = lineChart.getAxisLeft();
        yl.setTextColor(Color.WHITE);
        yl.setAxisMaximum(500f);
        yl.setDrawGridLines(true);
        YAxis yr = lineChart.getAxisRight();
        yr.setEnabled(false);

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setMessage("Need location permission");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                    }
                });
                builder.show();
            }
        }

        if (bluetoothAdapter == null) {
            viewBluetoothStatus.setText(getString(R.string.sBTaNF));
            Toast.makeText(getApplicationContext(), getString(R.string.sBTdevNF), Toast.LENGTH_SHORT).show();
        } else {
            viewBluetoothStatus.setText("Bluetooth Available");

            switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if (isChecked) {
                        // switchButton이 체크된 경우
                        bluetoothOn();
                    } else {
                        // switchButton이 체크되지 않은 경우
                        bluetoothOff();
                    }
                }
            });

            searchBT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bluetoothDiscover();
                    bluetoothPairing();
                }
            });
        }

        handler = new Handler(Looper.getMainLooper()) {
            @RequiresApi(api = Build.VERSION_CODES.N)
            public void handleMessage(Message msg) {
                if (msg.what == MESSAGE_READ) {
                    String readMessage = null;
                    readMessage = new String((byte[]) msg.obj, 0, msg.arg1, StandardCharsets.UTF_8);
                    Log.d("qqqq", readMessage);
                    String s = String.valueOf(readMessage.charAt(readMessage.length() - 3)); //추가 마지막 string이 '.'인거 가져오려고
                    Log.d("qqqq", s);

                    if (s.equals(".")) { //추가 여긴 다 받아오면 알려주는 거
                        readMessage = readMessage.substring(0, readMessage.length() - 3); //추가 '.'은 버려야하기 때문에
                        graphReady = 1;

                        Intent intent = new Intent(SignalActivity.this, PopupActivity.class);
                        intent.putExtra("data", "Ready to draw graph!");
                        startActivityForResult(intent, 1);
                    }else{ }
                    aaa += readMessage;
                }

                if (msg.what == CONNECTING_STATUS) {
                    char[] sConnected;
                    if (msg.arg1 == 1)
                        viewBluetoothStatus.setText("Connected to device " + msg.obj);
                    else
                        viewBluetoothStatus.setText("Connection Failed");
                }
            }
        };
        btnData.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                // 데이터 받아오기
                if (times == 0) {
                    Toast.makeText(getApplicationContext(), "Start to receive data", Toast.LENGTH_SHORT).show();

                    flag = true;
                    times = 1;

                    btnData.setBackgroundResource(R.drawable.graph);
                    btnData.setText("Graph");
                    aaa = "";
                    Log.d("again", "again");
                    connectedThread.write(1);
                } else if (times == 1 && graphReady == 1) { // 그래프 그리기
                    times = 0;
                    btnData.setBackgroundResource(R.drawable.send);
                    btnData.setText("RECEIVE");
                    graphReady = 0;

                    setBpm.setText(""+bpm);

                    Log.d("aaa", aaa);
                    System.out.println(aaa);
                    String id = getIntent().getStringExtra("Email");
                    Data senddata = new Data(aaa,bpm,"hello");
                    dao.add(senddata,id);
                    dd(aaa);
                } else if (times == 1 && graphReady == 0) {
                    Toast.makeText(getApplicationContext(), "Sending data. Please Hold.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void dd(String b){
        flag = true;
        String[] split = b.split(String.valueOf(','));
        Integer[] intInput = new Integer[split.length];
        for(int i = 0; i < split.length - 1; i++){

            try {

                intInput[i] = Integer.parseInt(split[i]);
                Log.d("ddddddddd:2", Integer.toString(i) + "  "+Integer.toString(intInput[i]));

            }catch(NumberFormatException e){
                Log.d("dddddddd:", "eororor");
            }
        }
        intList = Arrays.asList(intInput);
        bpm = parseDataForHB(intList);
        i = 0;
        feedMultile();

    }

    ///Graph
    private void feedMultile() {
        if (chartThread != null) chartThread.interrupt();

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                addEntry();
            }
        };

        chartThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (flag) {
                    runOnUiThread(runnable);

                    try {
                        chartThread.sleep(50);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        chartThread.start();
    }

    private void addEntry() {
        i++;
        j++; //추가
        data = lineChart.getData();
        if (data != null) {
            ILineDataSet lineDataSet = data.getDataSetByIndex(0);

            if (lineDataSet == null) {
                lineDataSet = createSet();
                data.addDataSet(lineDataSet);
            }

            try {
                data.addEntry(new Entry((float) j/10, intList.get(i)), 0); //추가
            } catch (NullPointerException e) {
            } catch (ArrayIndexOutOfBoundsException e1) {
            }
            data.notifyDataChanged();
            lineChart.notifyDataSetChanged();
            lineChart.setVisibleXRangeMaximum(14);
            if (i < intList.size()) {
                lineChart.moveViewToX(data.getEntryCount());
            } else {
                flag = false;
            }
        }
    }


    private ILineDataSet createSet() {
        LineDataSet set = new LineDataSet(null, "Dynamic Data");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.BLACK);
        xAxis.enableGridDashedLine(8, 24, 0);

        YAxis yLAxis = lineChart.getAxisLeft();
        yLAxis.setTextColor(Color.BLACK);

        YAxis yRAxis = lineChart.getAxisRight();
        yRAxis.setDrawLabels(false);
        yRAxis.setDrawAxisLine(false);
        yRAxis.setDrawGridLines(false);

        Description description = new Description();
        description.setText("");

        lineChart.setDoubleTapToZoomEnabled(true);
        lineChart.setDrawGridBackground(true);
        lineChart.setDescription(description);
        lineChart.invalidate();

        set.setColor(ColorTemplate.getHoloBlue());
        set.setCircleColor(ColorTemplate.getHoloBlue());
        set.setLineWidth(1f);
        set.setFillAlpha(65);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(Color.rgb(244, 117, 177));
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(9f);
        set.setDrawValues(false);
        set.setDrawCircles(false);
        return set;
    }
    public float parseDataForHB(List<Integer> ecgValues){
        int max1 =0;
        int max2 = 0;
        for(int i = 0; i < 50; i++){
            if(ecgValues.get(i) > max1){
                max1 = i;
            }
        }
        for(int i = 50; i < 100; i++){
            if(ecgValues.get(i) > max2){
                max2 = i;
            }
        }

        int index = max2 - max1;
        float beat = (float) (60 / (index * 0.02));
        Log.d("wppwpwpw", ""+beat);
        return beat;
    }
    ///Bluetooth
    ///bluetoothOn
    private void bluetoothOn() {
        if (!bluetoothAdapter.isEnabled()) {
            Intent bluetoorhIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                // You can use the API that requires the permission.
            } else {
                // You can directly ask for the permission.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions( new String[] { Manifest.permission.BLUETOOTH_CONNECT }, 1);
                }
            }
            startActivityForResult(bluetoorhIntent, REQUEST_ENABLE_BT);
            viewBluetoothStatus.setText("Bluetooth is enabled");
            Toast.makeText(getApplicationContext(), "Bluetooth is available now", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Bluetooth on", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                }
                return;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent Data) {
        // Check which request we're responding to
        super.onActivityResult(requestCode, resultCode, Data);
        if (requestCode == REQUEST_ENABLE_BT) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                viewBluetoothStatus.setText("Enabled");
            } else
                viewBluetoothStatus.setText("Disabled");
        }
    }


    ///bluetoothOff
    private void bluetoothOff() {


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
        }
        bluetoothAdapter.disable();
        viewBluetoothStatus.setText("Bluetooth off");
        Toast.makeText(getApplicationContext(), "Bluetooth off", Toast.LENGTH_SHORT).show();
    }

    ///bluetoothPairing
    private void bluetoothPairing() {
        bluetoothArray.clear();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            //return;
        }
        pairedDevices = bluetoothAdapter.getBondedDevices();
        if (bluetoothAdapter.isEnabled()) {
            for (BluetoothDevice device : pairedDevices)
                bluetoothArray.add(device.getName() + "\n" + device.getAddress());
            Toast.makeText(getApplicationContext(), "Show paired devices", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Bluetooth is not on", Toast.LENGTH_SHORT).show();
        }
    }

    ///bluetoothDiscover
    private void bluetoothDiscover() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {
            // You can use the API that requires the permission.
        } else {
            // You can directly ask for the permission.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions( new String[] { Manifest.permission.BLUETOOTH_SCAN }, 1);
            }
        }
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
            Toast.makeText(getApplicationContext(), "Discovering is stop", Toast.LENGTH_SHORT).show();
        } else {
            if (bluetoothAdapter.isEnabled()) {
                bluetoothArray.clear();
                bluetoothAdapter.startDiscovery();
                Toast.makeText(getApplicationContext(), "Discovery started", Toast.LENGTH_SHORT).show();
                registerReceiver(bluetoothReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
            } else {
                Toast.makeText(getApplicationContext(), "Bluetooth is not on", Toast.LENGTH_SHORT).show();
            }
        }
    }

    final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // add the name to the list
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(SignalActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                }
                bluetoothArray.add(device.getName() + "\n" + device.getAddress());
                bluetoothArray.notifyDataSetChanged();
            }
        }
    };

    private AdapterView.OnItemClickListener bluetoothDeviceClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            if (!bluetoothAdapter.isEnabled()) {
                Toast.makeText(getBaseContext(), "Bluetooth is not on", Toast.LENGTH_SHORT).show();
                //return;
            }

            viewBluetoothStatus.setText("Connecting…");
            String info = ((TextView) view).getText().toString();
            final String address = info.substring(info.length() - 17);
            final String name = info.substring(0, info.length() - 17);

            new Thread() {
                @Override
                public void run() {
                    super.run();
                    boolean fail = false;

                    BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);

                    try {
                        bluetoothSocket = createBluetoothSocket(device);
                    } catch (IOException e) {
                        fail = true;
                        Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
                    }

                    try {
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                            //return;
                        }
                        bluetoothSocket.connect();

                    } catch (IOException e) {
                        try {
                            fail = true;
                            bluetoothSocket.close();
                            handler.obtainMessage(CONNECTING_STATUS, -1, -1).sendToTarget();
                        } catch (IOException ex) {
                            Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                    if (!fail) {
                        connectedThread = new ConnectedThread(bluetoothSocket, handler);
                        connectedThread.start();

                        handler.obtainMessage(CONNECTING_STATUS, 1, -1, name).sendToTarget();
                    }

                }


            }.start();
        }
    };

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        try {
            final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", UUID.class);
            return (BluetoothSocket) m.invoke(device, BT_MODULE_UUID);
        } catch (NoSuchMethodException e) {
            Log.e(TAG, "Could not create Insecure RFComm Connection", e);
        } catch (InvocationTargetException e) {
            Log.e(TAG, "Could not create Insecure RFComm Connection", e);
        } catch (IllegalAccessException e) {
            Log.e(TAG, "Could not create Insecure RFComm Connection", e);
        }
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) getApplicationContext(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
        return device.createInsecureRfcommSocketToServiceRecord(BT_MODULE_UUID);
    }
}
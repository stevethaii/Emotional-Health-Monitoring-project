package com.example.dinhthai.sendingandreceivingdata;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dinhthai.sendingandreceivingdata.bluetooth.Bluetooth;
import com.example.dinhthai.sendingandreceivingdata.bluetooth.utils.ThreadHelper;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Chat extends AppCompatActivity implements com.example.dinhthai.sendingandreceivingdata.bluetooth.interfaces.DeviceCallback {
    private String name;
    private Bluetooth b;
    private EditText message;
    private Button go;
    private Button stopBtn;
    private TextView text;
    private TextView tvHR;
    private TextView tvSC;
    private TextView tvBT;
    private TextView tvTime;
    private DatabaseHelper myDB;
    public boolean isClickedFirstTime = true;
    Button getDataTableButton;
    boolean isInserted = false;
    private ScrollView scrollView;
    private boolean registered=false;
    RelativeLayout HRrelativeLayout;
    RelativeLayout BTrelativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        HRrelativeLayout = (RelativeLayout) findViewById(R.id.ringHR);
        BTrelativeLayout = (RelativeLayout) findViewById(R.id.ringBT);

        //ViewCompat.setBackgroundTintList(relativeLayout, ColorStateList.valueOf(Color.parseColor("#D81B60")));
        myDB = new DatabaseHelper(this);
        text = (TextView)findViewById(R.id.text);
        tvTime = (TextView) findViewById(R.id.textViewTime);
        tvHR = (TextView) findViewById(R.id.textViewHR);
        tvBT = (TextView) findViewById(R.id.textViewBT);
        tvSC = (TextView) findViewById(R.id.textViewSC);
        getDataTableButton = (Button) findViewById(R.id.viewButton);


        message = (EditText)findViewById(R.id.message);
        go = (Button)findViewById(R.id.Go);
        stopBtn = (Button) findViewById(R.id.Stop) ;
        stopBtn.setVisibility(View.INVISIBLE);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        text.setMovementMethod(new ScrollingMovementMethod());
        go.setEnabled(true);

        Resources res = getResources();
        final  Drawable drawable = res.getDrawable(R.drawable.circular_shape);
        final ProgressBar mProgress = (ProgressBar) findViewById(R.id.circularProgressbar);
        b = new Bluetooth(this);
        b.enable();
        b.setDeviceCallback(this);

        int pos;
        try{
            Bundle bundle = getIntent().getExtras();
            pos = bundle.getInt("pos");
            //List<BluetoothDevice> pairedDeviceList = b.getPairedDevices();
            name = b.getPairedDevices().get(pos).getName();
            Display("Connecting...");
            b.connectToDevice(b.getPairedDevices().get(pos));
        }   catch (Exception ex)
        {
            pos = -1;
        }

        final CountDownTimer countDownTimer = new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                go.setVisibility(View.INVISIBLE);
                int pStatus = mProgress.getProgress();
                if (pStatus >= mProgress.getMax()) {
                    pStatus = 0;
                }
                mProgress.setProgress(pStatus + 1);
                tvTime.setText(pStatus + "s");
//
            }

            @Override
            public void onFinish() {

                //Toast.makeText(Chat.this, "done.", Toast.LENGTH_SHORT).show();
                stopBtn.setVisibility(View.VISIBLE);
                this.start();

            }
        };
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mProgress.setProgress(0);   // Main Progress
                mProgress.setSecondaryProgress(10); // Secondary Progress
                mProgress.setMax(10); // Maximum Progress
                mProgress.setProgressDrawable(drawable);
                b.startDisplay();
                countDownTimer.start();
//                String msg = message.getText().toString();
//                message.setText("");
//                b.send(msg);
//                Display("You: "+msg);
                    // }
                }
        });
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b.stopDisplay();
                tvBT.setText("");
                tvHR.setText("");
                tvSC.setText("");
                tvTime.setText("");
                go.setVisibility(View.VISIBLE);
                countDownTimer.cancel();

            }
        });
        getDataTableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewAllData();
            }
        });


        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);
        registered=true;
    }
    public void setProgressBar(final ProgressBar mProgress,Drawable drawable, final Bluetooth b)
    {

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(registered) {
            unregisterReceiver(mReceiver);
            registered=false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.close:
                b.removeDeviceCallback();
                b.disconnect();
                Intent intent = new Intent(this, Select.class);
                startActivity(intent);
                finish();
                return true;

            case R.id.rate:
                Uri uri = Uri.parse("market://details?id=" + this.getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + this.getPackageName())));
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void HRComparation(final String s) {
        final int length = s.length();
        final char[] c = new char[length - 3];
        s.getChars(3, length, c, 0);

        //final String ss = String.valueOf(c);
        final Float number = Float.parseFloat(String.valueOf(c));
        if (number < 60.00 || number > 100.00) {
            ChangeBackgroundColor(HRrelativeLayout,tvHR);
        }
        else
        {
            ViewCompat.setBackgroundTintList(HRrelativeLayout, ColorStateList.valueOf(Color.parseColor("#36A7A7")));
            tvHR.setTextColor(Color.parseColor("#101010"));
        }
    }
    public void BTComparation(final String s) {
        final int length = s.length();
        final char[] c = new char[length - 3];
        s.getChars(3, length, c, 0);

        //final String ss = String.valueOf(c);
        final Float number = Float.parseFloat(String.valueOf(c));
        if (number < 37.2 || number > 36.1) {
            ChangeBackgroundColor(BTrelativeLayout,tvBT);
        }
        else
        {
            ViewCompat.setBackgroundTintList(BTrelativeLayout, ColorStateList.valueOf(Color.parseColor("#36A7A7")));
            tvBT.setTextColor(Color.parseColor("#101010"));
        }
    }
    public void ChangeBackgroundColor(final RelativeLayout layout, final TextView tv)
    {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final Handler handler = new Handler();
                String colorArray[] = {"#F44336","#ffffff"};
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        Random i = new Random();
//                        int count = (int) i.nextInt(2-1) + 1;
                        ViewCompat.setBackgroundTintList(layout, ColorStateList.valueOf(Color.parseColor("#F44336")));
                        tv.setTextColor(Color.parseColor("#F44336"));
//                        try {
//                            Thread.sleep(500);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        ViewCompat.setBackgroundTintList(layout, ColorStateList.valueOf(Color.parseColor("#ffffff")));

//                        if(count ==0) {
////                            count++;
////                        }
////                        else if(count == 1)
////                        {
////                            ViewCompat.setBackgroundTintList(layout, ColorStateList.valueOf(Color.parseColor("#ffffff")));
////                        }
//                        handler.postDelayed(this,1000);
//                    }
//                }, 1000);
            }
        });
    }
    public void UpdateParameters(final String s)
    {
        final char c = s.charAt(0);
        final String ss = String.valueOf(c);
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Find each type of measurement by the first character
                //Compare to a constant value to print out
                switch(ss)
                {
                    case "S":
                        DisplayValue(tvSC,s);
                        break;
                    case "B":
                        DisplayValue(tvBT,s);
                        BTComparation(s);
                        break;
                    case "H":
                        DisplayValue(tvHR,s);
                        HRComparation(s);
                        break;
                }
            }
        });

    }
    public void DisplayValue(TextView tv, final String s){
        tv.setText(s);
    }
    public void Display(final String s){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                text.append(s + "\n");
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }


    @Override
    public void onDeviceConnected(BluetoothDevice device) {
        Display("Connected to "+device.getName()+" - "+device.getAddress());
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                go.setEnabled(true);
            }
        });
    }

    @Override
    public void onDeviceDisconnected(BluetoothDevice device, String message) {
        Display("Disconnected!");
        Display("Connecting again...");
        b.connectToDevice(device);
    }

    @Override
    public void onMessage(byte[] message) {
        String str = new String(message);
        UpdateParameters( str);
    }

    @Override
    public void onError(int errorCode) {
        Display("Error: "+message);
    }


    @Override
    public void onConnectError(final BluetoothDevice device, String message) {
        Display("Error: "+message);
        Display("Trying again in 3 sec.");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        b.connectToDevice(device);
                    }
                }, 2000);
            }
        });
    }
    @Override
    public void updateDatabase() {
        try {
            isInserted = myDB.insertData(tvSC.getText().toString(), tvHR.getText().toString(), tvBT.getText().toString());
        } catch (Exception e) {
            e.getMessage();
        }
        if (isInserted = true)
            showToast("Data inserted");
        else
            showToast("Data not inserted)");
    }
    public void showToast(final String str)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(Chat.this,str,Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void ViewAllData()
    {
                Cursor res = myDB.GetDataFromTable();
                if(res.getCount() == 0) {
                    showMessage("Message Error","Error !! No data found !!");
                    return;
                }
                StringBuffer buffer = new StringBuffer();
                while(res.moveToNext()){
                    buffer.append("Id: " + res.getString(0) + "\n");
//                    buffer.append("Name: " + res.getString(1) + "\n");
//                    buffer.append("Age: " + res.getString(2) + "\n");
                    buffer.append("Skin conductance: " + res.getString(1) + "\n");
                    buffer.append("Heart-rate: " + res.getString(2) + "\n");
                    buffer.append("Body Temperature: " + res.getString(3) + "\n");
                    buffer.append("Date: " + res.getString(4) + "\n");
                }
                showMessage("Show database",buffer.toString());
            }
    public void showMessage(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                Intent intent1 = new Intent(Chat.this, Select.class);


                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        if(registered) {
                            unregisterReceiver(mReceiver);
                            registered=false;
                        }
                        startActivity(intent1);
                        finish();
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        if(registered) {
                            unregisterReceiver(mReceiver);
                            registered=false;
                        }
                        startActivity(intent1);
                        finish();
                        break;
                }
            }
        }
    };
}
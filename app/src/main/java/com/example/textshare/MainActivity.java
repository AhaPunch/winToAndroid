package com.example.textshare;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private TextView textView;
    private ServerSocket serverSocket;
    private static final int PORT = 8888;
    private boolean isServerRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        textView = findViewById(R.id.textView);
        Button copyButton = findViewById(R.id.copyButton);
        TextView ipAddressText = findViewById(R.id.ipAddressText);
        
        // 显示本机IP��址
        String ipAddress = getLocalIpAddress();
        ipAddressText.setText("IP地址: " + ipAddress + "\n端口: " + PORT);
        
        // 启动网络服务器
        startServer();
        
        // 复制按钮点击事件
        copyButton.setOnClickListener(v -> {
            String text = textView.getText().toString();
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("shared_text", text);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "已复制到剪贴板", Toast.LENGTH_SHORT).show();
        });
    }
    
    private void startServer() {
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(PORT);
                isServerRunning = true;
                while (isServerRunning) {
                    Socket socket = serverSocket.accept();
                    handleClientConnection(socket);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    private void handleClientConnection(Socket socket) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String text = reader.readLine();
            if (text != null) {
                runOnUiThread(() -> textView.setText(text));
            }
            reader.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private String getLocalIpAddress() {
        try {
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipInt = wifiInfo.getIpAddress();
            return String.format(Locale.getDefault(), "%d.%d.%d.%d",
                    (ipInt & 0xff), (ipInt >> 8 & 0xff),
                    (ipInt >> 16 & 0xff), (ipInt >> 24 & 0xff));
        } catch (Exception e) {
            return "未连接到WiFi";
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        isServerRunning = false;
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
} 
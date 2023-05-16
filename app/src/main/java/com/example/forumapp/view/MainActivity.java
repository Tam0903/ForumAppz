package com.example.forumapp.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;

import com.example.forumapp.R;

public class MainActivity extends AppCompatActivity {
    private static final int SPLASH_SCREEN_DELAY = 2000; // độ trễ của splash screen, tính bằng milliseconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Đặt layout cho màn hình splash
        setContentView(R.layout.activity_splash);

        // Tạo một handler để chuyển đến giao diện đăng nhập sau khi độ trễ của splash screen
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Chuyển đến giao diện đăng nhập
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_SCREEN_DELAY);
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Thực hiện công việc chuyển đổi từ logo sang giao diện đăng nhập ở đây
                // Có thể là tải dữ liệu, xử lý dữ liệu, hiển thị hoạt ảnh, chuyển đổi giao diện, ...
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Cập nhật giao diện người dùng tại đây
                    }
                });
            }
        }).start();
    }
}
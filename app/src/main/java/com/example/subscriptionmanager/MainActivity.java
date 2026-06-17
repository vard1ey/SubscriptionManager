package com.example.subscriptionmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.subscriptionmanager.database.DatabaseHelper;
import com.example.subscriptionmanager.models.Subscription;
import com.example.subscriptionmanager.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TextView tvWelcome;
    private ListView lvSubscriptions;
    private Button btnAddSubscription, btnLogout;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private List<Subscription> subscriptionList;
    private SubscriptionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        if (!sessionManager.isLoggedIn()) {
            navigateToLogin();
            return;
        }

        initViews();
        setupListeners();
        loadSubscriptions();
    }

    private void initViews() {
        tvWelcome = findViewById(R.id.tvWelcome);
        lvSubscriptions = findViewById(R.id.lvSubscriptions);
        btnAddSubscription = findViewById(R.id.btnAddSubscription);
        btnLogout = findViewById(R.id.btnLogout);

        tvWelcome.setText("Добро пожаловать, " + sessionManager.getUsername() + "!");
        subscriptionList = new ArrayList<>();
        adapter = new SubscriptionAdapter(this, subscriptionList);
        lvSubscriptions.setAdapter(adapter);
    }

    private void setupListeners() {
        btnAddSubscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SubscriptionActivity.class));
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionManager.logout();
                navigateToLogin();
            }
        });
    }

    private void loadSubscriptions() {
        int userId = sessionManager.getUserId();
        subscriptionList.clear();
        subscriptionList.addAll(dbHelper.getSubscriptions(userId));
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSubscriptions();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
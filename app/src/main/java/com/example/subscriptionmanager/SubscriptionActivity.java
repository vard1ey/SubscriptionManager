package com.example.subscriptionmanager;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.subscriptionmanager.database.DatabaseHelper;
import com.example.subscriptionmanager.models.Subscription;
import com.example.subscriptionmanager.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SubscriptionActivity extends AppCompatActivity {
    private EditText etName, etPrice, etCategory, etStartDate, etEndDate;
    private Button btnSave, btnCancel;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private Calendar startCalendar = Calendar.getInstance();
    private Calendar endCalendar = Calendar.getInstance();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        initViews();
        setupListeners();
        setDefaultDates();
    }

    private void initViews() {
        etName = findViewById(R.id.etName);
        etPrice = findViewById(R.id.etPrice);
        etCategory = findViewById(R.id.etCategory);
        etStartDate = findViewById(R.id.etStartDate);
        etEndDate = findViewById(R.id.etEndDate);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
    }

    private void setupListeners() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSubscription();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        etStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(true);
            }
        });

        etEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(false);
            }
        });
    }

    private void setDefaultDates() {
        etStartDate.setText(dateFormat.format(startCalendar.getTime()));

        endCalendar.add(Calendar.MONTH, 1);
        etEndDate.setText(dateFormat.format(endCalendar.getTime()));
    }

    private void showDatePickerDialog(final boolean isStartDate) {
        final Calendar calendar = isStartDate ? startCalendar : endCalendar;

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(year, month, dayOfMonth);

                        if (isStartDate) {
                            startCalendar.set(year, month, dayOfMonth);
                            etStartDate.setText(dateFormat.format(startCalendar.getTime()));

                            // Если дата окончания раньше даты начала, обновляем
                            if (endCalendar.before(startCalendar)) {
                                endCalendar.set(year, month, dayOfMonth);
                                endCalendar.add(Calendar.MONTH, 1);
                                etEndDate.setText(dateFormat.format(endCalendar.getTime()));
                            }
                        } else {
                            if (selectedDate.before(startCalendar)) {
                                Toast.makeText(SubscriptionActivity.this,
                                        "Дата окончания не может быть раньше даты начала",
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }
                            endCalendar.set(year, month, dayOfMonth);
                            etEndDate.setText(dateFormat.format(endCalendar.getTime()));
                        }
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    private void saveSubscription() {
        String name = etName.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String category = etCategory.getText().toString().trim();
        String startDate = etStartDate.getText().toString().trim();
        String endDate = etEndDate.getText().toString().trim();

        if (name.isEmpty() || priceStr.isEmpty() || category.isEmpty() ||
                startDate.isEmpty() || endDate.isEmpty()) {
            Toast.makeText(this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Введите корректную цену", Toast.LENGTH_SHORT).show();
            return;
        }

        int userId = sessionManager.getUserId();
        Subscription subscription = new Subscription(name, price, category, startDate, endDate, userId);

        if (dbHelper.addSubscription(subscription)) {
            Toast.makeText(this, "Подписка добавлена!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Ошибка при добавлении подписки", Toast.LENGTH_SHORT).show();
        }
    }
}
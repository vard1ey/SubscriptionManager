package com.example.subscriptionmanager;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.subscriptionmanager.database.DatabaseHelper;
import com.example.subscriptionmanager.models.User;

import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    private EditText etUsername, etPassword, etConfirmPassword;
    private Button btnRegister, btnBack;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new DatabaseHelper(this);
        initViews();
        setupListeners();
    }

    private void initViews() {
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnBack = findViewById(R.id.btnBack);
    }

    private void setupListeners() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private boolean validateUsername(String username) {
        // Проверка: только буквы и цифры, не начинается с цифры
        if (username.isEmpty()) {
            Toast.makeText(this, "Введите имя пользователя", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (Character.isDigit(username.charAt(0))) {
            Toast.makeText(this, "Имя пользователя не может начинаться с цифры", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!Pattern.matches("^[a-zA-Zа-яА-Я0-9_]+$", username)) {
            Toast.makeText(this, "Имя пользователя может содержать только буквы, цифры и _", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (username.length() < 3) {
            Toast.makeText(this, "Имя пользователя должно содержать минимум 3 символа", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean validatePassword(String password) {
        if (password.isEmpty()) {
            Toast.makeText(this, "Введите пароль", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Пароль должен содержать минимум 6 символов", Toast.LENGTH_SHORT).show();
            return false;
        }

        char firstChar = password.charAt(0);
        if (!Character.isLetterOrDigit(firstChar)) {
            Toast.makeText(this, "Пароль не может начинаться со спец. символа", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Проверка на наличие хотя бы одной цифры и одной буквы
        boolean hasDigit = false;
        boolean hasLetter = false;
        for (char c : password.toCharArray()) {
            if (Character.isDigit(c)) hasDigit = true;
            if (Character.isLetter(c)) hasLetter = true;
        }

        if (!hasDigit || !hasLetter) {
            Toast.makeText(this, "Пароль должен содержать хотя бы одну букву и одну цифру", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void registerUser() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (!validateUsername(username)) return;
        if (!validatePassword(password)) return;

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);

        if (dbHelper.registerUser(user)) {
            Toast.makeText(this, "Регистрация успешна!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Пользователь с таким именем уже существует", Toast.LENGTH_SHORT).show();
        }
    }
}
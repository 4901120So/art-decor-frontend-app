package com.example.arteydecoraciones;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.arteydecoraciones.api.ApiClient;
import com.example.arteydecoraciones.databinding.ActivityLoginBinding;
import com.example.arteydecoraciones.model.Cliente;
import com.example.arteydecoraciones.util.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);

        if (sessionManager.isLoggedIn()) {
            goToHome();
            return;
        }

        binding.btnLogin.setOnClickListener(v -> doLogin());
        binding.tvGoRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void doLogin() {
        String username = binding.etUsername.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.btnLogin.setEnabled(false);
        binding.progressBar.setVisibility(View.VISIBLE);

        Cliente loginRequest = new Cliente(null, username, password);

        ApiClient.getService().login(loginRequest).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                binding.btnLogin.setEnabled(true);
                binding.progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    String mensaje = response.body();
                    if (mensaje.toLowerCase().contains("exit") || mensaje.toLowerCase().contains("correc")) {
                        // Login exitoso - usar cliente guardado o crear uno básico
                        Cliente stored = sessionManager.getCliente();
                        if (stored != null && stored.getUsername().equals(username)) {
                            sessionManager.saveCliente(stored);
                        } else {
                            // Si no hay datos guardados, guardar con username (sin ID conocido)
                            Cliente c = new Cliente(null, username, "");
                            sessionManager.saveCliente(c);
                        }
                        Toast.makeText(LoginActivity.this, "¡Bienvenido, " + username + "!", Toast.LENGTH_SHORT).show();
                        goToHome();
                    } else {
                        Toast.makeText(LoginActivity.this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Error al iniciar sesión", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                binding.btnLogin.setEnabled(true);
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(LoginActivity.this,
                        "No se pudo conectar con el servidor.\nVerifica que Spring Boot esté ejecutándose en el puerto 8081.",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void goToHome() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }
}

package com.example.arteydecoraciones;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.arteydecoraciones.api.ApiClient;
import com.example.arteydecoraciones.databinding.ActivityRegisterBinding;
import com.example.arteydecoraciones.model.Cliente;
import com.example.arteydecoraciones.util.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);

        binding.btnRegister.setOnClickListener(v -> doRegister());
        binding.tvGoLogin.setOnClickListener(v -> finish());
    }

    private void doRegister() {
        String idStr = binding.etId.getText().toString().trim();
        String username = binding.etUsername.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        if (idStr.isEmpty() || username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        long id;
        try {
            id = Long.parseLong(idStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "El ID debe ser un número válido", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.btnRegister.setEnabled(false);
        binding.progressBar.setVisibility(View.VISIBLE);

        Cliente nuevoCliente = new Cliente(id, username, password);

        ApiClient.getService().registrarCliente(nuevoCliente).enqueue(new Callback<Cliente>() {
            @Override
            public void onResponse(Call<Cliente> call, Response<Cliente> response) {
                binding.btnRegister.setEnabled(true);
                binding.progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    Cliente clienteGuardado = response.body();
                    sessionManager.saveCliente(clienteGuardado);
                    Toast.makeText(RegisterActivity.this,
                            "¡Cuenta creada! Bienvenido, " + clienteGuardado.getUsername(),
                            Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else if (response.code() == 500) {
                    Toast.makeText(RegisterActivity.this,
                            "Ya existe un cliente con ese ID", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RegisterActivity.this,
                            "Error al registrar: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Cliente> call, Throwable t) {
                binding.btnRegister.setEnabled(true);
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(RegisterActivity.this,
                        "No se pudo conectar con el servidor.", Toast.LENGTH_LONG).show();
            }
        });
    }
}

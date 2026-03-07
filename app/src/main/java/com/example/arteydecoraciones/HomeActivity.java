package com.example.arteydecoraciones;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.arteydecoraciones.api.ApiClient;
import com.example.arteydecoraciones.databinding.ActivityHomeBinding;
import com.example.arteydecoraciones.model.Cliente;
import com.example.arteydecoraciones.model.Producto;
import com.example.arteydecoraciones.util.CartManager;
import com.example.arteydecoraciones.util.SessionManager;
import com.google.android.material.navigation.NavigationBarView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        Cliente cliente = sessionManager.getCliente();

        if (cliente != null) {
            binding.tvWelcome.setText("Bienvenido, " + cliente.getUsername() + "!");
        }

        binding.btnVerCatalogo.setOnClickListener(v ->
                startActivity(new Intent(this, ProductListActivity.class)));

        binding.btnVerPedidos.setOnClickListener(v ->
                startActivity(new Intent(this, PedidoListActivity.class)));

        binding.btnVerEnvios.setOnClickListener(v ->
                startActivity(new Intent(this, EnvioListActivity.class)));

        binding.btnCarrito.setOnClickListener(v ->
                startActivity(new Intent(this, CartActivity.class)));

        binding.btnLogout.setOnClickListener(v -> confirmLogout());

        cargarProductosDestacados();
    }

    private void cargarProductosDestacados() {
        ApiClient.getService().listarProductos().enqueue(new Callback<List<Producto>>() {
            @Override
            public void onResponse(Call<List<Producto>> call, Response<List<Producto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Producto> productos = response.body();
                    int count = Math.min(productos.size(), 3);
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < count; i++) {
                        Producto p = productos.get(i);
                        sb.append("• ").append(p.getName())
                          .append(" — $").append(p.getPrecio()).append("\n");
                    }
                    if (sb.length() > 0) {
                        binding.tvDestacados.setText(sb.toString().trim());
                    } else {
                        binding.tvDestacados.setText("No hay productos disponibles aún.");
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Producto>> call, Throwable t) {
                binding.tvDestacados.setText("No se pudo cargar el catálogo.\nVerifica la conexión con el servidor.");
            }
        });
    }

    private void confirmLogout() {
        new AlertDialog.Builder(this)
                .setTitle("Cerrar sesión")
                .setMessage("¿Deseas cerrar sesión?")
                .setPositiveButton("Sí", (d, w) -> {
                    CartManager.getInstance().clear();
                    sessionManager.logout();
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}

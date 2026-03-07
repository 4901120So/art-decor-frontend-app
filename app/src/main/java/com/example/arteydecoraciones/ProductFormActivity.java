package com.example.arteydecoraciones;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.arteydecoraciones.api.ApiClient;
import com.example.arteydecoraciones.databinding.ActivityProductFormBinding;
import com.example.arteydecoraciones.model.Producto;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductFormActivity extends AppCompatActivity {

    private ActivityProductFormBinding binding;
    private boolean isEditing = false;
    private int productoId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductFormBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        productoId = getIntent().getIntExtra("producto_id", 0);
        if (productoId != 0) {
            isEditing = true;
            if (getSupportActionBar() != null) getSupportActionBar().setTitle("Editar Producto");
            binding.etId.setEnabled(false);
            binding.etId.setText(String.valueOf(productoId));
            binding.etNombre.setText(getIntent().getStringExtra("producto_nombre"));
            binding.etDescripcion.setText(getIntent().getStringExtra("producto_desc"));
            binding.etColor.setText(getIntent().getStringExtra("producto_color"));
            binding.etDimension.setText(getIntent().getStringExtra("producto_dim"));
            binding.etStock.setText(String.valueOf(getIntent().getIntExtra("producto_stock", 0)));
            binding.etPrecio.setText(getIntent().getStringExtra("producto_precio"));
        } else {
            if (getSupportActionBar() != null) getSupportActionBar().setTitle("Agregar Producto");
        }

        binding.btnGuardar.setOnClickListener(v -> guardarProducto());
    }

    private void guardarProducto() {
        String idStr = binding.etId.getText().toString().trim();
        String nombre = binding.etNombre.getText().toString().trim();
        String desc = binding.etDescripcion.getText().toString().trim();
        String color = binding.etColor.getText().toString().trim();
        String dim = binding.etDimension.getText().toString().trim();
        String stockStr = binding.etStock.getText().toString().trim();
        String precio = binding.etPrecio.getText().toString().trim();

        if (nombre.isEmpty() || precio.isEmpty() || (!isEditing && idStr.isEmpty())) {
            Toast.makeText(this, "Nombre, ID y precio son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        int id = isEditing ? productoId : Integer.parseInt(idStr);
        int stock = stockStr.isEmpty() ? 0 : Integer.parseInt(stockStr);

        Producto producto = new Producto(id, nombre, desc, color, dim, stock, precio);

        binding.btnGuardar.setEnabled(false);
        binding.progressBar.setVisibility(View.VISIBLE);

        Call<Producto> call = isEditing
                ? ApiClient.getService().actualizarProducto(productoId, producto)
                : ApiClient.getService().crearProducto(producto);

        call.enqueue(new Callback<Producto>() {
            @Override
            public void onResponse(Call<Producto> c, Response<Producto> response) {
                binding.btnGuardar.setEnabled(true);
                binding.progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Toast.makeText(ProductFormActivity.this,
                            isEditing ? "Producto actualizado" : "Producto creado",
                            Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(ProductFormActivity.this,
                            "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Producto> c, Throwable t) {
                binding.btnGuardar.setEnabled(true);
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(ProductFormActivity.this,
                        "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

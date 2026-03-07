package com.example.arteydecoraciones;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.arteydecoraciones.databinding.ActivityProductDetailBinding;
import com.example.arteydecoraciones.model.Producto;
import com.example.arteydecoraciones.util.CartManager;

public class ProductDetailActivity extends AppCompatActivity {

    private ActivityProductDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        int id = getIntent().getIntExtra("producto_id", 0);
        String nombre = getIntent().getStringExtra("producto_nombre");
        String desc = getIntent().getStringExtra("producto_desc");
        String color = getIntent().getStringExtra("producto_color");
        String dim = getIntent().getStringExtra("producto_dim");
        int stock = getIntent().getIntExtra("producto_stock", 0);
        String precio = getIntent().getStringExtra("producto_precio");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(nombre);
        }

        binding.tvNombre.setText(nombre);
        binding.tvDescripcion.setText(desc);
        binding.tvColor.setText("Color: " + color);
        binding.tvDimension.setText("Dimensiones: " + dim);
        binding.tvPrecio.setText("$" + precio);

        if (stock > 0) {
            binding.tvStock.setText("Disponible: " + stock + " unidades");
            binding.tvStock.setTextColor(getColor(R.color.success_green));
            binding.btnAddToCart.setEnabled(true);
        } else {
            binding.tvStock.setText("Sin stock disponible");
            binding.tvStock.setTextColor(getColor(R.color.error_red));
            binding.btnAddToCart.setEnabled(false);
        }

        binding.btnAddToCart.setOnClickListener(v -> {
            Producto producto = new Producto(id, nombre, desc, color, dim, stock, precio);
            CartManager.getInstance().addItem(producto, 1);
            Toast.makeText(this, nombre + " agregado al carrito", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

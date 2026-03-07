package com.example.arteydecoraciones;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.arteydecoraciones.adapter.ProductoAdapter;
import com.example.arteydecoraciones.api.ApiClient;
import com.example.arteydecoraciones.databinding.ActivityProductListBinding;
import com.example.arteydecoraciones.model.Producto;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductListActivity extends AppCompatActivity {

    private ActivityProductListBinding binding;
    private ProductoAdapter adapter;
    private List<Producto> productos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Catálogo de Productos");
        }

        adapter = new ProductoAdapter(productos, new ProductoAdapter.OnProductoClickListener() {
            @Override
            public void onProductoClick(Producto producto) {
                Intent intent = new Intent(ProductListActivity.this, ProductDetailActivity.class);
                intent.putExtra("producto_id", producto.getIdProducto());
                intent.putExtra("producto_nombre", producto.getName());
                intent.putExtra("producto_desc", producto.getDescripcion());
                intent.putExtra("producto_color", producto.getColor());
                intent.putExtra("producto_dim", producto.getDimension());
                intent.putExtra("producto_stock", producto.getStock());
                intent.putExtra("producto_precio", producto.getPrecio());
                startActivity(intent);
            }

            @Override
            public void onProductoLongClick(Producto producto) {
                showProductOptions(producto);
            }
        });

        binding.rvProductos.setLayoutManager(new GridLayoutManager(this, 2));
        binding.rvProductos.setAdapter(adapter);

        binding.fabAddProducto.setOnClickListener(v ->
                startActivity(new Intent(this, ProductFormActivity.class)));

        cargarProductos();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarProductos();
    }

    private void cargarProductos() {
        binding.progressBar.setVisibility(View.VISIBLE);
        ApiClient.getService().listarProductos().enqueue(new Callback<List<Producto>>() {
            @Override
            public void onResponse(Call<List<Producto>> call, Response<List<Producto>> response) {
                binding.progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    productos.clear();
                    productos.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    binding.tvEmpty.setVisibility(productos.isEmpty() ? View.VISIBLE : View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<Producto>> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(ProductListActivity.this,
                        "Error de conexión con el servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showProductOptions(Producto producto) {
        String[] options = {"Editar", "Eliminar"};
        new AlertDialog.Builder(this)
                .setTitle(producto.getName())
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        Intent intent = new Intent(this, ProductFormActivity.class);
                        intent.putExtra("producto_id", producto.getIdProducto());
                        intent.putExtra("producto_nombre", producto.getName());
                        intent.putExtra("producto_desc", producto.getDescripcion());
                        intent.putExtra("producto_color", producto.getColor());
                        intent.putExtra("producto_dim", producto.getDimension());
                        intent.putExtra("producto_stock", producto.getStock());
                        intent.putExtra("producto_precio", producto.getPrecio());
                        startActivity(intent);
                    } else {
                        confirmDelete(producto);
                    }
                }).show();
    }

    private void confirmDelete(Producto producto) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar producto")
                .setMessage("¿Eliminar '" + producto.getName() + "'?")
                .setPositiveButton("Eliminar", (d, w) -> {
                    ApiClient.getService().eliminarProducto(producto.getIdProducto())
                            .enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    Toast.makeText(ProductListActivity.this,
                                            "Producto eliminado", Toast.LENGTH_SHORT).show();
                                    cargarProductos();
                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {
                                    Toast.makeText(ProductListActivity.this,
                                            "Error al eliminar", Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .setNegativeButton("Cancelar", null).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

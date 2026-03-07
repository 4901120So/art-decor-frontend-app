package com.example.arteydecoraciones;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.arteydecoraciones.adapter.CartAdapter;
import com.example.arteydecoraciones.api.ApiClient;
import com.example.arteydecoraciones.databinding.ActivityCartBinding;
import com.example.arteydecoraciones.model.Cliente;
import com.example.arteydecoraciones.model.Pedido;
import com.example.arteydecoraciones.model.PedidoItem;
import com.example.arteydecoraciones.util.CartManager;
import com.example.arteydecoraciones.util.SessionManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity {

    private ActivityCartBinding binding;
    private CartAdapter adapter;
    private CartManager cartManager;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Mi Carrito");
        }

        cartManager = CartManager.getInstance();
        sessionManager = new SessionManager(this);

        adapter = new CartAdapter(cartManager.getItems(), new CartAdapter.OnCartItemListener() {
            @Override
            public void onRemoveItem(int position) {
                cartManager.removeItem(position);
                adapter.notifyDataSetChanged();
                updateTotal();
                checkEmpty();
            }

            @Override
            public void onQuantityChanged(int position, int newQuantity) {
                cartManager.getItems().get(position).setCantidad(newQuantity);
                adapter.notifyItemChanged(position);
                updateTotal();
            }
        });

        binding.rvCart.setLayoutManager(new LinearLayoutManager(this));
        binding.rvCart.setAdapter(adapter);

        binding.btnCheckout.setOnClickListener(v -> realizarPedido());

        updateTotal();
        checkEmpty();
    }

    private void updateTotal() {
        binding.tvTotal.setText(String.format("Total: $%.2f", cartManager.getTotal()));
    }

    private void checkEmpty() {
        if (cartManager.getItemCount() == 0) {
            binding.tvEmpty.setVisibility(View.VISIBLE);
            binding.btnCheckout.setEnabled(false);
        } else {
            binding.tvEmpty.setVisibility(View.GONE);
            binding.btnCheckout.setEnabled(true);
        }
    }

    private void realizarPedido() {
        Cliente cliente = sessionManager.getCliente();
        if (cliente == null || cliente.getId() == null) {
            Toast.makeText(this,
                    "No se puede identificar tu cuenta. Por favor regístrate nuevamente.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Confirmar pedido")
                .setMessage("¿Confirmas el pedido por $" + String.format("%.2f", cartManager.getTotal()) + "?")
                .setPositiveButton("Confirmar", (d, w) -> crearPedidoEnServidor(cliente))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void crearPedidoEnServidor(Cliente cliente) {
        binding.btnCheckout.setEnabled(false);
        binding.progressBar.setVisibility(View.VISIBLE);

        Pedido pedido = new Pedido();
        // Solo enviamos el ID del cliente para la referencia
        Cliente clienteRef = new Cliente(cliente.getId(), null, null);
        pedido.setCliente(clienteRef);
        pedido.setItems(cartManager.getItems());

        ApiClient.getService().crearPedido(pedido).enqueue(new Callback<Pedido>() {
            @Override
            public void onResponse(Call<Pedido> call, Response<Pedido> response) {
                binding.btnCheckout.setEnabled(true);
                binding.progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    cartManager.clear();
                    adapter.notifyDataSetChanged();
                    updateTotal();
                    checkEmpty();
                    Toast.makeText(CartActivity.this,
                            "¡Pedido #" + response.body().getId() + " creado con éxito!",
                            Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(CartActivity.this,
                            "Error al crear el pedido: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Pedido> call, Throwable t) {
                binding.btnCheckout.setEnabled(true);
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(CartActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

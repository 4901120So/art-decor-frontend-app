package com.example.arteydecoraciones;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.arteydecoraciones.adapter.PedidoAdapter;
import com.example.arteydecoraciones.api.ApiClient;
import com.example.arteydecoraciones.databinding.ActivityPedidoListBinding;
import com.example.arteydecoraciones.model.Pedido;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PedidoListActivity extends AppCompatActivity {

    private ActivityPedidoListBinding binding;
    private PedidoAdapter adapter;
    private List<Pedido> pedidos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPedidoListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Mis Pedidos");
        }

        adapter = new PedidoAdapter(pedidos, pedido -> showPedidoOptions(pedido));

        binding.rvPedidos.setLayoutManager(new LinearLayoutManager(this));
        binding.rvPedidos.setAdapter(adapter);

        cargarPedidos();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarPedidos();
    }

    private void cargarPedidos() {
        binding.progressBar.setVisibility(View.VISIBLE);
        ApiClient.getService().listarPedidos().enqueue(new Callback<List<Pedido>>() {
            @Override
            public void onResponse(Call<List<Pedido>> call, Response<List<Pedido>> response) {
                binding.progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    pedidos.clear();
                    pedidos.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    binding.tvEmpty.setVisibility(pedidos.isEmpty() ? View.VISIBLE : View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<Pedido>> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(PedidoListActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showPedidoOptions(Pedido pedido) {
        String[] estados = {"CREADO", "PROCESANDO", "ENVIADO", "ENTREGADO"};
        new AlertDialog.Builder(this)
                .setTitle("Pedido #" + pedido.getId())
                .setItems(new String[]{"Cambiar estado", "Eliminar pedido"}, (d, which) -> {
                    if (which == 0) {
                        new AlertDialog.Builder(this)
                                .setTitle("Selecciona el nuevo estado")
                                .setItems(estados, (d2, i) -> actualizarEstado(pedido, estados[i]))
                                .show();
                    } else {
                        confirmDelete(pedido);
                    }
                }).show();
    }

    private void actualizarEstado(Pedido pedido, String nuevoEstado) {
        pedido.setEstado(nuevoEstado);
        ApiClient.getService().actualizarPedido(pedido.getId(), pedido)
                .enqueue(new Callback<Pedido>() {
                    @Override
                    public void onResponse(Call<Pedido> call, Response<Pedido> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(PedidoListActivity.this,
                                    "Estado actualizado a: " + nuevoEstado, Toast.LENGTH_SHORT).show();
                            cargarPedidos();
                        }
                    }

                    @Override
                    public void onFailure(Call<Pedido> call, Throwable t) {
                        Toast.makeText(PedidoListActivity.this, "Error al actualizar", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void confirmDelete(Pedido pedido) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar pedido")
                .setMessage("¿Eliminar Pedido #" + pedido.getId() + "?")
                .setPositiveButton("Eliminar", (d, w) ->
                        ApiClient.getService().eliminarPedido(pedido.getId())
                                .enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(Call<Void> call, Response<Void> r) {
                                        Toast.makeText(PedidoListActivity.this, "Pedido eliminado", Toast.LENGTH_SHORT).show();
                                        cargarPedidos();
                                    }

                                    @Override
                                    public void onFailure(Call<Void> call, Throwable t) {
                                        Toast.makeText(PedidoListActivity.this, "Error al eliminar", Toast.LENGTH_SHORT).show();
                                    }
                                }))
                .setNegativeButton("Cancelar", null).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

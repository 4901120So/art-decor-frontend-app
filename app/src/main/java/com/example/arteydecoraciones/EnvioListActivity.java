package com.example.arteydecoraciones;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.arteydecoraciones.adapter.EnvioAdapter;
import com.example.arteydecoraciones.api.ApiClient;
import com.example.arteydecoraciones.databinding.ActivityEnvioListBinding;
import com.example.arteydecoraciones.model.Envio;
import com.example.arteydecoraciones.model.Pedido;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EnvioListActivity extends AppCompatActivity {

    private ActivityEnvioListBinding binding;
    private EnvioAdapter adapter;
    private List<Envio> envios = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEnvioListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Gestión de Envíos");
        }

        adapter = new EnvioAdapter(envios, envio -> showEnvioOptions(envio));

        binding.rvEnvios.setLayoutManager(new LinearLayoutManager(this));
        binding.rvEnvios.setAdapter(adapter);

        binding.fabAddEnvio.setOnClickListener(v -> showAddEnvioDialog());

        cargarEnvios();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarEnvios();
    }

    private void cargarEnvios() {
        binding.progressBar.setVisibility(View.VISIBLE);
        ApiClient.getService().listarEnvios().enqueue(new Callback<List<Envio>>() {
            @Override
            public void onResponse(Call<List<Envio>> call, Response<List<Envio>> response) {
                binding.progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    envios.clear();
                    envios.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    binding.tvEmpty.setVisibility(envios.isEmpty() ? View.VISIBLE : View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<Envio>> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(EnvioListActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddEnvioDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_envio, null);
        EditText etPedidoId = dialogView.findViewById(R.id.et_pedido_id);
        EditText etDireccion = dialogView.findViewById(R.id.et_direccion);
        EditText etTracking = dialogView.findViewById(R.id.et_tracking);

        new AlertDialog.Builder(this)
                .setTitle("Registrar Envío")
                .setView(dialogView)
                .setPositiveButton("Registrar", (d, w) -> {
                    String pedidoIdStr = etPedidoId.getText().toString().trim();
                    String direccion = etDireccion.getText().toString().trim();
                    String tracking = etTracking.getText().toString().trim();

                    if (pedidoIdStr.isEmpty() || direccion.isEmpty()) {
                        Toast.makeText(this, "ID de pedido y dirección son obligatorios", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Envio envio = new Envio();
                    Pedido pedidoRef = new Pedido();
                    pedidoRef.setId(Long.parseLong(pedidoIdStr));
                    envio.setPedido(pedidoRef);
                    envio.setDireccion(direccion);
                    envio.setTrackingNumber(tracking);
                    envio.setEstado("PENDIENTE");

                    crearEnvio(envio);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void crearEnvio(Envio envio) {
        ApiClient.getService().crearEnvio(envio).enqueue(new Callback<Envio>() {
            @Override
            public void onResponse(Call<Envio> call, Response<Envio> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EnvioListActivity.this, "Envío registrado", Toast.LENGTH_SHORT).show();
                    cargarEnvios();
                } else {
                    Toast.makeText(EnvioListActivity.this, "Error al registrar envío", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Envio> call, Throwable t) {
                Toast.makeText(EnvioListActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEnvioOptions(Envio envio) {
        String[] estados = {"PENDIENTE", "EN_CAMINO", "ENTREGADO"};
        new AlertDialog.Builder(this)
                .setTitle("Envío #" + envio.getId())
                .setItems(new String[]{"Cambiar estado", "Eliminar"}, (d, which) -> {
                    if (which == 0) {
                        new AlertDialog.Builder(this)
                                .setTitle("Nuevo estado")
                                .setItems(estados, (d2, i) -> {
                                    envio.setEstado(estados[i]);
                                    ApiClient.getService().actualizarEnvio(envio.getId(), envio)
                                            .enqueue(new Callback<Envio>() {
                                                @Override
                                                public void onResponse(Call<Envio> c, Response<Envio> r) {
                                                    if (r.isSuccessful()) {
                                                        Toast.makeText(EnvioListActivity.this, "Estado actualizado", Toast.LENGTH_SHORT).show();
                                                        cargarEnvios();
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<Envio> c, Throwable t) {
                                                    Toast.makeText(EnvioListActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }).show();
                    } else {
                        ApiClient.getService().eliminarEnvio(envio.getId())
                                .enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(Call<Void> c, Response<Void> r) {
                                        Toast.makeText(EnvioListActivity.this, "Envío eliminado", Toast.LENGTH_SHORT).show();
                                        cargarEnvios();
                                    }

                                    @Override
                                    public void onFailure(Call<Void> c, Throwable t) {
                                        Toast.makeText(EnvioListActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

package com.example.arteydecoraciones;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

        binding.fabAddEnvio.setOnClickListener(v -> cargarPedidosYMostrarDialogo());

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
                } else {
                    Toast.makeText(EnvioListActivity.this, "No se pudieron cargar los envíos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Envio>> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(EnvioListActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /** Carga pedidos y envíos para mostrar solo los pedidos sin envío asignado */
    private void cargarPedidosYMostrarDialogo() {
        binding.progressBar.setVisibility(View.VISIBLE);
        ApiClient.getService().listarPedidos().enqueue(new Callback<List<Pedido>>() {
            @Override
            public void onResponse(Call<List<Pedido>> call, Response<List<Pedido>> response) {
                binding.progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    // IDs de pedidos que ya tienen envío
                    Set<Long> pedidosConEnvio = new HashSet<>();
                    for (Envio e : envios) {
                        if (e.getPedido() != null && e.getPedido().getId() != null) {
                            pedidosConEnvio.add(e.getPedido().getId());
                        }
                    }

                    List<Pedido> disponibles = new ArrayList<>();
                    for (Pedido p : response.body()) {
                        if (!pedidosConEnvio.contains(p.getId())) {
                            disponibles.add(p);
                        }
                    }

                    if (disponibles.isEmpty()) {
                        new AlertDialog.Builder(EnvioListActivity.this)
                                .setTitle("Sin pedidos disponibles")
                                .setMessage("Todos los pedidos ya tienen un envío asignado o no hay pedidos registrados.")
                                .setPositiveButton("OK", null)
                                .show();
                        return;
                    }

                    showAddEnvioDialog(disponibles);
                } else {
                    Toast.makeText(EnvioListActivity.this, "No se pudieron cargar los pedidos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Pedido>> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(EnvioListActivity.this, "Error de conexión al cargar pedidos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddEnvioDialog(List<Pedido> pedidosDisponibles) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_envio, null);
        AutoCompleteTextView spinnerPedido = dialogView.findViewById(R.id.spinner_pedido);
        EditText etDireccion = dialogView.findViewById(R.id.et_direccion);
        EditText etTracking = dialogView.findViewById(R.id.et_tracking);

        // Construir etiquetas para cada pedido
        String[] labels = new String[pedidosDisponibles.size()];
        for (int i = 0; i < pedidosDisponibles.size(); i++) {
            Pedido p = pedidosDisponibles.get(i);
            String total = p.getTotal() != null ? "$" + p.getTotal() : "";
            String estado = p.getEstado() != null ? " [" + p.getEstado() + "]" : "";
            labels[i] = "Pedido #" + p.getId() + " " + total + estado;
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, labels);
        spinnerPedido.setAdapter(spinnerAdapter);
        spinnerPedido.setText(labels[0], false);

        final int[] selectedIndex = {0};
        spinnerPedido.setOnItemClickListener((parent, view, position, id) -> selectedIndex[0] = position);

        new AlertDialog.Builder(this)
                .setTitle("Registrar Envío")
                .setView(dialogView)
                .setPositiveButton("Registrar", (d, w) -> {
                    String direccion = etDireccion.getText().toString().trim();
                    String tracking = etTracking.getText().toString().trim();

                    if (direccion.isEmpty()) {
                        Toast.makeText(this, "La dirección es obligatoria", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Pedido pedidoSeleccionado = pedidosDisponibles.get(selectedIndex[0]);
                    Pedido pedidoRef = new Pedido();
                    pedidoRef.setId(pedidoSeleccionado.getId());

                    Envio envio = new Envio();
                    envio.setPedido(pedidoRef);
                    envio.setDireccion(direccion);
                    envio.setTrackingNumber(tracking.isEmpty() ? null : tracking);
                    envio.setEstado("PENDIENTE");

                    crearEnvio(envio);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void crearEnvio(Envio envio) {
        binding.progressBar.setVisibility(View.VISIBLE);
        ApiClient.getService().crearEnvio(envio).enqueue(new Callback<Envio>() {
            @Override
            public void onResponse(Call<Envio> call, Response<Envio> response) {
                binding.progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Toast.makeText(EnvioListActivity.this, "Envío registrado correctamente", Toast.LENGTH_SHORT).show();
                    cargarEnvios();
                } else {
                    String errorMsg = "Error al registrar envío (código " + response.code() + ")";
                    if (response.errorBody() != null) {
                        try {
                            String body = response.errorBody().string();
                            if (body != null && !body.isEmpty()) errorMsg = body;
                        } catch (IOException ignored) {}
                    }
                    new AlertDialog.Builder(EnvioListActivity.this)
                            .setTitle("Error al registrar envío")
                            .setMessage(errorMsg)
                            .setPositiveButton("OK", null)
                            .show();
                }
            }

            @Override
            public void onFailure(Call<Envio> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(EnvioListActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
                                                    } else {
                                                        Toast.makeText(EnvioListActivity.this, "Error al actualizar estado", Toast.LENGTH_SHORT).show();
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<Envio> c, Throwable t) {
                                                    Toast.makeText(EnvioListActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }).show();
                    } else {
                        new AlertDialog.Builder(this)
                                .setTitle("Eliminar envío")
                                .setMessage("¿Estás seguro de que deseas eliminar el envío #" + envio.getId() + "?")
                                .setPositiveButton("Eliminar", (dc, wi) ->
                                        ApiClient.getService().eliminarEnvio(envio.getId())
                                                .enqueue(new Callback<Void>() {
                                                    @Override
                                                    public void onResponse(Call<Void> c, Response<Void> r) {
                                                        Toast.makeText(EnvioListActivity.this, "Envío eliminado", Toast.LENGTH_SHORT).show();
                                                        cargarEnvios();
                                                    }

                                                    @Override
                                                    public void onFailure(Call<Void> c, Throwable t) {
                                                        Toast.makeText(EnvioListActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                                                    }
                                                }))
                                .setNegativeButton("Cancelar", null)
                                .show();
                    }
                }).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

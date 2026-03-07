package com.example.arteydecoraciones.api;

import com.example.arteydecoraciones.model.Cliente;
import com.example.arteydecoraciones.model.Envio;
import com.example.arteydecoraciones.model.Pedido;
import com.example.arteydecoraciones.model.Producto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {

    // ─── CLIENTES ───────────────────────────────────────────────────────────────
    @POST("api/clientes")
    Call<Cliente> registrarCliente(@Body Cliente cliente);

    @POST("api/clientes/login")
    Call<String> login(@Body Cliente cliente);

    // ─── PRODUCTOS ──────────────────────────────────────────────────────────────
    @GET("api/productos/listar")
    Call<List<Producto>> listarProductos();

    @GET("api/productos/{id}")
    Call<Producto> getProductoPorId(@Path("id") int id);

    @POST("api/productos")
    Call<Producto> crearProducto(@Body Producto producto);

    @PUT("api/productos/actualizar/{id}")
    Call<Producto> actualizarProducto(@Path("id") int id, @Body Producto producto);

    @DELETE("api/productos/{id}")
    Call<Void> eliminarProducto(@Path("id") int id);

    // ─── PEDIDOS ────────────────────────────────────────────────────────────────
    @GET("api/pedidos/listar")
    Call<List<Pedido>> listarPedidos();

    @GET("api/pedidos/{id}")
    Call<Pedido> getPedidoPorId(@Path("id") long id);

    @POST("api/pedidos")
    Call<Pedido> crearPedido(@Body Pedido pedido);

    @PUT("api/pedidos/actualizar/{id}")
    Call<Pedido> actualizarPedido(@Path("id") long id, @Body Pedido pedido);

    @DELETE("api/pedidos/{id}")
    Call<Void> eliminarPedido(@Path("id") long id);

    // ─── ENVIOS ─────────────────────────────────────────────────────────────────
    @GET("api/envios/listar")
    Call<List<Envio>> listarEnvios();

    @GET("api/envios/{id}")
    Call<Envio> getEnvioPorId(@Path("id") long id);

    @POST("api/envios")
    Call<Envio> crearEnvio(@Body Envio envio);

    @PUT("api/envios/actualizar/{id}")
    Call<Envio> actualizarEnvio(@Path("id") long id, @Body Envio envio);

    @DELETE("api/envios/{id}")
    Call<Void> eliminarEnvio(@Path("id") long id);
}

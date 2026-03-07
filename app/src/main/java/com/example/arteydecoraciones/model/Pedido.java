package com.example.arteydecoraciones.model;

import java.util.ArrayList;
import java.util.List;

public class Pedido {
    private Long id;
    private Cliente cliente;
    private String fechaCreacion;
    private String estado;
    private String total;
    private List<PedidoItem> items = new ArrayList<>();

    public Pedido() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public String getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(String fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getTotal() { return total; }
    public void setTotal(String total) { this.total = total; }

    public List<PedidoItem> getItems() { return items; }
    public void setItems(List<PedidoItem> items) { this.items = items; }
}

package com.example.arteydecoraciones.model;

public class PedidoItem {
    private Long id;
    private Producto producto;
    private Integer cantidad;
    private String precio;

    public PedidoItem() {}

    public PedidoItem(Producto producto, Integer cantidad, String precio) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.precio = precio;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public String getPrecio() { return precio; }
    public void setPrecio(String precio) { this.precio = precio; }
}

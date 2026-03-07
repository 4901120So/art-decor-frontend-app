package com.example.arteydecoraciones.model;

import com.google.gson.annotations.SerializedName;

public class Producto {
    // Spring Boot getter getId_Producto() -> Jackson serializa como "id_Producto"
    @SerializedName("id_Producto")
    private int idProducto;

    private String name;
    private String descripcion;
    private String color;
    private String dimension;
    private int stock;
    private String precio;

    public Producto() {}

    public Producto(int idProducto, String name, String descripcion,
                    String color, String dimension, int stock, String precio) {
        this.idProducto = idProducto;
        this.name = name;
        this.descripcion = descripcion;
        this.color = color;
        this.dimension = dimension;
        this.stock = stock;
        this.precio = precio;
    }

    public int getIdProducto() { return idProducto; }
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getDimension() { return dimension; }
    public void setDimension(String dimension) { this.dimension = dimension; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public String getPrecio() { return precio; }
    public void setPrecio(String precio) { this.precio = precio; }
}

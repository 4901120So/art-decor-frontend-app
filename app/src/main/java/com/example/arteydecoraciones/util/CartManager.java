package com.example.arteydecoraciones.util;

import com.example.arteydecoraciones.model.PedidoItem;
import com.example.arteydecoraciones.model.Producto;

import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static CartManager instance;
    private final List<PedidoItem> items = new ArrayList<>();

    private CartManager() {}

    public static CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public void addItem(Producto producto, int cantidad) {
        for (PedidoItem item : items) {
            if (item.getProducto().getIdProducto() == producto.getIdProducto()) {
                item.setCantidad(item.getCantidad() + cantidad);
                return;
            }
        }
        PedidoItem item = new PedidoItem(producto, cantidad, producto.getPrecio());
        items.add(item);
    }

    public void removeItem(int index) {
        if (index >= 0 && index < items.size()) {
            items.remove(index);
        }
    }

    public List<PedidoItem> getItems() {
        return items;
    }

    public void clear() {
        items.clear();
    }

    public int getItemCount() {
        return items.size();
    }

    public double getTotal() {
        double total = 0;
        for (PedidoItem item : items) {
            try {
                double precio = Double.parseDouble(item.getPrecio());
                total += precio * item.getCantidad();
            } catch (NumberFormatException ignored) {}
        }
        return total;
    }
}

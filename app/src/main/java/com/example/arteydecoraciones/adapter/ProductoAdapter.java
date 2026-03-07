package com.example.arteydecoraciones.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.arteydecoraciones.R;
import com.example.arteydecoraciones.model.Producto;

import java.util.List;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ViewHolder> {

    public interface OnProductoClickListener {
        void onProductoClick(Producto producto);
        void onProductoLongClick(Producto producto);
    }

    private final List<Producto> productos;
    private final OnProductoClickListener listener;

    public ProductoAdapter(List<Producto> productos, OnProductoClickListener listener) {
        this.productos = productos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_producto, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Producto p = productos.get(position);
        holder.tvNombre.setText(p.getName());
        holder.tvPrecio.setText("$" + p.getPrecio());
        holder.tvColor.setText(p.getColor());
        holder.tvDimension.setText(p.getDimension());

        if (p.getStock() > 0) {
            holder.tvStock.setText("Stock: " + p.getStock());
            holder.tvStock.setTextColor(holder.itemView.getContext().getColor(R.color.success_green));
        } else {
            holder.tvStock.setText("Sin stock");
            holder.tvStock.setTextColor(holder.itemView.getContext().getColor(R.color.error_red));
        }

        holder.card.setOnClickListener(v -> listener.onProductoClick(p));
        holder.card.setOnLongClickListener(v -> {
            listener.onProductoLongClick(p);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return productos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView card;
        TextView tvNombre, tvPrecio, tvColor, tvDimension, tvStock;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.card_producto);
            tvNombre = itemView.findViewById(R.id.tv_producto_nombre);
            tvPrecio = itemView.findViewById(R.id.tv_producto_precio);
            tvColor = itemView.findViewById(R.id.tv_producto_color);
            tvDimension = itemView.findViewById(R.id.tv_producto_dimension);
            tvStock = itemView.findViewById(R.id.tv_producto_stock);
        }
    }
}

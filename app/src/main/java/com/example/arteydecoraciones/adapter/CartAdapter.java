package com.example.arteydecoraciones.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.arteydecoraciones.R;
import com.example.arteydecoraciones.model.PedidoItem;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    public interface OnCartItemListener {
        void onRemoveItem(int position);
        void onQuantityChanged(int position, int newQuantity);
    }

    private final List<PedidoItem> items;
    private final OnCartItemListener listener;

    public CartAdapter(List<PedidoItem> items, OnCartItemListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PedidoItem item = items.get(position);
        holder.tvNombre.setText(item.getProducto() != null ? item.getProducto().getName() : "Producto");
        holder.tvPrecio.setText("$" + item.getPrecio());
        holder.tvCantidad.setText(String.valueOf(item.getCantidad()));

        double subtotal = 0;
        try {
            subtotal = Double.parseDouble(item.getPrecio()) * item.getCantidad();
        } catch (NumberFormatException ignored) {}
        holder.tvSubtotal.setText("Subtotal: $" + String.format("%.2f", subtotal));

        holder.btnRemove.setOnClickListener(v -> listener.onRemoveItem(holder.getAdapterPosition()));

        holder.btnMinus.setOnClickListener(v -> {
            int qty = item.getCantidad();
            if (qty > 1) {
                listener.onQuantityChanged(holder.getAdapterPosition(), qty - 1);
            }
        });

        holder.btnPlus.setOnClickListener(v -> {
            int qty = item.getCantidad();
            listener.onQuantityChanged(holder.getAdapterPosition(), qty + 1);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvPrecio, tvCantidad, tvSubtotal;
        ImageButton btnRemove, btnPlus, btnMinus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tv_cart_nombre);
            tvPrecio = itemView.findViewById(R.id.tv_cart_precio);
            tvCantidad = itemView.findViewById(R.id.tv_cart_cantidad);
            tvSubtotal = itemView.findViewById(R.id.tv_cart_subtotal);
            btnRemove = itemView.findViewById(R.id.btn_cart_remove);
            btnPlus = itemView.findViewById(R.id.btn_cart_plus);
            btnMinus = itemView.findViewById(R.id.btn_cart_minus);
        }
    }
}

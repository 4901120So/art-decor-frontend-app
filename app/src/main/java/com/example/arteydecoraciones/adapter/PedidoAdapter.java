package com.example.arteydecoraciones.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.arteydecoraciones.R;
import com.example.arteydecoraciones.model.Pedido;

import java.util.List;

public class PedidoAdapter extends RecyclerView.Adapter<PedidoAdapter.ViewHolder> {

    public interface OnPedidoClickListener {
        void onPedidoClick(Pedido pedido);
    }

    private final List<Pedido> pedidos;
    private final OnPedidoClickListener listener;

    public PedidoAdapter(List<Pedido> pedidos, OnPedidoClickListener listener) {
        this.pedidos = pedidos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pedido, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Pedido p = pedidos.get(position);
        holder.tvId.setText("Pedido #" + p.getId());
        holder.tvTotal.setText("Total: $" + p.getTotal());
        holder.tvEstado.setText(p.getEstado() != null ? p.getEstado() : "CREADO");

        String fecha = p.getFechaCreacion() != null
                ? p.getFechaCreacion().replace("T", " ").substring(0, Math.min(16, p.getFechaCreacion().length()))
                : "Sin fecha";
        holder.tvFecha.setText(fecha);

        int colorRes = getEstadoColor(p.getEstado());
        holder.tvEstado.setTextColor(holder.itemView.getContext().getColor(colorRes));

        holder.card.setOnClickListener(v -> listener.onPedidoClick(p));
    }

    private int getEstadoColor(String estado) {
        if (estado == null) return R.color.status_created;
        switch (estado) {
            case "CREADO": return R.color.status_created;
            case "PROCESANDO": return R.color.status_processing;
            case "ENVIADO": return R.color.status_sent;
            case "ENTREGADO": return R.color.status_delivered;
            default: return R.color.brown_600;
        }
    }

    @Override
    public int getItemCount() {
        return pedidos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView card;
        TextView tvId, tvFecha, tvEstado, tvTotal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.card_pedido);
            tvId = itemView.findViewById(R.id.tv_pedido_id);
            tvFecha = itemView.findViewById(R.id.tv_pedido_fecha);
            tvEstado = itemView.findViewById(R.id.tv_pedido_estado);
            tvTotal = itemView.findViewById(R.id.tv_pedido_total);
        }
    }
}

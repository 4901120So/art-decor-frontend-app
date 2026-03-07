package com.example.arteydecoraciones.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.arteydecoraciones.R;
import com.example.arteydecoraciones.model.Envio;

import java.util.List;

public class EnvioAdapter extends RecyclerView.Adapter<EnvioAdapter.ViewHolder> {

    public interface OnEnvioClickListener {
        void onEnvioClick(Envio envio);
    }

    private final List<Envio> envios;
    private final OnEnvioClickListener listener;

    public EnvioAdapter(List<Envio> envios, OnEnvioClickListener listener) {
        this.envios = envios;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_envio, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Envio e = envios.get(position);
        holder.tvId.setText("Envío #" + e.getId());
        holder.tvDireccion.setText(e.getDireccion() != null ? e.getDireccion() : "Sin dirección");
        holder.tvTracking.setText("Tracking: " + (e.getTrackingNumber() != null ? e.getTrackingNumber() : "N/A"));
        holder.tvEstado.setText(e.getEstado() != null ? e.getEstado() : "PENDIENTE");

        if (e.getPedido() != null) {
            holder.tvPedidoRef.setText("Pedido #" + e.getPedido().getId());
        } else {
            holder.tvPedidoRef.setText("Sin pedido asociado");
        }

        int colorRes = getEstadoColor(e.getEstado());
        holder.tvEstado.setTextColor(holder.itemView.getContext().getColor(colorRes));

        holder.card.setOnClickListener(v -> listener.onEnvioClick(e));
    }

    private int getEstadoColor(String estado) {
        if (estado == null) return R.color.status_pending;
        switch (estado) {
            case "PENDIENTE": return R.color.status_pending;
            case "EN_CAMINO": return R.color.status_processing;
            case "ENTREGADO": return R.color.status_delivered;
            default: return R.color.brown_600;
        }
    }

    @Override
    public int getItemCount() {
        return envios.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView card;
        TextView tvId, tvDireccion, tvTracking, tvEstado, tvPedidoRef;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.card_envio);
            tvId = itemView.findViewById(R.id.tv_envio_id);
            tvDireccion = itemView.findViewById(R.id.tv_envio_direccion);
            tvTracking = itemView.findViewById(R.id.tv_envio_tracking);
            tvEstado = itemView.findViewById(R.id.tv_envio_estado);
            tvPedidoRef = itemView.findViewById(R.id.tv_envio_pedido_ref);
        }
    }
}

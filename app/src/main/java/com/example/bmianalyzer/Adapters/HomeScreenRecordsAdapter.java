package com.example.bmianalyzer.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bmianalyzer.Models.Record;
import com.example.bmianalyzer.R;

import java.util.List;

public class HomeScreenRecordsAdapter extends RecyclerView.Adapter<HomeScreenRecordsAdapter.ViewHolder> {

    private final List<Record> mRecords;
    private onItemClickList mListener;

    public interface onItemClickList {
        void onItemClick(int position);
    }

    public void setOnClickList(HomeScreenRecordsAdapter.onItemClickList listener) {
        mListener = listener;
    }

    public HomeScreenRecordsAdapter(List<Record> mRecords) {
        this.mRecords = mRecords;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.record_row, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Record record = mRecords.get(position);

        holder.record_date.setText(record.getDate());
        holder.record_weight.setText(String.format("%sKg", record.getWeight()));
        holder.record_status.setText(record.getStatus());
        holder.record_height.setText(String.format("%sCm", record.getHeight()));
    }

    @Override
    public int getItemCount() {
        return mRecords.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView record_date, record_weight, record_status, record_height;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            record_date = itemView.findViewById(R.id.record_date);
            record_weight = itemView.findViewById(R.id.record_weight);
            record_status = itemView.findViewById(R.id.record_status);
            record_height = itemView.findViewById(R.id.record_height);

            itemView.setOnClickListener(v -> {
                if (mListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        mListener.onItemClick(position);
                    }
                }
            });

        }
    }
}

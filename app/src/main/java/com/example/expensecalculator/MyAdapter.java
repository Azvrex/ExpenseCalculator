package com.example.expensecalculator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private List<Item> itemList;
    private Context context;
    private onItemDeleteListener deleteListener;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtViewItem, txtViewCost;
        public ImageButton imgBtnClose;
        public ViewHolder(View itemView) {
            super(itemView);
            txtViewItem = itemView.findViewById(R.id.txtViewItem);
            txtViewCost = itemView.findViewById(R.id.txtViewCost);
            imgBtnClose = itemView.findViewById(R.id.imgBtnClose);
        }

    }
    public MyAdapter(List<Item> itemList, Context context,onItemDeleteListener listener) {
        this.itemList = itemList;
        this.context = context;
        this.deleteListener = listener;
    }

    public void deleteItem(int position) {
            int itemId = itemList.get(position).getId();
            itemList.remove(position);
            notifyItemRemoved(position);

            DBHelper dbHandler = new DBHelper(context);
            dbHandler.removeItem(itemId);
            dbHandler.printDatabaseContent();

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Item item = itemList.get(position);
        holder.txtViewItem.setText(item.getItemName());
        holder.txtViewCost.setText(String.valueOf(item.getCost()));

        if (item.getCategory().equals("Budget")){
            holder.txtViewCost.setTextColor(ContextCompat.getColor(context, R.color.green));
        }else{
            holder.txtViewCost.setTextColor(ContextCompat.getColor(context, R.color.red));
        }

        holder.imgBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItem(position); // Delete the item when the ImageButton is clicked
                deleteListener.onItemDeleted();
            }
        });


    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
    /*private List<ModelItem> itemList;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtViewItem, txtViewCost;

        public ViewHolder(View itemView) {
            super(itemView);
            txtViewItem = itemView.findViewById(R.id.txtViewItem);
            txtViewCost = itemView.findViewById(R.id.txtViewCost);
        }

    }
    public MyAdapter(List<ModelItem> itemList) {
        this.itemList = itemList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ModelItem item = itemList.get(position);
        holder.txtViewItem.setText(item.getItemName());
        holder.txtViewCost.setText(String.valueOf(item.getCost()));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }*/

}

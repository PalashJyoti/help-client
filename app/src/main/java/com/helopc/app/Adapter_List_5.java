package com.helopc.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.helopc.shopheaven.app.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class Adapter_List_5 extends RecyclerView.Adapter<Adapter_List_5.viewHolder>{
    private Context mContext;
    private List<Model_List_5> mUploads;
    private OnCategoryItemClickListener mListener;
    public Adapter_List_5(Context context, List<Model_List_5> uploads) {
        mContext = context;
        mUploads = uploads;
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Model_List_5 med = mUploads.get(position);

        Picasso.get()
                .load(med.getImage())
                .into(holder.image);
        holder.tv.setText(med.getOffer()+"%");
        holder.price.setText("Rs. "+med.getOfferAmount());
        holder.name.setText(med.getName());

    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    @NonNull
    @Override
    public Adapter_List_5.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_5,parent,false);
        return new viewHolder(view);

    }

    public class viewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView image;
        TextView tv,price,name;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            tv=itemView.findViewById(R.id.offerPercent);
            price=itemView.findViewById(R.id.price);
            name=itemView.findViewById(R.id.name);

            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mListener.onCategoryItemClick5(position);
                }
            }
        }


    }
    public interface OnCategoryItemClickListener {
        void onCategoryItemClick5(int position);

    }
    public void setOnItemClickListener(Adapter_List_5.OnCategoryItemClickListener listener) {
        mListener = listener;
    }
}

class Model_List_5 {
    String name,image,category,keyid,amount,offerAmount,offer;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getKeyid() {
        return keyid;
    }

    public void setKeyid(String keyid) {
        this.keyid = keyid;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getOfferAmount() {
        return offerAmount;
    }

    public void setOfferAmount(String offerAmount) {
        this.offerAmount = offerAmount;
    }

    public String getOffer() {
        return offer;
    }

    public void setOffer(String offer) {
        this.offer = offer;
    }
}
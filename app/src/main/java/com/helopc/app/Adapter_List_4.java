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

public class Adapter_List_4 extends RecyclerView.Adapter<Adapter_List_4.viewHolder> {

    private Context mContext;
    private List<Model_List_4> mUploads;
    private OnCategoryItemClickListener mListener;
    public Adapter_List_4(Context context, List<Model_List_4> uploads) {
        mContext = context;
        mUploads = uploads;
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Model_List_4 med = mUploads.get(position);

        Picasso.get()
                .load(med.getImage())
                .into(holder.image);
        if (med.getOffer()!=null){
            holder.price.setText("Rs. "+med.getOfferAmount());
        }else{
            holder.price.setText("Rs. "+med.getAmount());
        }
        holder.name.setText(med.getName());

    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_4,parent,false);
        return new viewHolder(view);

    }



    public class viewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView image;
        TextView price,name;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            price=itemView.findViewById(R.id.price);
            name=itemView.findViewById(R.id.name);

            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mListener.onCategoryItemClick4(position);
                }
            }
        }


    }
    public interface OnCategoryItemClickListener {
        void onCategoryItemClick4(int position);

    }
    public void setOnItemClickListener(OnCategoryItemClickListener listener) {
        mListener = listener;
    }
}

class Model_List_4 {
    String name,image,category,key,keyid,amount,offer,offerAmount;

    public String getAmount() {
        return amount;
    }

    public String getKeyid() {
        return keyid;
    }

    public String getOffer() {
        return offer;
    }

    public void setOffer(String offer) {
        this.offer = offer;
    }

    public String getOfferAmount() {
        return offerAmount;
    }

    public void setOfferAmount(String offerAmount) {
        this.offerAmount = offerAmount;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getCategory() {
        return category;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}

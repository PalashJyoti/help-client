package com.helopc.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.helopc.shopheaven.app.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class Adapter_Product_Images extends RecyclerView.Adapter<Adapter_Product_Images.viewHolder> {

    private Context mContext;
    private List<Model_Product_Images> mUploads;
    private OnCategoryItemClickListener mListener;
    public Adapter_Product_Images(Context context, List<Model_Product_Images> uploads) {
        mContext = context;
        mUploads = uploads;
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Model_Product_Images med = mUploads.get(position);

        Picasso.get()
                .load(med.getImage())
                .into(holder.image);

    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_image,parent,false);
        return new viewHolder(view);

    }



    public class viewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView image;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);

            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mListener.onCategoryItemClick2(position);
                }
            }
        }


    }
    public interface OnCategoryItemClickListener {
        void onCategoryItemClick2(int position);

    }
    public void setOnItemClickListener(OnCategoryItemClickListener listener) {
        mListener = listener;
    }
}

class Model_Product_Images {
    String image,name,keyid,category,key;

    public String getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public String getKeyid() {
        return keyid;
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

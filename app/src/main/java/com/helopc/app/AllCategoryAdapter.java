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

public class AllCategoryAdapter extends RecyclerView.Adapter<AllCategoryAdapter.viewHolder> {

    private Context mContext;
    private List<Model_List_3> mUploads;
    private OnCategoryItemClickListener mListener;
    public AllCategoryAdapter(Context context, List<Model_List_3> uploads) {
        mContext = context;
        mUploads = uploads;
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Model_List_3 med = mUploads.get(position);

        Picasso.get()
                .load(med.getImage())
                .into(holder.image);
        holder.name.setText(med.category);

    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_all_category_list,parent,false);
        return new viewHolder(view);

    }



    public class viewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView image;
        TextView name;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            name=itemView.findViewById(R.id.name);

            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mListener.onCategoryItemClick3(position);
                }
            }
        }


    }
    public interface OnCategoryItemClickListener {
        void onCategoryItemClick3(int position);

    }
    public void setOnItemClickListener(OnCategoryItemClickListener listener) {
        mListener = listener;
    }
}


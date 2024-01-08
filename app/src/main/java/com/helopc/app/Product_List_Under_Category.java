package com.helopc.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.helopc.shopheaven.app.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class Product_List_Under_Category extends AppCompatActivity implements SearchPlaceAdapter.OnCategoryItemClickListener{

    RecyclerView recyclerView;
    private ValueEventListener mDBListener;
    SearchPlaceAdapter adapter;
    private ArrayList<Model_List_4> list;
    String category;

    EditText searchText;
    ImageView search;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_list_under_category);

        recyclerView = findViewById(R.id.recyclerView);
        searchText = findViewById(R.id.searchText);
        search = findViewById(R.id.search);
        Bundle bundle = getIntent().getExtras();
        category = bundle.getString("category");

        getSupportActionBar().setTitle(category);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String SearchText = searchText.getText().toString();
                if (SearchText.matches("")) {

                }else {
                    if(adapter!=null){
                        adapter.getFilter().filter(SearchText);
                    }
                }


            }
        });

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString()!=null){
                    if(adapter!=null){
                        adapter.getFilter().filter(s.toString());
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString()!=null){
                    if(adapter!=null){
                        adapter.getFilter().filter(s.toString());
                    }
                }

            }
        });


        DatabaseReference dbcc = FirebaseDatabase.getInstance().getReference("Product Category Wise").child(category);

        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);

        dbcc.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list = new ArrayList<>();
                for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                    list.add(eventSnapshot.getValue(Model_List_4.class));
                }

                adapter = new SearchPlaceAdapter(getApplicationContext(), list);
                adapter.setOnItemClickListener(Product_List_Under_Category.this);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    @Override
    public void onCategoryItemClick(int position) {
        Intent intent = new Intent(getApplicationContext(), Product.class);
        intent.putExtra("keyid",list.get(position).getKeyid());
        startActivity(intent);
    }
}

class Adapter_Product_Under_Category extends RecyclerView.Adapter<Adapter_Product_Under_Category.viewHolder> {

    private Context mContext;
    private List<Model_List_4> mUploads;
    private OnCategoryItemClickListener mListener;
    public Adapter_Product_Under_Category(Context context, List<Model_List_4> uploads) {
        mContext = context;
        mUploads = uploads;
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Model_List_4 med = mUploads.get(position);
        if(med!=null){
            Picasso.get()
                    .load(med.getImage())
                    .into(holder.image);
            holder.title.setText(med.getName());

            if(med.getOffer()!=null && !(med.getOffer().equals("0.00"))){
                holder.price.setText("₹ "+med.getOfferAmount());
            }else {
                holder.price.setText("Rs "+med.getAmount());
            }

            holder.shimmer.setVisibility(View.GONE);
            holder.title.setVisibility(View.VISIBLE);
            holder.image.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_grid,parent,false);
        return new viewHolder(view);

    }



    public class viewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView image;
        TextView title,price;
        ShimmerFrameLayout shimmer;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.title);
            shimmer = itemView.findViewById(R.id.shimmer);
            price = itemView.findViewById(R.id.price);

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

package com.helopc.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

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

public class Product_List_All extends AppCompatActivity implements SearchPlaceAdapter.OnCategoryItemClickListener {

    RecyclerView recyclerView;
    SearchPlaceAdapter adapter;
    String text, from, category;
    int count = 0;
    DatabaseReference dbcc;
    SearchView searchView;
    ImageView search;
    private ValueEventListener mDBListener;
    private ArrayList<Model_List_4> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_list_all);
        recyclerView = findViewById(R.id.recyclerView);
        searchView = findViewById(R.id.searchText);
        search = findViewById(R.id.search);


        Bundle bundle = getIntent().getExtras();
        text = bundle.getString("text");
        from = bundle.getString("from");
        category = bundle.getString("category");


        if (from != null) {
            dbcc = FirebaseDatabase.getInstance().getReference("Product Category Wise").child(category);
        } else {
            dbcc = FirebaseDatabase.getInstance().getReference("Product");
        }


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
                adapter.setOnItemClickListener(Product_List_All.this);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });





/*        search.setOnClickListener(new View.OnClickListener() {
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
        });*/


    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main_search,menu);
        MenuItem item=menu.findItem(R.id.search);

        SearchView searchView=(SearchView)item.getActionView();
        String str = getIntent().getStringExtra("text");
        searchView.setQuery(str, true);
        if(adapter!=null){
            adapter.getFilter().filter(str);
        }
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(adapter!=null){
                    adapter.getFilter().filter(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                if(adapter!=null){
                    adapter.getFilter().filter(newText);
                }
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
*/


    @Override
    public void onCategoryItemClick(int position) {
        Intent intent = new Intent(getApplicationContext(), Product.class);
        intent.putExtra("keyid", list.get(position).getKeyid());
        startActivity(intent);
    }
}


class SearchPlaceAdapter extends RecyclerView.Adapter<SearchPlaceAdapter.SearchPlaceAdapterViewHolder> implements Filterable {

    public ArrayList<Model_List_4> arrayList;
    public ArrayList<Model_List_4> arrayListFiltered;
    Context mCntx;
    private OnCategoryItemClickListener mListener;
    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Model_List_4> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(arrayListFiltered);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Model_List_4 item : arrayListFiltered) {
                    if (item.getName().toLowerCase().contains(filterPattern) || item.getCategory().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults results) {
            arrayList.clear();
            arrayList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public SearchPlaceAdapter(Context mCntx, ArrayList<Model_List_4> arrayList) {
        this.mCntx = mCntx;
        this.arrayList = arrayList;
        this.arrayListFiltered = new ArrayList<>(arrayList);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public SearchPlaceAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_grid, parent, false);

        SearchPlaceAdapterViewHolder viewHolder = new SearchPlaceAdapterViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(SearchPlaceAdapterViewHolder holder, final int position) {
        Model_List_4 med = arrayList.get(position);
        if (med != null) {
            Picasso.get()
                    .load(med.getImage())
                    .into(holder.image);
            holder.title.setText(UppercaseFirstLetter(med.getName()));
            holder.price.setText("₹ " + med.getAmount());
            holder.shimmer.setVisibility(View.GONE);
            holder.title.setVisibility(View.VISIBLE);
            holder.image.setVisibility(View.VISIBLE);
        }

    }

    public Filter getFilter() {
        return exampleFilter;
    }

    public void setOnItemClickListener(OnCategoryItemClickListener listener) {
        mListener = listener;
    }

    private String UppercaseFirstLetter(String newText) {
        String firstLetter = newText.substring(0, 1);
        String remainingLetters = newText.substring(1, newText.length());
        firstLetter = firstLetter.toUpperCase();
        newText = firstLetter + remainingLetters;
        return newText;
    }

    public interface OnCategoryItemClickListener {
        void onCategoryItemClick(int position);
    }

    public class SearchPlaceAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView image;
        TextView title, price;
        ShimmerFrameLayout shimmer;

        public SearchPlaceAdapterViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.title);
            shimmer = itemView.findViewById(R.id.shimmer);
            price = itemView.findViewById(R.id.price);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mListener.onCategoryItemClick(position);
                }
            }
        }

    }
}

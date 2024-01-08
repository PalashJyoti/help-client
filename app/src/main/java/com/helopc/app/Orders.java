package com.helopc.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.helopc.shopheaven.app.R;

import java.util.ArrayList;
import java.util.List;

public class Orders extends AppCompatActivity implements AdapterItem.OnCategoryItemClickListener{

    private AdapterItem adapterItem;
    private List<AmountWay> list;
    private RecyclerView recyclerView;
    private ValueEventListener valueEventListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("My Orders");
        recyclerView = findViewById(R.id.recyclerview);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(FirebaseAuth.getInstance().getUid()).child("Orders");

        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

     //   recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        list = new ArrayList<>();
        adapterItem = new AdapterItem(getApplicationContext(),list);
        recyclerView.setAdapter(adapterItem);
        adapterItem.setOnItemClickListener(Orders.this);
        valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    AmountWay upload = postSnapshot.getValue(AmountWay.class);
                    upload.setKey(postSnapshot.getKey());
                    list.add(upload);
                }
                adapterItem.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navig);
        Menu menu  = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case  R.id.nav_home:
                        finish();
                        startActivity(new Intent(Orders.this,HomeActivity.class));
                        break;
                    case  R.id.cart:
                        finish();
                        startActivity(new Intent(Orders.this,Cart.class));
                        break;
                    case  R.id.my_orders:
                        break;
                    case  R.id.account:
                        finish();
                        startActivity(new Intent(Orders.this,My_Account.class));
                        break;
                }

                return false;
            }
        });
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
    @Override
    public void onCategoryItemClick(int position) {
        String key = list.get(position).getKey();

        Intent intent = new Intent(getApplicationContext(), Ordered_Item.class);
        intent.putExtra("key", key);
        startActivity(intent);
    }
    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(Orders.this,HomeActivity.class));
    }
}

class AdapterItem extends RecyclerView.Adapter<AdapterItem.viewHolder> {

    private Context mContext;
    private List<AmountWay> mUploads;
    private OnCategoryItemClickListener  mListener;
    public AdapterItem(Context context, List<AmountWay> uploads) {
        mContext = context;
        mUploads = uploads;
    }
    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        AmountWay uploadCurrent = mUploads.get(position);
        holder.item.setText("₹"+uploadCurrent.getAmount());
        holder.time.setText(uploadCurrent.getDate());
    }
    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item,parent,false);
        return new viewHolder(view);

    }

    public class viewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView item,time;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.item);
            time = itemView.findViewById(R.id.time);

            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mListener.onCategoryItemClick(position);
                }
            }
        }
    }
    public interface OnCategoryItemClickListener {
        void onCategoryItemClick(int position);
    }
    public void setOnItemClickListener(OnCategoryItemClickListener  listener) {
        mListener = listener;
    }

}

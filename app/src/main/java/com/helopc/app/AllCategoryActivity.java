package com.helopc.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.helopc.shopheaven.app.R;
import com.helopc.shopheaven.app.databinding.ActivityAllCategoryBinding;

import java.util.ArrayList;
import java.util.List;

public class AllCategoryActivity extends AppCompatActivity implements AllCategoryAdapter.OnCategoryItemClickListener {

    ActivityAllCategoryBinding binding;
    private List<Model_List_3> list;
    private AllCategoryAdapter adapter_list;
    private ValueEventListener valueEventListener_1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityAllCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        DatabaseReference dbc= FirebaseDatabase.getInstance().getReference().child("Category");
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(),3, GridLayoutManager.VERTICAL, false));

        list = new ArrayList<>();
        adapter_list = new AllCategoryAdapter(getApplicationContext(), list);
        binding.recyclerView.setAdapter(adapter_list);

        adapter_list.setOnItemClickListener(AllCategoryActivity.this);
        valueEventListener_1 = dbc.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Model_List_3 upload = postSnapshot.getValue(Model_List_3.class);
                    upload.setKey(postSnapshot.getKey());
                    list.add(upload);
                }
                adapter_list.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public void onCategoryItemClick3(int position) {
        Intent intent = new Intent(getApplicationContext(), Product_List_Under_Category.class);
        intent.putExtra("category", list.get(position).getCategory());
        startActivity(intent);
    }
}
package com.helopc.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.helopc.shopheaven.app.R;
import com.helopc.shopheaven.app.databinding.ActivityHomeBinding;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener,
        Adapter_List_1.OnCategoryItemClickListener, Adapter_List_2.OnCategoryItemClickListener, Adapter_List_3.OnCategoryItemClickListener, Adapter_List_4.OnCategoryItemClickListener
        , SearchPlaceAdapter.OnCategoryItemClickListener, Adapter_List_5.OnCategoryItemClickListener {


    ActivityHomeBinding binding;
    NavigationView nav;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawerLayout;
    MaterialToolbar toolbar;
    SearchPlaceAdapter adapter;
    DatabaseReference dbcc;

    private Adapter_List_3 adapter_list_1;
    private List<Model_List_3> list_1;
    private ValueEventListener valueEventListener_1;
    private Adapter_List_2 adapter_list_2;
    private List<Model_List_2> list_2;
    private ValueEventListener valueEventListener_2;
    private Adapter_List_4 adapter_list_4;
    private List<Model_List_4> list_4;
    private ValueEventListener valueEventListener_4;
    private Adapter_List_5 adapter_list_5;
    private List<Model_List_5> list_5;
    private ValueEventListener valueEventListener_5;
    private ArrayList<Model_List_4> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        toolbar = findViewById(R.id.appBar);
        toolbar.setTitleCentered(true);
        setSupportActionBar(toolbar);

        nav = findViewById(R.id.navMenu);
        drawerLayout = findViewById(R.id.mainDrawer);

        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        binding.recyclerview1.setNestedScrollingEnabled(false);
        binding.recyclerview2.setNestedScrollingEnabled(false);
        binding.recyclerviewDeal.setNestedScrollingEnabled(false);
        binding.recyclerview4.setNestedScrollingEnabled(false);

        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.drawer_category:
                        startActivity(new Intent(getApplicationContext(),AllCategoryActivity.class));
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.drawer_prebuilt:
                        Intent intent = new Intent(getApplicationContext(), Product_List_Under_Category.class);
                        intent.putExtra("category", "Prebuilt pc");
                        startActivity(intent);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.drawer_customBuild:
                        startActivity(new Intent(getApplicationContext(),CustomPcActivity.class));
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.drawer_order:
                        startActivity(new Intent(getApplicationContext(), Orders.class));
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.drawer_cart:
                        startActivity(new Intent(getApplicationContext(), Cart.class));
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.drawer_wishlist:
                        Toast.makeText(HomeActivity.this, "wishlist clicked", Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.drawer_account:
                        startActivity(new Intent(getApplicationContext(), My_Account.class));
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.drawer_contact:
                        startActivity(new Intent(getApplicationContext(),ContactUs.class));
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.drawer_logout:
                        FirebaseAuth.getInstance().signOut();
                        finish();
                        startActivity(new Intent(getApplicationContext(), Activity_Login.class));
                        break;
                }
                return true;
            }
        });

        binding.searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                binding.search.setVisibility(View.INVISIBLE);
                binding.cancel.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    binding.search.setVisibility(View.INVISIBLE);
                    binding.cancel.setVisibility(View.VISIBLE);
                } else {
                    binding.search.setVisibility(View.VISIBLE);
                    binding.cancel.setVisibility(View.INVISIBLE);
                    if (adapter != null) {
                        adapter.getFilter().filter(s.toString());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    binding.search.setVisibility(View.INVISIBLE);
                    binding.cancel.setVisibility(View.VISIBLE);
                } else {
                    binding.search.setVisibility(View.VISIBLE);
                    binding.cancel.setVisibility(View.INVISIBLE);
                    if (adapter != null) {
                        adapter.getFilter().filter(s.toString());
                    }
                }
            }
        });

        dbcc = FirebaseDatabase.getInstance().getReference("Product");

        binding.productsList.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2, GridLayoutManager.VERTICAL, false);
        binding.productsList.setLayoutManager(gridLayoutManager);

        dbcc.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list = new ArrayList<>();
                for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                    list.add(eventSnapshot.getValue(Model_List_4.class));
                }
                adapter = new SearchPlaceAdapter(getApplicationContext(), list);
                adapter.setOnItemClickListener(HomeActivity.this);
                binding.productsList.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        binding.search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String SearchText = binding.searchText.getText().toString();
                if (SearchText.matches("")) {
                    binding.search.setVisibility(View.INVISIBLE);
                    binding.cancel.setVisibility(View.VISIBLE);
                } else {
                    binding.layout1.setVisibility(View.INVISIBLE);
                    binding.layout2.setVisibility(View.VISIBLE);
                    binding.layout1.setEnabled(false);
                    binding.layout2.setEnabled(true);
                }
            }
        });
        binding.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.cancel.setVisibility(View.INVISIBLE);
                binding.searchText.setVisibility(View.INVISIBLE);
                binding.appBarLayout.setVisibility(View.VISIBLE);
                binding.layout1.setVisibility(View.VISIBLE);
                binding.layout2.setVisibility(View.INVISIBLE);
                binding.layout1.setEnabled(true);
                binding.layout2.setEnabled(false);
            }
        });

        binding.dots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup(view);
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navig);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:

                        break;
                    case R.id.cart:
                        finish();
                        startActivity(new Intent(HomeActivity.this, Cart.class));
                        break;
                    case R.id.my_orders:
                        finish();
                        startActivity(new Intent(HomeActivity.this, Orders.class));
                        break;
                    case R.id.account:
                        finish();
                        startActivity(new Intent(HomeActivity.this, My_Account.class));
                        break;
                }
                return false;
            }
        });


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Category");

        binding.recyclerview1.setHasFixedSize(true);
        binding.recyclerview1.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        list_1 = new ArrayList<>();
        adapter_list_1 = new Adapter_List_3(getApplicationContext(), list_1);
        binding.recyclerview1.setAdapter(adapter_list_1);
        adapter_list_1.setOnItemClickListener(HomeActivity.this);
        valueEventListener_1 = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list_1.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    binding.shimmer1.setVisibility(View.GONE);
                    binding.recyclerview1.setVisibility(View.VISIBLE);
                    Model_List_3 upload = postSnapshot.getValue(Model_List_3.class);
                    upload.setKey(postSnapshot.getKey());
                    list_1.add(upload);
                }
                adapter_list_1.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("List_2");

        binding.recyclerview2.setHasFixedSize(true);
        binding.recyclerview2.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        list_2 = new ArrayList<>();
        adapter_list_2 = new Adapter_List_2(getApplicationContext(), list_2);
        binding.recyclerview2.setAdapter(adapter_list_2);
        adapter_list_2.setOnItemClickListener(HomeActivity.this);
        valueEventListener_2 = databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list_2.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    binding.shimmer2.setVisibility(View.GONE);
                    binding.recyclerview2.setVisibility(View.VISIBLE);
                    Model_List_2 upload = postSnapshot.getValue(Model_List_2.class);
                    upload.setKey(postSnapshot.getKey());
                    list_2.add(upload);
                }
                adapter_list_2.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        DatabaseReference databaseReference5 = FirebaseDatabase.getInstance().getReference("Deals");

        GridLayoutManager gridLayoutManagerD = new GridLayoutManager(getApplicationContext(), 3, GridLayoutManager.VERTICAL, false);

        binding.recyclerviewDeal.setHasFixedSize(true);
        binding.recyclerviewDeal.setLayoutManager(gridLayoutManagerD);

        list_5 = new ArrayList<>();
        adapter_list_5 = new Adapter_List_5(getApplicationContext(), list_5);
        binding.recyclerviewDeal.setAdapter(adapter_list_5);

        adapter_list_5.setOnItemClickListener(HomeActivity.this);
        valueEventListener_5 = databaseReference5.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list_5.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    binding.shimmerDeal.setVisibility(View.GONE);
                    binding.recyclerviewDeal.setVisibility(View.VISIBLE);
                    Model_List_5 upload = postSnapshot.getValue(Model_List_5.class);
//                    upload.setKey(postSnapshot.getKey());
                    list_5.add(upload);
                }
                adapter_list_5.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        DatabaseReference databaseReference4 = FirebaseDatabase.getInstance().getReference("Product");

        GridLayoutManager gridLayoutManagerP = new GridLayoutManager(getApplicationContext(), 3, GridLayoutManager.VERTICAL, false);

        binding.recyclerview4.setHasFixedSize(true);
        binding.recyclerview4.setLayoutManager(gridLayoutManagerP);

        list_4 = new ArrayList<>();
        adapter_list_4 = new Adapter_List_4(getApplicationContext(), list_4);
        binding.recyclerview4.setAdapter(adapter_list_4);

        adapter_list_4.setOnItemClickListener(HomeActivity.this);
        valueEventListener_4 = databaseReference4.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list_4.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    binding.shimmer4.setVisibility(View.GONE);
                    binding.recyclerview4.setVisibility(View.VISIBLE);
                    Model_List_4 upload = postSnapshot.getValue(Model_List_4.class);
                    upload.setKey(postSnapshot.getKey());
                    list_4.add(upload);
                }
                adapter_list_4.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (binding.layout1.isEnabled()) {
            finish();
        } else {
            binding.layout1.setVisibility(View.VISIBLE);
            binding.layout2.setVisibility(View.INVISIBLE);
            binding.layout1.setEnabled(true);
            binding.layout2.setEnabled(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.opmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.searchBtn) {
            binding.appBarLayout.setVisibility(View.INVISIBLE);
            binding.searchText.setVisibility(View.VISIBLE);

            binding.cancel.setVisibility(View.VISIBLE);
        }
        return super.onOptionsItemSelected(item);
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.inflate(R.menu.opmenu);
        popup.show();
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        return super.onContextItemSelected(item);
    }


    @Override
    public void onCategoryItemClick1(int position) {
        Intent intent = new Intent(getApplicationContext(), Product_List_Under_Category.class);
        intent.putExtra("category", list_1.get(position).getCategory());
        startActivity(intent);
    }

    @Override
    public void onCategoryItemClick2(int position) {
        if (list_2.get(position).getType().toLowerCase().equals("category")) {
            Intent intent = new Intent(getApplicationContext(), Product_List_Under_Category.class);
            intent.putExtra("category", list_2.get(position).getCategory());
            startActivity(intent);
        } else {
            Intent intent = new Intent(getApplicationContext(), Product.class);
            intent.putExtra("keyid", list_2.get(position).getKeyid());
            startActivity(intent);
        }
    }

    @Override
    public void onCategoryItemClick3(int position) {
        Intent intent = new Intent(getApplicationContext(), Product_List_Under_Category.class);
        intent.putExtra("category", list_1.get(position).getCategory());
        startActivity(intent);
    }

    @Override
    public void onCategoryItemClick4(int position) {
        Intent intent = new Intent(getApplicationContext(), Product.class);
        intent.putExtra("keyid", list_4.get(position).getKeyid());
        startActivity(intent);
    }

    @Override
    public void onCategoryItemClick(int position) {
        Intent intent = new Intent(getApplicationContext(), Product.class);
        intent.putExtra("keyid", list.get(position).getKeyid());
        startActivity(intent);
    }

    @Override
    public void onCategoryItemClick5(int position) {
        Intent intent = new Intent(getApplicationContext(), Product.class);
        intent.putExtra("keyid", list_5.get(position).getKeyid());
        startActivity(intent);
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        return false;
    }
}



package com.omelchenkoaleks.recyclermultiview;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.omelchenkoaleks.recyclermultiview.adapter.MyAdapter;
import com.omelchenkoaleks.recyclermultiview.model.Item;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    RecyclerView mList;
    RecyclerView.LayoutManager mLayoutManager;
    List<Item> mItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mList = findViewById(R.id.recycler_view_rv);
        mList.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mList.setLayoutManager(mLayoutManager);

        setData();
    }

    private void setData() {
        for (int i = 0; i < 20; i++) {
            if (i % 2 == 0) {
                Item item = new Item("This is item " + (i + 1),
                        "This is child item " + (i + 1), true);
                mItems.add(item);
            } else {
                Item item = new Item("This is item" + (i + 1),
                        "", false);
                mItems.add(item);
            }
        }

        MyAdapter myAdapter = new MyAdapter(mItems);
        mList.setAdapter(myAdapter);
    }
}

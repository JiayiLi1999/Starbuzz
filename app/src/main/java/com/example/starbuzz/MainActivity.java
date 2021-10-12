package com.example.starbuzz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    SQLiteDatabase db;
    Cursor favoriteCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupOptionListView();
        setupFavoriteView();
    }

    private void setupOptionListView() {
        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    Intent intent = new Intent(MainActivity.this,DrinkCategoryActivity.class);
                    startActivity(intent);
                }
            }
        };

        ListView listView = findViewById(R.id.list);
        listView.setOnItemClickListener(itemClickListener);
    }

    private void setupFavoriteView() {
        ListView favoriteView = findViewById(R.id.favorite_list);
        try{
            SQLiteOpenHelper starbuzzDatabaseHelper = new StarbuzzDatabaseHelper(this);
            db = starbuzzDatabaseHelper.getReadableDatabase();
            favoriteCursor = db.query("DRINK",new String[]{"_id","NAME"},"FAVORITE = 1",null,null,null,null);
            CursorAdapter favoriteAdapter = new SimpleCursorAdapter(MainActivity.this,android.R.layout.simple_list_item_1,favoriteCursor,new String[]{"NAME"},new int[]{android.R.id.text1},0);
            favoriteView.setAdapter(favoriteAdapter);
        }catch (SQLException e){
            Toast toast = Toast.makeText(this,"database unavailable",Toast.LENGTH_SHORT);
            toast.show();
        }
        favoriteView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this,DrinkActivity.class);
                intent.putExtra(DrinkActivity.EXTRA_DRINKID,(int)id);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Cursor newCursor = db.query("DRINK",new String[]{"_id","NAME"},"FAVORITE = 1",null,null,null,null);
        ListView favoriteView = findViewById(R.id.favorite_list);
        CursorAdapter adapter = (CursorAdapter) favoriteView.getAdapter();
        adapter.changeCursor(newCursor);
        favoriteCursor = newCursor;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        favoriteCursor.close();
        db.close();
    }
}
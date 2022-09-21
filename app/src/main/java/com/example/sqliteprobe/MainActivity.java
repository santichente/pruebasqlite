package com.example.sqliteprobe;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.CaseMap;
import android.provider.BaseColumns;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private String entrada, salida;
    private FeedReaderDbHelper dbHelper;
    TextView salidaTextView;
    EditText entradaEditText;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        entradaEditText = (EditText) findViewById(R.id.editTextTextPersonName);
        dbHelper = new FeedReaderDbHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        //dbHelper.onCreate(db);
        salidaTextView = (TextView) findViewById(R.id.textView);

    }

    public void LeerEscribir(View view) {
        String entrada = entradaEditText.getText().toString();
        Entry e = new Entry();
        e.Title = entrada;
        InsertRegister(e);
        Entry s = ReadRegister(count);
        count++;
        salidaTextView.setText(s.Title);

    }

    public void InsertRegister(Entry entry) {
// Gets the data repository in write mode
        SQLiteDatabase db = dbHelper.getWritableDatabase();

// Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE, entry.Title);
        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE, entry.Subtitle);

// Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(FeedReaderContract.FeedEntry.TABLE_NAME, null, values);
    }

    public boolean DeleteRegister(int Id) {
        try {
            // Gets the data repository in write mode
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            // Define 'where' part of query.
            String selection = FeedReaderContract.FeedEntry._ID + " = ?";
            String[] selectionArgs = {String.valueOf(Id)};
            // Issue SQL statement.
            int deletedRows = db.delete(FeedReaderContract.FeedEntry.TABLE_NAME, selection, selectionArgs);
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    public boolean UpdateRegister(Entry actual) {
        try {
            // Gets the data repository in write mode
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE, actual.Title);
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE, actual.Subtitle);
            // Which row to update, based on the title
            String selection = FeedReaderContract.FeedEntry._ID + " = ?";
            String[] selectionArgs = {String.valueOf(actual.Id)};
            // Insert the new row, returning the primary key value of the new row
            long newRowId = db.update(FeedReaderContract.FeedEntry.TABLE_NAME, values, selection,
                    selectionArgs);

        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public Entry ReadRegister(int Id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                BaseColumns._ID,
                FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE,
                FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = BaseColumns._ID + " = ?";
        String[] selectionArgs = {"" + Id};

        // How you want the results sorted in the resulting Cursor
        String sortOrder = FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE + " DESC";
        Entry res = new Entry();
        Cursor cursor = db.query(
                FeedReaderContract.FeedEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );
        List itemIds = new ArrayList<>();
        while (cursor.moveToNext()) {
            long itemId = cursor.getLong(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry._ID));
            if (itemId > 0) {
                res.Id = (int) itemId;
                res.Title = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE));
                res.Subtitle = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE));
                break;
            }
            itemIds.add(itemId);
        }
        cursor.close();
        return res;
    }
}

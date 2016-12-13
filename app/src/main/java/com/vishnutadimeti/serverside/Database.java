package com.vishnutadimeti.serverside;

/**
 * Created by vishnutadimeti on 11/30/16.
 */

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

// Initializing a new SQL Database

class Database extends SQLiteOpenHelper {

    private static final int databaseVersion = 3;
    private static final String databaseName = "trivia";
    private static final String leaderboardTable = "leaderboard";
    private static final String leaderboardName = "";
    private static final String leaderboardScore = "";

    Database(Context context) {
        super(context, databaseName, null, databaseVersion);
    }

    // Method for creating a new SQL Table
    
    @Override
    public void onCreate(SQLiteDatabase sqldb) {
        String createTable = "create table " + leaderboardTable + "(" +
                leaderboardScore + " number, " + leaderboardName +  " text" + ")";
        sqldb.execSQL(createTable);
    }

    // Method to drop the table
    
    @Override
    public void onUpgrade(SQLiteDatabase sqldb, int i, int i1) {
        sqldb.execSQL("DROP TABLE IF EXISTS "+ leaderboardTable);
        onCreate(sqldb);
    }
    
    // Method to add a record to the table

    void addRecord(int score, String username) {
        String uname = "'" + username + "'";
        this.getWritableDatabase().execSQL("insert into " + leaderboardTable + " values (" + score + ", " + uname + ")");
    }

    // Method to read the Database using Cursor
    
    public void getRecord() {
        Cursor cur = this.getWritableDatabase().rawQuery("select * from leaderboard", new String [] {});
        if (cur.moveToFirst()) {
            while (!cur.isAfterLast()) {

                int count = cur.getColumnCount();
                String test = "";
                for (int i = 0; i < count; i++) {
                    test = test + cur.getString(i);
                }
                Log.d("Database: ", test);
                cur.moveToNext();
            }
        }
        cur.close();
    }

    // JSON Array to get the results from the database, to send data to the client. 
    
    JSONArray getResults() {
        Cursor cursor = this.getReadableDatabase().rawQuery("select * from leaderboard order by number desc", new String [] {});
        JSONArray resultSet = new JSONArray();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {

            int totalColumn = cursor.getColumnCount();
            JSONObject rowObject = new JSONObject();

            for (int i = 0 ; i < totalColumn ; i++) {
                if( cursor.getColumnName(i) != null ) {
                    try
                    {
                        if ( cursor.getString(i) != null ) {
                            Log.d("Getting String: ", cursor.getString(i) );
                            rowObject.put(cursor.getColumnName(i) ,  cursor.getString(i) );
                        }
                        else {
                            rowObject.put( cursor.getColumnName(i) ,  "" );
                        }
                    }
                    catch (Exception e)
                    {
                        Log.d("Wrong: ", e.getMessage());
                    }
                }
            }
            resultSet.put(rowObject);
            cursor.moveToNext();
        }
        cursor.close();
        Log.d("Result", resultSet.toString());
        return resultSet;
    }
}




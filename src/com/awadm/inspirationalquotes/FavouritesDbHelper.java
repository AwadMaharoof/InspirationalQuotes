package com.awadm.inspirationalquotes;

import com.awadm.inspirationalquotes.FavouriteContract.FeedEntry;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FavouritesDbHelper extends SQLiteOpenHelper {

	private static final String TEXT_TYPE = " TEXT";
	private static final String COMMA_SEP = ",";
	private static final String SQL_CREATE_ENTRIES = "CREATE TABLE "
			+ FeedEntry.TABLE_NAME + " (" + FeedEntry._ID
			+ " INTEGER PRIMARY KEY," + FeedEntry.COLUMN_NAME_QUOTE + TEXT_TYPE
			+ COMMA_SEP + FeedEntry.COLUMN_NAME_AUTHOR + TEXT_TYPE + " )";

	private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "
			+ FeedEntry.TABLE_NAME;
	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "InspirationalQuotes.db";

	public FavouritesDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_ENTRIES);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(SQL_DELETE_ENTRIES);
		onCreate(db);
	}

	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onUpgrade(db, oldVersion, newVersion);
	}

}

package com.awadm.inspirationalquotes;

import android.provider.BaseColumns;

public final class FavouriteContract {
	/* Inner class that defines the table contents */
	public static abstract class FeedEntry implements BaseColumns {
		public static final String TABLE_NAME = "favourites";
		public static final String COLUMN_NAME_QUOTE = "quote";
		public static final String COLUMN_NAME_AUTHOR = "author";
	}
}

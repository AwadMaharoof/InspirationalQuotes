package com.awadm.inspirationalquotes;

import java.util.ArrayList;
import java.util.List;

import com.awadm.inspirationalquotes.FavouriteContract.FeedEntry;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class FavouriteActivity extends Activity implements OnClickListener {

	TextView quote;
	TextView author;
	Button refresh;
	Button prev;
	List<Quote> quotes;
	int quoteIndex;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fav_activity);
		findViews();
		quotes = new ArrayList<>();
		fetchQuotes();

		if (!quotes.isEmpty()) {
			quoteIndex = 0;
			setQuote(quotes.get(quoteIndex));
		} else {
			// no favorites
			quote.setText("You dont seem to have favorited any quotes :)");
		}

	}

	private void fetchQuotes() {
		FavouritesDbHelper mDbHelper = new FavouritesDbHelper(this);
		SQLiteDatabase db = mDbHelper.getReadableDatabase();

		String[] projection = { FeedEntry._ID, FeedEntry.COLUMN_NAME_QUOTE,
				FeedEntry.COLUMN_NAME_AUTHOR, };

		String sortOrder = FeedEntry._ID + " DESC";

		Cursor c = db.query(FeedEntry.TABLE_NAME, // The table to query
				projection, // The columns to return
				null, // The columns for the WHERE clause
				null, // The values for the WHERE clause
				null, // don't group the rows
				null, // don't filter by row groups
				sortOrder // The sort order
				);

		startManagingCursor(c);
		while (c.moveToNext()) {
			Quote q = new Quote();
			q.quote = c.getString(1);
			q.author = c.getString(2);
			quotes.add(q);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("Browse quotes");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch ((String) item.getTitle()) {
		case "Browse quotes":
			finish();
			super.onBackPressed();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.refresh:
			next();
			break;
		case R.id.previous:
			prev();
			break;
		}
	}

	private void next() {
		if (quotes.size() > quoteIndex + 1)
			setQuote(quotes.get(++quoteIndex));
	}

	private void prev() {
		if (1 < quoteIndex + 1)
			setQuote(quotes.get(--quoteIndex));
	}

	private void setQuote(Quote quoteObj) {
		quote.setText(quoteObj.quote);
		author.setText(quoteObj.author);
	}

	private void findViews() {
		Typeface custom_font = Typeface.createFromAsset(getAssets(),
				"fonts/BebasNeue.otf");
		quote = (TextView) findViewById(R.id.quote_text);
		quote.setTypeface(custom_font);
		author = (TextView) findViewById(R.id.author_text);
		author.setTypeface(custom_font);

		// NEXT
		refresh = (Button) findViewById(R.id.refresh);
		refresh.setOnClickListener(this);
		refresh.setTypeface(custom_font);

		// PREV
		prev = (Button) findViewById(R.id.previous);
		prev.setOnClickListener(this);
		prev.setTypeface(custom_font);
	}
}

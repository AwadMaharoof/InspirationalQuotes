package com.awadm.inspirationalquotes;

import java.util.ArrayList;
import java.util.List;

import com.awadm.inspirationalquotes.FavouriteContract.FeedEntry;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
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
	Button delete;
	List<Quote> quotes;
	int quoteIndex;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fav_activity);
		findViews();
		quotes = new ArrayList<Quote>();
		fetchQuotes();

		if (quotes.isEmpty()) {
			// no favorites
			quotesEmpty();
		} else {
			quoteIndex = 0;
			setQuote(quotes.get(quoteIndex));
		}
	}

	private void quotesEmpty() {
		delete.setEnabled(false);
		quote.setText("You dont seem to have favorited any quotes :)");
		author.setText("");
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
			q.id = c.getString(0);
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
		case R.id.delete:
			removeQuote();
			break;
		}
	}

	private void removeQuote() {
		if (!quotes.isEmpty()) {
			Quote toDelete = quotes.get(quoteIndex);
			FavouritesDbHelper mDbHelper = new FavouritesDbHelper(this);
			SQLiteDatabase db = mDbHelper.getReadableDatabase();

			db.delete(FeedEntry.TABLE_NAME, FeedEntry._ID + "=?",
					new String[] { toDelete.id });

			quotes.remove(quoteIndex--); // remove quote from current list

			if (quotes.isEmpty()) {
				// no more quotes
				quotesEmpty();
			} else {
				// we removed the last item in the list, so set the first quote
				if (quoteIndex + 1 == quotes.size()) {
					setQuote(quotes.get(quoteIndex = 0));
				} else {
					next(); // load next quote to view
				}
			}
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
		setRandomBackGround();
	}

	private void findViews() {
		Typeface custom_font = Typeface.createFromAsset(getAssets(),
				"fonts/BebasNeue.otf");
		Typeface icon_font = Typeface.createFromAsset(getAssets(),
				"fonts/fontawesome-webfont.ttf");
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

		// delete
		delete = (Button) findViewById(R.id.delete);
		delete.setOnClickListener(this);
		delete.setTypeface(icon_font);
	}

	private void setRandomBackGround() {
		View layout = findViewById(R.id.main_bg);

		GradientDrawable gd = new GradientDrawable(
				GradientDrawable.Orientation.TOP_BOTTOM,
				Utils.randomGradient(getResources()));
		gd.setCornerRadius(0f);

		layout.setBackgroundDrawable(gd);
	}
}

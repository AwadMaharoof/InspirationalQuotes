package com.awadm.inspirationalquotes;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import com.awadm.inspirationalquotes.FavouriteContract.FeedEntry;
import android.R.color;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class QuoteActivity extends Activity implements OnClickListener {

	TextView quote;
	TextView author;
	Button refresh;
	Button share;
	Button favourite;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.quote_activity); // set view
		findViews();
		setRandomQuote();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("View favourites");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch ((String) item.getTitle()) {
		case "View favourites":
			startFavActivity();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.refresh:
			setRandomQuote();
			break;
		case R.id.share:
			Intent sharingIntent = new Intent(
					android.content.Intent.ACTION_SEND);
			sharingIntent.setType("text/plain");
			String shareBody = (String) quote.getText() + " - "
					+ (String) author.getText();
			sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
					"Inspirational Quotes");
			sharingIntent
					.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
			startActivity(Intent.createChooser(sharingIntent, "Share via"));
			break;
		case R.id.fav:
			saveQuote();
			break;
		}
	}

	private void saveQuote() {
		FavouritesDbHelper mDbHelper = new FavouritesDbHelper(this);
		SQLiteDatabase db = mDbHelper.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(FeedEntry.COLUMN_NAME_QUOTE, (String) quote.getText());
		values.put(FeedEntry.COLUMN_NAME_AUTHOR, (String) author.getText());

		db.insert(FeedEntry.TABLE_NAME, null, values);
		// disable share button so users cant favorite over and over
		favourite.setEnabled(false);
		favourite.setTextColor(getResources().getColor(color.holo_red_light));
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

		refresh = (Button) findViewById(R.id.refresh);
		refresh.setOnClickListener(this);
		refresh.setTypeface(custom_font);

		favourite = (Button) findViewById(R.id.fav);
		favourite.setOnClickListener(this);
		favourite.setTypeface(icon_font);

		share = (Button) findViewById(R.id.share);
		share.setTypeface(icon_font);
		share.setOnClickListener(this);
	}

	private void setRandomQuote() {
		// check connectivity
		if (isNetworkAvailable()) {
			new RetrieveFeedTask().execute();
		} else {
			new AlertDialog.Builder(this)
					.setTitle("No connectivity")
					.setMessage(
							"We noticed that you are not connected to tne internet. Please connect and try again")
					.setPositiveButton("Exit",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									finish();
								}
							})
					.setNegativeButton("View favorites",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									startFavActivity();
									finish();
								}
							}).setIcon(android.R.drawable.ic_dialog_alert)
					.show();
		}
	}

	private void setRandomBackGround() {
		View layout = findViewById(R.id.main_bg);

		GradientDrawable gd = new GradientDrawable(
				GradientDrawable.Orientation.TOP_BOTTOM,
				Utils.randomGradient(getResources()));
		gd.setCornerRadius(0f);

		layout.setBackgroundDrawable(gd);
	}

	private void toggleFavBtn(Boolean isEnabled) {
		favourite.setEnabled(isEnabled);
	}

	class RetrieveFeedTask extends AsyncTask<String, Integer, String> {
		protected String doInBackground(String... progress) {
			try {
				HttpClient client = new DefaultHttpClient();
				// The API service URL
				String getURL = "http://api.forismatic.com/api/1.0/?method=getQuote&lang=en&format=text";
				HttpGet get = new HttpGet(getURL);
				HttpResponse responseGet = client.execute(get);
				HttpEntity resEntityGet = responseGet.getEntity();

				if (resEntityGet != null) {
					return EntityUtils.toString(resEntityGet);
				} else {
					toggleFavBtn(false);
					return "We could not retrieve your quote :( Please tap next to retry";
				}
			} catch (Exception e) {
				e.printStackTrace();
				toggleFavBtn(false);
				return "We ran into a tiny problem :( Please tap next to retry";
			}
		}

		protected void onPostExecute(String quoteText) {
			// extract the quote author
			String[] a = quoteText.split("\\s(?=\\()|(?<=\\()\\s");
			quote.setText(a[0]); // first index is always the quote
			if (a.length == 2) // some quotes dont have authors
				author.setText(a[1].replace('(', ' ').replace(')', ' '));
			// enable favorite after each quote is set
			toggleFavBtn(true);
			favourite.setTextColor(getResources().getColor(color.white));
			setRandomBackGround();
		}
	}

	public boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null
				&& activeNetworkInfo.isConnectedOrConnecting();
	}

	private void startFavActivity() {
		Intent intent = new Intent(this, FavouriteActivity.class);
		startActivity(intent);
	}
}

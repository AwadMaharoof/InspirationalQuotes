package com.awadm.inspirationalquotes;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class QuoteActivity extends Activity implements OnClickListener {

	TextView quote;
	Button refresh;
	Button share;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.quote_activity); // set view
		findViews(); // find view from layout
		setRandomQuote();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.refresh:
			setRandomQuote();
			break;
		case R.id.share:
			Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
			sharingIntent.setType("text/plain");
			String shareBody = (String) quote.getText();
			sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Inspirational Quotes");
			sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
			startActivity(Intent.createChooser(sharingIntent, "Share via"));
			break;
		}
	}

	private void findViews() {
		quote = (TextView) findViewById(R.id.quote_text);
		refresh = (Button) findViewById(R.id.refresh);
		refresh.setOnClickListener(this);

		share = (Button) findViewById(R.id.share);
		share.setOnClickListener(this);
	}

	private void setRandomQuote() {
		new RetrieveFeedTask().execute();
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
					// do something with the response
					String response = EntityUtils.toString(resEntityGet);
					return response;

				} else {
					return "null reponse";
				}
			} catch (Exception e) {
				e.printStackTrace();
				return "";
			}
		}

		protected void onProgressUpdate(Integer... progress) {
			quote.setText("Loading " + progress + "%");
		}

		protected void onPostExecute(String quoteText) {
			quote.setText("\"" + quoteText + "\"");
		}
	}
}

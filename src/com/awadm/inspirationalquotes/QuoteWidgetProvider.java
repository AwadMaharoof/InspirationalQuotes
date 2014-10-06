package com.awadm.inspirationalquotes;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.RemoteViews;

public class QuoteWidgetProvider extends AppWidgetProvider {

	RemoteViews remoteViews;

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {

		RemoteViews remoteViews;
		ComponentName watchWidget;

		remoteViews = new RemoteViews(context.getPackageName(),
				R.layout.widget_layout);
		watchWidget = new ComponentName(context, QuoteWidgetProvider.class);

		// trigger fetch from the server!
		RetrieveFeedTask fetchTask = new RetrieveFeedTask();
		fetchTask.appWidgetManager = appWidgetManager;
		fetchTask.remoteViews = remoteViews;
		fetchTask.watchWidget = watchWidget;
		fetchTask.execute("");
	}
}

class RetrieveFeedTask extends AsyncTask<String, Integer, String> {
	public ComponentName watchWidget;
	public RemoteViews remoteViews;
	public AppWidgetManager appWidgetManager;

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
				return "We could not retrieve your quote :( Please tap next to retry";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "We ran into a tiny problem :( Please tap next to retry";
		}
	}

	protected void onPostExecute(String quoteText) {
		// extract the quote author
		String[] a = quoteText.split("\\s(?=\\()|(?<=\\()\\s");
		remoteViews.setTextViewText(R.id.quote_text, a[0]);
		if (a.length == 2) // some quotes dont have authors
			remoteViews.setTextViewText(R.id.author_text, a[1]
					.replace('(', ' ').replace(')', ' '));

		appWidgetManager.updateAppWidget(watchWidget, remoteViews);
	}

}

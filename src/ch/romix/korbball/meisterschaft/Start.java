package ch.romix.korbball.meisterschaft;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class Start extends Activity {

	private static final int NO_DATA_MSG = 0;
	private static final String GROUP_NAME = "name";
	private static final String GROUP_ID = "id";
	private List<Map<String, String>> groupsByGroupId;
	private SimpleAdapter simpleAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_start);
		ListView listleague = (ListView) findViewById(R.id.listLeague);
		groupsByGroupId = new LinkedList<Map<String, String>>();
		simpleAdapter = new SimpleAdapter(this, groupsByGroupId, android.R.layout.simple_list_item_1, new String[] { GROUP_NAME },
				new int[] { android.R.id.text1 });
		listleague.setAdapter(simpleAdapter);
		listleague.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent myIntent = new Intent(view.getContext(), RankingActivity.class);
				Map<String, String> map = groupsByGroupId.get((int) id);
				myIntent.putExtra(RankingActivity.INTENT_GROUP_ID, map.get(GROUP_ID));
				myIntent.putExtra(RankingActivity.INTENT_GROUP_NAME, map.get(GROUP_NAME));
				startActivity(myIntent);
			}
		});
		Handler exceptionHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case NO_DATA_MSG:
					Builder builder = new AlertDialog.Builder(Start.this);
					builder.setMessage(R.string.noConnection);
					builder.setCancelable(true);
					builder.create().show();
				}
			}
		};
		new GetGroupsTask(exceptionHandler).execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(ch.romix.korbball.meisterschaft.R.menu.activity_start, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_info) {
			Intent intent = new Intent(this, Info.class);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private class GetGroupsTask extends AsyncTask<Void, Void, List<Map<String, String>>> {
		private final Handler exceptionHandler;

		public GetGroupsTask(Handler exceptionHandler) {
			this.exceptionHandler = exceptionHandler;
		}

		@Override
		protected List<Map<String, String>> doInBackground(Void... params) {
			groupsByGroupId.clear();
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(UrlConsts.GROUPS);
			HttpResponse response;
			try {
				response = httpclient.execute(httppost);
				String jsonResult = new StreamReader().inputStreamToString(response.getEntity().getContent()).toString();
				JSONObject object = new JSONObject(jsonResult);
				@SuppressWarnings("rawtypes")
				Iterator it = object.keys();
				while (it.hasNext()) {
					String groupId = it.next().toString();
					String groupName = object.get(groupId).toString();
					HashMap<String, String> map = new HashMap<String, String>();
					map.put(GROUP_ID, groupId);
					map.put(GROUP_NAME, groupName);
					groupsByGroupId.add(map);
					Collections.sort(groupsByGroupId, new Comparator<Map<String, String>>() {
						@Override
						public int compare(Map<String, String> lhs, Map<String, String> rhs) {
							return lhs.get(GROUP_NAME).compareTo(rhs.get(GROUP_NAME));
						}
					});
				}
			} catch (ClientProtocolException e) {
			} catch (IOException e) {
			} catch (JSONException e) {
				Message msg = new Message();
				msg.what = NO_DATA_MSG;
				exceptionHandler.sendMessage(msg);
			}
			return groupsByGroupId;
		}

		@Override
		protected void onPostExecute(List<Map<String, String>> result) {
			if (result != null) {
				simpleAdapter.notifyDataSetChanged();
			}
			super.onPostExecute(result);
		}
	}
}

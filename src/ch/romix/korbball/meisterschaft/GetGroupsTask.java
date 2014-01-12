package ch.romix.korbball.meisterschaft;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.BaseAdapter;

public class GetGroupsTask extends AsyncTask<Void, Void, List<Map<String, String>>> {
	private final List<Map<String, String>> groupsByGroupId;
	private final BaseAdapter adapter;
	private final Context context;
	private Exception exception;
	private CountDownLatch finishedDrawingSignal;

	public GetGroupsTask(List<Map<String, String>> groupsByGroupId, BaseAdapter adapter, Context context) {
		this.groupsByGroupId = groupsByGroupId;
		this.adapter = adapter;
		this.context = context;
		finishedDrawingSignal = new CountDownLatch(1);
	}

	@Override
	protected List<Map<String, String>> doInBackground(Void... params) {
		final List<Map<String, String>> result = new ArrayList<Map<String, String>>();
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(UrlConsts.GROUPS);
		HttpResponse response;
		try {
			response = httpclient.execute(httppost);
			String jsonResult = StreamTools.inputStreamToString(response.getEntity().getContent()).toString();
			JSONObject object = new JSONObject(jsonResult);
			@SuppressWarnings("rawtypes")
			Iterator it = object.keys();
			while (it.hasNext()) {
				String groupId = it.next().toString();
				String groupName = object.get(groupId).toString();
				HashMap<String, String> map = new HashMap<String, String>();
				map.put(Start.GROUP_ID, groupId);
				map.put(Start.GROUP_NAME, groupName);
				result.add(map);
			}
			Collections.sort(result, new Comparator<Map<String, String>>() {
				@Override
				public int compare(Map<String, String> lhs, Map<String, String> rhs) {
					return lhs.get(Start.GROUP_NAME).compareTo(rhs.get(Start.GROUP_NAME));
				}
			});
		} catch (ClientProtocolException e) {
			setException(e);
		} catch (IOException e) {
			setException(e);
		} catch (JSONException e) {
			setException(e);
		}
		return result;
	}

	private void setException(Exception exception) {
		this.exception = exception;
	}

	@Override
	protected void onPostExecute(List<Map<String, String>> result) {
		groupsByGroupId.clear();
		if (hasException()) {
			Builder builder = new AlertDialog.Builder(context);
			builder.setMessage(R.string.noConnection);
			builder.setCancelable(true);
			builder.create().show();
		} else if (result != null) {
			for (Map<String, String> map : result) {
				groupsByGroupId.add(map);
			}
			adapter.notifyDataSetChanged();
		}
		super.onPostExecute(result);
		finishedDrawingSignal.countDown();
	}

	public void waitForDrawing() throws InterruptedException {
		finishedDrawingSignal.await();
	}

	private boolean hasException() {
		return exception != null;
	}
}
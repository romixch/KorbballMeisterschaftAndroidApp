package ch.romix.korbball.meisterschaft.groups;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.BaseAdapter;
import ch.romix.korbball.meisterschaft.R;
import ch.romix.korbball.meisterschaft.StreamTools;
import ch.romix.korbball.meisterschaft.UrlConsts;

public class GetGroupsTask extends AsyncTask<Void, Void, List<Group>> {
	private final List<Group> groups;
	private final BaseAdapter adapter;
	private final Context context;
	private Exception exception;

	public GetGroupsTask(List<Group> groups, BaseAdapter adapter, Context context) {
		this.groups = groups;
		this.adapter = adapter;
		this.context = context;
	}

	@Override
	protected List<Group> doInBackground(Void... params) {
		final List<Group> result = new ArrayList<Group>();
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpGET = new HttpGet(UrlConsts.GROUPS);
		HttpResponse response;
		try {
			response = httpclient.execute(httpGET);
			String jsonResult = StreamTools.inputStreamToString(response.getEntity().getContent()).toString();
			JSONObject object = new JSONObject(jsonResult);
			@SuppressWarnings("rawtypes")
			Iterator it = object.keys();
			while (it.hasNext()) {
				String groupId = it.next().toString();
				String groupName = object.get(groupId).toString();
				Group group = new Group(groupId, groupName);
				result.add(group);
			}
			Collections.sort(result, new Comparator<Group>() {
				@Override
				public int compare(Group lhs, Group rhs) {
					return lhs.getGroupName().compareTo(rhs.getGroupName());
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
	protected void onPostExecute(List<Group> result) {
		groups.clear();
		if (hasException()) {
			Builder builder = new AlertDialog.Builder(context);
			builder.setMessage(R.string.noConnection);
			builder.setCancelable(true);
			builder.create().show();
		} else if (result != null) {
			for (Group group : result) {
				groups.add(group);
			}
			adapter.notifyDataSetChanged();
		}
		super.onPostExecute(result);
	}

	private boolean hasException() {
		return exception != null;
	}
}
package ch.romix.korbball.meisterschaft;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class Start extends Activity {

	static final String GROUP_NAME = "name";
	static final String GROUP_ID = "id";
	private List<Map<String, String>> groupsByGroupId;
	private SimpleAdapter simpleAdapter;
	private GetGroupsTask getGroupsTask;

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
		Button nextFeature = (Button) findViewById(R.id.nextFeature);
		nextFeature.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent pollIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://romixch.typeform.com/to/r3ELGk"));
				startActivity(pollIntent);
			}
		});
		getGroupsTask = new GetGroupsTask(groupsByGroupId, simpleAdapter, this);
		getGroupsTask.execute();
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

	public void waitForGroups() throws InterruptedException, ExecutionException {
		getGroupsTask.waitForDrawing();
	}
}

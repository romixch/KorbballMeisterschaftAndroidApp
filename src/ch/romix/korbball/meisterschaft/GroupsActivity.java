package ch.romix.korbball.meisterschaft;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class GroupsActivity extends Activity {

	static final String GROUP_NAME = "name";
	static final String GROUP_ID = "id";
	private List<Map<String, String>> groupsByGroupId;
	private SimpleAdapter simpleAdapter;
	private GetGroupsTask getGroupsTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.groups);

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
		getGroupsTask = new GetGroupsTask(groupsByGroupId, simpleAdapter, this);
		getGroupsTask.execute();
	}
}

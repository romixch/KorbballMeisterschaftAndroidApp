package ch.romix.korbball.meisterschaft.groups;

import java.util.LinkedList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import ch.romix.korbball.meisterschaft.R;
import ch.romix.korbball.meisterschaft.ranking.RankingActivity;

public class GroupsActivity extends Activity {

	static final String GROUP_NAME = "name";
	static final String GROUP_ID = "id";
	private LinkedList<Group> groups;
	private GroupAdapter groupAdapter;
	private GetGroupsTask getGroupsTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.groups);

		ListView listleague = (ListView) findViewById(R.id.listLeague);
		groups = new LinkedList<Group>();
		groupAdapter = new GroupAdapter(getApplicationContext(), groups);
		listleague.setAdapter(groupAdapter);
		listleague.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent myIntent = new Intent(view.getContext(), RankingActivity.class);
				Group group = groups.get(position);
				myIntent.putExtra(RankingActivity.INTENT_GROUP_ID, group.getGroupId());
				myIntent.putExtra(RankingActivity.INTENT_GROUP_NAME, group.getGroupName());
				startActivity(myIntent);
			}
		});
		getGroupsTask = new GetGroupsTask(groups, groupAdapter, this);
		getGroupsTask.execute();
	}
}

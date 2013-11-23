package ch.romix.korbball.meisterschaft;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class RankingActivity extends Activity {

	public static final String INTENT_GROUP_NAME = "groupName";
	public static final String INTENT_GROUP_ID = "groupId";

	static final String DETAIL = "detail";
	static final String TEAM_TITLE = "teamTitle";
	static final String TEAM_ID = "teamId";
	static final String TEAM_NAME = "teamName";
	static final String RANKING = "ranking";

	SimpleAdapter adapter;
	List<Map<String, String>> data;
	private MenuItem refreshItem;
	private Animation rotation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ranking);
		writeTitle();
		linkDataAndView();
		registerClickListener();
		createAnimation();
		createRankingTask().execute();
	}

	private void createAnimation() {
		rotation = AnimationUtils.loadAnimation(this, R.anim.clockwise_refresh);
		rotation.setRepeatCount(Animation.INFINITE);
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		enableBackButton();
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.games_action_bar, menu);
		installAnimatedRefreshButton(menu);
		startAnimatedRefreshButton();
		return true;
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	private void enableBackButton() {
		if (withBackButton()) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_refresh:
			startAnimatedRefreshButton();
			createRankingTask().execute();
			break;
		default:
			finish();
			break;
		}
		return true;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void installAnimatedRefreshButton(final Menu menu) {
		if (withAnimation()) {
			refreshItem = menu.findItem(R.id.action_refresh);
			refreshItem.getActionView().setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					menu.performIdentifierAction(refreshItem.getItemId(), 0);
				}
			});
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void startAnimatedRefreshButton() {
		if (withAnimation()) {
			refreshItem.getActionView().startAnimation(rotation);
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void stopAnimatedRefreshButton() {
		if (withAnimation()) {
			refreshItem.getActionView().clearAnimation();
		}
	}

	private void writeTitle() {
		String groupName = getIntent().getStringExtra(INTENT_GROUP_NAME);
		String title = String.format(getResources().getString(R.string.title_activity_ranking), groupName);
		setTitle(title);
	}

	private GetRankingTask createRankingTask() {
		return new GetRankingTask(this);
	}

	private void linkDataAndView() {
		data = new LinkedList<Map<String, String>>();
		adapter = new SimpleAdapter(this, data, R.layout.rankingitem, new String[] { TEAM_TITLE, DETAIL }, new int[] { R.id.RankingTeam,
				R.id.RankingDetails });
		ListView listView = (ListView) findViewById(R.id.ranking_listview);
		listView.setAdapter(adapter);
	}

	private void registerClickListener() {
		ListView listRanking = (ListView) findViewById(R.id.ranking_listview);
		listRanking.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent myIntent = new Intent(view.getContext(), GamesActivity.class);
				Map<String, String> map = data.get((int) id);
				myIntent.putExtra(GamesActivity.INTENT_TEAM_NAME, map.get(TEAM_NAME));
				myIntent.putExtra(GamesActivity.INTENT_TEAM_ID, map.get(TEAM_ID));
				startActivity(myIntent);
			}
		});
	}

	void updateView() {
		stopAnimatedRefreshButton();
		adapter.notifyDataSetChanged();
	}

	private boolean withAnimation() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
	}

	private boolean withBackButton() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
	}
}

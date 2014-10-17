package ch.romix.korbball.meisterschaft.game;

import java.util.LinkedList;

import ch.romix.korbball.meisterschaft.R;
import ch.romix.korbball.meisterschaft.R.anim;
import ch.romix.korbball.meisterschaft.R.id;
import ch.romix.korbball.meisterschaft.R.layout;
import ch.romix.korbball.meisterschaft.R.menu;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ListView;

public class GamesActivity extends Activity {

	public static final String INTENT_TEAM_NAME = "team";
	public static final String INTENT_TEAM_ID = "teamId";

	final private LinkedList<Game> games;
	private GameAdapter adapter;
	private String team;
	private String teamId;
	private MenuItem refreshItem;
	private Animation rotation;

	public GamesActivity() {
		games = new LinkedList<Game>();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_games);

		teamId = getIntent().getStringExtra(INTENT_TEAM_ID);
		team = getIntent().getStringExtra(INTENT_TEAM_NAME);
		adapter = new GameAdapter(this, games, team);

		setTitle("Spiele von " + team);

		ListView listGames = (ListView) findViewById(R.id.games_listview);

		rotation = AnimationUtils.loadAnimation(this, R.anim.clockwise_refresh);
		rotation.setRepeatCount(Animation.INFINITE);

		listGames.setAdapter(adapter);
		createGamesTask().execute();
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_refresh:
			startAnimatedRefreshButton();
			createGamesTask().execute();
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

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	private void enableBackButton() {
		if (withBackButton()) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	void updateView() {
		stopAnimatedRefreshButton();
		adapter.notifyDataSetChanged();
	}

	private GetGamesTask createGamesTask() {
		return new GetGamesTask(new Runnable() {
			@Override
			public void run() {
				updateView();
			}
		}, teamId, games);
	}

	private boolean withAnimation() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
	}

	private boolean withBackButton() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
	}
}

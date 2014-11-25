package ch.romix.korbball.meisterschaft.favorite;

import java.util.LinkedList;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import ch.romix.korbball.meisterschaft.R;
import ch.romix.korbball.meisterschaft.game.Game;
import ch.romix.korbball.meisterschaft.game.GetGamesTask;
import ch.romix.korbball.meisterschaft.groups.Group;
import ch.romix.korbball.meisterschaft.ranking.GetRankingTask;
import ch.romix.korbball.meisterschaft.ranking.RankingActivity;

public class FavoritesActivity extends Activity {

	private LayoutInflater inflater;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.favorites);
		inflater = LayoutInflater.from(getApplicationContext());
	}

	private void buildFavoriteView() {
		LinearLayout favoriteView = getFavoriteView();

		FavoriteStore favoriteStore = new FavoriteStore(this);
		String[] favoritedTeams = favoriteStore.getFavorites();

		View findViewById = findViewById(R.id.textHowToAddFavorites);
		if (favoritedTeams.length == 0) {
			findViewById.setVisibility(View.VISIBLE);
		} else {
			findViewById.setVisibility(View.GONE);
		}

		for (String favoriteTeamId : favoritedTeams) {
			View favoriteTeamView = inflater.inflate(R.layout.favorite, favoriteView, false);

			setText(favoriteTeamView, R.id.favoriteTeamName, favoriteStore.getFavoriteName(favoriteTeamId));
			setText(favoriteTeamView, R.id.favoriteGroupName, favoriteStore.getFavoriteGroupName(favoriteTeamId));

			installRankingTask(favoriteStore, favoriteTeamId, favoriteTeamView);
			installGamesTask(favoriteStore, favoriteTeamId, favoriteTeamView);

			favoriteView.addView(favoriteTeamView);
		}
	}

	private void installRankingTask(final FavoriteStore favoriteStore, final String favoriteTeamId, final View favoriteTeamView) {
		final LinkedList<Map<String, String>> rankingData = new LinkedList<Map<String, String>>();
		Runnable callback = new RankingResponseCallback(this, favoriteTeamId, favoriteTeamView, rankingData, favoriteStore);
		new GetRankingTask(rankingData, callback, favoriteStore.getFavoriteGroupId(favoriteTeamId)).execute();
	}

	private void installGamesTask(FavoriteStore favoriteStore, String favoriteTeamId, final View favoriteTeamView) {
		final LinkedList<Game> games = new LinkedList<Game>();
		Runnable callback = new GamesResponseCallback(favoriteTeamView, games, favoriteStore, favoriteTeamId);
		new GetGamesTask(callback, favoriteTeamId, games).execute();
	}

	void setText(View view, int textViewResource, String text) {
		TextView teamView = (TextView) view.findViewById(textViewResource);
		teamView.setText(text);
		teamView.setTypeface(null, Typeface.NORMAL);
		teamView.setTextColor(Color.parseColor("#E0E0E0"));
	}

	Intent getIntent(Group group) {
		Intent myIntent = new Intent(getApplicationContext(), RankingActivity.class);
		myIntent.putExtra(RankingActivity.INTENT_GROUP_ID, group.getGroupId());
		myIntent.putExtra(RankingActivity.INTENT_GROUP_NAME, group.getGroupName());
		return myIntent;
	}

	@Override
	protected void onResume() {
		super.onResume();
		LinearLayout favoriteView = getFavoriteView();
		favoriteView.removeAllViews();
		buildFavoriteView();
	}

	private LinearLayout getFavoriteView() {
		return (LinearLayout) findViewById(R.id.favoriteScrollViewLayout);
	}
}

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

		for (String favoriteId : favoritedTeams) {
			View favoriteTeamView = inflater.inflate(R.layout.favorite, favoriteView, false);

			setText(favoriteTeamView, R.id.favoriteTeamName, favoriteStore.getFavoriteName(favoriteId), false, null);
			setText(favoriteTeamView, R.id.favoriteGroupName, favoriteStore.getFavoriteGroupName(favoriteId), false, null);

			installRankingTask(favoriteStore, favoriteId, favoriteTeamView);

			favoriteView.addView(favoriteTeamView);
		}
	}

	private void installRankingTask(final FavoriteStore favoriteStore, final String favoriteId, final View favoriteTeamView) {
		final LinkedList<Map<String, String>> rankingData = new LinkedList<Map<String, String>>();
		Runnable callback = new Runnable() {
			@Override
			public void run() {
				String thisTeamName = favoriteStore.getFavoriteName(favoriteId);
				final Group group = new Group(favoriteStore.getFavoriteGroupId(favoriteId), favoriteStore.getFavoriteGroupName(favoriteId));
				final Intent groupIntent = getIntent(group);
				View.OnClickListener listener = new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						startActivity(groupIntent);
					}
				};
				int i;
				for (i = 0; i < rankingData.size(); i++) {
					Map<String, String> ranking = rankingData.get(i);
					String teamName = ranking.get(RankingActivity.TEAM_NAME);
					if (teamName.equals(thisTeamName)) {
						break;
					}
				}

				if (i == 0) {
					setText(favoriteTeamView, R.id.favoriteRanking1, getTeamString(rankingData, i), true, listener);
					setText(favoriteTeamView, R.id.favoriteRanking2, getTeamString(rankingData, i + 1), false, listener);
					setText(favoriteTeamView, R.id.favoriteRanking3, getTeamString(rankingData, i + 2), false, listener);
				} else if (i == rankingData.size() - 1) {
					setText(favoriteTeamView, R.id.favoriteRanking1, getTeamString(rankingData, i - 2), false, listener);
					setText(favoriteTeamView, R.id.favoriteRanking2, getTeamString(rankingData, i - 1), false, listener);
					setText(favoriteTeamView, R.id.favoriteRanking3, getTeamString(rankingData, i), true, listener);
				} else {
					setText(favoriteTeamView, R.id.favoriteRanking1, getTeamString(rankingData, i - 1), false, listener);
					setText(favoriteTeamView, R.id.favoriteRanking2, getTeamString(rankingData, i), true, listener);
					setText(favoriteTeamView, R.id.favoriteRanking3, getTeamString(rankingData, i + 1), false, listener);
				}

			}

			private String getTeamString(final LinkedList<Map<String, String>> rankingData, int i) {
				return rankingData.get(i).get(RankingActivity.RANKING) + ". " + rankingData.get(i).get(RankingActivity.TEAM_NAME);
			}
		};
		new GetRankingTask(rankingData, callback, favoriteStore.getFavoriteGroupId(favoriteId)).execute();
	}

	private void setText(View view, int textViewResource, String text, boolean bold, View.OnClickListener clickListener) {
		TextView teamView = (TextView) view.findViewById(textViewResource);
		teamView.setText(text);
		if (bold) {
			teamView.setTypeface(null, Typeface.BOLD);
			teamView.setTextColor(getResources().getColor(android.R.color.white));
		} else {
			teamView.setTypeface(null, Typeface.NORMAL);
			teamView.setTextColor(Color.parseColor("#E0E0E0"));
		}
		if (clickListener != null) {
			view.setOnClickListener(clickListener);
		}
	}

	private Intent getIntent(Group group) {
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

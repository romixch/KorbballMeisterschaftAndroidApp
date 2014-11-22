package ch.romix.korbball.meisterschaft.favorite;

import java.util.LinkedList;
import java.util.Map;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;
import ch.romix.korbball.meisterschaft.R;
import ch.romix.korbball.meisterschaft.groups.Group;
import ch.romix.korbball.meisterschaft.ranking.RankingActivity;

final class RankingResponseCallback implements Runnable {
	private final FavoritesActivity favoritesActivity;
	private final String favoriteTeamId;
	private final View favoriteTeamView;
	private final LinkedList<Map<String, String>> rankingData;
	private final FavoriteStore favoriteStore;

	RankingResponseCallback(FavoritesActivity favoritesActivity, String favoriteTeamId, View favoriteTeamView,
			LinkedList<Map<String, String>> rankingData, FavoriteStore favoriteStore) {
		this.favoritesActivity = favoritesActivity;
		this.favoriteTeamId = favoriteTeamId;
		this.favoriteTeamView = favoriteTeamView;
		this.rankingData = rankingData;
		this.favoriteStore = favoriteStore;
	}

	@Override
	public void run() {
		String thisTeamName = favoriteStore.getFavoriteName(favoriteTeamId);
		final Group group = new Group(favoriteStore.getFavoriteGroupId(favoriteTeamId), favoriteStore.getFavoriteGroupName(favoriteTeamId));
		final Intent groupIntent = this.favoritesActivity.getIntent(group);
		View.OnClickListener listener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				RankingResponseCallback.this.favoritesActivity.startActivity(groupIntent);
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

	private void setText(View view, int textViewResource, String text, boolean bold, View.OnClickListener clickListener) {
		TextView teamView = (TextView) view.findViewById(textViewResource);
		teamView.setText(text);
		if (bold) {
			teamView.setTypeface(null, Typeface.BOLD);
			teamView.setTextColor(favoriteTeamView.getResources().getColor(android.R.color.white));
		} else {
			teamView.setTypeface(null, Typeface.NORMAL);
			teamView.setTextColor(Color.parseColor("#E0E0E0"));
		}
		if (clickListener != null) {
			view.setOnClickListener(clickListener);
		}
	}
}
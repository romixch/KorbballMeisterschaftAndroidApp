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
		if (isFavoriteTeamInGroup(thisTeamName)) {
			View.OnClickListener listener = createGroupActivityListener();
			setupRanking(thisTeamName, listener);
		} else {
			this.favoriteTeamView.setVisibility(View.GONE);
			favoriteStore.removeFavorite(favoriteTeamId);
		}
	}

	private View.OnClickListener createGroupActivityListener() {
		final Group group = new Group(favoriteStore.getFavoriteGroupId(favoriteTeamId), favoriteStore.getFavoriteGroupName(favoriteTeamId));
		final Intent groupIntent = this.favoritesActivity.getIntent(group);

		View.OnClickListener listener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				RankingResponseCallback.this.favoritesActivity.startActivity(groupIntent);
			}
		};
		return listener;
	}

	private void setupRanking(String thisTeamName, View.OnClickListener listener) {
		int rank;
		for (rank = 0; rank < rankingData.size(); rank++) {
			Map<String, String> ranking = rankingData.get(rank);
			String teamName = ranking.get(RankingActivity.TEAM_NAME);
			if (teamName.equals(thisTeamName)) {
				break;
			}
		}

		if (rank == 0) {
			// Our team is at the first place
			setText(favoriteTeamView, R.id.favoriteRanking1, getTeamString(rankingData, rank), true, listener);
			setText(favoriteTeamView, R.id.favoriteRanking2, getTeamString(rankingData, rank + 1), false, listener);
			setText(favoriteTeamView, R.id.favoriteRanking3, getTeamString(rankingData, rank + 2), false, listener);
		} else if (rank == rankingData.size() - 1) {
			// Our team is at the last place
			setText(favoriteTeamView, R.id.favoriteRanking1, getTeamString(rankingData, rank - 2), false, listener);
			setText(favoriteTeamView, R.id.favoriteRanking2, getTeamString(rankingData, rank - 1), false, listener);
			setText(favoriteTeamView, R.id.favoriteRanking3, getTeamString(rankingData, rank), true, listener);
		} else {
			// Our team is somewhere in between
			setText(favoriteTeamView, R.id.favoriteRanking1, getTeamString(rankingData, rank - 1), false, listener);
			setText(favoriteTeamView, R.id.favoriteRanking2, getTeamString(rankingData, rank), true, listener);
			setText(favoriteTeamView, R.id.favoriteRanking3, getTeamString(rankingData, rank + 1), false, listener);
		}
	}

	private boolean isFavoriteTeamInGroup(String thisTeamName) {
		for (Map<String, String> ranking : rankingData) {
			String teamName = ranking.get(RankingActivity.TEAM_NAME);
			if (teamName.equals(thisTeamName)) {
				return true;
			}
		}
		return false;
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
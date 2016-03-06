package ch.romix.korbball.meisterschaft.favorite;

import java.util.LinkedList;
import java.util.Stack;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import ch.romix.korbball.meisterschaft.R;
import ch.romix.korbball.meisterschaft.game.Game;

final class GamesResponseCallback implements Runnable {
	private final View favoriteTeamView;
	private final LinkedList<Game> games;
	private FavoriteStore favoriteStore;
	private String favoriteTeamId;

	GamesResponseCallback(View favoriteTeamView, LinkedList<Game> games, FavoriteStore favoriteStore, String favoriteTeamId) {
		this.favoriteTeamView = favoriteTeamView;
		this.games = games;
		this.favoriteStore = favoriteStore;
		this.favoriteTeamId = favoriteTeamId;
	}

	@Override
	public void run() {
		int nextCount = 0;
		Stack<Game> lastGames = new Stack<Game>();
		for (Game game : games) {
			if (game.isPlayed()) {
				lastGames.push(game);
			} else {
				nextCount++;
				setUnplayed(nextCount, game);
				setUnplayed(nextCount, game);
				setUnplayed(nextCount, game);
				if (nextCount >= 3) {
					break;
				}
			}
		}
		int i = 3;
		while (i > 0) {
			if (lastGames.isEmpty()) {
				setLastGameInvisible(i);
			} else {
				Game game = lastGames.pop();
				setPlayedGame(i, game);
			}

			i--;
		}
	}

	private void setPlayedGame(int lineNumber, Game game) {
		if (lineNumber == 1) {
			setPlayedGameText(game, R.id.lastGames1);
		} else if (lineNumber == 2) {
			setPlayedGameText(game, R.id.lastGames2);
		} else if (lineNumber == 3) {
			setPlayedGameText(game, R.id.lastGames3);
		}
	}

	private void setPlayedGameText(Game game, int textViewResource) {
		TextView textView = (TextView) favoriteTeamView.findViewById(textViewResource);
		textView.setVisibility(View.VISIBLE);
		String otherTeam;
		String thisTeam = favoriteStore.getFavoriteName(this.favoriteTeamId);
		if (game.getTeamA().equals(thisTeam)) {
			otherTeam = game.getTeamB();
		} else {
			otherTeam = game.getTeamA();
		}

		CharacterStyle colorSpan = null;
		if (game.isTie()) {
			colorSpan = new ForegroundColorSpan(0xFFFFFF00);
		} else if (game.isThisTheWinner(thisTeam)) {
			colorSpan = new ForegroundColorSpan(0xFF00FF00);
		} else {
			colorSpan = new ForegroundColorSpan(0xFFFF0000);
		}
		String firstPart = String.format("%1$s: %2$s / ", game.getDay(), otherTeam);
		String secondPart = String.format("%1$s : %2$s", game.getResultA(), game.getResultB());
		String bothParts = firstPart + secondPart;
		textView.setText(bothParts, BufferType.SPANNABLE);
		Spannable pointsText = (Spannable) textView.getText();
		pointsText.setSpan(colorSpan, firstPart.length(), bothParts.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	}

	private void setLastGameInvisible(int lineNumber) {
		if (lineNumber == 1) {
			favoriteTeamView.findViewById(R.id.lastGames1).setVisibility(View.GONE);
		} else if (lineNumber == 2) {
			favoriteTeamView.findViewById(R.id.lastGames2).setVisibility(View.GONE);
		} else if (lineNumber == 3) {
			favoriteTeamView.findViewById(R.id.lastGames3).setVisibility(View.GONE);
		}
	}

	private void setUnplayed(int lineNumber, Game game) {
		if (lineNumber == 1) {
			setText(favoriteTeamView, R.id.nextGames1, getUnplayedGameString(game), null);
		} else if (lineNumber == 2) {
			setText(favoriteTeamView, R.id.nextGames2, getUnplayedGameString(game), null);
		} else if (lineNumber == 3) {
			setText(favoriteTeamView, R.id.nextGames3, getUnplayedGameString(game), null);
		}
	}

	private String getUnplayedGameString(Game game) {
		StringBuilder sb = new StringBuilder();
		sb.append(game.getDay()).append(" ");
		sb.append(game.getTime()).append(" ");
		sb.append(game.getHall()).append(": ");
		String team;
		if (favoriteStore.getFavoriteName(favoriteTeamId).equals(game.getTeamA())) {
			team = game.getTeamB();
		} else {
			team = game.getTeamA();
		}
		sb.append(team);
		return sb.toString();
	}

	private void setText(View view, int textViewResource, String text, View.OnClickListener clickListener) {
		TextView teamView = (TextView) view.findViewById(textViewResource);
		teamView.setVisibility(View.VISIBLE);
		teamView.setText(text);
		teamView.setTypeface(null, Typeface.NORMAL);
		teamView.setTextColor(Color.parseColor("#E0E0E0"));
		if (clickListener != null) {
			view.setOnClickListener(clickListener);
		}
	}
}
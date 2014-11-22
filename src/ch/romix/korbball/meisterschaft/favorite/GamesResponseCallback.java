package ch.romix.korbball.meisterschaft.favorite;

import java.util.LinkedList;
import java.util.Stack;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;
import ch.romix.korbball.meisterschaft.R;
import ch.romix.korbball.meisterschaft.game.Game;

final class GamesResponseCallback implements Runnable {
	private final View favoriteTeamView;
	private final LinkedList<Game> games;

	GamesResponseCallback(View favoriteTeamView, LinkedList<Game> games) {
		this.favoriteTeamView = favoriteTeamView;
		this.games = games;
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
		String playedString = getPlayedGameString(game);
		if (lineNumber == 1) {
			setText(favoriteTeamView, R.id.lastGames1, playedString, null);
		} else if (lineNumber == 2) {
			setText(favoriteTeamView, R.id.lastGames2, playedString, null);
		} else if (lineNumber == 3) {
			setText(favoriteTeamView, R.id.lastGames3, playedString, null);
		}
	}

	private String getPlayedGameString(Game game) {
		StringBuilder sb = new StringBuilder();
		sb.append(game.getDay()).append(": ");
		sb.append(game.getTeamA()).append(" - ").append(game.getTeamB()).append(" ");
		sb.append(game.getResultA()).append(" : ").append(game.getResultB());
		return sb.toString();
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
		sb.append(game.getTeamA()).append(" - ").append(game.getTeamB());
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
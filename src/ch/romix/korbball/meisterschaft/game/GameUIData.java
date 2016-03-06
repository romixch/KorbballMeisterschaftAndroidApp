package ch.romix.korbball.meisterschaft.game;

import android.content.Context;
import android.text.Spannable;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import ch.romix.korbball.meisterschaft.R;

public abstract class GameUIData {

	private View view;

	public static GameUIData createDay(String day, String dayOfWeek) {
		return new GameUIDataForDay(day, dayOfWeek);
	}

	public static GameUIData createGame(Game game, String currentTeam) {
		return new GameUIDataForGame(game, currentTeam);
	}

	public abstract Object getData();

	public View getView(LayoutInflater inflater, ViewGroup parent, Context context) {
		if (view == null) {
			view = createView(inflater, parent, context);
		}
		return view;
	}

	protected void setViewText(View parentView, int childViewId, String text) {
		TextView textView = (TextView) parentView.findViewById(childViewId);
		textView.setText(text);
	}

	protected abstract View createView(LayoutInflater inflater, ViewGroup parent, Context context);

	private static final class GameUIDataForDay extends GameUIData {
		private final String day;

		public GameUIDataForDay(String day, String dayOfWeek) {
			this.day = day + " / " + dayOfWeek;
		}

		@Override
		public Object getData() {
			return day;
		}

		@Override
		protected View createView(LayoutInflater inflater, ViewGroup parent, Context context) {
			View rowView = inflater.inflate(R.layout.dayitem, parent, false);
			setViewText(rowView, R.id.game_day, day + ":");
			return rowView;
		}

	}

	private static final class GameUIDataForGame extends GameUIData {
		private final Game game;
		private final String currentTeam;

		public GameUIDataForGame(Game game, String currentTeam) {
			this.game = game;
			this.currentTeam = currentTeam;
		}

		@Override
		public Object getData() {
			return game;
		}

		@Override
		protected View createView(LayoutInflater inflater, ViewGroup parent, Context context) {
			View rowView = inflater.inflate(R.layout.gameitem, parent, false);

			setViewText(rowView, R.id.game_hall, game.getHall());
			setViewText(rowView, R.id.game_time, game.getTime());
			setViewText(rowView, R.id.game_round, game.getRound());
			setTeamText(context, rowView);

			String resultString = calculateResultText(context);
			setViewText(rowView, R.id.game_result, resultString);

			setPointsText(rowView);

			return rowView;
		}

		private String calculateResultText(Context context) {
			String resultString;
			if (game.isPlayed()) {
				resultString = String.format(context.getResources().getString(R.string.games_result), game.getResultA(), game.getResultB());
			} else {
				resultString = "";
			}
			return resultString;
		}

		private void setPointsText(View rowView) {
			TextView pointsView = (TextView) rowView.findViewById(R.id.game_points);
			if (game.isPlayed()) {

				String points = String.valueOf(game.getMyPoints(currentTeam));
				CharacterStyle colorSpan = null;
				if (game.isTie()) {
					colorSpan = new ForegroundColorSpan(0xFFFFFF00);
				} else if (game.isThisTheWinner(currentTeam)) {
					colorSpan = new ForegroundColorSpan(0xFF00FF00);
				} else {
					colorSpan = new ForegroundColorSpan(0xFFFF0000);
				}
				pointsView.setText(points, BufferType.SPANNABLE);
				Spannable pointsText = (Spannable) pointsView.getText();
				pointsText.setSpan(colorSpan, 0, points.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			} else {
				pointsView.setText("");
			}
		}

		private void setTeamText(Context context, View rowView) {
			TextView teams = (TextView) rowView.findViewById(R.id.game_teams);
			String teamString = String.format(context.getResources().getString(R.string.games_teams), game.getTeamA(), game.getTeamB());
			teams.setText(teamString, TextView.BufferType.SPANNABLE);

			Spannable text = (Spannable) teams.getText();
			StyleSpan boldSpan = new StyleSpan(android.graphics.Typeface.BOLD_ITALIC);
			if (game.getTeamA().endsWith(currentTeam)) {
				text.setSpan(boldSpan, 0, game.getTeamA().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			} else {
				int l = game.getTeamB().length();
				text.setSpan(boldSpan, text.length() - l, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}

	}
}

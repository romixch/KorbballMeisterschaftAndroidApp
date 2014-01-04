package ch.romix.korbball.meisterschaft;

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

public abstract class GameUIData {

	private View view;

	public static GameUIData createDay(String day) {
		return new GameUIDataForDay(day);
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

	protected abstract View createView(LayoutInflater inflater, ViewGroup parent, Context context);

	private static final class GameUIDataForDay extends GameUIData {
		private final String day;

		public GameUIDataForDay(String day) {
			this.day = day;
		}

		@Override
		protected View createView(LayoutInflater inflater, ViewGroup parent, Context context) {
			View rowView = inflater.inflate(R.layout.dayitem, parent, false);
			TextView day = (TextView) rowView.findViewById(R.id.game_day);
			day.setText(this.day + ":");
			return rowView;
		}

		@Override
		public Object getData() {
			return day;
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

			TextView hall = (TextView) rowView.findViewById(R.id.game_hall);
			hall.setText(game.getHall());
			TextView time = (TextView) rowView.findViewById(R.id.game_time);
			time.setText(game.getTime());
			TextView round = (TextView) rowView.findViewById(R.id.game_round);
			round.setText(game.getRound());
			TextView teams = (TextView) rowView.findViewById(R.id.game_teams);
			String teamString = String.format(context.getResources().getString(R.string.games_teams), game.getTeamA(), game.getTeamB());
			teams.setText(teamString, TextView.BufferType.SPANNABLE);

			Spannable text = (Spannable) teams.getText();
			StyleSpan boldSpan = new StyleSpan(android.graphics.Typeface.BOLD_ITALIC);
			if (game.getTeamA().equals(currentTeam)) {
				text.setSpan(boldSpan, 0, game.getTeamA().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			} else {
				int l = game.getTeamB().length();
				text.setSpan(boldSpan, text.length() - l, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}

			TextView result = (TextView) rowView.findViewById(R.id.game_result);
			TextView pointsView = (TextView) rowView.findViewById(R.id.game_points);
			if (game.isPlayed()) {
				String resultString = String.format(context.getResources().getString(R.string.games_result), game.getResultA(),
						game.getResultB());
				result.setText(resultString);

				String points = "";
				CharacterStyle colorSpan = null;
				if (game.isTie()) {
					colorSpan = new ForegroundColorSpan(0xFFFFFF00);
					points = "1";
				} else if (game.getWinner().equals(currentTeam)) {
					colorSpan = new ForegroundColorSpan(0xFF00FF00);
					points = "2";
				} else {
					points = "0";
					colorSpan = new ForegroundColorSpan(0xFFFF0000);
				}
				pointsView.setText(points, BufferType.SPANNABLE);
				Spannable pointsText = (Spannable) pointsView.getText();
				pointsText.setSpan(colorSpan, 0, points.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			} else {
				result.setText("");
				pointsView.setText("");
			}

			return rowView;
		}
	}
}

package ch.romix.korbball.meisterschaft;

import java.util.LinkedList;

import android.content.Context;
import android.text.Spannable;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.TextView.BufferType;

public class GameAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private final LinkedList<Game> games;
	private final Context context;
	private final String currentTeam;

	public GameAdapter(Context context, LinkedList<Game> games, String currentTeam) {
		this.context = context;
		this.games = games;
		this.currentTeam = currentTeam;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return games.size();
	}

	@Override
	public Object getItem(int position) {
		return games.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = inflater.inflate(R.layout.gameitem, parent, false);

		Game gameData = games.get(position);
		TextView day = (TextView) rowView.findViewById(R.id.game_day);
		day.setText(gameData.getDay());
		TextView time = (TextView) rowView.findViewById(R.id.game_time);
		time.setText(gameData.getTime());
		TextView hall = (TextView) rowView.findViewById(R.id.game_hall);
		hall.setText(gameData.getHall());
		TextView round = (TextView) rowView.findViewById(R.id.game_round);
		round.setText(gameData.getRound());
		TextView teams = (TextView) rowView.findViewById(R.id.game_teams);
		String teamString = String.format(context.getResources().getString(R.string.games_teams), gameData.getTeamA(), gameData.getTeamB());
		teams.setText(teamString, TextView.BufferType.SPANNABLE);

		Spannable text = (Spannable) teams.getText();
		StyleSpan boldSpan = new StyleSpan(android.graphics.Typeface.BOLD_ITALIC);
		if (gameData.getTeamA().equals(currentTeam)) {
			text.setSpan(boldSpan, 0, gameData.getTeamA().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		} else {
			int l = gameData.getTeamB().length();
			text.setSpan(boldSpan, text.length() - l, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}

		TextView result = (TextView) rowView.findViewById(R.id.game_result);
		TextView pointsView = (TextView) rowView.findViewById(R.id.game_points);
		if (gameData.isPlayed()) {
			String resultString = String.format(context.getResources().getString(R.string.games_result), gameData.getResultA(),
					gameData.getResultB());
			result.setText(resultString);

			String points = "";
			CharacterStyle colorSpan = null;
			if (gameData.isTie()) {
				colorSpan = new ForegroundColorSpan(0xFFFFFF00);
				points = "1";
			} else if (gameData.getWinner().equals(currentTeam)) {
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

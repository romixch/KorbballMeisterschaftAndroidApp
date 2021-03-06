package ch.romix.korbball.meisterschaft.game;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import ch.romix.korbball.meisterschaft.StreamTools;
import ch.romix.korbball.meisterschaft.UrlConsts;

public class GetGamesTask extends AsyncTask<Void, Void, List<Game>> {

	private static String JSON_DAY = "tag";
	private static String JSON_TIME = "zeit";
	private static String JSON_HALL = "halle";
	private static String JSON_ROUND = "runde";
	private static String JSON_TEAM_A = "txtTeamA";
	private static String JSON_TEAM_B = "txtTeamB";
	private static String JSON_RESULT_A = "resultatA";
	private static String JSON_RESULT_B = "resultatB";
	private static String JSON_POINTS_A = "punkteA";
	private static String JSON_POINTS_B = "punkteB";

	static SimpleDateFormat DATE_PARSER = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
	static DateFormat DATE_FORMATTER = SimpleDateFormat.getDateInstance(DateFormat.SHORT);
	static SimpleDateFormat TIME_PARSER = new SimpleDateFormat("HH:mm:ss", Locale.US);
	static DateFormat TIME_FORMATTER = SimpleDateFormat.getTimeInstance(DateFormat.SHORT);

	private final Runnable callback;
	private final String teamId;
	private final List<Game> games;

	public GetGamesTask(Runnable callback, String teamId, List<Game> games) {
		this.callback = callback;
		this.teamId = teamId;
		this.games = games;
	}

	@Override
	protected List<Game> doInBackground(Void... params) {
		List<Game> games = new LinkedList<Game>();
		try {
			HttpResponse response = requestGames();
			String jsonResult = StreamTools.inputStreamToString(response.getEntity().getContent()).toString();
			JSONArray json = new JSONArray(jsonResult);
			for (int i = 0; i < json.length(); i++) {
				JSONObject gameJson = json.getJSONObject(i);
				Game gameData = parseGame(gameJson);
				games.add(gameData);
			}
		} catch (ClientProtocolException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
		return games;
	}

	private Game parseGame(JSONObject gameJson) throws JSONException {
		Game gameData = new Game();
		gameData.setDay(formatDate(gameJson.getString(JSON_DAY)));
		gameData.setDayOfWeek(getDayOfWeek(gameJson.getString(JSON_DAY)));
		gameData.setTime(formatTime(gameJson.getString(JSON_TIME)));
		gameData.setHall(gameJson.getString(JSON_HALL));
		gameData.setRound(gameJson.getString(JSON_ROUND));
		gameData.setTeamA(gameJson.getString(JSON_TEAM_A));
		gameData.setTeamB(gameJson.getString(JSON_TEAM_B));
		if (!"".equals(gameJson.getString(JSON_RESULT_A))) {
			gameData.setPlayed(true);
			try {
				int resultA = Integer.parseInt(gameJson.getString(JSON_RESULT_A));
				gameData.setResultA(resultA);
				int resultB = Integer.parseInt(gameJson.getString(JSON_RESULT_B));
				gameData.setResultB(resultB);
				int pointsA = Integer.parseInt(gameJson.getString(JSON_POINTS_A));
				gameData.setPointsA(pointsA);
				int pointsB = Integer.parseInt(gameJson.getString(JSON_POINTS_B));
				gameData.setPointsB(pointsB);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		return gameData;
	}

	private HttpResponse requestGames() throws IOException, ClientProtocolException {
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpGET = new HttpGet(UrlConsts.GAMES + teamId);
		HttpResponse response = httpclient.execute(httpGET);
		return response;
	}

	private String formatTime(String jsonTime) {
		String formatted;
		try {
			Date date = TIME_PARSER.parse(jsonTime);
			formatted = TIME_FORMATTER.format(date);
		} catch (ParseException e) {
			// no formatting takes place
			formatted = jsonTime;
		}
		return formatted;
	}

	private String formatDate(String jsonDay) {
		String formatted;
		try {
			Date date = DATE_PARSER.parse(jsonDay);
			formatted = DATE_FORMATTER.format(date);
		} catch (ParseException e) {
			// no formatting takes place
			formatted = jsonDay;
		}
		return formatted;
	}

	private String getDayOfWeek(String jsonDay) {
		String dayOfWeek;
		try {
			Date date = DATE_PARSER.parse(jsonDay);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			int dayOfWeekInt = calendar.get(Calendar.DAY_OF_WEEK);
			switch (dayOfWeekInt) {
			case Calendar.MONDAY:
				dayOfWeek = "Montag";
				break;
			case Calendar.TUESDAY:
				dayOfWeek = "Dienstag";
				break;
			case Calendar.WEDNESDAY:
				dayOfWeek = "Mittwoch";
				break;
			case Calendar.THURSDAY:
				dayOfWeek = "Donnerstag";
				break;
			case Calendar.FRIDAY:
				dayOfWeek = "Freitag";
				break;
			case Calendar.SATURDAY:
				dayOfWeek = "Samstag";
				break;
			case Calendar.SUNDAY:
				dayOfWeek = "Sonntag";
				break;
			default:
				dayOfWeek = "";
			}
		} catch (ParseException e) {
			dayOfWeek = "";
		}
		return dayOfWeek;
	}

	@Override
	protected void onPostExecute(List<Game> result) {
		games.clear();
		for (Game game : result) {
			games.add(game);
		}
		callback.run();
		super.onPostExecute(result);
	}
}
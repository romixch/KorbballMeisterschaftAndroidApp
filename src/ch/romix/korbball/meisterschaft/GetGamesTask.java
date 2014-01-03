package ch.romix.korbball.meisterschaft;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

class GetGamesTask extends AsyncTask<Void, Void, List<Game>> {

	private static String JSON_DAY = "tag";
	private static String JSON_TIME = "zeit";
	private static String JSON_HALL = "halle";
	private static String JSON_ROUND = "runde";
	private static String JSON_TEAM_A = "txtTeamA";
	private static String JSON_TEAM_B = "txtTeamB";
	private static String JSON_RESULT_A = "resultatA";
	private static String JSON_RESULT_B = "resultatB";

	static SimpleDateFormat DATE_PARSER = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
	static DateFormat DATE_FORMATTER = SimpleDateFormat.getDateInstance(DateFormat.SHORT);
	static SimpleDateFormat TIME_PARSER = new SimpleDateFormat("HH:mm:ss", Locale.US);
	static DateFormat TIME_FORMATTER = SimpleDateFormat.getTimeInstance(DateFormat.SHORT);

	private final GamesActivity gamesActivity;
	private final String teamId;
	private final List<Game> games;

	GetGamesTask(GamesActivity gamesActivity, String teamId, List<Game> games) {
		this.gamesActivity = gamesActivity;
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
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		return gameData;
	}

	private HttpResponse requestGames() throws IOException, ClientProtocolException {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(UrlConsts.GAMES + teamId);
		HttpResponse response = httpclient.execute(httppost);
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

	@Override
	protected void onPostExecute(List<Game> result) {
		games.clear();
		for (Game game : result) {
			games.add(game);
		}
		gamesActivity.updateView();
		super.onPostExecute(result);
	}
}
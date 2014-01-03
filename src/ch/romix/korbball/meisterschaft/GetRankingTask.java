package ch.romix.korbball.meisterschaft;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

class GetRankingTask extends AsyncTask<Void, Void, List<Map<String, String>>> {

	private static final String JSON_POINTS = "points";
	private static final String JSON_GAMES = "games";
	private static final String JSON_PLAYED = "played";
	private static final String JSON_TEAM = "team";
	private static final String JSON_TEAM_ID = "teamId";
	private static final String JSON_RANK = "rank";

	private final RankingActivity rankingActivity;

	/**
	 * @param rankingActivity
	 */
	GetRankingTask(RankingActivity rankingActivity) {
		this.rankingActivity = rankingActivity;
	}

	@Override
	protected List<Map<String, String>> doInBackground(Void... params) {
		List<Map<String, String>> ranking = new LinkedList<Map<String, String>>();
		try {
			HttpResponse response = requestRankings();
			JSONObject json = convertToJson(response);
			@SuppressWarnings("rawtypes")
			Iterator it = json.keys();
			while (it.hasNext()) {
				String teamKey = it.next().toString();
				HashMap<String, String> teamMap = createTeamMap(json, teamKey);
				ranking.add(teamMap);
			}
			ranking = sort(ranking);

		} catch (ClientProtocolException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
		return ranking;
	}

	private List<Map<String, String>> sort(List<Map<String, String>> ranking) {
		Collections.sort(ranking, new Comparator<Map<String, String>>() {
			@Override
			public int compare(Map<String, String> lhs, Map<String, String> rhs) {
				int rankLeft = Integer.parseInt(lhs.get(RankingActivity.RANKING));
				int rankRight = Integer.parseInt(rhs.get(RankingActivity.RANKING));
				return rankLeft - rankRight;
			}
		});
		return ranking;
	}

	private HashMap<String, String> createTeamMap(JSONObject json, String teamKey) throws JSONException {
		JSONObject teamObject = json.getJSONObject(teamKey);
		String rankingString = teamObject.getString(JSON_RANK);
		String teamName = teamObject.getString(JSON_TEAM);
		String teamPoints = teamObject.getString(JSON_POINTS);
		int teamGames = teamObject.getInt(JSON_GAMES);
		int teamPlayed = teamObject.getInt(JSON_PLAYED);
		HashMap<String, String> teamMap = new HashMap<String, String>();
		teamMap.put(RankingActivity.TEAM_TITLE, rankingString + ". " + teamName);
		teamMap.put(RankingActivity.TEAM_NAME, teamName);
		String detailString = String.format(this.rankingActivity.getResources().getString(R.string.ranking_item_detail), teamPoints,
				teamGames, teamPlayed);
		teamMap.put(RankingActivity.DETAIL, detailString);
		teamMap.put(RankingActivity.RANKING, rankingString);
		teamMap.put(RankingActivity.TEAM_ID, teamObject.getString(JSON_TEAM_ID));
		return teamMap;
	}

	private JSONObject convertToJson(HttpResponse response) throws IOException, JSONException {
		String jsonResult = StreamTools.inputStreamToString(response.getEntity().getContent()).toString();
		JSONObject json = new JSONObject(jsonResult);
		return json;
	}

	private HttpResponse requestRankings() throws IOException, ClientProtocolException {
		HttpClient httpclient = new DefaultHttpClient();
		String groupId = this.rankingActivity.getIntent().getStringExtra(RankingActivity.INTENT_GROUP_ID);
		HttpPost httppost = new HttpPost(UrlConsts.RANKINGS + groupId);
		HttpResponse response = httpclient.execute(httppost);
		return response;
	}

	@Override
	protected void onPostExecute(List<Map<String, String>> result) {
		this.rankingActivity.data.clear();
		for (Map<String, String> map : result) {
			this.rankingActivity.data.add(map);
		}
		this.rankingActivity.updateView();
		super.onPostExecute(result);
	}
}
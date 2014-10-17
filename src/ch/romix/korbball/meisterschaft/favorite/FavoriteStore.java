package ch.romix.korbball.meisterschaft.favorite;

import java.util.Arrays;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class FavoriteStore {

	private static final String KEY_FAVORITES = "favorites";
	private static final String KEY_FAVORITES_TEAM_NAME = "favorites_%s";

	private SharedPreferences sharedPreferences;

	public FavoriteStore(Context context) {
		this(context.getSharedPreferences("favorites", Context.MODE_PRIVATE));
	}

	FavoriteStore(SharedPreferences sharedPreferences) {
		this.sharedPreferences = sharedPreferences;
	}

	public void addFavorite(String teamId, String teamName) {
		String[] teamIds = getFavorites();
		String[] newTeamIds = Arrays.copyOf(teamIds, teamIds.length + 1);
		newTeamIds[teamIds.length] = teamId;
		saveFavorites(newTeamIds);
		saveFavoriteName(teamId, teamName);
	}

	private void saveFavorites(String[] newTeamIds) {
		String separator = "";
		StringBuilder sb = new StringBuilder();
		for (String id : newTeamIds) {
			sb.append(separator);
			sb.append(id);
			separator = ";";
		}
		String teamIdsAsString = sb.toString();
		Editor editor = sharedPreferences.edit();
		editor.putString(KEY_FAVORITES, teamIdsAsString);
		editor.commit();
	}

	private void saveFavoriteName(String teamId, String teamName) {
		Editor editor = sharedPreferences.edit();
		editor.putString(String.format(KEY_FAVORITES_TEAM_NAME, teamId), teamName);
		editor.commit();
	}

	private void removeFavoriteName(String teamId) {
		Editor editor = sharedPreferences.edit();
		editor.remove(String.format(KEY_FAVORITES_TEAM_NAME, teamId));
		editor.commit();
	}

	public void removeFavorite(String teamId) {
		String[] favs = getFavorites();
		boolean found = false;
		for (int i = 0; i < favs.length; i++) {
			String fav = favs[i];
			if (i > 0 && found) {
				favs[i - 1] = favs[i];
			}
			if (fav.equals(teamId)) {
				found = true;
			}
		}
		if (found) {
			favs = Arrays.copyOf(favs, favs.length - 1);
		}
		saveFavorites(favs);
		removeFavoriteName(teamId);
	}

	public String[] getFavorites() {
		String favs = sharedPreferences.getString(KEY_FAVORITES, "");
		String[] teamIds;
		if (favs == null) {
			teamIds = new String[0];
		} else {
			teamIds = favs.split(";");
		}
		return teamIds;
	}

	public String getFavoriteName(String teamId) {
		return sharedPreferences.getString(String.format(KEY_FAVORITES_TEAM_NAME, teamId), "");
	}

	public boolean isFavorite(String teamId) {
		String[] favorites = getFavorites();
		for (String fav : favorites) {
			if (fav.equals(teamId)) {
				return true;
			}
		}
		return false;
	}

}

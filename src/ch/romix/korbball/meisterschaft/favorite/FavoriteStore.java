package ch.romix.korbball.meisterschaft.favorite;

import java.util.Arrays;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import ch.romix.korbball.meisterschaft.groups.Group;

public class FavoriteStore {

	private static final String KEY_FAVORITES = "favorites";
	private static final String KEY_FAVORITES_TEAM_NAME = "favorites_%s";
	private static final String KEY_FAVORITES_GROUP_NAME = "favorites_group_%s";
	private static final String KEY_FAVORITES_GROUPID_NAME = "favorites_groupId_%s";

	private SharedPreferences sharedPreferences;

	public FavoriteStore(Context context) {
		this(context.getSharedPreferences("favorites", Context.MODE_PRIVATE));
	}

	FavoriteStore(SharedPreferences sharedPreferences) {
		this.sharedPreferences = sharedPreferences;
	}

	public void addFavorite(String teamId, String teamName, Group group) {
		String[] teamIds = getFavorites();
		String[] newTeamIds = Arrays.copyOf(teamIds, teamIds.length + 1);
		newTeamIds[teamIds.length] = teamId;
		saveFavorites(newTeamIds);
		saveFavoriteName(teamId, teamName);
		saveFavoriteGroup(teamId, group);
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

	private void saveFavoriteGroup(String teamId, Group group) {
		Editor editor = sharedPreferences.edit();
		editor.putString(String.format(KEY_FAVORITES_GROUP_NAME, teamId), group.getGroupName());
		editor.putString(String.format(KEY_FAVORITES_GROUPID_NAME, teamId), group.getGroupId());
		editor.commit();
	}

	private void removeFavoriteGroup(String teamId) {
		Editor editor = sharedPreferences.edit();
		editor.remove(String.format(KEY_FAVORITES_GROUP_NAME, teamId));
		editor.remove(String.format(KEY_FAVORITES_GROUPID_NAME, teamId));
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
		removeFavoriteGroup(teamId);
	}

	public String[] getFavorites() {
		String favs = sharedPreferences.getString(KEY_FAVORITES, "");
		String[] teamIds;
		if (favs == null || favs.isEmpty()) {
			teamIds = new String[0];
		} else {
			teamIds = favs.split(";");
		}
		return teamIds;
	}

	public String getFavoriteName(String teamId) {
		return sharedPreferences.getString(String.format(KEY_FAVORITES_TEAM_NAME, teamId), "");
	}

	public String getFavoriteGroupName(String teamId) {
		return sharedPreferences.getString(String.format(KEY_FAVORITES_GROUP_NAME, teamId), "");
	}

	public String getFavoriteGroupId(String teamId) {
		return sharedPreferences.getString(String.format(KEY_FAVORITES_GROUPID_NAME, teamId), "");
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

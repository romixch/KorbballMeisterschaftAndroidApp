package ch.romix.korbball.meisterschaft.favorite;

import java.util.Collections;
import java.util.List;

public class FavoriteTeam {
	private final String teamName;
	private final List<String> closestTeams;
	private final Games lastGames;
	private final Games nextGames;

	public FavoriteTeam(String teamName, List<String> closestTeams, Games lastGames, Games nextGames) {
		this.teamName = teamName;
		this.closestTeams = Collections.unmodifiableList(closestTeams);
		this.lastGames = lastGames;
		this.nextGames = nextGames;
	}

	public String getTeamName() {
		return teamName;
	}

	public List<String> getClosestTeams() {
		return closestTeams;
	}

	public boolean hasLastGames() {
		return lastGames != null && lastGames.getGames() != null && lastGames.getGames().size() > 0;
	}

	public Games getLastGames() {
		return lastGames;
	}

	public boolean hasNextGames() {
		return nextGames != null && nextGames.getGames() != null && nextGames.getGames().size() > 0;
	}

	public Games getNextGames() {
		return nextGames;
	}
}

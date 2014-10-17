package ch.romix.korbball.meisterschaft.favorite;

import java.util.Collections;
import java.util.List;

public class Games {

	private final List<Game> games;
	private String date;

	public Games(String date, List<Game> games) {
		this.date = date;
		this.games = Collections.unmodifiableList(games);
	}

	public String getDate() {
		return date;
	}

	public List<Game> getGames() {
		return games;
	}
}

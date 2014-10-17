package ch.romix.korbball.meisterschaft.favorite;

public class Game {

	private final String time;
	private final String hall;
	private final String teamOne;
	private final String teamTwo;
	private final String result;

	public Game(String time, String hall, String teamOne, String teamTwo, String result) {
		this.time = time;
		this.hall = hall;
		this.teamOne = teamOne;
		this.teamTwo = teamTwo;
		this.result = result;
	}

	public String getTime() {
		return time;
	}

	public String getHall() {
		return hall;
	}

	public String getTeamOne() {
		return teamOne;
	}

	public String getTeamTwo() {
		return teamTwo;
	}

	public String getResult() {
		return result;
	}
}

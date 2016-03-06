package ch.romix.korbball.meisterschaft.game;

public class Game {

	private String day;
	private String time;
	private String hall;
	private String round;
	private String teamA;
	private String teamB;
	private int resultA;
	private int resultB;
	private boolean played;
	private String dayOfWeek;
	private int pointsA;
	private int pointsB;

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public String getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(String dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getHall() {
		return hall;
	}

	public void setHall(String hall) {
		this.hall = hall;
	}

	public String getRound() {
		return round;
	}

	public void setRound(String round) {
		this.round = round;
	}

	public String getTeamA() {
		return teamA;
	}

	public void setTeamA(String teamA) {
		this.teamA = teamA;
	}

	public String getTeamB() {
		return teamB;
	}

	public void setTeamB(String teamB) {
		this.teamB = teamB;
	}

	public int getResultA() {
		return resultA;
	}

	public void setResultA(int resultA) {
		this.resultA = resultA;
	}

	public int getResultB() {
		return resultB;
	}

	public void setResultB(int resultB) {
		this.resultB = resultB;
	}

	public int getPointsA() {
		return pointsA;
	}

	public void setPointsA(int pointsA) {
		this.pointsA = pointsA;
	}

	public int getPointsB() {
		return pointsB;
	}

	public void setPointsB(int pointsB) {
		this.pointsB = pointsB;
	}

	public int getMyPoints(String team) {
		if (teamA.endsWith(team)) {
			return pointsA;
		} else if (teamB.endsWith(team)) {
			return pointsB;
		} else {
			return 0;
		}
	}

	public boolean isPlayed() {
		return played;
	}

	public void setPlayed(boolean played) {
		this.played = played;
	}

	public boolean isTie() {
		return resultA == resultB;
	}

	public boolean isThisTheWinner(String team) {
		if (teamA.endsWith(team)) {
			return resultA > resultB;
		} else if (teamB.endsWith(team)) {
			return resultB > resultA;
		} else {
			return false;
		}
	}
}

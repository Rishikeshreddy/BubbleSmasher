package com.example.bubblesmasher;

public class Score {

	// Create username and score string and set getter setter method
	private String username;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	private String score;

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public Score(String username, String score) {
		super();
		this.username = username;
		this.score = score;
	}
	// getters and setters...
}
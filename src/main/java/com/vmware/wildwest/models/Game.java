package com.vmware.wildwest.models;

import java.util.*;

public class Game {

	private int score = 0;
	private String id;
	private List<GameObject> gameObjects;

	public Game() {
		id = generateGameID();
		this.score = 0;
		gameObjects = new ArrayList<>();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public void addGameObject(GameObject newObject) {
		gameObjects.add(newObject);
	}

	public List<GameObject> getGameObjects(){
		return gameObjects;
	}

	public void removeGameObject(GameObject theObject) {
		gameObjects.remove(theObject);
	}

	public void addListGameObject(List<GameObject> objects) {
			gameObjects.addAll(objects);
	}

	public GameObject getRandomGameObject() {
		if (gameObjects.size()>0)
			return gameObjects.get(new Random().nextInt(gameObjects.size()));
		else
			return null;
	}

	public void deleteGameObject(GameObject object) {
		gameObjects.remove(object);
	}

	private String generateGameID() {
		String randomChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		StringBuilder gameID = new StringBuilder();
		Random rnd = new Random();
		while (gameID.length() < 18) {
			int index = (int) (rnd.nextFloat() * randomChars.length());
			gameID.append(randomChars.charAt(index));
		}

		return gameID.toString();
	}

}

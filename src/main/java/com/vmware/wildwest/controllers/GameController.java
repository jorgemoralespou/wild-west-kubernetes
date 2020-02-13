package com.vmware.wildwest.controllers;

import com.vmware.wildwest.helpers.GameObjectHelper;
import com.vmware.wildwest.models.Game;
import com.vmware.wildwest.models.GameObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Configuration
@Scope(value = "singleton")
public class GameController {

	private Map<String, Game> games = new ConcurrentHashMap<String, Game>();

	@Autowired
	@Qualifier("map")
	private GameObjectHelper objectHelper;

	public Game createGame() {
		Game newGame = new Game();
		games.put(newGame.getId(), newGame);
		return newGame;
	}

	public Game getGame(String gameID) {
		return this.games.get(gameID);
	}

	public void deleteGame(String gameID) {
		this.games.remove(gameID);
	}

	public List<GameObject> getAllObjects(String gameID) {
		return games.get(gameID).getGameObjects();
	}

	public GameObject getRandomObject(String gameID) {
		if (gameID!=null)
			return games.get(gameID).getRandomGameObject();
		else
			return null;
	}

	public void deleteObject(String gameID, String objectID, String objectType, String objectName) {
		if ((gameID!=null) || (!"".equals(gameID)))
			if ((objectID!=null) || ("".equals(objectID)))
				games.get(gameID).deleteGameObject(new GameObject(objectID, objectName, objectType));
	}

	public void startGame(String gameID) {
		Game game = games.get(gameID);
		if (game!=null) {
			game.addListGameObject(objectHelper.getPlatformObjects());
		}else{
			System.out.println("No game with ID: " + gameID);
		}
	}

	public void endGame(String gameID, int score) {
		Game game = games.get(gameID);
		if (game!=null) {
			game.setScore(score);
			System.out.println("Score is " + score);
		}else{
			System.out.println("No game with ID: " + gameID);
		}
	}
}

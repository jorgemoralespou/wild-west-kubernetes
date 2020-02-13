package com.vmware.wildwest.controllers;

import java.util.List;

import com.vmware.wildwest.models.Game;
import com.vmware.wildwest.models.GameObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = "*")
@RestController
public class APIController {
	@Autowired
	private GameController gameController;

	@RequestMapping("/score")
	public int getScore(@RequestParam(value = "gameID") String gameID) {
		return this.gameController.getGame(gameID).getScore();
	}

	@RequestMapping("/createGame")
	public Game createGame() {
		return gameController.createGame();
	}

	@RequestMapping("/startGame")
	public void startGame(@RequestParam(value = "gameID") String gameID) {
		gameController.startGame(gameID);
	}

	@RequestMapping("/endGame")
	public void endGame(@RequestParam(value = "gameID") String gameID,
						@RequestParam(value = "score") int score) {
		gameController.endGame(gameID, score);
	}

	@RequestMapping("/objects")
	public List<GameObject> getGameObjects(@RequestParam(value = "gameID") String gameID) {
		return gameController.getAllObjects(gameID);
	}

	@RequestMapping("/getRandomObject")
	public GameObject getRandomPlatformObject(@RequestParam(value = "gameID") String gameID) {
		return gameController.getRandomObject(gameID);
	}

	@RequestMapping("/deleteObject")
	public void deletePlatformObject(@RequestParam(value = "gameID") String gameID, 
							         @RequestParam(value = "id") String objectID,
									 @RequestParam(value = "name") String objectName,
									 @RequestParam(value = "type") String objectType
							) {

		gameController.deleteObject(gameID, objectID, objectType, objectName);
	}

}
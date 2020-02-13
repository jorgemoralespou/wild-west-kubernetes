    //*******************************************************************
    //**  Wild West K8s
    //**  Author: Grant Shipley @gshipley
    //**  Thanks to Marek Jelen, Jorge Morales, and RyanJ
    //**  Shootem-up game to kill K8s pods
    //*******************************************************************
    var thisPhaser = {
        game: null,
        emitter: null,
        gunSight: null,
        gunshot: null,
        gameLoop: null,
        frameObject: null,
        line: '',
        yeehaw: null,
        explosion: null,
        button: null,
        activated: false
    }

    var currSpriteObject;  // This is the sprite in the game representing the current K8s Object
    var currGameObject;

    var index=0;

    var thisScreen = {
        objectText: null,
        introText: null,
        scoreText: null,
        killFrameText: null
    }

    var introText = [
        " ",
        "Wild West K8s",
        "",
        "Press start to shoot some pods"
    ];

    var locations = [
        [15,145],  // roofs
        [185,145],
        [240,90],
        [450,105],
        [520,125],
        [660,125],
        [735,225],
        [905,225],
        [940,145],
        [1100,145],
        [550,275], // balconies
        [690,275],
        [795,310],
        [915,310],
        [1035,275],
        [335,410], // doors
        [580,425],
        [830,430],
        [1025,420],
        [40,615], // ground
        [525,685],
        [845,665]
    ];
    var currentGame = {
        username: 'Jorge', // TODO: Get username from real place
        id: '',
        score: 0
    }

    // We need to create the game on the server
    $.ajax({
        url: '/createGame',
        async: false,
        type: 'GET',
        success: function(results) {
            console.log("Requested via ajax: /createGame");
            currentGame.id = results.id;
            console.log("Game id: " + currentGame.id);

            // Now that we have a gameID from the server, we can start the game
            thisPhaser.game = new Phaser.Game(1151, 768, Phaser.AUTO, 'k8sgame', { preload: preload, create: create, update: update, render: render });
        }
    });

    function preload() {
        thisPhaser.game.load.image('playfield', 'assets/gameplayfield.png');
        thisPhaser.game.load.image('gunsight', 'assets/gunsight.png');
        thisPhaser.game.load.audio('gunshot', 'assets/gunshot.wav');
        thisPhaser.game.load.image('SERVICE', 'assets/svc-48.png');
        thisPhaser.game.load.image('POD', 'assets/pod-48.png');
        thisPhaser.game.load.image('PVC', 'assets/pvc-48.png');
        thisPhaser.game.load.audio('yeehaw', 'assets/yeehaw.wav');
        thisPhaser.game.load.audio('explosion', 'assets/explosion.wav');
        thisPhaser.game.load.image('killframe', 'assets/frame.png');
        thisPhaser.game.load.image('start-button','assets/start.png')
    }

    var clickHandler = function () {
        thisPhaser.gunshot.play();
        // Check if the gunsight is over the currentObject

        if(checkOverlap(thisPhaser.gunSight, currSpriteObject)) {
            // Add the emitter for the explosion and play the yeehaw for target hit
            thisPhaser.explosion.play();
            thisPhaser.emitter = thisPhaser.game.add.emitter(0, 0, 100);
            // TODO: [JMP] Check that we're providing back the type
            thisPhaser.emitter.makeParticles(currGameObject.type);
            thisPhaser.emitter.gravity = 200;
            //  Position the emitter where the mouse/touch event was
            thisPhaser.emitter.x = locations[currLocation][0];
            thisPhaser.emitter.y = locations[currLocation][1];
            //  The first parameter sets the effect to "explode" which means all particles are emitted at once
            //  The second gives each particle a 2000ms lifespan
            //  The third is ignored when using burst/explode mode
            //  The final parameter (10) is how many particles will be emitted in this single burst
            thisPhaser.emitter.start(true, 2000, null, 10);

            // TODO: [JMP] Send score to the server
            // TODO: Add score to the ObjectType
            currentGame.score += 30;

            // delete the object on the game server
            deleteObject(currentGame.id, currGameObject);

            currSpriteObject.destroy();
            thisScreen.objectText.text="";
        } else {
            // The player missed the target and should be penalized with a deduction in score
            // TODO: [JMP] Send score to the server
            currentGame.score -= 30;
        }
        displayScore(currentGame.username, currentGame.score);
    };

    function startGame(){
        // We need to create the game on the server
        $.ajax({
            url: '/startGame',
            async: false,
            type: 'GET',
            data: { gameID: currentGame.id },
            success: function(results) {
                console.log("Requested via ajax: /startGame");
                console.log("Started game id: " + currentGame.id);
            }
        });
        thisPhaser.button.destroy();
        thisPhaser.activated = true;

        // load the gun sights
        thisPhaser.gunSight = thisPhaser.game.add.sprite(thisPhaser.game.world.centerX, thisPhaser.game.world.centerY, 'gunsight');
        thisPhaser.gunSight.anchor.set(0.5);
        thisPhaser.game.physics.arcade.enable(thisPhaser.gunSight);
        thisPhaser.gunSight.inputEnabled = true;

        startGameDisplayLoop();

        // If the player fired their weapon
        thisPhaser.gunSight.events.onInputDown.add(clickHandler, this);
    }

    function create() {
        // load the playfield background image
        var playfield = thisPhaser.game.add.image(thisPhaser.game.world.centerX, thisPhaser.game.world.centerY, 'playfield');
        playfield.anchor.setTo(0.5, 0.5);

        // Start the physics system for the gunsight and explosion
        thisPhaser.game.physics.startSystem(Phaser.Physics.ARCADE);

        thisScreen.introText = thisPhaser.game.add.text(32, 660, '', { font: "26pt Courier", fill: "#000000", stroke: "#000000", strokeThickness: 2 });
        thisScreen.scoreText = thisPhaser.game.add.text(765, 10, 'User: ' + currentGame.username + '\nScore: 000', { font: "16pt Courier", fill: "#000000", stroke: "#000000", strokeThickness: 2 });
        thisScreen.objectText = thisPhaser.game.add.text(32, 670, '', { font: "16pt Courier", fill: "#000000", stroke: "#000000", strokeThickness: 2 });

        thisPhaser.gunshot = thisPhaser.game.add.audio('gunshot'); // Load the gunshot audio
        thisPhaser.yeehaw = thisPhaser.game.add.audio('yeehaw'); // Load the gunshot yeehaw
        thisPhaser.yeehaw.play();  // Play the intro sound
        thisPhaser.explosion = thisPhaser.game.add.audio('explosion'); // Set the explosion sound

        displayIntroText();
    }

    function displayScore(user, score) {
        thisScreen.scoreText.text = "User: " + user + "\nScore: " + score;
    }

    function displayObject() {
        // Get a random location from the location array as defined in the locations array
        currLocation = getRandomLocation(0, locations.length - 1);

        // Get a random object from the kubernetes API
        getRandomObject();

        if (currGameObject) {
            // Add the object to the playfield using the random location
            currSpriteObject = thisPhaser.game.add.sprite(locations[currLocation][0], locations[currLocation][1], currGameObject.type);

            //delete the kubernetes object after it has been visible for 3 seconds.
            thisPhaser.game.time.events.add(Phaser.Timer.SECOND * 2, function () {
                // Only delete if currGameObject has not been properly shot
                if (currGameObject) {
                    currSpriteObject.destroy();
                    thisScreen.objectText.text = "";
                    // delete the object on the game server
                    deleteObject(currentGame.gameId, currGameObject);
                    currentGame.score -= 30;
                    displayScore(currentGame.username, currentGame.score);
                }
                // currSpriteObject.destroy();
                // thisScreen.objectText.text = "";
            });
            thisPhaser.gunSight.bringToTop();
        }
    }
    
    function getRandomObject() {
        $.ajax({
            url: '/getRandomObject',
            async: false,
            type: 'GET',
            data: { gameID: currentGame.id },
            success: function(results) {
//                currSpriteObject = results;
                currGameObject = results;
                console.log("Results:" + results);
                if (results){
                    thisScreen.objectText.text = "Type: " + results.type + "\nName: " + results.name + "\nID: " + results.id;
                }else {
                    displayGameOver();
                }

            },
            error: function (jqXHR, textStatus, error) {
                //TODO: GAME OVER
                console.log("Error " + textStatus + " getting a random object: " + error);
                displayGameOver();
            }
        });
    }

    function deleteObject() {
        $.ajax({
            url: '/deleteObject',
            async: false,
            type: 'GET',
            data: { gameID: currentGame.id, id : currGameObject.id, name : currGameObject.name, type : currGameObject.type },
            success: function() {
                    console.log("Deleted object ["+currGameObject.id+"] from gameId ["+currentGame.id);
            },
            error: function() {
                    console.log("Error deleting object ["+currGameObject.id+"] from gameId ["+currentGame.id);
            }
        })
        currGameObject = null;
    }

    function displayGameOver() {
        thisPhaser.gunSight.events.onInputDown.removeAll();
        stopGameDisplayLoop();

        thisPhaser.frameObject = thisPhaser.game.add.sprite(220, 153, 'killframe');
        thisPhaser.frameObject.inputEnabled = true;

        thisScreen.killFrameText = thisPhaser.game.add.text(330, 270, '', { font: "26pt Courier", fill: "#000000", stroke: "#000000", strokeThickness: 2 });
        thisScreen.killFrameText.setText("GAME OVER!!! \nYour score is: " + currentGame.score);
        /*
              frameObject.events.onInputDown.add(function() {
                  frameObject.destroy();
                  thisScreen.killFrameText.destroy();
              }, this);
        */
        endGame();
    }

    function endGame(){
        // We need to create the game on the server
        $.ajax({
            url: '/endGame',
            async: false,
            type: 'GET',
            data: { gameID: currentGame.id, score: currentGame.score },
            success: function(results) {
                console.log("Requested via ajax: /endGame");
                console.log("Ended game id: " + currentGame.id);
            }
        });
    }

    function checkOverlap(spriteA, spriteB) {
        if(spriteA && spriteB) {
            if (spriteA.visible && spriteB.visible) {
                return Phaser.Rectangle.intersects(spriteA.getBounds(), spriteB.getBounds());
            }
        }

    }

    function getRandomLocation(min,max){
        var i = Math.floor(Math.random()*(max-min+1)+min);
        console.log("Location " + i);
        return i;
    }

    function update() {
        if (thisPhaser.activated) {
            //  If the gunsight is > 8px away from the pointer then let's move to it
            if (thisPhaser.game.physics.arcade.distanceToPointer(thisPhaser.gunSight, thisPhaser.game.input.activePointer) > 8) {
                //  Make the object seek to the active pointer (mouse or touch).
                thisPhaser.game.physics.arcade.moveToPointer(thisPhaser.gunSight, 300, thisPhaser.game.input.activePointer, 100);
            } else {
                //  Otherwise turn off velocity because we're close enough to the pointer
                thisPhaser.gunSight.body.velocity.set(0);
            }
        }
    }

    function displayIntroTextUpdateLine() {

        if (thisPhaser.line.length < introText[index].length)
        {
            thisPhaser.line = introText[index].substr(0, thisPhaser.line.length + 1);
            // text.text = line;
            thisScreen.introText.setText(thisPhaser.line);
        }
        else
        {
            //  Wait 2 seconds then start a new line
            thisPhaser.game.time.events.add(Phaser.Timer.SECOND * 1, displayIntroText, this);
        }

    }

    function displayIntroText() {
        index++;

        if (index < introText.length)
        {
            thisPhaser.line = '';
            thisPhaser.game.time.events.repeat(80, introText[index].length + 1, displayIntroTextUpdateLine, this);
        } else {
            thisScreen.introText.destroy();
            showStartButton();
        }
    }

    function showStartButton() {
        // Add button to start Game
        thisPhaser.button = thisPhaser.game.add.button(thisPhaser.game.world.centerX - 50, thisPhaser.game.world.centerX - 50, 'start-button', startGame, this, 2, 1, 0);
        thisPhaser.button.input.useHandCursor = true;
        thisPhaser.button.inputEnabled = true;
    }

    function startGameDisplayLoop() {
        thisPhaser.gameLoop = thisPhaser.game.time.events.loop(Phaser.Timer.SECOND * 3, displayObject, this);
    }

    function stopGameDisplayLoop() {
        thisPhaser.game.time.events.remove(thisPhaser.gameLoop);
    }

    function render() {
        // If you are working / modifying this code base,
        // uncomment the following line to display helpful information
        // in the top left corner

       // thisPhaser.game.debug.inputInfo(32, 32);
    }

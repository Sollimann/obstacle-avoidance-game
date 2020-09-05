package com.obstacleavoid.game.screen.game

import com.badlogic.gdx.math.MathUtils
import com.obstacleavoid.game.config.DifficultyLevel
import com.obstacleavoid.game.config.GameConfig
import com.obstacleavoid.game.entity.Obstacle
import com.obstacleavoid.game.entity.Player
import com.obstacleavoid.game.util.GdxArray

class GameController {

    // private properties
    private val startPlayerX = GameConfig.WORLD_WIDTH / 2f
    private val startPlayerY = 1f
    private var obstacleTimer = 0f
    private var scoreTimer = 0f
    private var difficultyLevel = DifficultyLevel.MEDIUM // enum setter


    // public properties
    val gameOver
        // boolean game over property using a getter
        // everytime we cal the gameOver val, it will execute
        // and return the result of lives <= 0
        get() = lives <= 0

    val obstacles = GdxArray<Obstacle>()

    var player = Player()
        private set // can only set player inside GameController class
    var lives = GameConfig.LIVES_START
        private set // can only set player inside GameController class
    var score = 0
        private set
    var displayScore = 0

    // init
    init {
        // could have replaced this init block with:
        // var player = Player().apply { setPosition(startPlayerX, startPlayerY) }
        //     private set

        // position player
        player.setPosition(startPlayerX, startPlayerY)

    }

    // public functions
    fun update(delta: Float) {
        // update game world
        player.update()
        blockPlayerFromLeavingWorldBounds()

        updateObstacles()
        createNewObstacle(delta)
        updateScore(delta)
        updateDisplayScore(delta)

        if (isPlayerCollidingWithObstacle()) {
            lives--
        }
    }

    // private functions
    private fun updateScore(delta: Float) {
        scoreTimer += delta

        if (scoreTimer >= GameConfig.SCORE_MAX_TIME) {
            scoreTimer = 0f
            score += MathUtils.random(1, 5)
        }
    }

    private fun updateDisplayScore(delta: Float) {

        // condition tells us we have to update score in game UI
        if (displayScore < score) {
            displayScore = Math.min(score, displayScore + (60 * delta).toInt())
        }
    }

    private fun isPlayerCollidingWithObstacle(): Boolean {
        obstacles.forEach {
            if (!it.hit && it.isCollidingWith(gameObject = player)) {
                return true
            }
        }

        return false
    }

    private fun updateObstacles() {
        obstacles.forEach { it.update() } // same as writing obstacle -> obstacle.update()
    }

    private fun createNewObstacle(delta: Float) {
        obstacleTimer += delta

        if (obstacleTimer >= GameConfig.OBSTACLE_SPAWN_TIME) {
            obstacleTimer = 0f // reset timer

            // spawn obstacle at random x position
            val obstacleX = MathUtils.random(0f, GameConfig.WORLD_WIDTH)
            val obstacle = Obstacle()
            obstacle.setPosition(obstacleX, GameConfig.WORLD_HEIGHT)

            // set the obstacle speed
            obstacle.ySpeed = difficultyLevel.obstacleSpeed // enum getter

            // add to array
            obstacles.add(obstacle)
        }
    }

    private fun blockPlayerFromLeavingWorldBounds() {
        player.x = MathUtils.clamp(player.x, Player.HALF_SIZE, GameConfig.WORLD_WIDTH - Player.HALF_SIZE)
    }
}
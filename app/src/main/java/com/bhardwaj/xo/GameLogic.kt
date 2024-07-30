package com.bhardwaj.xo

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.gms.ads.*
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import java.util.*

class GameLogic : AppCompatActivity(), OnUserEarnedRewardListener {
    private var playerO: String? = null
    private var playerX: String? = null
    private var llGameBoard: LinearLayout? = null
    private var clResultScreen: ConstraintLayout? = null
    private var clPlayingScreen: ConstraintLayout? = null
    private var clDifficultySelectionScreen: ConstraintLayout? = null
    private var tvPlayerXTurn: TextView? = null
    private var tvPlayerYTurn: TextView? = null
    private var tvResultText: TextView? = null
    private lateinit var currentGridImagesArray: Array<ImageView>
    private var ivResultImage: ImageView? = null
    private var imResultResetButton: ImageView? = null
    private var mediumChancesCalculator = 0
    private lateinit var winningConditionsList: Array<IntArray>
    private var anyWinningConditionSatisfy = false
    private var chanceOfPlayer: String? = null
    private var difficultyLevelUserSelected: String? = null
    private var gameModeUserSelected: String? = null
    private lateinit var currentStateOfBoard: Array<String?>
    private var mRewardedAd: RewardedAd? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_layout)
        initialise()
        loadBannerAds()
        loadRewardedAds()
    }

    private fun initialise() {
        playerO = getString(R.string.player_o)
        playerX = getString(R.string.player_x)
        tvPlayerXTurn = findViewById(R.id.tvPlayerXTurn)
        tvPlayerYTurn = findViewById(R.id.tvPlayerYTurn)
        llGameBoard = findViewById(R.id.llGameBoard)
        clResultScreen = findViewById(R.id.clResultScreen)
        ivResultImage = findViewById(R.id.ivResultImage)
        tvResultText = findViewById(R.id.tvResultText)
        imResultResetButton = findViewById(R.id.imResultResetButton)
        clPlayingScreen = findViewById(R.id.clPlayingScreen)
        clDifficultySelectionScreen = findViewById(R.id.clDifficultySelectionScreen)
        winningConditionsList = arrayOf(
            intArrayOf(0, 1, 2),
            intArrayOf(3, 4, 5),
            intArrayOf(6, 7, 8),
            intArrayOf(0, 3, 6),
            intArrayOf(1, 4, 7),
            intArrayOf(2, 5, 8),
            intArrayOf(0, 4, 8),
            intArrayOf(2, 4, 6)
        )
        gameModeUserSelected = intent.getStringExtra("Game")
        findViewById<View>(R.id.btnEasyComputer).setOnClickListener {
            difficultyLevelUserSelected = "easy"
            resetGame()
        }
        findViewById<View>(R.id.btnMediumComputer).setOnClickListener {
            difficultyLevelUserSelected = "medium"
            resetGame()
        }
        findViewById<View>(R.id.btnHardComputer).setOnClickListener {
            difficultyLevelUserSelected = "hard"
            resetGame()
        }
        resetGame()
        if (gameModeUserSelected == "SinglePlayer") {
            clDifficultySelectionScreen?.visibility = View.VISIBLE
            clPlayingScreen?.visibility = View.GONE
        }
    }

    private fun resetGame() {
        anyWinningConditionSatisfy = false
        chanceOfPlayer = playerX
        mediumChancesCalculator = 3
        currentStateOfBoard = arrayOf("", "", "", "", "", "", "", "", "")
        currentGridImagesArray = arrayOf(
            findViewById(R.id.ivBox0),
            findViewById(R.id.ivBox1),
            findViewById(R.id.ivBox2),
            findViewById(R.id.ivBox3),
            findViewById(R.id.ivBox4),
            findViewById(R.id.ivBox5),
            findViewById(R.id.ivBox6),
            findViewById(R.id.ivBox7),
            findViewById(R.id.ivBox8)
        )
        for (imageView in currentGridImagesArray) imageView.setImageResource(0)
        tvPlayerXTurn!!.visibility = View.VISIBLE
        tvPlayerYTurn!!.visibility = View.INVISIBLE
        clResultScreen!!.visibility = View.GONE
        llGameBoard!!.visibility = View.VISIBLE
        clDifficultySelectionScreen!!.visibility = View.GONE
        clPlayingScreen!!.visibility = View.VISIBLE
        if (gameModeUserSelected == "SinglePlayer") {
            tvPlayerXTurn!!.visibility = View.INVISIBLE
            tvPlayerYTurn!!.visibility = View.INVISIBLE
        }

//        if (mRewardedAd != null) {
//            mRewardedAd.show(this, rewardItem -> {
//            });
//            loadRewardedAds();
//        }
    }

    fun showChangesOnBoxSelected(view: View) {
        val tappedBoxTag = view.tag.toString().toInt()
        if (gameModeUserSelected == "MultiPlayer") {
            if (chanceOfPlayer == playerO && currentStateOfBoard[tappedBoxTag] == "") {
                currentGridImagesArray[tappedBoxTag].setImageResource(R.drawable.o_symbol)
                tvPlayerYTurn!!.visibility = View.INVISIBLE
                tvPlayerXTurn!!.visibility = View.VISIBLE
                currentStateOfBoard[tappedBoxTag] = playerO
                chanceOfPlayer = playerX
            }
            if (chanceOfPlayer == playerX && currentStateOfBoard[tappedBoxTag] == "") {
                currentGridImagesArray[tappedBoxTag].setImageResource(R.drawable.x_symbol)
                tvPlayerXTurn!!.visibility = View.INVISIBLE
                tvPlayerYTurn!!.visibility = View.VISIBLE
                currentStateOfBoard[tappedBoxTag] = playerX
                chanceOfPlayer = playerO
            }
        } else if (gameModeUserSelected == "SinglePlayer") {
            if (currentStateOfBoard[tappedBoxTag] == "") {
                if (isMovesLeftInGame) {
                    currentGridImagesArray[tappedBoxTag].setImageResource(R.drawable.x_symbol)
                    currentStateOfBoard[tappedBoxTag] = playerX
                }
                if (isMovesLeftInGame) {
                    val computerMoveBasedOnDifficulty: Int = if (difficultyLevelUserSelected == "easy") computerEasyMoves else if (difficultyLevelUserSelected == "medium") computerMediumMoves else computerHardMoves
                    currentGridImagesArray[computerMoveBasedOnDifficulty].setImageResource(R.drawable.o_symbol)
                    currentStateOfBoard[computerMoveBasedOnDifficulty] = playerO
                }
            }
        }
        val winner = checkForWinningConditions()
        if (winner == playerX) {
            anyWinningConditionSatisfy = true
            showTheWinnerAs(playerX)
        }
        if (winner == playerO) {
            anyWinningConditionSatisfy = true
            showTheWinnerAs(playerO)
        }
        if (winner == "" && !isMovesLeftInGame && !anyWinningConditionSatisfy) {
            showTheWinnerAs("")
        }
    }

    private val isMovesLeftInGame: Boolean
        get() {
            for (i in 0..8) if (currentStateOfBoard[i] == "") return true
            return false
        }

    private fun checkForWinningConditions(): String? {
        for (innerPositions in winningConditionsList) if (currentStateOfBoard[innerPositions[0]] == currentStateOfBoard[innerPositions[1]] && currentStateOfBoard[innerPositions[1]] == currentStateOfBoard[innerPositions[2]] && currentStateOfBoard[innerPositions[0]] != "") return if (currentStateOfBoard[innerPositions[0]] == playerX) playerX else playerO
        return ""
    }

    private fun showTheWinnerAs(player: String?) {
        llGameBoard!!.visibility = View.GONE
        clResultScreen!!.visibility = View.VISIBLE
        var win = getString(R.string.you_win)
        var lose = getString(R.string.you_lose)
        val draw = getString(R.string.its_a_draw)
        if (gameModeUserSelected == "MultiPlayer") {
            win = getString(R.string.player_1_wins)
            lose = getString(R.string.player_2_wins)
        }
        if (player == "") {
            ivResultImage!!.setImageResource(R.drawable.draw)
            tvResultText!!.text = draw
        } else if (player == playerX) {
            tvResultText!!.text = win
            ivResultImage!!.setImageResource(R.drawable.win)
        } else {
            tvResultText!!.text = lose
            ivResultImage!!.setImageResource(R.drawable.lose)
            if (gameModeUserSelected == "MultiPlayer") {
                ivResultImage!!.setImageResource(R.drawable.win)
            }
        }
        imResultResetButton!!.setOnClickListener { resetGame() }
    }

    private val computerEasyMoves: Int
        get() {
            var easyMove = 0
            while (currentStateOfBoard[easyMove] != "") easyMove = Random().nextInt(9)
            return easyMove
        }
    private val computerMediumMoves: Int
        get() = if (mediumChancesCalculator == 0) {
            computerEasyMoves
        } else {
            mediumChancesCalculator--
            computerHardMoves
        }
    private val computerHardMoves: Int
        get() {
            var bestScore = Int.MIN_VALUE
            var bestHardMove = 0
            for (i in 0..8) if (currentStateOfBoard[i] == "") {
                currentStateOfBoard[i] = playerO
                val currentScore = alphaBetaPruning(0, false, Int.MIN_VALUE, Int.MAX_VALUE)
                currentStateOfBoard[i] = ""
                if (currentScore > bestScore) {
                    bestScore = currentScore
                    bestHardMove = i
                }
            }
            return bestHardMove
        }

    private fun alphaBetaPruning(depth: Int, isMaximizing: Boolean, alpha: Int, beta: Int): Int {
        var alphaInside = alpha
        var betaInside = beta
        if (checkForWinningConditions() == playerX) return -1 else if (checkForWinningConditions() == playerO) return +1 else if (!isMovesLeftInGame) return 0
        var bestScore = if (isMaximizing) Int.MIN_VALUE else Int.MAX_VALUE
        if (isMaximizing) {
            for (i in 0..8) {
                if (currentStateOfBoard[i] == "") {
                    currentStateOfBoard[i] = playerO
                    val score = alphaBetaPruning(depth + 1, false, alphaInside, betaInside)
                    currentStateOfBoard[i] = ""
                    bestScore = score.coerceAtLeast(bestScore)
                    alphaInside = alpha.coerceAtLeast(bestScore)
                    if (betaInside <= alphaInside) break
                }
            }
        } else {
            for (i in 0..8) {
                if (currentStateOfBoard[i] == "") {
                    currentStateOfBoard[i] = playerX
                    val score = alphaBetaPruning(depth + 1, true, alphaInside, betaInside)
                    currentStateOfBoard[i] = ""
                    bestScore = score.coerceAtMost(bestScore)
                    betaInside = betaInside.coerceAtMost(bestScore)
                    if (betaInside <= alphaInside) break
                }
            }
        }
        return bestScore
    }

    private fun loadRewardedAds() {
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(
            this,
            getString(R.string.rewarded_id),
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(rewardedAd: RewardedAd) {
                    mRewardedAd = rewardedAd
                    mRewardedAd!!.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdShowedFullScreenContent() {
                            mRewardedAd = null
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {}
                        override fun onAdDismissedFullScreenContent() {}
                    }
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    mRewardedAd = null
                }
            })
    }

    private fun loadBannerAds() {
        val bannerAds = findViewById<AdView>(R.id.adsGameActivity)
        val adRequest = AdRequest.Builder().build()
        bannerAds.loadAd(adRequest)
    }

    override fun onUserEarnedReward(rewardItem: RewardItem) {}
}
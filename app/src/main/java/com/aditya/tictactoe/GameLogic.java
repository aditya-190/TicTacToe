package com.aditya.tictactoe;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.Random;

public class GameLogic extends AppCompatActivity {

    private final String O = "O", X = "X";
    private LinearLayout grid;
    private ConstraintLayout result, playActivity, levelActivity;
    private TextView XTurn, OTurn, resultText;
    private ImageView[] boxes;
    private ImageView image, reset;
    private int whyMedium;
    private int[][] winningCondition;
    private boolean anyWin;
    private String chanceOf, difficultyLevel, gameMode;
    private String[] state;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_layout);

        // Referencing.
        XTurn = findViewById(R.id.player_X_turn);
        OTurn = findViewById(R.id.player_O_turn);
        grid = findViewById(R.id.game_grid);
        result = findViewById(R.id.result);
        image = findViewById(R.id.result_image);
        resultText = findViewById(R.id.result_text);
        reset = findViewById(R.id.result_reset);
        playActivity = findViewById(R.id.game_play);
        levelActivity = findViewById(R.id.difficulty);
        winningCondition = new int[][]{{0, 1, 2}, {3, 4, 5}, {6, 7, 8}, {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, {0, 4, 8}, {2, 4, 6}};

        // Getting the Chosen Activity From MainActivity.
        gameMode = getIntent().getStringExtra("Game");

        // Executes when Easy Level is chosen.
        findViewById(R.id.easy_bot).setOnClickListener(v -> {
            difficultyLevel = "easy";
            gameReset();
        });

        // Executes when Medium Level is chosen.
        findViewById(R.id.medium_bot).setOnClickListener(v -> {
            difficultyLevel = "medium";
            gameReset();
        });

        // Executes when Hard Level is chosen.
        findViewById(R.id.hard_bot).setOnClickListener(v -> {
            difficultyLevel = "hard";
            gameReset();
        });

        gameReset();

        // Show Select Difficulty Section First when Single Player is Chosen.
        if (gameMode.equals("SinglePlayer")) {
            levelActivity.setVisibility(View.VISIBLE);
            playActivity.setVisibility(View.GONE);
        }
    }

    private void gameReset() {
        anyWin = false;
        chanceOf = X;
        whyMedium = 3;
        state = new String[]{"", "", "", "", "", "", "", "", ""};

        // Array of Boxes that we clicks.
        boxes = new ImageView[]{findViewById(R.id.box_0), findViewById(R.id.box_1), findViewById(R.id.box_2), findViewById(R.id.box_3), findViewById(R.id.box_4), findViewById(R.id.box_5), findViewById(R.id.box_6), findViewById(R.id.box_7), findViewById(R.id.box_8)};

        // Setting Image to Null After Game and Before.
        for (ImageView imageView : boxes) imageView.setImageResource(0);

        // Turn Decider
        XTurn.setVisibility(View.VISIBLE);
        OTurn.setVisibility(View.INVISIBLE);

        // Result Pop Up is Disabled and Game Grid Enabled when Game Starts.
        result.setVisibility(View.GONE);
        grid.setVisibility(View.VISIBLE);

        // Removing Difficult Selection Menu and Displaying Game Grid.
        levelActivity.setVisibility(View.GONE);
        playActivity.setVisibility(View.VISIBLE);

        // No Turn Decider when User in SinglePlayerMode.
        if (gameMode.equals("SinglePlayer")) {
            XTurn.setVisibility(View.INVISIBLE);
            OTurn.setVisibility(View.INVISIBLE);
        }
    }

    public void boxTapped(View view) {

        // Gets the ID of the Tapped Button.
        int tappedBoxTag = Integer.parseInt(view.getTag().toString());
        if (gameMode.equals("MultiPlayer")) {

            // Checking the Turn of the Player.
            if (chanceOf.equals(O) && state[tappedBoxTag].equals("")) {
                boxes[tappedBoxTag].setImageResource(R.drawable.o_symbol);
                OTurn.setVisibility(View.INVISIBLE);
                XTurn.setVisibility(View.VISIBLE);
                state[tappedBoxTag] = O;
                chanceOf = X;
            }
            if (chanceOf.equals(X) && state[tappedBoxTag].equals("")) {
                boxes[tappedBoxTag].setImageResource(R.drawable.x_symbol);
                XTurn.setVisibility(View.INVISIBLE);
                OTurn.setVisibility(View.VISIBLE);
                state[tappedBoxTag] = X;
                chanceOf = O;
            }
        } else if (gameMode.equals("SinglePlayer")) {
            if (state[tappedBoxTag].equals("")) {

                // User Chance.
                if (isMovesLeft()) {
                    boxes[tappedBoxTag].setImageResource(R.drawable.x_symbol);
                    state[tappedBoxTag] = X;
                }

                // Computer Chance.
                if (isMovesLeft()) {
                    int computerMove;

                    if (difficultyLevel.equals("easy")) computerMove = getEasyMove();
                    else if (difficultyLevel.equals("medium")) computerMove = getMediumMove();
                    else computerMove = getHardMove();

                    boxes[computerMove].setImageResource(R.drawable.o_symbol);
                    state[computerMove] = O;
                }
            }
        }

        // Checking if there is an winning Combination.
        String winner = checkWinner();
        if (winner.equals(X)) {
            anyWin = true;
            displayWinner(X);
        }
        if (winner.equals(O)) {
            anyWin = true;
            displayWinner(O);
        }
        if (winner.equals("") && !isMovesLeft() && !anyWin) {
            displayWinner("");
        }
    }

    private int getEasyMove() {
        int number = 0;
        while (!state[number].equals(""))
            number = new Random().nextInt(9);
        return number;
    }

    private int getMediumMove() {
        if (whyMedium == 0) {
            return getEasyMove();
        } else {
            whyMedium--;
            return getHardMove();
        }
    }

    private int getHardMove() {
        int bestScore = Integer.MIN_VALUE, move = 0;
        for (int i = 0; i < 9; i++)
            if (state[i].equals("")) {
                state[i] = O;
                int currentScore = alphaBetaPruning(0, false, Integer.MIN_VALUE, Integer.MAX_VALUE);
                state[i] = "";
                if (currentScore > bestScore) {
                    bestScore = currentScore;
                    move = i;
                }
            }
        return move;
    }

    private boolean isMovesLeft() {
        for (int i = 0; i < 9; i++)
            if (state[i].equals(""))
                return true;
        return false;
    }

    private String checkWinner() {
        for (int[] innerPositions : winningCondition)
            if (state[innerPositions[0]].equals(state[innerPositions[1]]) && state[innerPositions[1]].equals(state[innerPositions[2]]) && !state[innerPositions[0]].equals(""))
                if (state[innerPositions[0]].equals(X)) return X;
                else return O;
        return "";
    }

    private void displayWinner(String player) {

        // Removing Game Grid and Showing the result layout.
        grid.setVisibility(View.GONE);
        result.setVisibility(View.VISIBLE);

        // Setting Winning Text, Losing Text and Draw Text.
        String win = "You Win!!!", lose = "You Lose!!!", draw = "It's a Draw";

        if (gameMode.equals("MultiPlayer")) {
            win = "Player 1 Wins";
            lose = "Player 2 Wins";
        }

        if (player.equals("")) {
            image.setImageResource(R.drawable.draw);
            resultText.setText(draw);
        } else if (player.equals(X)) {
            resultText.setText(win);
            image.setImageResource(R.drawable.win);
        } else {
            resultText.setText(lose);
            image.setImageResource(R.drawable.lose);

            // There is always a winner if not draw in Local Multi-Player - Either Player 1 or 2.
            if (gameMode.equals("MultiPlayer"))
                image.setImageResource(R.drawable.win);
        }

        // Calls when Refresh button is clicked.
        reset.setOnClickListener(v -> gameReset());
    }

    private int alphaBetaPruning(int depth, boolean isMaximizing, int alpha, int beta) {
        if (checkWinner().equals(X)) return -1;
        else if (checkWinner().equals(O)) return +1;
        else if (!isMovesLeft()) return 0;

        int bestScore = isMaximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        if (isMaximizing) {
            for (int i = 0; i < 9; i++) {
                if (state[i].equals("")) {
                    state[i] = O;
                    int score = alphaBetaPruning(depth + 1, false, alpha, beta);
                    state[i] = "";
                    bestScore = Math.max(score, bestScore);
                    alpha = Math.max(alpha, bestScore);
                    if (beta <= alpha)
                        break;
                }
            }
        } else {
            for (int i = 0; i < 9; i++) {
                if (state[i].equals("")) {
                    state[i] = X;
                    int score = alphaBetaPruning(depth + 1, true, alpha, beta);
                    state[i] = "";
                    bestScore = Math.min(score, bestScore);
                    beta = Math.min(beta, bestScore);
                    if (beta <= alpha)
                        break;
                }
            }
        }
        return bestScore;
    }
}
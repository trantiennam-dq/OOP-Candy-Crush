package main.model;

public class LevelManager {
    private static LevelManager instance;
    private int currentLevel = 1;
    private int movesLeft = 25;
    private int targetScore = 500;
    private boolean gameOver = false;
    private boolean gameWon = false;
    private java.util.Map<Integer, Integer> highScores = new java.util.HashMap<>();

    private LevelManager() {}

    public static LevelManager getInstance() {
        if (instance == null) {
            instance = new LevelManager();
        }
        return instance;
    }

    public void startLevel(int level) {
        this.currentLevel = level;
        this.movesLeft = 25;
        this.gameOver = false;
        this.gameWon = false;
        this.targetScore = getTargetScoreForLevel(level);
    }

    public int getTargetScoreForLevel(int level) {
        switch (level) {
            case 1: return 500;
            case 2: return 700;
            case 3: return 900;
            default: return 900;
        }
    }

    public int getHighScore(int level) {
        return highScores.getOrDefault(level, 0);
    }
    
    public void saveScoreIfHigher(int level, int score) {
        int currentHigh = getHighScore(level);
        if (score > currentHigh) {
            highScores.put(level, score);
        }
    }

    public static int calculateStars(int score, int targetScore) {
        if (score >= targetScore * 3) return 3;
        if (score >= targetScore * 2) return 2;
        if (score >= targetScore) return 1;
        return 0;
    }

    public void resetCurrentLevel() {
        startLevel(this.currentLevel);
    }

    public void nextLevel() {
        if (currentLevel < 3) {
            startLevel(currentLevel + 1);
        } else {
            // Beat the whole game
            gameWon = true;
            gameOver = true;
        }
    }

    public void decrementMove() {
        if (movesLeft > 0) movesLeft--;
    }
    
    public void setMovesLeft(int moves) {
        this.movesLeft = moves;
    }

    public int getCurrentLevel() { return currentLevel; }
    public int getMovesLeft() { return movesLeft; }
    public int getTargetScore() { return targetScore; }
    
    public boolean isGameOver() { return gameOver; }
    public void setGameOver(boolean gameOver) { this.gameOver = gameOver; }
    
    public boolean isGameWon() { return gameWon; }
    public void setGameWon(boolean gameWon) { this.gameWon = gameWon; }
}

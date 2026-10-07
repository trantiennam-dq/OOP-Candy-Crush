package main.state;

import main.GameManager;
import main.model.LevelManager;
import main.model.ScoreManager;

public class IdleState implements IGameState {
    @Override
    public void update(GameManager manager) {
        if (manager.getAnimationSystem().isAnimating()) return;

        LevelManager lm = LevelManager.getInstance();

        if (lm.isGameOver()) {
            manager.setState(manager.getGameOverState());
            return;
        }

        if (lm.isGameWon()) {
            if (lm.getMovesLeft() > 0) {
                manager.setState(manager.getSugarCrushState());
            } else {
                lm.setGameOver(true);
                lm.saveScoreIfHigher(lm.getCurrentLevel(), ScoreManager.getInstance().getScore());
                manager.setState(manager.getGameOverState());
            }
            return;
        }

        if (ScoreManager.getInstance().getScore() >= lm.getTargetScore()) {
            lm.setGameWon(true);
            manager.setState(manager.getSugarCrushState());
            return;
        }

        if (lm.getMovesLeft() <= 0 && ScoreManager.getInstance().getScore() < lm.getTargetScore()) {
            lm.setGameOver(true);
            lm.saveScoreIfHigher(lm.getCurrentLevel(), ScoreManager.getInstance().getScore());
            manager.setState(manager.getGameOverState());
            return;
        }
    }
}

package main;

import main.model.Board;
import main.model.Candy;
import main.model.MatchResult;
import main.model.ScoreManager;
import main.logic.IMatchLogic;
import main.logic.IGravityLogic;
import main.logic.ISpecialCandyLogic;
import main.animation.AnimationSystem;
import main.animation.SwapAnimation;
import main.animation.FallAnimation;
import main.state.IGameState;
import main.state.IdleState;
import main.logic.ICandyDestroyListener;
import main.state.SwappingState;
import main.state.RevertingState;
import main.state.MatchingState;
import main.state.FallingState;
import main.state.GameOverState;
import main.state.SugarCrushState;
import main.model.LevelManager;
import javax.swing.Timer;
import java.util.List;

public class GameManager implements ICandyDestroyListener {
    private final IGameState idleState = new IdleState();
    private final IGameState swappingState = new SwappingState();
    private final IGameState revertingState = new RevertingState();
    private final IGameState matchingState = new MatchingState();
    private final IGameState fallingState = new FallingState();
    private final IGameState gameOverState = new GameOverState();
    private final IGameState sugarCrushState = new SugarCrushState();

    private Board board;
    private final IMatchLogic matchLogic;
    private final IGravityLogic gravityLogic;
    private final ISpecialCandyLogic specialCandyLogic;
    private AnimationSystem animationSystem;
    private ScoreManager scoreManager = ScoreManager.getInstance();
    private Runnable repaintCallback;
    private IGameState state = idleState;
    private Timer gameLoop;
    
    private int swapR1, swapC1, swapR2, swapC2;

    public GameManager(IMatchLogic matchLogic, IGravityLogic gravityLogic, ISpecialCandyLogic specialCandyLogic) {
        this.board = new Board(8, 8);
        this.matchLogic = matchLogic;
        this.gravityLogic = gravityLogic;
        this.specialCandyLogic = specialCandyLogic;
        this.animationSystem = new AnimationSystem();
    }

    public void setRepaintCallback(Runnable repaintCallback) {
        this.repaintCallback = repaintCallback;
    }

    public void start() {
        this.state = idleState;
        LevelManager.getInstance().resetCurrentLevel();
        scoreManager.reset();
        
        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getCols(); c++) {
                board.setCandy(r, c, null);
            }
        }
        
        gravityLogic.refill(board);
        List<MatchResult> initialMatches = matchLogic.findMatches(board);
        while (!initialMatches.isEmpty()) {
            for (int r = 0; r < board.getRows(); r++) {
                for (int c = 0; c < board.getCols(); c++) {
                    board.setCandy(r, c, null);
                }
            }
            gravityLogic.refill(board);
            initialMatches = matchLogic.findMatches(board);
        }
        
        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getCols(); c++) {
                Candy candy = board.getCandy(r, c);
                if (candy != null) {
                    candy.setVisualX(c);
                    candy.setVisualY(r);
                }
            }
        }
        
        if (repaintCallback != null) repaintCallback.run();

        if (gameLoop != null && gameLoop.isRunning()) {
            gameLoop.stop();
        }

        gameLoop = new Timer(16, e -> update());
        gameLoop.start();
    }

    public void update() {
        if (animationSystem.isAnimating()) {
            animationSystem.update();
            if (repaintCallback != null) repaintCallback.run();
            return;
        }

        if (state != null) {
            state.update(this);
        }
        if (repaintCallback != null) repaintCallback.run();
    }

    public void refillAndAnimate() {
        gravityLogic.applyGravity(board);
        
        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getCols(); c++) {
                Candy candy = board.getCandy(r, c);
                if (candy != null && candy.getVisualY() < r) {
                    animationSystem.addAnimation(new FallAnimation(candy, r, 0.2f));
                }
            }
        }
        
        List<Candy> newCandies = gravityLogic.generateNewCandies(board);
        for (Candy newCandy : newCandies) {
            animationSystem.addAnimation(new FallAnimation(newCandy, newCandy.getRow(), 0.2f));
        }
    }

    public void requestRepaint() {
        if (repaintCallback != null) repaintCallback.run();
    }

    public boolean isIdle() {
        return state == idleState;
    }

    public void handleSwap(int r1, int c1, int r2, int c2) {
        this.swapR1 = r1;
        this.swapC1 = c1;
        this.swapR2 = r2;
        this.swapC2 = c2;

        main.model.LevelManager.getInstance().decrementMove();

        Candy c1Obj = board.getCandy(r1, c1);
        Candy c2Obj = board.getCandy(r2, c2);


        
        board.swap(r1, c1, r2, c2);
        animationSystem.addAnimation(new SwapAnimation(c1Obj, c2Obj, 0.2f));
        setState(swappingState);
    }

    @Override
    public void onCandyDestroyed(Candy candy) {
        handleCandyDestruction(candy, false);
    }

    @Override
    public void onSecondaryCandyDestroyed(Candy candy) {
        handleCandyDestruction(candy, true);
    }

    private void handleCandyDestruction(Candy candy, boolean isLight) {
        scoreManager.addScore(10);
        boolean isSpecial = candy.getSpecialType() != main.model.enums.SpecialType.NONE;
        animationSystem.addAnimation(new main.animation.DestroyAnimation(candy, isSpecial, isLight));
        
        if (isSpecial) {
            specialCandyLogic.activateSpecialCandy(board, candy, this);
        }
    }

    public void processMatches(List<MatchResult> matches) {
        for (MatchResult match : matches) {
            for (Candy candy : match.getMatchedCandies()) {
                if (candy != null && board.getCandy(candy.getRow(), candy.getCol()) != null) {
                    board.setCandy(candy.getRow(), candy.getCol(), null);
                    onCandyDestroyed(candy);
                }
            }
            Candy spawned = match.getSpawnedCandy();
            if (spawned != null) {
                spawned.setVisualX(spawned.getCol());
                spawned.setVisualY(spawned.getRow());
                board.setCandy(spawned.getRow(), spawned.getCol(), spawned);
            }
        }
    }

    public Board getBoard() { return board; }
    public IMatchLogic getMatchLogic() { return matchLogic; }
    public ISpecialCandyLogic getSpecialCandyLogic() { return specialCandyLogic; }
    public AnimationSystem getAnimationSystem() { return animationSystem; }
    public int getSwapR1() { return swapR1; }
    public int getSwapC1() { return swapC1; }
    public int getSwapR2() { return swapR2; }
    public int getSwapC2() { return swapC2; }
    public IGameState getIdleState() { return idleState; }
    public IGameState getSwappingState() { return swappingState; }
    public IGameState getRevertingState() { return revertingState; }
    public IGameState getMatchingState() { return matchingState; }
    public IGameState getFallingState() { return fallingState; }
    public IGameState getGameOverState() { return gameOverState; }
    public IGameState getSugarCrushState() { return sugarCrushState; }
    public void setState(IGameState state) { this.state = state; }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package red.guih.games.minesweeper;

/**
 * @author Note
 */
public class MinesweeperSquare {

    public enum State {
        HIDDEN,
        SHOWN,
        FLAGGED
    }

    private final int i, j;
    private MinesweeperImage minesweeperImage = MinesweeperImage.BLANK;
    private int num;
    private State state = State.HIDDEN;

    public MinesweeperSquare(int i, int j) {
        this.i = i;
        this.j = j;
    }

    @Override
    public String toString() {
        return "MinesweeperSquare [i=" + i + ", j=" + j + ", minesweeperImage=" + minesweeperImage + ", num=" + num
                + ", state=" + state + "]";
    }

    public final MinesweeperImage getMinesweeperImage() {
        return minesweeperImage;
    }

    public int getNum() {
        return num;
    }

    public final State getState() {
        return state;
    }

    public final void setMinesweeperImage(final MinesweeperImage minesweeperImage) {
        this.minesweeperImage = minesweeperImage;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public final void setState(final State state) {
        this.state = state;
    }

    public int getJ() {
        return j;
    }

    public int getI() {
        return i;
    }

}

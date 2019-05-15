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

    private final int i;
    private final int j;
    private MinesweeperImage minesweeperImage = MinesweeperImage.BLANK;
    private int num;
    private State state = State.HIDDEN;

    MinesweeperSquare(int i, int j) {
        this.i = i;
        this.j = j;
    }

    @Override
    public String toString() {
        return "MinesweeperSquare [i=" + i + ", j=" + j + ", minesweeperImage=" + minesweeperImage +
                ", num=" + num
                + ", state=" + state + "]";
    }

    final MinesweeperImage getMinesweeperImage() {
        return minesweeperImage;
    }

    final void setMinesweeperImage(final MinesweeperImage minesweeperImage) {
        this.minesweeperImage = minesweeperImage;
    }

    int getNum() {
        return num;
    }

    void setNum(int num) {
        this.num = num;
    }

    final State getState() {
        return state;
    }

    final void setState(final State state) {
        this.state = state;
    }

    public int getJ() {
        return j;
    }

    public int getI() {
        return i;
    }

    public enum State {
        HIDDEN,
        SHOWN,
        FLAGGED
    }

}

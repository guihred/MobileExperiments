
package red.guih.games.dots;

public class Line {
    float startX,startY,endX,endY;

    Line(){}
    Line(float[] center1,float[]center2){
        startX=center1[0];
        startY=center1[1];
        endX=center2[0];
        endY=center2[1];
    }



    void reset(){
        startX=0;startY=0;endX=0;endY=0;
    }

    public void setEndX(float endX) {
        this.endX =  endX;

    }

    public void setEndY(float endY) {
        this.endY =  endY;
    }

    public void setStartX(float startX) {
        this.startX =  startX;
    }

    public void setStartY(float startY) {
        this.startY =  startY;
    }
}

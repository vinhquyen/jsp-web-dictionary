/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cercalocal;

/**
 *
 * @author chiron
 */
public class Peticio {
    private int x;
    private int y;
    private int prioritat;

    public Peticio(int x, int y, int p)
    {
        this.x = x;
        this.y = y;
        this.prioritat = p;
    }
    
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getPrioritat() {
        return prioritat;
    }
}

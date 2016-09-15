package game.items;

import java.awt.image.BufferedImage;

/**
 * This class represents a torch.
 * 
 * @author Hector (Fang Zhao 300364061)
 *
 */
public class Torch extends Item implements Destroyable, Tradable {

    /**
     * Each torch can burn up to 3 minute.
     */
    private static final int INIT_TIME = 3 * 60;
    private int timeLimit;
    private boolean isFlaming;

    /**
     * 
     * @param description
     * @param sprite
     */
    public Torch(String description, BufferedImage sprite) {
        super(description, sprite);
        timeLimit = INIT_TIME;
        isFlaming = false;
    }

    /**
     * Get the current status of this torch, i.e. whether it is flaming.
     * 
     * @return
     */
    public boolean isFlaming() {
        return isFlaming;
    }

    /**
     * This method sets whether the torch is lighted up or not. It will only take effect
     * when this torch is not used up.
     * 
     * @param isFlaming
     */
    public void setIsFlaming(boolean isFlaming) {
        if (timeLimit > 0) {
            this.isFlaming = isFlaming;
        }
    }

    /**
     * This method burns the torch (time left decreased by 1)
     */
    public void Burn() {
        if (timeLimit >= 0) {
            timeLimit--;
            if (timeLimit <= 0) {
                isFlaming = false;
            }
        }
    }

    public int getTimeLeft() {
        return timeLimit;
    }

    @Override
    public String toString() {
        int minutesLeft = timeLimit / 60;
        String minutesLeftString = minutesLeft >= 1 ? String.valueOf(minutesLeft)
                : "less than 1";
        return super.toString() + " I reckon it has about " + minutesLeftString
                + " minutes left to burn.";
    }
}

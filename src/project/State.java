package src.project;

import java.awt.Graphics;

public abstract class State {
    private static State currentState;

    public static State getCurrentState() { return currentState; }
    public static void changeState(State state) { currentState = state; }

    public abstract void update(float dt);
    public abstract void draw(Graphics graphics);
}

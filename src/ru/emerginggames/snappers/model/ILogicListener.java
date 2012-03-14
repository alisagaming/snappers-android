package ru.emerginggames.snappers.model;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 14.03.12
 * Time: 23:59
 */
public interface ILogicListener {
    public void snapperTouched(int i, int j);
    public void blastCreated(Blast blast);
    public void blastLaunched(Blast blast);
}

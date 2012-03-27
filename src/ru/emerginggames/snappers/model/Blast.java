package ru.emerginggames.snappers.model;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 12.03.12
 * Time: 0:02
 */
public class Blast {
    public enum Direction {Up, Right, Down, Left}
    public float dest;
    public float x;
    public float y;
    public Direction direction;
    public int destI;
    public int destJ;
    public float source;
    public float age;
}

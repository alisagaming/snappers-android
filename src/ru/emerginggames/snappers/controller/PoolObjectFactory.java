package ru.emerginggames.snappers.controller;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 12.03.12
 * Time: 0:28
 */
public abstract class PoolObjectFactory<T> {
    public abstract T createObject();
}

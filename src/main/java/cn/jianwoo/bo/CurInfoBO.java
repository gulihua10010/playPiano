package cn.jianwoo.bo;

import java.io.Serializable;

import javafx.animation.Timeline;

/**
 * @author gulihua
 * @Description
 * @date 2022-12-03 22:29
 */
public class CurInfoBO implements Serializable
{
    private static final long serialVersionUID = -2440070252258836670L;

    private Double currentTime;

    private Double nextStartTime;
    private Timeline timeline;

    public CurInfoBO()
    {
        this.currentTime = -1d;
        this.nextStartTime = -1d;
        this.timeline = new Timeline();
    }


    public Timeline getTimeline()
    {
        return this.timeline;
    }


    public Double getCurrentTime()
    {
        return this.currentTime + 0.003472;
    }


    public void setCurrentTime(Double currentTime)
    {
        this.currentTime = currentTime;
    }


    public Double getNextStartTime()
    {
        return this.nextStartTime;
    }


    public void setNextStartTime(Double nextStartTime)
    {
        this.nextStartTime = nextStartTime;
    }
}

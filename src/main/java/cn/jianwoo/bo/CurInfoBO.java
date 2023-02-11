package cn.jianwoo.bo;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import cn.jianwoo.play.AudioPlay;

/**
 * @author gulihua
 * @Description
 * @date 2022-12-03 22:29
 */
public class CurInfoBO implements Serializable
{
    private static final long serialVersionUID = -2440070252258836670L;

    /** 时间戳，单位：毫秒 */
    private Long timestamp;

    /** 是否暂停 */
    private Boolean isPause;

    /** 是否完成准备 */
    private Boolean isReady;

    /** 锁 */
    private Object lock;
    /** 播放实例 */
    private List<AudioPlay> threadList;

    private Boolean isDebug;

    public CurInfoBO()
    {
        this.timestamp = 0L;
        this.isPause = false;
        this.isDebug = false;
        this.lock = new Object();
        this.threadList = new CopyOnWriteArrayList<>();
        this.isReady = false;
    }


    public CurInfoBO(boolean isDebug)
    {
        this();
        this.isDebug = isDebug;
    }


    public Boolean getIsDebug()
    {
        return this.isDebug;
    }


    public void addPlayer(AudioPlay play)
    {
        this.threadList.add(play);
    }


    public void stopAllSleep()
    {
        for (AudioPlay play : threadList)
        {
            play.interrupt();

        }
    }


    /**
     *
     * 拖动进度条或者暂停重新计算索引和时间
     *
     * @author gulihua
     * @param time 进度条的当前时间，-1 时表示为暂停
     * @return
     */
    public void calcIdxAndTime(Double time)
    {
        for (AudioPlay play : threadList)
        {
            play.updateIndex(time);

        }

        // 获取参照点
        double maxTime = -1;
        int j = -1;
        for (int i = 0; i < threadList.size(); i++)
        {
            AudioPlay play = threadList.get(i);
            NoteBO noteBO = play.getNote(play.getTmpIdx() + 1);
            if (maxTime < noteBO.getStartTime())
            {
                maxTime = noteBO.getStartTime();
                j = i;
            }
        }
        // 重新计算
        for (int i = 0; i < threadList.size(); i++)
        {
            AudioPlay play = threadList.get(i);
            if (j != i)
            {
                play.tokenDelay();
                NoteBO noteBO = play.getNote(play.getTmpIdx() + 1);
                play.setDelayTime(NoteBO.calcTime(noteBO.getEndTime() - maxTime, noteBO.getRate()).intValue());
                play.incrementTmpIdx();
            }
            play.updateIndex(play.getTmpIdx());

            // 更新拖动进度条之后的 totalTime
            play.setTotalTime(NoteBO.calcTime(play.getNote(play.getTmpIdx() + 1).getStartTime(),
                    play.getNote(play.getTmpIdx() + 1).getRate()).intValue());
            if (i == j)
            {
                // 拖动进度条时重新计算起始的基准时间
                this.setTimestamp(System.currentTimeMillis() - play.getTotalTime());
            }

        }
        // 停止 线程sleep
        this.stopAllSleep();

    }


    public Object getLock()
    {
        return this.lock;
    }


    public Boolean isPause()
    {
        return this.isPause;
    }


    public void play()
    {
        this.isPause = false;
    }


    public void pause()
    {
        this.isPause = true;
    }


    public Long getTimestamp()
    {
        return this.timestamp;
    }


    public void setTimestamp(Long timestamp)
    {
        this.timestamp = timestamp;
    }


    public Boolean isCompleteReady()
    {
        return this.isReady;
    }


    public void completeReady()
    {
        this.isReady = true;

        synchronized (this.lock)
        {
            this.lock.notifyAll();
        }

    }
}

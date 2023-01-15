package cn.jianwoo.play;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import cn.jianwoo.bo.CurInfoBO;
import cn.jianwoo.bo.NoteBO;
import cn.jianwoo.util.NoteUtil;

import java.util.List;

/**
 * @author gulihua
 * @Description
 * @date 2022-11-12 18:24
 */
public class AudioPlay extends Thread
{
    private static final Log log = LogFactory.get();

    /** 音符 */
    private List<NoteBO> noteList;
    /** 模式 */
    private NoteBO.Mode mode;

    private CurInfoBO curInfo;

    /** 音谱的当前索引 */
    private int index;

    private int tmpIdx;

    /** 是否延迟，拖动进度条时应用 */
    private boolean delay;
    /** 延迟时间，单位：毫秒 */
    private int delayTime;
    /** 总时间，单位：毫秒 */
    private int totalTime;

    private AudioPlay(List<NoteBO> noteList)
    {
        this.noteList = noteList;
    }


    private AudioPlay(List<NoteBO> noteList, NoteBO.Mode mode)
    {
        this.noteList = noteList;
        this.mode = mode;
    }


    private AudioPlay()
    {

    }


    public AudioPlay(List<NoteBO> noteList, NoteBO.Mode mode, CurInfoBO curInfo)
    {
        this.noteList = noteList;
        this.mode = mode;
        this.curInfo = curInfo;
    }


    @Override
    public void run()
    {
        try
        {
            if (null == this.curInfo)
            {
                throw new IllegalStateException("请初始化参数curInfo!");
            }
            this.curInfo.addPlayer(this);
            new Audio("test").start();
            sleep(2500);
            this.curInfo.setTimestamp(System.currentTimeMillis());
            this.totalTime = 0;
            for (; this.index < this.noteList.size(); this.index++)
            {
                long start = System.currentTimeMillis();
                NoteBO note = this.noteList.get(this.index);
                if (null == note || null == note.getTime() || 0 == note.getTime())
                {
                    continue;
                }

                int time = note.getTime();
                if (this.curInfo.getIsDebug())
                {
                    log.debug(">>>>Play the note: mode= {}, startTime= {}, note= {}, value= {}", this.getMode(),
                            note.getStartTime(), note.getNote(), note.getValue());
                }

                if (note.getValue() != 0)
                {
                    new Audio(note.getNote()).start();
                    if (note.getIsMuleNotes())
                    {
                        // 支持单轨道多音符
                        for (NoteBO.MergeNote m : note.getMergeVals())
                        {
                            new Audio(m.getNote()).start();
                        }

                    }

                }
                System.out.println("P_" + note.getMode() + ", " + note.getStartTime() + ", " + note.getValue() + ", "
                        + (start - this.curInfo.getTimestamp()) + ", " + totalTime);
                int cost = (int) (start - this.curInfo.getTimestamp());

                long end = System.currentTimeMillis();
                // 时间偏移量校准1, 剔除程序执行所消耗的时间数
                if (end - start < time)
                {
                    time = (int) (time - (end - start));

                }
                else
                {
                    log.warn("time out, time = %d, cost = %d%n", time, (end - start));
                }
                // 时间偏移量校准2, 与物理时间线对齐
                if (cost > totalTime)
                {
                    if (time > cost - totalTime)
                    {
                        time = time - (cost - totalTime);
                    }
                }
                totalTime += note.getTime();
                try
                {
                    sleep(time);
                }
                catch (InterruptedException ignored)
                {
                    // 拖动进度条时会中断线程
                }
                while (this.curInfo.isPause())
                {
                    try
                    {
                        synchronized (this.curInfo.getLock())
                        {
                            this.curInfo.getLock().wait();
                        }
                    }
                    catch (InterruptedException ignored)
                    {
                        // 拖动进度条时会中断线程

                    }
                }

                // 用 while 是为了应对多次点击暂停
                while (this.delay)
                {
                    this.delay = false;

                    if (delayTime > 0)
                    {
                        try
                        {
                            sleep(delayTime);
                        }
                        catch (InterruptedException ignored)
                        {
                            // 拖动进度条时会中断线程
                        }
                    }
                }

            }

        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }

    }


    /**
     *
     * 根据当前进度条的时间，重新计算索引
     *
     * @author gulihua
     * @param time 进度条的当前时间，-1 时表示为暂停
     * @return
     */
    public void updateIndex(Double time)
    {
        if (time == -1)
        {
            this.tmpIdx = this.index - 1;
            return;
        }
        NoteUtil.findTime(this.noteList, time, p -> {
            this.tmpIdx = p.getSequence() - 1;
            if (this.tmpIdx < -1)
            {
                this.tmpIdx = -1;
            }
        });
    }


    public int getTotalTime()
    {
        return this.totalTime;
    }


    public void setTotalTime(int totalTime)
    {
        this.totalTime = totalTime;
    }


    public NoteBO.Mode getMode()
    {
        return this.mode;
    }


    public void setMode(NoteBO.Mode mode)
    {
        this.mode = mode;
    }


    public List<NoteBO> getNoteList()
    {
        return this.noteList;
    }


    public void tokenDelay()
    {
        this.delay = true;
    }


    public void setDelayTime(int delayTime)
    {
        this.delayTime = delayTime;
    }


    //
    public int getDelayTime()
    {
        return this.delayTime;
    }


    public void setNoteList(List<NoteBO> noteList)
    {
        this.noteList = noteList;
    }


    public NoteBO getNote(int index)
    {
        return this.noteList.get(index);
    }


    public int getTmpIdx()
    {
        return this.tmpIdx;
    }


    public void incrementTmpIdx()
    {
        this.tmpIdx++;
    }


    public void updateIndex(int tmpIdx)
    {
        this.index = tmpIdx;
    }

}

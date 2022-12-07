package cn.jianwoo.play;

import cn.jianwoo.bo.CurInfoBO;
import cn.jianwoo.bo.NoteBO;

import java.util.List;

/**
 * @author gulihua
 * @Description
 * @date 2022-11-12 18:24
 */
public class AudioPlay extends Thread
{
    /** 音符 */
    private List<NoteBO> noteList;
    /** 模式 */
    private NoteBO.Mode mode;

    private CurInfoBO curInfo;

    public AudioPlay(List<NoteBO> noteList)
    {
        this.noteList = noteList;
    }


    public AudioPlay(List<NoteBO> noteList, NoteBO.Mode mode)
    {
        this.noteList = noteList;
        this.mode = mode;
    }


    public AudioPlay(List<NoteBO> noteList, NoteBO.Mode mode, CurInfoBO curInfo)
    {
        this.noteList = noteList;
        this.mode = mode;
        this.curInfo = curInfo;
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


    public void setNoteList(List<NoteBO> noteList)
    {
        this.noteList = noteList;
    }


    @Override
    public void run()
    {
        try
        {
            new Audio("test").start();
            sleep(2500);
            for (NoteBO note : noteList)
            {
                if (0 == note.getTime())
                {
                    continue;
                }
                long start = System.currentTimeMillis();
                int time = note.getTime();
                if (NoteBO.Mode.MAIN.equals(this.getMode()))
                {
                    this.curInfo.setCurrentTime(note.getStartTime());
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
                long end = System.currentTimeMillis();
//                // 时间偏移量校准
                if (end - start < time)
                {
                    time = (int) (time - (end - start));
                }
                else
                {
                    System.err.printf("time out, time = %d, cost = %d%n", time, (end - start));
                }

                sleep(time);
            }

        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }

    }

}

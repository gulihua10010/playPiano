package cn.jianwoo.play;

import cn.hutool.core.io.FileUtil;

/**
 * @author gulihua
 * @Description
 * @date 2022-11-12 18:24
 */
public class AudioPlayOld extends Thread
{
    /** 音符 */
    private String[] notes;
    /** 间隔时间（单位：毫秒） */
    private int times;
    /** 模式 */
    private Mode mode;

    private int k = 0;

    public AudioPlayOld(String[] notes, int times)
    {
        this.notes = notes;
        this.times = times;
    }


    public AudioPlayOld(String[] notes, int times, Mode mode)
    {
        this.notes = notes;
        this.times = times;
        this.mode = mode;
    }


    public AudioPlayOld(String filePath, int times)
    {
        String content = FileUtil.readString(filePath, "UTF-8");
        this.notes = content.split(" ");
        this.times = times;
    }


    public AudioPlayOld(int times)
    {
        this.times = times;
    }


    public Mode getMode()
    {
        return this.mode;
    }


    public void setMode(Mode mode)
    {
        this.mode = mode;
    }


    public String[] getNotes()
    {
        return this.notes;
    }


    public void setNotes(String[] notes)
    {
        this.notes = notes;
    }


    public AudioPlayOld loadNotes(String notes)
    {
        this.notes = notes.split(" ");
        return this;
    }


    public int getTimes()
    {
        return this.times;
    }


    public void setTimes(int times)
    {
        this.times = times;
    }


    @Override
    public void run()
    {
        try
        {
            new Audio("test").start();
            sleep(1000);
            for (String note : notes)
            {
                int time = this.times;
                if (note.length() < 1)
                {
                    continue;
                }
                if (note.equals("\n") || note.equals("\r"))
                {
                    continue;
                }
                if (!"R".equals(note))
                {
                    if (note.length() > 1 && "_".equals(note.substring(0, 1)))
                    {
                        // 支持单轨道多音符
                        String[] arr = note.substring(1).split("@");
                        for (String s : arr)
                        {
                            try {
                                if (s.length() > 1 && "!".equals(s.substring(0, 1)))
                                {
                                    // 支持用户自定义时长
                                    String[] arr1 = s.substring(1).split("=");
                                    time = Integer.parseInt(arr1[1]);
                                    s = arr1[0];

                                }
                                new Audio(s).start();

                            } catch (Exception e) {
                                time = this.times;
                            }
                        }
                    }
                    else
                    {
                        try {
                            if (note.length() > 1 && "!".equals(note.substring(0, 1)))
                            {
                                // 支持用户自定义时长
                                String[] arr1 = note.substring(1).split("=");
                                time = Integer.parseInt(arr1[1]);
                                note = arr1[0];

                            }
                            new Audio(note).start();
                        } catch (Exception e) {
                            time = this.times;
                        }
                    }
                }
                sleep(time);
            }

        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }

    }

    enum Mode {
        /** 主奏 */
        MAIN,
        /** 伴奏 */
        ACCOMPANIMENTS

    }

}

package cn.jianwoo.play;

import cn.hutool.core.io.FileUtil;

import java.util.Map;

/**
 * @author gulihua
 * @Description
 * @date 2022-11-12 18:24
 */
public class AudioPlay extends Thread
{
    /** 音符 */
    private String[] notes;
    /** 间隔时间（单位：毫秒） */
    private int times;
    /** 模式 */
    private Mode mode;

    public AudioPlay(String[] notes, int times)
    {
        this.notes = notes;
        this.times = times;
    }


    public AudioPlay(String[] notes, int times, Mode mode)
    {
        this.notes = notes;
        this.times = times;
        this.mode = mode;
    }

    public AudioPlay(String filePath, int times)
    {
        String content = FileUtil.readString(filePath,"UTF-8");
        this.notes = content.split(" ");
        this.times = times;
    }

    public AudioPlay(int times)
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
    public AudioPlay loadNotes(String notes)
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

    /**
     * 播放音符
     * @param note
     */
    private void play(String note, int times) throws InterruptedException {
        // 0是延音
        if ("0".equals(note)) {
            sleep(times / 2);
        }

        Map<String, String> map = Note2Sound.map;
        if (map.containsKey(note)) {
            new Audio(map.get(note)).start();
            sleep(times / 2);
        }

        sleep(times / 2);
    }


    @Override
    public void run()
    {
        try
        {
            int times = this.times;
            new Audio("audio/test.mp3").start();
            sleep(1000);
            for (int i = 0; i < notes.length; i++)
            {
                if (notes[i].length()<1){
                    continue;
                }
                play(notes[i], times);
                times = this.times;
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

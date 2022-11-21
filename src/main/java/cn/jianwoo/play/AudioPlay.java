package cn.jianwoo.play;

import cn.hutool.core.io.FileUtil;

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
                switch (notes[i])
                {
                case "1--":
                    new Audio("audio/ll1.mp3").start();
                    sleep(times / 2);
                    break;
                case "2--":
                    new Audio("audio/ll2.mp3").start();
                    sleep(times / 2);
                    break;
                case "3--":
                    new Audio("audio/ll3.mp3").start();
                    sleep(times / 2);
                    break;
                case "4--":
                    new Audio("audio/ll4.mp3").start();
                    sleep(times / 2);
                    break;
                case "5--":
                    new Audio("audio/ll5.mp3").start();
                    sleep(times / 2);
                    break;
                case "6--":
                    new Audio("audio/ll6.mp3").start();
                    sleep(times / 2);
                    break;
                case "7--":
                    new Audio("audio/ll7.mp3").start();
                    sleep(times / 2);
                    break;
                case "1-":
                    new Audio("audio/l1.mp3").start();
                    sleep(times / 2);
                    break;
                case "2-":
                    new Audio("audio/l2.mp3").start();
                    sleep(times / 2);
                    break;
                case "3-":
                    new Audio("audio/l3.mp3").start();
                    sleep(times / 2);
                    break;
                case "4-":
                    new Audio("audio/l4.mp3").start();
                    sleep(times / 2);
                    break;
                case "5-":
                    new Audio("audio/l5.mp3").start();
                    sleep(times / 2);
                    break;
                case "6-":
                    new Audio("audio/l6.mp3").start();
                    sleep(times / 2);
                    break;
                case "7-":
                    new Audio("audio/l7.mp3").start();
                    sleep(times / 2);
                    break;
                case "1":
                    new Audio("audio/m1.mp3").start();
                    sleep(times / 2);
                    break;
                case "2":
                    new Audio("audio/m2.mp3").start();
                    sleep(times / 2);
                    break;
                case "3":
                    new Audio("audio/m3.mp3").start();
                    sleep(times / 2);
                    break;
                case "4":
                    new Audio("audio/m4.mp3").start();
                    sleep(times / 2);
                    break;
                case "5":
                    new Audio("audio/m5.mp3").start();
                    sleep(times / 2);
                    break;
                case "6":
                    new Audio("audio/m6.mp3").start();
                    sleep(times / 2);
                    break;
                case "7":
                    new Audio("audio/m7.mp3").start();
                    sleep(times / 2);
                    break;
                case "1+":
                    new Audio("audio/h1.mp3").start();
                    sleep(times / 2);
                    break;
                case "2+":
                    new Audio("audio/h2.mp3").start();
                    sleep(times / 2);
                    break;
                case "3+":
                    new Audio("audio/h3.mp3").start();
                    sleep(times / 2);
                    break;
                case "4+":
                    new Audio("audio/h4.mp3").start();
                    sleep(times / 2);
                    break;
                case "5+":
                    new Audio("audio/h5.mp3").start();
                    sleep(times / 2);
                    break;
                case "6+":
                    new Audio("audio/h6.mp3").start();
                    sleep(times / 2);
                    break;
                case "7+":
                    new Audio("audio/h7.mp3").start();
                    sleep(times / 2);
                    break;
                case "1++":
                    new Audio("audio/hh1.mp3").start();
                    sleep(times / 2);
                    break;
                case "2++":
                    new Audio("audio/hh2.mp3").start();
                    sleep(times / 2);
                    break;
                case "3++":
                    new Audio("audio/hh3.mp3").start();
                    sleep(times / 2);
                    break;
                case "4++":
                    new Audio("audio/hh4.mp3").start();
                    sleep(times / 2);
                    break;
                case "5++":
                    new Audio("audio/hh5.mp3").start();
                    sleep(times / 2);
                    break;
                case "6++":
                    new Audio("audio/hh6.mp3").start();
                    sleep(times / 2);
                    break;
                case "7++":
                    new Audio("audio/hh7.mp3").start();
                    sleep(times / 2);
                    break;
                case "0":
                    sleep(times / 2);
                    break;
                default:
                    continue;
                }
                sleep(times / 2);
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

package cn.jianwoo.play;

/**
 * @author gulihua
 * @Description
 * @date 2022-11-15 00:58
 */
public class Animation extends Thread{

    /** 音符 */
    private String[] notes;
    /** 间隔时间（单位：毫秒） */
    private int times;

    public Animation(int times) {
        this.times = times;
    }

    public Animation(String[] notes, int times) {
        this.notes = notes;
        this.times = times;
    }

    public String[] getNotes() {
        return this.notes;
    }

    public void setNotes(String[] notes) {
        this.notes = notes;
    }

    public int getTimes() {
        return this.times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public Animation loadNotes(String notes) {
        this.notes = notes.split(" ");
        return this;
    }

    @Override
    public void run() {
        try {
            int times = this.times;
            new Audio("audio/test.mp3").start();
            sleep(1000);
            int no = 1;
//            System.out.print(no+": ");
            for (int i = 0; i < this.notes.length; i++)
            {
                if (notes[i].length()<1){
                    continue;
                }
                String n = this.notes[i].replace("+","").replace("-","");
                if (n.equals("\n")||n.equals("\r")){
                    System.out.print("\n");
                    no++;
//                    System.out.print(no+": ");
                    continue;
                }
                switch (n)
                {
                    case "0":
                        System.out.print("_");
                        break;
                    case "1":
                        System.out.print("▁");
                        break;
                    case "2":
                        System.out.print("▂");
                        break;
                    case "3":
                        System.out.print("▃");
                        break;
                    case "4":
                        System.out.print("▄");
                        break;
                    case "5":
                        System.out.print("▅");
                        break;
                    case "6":
                        System.out.print("▆");
                        break;
                    case "7":
                        System.out.print("▇");
                        break;
                }
                System.out.print(" ");
                sleep(times);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}

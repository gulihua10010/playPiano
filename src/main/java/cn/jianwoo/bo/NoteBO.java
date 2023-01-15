package cn.jianwoo.bo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import cn.hutool.core.util.StrUtil;
import javafx.scene.control.Button;

/**
 * 音符实体类
 * 
 * @author gulihua
 * @site https://jianwoo.cn
 * @date 2022-11-30 07:20
 */
public class NoteBO implements Serializable
{
    private static final long serialVersionUID = -6622957226309536544L;

    /** 索引 */
    private Integer sequence;
    /** 唯一标识 */
    private String id;
    /** 音符 */
    private String note;
    /** 音符值 */
    private Integer value;
    /** 单个音轨同一时间存在的多个音符 */
    private List<MergeNote> mergeVals;
    /** 音符时长(duration = extendEndTime-startTime) */
    private Double duration;
    /** 音符开始时间 */
    private Double startTime;
    /** 音符结束时间 */
    private Double endTime;
    /** 音长倍数(multiple = duration/unit) */
    private Double multiple;
    /** 最小单元停顿时长(AudioPlayOld[旧版本]中 time = 180*(unit/0.0625) ) */
    private Double unit;
    /** 最小单元停顿时长(单位:毫秒)(time = 180*(unit/0.0625) ) */
    private Integer unitTime;
    /** 是否时间重置,true意味着单个音轨同一时间存在的多个音符, 需要重新计算音符的起始时间值 */
    private Boolean isResetTime;
    /** 是否使用用户自定义的时间, true 意味着multiple的值将失效，也就不需要通过多个停顿音'R'来填充音符, AudioPlay中 单个音符的time = duration / 0.0625 *180 */
    private Boolean isUseDefinedDuration;
    /** 延长音符集合，通过多个停顿音'R'填充音符，来达到延长音的效果，R的数量 = multiple */
    private List<NoteBO> extendNoteList;

    /** 音符时长(单位:毫秒), AudioPlay中 单个音符的time = duration / 0.0625 *180 */
    private Integer time;

    /** 是否存在的多个音符, mergeVals中有值是为 true */
    private Boolean isMuleNotes;

    /** 音符实际长度 */
    private Double noteLength;

    /** 音符模式 */
    private Mode mode;

    /** 音符组件对象 */
    private Button node;

    public NoteBO()
    {
        this.mergeVals = new ArrayList<>();
        this.extendNoteList = new ArrayList<>();
        this.isResetTime = false;
        this.isUseDefinedDuration = false;
        this.isMuleNotes = false;
    }


    /**
     *
     * 初始化延长音，延长音的数量为multiple，在初始化完所有属性后执行
     *
     * @param isUseDefinedDuration 当 multiple = duration/unit 不整除时是否使用自定义延长音, 如果不使用，主奏和伴奏可能对不齐
     * @date 22:02 2022/11/30
     * @author gulihua
     **/
    public void initExtendNoteList(boolean isUseDefinedDuration)
    {
        double st = endTime;
        BigDecimal mult1 = new BigDecimal(multiple.toString()).divide(BigDecimal.ONE, 4, RoundingMode.HALF_UP);
        BigDecimal mult2 = new BigDecimal(multiple.toString()).divide(BigDecimal.ONE, 0, RoundingMode.HALF_UP);
        // 音长是最小时长的整数倍
        if (mult1.compareTo(mult2) == 0 || !isUseDefinedDuration)
        {
            for (int i = 1; i < mult2.intValue(); i++)
            {
                NoteBO noteBO = new NoteBO();
                noteBO.setNote("R");
                noteBO.setValue(0);
                noteBO.setDuration(unit);
                noteBO.setNoteLength(unit);
                noteBO.setUnit(unit);
                noteBO.setMultiple(1D);
                noteBO.setStartTime(st);
                noteBO.setEndTime(st + unit);
                noteBO.init();
                extendNoteList.add(noteBO);
                st += unit;
            }
        }
        else
        {
            this.setNote("!" + this.getNote() + "=" + this.getTime());
        }
    }


    public void init()
    {
        this.time = calcTime(this.getDuration()).intValue();
        this.unitTime = calcTime(this.getUnit()).intValue();
        if (StrUtil.isBlank(this.getNote()))
        {
            this.setNote(generateNote(this.getValue()));
        }
        if (null == this.getValue())
        {
            this.setValue(parseNote(this.getNote()));
        }
        this.setMultiple(this.getDuration() / this.getUnit());
        this.setEndTime(this.getStartTime() + this.getDuration());
    }


    public static String generateId(Double startTime, int value)
    {
        return startTime.toString().replace(".", "_").concat("__").concat(String.valueOf(value));
    }


    public static String generateNote(int note)
    {
        String[] notes = { "C", "C#/Db", "D", "D#/Eb", "E", "F", "F#/Gb", "G", "G#/Ab", "A", "A#/Bb", "B" };
        String n;
        String note_ = notes[note % 12];
        switch (note_)
        {
        case "C#/Db":
            n = "C#";
            break;
        case "D#/Eb":
            n = "D#";
            break;
        case "F#/Gb":
            n = "F#";
            break;
        case "G#/Ab":
            n = "G#";
            break;
        case "A#/Bb":
            n = "A#";
            break;
        default:
            n = note_;
            break;
        }
        if (note == 0)
        {
            return "R";
        }
        return n + note / 12;

    }


    /**
     * 时间转换(音谱里的时间轴转换为毫秒)
     * 
     * @param t 音谱里的时间
     * @date 17:33 2023/1/2
     * @author gulihua
     *
     * @return 毫秒
     **/
    public static Double calcTime(Double t)
    {
        if (t == null)
        {
            return 0d;
        }
        BigDecimal dur = new BigDecimal(t.toString());
        return dur.divide(new BigDecimal("0.0625"), 6, RoundingMode.HALF_UP).multiply(new BigDecimal("180"))
                .doubleValue();
    }


    public static int parseNote(String note)
    {
        if ("R".equals(note)) return 0;

        Map<String, Integer> map = new HashMap<>();
        map.put("C", 0);
        map.put("C#", 1);
        map.put("D", 2);
        map.put("D#", 3);
        map.put("E", 4);
        map.put("F", 5);
        map.put("F#", 6);
        map.put("G", 7);
        map.put("G#", 8);
        map.put("A", 9);
        map.put("A#", 10);
        map.put("B", 11);
        int v1 = map.get(note.substring(0, note.length() - 1));
        int v2 = Integer.parseInt(note.substring(note.length() - 1));
        return v2 * 12 + v1;

    }

    public static class MergeNote
    {

        /** 唯一标识 */
        private String id;

        /** 音符 */
        private String note;

        /** 音符值 */
        private Integer value;

        /** 音符实际长度 */
        private Double noteLength;

        /** 音符模式 */
        private Mode mode;

        public MergeNote(Integer value)
        {
            this.value = value;
            this.note = generateNote(value);
        }


        public MergeNote(String note, Double noteLength)
        {
            this.note = note;
            this.noteLength = noteLength;
            this.value = parseNote(note);
        }


        public MergeNote(String note)
        {
            this.note = note;
            this.value = parseNote(note);

        }


        public MergeNote(Integer value, Double noteLength)
        {
            this.value = value;
            this.noteLength = noteLength;
            this.note = generateNote(value);
        }


        public MergeNote(Integer value, Double noteLength, Mode mode)
        {
            this.value = value;
            this.noteLength = noteLength;
            this.mode = mode;
            this.note = generateNote(value);
        }


        @Override
        public boolean equals(Object o)
        {
            if (this == o) return true;
            if (!(o instanceof MergeNote)) return false;
            MergeNote mergeNote = (MergeNote) o;
            return getValue().equals(mergeNote.getValue());
        }


        @Override
        public int hashCode()
        {
            return Objects.hash(getValue());
        }


        @Override
        public String toString()
        {
            return "MergeNote{" + "note='" + note + '\'' + ", value=" + value + ", noteLength=" + noteLength + '}';
        }


        public String getId()
        {
            return this.id;
        }


        public void setId(String id)
        {
            this.id = id;
        }


        public String getNote()
        {
            return this.note;
        }


        public void setNote(String note)
        {
            this.note = note;
        }


        public Integer getValue()
        {
            return this.value;
        }


        public void setValue(Integer value)
        {
            this.value = value;
        }


        public Double getNoteLength()
        {
            return this.noteLength;
        }


        public void setNoteLength(Double noteLength)
        {
            this.noteLength = noteLength;
        }


        public Mode getMode()
        {
            return this.mode;
        }


        public void setMode(Mode mode)
        {
            this.mode = mode;
        }
    }

    public enum Mode {
        /** 主奏 */
        MAIN,
        /** 伴奏 */
        ACCOMPANIMENTS

    }

    public NoteBO(Mode mode, Button node)
    {
        this.mode = mode;
        this.node = node;
    }


//
    public NoteBO(Mode mode, Button node, Double startTime)
    {
        this.mode = mode;
        this.node = node;
        this.startTime = startTime;
    }


    public NoteBO(Mode mode, int value, Button node, Double startTime)
    {
        this.mode = mode;
        this.value = value;
        this.node = node;
        this.startTime = startTime;
    }


    public String getId()
    {
        return this.id;
    }


    public void setId(String id)
    {
        this.id = id;
    }


    public Mode getMode()
    {
        return this.mode;
    }


    public void setMode(Mode mode)
    {
        this.mode = mode;
    }


    public Integer getSequence()
    {
        return this.sequence;
    }


    public Integer getUnitTime()
    {
        return this.unitTime;
    }


    public Double getNoteLength()
    {
        return this.noteLength;
    }


    public void setNoteLength(Double noteLength)
    {
        this.noteLength = noteLength;
    }


    public void setSequence(Integer sequence)
    {
        this.sequence = sequence;
    }


    public Boolean getIsMuleNotes()
    {
        return this.isMuleNotes;
    }


    public void setIsMuleNotes(Boolean muleNotes)
    {
        this.isMuleNotes = muleNotes;
    }


    public String getNote()
    {
        return this.note;
    }


    public void setNote(String note)
    {
        this.note = note;
    }


    public Integer getValue()
    {
        return this.value;
    }


    public void setValue(Integer value)
    {
        this.value = value;
    }


    public Integer getTime()
    {
        return this.time;
    }


    public Boolean getIsUseDefinedDuration()
    {
        return this.isUseDefinedDuration;
    }


    public void setIsUseDefinedDuration(Boolean useDefinedDuration)
    {
        this.isUseDefinedDuration = useDefinedDuration;
    }


    public List<MergeNote> getMergeVals()
    {
        return this.mergeVals;
    }


    public void setMergeVals(List<MergeNote> mergeVals)
    {
        this.mergeVals = mergeVals;
    }


    public Boolean getIsResetTime()
    {
        return this.isResetTime;
    }


    public void setIsResetTime(Boolean resetTime)
    {
        this.isResetTime = resetTime;
    }


    public Double getUnit()
    {
        return this.unit;
    }


    public void setUnit(Double unit)
    {
        this.unit = unit;
    }


    public Double getDuration()
    {
        return this.duration;
    }


    public void setDuration(Double duration)
    {
        this.duration = duration;
    }


    public Double getStartTime()
    {
        return this.startTime;
    }


    public void setStartTime(Double startTime)
    {
        this.startTime = startTime;
    }


    public Double getEndTime()
    {
        return this.endTime;
    }


    public void setEndTime(Double endTime)
    {
        this.endTime = endTime;
    }


    public Double getMultiple()
    {
        return this.multiple;
    }


    public void setMultiple(Double multiple)
    {
        this.multiple = multiple;
    }


    public List<NoteBO> getExtendNoteList()
    {
        return this.extendNoteList;
    }


    public void setExtendNoteList(List<NoteBO> extendNoteList)
    {
        this.extendNoteList = extendNoteList;
    }


    public Button getNode()
    {
        return this.node;
    }


    public void setNode(Button node)
    {
        this.node = node;
    }
}

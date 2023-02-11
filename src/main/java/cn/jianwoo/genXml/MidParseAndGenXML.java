package cn.jianwoo.genXml;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;

import org.jfugue.midi.MidiParser;
import org.jfugue.parser.ParserListenerAdapter;
import org.jfugue.theory.Note;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Filter;
import cn.hutool.core.util.StrUtil;
import cn.jianwoo.bo.NoteBO;
import cn.jianwoo.util.GenerateXmlUtil;

/**
 * 根据 mid 文件生成 xml 文件、
 * 
 * @date 16:21 2023/2/10
 * @author gulihua、
 **/
class MidParseAndGenXML extends ParserListenerAdapter
{

    public int counter;

    private Double unit = 0.041666666666666664;
    private Double tolerance = 0.005;

    private Double startTime = 0D;
    Map<BigDecimal, List<String>> map = new LinkedHashMap<BigDecimal, List<String>>();
    private List<NoteBO> noteList = new ArrayList<NoteBO>();
    private Boolean isResetTime = false;
    // 输出 xml 的全路径
    private String outPath;
    // 输入的 midi 文件 的全路径
    private String inPath;
    // 歌曲名称
    private String name;

    private MidParseAndGenXML()
    {
    }


    public MidParseAndGenXML(String inPath, String outPath)
    {
        this(inPath, outPath, "sample");
    }


    public MidParseAndGenXML(String inPath, String outPath, String name)
    {
        this.inPath = inPath;
        this.outPath = outPath;
        this.name = name;
    }


    @Override
    public void onNoteParsed(Note note)
    {
        counter++;
        String note_ = parseNote(note.getValue());
        BigDecimal key = new BigDecimal(startTime).divide(BigDecimal.ONE, 5, BigDecimal.ROUND_HALF_UP);
        System.out.println(key + ": " + note_ + ", " + note.getDuration());
        NoteBO noteBO = new NoteBO();
        noteBO.setValue((int) note.getValue());
        noteBO.setDuration(note.getDuration());
        noteBO.setNoteLength(note.getDuration());
//        noteBO.setUnit(unit);
        noteBO.setStartTime(startTime);
        noteBO.setEndTime(startTime + note.getDuration());
        noteBO.init();
//        noteBO.initExtendNoteList(false);
        if (isResetTime)
        {
            noteBO.setIsResetTime(true);
        }
        noteList.add(noteBO);
        if (!noteBO.getExtendNoteList().isEmpty())
        {
            noteList.addAll(noteBO.getExtendNoteList());
        }
//        for (int i = 1; i < note.getDuration() / unit; i++)
//        {
//            buffer.append("0   ");
//            if (!map.containsKey(startTime))
//            {
//                map.put(new BigDecimal(startTime), new ArrayList<>());
//            }
//            map.get(startTime).add("0");
//
//        }
        startTime = startTime + note.getDuration();
        isResetTime = false;
    }


    public void afterParsingFinished()
    {
//        System.out.println(convert(buffer.toString()));
//        System.out.println(JSON.toJSONString(map));
        System.out.println("=================afterParsingFinished====================");
        List<NoteBO> noteList_ = new ArrayList<NoteBO>();
        noteList_ = processMergeNoteBo(noteList);
        for (NoteBO noteBO : noteList_)
        {
            String s = noteBO.getNote();
            List<NoteBO.MergeNote> tmp = new ArrayList<>();
            tmp.add(new NoteBO.MergeNote(s, noteBO.getNoteLength()));
            tmp.addAll(noteBO.getMergeVals());
            List<NoteBO.MergeNote> list = CollUtil.distinct(tmp);
            if (!list.isEmpty())
            {
                if (list.contains(new NoteBO.MergeNote(0)) && list.size() > 1)
                {
                    list = CollUtil.filter(list, (Filter) o -> !new NoteBO.MergeNote(0).equals(o));
                }

//                s = CollUtil.join(list, "@");
                if (list.size() > 1)
                {
//                    s = "_" + s;
                    noteBO.setMergeVals(list.subList(1, list.size()));
                }
                else
                {
                    noteBO.getMergeVals().clear();
                }
            }
            noteBO.setValue(list.get(0).getValue());
            noteBO.setNote(list.get(0).getNote());
            noteBO.setNoteLength(list.get(0).getNoteLength());
//            System.out.print(s + " ");

        }
        GenerateXmlUtil.getInstance().generateNote(noteList_, outPath, name);

    }


    // 核心代码
    private List<NoteBO> processMergeNoteBo(List<NoteBO> noteList)
    {
        noteList.sort(Comparator.comparing(NoteBO::getStartTime));
        List<NoteBO> newNoteList = new ArrayList<>();
        for (int i = 0; i < noteList.size(); i++)
        {
            NoteBO noteBO = noteList.get(i);
            if (i < noteList.size() - 1)
            {
                int j = i + 1;
                while (j < noteList.size()
                        && Math.abs(noteBO.getStartTime() - noteList.get(j).getStartTime()) <= tolerance)
                {
                    noteBO.getMergeVals()
                            .add(new NoteBO.MergeNote(noteList.get(j).getValue(), noteList.get(j).getNoteLength()));
                    i++;
                    j++;
                }

                // 重新计算时长
                if (j < noteList.size())
                {
                    noteBO.setDuration(noteList.get(j).getStartTime() - noteBO.getStartTime());
                }
                if (null != noteBO.getUnit())
                {
                    noteBO.setMultiple(noteBO.getDuration() / noteBO.getUnit());
                }
                noteBO.init();
                newNoteList.add(noteBO);

            }
            else
            {
                if (noteBO.getStartTime().compareTo(noteList.get(i - 1).getStartTime()) != 0)
                {
                    newNoteList.add(noteBO);
                }
            }

        }
        return newNoteList;
    }


    public void onTrackBeatTimeRequested(double time)
    {
        startTime = time;
        isResetTime = true;
        System.out.println("                            onTrackBeatTimeRequested " + time);
    }


    public void run() throws InvalidMidiDataException, IOException
    {

        MidiParser parser = new MidiParser();

        parser.addParserListener(this);
        parser.parse(MidiSystem.getSequence(new File(inPath)));


    }


    private static String parseNote(int note)
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


    private static String convert(String notes)
    {
        String[] noteArr = notes.split(" ");
        StringBuilder newNote = new StringBuilder();
        int i = 0;
        for (String s : noteArr)
        {
            if (s.length() < 1)
            {
                continue;
            }
            if (s.equals("\n") || s.equals("\r"))
            {
                continue;
            }
            s = s.replace("\n", "");
            if (i % 20 == 0)
            {
                newNote.append(" \n ");
            }
            i++;
            newNote.append(StrUtil.padAfter(s, 4, " "));
        }
        return newNote.toString();
    }

}

package cn.jianwoo.covert;

import java.util.ArrayList;
import java.util.List;

import cn.jianwoo.bo.NoteBO;
import cn.jianwoo.util.GenerateXmlUtil;

/**
 * @author gulihua
 * @Description
 * @date 2022-11-28 18:03
 */
public class Convert
{
    public static void main(String[] args)
    {
        String notes = " 7-  1   2   3   0   5-  5   3   0   0   0   0   0   0   0   0 \n"
                + " 7-  1   2   3   0   5-  5   3   2   3   1   2   7-  1   5-  0 \n"
                + " 7-  1   2   3   0   5-  5   3   0   0   0   0   0   0   0   0 \n"
                + " 7-  1   2   3   0   5-  5   3   2   3   1   2   7-  1   5-  0 \n"
                + " 7   1+  2+  3+  0   5   5+  3+  0   0   0   0   0   0   0   0 \n"
                + " 7   1+  2+  3+  0   5   5+  3+  2+  3+  1+  2+  7   1+  5   0 \n"
                + " 7   1+  2+  3+  0   5   5+  3+  0   0   0   0   0   0   0   0 \n"
                + " 2   0   0   0   0   0   0   0   1   0   0   0   0   0   0   0 \n" +

                " 2   0   0   1   2   0   0   1   2   0   3   0   5   0   3   0   \n"
                + " 2   0   0   1   2   0   0   1   2   3   2   1   6-  0   0   0   \n"
                + " 2   0   0   1   2   0   0   1   2   0   3   0   5   0   3   0   \n"
                + " 2   0   0   3   2   0   1   2   2   0   0   0   0   0   0   0   \n"
                + " 2   0   0   1   2   0   0   1   2   0   3   0   5   0   3   0   \n"
                + " 2   0   0   3   2   0   1   0   6-  0   0   0   \n" + " 3   2   1   2   1   0   0   0   \n"
                + " 3   2   1   2   1   0   0   \n" + " 5-  3   2   1   2   0   0   1   0   0   0   0   0   \n"
                + " 1   0   2   0   3   0   1   0   6   0   5   6   0   0   0   \n"
                + " 2   7   0   6   7   0   0   0   0   \n"
                + " 7   0   6   7   0   0   3   0   1+  2+  1+  7   6   0   0   \n"
                + " 5   6   0   5   6   0   5   6   5   6   0   5   1   0   5   0   3   3   0   0   0   0   0   0   0   \n"
                + " 1   0   2   0   3   0   1   0   6   0   5   6   0   0   0   \n"
                + " 2   7   0   6   7   0   0   0   0   \n"
                + " 7   0   6   7   0   0   3   0   1+  2+  1+  7   6   0   0   \n"
                + " 5   6   0   3+  3+  0   0   5   0   6   0   3+  3+  0   \n"
                + " 5   0   6   6   0   3-  0   3-  0   3-  0   3-  0   0   0   \n"
                + " 1+  0   2+  0   3+  0   6+  5+  0   0   6+  5+  0   0   6+  5+  0   2+  0   0   \n"
                + " 3+  0   6+  5+  0   0   6+  5+  0   0   6+  5+  0   3+  0   0   \n"
                + " 2+  0   1+  6   0   1+  0   1+  2+  0   1+  6   0   0   1+  0   3+  0   0   0   0   0   3+  0   2+  0   0   0   \n"
                + " 1+  0   2+  0   3+  0   6+  5+  0   0   6+  5+  0   0   6+  5+  0   0   \n"
                + " 2+  0   3+  0   6+  5+  0   0   6+  5+  0   0   6+  5+  0   0   \n"
                + " 3+  0   2+  0   1+  6   0   0   3+  0   2+  0   1+  \n"
                + " 6   0   1+  0   0   1+  0   0   0   0   0   0   0   0   0   0   0   \n"
                + " 6   3+  0   0   2+  0   1+  6   0   3+  0   0   2+  0   1+  \n"
                + " 6   0   1+  0   0   1+  0   0   0   0   0   0   0   0   0   0   0   0   0   0   0   \n"
                + " 7   1+  2+  3+  0   5   5+  3+  2+  3+  7   1+  6   7   5   0   \n"
                + " 7   1+  2+  3+  0   5   5+  3+  0   0   0   0   0   0   0   0   \n"
                + " 6+  3+  2+  6   3   6   2+  3+  6+  0   0   0   0   0   0   0 \n ";
        List<NoteBO> list = convert2Obj(notes, 0.0625);
        GenerateXmlUtil.getInstance().generateNote(list, "/Users/gulihua/tmp/tempx/webUpload/test.xml", "test");
    }


    /***
     * 转换字母音符的文本格式到 NotBO {@link NoteBO}对象格式<br>
     * 
     * @param notes 音符文本
     * @param unit 最小单元停顿时长
     * @return
     */
    public static List<NoteBO> convert2Obj(String notes, Double unit)
    {
        String n_ = convert(notes);
        double st = 0;
        List<NoteBO> list = new ArrayList<NoteBO>();
        String[] noteArr = n_.split(" ");
        for (int i = 0; i < noteArr.length; i++)
        {
            String note = noteArr[i];
            if (note.length() < 1)
            {
                continue;
            }
            if (note.equals("\n") || note.equals("\r"))
            {
                continue;
            }
            NoteBO noteBO = new NoteBO();
            double dur = unit;
            if (note.length() > 1 && "_".equals(note.substring(0, 1)))
            {
                // 支持单轨道多音符
                String[] arr = note.substring(1).split("@");
                List<NoteBO.MergeNote> mergeVals = new ArrayList<NoteBO.MergeNote>(arr.length);
                for (String s : arr)
                {
                    try
                    {
                        if (s.length() > 1 && "!".equals(s.substring(0, 1)))
                        {
                            // 支持用户自定义时长
                            String[] arr1 = s.substring(1).split("=");
                            dur = Integer.parseInt(arr1[1]) / 180 * 0.625;
                            s = arr1[0];

                        }
                    }
                    catch (Exception e)
                    {
                        dur = unit;
                    }
                    mergeVals.add(new NoteBO.MergeNote(s, dur));
                }
                noteBO.setMergeVals(mergeVals);

            }
            else
            {
                try
                {
                    if (note.length() > 1 && "!".equals(note.substring(0, 1)))
                    {
                        // 支持用户自定义时长
                        String[] arr1 = note.substring(1).split("=");
                        dur = Integer.parseInt(arr1[1]) / 180 * 0.625;
                        note = arr1[0];

                    }
                }
                catch (Exception e)
                {
                    dur = unit;
                }

            }
            // 延长音
            while (i < noteArr.length - 1 && "R".equals(noteArr[i + 1]))
            {
                dur += unit;
                i++;
            }
            noteBO.setNote(note);
            noteBO.setDuration(dur);
            noteBO.setNoteLength(dur);
            noteBO.setUnit(dur);
            noteBO.setMultiple(1D);
            noteBO.setStartTime(st);
            noteBO.setEndTime(st + dur);
            noteBO.init();
            list.add(noteBO);
            st += dur;
        }
        return list;
    }


    /***
     * 转换数字音符到标准的字母音符<br>
     * 1--C,2--D,3--E,4--F,5--G,6--A,7--B
     * 
     * @param notes 音符文本
     * @return
     */
    public static String convert(String notes)
    {
        String[] noteArr = notes.split(" ");
        StringBuilder newNote = new StringBuilder();
        for (String s : noteArr)
        {
            if (s.length() < 1)
            {
                continue;
            }
            if (s.equals("\n") || s.equals("\r"))
            {
                newNote.append(" \n ");
                continue;
            }
            String t = "";
            String n = s;
            switch (n.substring(0, 1))
            {
            case "1":
                t = "C";
                break;
            case "2":
                t = "D";
                break;
            case "3":
                t = "E";
                break;
            case "4":
                t = "F";
                break;
            case "5":
                t = "G";
                break;
            case "6":
                t = "A";
                break;
            case "7":
                t = "B";
                break;
            case "0":
                t = "R";
                break;
            default:
                break;
            }
            if (!"0".equals(s))
            {
                switch (n.substring(1))
                {
                case "+":
                    t += "6";
                    break;
                case "++":
                    t += "7";
                    break;
                case "-":
                    t += "4";
                    break;
                case "--":
                    t += "3";
                    break;
                default:
                    t += "5";
                    break;
                }
            }
            newNote.append(t).append(" ");

        }
        return newNote.toString();
    }
}

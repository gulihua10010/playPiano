package cn.jianwoo.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Filter;
import cn.jianwoo.bo.NoteBO;

/**
 * 音符工具类
 * 
 * @author gulihua
 * @date 2022-12-05 18:16
 */
public class NoteUtil
{
    /***
     * 多个音符集合按照时间轴合并
     * 
     * @param noteList 音符集合
     * @return 合并后的新集合
     */
    public static List<NoteBO> processMergeNoteBo(List<NoteBO> noteList)
    {
        noteList.sort(Comparator.comparing(NoteBO::getStartTime));
        List<NoteBO> newNoteList = new ArrayList<>();
        int k = 0;
        for (int i = 0; i < noteList.size(); i++)
        {
            final NoteBO noteBO = noteList.get(i);
            // 禁止污染源对象!
            NoteBO newNoteBO = new NoteBO();
            BeanUtil.copyProperties(noteBO, newNoteBO);
            if (i < noteList.size() - 1)
            {
                int j = i + 1;
                // 循环检查之后的音符的起始时间是否相同
                while (j < noteList.size() && newNoteBO.getStartTime().compareTo(noteList.get(j).getStartTime()) == 0)
                {
                    NoteBO.MergeNote mergeNote = new NoteBO.MergeNote(noteList.get(j).getValue(),
                            noteList.get(j).getNoteLength(), noteList.get(j).getMode());
                    newNoteBO.getMergeVals().add(mergeNote);
                    if (CollUtil.isNotEmpty(noteList.get(j).getMergeVals()))
                    {
                        newNoteBO.getMergeVals().addAll(noteList.get(j).getMergeVals());

                    }
                    i++;
                    j++;
                }
                // 重新计算时长
                if (j < noteList.size())
                {
                    newNoteBO.setDuration(noteList.get(j).getStartTime() - noteBO.getStartTime());
                }
                newNoteBO.setMultiple(newNoteBO.getDuration() / newNoteBO.getUnit());
                newNoteBO.init();
                processMerge(newNoteBO);
                newNoteList.add(newNoteBO);

            }
            else
            {
                if (newNoteBO.getStartTime().compareTo(noteList.get(i - 1).getStartTime()) != 0)
                {
                    processMerge(newNoteBO);
                    newNoteList.add(newNoteBO);
                }
            }

        }
        return newNoteList;
    }


    private static void processMerge(NoteBO noteBO)
    {
        List<NoteBO.MergeNote> tmp = new ArrayList<>();
        tmp.add(new NoteBO.MergeNote(noteBO.getValue(), noteBO.getNoteLength(), noteBO.getMode()));
        tmp.addAll(noteBO.getMergeVals());
        List<NoteBO.MergeNote> list = CollUtil.distinct(tmp);
        if (!list.isEmpty())
        {
            if (list.contains(new NoteBO.MergeNote(0)) && list.size() > 1)
            {
                list = CollUtil.filter(list, (Filter) o -> !new NoteBO.MergeNote(0).equals(o));
            }

            if (list.size() > 1)
            {
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
        noteBO.setMode(list.get(0).getMode());
    }


    /**
     * 标记音符的伴奏和主奏
     * 
     * @param noteList 音符集合
     * @author gulihua
     **/
    public static void processMarkAcc(List<NoteBO> noteList)
    {
        int minVal = 47;
        List<NoteBO> accNoteList_ = new ArrayList<NoteBO>();
        for (NoteBO noteBO : noteList)
        {
            if (CollUtil.isEmpty(noteBO.getMergeVals()))
            {
                continue;
            }
            noteBO.setMode(NoteBO.Mode.MAIN);
            if (noteBO.getValue() <= minVal)
            {
                noteBO.setMode(NoteBO.Mode.ACCOMPANIMENTS);
            }
            for (NoteBO.MergeNote member : noteBO.getMergeVals())
            {
                member.setMode(NoteBO.Mode.MAIN);
                if (member.getValue() <= minVal)
                {
                    member.setMode(NoteBO.Mode.ACCOMPANIMENTS);
                }
            }
        }
    }


    /**
     * 根据音谱时间轴获取所在索引
     * 
     * @param noteList 音符集合
     * @param startTime 时间轴的某一点时间
     * @date 00:09 2023/1/3
     * @author gulihua
     *
     * @return 索引, 若为-1 则表示未找到
     **/
    public static int findTime(List<NoteBO> noteList, Double startTime, Callback<NoteBO> callback)
    {
        int startIdx = 0;
        int endIdx = noteList.size() - 1;
        int midIdx = (startIdx + endIdx) / 2;
        do
        {
            if (startTime < noteList.get(midIdx).getStartTime())
            {
                endIdx = midIdx - 1;
                midIdx = (startIdx + endIdx) / 2;
            }
            else if (startTime >= noteList.get(midIdx).getEndTime())
            {
                startIdx = midIdx + 1;
                midIdx = (startIdx + endIdx) / 2;
            }
            if (startIdx > endIdx)
            {
                break;
            }
        }
        while (!(startTime >= noteList.get(midIdx).getStartTime() && startTime < noteList.get(midIdx).getEndTime()));
        if (startIdx <= endIdx)
        {
            noteList.get(midIdx).setSequence(midIdx);
            callback.call(noteList.get(midIdx));
            return midIdx;
        }
        return -1;
    }
}

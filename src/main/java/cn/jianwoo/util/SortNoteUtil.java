package cn.jianwoo.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Filter;
import cn.jianwoo.bo.NoteBO;

/**
 * @author gulihua
 * @Description
 * @date 2022-12-05 18:16
 */
public class SortNoteUtil
{

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
            newNoteBO.setId(String.valueOf(k++));
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
        if (CollUtil.isNotEmpty(noteBO.getMergeVals()))
        {
            int t = 0;
            for (NoteBO.MergeNote mergeNote : noteBO.getMergeVals())
            {
                mergeNote.setId(noteBO.getId() + "_" + t++);
            }
        }
        noteBO.setValue(list.get(0).getValue());
        noteBO.setNote(list.get(0).getNote());
        noteBO.setNoteLength(list.get(0).getNoteLength());
    }
}

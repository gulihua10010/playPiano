package cn.jianwoo.play;

import cn.hutool.core.collection.CollUtil;
import cn.jianwoo.bo.CurInfoBO;
import cn.jianwoo.bo.NoteBO;
import cn.jianwoo.util.NoteUtil;
import cn.jianwoo.util.ReadXmlAsNoteUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 运行调用入口
 * 
 * @author gulihua
 * @date 2023-01-14 18:55
 */
public class AppStart
{
    /**
     * 运行方法封装
     * 
     * @param mainFilePath 主奏 xml 文件, 必填
     * @param accompanimentFilePath 伴奏 xml 文件, 可为 null
     * @param songName 歌曲名称
     * @date 18:57 2023/1/14
     * @author gulihua
     * @throws
     **/
    public static void run(String mainFilePath, String accompanimentFilePath, String songName)
    {
        CurInfoBO curInfo = new CurInfoBO();
        List<NoteBO> noteListMain = ReadXmlAsNoteUtil.getInstance()
                .readAsNote(mainFilePath, NoteBO.Mode.MAIN);

        List<NoteBO> noteListAcc = null;
        if (StringUtils.isNotBlank(accompanimentFilePath)){
            noteListAcc = ReadXmlAsNoteUtil.getInstance()
                    .readAsNote(accompanimentFilePath, NoteBO.Mode.ACCOMPANIMENTS);
        }

        List<NoteBO> list = new ArrayList<>();
        list.addAll(noteListMain);
        if (CollUtil.isNotEmpty(noteListAcc))
        {
            list.addAll(noteListAcc);
        }
        list = NoteUtil.processMergeNoteBo(list);

        new AudioPlay(noteListMain, NoteBO.Mode.MAIN,curInfo).start();
        if (CollUtil.isNotEmpty(noteListAcc))
        {
            new AudioPlay(noteListAcc, NoteBO.Mode.ACCOMPANIMENTS,curInfo).start();
        }
        Runnable runnable = AnimationFx.build(list, songName,curInfo).setMaxVal(105);
        new Thread(runnable).start();
    }
}

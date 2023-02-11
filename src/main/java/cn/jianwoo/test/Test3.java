package cn.jianwoo.test;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RuntimeUtil;
import cn.jianwoo.bo.NoteBO;
import cn.jianwoo.play.Audio;
import cn.jianwoo.util.GenerateXmlUtil;
import cn.jianwoo.util.NoteUtil;
import cn.jianwoo.util.ReadXmlAsNoteUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gulihua
 * @Description
 * @date 2022-12-29 19:02
 */
public class Test3 {
    public static void main(String[] args) {
        List<NoteBO> noteListAcc1 = ReadXmlAsNoteUtil.getInstance()
                .readAsNote("/Users/gulihua/Downloads/枫-周杰伦-伴奏1.xml", NoteBO.Mode.ACCOMPANIMENTS);

        List<NoteBO> noteListAcc2 = ReadXmlAsNoteUtil.getInstance()
                .readAsNote("/Users/gulihua/Downloads/枫-周杰伦-伴奏2.xml", NoteBO.Mode.ACCOMPANIMENTS);
        List<NoteBO> list = new ArrayList<>();
        list.addAll(noteListAcc1);
        list.addAll(noteListAcc2);

        list = NoteUtil.processMergeNoteBo(list);
        GenerateXmlUtil.getInstance().generateNote(list, "/Users/gulihua/Downloads/枫-周杰伦-伴奏.xml", "test");

    }
}

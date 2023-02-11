package cn.jianwoo.test;

import cn.jianwoo.bo.NoteBO;
import cn.jianwoo.util.ReadXmlAsNoteUtil;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Port;
import javax.swing.*;
import java.util.List;

/**
 * @author gulihua
 * @Description
 * @date 2023-01-18 14:30
 */
public class ReWriteXml extends TestAudio{
    public static void main(String[] args) {
        List<NoteBO> noteListAcc = ReadXmlAsNoteUtil.getInstance()
                .readAsNote("/Users/gulihua/Downloads/枫-周杰伦-伴奏.xml", NoteBO.Mode.ACCOMPANIMENTS);

    }
}

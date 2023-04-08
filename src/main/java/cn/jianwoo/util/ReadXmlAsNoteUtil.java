package cn.jianwoo.util;

import java.util.ArrayList;
import java.util.List;

import cn.jianwoo.bo.NoteBO;
import org.jdom2.Document;
import org.jdom2.Element;

import cn.hutool.core.io.FileUtil;

/**
 * @author gulihua
 * @Description
 * @date 2022-11-30 22:29
 */
public class ReadXmlAsNoteUtil
{
    private static ReadXmlAsNoteUtil instance;
    private static final JdomXmlHelper JDOM_HELPER = JdomXmlHelper.getInstance();

    private ReadXmlAsNoteUtil()
    {
    }


    public static ReadXmlAsNoteUtil getInstance()
    {
        synchronized (ReadXmlAsNoteUtil.class)
        {
            if (instance == null)
            {
                instance = new ReadXmlAsNoteUtil();
            }
        }

        return instance;
    }


    /**
     * 读取 XML 文件
     * 
     * @param filePath 文件全路径
     * @date 22:32 2022/11/30
     * @author gulihua
     *
     * @return
     * @throws
     **/
    public List<NoteBO> readAsNote(String filePath, NoteBO.Mode mode)
    {
        try
        {
            byte[] bytes = FileUtil.readBytes(filePath);
            Document document = JDOM_HELPER.build(bytes);
            Double unit = JDOM_HELPER.obtainNumValueFrom(document, "header.audio.midi.unit");
            Double bpm = JDOM_HELPER.obtainNumValueFrom(document, "header.audio.midi.bpm");
            Double rate = 1d;
            if (bpm != 0)
            {
                rate = 1 / bpm / 0.006;
            }

            return readXml(document, unit, rate, mode);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.err.println(e);
        }
        return null;
    }


    private List<NoteBO> readXml(Document document, Double unit, Double rate, NoteBO.Mode mode)
    {
        List<NoteBO> list = new ArrayList<NoteBO>();
        String detailNode = "note";
        List<Element> lineItems = document.getRootElement().getChildren(detailNode);
        for (int i = 0; i < lineItems.size(); i++)
        {
            NoteBO noteBO = new NoteBO();
            noteBO.setUnit(unit);
            noteBO.setSequence(JDOM_HELPER.obtainNumValueFrom(document, detailNode + "(" + i + ").@number").intValue());
            noteBO.setNote(JDOM_HELPER.obtainValueFrom(document, detailNode + "(" + i + ").key"));
            noteBO.setValue(JDOM_HELPER.obtainNumValueFrom(document, detailNode + "(" + i + ").value").intValue());
            noteBO.setDuration(JDOM_HELPER.obtainNumValueFrom(document, detailNode + "(" + i + ").duration"));
            noteBO.setNoteLength(JDOM_HELPER.obtainNumValueFrom(document, detailNode + "(" + i + ").length"));
            noteBO.setStartTime(JDOM_HELPER.obtainNumValueFrom(document, detailNode + "(" + i + ").startTime"));
            noteBO.setEndTime(JDOM_HELPER.obtainNumValueFrom(document, detailNode + "(" + i + ").endTime"));
            noteBO.setMultiple(JDOM_HELPER.obtainNumValueFrom(document, detailNode + "(" + i + ").multiple"));
            noteBO.setMode(mode);
            noteBO.setRate(rate);
            noteBO.init();
            String multNote = "multNote";
            noteBO.setMergeVals(new ArrayList<>());
            List<Element> multNoteEles = lineItems.get(i).getChildren(multNote);
            for (int j = 0; j < multNoteEles.size(); j++)
            {
                Integer v = JDOM_HELPER
                        .obtainNumValueFrom(document, detailNode + "(" + i + ")." + multNote + "(" + j + ").value")
                        .intValue();
                Double l = JDOM_HELPER.obtainNumValueFrom(document,
                        detailNode + "(" + i + ")." + multNote + "(" + j + ").length");
                noteBO.getMergeVals().add(new NoteBO.MergeNote(v, l, mode));
            }
            if (multNoteEles.size() > 0)
            {
                noteBO.setIsMuleNotes(true);
            }
            list.add(noteBO);
        }
        return list;
    }

}

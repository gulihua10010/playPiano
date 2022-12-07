package cn.jianwoo.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import cn.jianwoo.bo.NoteBO;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;

/**
 * @author gulihua
 * @Description
 * @date 2022-11-30 21:12
 */
public class GenerateXmlUtil
{
    public static void main(String[] args)
    {

    }

    private static GenerateXmlUtil instance;
    private static final JdomXmlHelper JDOM_HELPER = JdomXmlHelper.getInstance();

    private GenerateXmlUtil()
    {
    }


    public static GenerateXmlUtil getInstance()
    {
        synchronized (GenerateXmlUtil.class)
        {
            if (instance == null)
            {
                instance = new GenerateXmlUtil();
            }
        }

        return instance;
    }


    /**
     * 根据NoteBO集合生成 xml 文件
     *
     * @param noteList 音符集合
     * @param targetFile 输出文件全路径
     * @param audioName 音乐名称
     * @date 22:26 2022/11/30
     * @author gulihua
     *
     * @return
     * @throws
     **/
    public void generateNote(List<NoteBO> noteList, String targetFile, String audioName)
    {
        if (CollUtil.isEmpty(noteList))
        {
            System.err.println("音符集合为空!");
        }
        Document document = new Document();
        createNamespace(document);
        createHeader(audioName, noteList.get(0).getUnit(), document);
        createDetails(noteList, document);
        Format format = Format.getPrettyFormat();
        format.setEncoding("UTF-8");
        XMLOutputter out = new XMLOutputter(format);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try
        {
            out.output(document, bos);
            byte[] bytes = bos.toByteArray();
            FileUtil.writeBytes(bytes, targetFile);
        }
        catch (IOException e)
        {
            System.err.println(e);
        }
        finally
        {
            try
            {
                bos.close();
            }
            catch (IOException e)
            {
                System.err.println(e);
            }
        }
    }


    private void createNamespace(Document document)
    {
        Namespace sanc = Namespace.getNamespace("sanc", "http://www.sanc.org.sg/schemas/ean");
        Element root = new Element("midi", sanc);

        Namespace xsi = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        root.addNamespaceDeclaration(xsi);
        root.setAttribute("schemaLocation", "http://www.sanc.org.sg/schemas/ean Order.xsd", xsi);
        root.setAttribute("documentStatus", "MIDI");
        Date now = new Date();
        root.setAttribute("creationDate", DateUtil.format(now, "yyyy-MM-dd'T'HH:mm:ss"));
        root.setAttribute("creationBy", "jianwoo.cn");

        document.setRootElement(root);
    }


    private void createHeader(String audioName, Double unit, Document document)
    {
        JDOM_HELPER.setValueToDoc(document, "header.audio.midi.name", audioName);
        JDOM_HELPER.setValueToDoc(document, "header.audio.midi.unit", JDOM_HELPER.formatNumber(unit));

    }


    private void createDetails(List<NoteBO> noteList, Document document)
    {
        String detailNode = "note";
        for (int i = 0; i < noteList.size(); i++)
        {
            NoteBO note = noteList.get(i);
            JDOM_HELPER.setValueToDoc(document, detailNode + "(" + i + ").@number", String.valueOf(i + 1));
            JDOM_HELPER.setValueToDoc(document, detailNode + "(" + i + ").key", note.getNote());
            JDOM_HELPER.setValueToDoc(document, detailNode + "(" + i + ").value", String.valueOf(note.getValue()));
            JDOM_HELPER.setValueToDoc(document, detailNode + "(" + i + ").duration",
                    JDOM_HELPER.formatNumber(note.getDuration()));
            JDOM_HELPER.setValueToDoc(document, detailNode + "(" + i + ").length",
                    JDOM_HELPER.formatNumber(note.getNoteLength()));
            JDOM_HELPER.setValueToDoc(document, detailNode + "(" + i + ").startTime",
                    JDOM_HELPER.formatNumber(note.getStartTime()));
            JDOM_HELPER.setValueToDoc(document, detailNode + "(" + i + ").endTime",
                    JDOM_HELPER.formatNumber(note.getEndTime()));
            JDOM_HELPER.setValueToDoc(document, detailNode + "(" + i + ").multiple",
                    JDOM_HELPER.formatNumber(note.getMultiple()));
            JDOM_HELPER.setValueToDoc(document, detailNode + "(" + i + ").time", String.valueOf(note.getTime()));
            String multNote = "multNote";
            if (!note.getMergeVals().isEmpty())
            {
                for (int j = 0; j < note.getMergeVals().size(); j++)
                {
                    JDOM_HELPER.setValueToDoc(document, detailNode + "(" + i + ")." + multNote + "(" + j + ").@number",
                            String.valueOf(j + 1));

                    JDOM_HELPER.setValueToDoc(document, detailNode + "(" + i + ")." + multNote + "(" + j + ").key",
                            note.getMergeVals().get(j).getNote());
                    JDOM_HELPER.setValueToDoc(document, detailNode + "(" + i + ")." + multNote + "(" + j + ").value",
                            String.valueOf(note.getMergeVals().get(j).getValue()));
                    JDOM_HELPER.setValueToDoc(document, detailNode + "(" + i + ")." + multNote + "(" + j + ").length",
                            JDOM_HELPER.formatNumber(note.getMergeVals().get(j).getNoteLength()));
                }

            }

        }
    }

}

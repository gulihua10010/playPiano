package cn.jianwoo.genXml;

/**
 * 根据 mid 文件生成 xml 文件的运行类
 * !!重要：运行前请先看 Readme 中相应的方法
 * @author gulihua
 * @Description
 * @date 2023-02-10 16:29
 */
public class GenStart
{
    public static void main(String[] args) throws Exception
    {
        String midiPath = "/Users/gulihua/Downloads/她说伴奏.mid";
        String xmlPath = "/Users/gulihua/Downloads/她说-林俊杰_伴奏.xml";
        MidParseAndGenXML gen = new MidParseAndGenXML(midiPath, xmlPath);
        gen.run();
    }
}

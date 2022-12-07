package cn.jianwoo.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jdom2.Attribute;
import org.jdom2.CDATA;
import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.Text;
import org.jdom2.input.SAXBuilder;

public final class JdomXmlHelper
{

    private static JdomXmlHelper instance;

    private JdomXmlHelper()
    {
    }


    public static JdomXmlHelper getInstance()
    {
        synchronized (JdomXmlHelper.class)
        {
            if (instance == null)
            {
                instance = new JdomXmlHelper();
            }
        }

        return instance;
    }


    private String obtainValueFrom_(Document doc, String location)
    {
        try
        {
            Element root = doc.getRootElement();
            String[] str = location.split("\\.");
            String attrName = null;

            Element target = root;
            for (int i = 0; i < str.length; i++)
            {
                String strt = str[i];
                if (strt.startsWith("@"))
                {
                    attrName = strt.substring(1);
                }
                else
                {
                    target = getTargetElement(target, strt);
                }
            }

            if (attrName == null)
            {
                return target.getText();
            }
            else
            {
                return target.getAttributeValue(attrName);
            }
        }
        catch (Exception e)
        {
            return null;
        }
    }


    public String obtainValueFrom(Document doc, String location, String name, Namespace ns)
    {
        try
        {
            Element root = doc.getRootElement().getChild(name, ns);
            String[] str = location.split("\\.");
            String attrName = null;

            Element target = root;
            for (int i = 0; i < str.length; i++)
            {
                String strt = str[i];
                if (strt.startsWith("@"))
                {
                    attrName = strt.substring(1);
                }
                else
                {
                    target = getTargetElement(target, strt);
                }
            }

            if (attrName == null)
            {
                return target.getText();
            }
            else
            {
                return target.getAttributeValue(attrName);
            }
        }
        catch (Exception e)
        {
            return null;
        }
    }


    private Element getTargetElement(Element target, String strt)
    {
        if (strt.endsWith(")"))
        {
            int startIndex = strt.indexOf('(');
            int endIndex = strt.indexOf(')');

            int index = Integer.parseInt(strt.substring(startIndex + 1, endIndex));

            return (Element) target.getChildren(strt.substring(0, startIndex)).get(index);
        }
        else if (strt.endsWith("]"))
        {
            int startIndex = strt.indexOf('[');
            int endIndex = strt.indexOf(']');

            Map<String, String> attrMap = convertAttrsToMap(strt.substring(startIndex + 1, endIndex).split(","));

            List<Element> children = target.getChildren(strt.substring(0, startIndex));
            for (Element child : children)
            {
                boolean flag = true;
                Map<String, String> elementAttrs = convertAttrsToMap(child);
                for (Entry<String, String> entry : attrMap.entrySet())
                {
                    String key = entry.getKey();
                    if (!elementAttrs.containsKey(key))
                    {
                        flag = false;
                        break;
                    }
                    if (!attrMap.get(key).equalsIgnoreCase(elementAttrs.get(key)))
                    {
                        flag = false;
                        break;
                    }
                }
                if (flag)
                {
                    return child;
                }
            }
            return null;
        }
        else
        {
            return (Element) target.getChild(strt);
        }
    }


    private Map<String, String> convertAttrsToMap(String[] attrs)
    {
        Map<String, String> map = new HashMap<String, String>();
        for (String attr : attrs)
        {
            if (attr.trim().isEmpty())
            {
                continue;
            }
            String[] str = attr.split("=");
            if (str.length != 2)
            {
                continue;
            }
            map.put(str[0], str[1]);
        }
        return map;
    }


    private Map<String, String> convertAttrsToMap(Element element)
    {
        Map<String, String> map = new HashMap<String, String>();
        List<Attribute> attributes = element.getAttributes();
        for (Attribute attr : attributes)
        {
            map.put(attr.getName(), attr.getValue());
        }
        return map;
    }


    /**
     *
     * 写入XML时有特殊字符(< > & " ')加入<![CDATA[ ]]>
     */
    public void setValueToDoc(Document doc, String location, String value)
    {
        if (null == value)
        {
            return;
        }
        if (value.contains("<") || value.contains(">") || value.contains("&") || value.contains("\"")
                || value.contains("'"))
        {
            setValue(doc, location, new CDATA(value));

        }
        else
        {
            setValue(doc, location, new Text(value));

        }

    }


    public Document build(byte[] input) throws Exception
    {
        ByteArrayInputStream is = null;

        try
        {
            is = new ByteArrayInputStream(input);
            return build(is);
        }
        finally
        {
            if (null != is)
            {
                is.close();
                is = null;
            }
        }
    }


    public Document build(InputStream ebxml) throws Exception
    {
        if (ebxml == null)
        {
            throw new NullPointerException();
        }

        SAXBuilder builder = new SAXBuilder();
        builder.setFeature("http://apache.org/xml/features/allow-java-encodings", true);
        Document document = builder.build(ebxml);

        if (document == null)
        {
            return null;
        }

        return document;
    }


    public String formatNumber(Double value)
    {
        if (value == null)
        {
            return "0.00";
        }
        DecimalFormat format = new DecimalFormat("#,##0.00###");
        return format.format(new BigDecimal(value.toString()).divide(BigDecimal.ONE, 4, RoundingMode.HALF_UP));
    }


    public Double obtainNumValueFrom(Document doc, String location)
    {
        String val = this.obtainValueFrom(doc, location);
        if (null == val)
        {
            return 0D;
        }
        return new Double(val);
    }

    public String obtainValueFrom(Document doc, String location)
    {
        String result = obtainValueFrom_(doc, location);

        if (result == null || "".equals(result.trim()) || "-".equals(result.trim()) || "---".equals(result.trim())
                || "null".equals(result.trim()))
        {
            return null;
        }
        else
        {
            return result.trim();
        }
    }

    /**
     *
     * 写入XML时加入<![CDATA[ ]]>
     */
    public void setCdataToDoc(Document doc, String location, String value)
    {
        if (null == value)
        {
            return;
        }
        setValue(doc, location, new CDATA(value));
    }


    public void setValue(Document doc, String location, Content content)
    {
        Element target = doc.getRootElement();
        String[] str = location.split("\\.");
        Element child = null;
        String attrName = null;

        for (int i = 0; i < str.length; i++)
        {
            String strt = str[i];

            if (strt.endsWith(")"))
            {
                int startIndex = strt.indexOf('(');
                int endIndex = strt.indexOf(')');

                int index = Integer.parseInt(strt.substring(startIndex + 1, endIndex));

                strt = strt.substring(0, startIndex);

                List<Element> list = target.getChildren(strt);

                if (list == null || list.size() <= index)
                {
                    Element newEle = new Element(strt);

                    target.addContent(newEle);

                    target = newEle;
                }
                else
                {
                    target = (Element) list.get(index);
                }
            }
            else if (strt.startsWith("@"))
            {
                attrName = strt.substring(1);
            }
            else
            {
                child = target.getChild(strt);
                if (child == null)
                {
                    child = new Element(strt);
                    target.addContent(child);
                }

                target = child;
            }
        }

        if (attrName == null)
        {
            target.setContent(content);
        }
        else
        {
            target.setAttribute(new Attribute(attrName, content.getValue()));
        }
    }
}

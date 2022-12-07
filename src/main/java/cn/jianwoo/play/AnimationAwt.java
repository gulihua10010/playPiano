package cn.jianwoo.play;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.*;

import cn.jianwoo.bo.CurInfoBO;
import cn.jianwoo.bo.NoteBO;
import com.jhlabs.image.GaussianFilter;

import cn.hutool.core.collection.CollUtil;
import cn.jianwoo.util.ReadXmlAsNoteUtil;

/**
 * @author gulihua
 * @Description
 * @date 2022-12-02 17:20
 */
public class AnimationAwt extends Thread
{
    public static final int WIDTH = 1200;
    public static final int HEIGHT = 600;

    private final static int UNIT = 400;

    private List<NoteBO> list;
    private int curIdx1;
    private int curIdx2;
    private int pointer;

    private int maxWidth;
    private Double startTime1 = 0d;
    private Double startTime2 = 0d;
    private int minVal;
    private int maxVal;
    private boolean isFinished;

    private int lastPoint;

    private CurInfoBO curInfo;

    public AnimationAwt()
    {
    }


    public AnimationAwt(List<NoteBO> list, CurInfoBO curInfo)
    {
        this.list = list;
        this.curInfo = curInfo;
    }


    public AnimationAwt(CurInfoBO curInfo)
    {
        this.curInfo = curInfo;
    }


    public CurInfoBO getCurInfo()
    {
        return this.curInfo;
    }


    public void setCurInfo(CurInfoBO curInfo)
    {
        this.curInfo = curInfo;
    }


    public boolean getIsFinished()
    {
        return this.isFinished;
    }


    public void setIsFinished(boolean finished)
    {
        this.isFinished = finished;
    }


    public int getPointer()
    {
        return this.pointer;
    }


    public void setPointer(int pointer)
    {
        this.pointer = pointer;
    }


    public Double getStartTime1()
    {
        return this.startTime1;
    }


    public void setStartTime1(Double startTime1)
    {
        this.startTime1 = startTime1;
    }


    public Double getStartTime2()
    {
        return this.startTime2;
    }


    public void setStartTime2(Double startTime2)
    {
        this.startTime2 = startTime2;
    }


    public int getMaxWidth()
    {
        return this.maxWidth;
    }


    public void setMaxWidth(int maxWidth)
    {
        this.maxWidth = maxWidth;
    }


    public List<NoteBO> getList()
    {
        return this.list;
    }


    public void setList(List<NoteBO> list)
    {
        this.list = list;
    }


    public int getCurIdx1()
    {
        return this.curIdx1;
    }


    public void setCurIdx1(int curIdx1)
    {
        this.curIdx1 = curIdx1;
    }


    public int getCurIdx2()
    {
        return this.curIdx2;
    }


    public void setCurIdx2(int curIdx2)
    {
        this.curIdx2 = curIdx2;
    }


    public int getMinVal()
    {
        return this.minVal;
    }


    public void setMinVal(int minVal)
    {
        this.minVal = minVal;
    }


    public int getMaxVal()
    {
        return this.maxVal;
    }


    public void setMaxVal(int maxVal)
    {
        this.maxVal = maxVal;
    }


    public static void main(String[] args)
    {
        AnimationAwt gui = new AnimationAwt();
//        String path = ResourceUtil.getResource("notes/1111111.xml").getPath();
        List<NoteBO> noteList_n = ReadXmlAsNoteUtil.getInstance()
                .readAsNote("/Users/gulihua/tmp/tempx/webUpload/1111111.xml", NoteBO.Mode.MAIN);
        gui.setList(noteList_n);
        gui.setMinVal(65);
        gui.setMaxVal(87);
        gui.start();
    }


    public void run()
    {
        // 获取Frame对象
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // 声明组件对象
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setBounds(0, 0, WIDTH, HEIGHT);

        GridPanel gridPanel = new GridPanel();
        gridPanel.setOpaque(false);
        layeredPane.add(gridPanel, 1);
        gridPanel.setSize(layeredPane.getPreferredSize());
        gridPanel.setLocation(0, 0);

        NotePanel notePanel = new NotePanel();
        layeredPane.add(notePanel, 2);
        notePanel.setOpaque(false);
        notePanel.setSize(layeredPane.getPreferredSize());
        notePanel.setLocation(0, 2);
        PointPanel pointPanel = new PointPanel();
        pointPanel.setOpaque(false);
        layeredPane.add(pointPanel, 1);
        pointPanel.setSize(layeredPane.getPreferredSize());
        pointPanel.setLocation(0, 0);
        // 添加画布并且设置大小
        frame.setSize(WIDTH, HEIGHT);
        frame.setVisible(true);
        frame.add(layeredPane);
        // 通过for循环实现坐标不断移动并且重新绘画

//        double currentTime = getCurInfo().getCurrentTime();
//        Double cur = currentTime * UNIT;
//        pointer = cur.intValue();
//
//        while (getCurInfo().getCurrentTime() < 0)
//        {
//            try
//            {
//                sleep(10);
//            }
//            catch (InterruptedException e)
//            {
//                throw new RuntimeException(e);
//            }
//        }
        while (curIdx1 < getList().size() - 1)
        {
            // 指针重新从左开始
            if (pointer - lastPoint == 0)
            {
                setIsFinished(false);
            }
//            if (getCurInfo().getCurrentTime() != currentTime)
//            {
////                System.out.println(getCurInfo().getCurrentTime() +","+currentTime);
//                currentTime = getCurInfo().getCurrentTime();
//                cur = currentTime * UNIT;
//                pointer = cur.intValue();
//            }

            pointer += 1;

            layeredPane.repaint();
            if (0 != getMaxWidth() && pointer - lastPoint > getMaxWidth())
            {
                setCurIdx2(curIdx1);
                setStartTime2(startTime1);
                lastPoint = pointer;

            }
            try
            {
                Thread.sleep(6);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    /***
     * 背景网格
     */
    class GridPanel extends JPanel
    {
        @Override
        protected void paintComponent(Graphics g)
        {
            g.setColor(new Color(230, 230, 230));
            for (int i = 0; i < 50; i++)
            {
                g.drawLine(0, 10 * (i + 1), this.getWidth(), 10 * (i + 1));

            }
        }
    }

    /***
     * 音符
     */
    class NotePanel extends JPanel
    {

        @Override
        protected void paintComponent(Graphics g)
        {
            // 用白色覆盖来消除痕迹
            g.setColor(Color.white);
            g.fillRect(0, 0, this.getWidth(), this.getHeight());

            g.setColor(Color.green);
            for (int i = curIdx2; i < list.size(); i++)
            {
                NoteBO node = list.get(i);
                Double startTime = (node.getStartTime() - getStartTime2()) * UNIT;
                Double length = node.getNoteLength() * UNIT;
                int height = calcHeight(node.getValue());
                if (!getIsFinished() && startTime > this.getWidth() - 200)
                {
                    setStartTime1(node.getStartTime());
                    setMaxWidth(startTime.intValue());
                    setCurIdx1(i);
                    setIsFinished(true);

                }
                // 判断是否结束
                if (i >= list.size() - 1)
                {
                    setCurIdx1(i);
                }
                if (startTime > this.getWidth())
                {
                    break;
                }
                if (pointer - lastPoint > startTime.intValue())
                {
                    Graphics2D g2d = (Graphics2D) g.create();

                    if (0 != length.intValue())
                    {
                        BufferedImage img = generateGlow(length.intValue(), 9, 20, Color.GREEN, 1f);
                        g2d.drawImage(img, startTime.intValue() - 25, height - 24, this);
                        g2d.setColor(Color.GREEN);
                        g2d.fillRect(startTime.intValue(), height, length.intValue(), 10);
                        g2d.dispose();
                    }
                }
                else
                {
                    g.fillRect(startTime.intValue(), height, length.intValue(), 10);

                }
                if (CollUtil.isNotEmpty(node.getMergeVals()))
                {
                    for (NoteBO.MergeNote mergeNote : node.getMergeVals())
                    {
                        length = mergeNote.getNoteLength() * UNIT;
                        height = calcHeight(mergeNote.getValue());
                        if (pointer - lastPoint > startTime.intValue())
                        {
                            Graphics2D g2d = (Graphics2D) g.create();

                            if (0 != length.intValue())
                            {
                                BufferedImage img = generateGlow(length.intValue(), 9, 20, Color.GREEN, 1f);
                                g2d.drawImage(img, startTime.intValue() - 25, height - 24, this);
                                g2d.setColor(Color.GREEN);
                                g2d.fillRect(startTime.intValue(), height, length.intValue(), 10);
                                g2d.dispose();
                            }
                        }
                        else
                        {
                            g.fillRect(startTime.intValue(), height, length.intValue(), 10);

                        }
                    }
                }

            }
        }


        private int calcHeight(int value)
        {
            return 10 * (maxVal - value) + 148;

        }


        public BufferedImage generateGlow(int width, int height, int size, Color glow, float alpha)
        {

            BufferedImage source = createCompatibleImage(width, height);
            Graphics2D g2d = source.createGraphics();
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, width, height);
            g2d.dispose();
            return generateGlow(source, size, glow, alpha);
        }


        public BufferedImage generateGlow(BufferedImage imgSource, int size, Color color, float alpha)
        {

            int imgWidth = (int) Math.round(imgSource.getWidth() + (size * 2.5));
            int imgHeight = (int) Math.round(imgSource.getHeight() + (size * 2.5));

            BufferedImage imgMask = createCompatibleImage(imgWidth, imgHeight);
            Graphics2D g2 = imgMask.createGraphics();

            int x = Math.round((imgWidth - imgSource.getWidth()) / 2f);
            int y = Math.round((imgHeight - imgSource.getHeight()) / 2f);
            g2.drawImage(imgSource, x, y, null);
            g2.dispose();

            BufferedImage imgGlow = generateBlur(imgMask, size, color, alpha);

            imgGlow = applyMask(imgGlow, imgMask, AlphaComposite.DST_OUT);

            return imgGlow;

        }


        public BufferedImage generateBlur(BufferedImage imgSource, int size, Color color, float alpha)
        {

            GaussianFilter filter = new GaussianFilter(size);

            int imgWidth = imgSource.getWidth();
            int imgHeight = imgSource.getHeight();

            BufferedImage imgBlur = createCompatibleImage(imgWidth, imgHeight);
            Graphics2D g2 = imgBlur.createGraphics();

            g2.drawImage(imgSource, 0, 0, null);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_IN, alpha));
            g2.setColor(color);

            g2.fillRect(0, 0, imgSource.getWidth(), imgSource.getHeight());
            g2.dispose();

            imgBlur = filter.filter(imgBlur, null);

            return imgBlur;

        }


        public BufferedImage createCompatibleImage(int width, int height)
        {
            return createCompatibleImage(width, height, Transparency.TRANSLUCENT);
        }


        public BufferedImage createCompatibleImage(int width, int height, int transparency)
        {
            BufferedImage image = getGraphicsConfiguration().createCompatibleImage(width, height, transparency);
            image.coerceData(true);
            return image;
        }


        public GraphicsConfiguration getGraphicsConfiguration()
        {
            return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        }


        public BufferedImage applyMask(BufferedImage sourceImage, BufferedImage maskImage, int method)
        {
            BufferedImage maskedImage = null;
            if (sourceImage != null)
            {
                int width = maskImage.getWidth(null);
                int height = maskImage.getHeight(null);

                maskedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                Graphics2D mg = maskedImage.createGraphics();

                int x = (width - sourceImage.getWidth(null)) / 2;
                int y = (height - sourceImage.getHeight(null)) / 2;

                mg.drawImage(sourceImage, x, y, null);
                mg.setComposite(AlphaComposite.getInstance(method));

                mg.drawImage(maskImage, 0, 0, null);

                mg.dispose();
            }
            return maskedImage;
        }
    }

    /***
     * 动态指针
     */
    class PointPanel extends JPanel
    {
        @Override
        protected void paintComponent(Graphics g)
        {

            g.setColor(Color.GRAY);
            g.drawLine(pointer - lastPoint, 0, pointer - lastPoint, this.getHeight());
        }
    }

}

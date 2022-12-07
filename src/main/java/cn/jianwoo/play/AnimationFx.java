package cn.jianwoo.play;

import java.util.ArrayList;
import java.util.List;

import cn.hutool.core.collection.CollUtil;
import cn.jianwoo.bo.CurInfoBO;
import cn.jianwoo.bo.NoteBO;
import cn.jianwoo.util.ReadXmlAsNoteUtil;
import cn.jianwoo.util.SortNoteUtil;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.Effect;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.Glow;
import javafx.scene.effect.Shadow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * @author gulihua
 * @Description
 * @date 2022-12-04 21:33
 */
public class AnimationFx extends Application implements Runnable
{

    public static final int WIDTH = 1200;
    public static final int HEIGHT = 600;

    private final static int UNIT = 400;
    private final static String TITLE = "[B站 | 顾咕咕了] https://jianwoo.cn";

    private static AnimationFx instance;

    /** 音符 */
    private List<NoteBO> list;
    /** list 当前索引1 */
    private int curIdx1;
    /** list 当前索引2 */
    private int curIdx2;
    /** 指针当前 x 坐标像素位置 */
    private Double pointer = 0d;

    /** 指针移动到的最大宽度 */
    private int maxWidth;

    /** 起始时间 1 */
    private Double startTime1 = 0d;
    /** 起始时间 2 */
    private Double startTime2 = 0d;

    /** 音符最小值, 用于确定音符的 y 坐标位置 */

    private int minVal;
    /** 音符最大值, 用于确定音符的 y 坐标位置 */
    private int maxVal;

    /** 指针最后扫描过的累计长度 */
    private Double lastPoint = 0d;

    /** 指针对象, 用于同步AudioPlayNew里的当前时间 */
    private CurInfoBO curInfo;
    /** 当前时间 */
    private double currentTime;

    /** 左上角时间显示 */
    private int hour = 0;
    private int min = 0;
    private int sec = 0;

    /** 是否调试 */
    private boolean isDebug;
    /** 歌曲名称 */
    private String name;
    /** 是否结束 */
    private boolean isFinished;

    public AnimationFx()
    {

    }


    public String getName()
    {
        return this.name;
    }


    public void setName(String name)
    {
        this.name = name;
    }


    public CurInfoBO getCurInfo()
    {
        return this.curInfo;
    }


    public void setCurInfo(CurInfoBO curInfo)
    {
        this.curInfo = curInfo;
    }


    public List<NoteBO> getList()
    {
        return this.list;
    }


    public void setList(List<NoteBO> list)
    {
        this.list = list;
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


    public boolean getIsDebug()
    {
        return this.isDebug;
    }


    public AnimationFx setIsDebug(boolean debug)
    {
        this.isDebug = debug;
        return this;
    }


    public AnimationFx setMinAndMaxVal(int minVal, int maxVal)
    {
        this.minVal = minVal;
        this.maxVal = maxVal;
        return this;
    }


    @Override
    public void start(Stage stage) throws Exception
    {
        if (null == instance)
        {
            System.err.println("实例为空!!");
            System.exit(0);
            return;
        }
        if (CollUtil.isEmpty(instance.list))
        {
            System.err.println("音符集合为空!!");
            System.exit(0);
            return;
        }
        Pane gridPane = instance.createGridPane();
        Pane notePane = new Pane();
        instance.createNotePane(notePane);
        Pane pointPane = instance.createPointPane(notePane);
        final Scene scene = new Scene(new Group(gridPane, notePane, pointPane), WIDTH, HEIGHT, Color.WHITE);
        instance.scanNotePane(scene);
        stage.setScene(scene);
        stage.show();
        stage.setTitle(instance.name + " _ " + TITLE);
        stage.setOnCloseRequest(event -> {
            System.exit(0);
        });

    }


    /***
     * 动态指针
     */
    private Pane createPointPane(Pane notePane)
    {
        Pane pointPane = new Pane();
        Line line = new Line(0, 0, 0, WIDTH);
        line.setStroke(Color.GRAY);
        pointPane.getChildren().add(line);
        if (!isDebug)
        {
            currentTime = getCurInfo().getCurrentTime();
        }
        // 定时器, 用于刷新指针
        Timeline timeline = getCurInfo().getTimeline();
        KeyFrame kf = new KeyFrame(Duration.seconds(0.05), event -> {

            if (0 != maxWidth && pointer - lastPoint > maxWidth)
            {
                if (isFinished && pointer - lastPoint > WIDTH)
                {
                    timeline.stop();
                }
                if (!isFinished)
                {
                    curIdx2 = curIdx1;
                    startTime2 = startTime1;
                    lastPoint = pointer;
                    notePane.getChildren().clear();
                    createNotePane(notePane);
                }
            }

            if (!isDebug)
            {
                if (getCurInfo().getCurrentTime() != currentTime)
                {
                    currentTime = getCurInfo().getCurrentTime();
                    pointer = currentTime * UNIT;
                }
            }
            if (getCurInfo().getCurrentTime() >= 0)
            {

                pointer += 1;
            }
            line.setTranslateX(pointer - lastPoint);

        });

        timeline.getKeyFrames().addAll(kf);
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.setRate(7);
        timeline.play();
        return pointPane;
    }


    /***
     * 音符
     */
    private void createNotePane(Pane notePane)
    {
        boolean isFinished = false;
        for (int i = curIdx2; i < list.size(); i++)
        {
            NoteBO node = list.get(i);
            if (node.getValue() == 0)
            {
                continue;
            }
            Double startTime = (node.getStartTime() - startTime2) * UNIT;
            Double length = node.getNoteLength() * UNIT;
            int height = calcHeight(node.getValue());
            // 当当前时间快大于面板宽度时，记录下 list 索引和指针的最大移动位置
            if (!isFinished && startTime > WIDTH - 200)
            {
                startTime1 = node.getStartTime();
                maxWidth = startTime.intValue();
                curIdx1 = i;
                isFinished = true;
            }
            // 判断是否结束
            if (i >= list.size() - 1)
            {
                this.isFinished = true;
                curIdx1 = i;
            }
            if (startTime > WIDTH)
            {
                break;
            }
            Color color = Color.LIGHTSEAGREEN;
            if (NoteBO.Mode.ACCOMPANIMENTS.equals(node.getMode()))
            {
                color = Color.rgb(147, 143, 234, 0.5);
            }
            Rectangle rect = new Rectangle(startTime.intValue(), height, length.intValue(), 10);
            rect.setFill(color);
            rect.setId(node.getId());
            notePane.getChildren().add(rect);

            if (CollUtil.isNotEmpty(node.getMergeVals()))
            {
                for (NoteBO.MergeNote mergeNote : node.getMergeVals())
                {
                    if (NoteBO.Mode.ACCOMPANIMENTS.equals(mergeNote.getMode()))
                    {
                        color = Color.rgb(147, 143, 234, 0.5);
                    }
                    length = mergeNote.getNoteLength() * UNIT;
                    height = calcHeight(mergeNote.getValue());
                    Rectangle rectMerge = new Rectangle(startTime.intValue(), height, length.intValue(), 10);
                    rectMerge.setId(mergeNote.getId());
                    rectMerge.setFill(color);
                    notePane.getChildren().add(rectMerge);

                }
            }

        }

    }


    /***
     * 指针扫描到音符是发光
     */
    private void scanNotePane(Scene scene)
    {

        // 创建 时间显示器
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.05), event -> {
            for (int i = curIdx2; i < list.size(); i++)
            {
                NoteBO node = list.get(i);
                if (node.getValue() == 0)
                {
                    continue;
                }
                Double startTime = (node.getStartTime() - startTime2) * UNIT;
                Color color = Color.LIGHTSEAGREEN;
                if (NoteBO.Mode.ACCOMPANIMENTS.equals(node.getMode()))
                {
                    color = Color.rgb(147, 143, 234);
                }

                // 当指针扫描到时, 音符发光
                if (pointer - lastPoint > startTime.intValue())
                {
                    Node n = scene.lookup("#" + node.getId());
                    if (n != null)
                    {
                        n.setEffect(glow(color));

                    }
                }
                if (CollUtil.isNotEmpty(node.getMergeVals()))
                {
                    for (NoteBO.MergeNote mergeNote : node.getMergeVals())
                    {
                        if (NoteBO.Mode.ACCOMPANIMENTS.equals(mergeNote.getMode()))
                        {
                            color = Color.rgb(147, 143, 234);
                        }

                        if (pointer - lastPoint > startTime.intValue())
                        {
                            Double length = mergeNote.getNoteLength() * UNIT;
                            if (0 != length.intValue())
                            {
                                Node n = scene.lookup("#" + mergeNote.getId());
                                if (n != null)
                                {
                                    n.setEffect(glow(color));
                                }
                            }
                        }
                    }
                }
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.setRate(7);
        timeline.play();
    }


    /***
     * 背景网格
     */
    private Pane createGridPane()
    {
        Pane gridPane = new Pane();
        for (int i = 0; i < 100; i++)
        {
            Line line = new Line(0, 10 * (i + 1) + 30, WIDTH, 10 * (i + 1) + 30);
            line.setStroke(Color.rgb(230, 230, 230));
            gridPane.getChildren().add(line);
        }
        Text mainText = new Text();
        mainText.setText("主奏: ");
        mainText.setX(20);
        mainText.setY(15);
        gridPane.getChildren().add(mainText);

        Text accText = new Text();
        accText.setText("伴奏: ");
        accText.setX(20);
        accText.setY(33);
        gridPane.getChildren().add(accText);

        Text text = new Text();
        text.setText("00:00:00");
        text.setX(90);
        text.setY(23);
        text.setFont(Font.font("Abyssinica SIL", FontWeight.BOLD, FontPosture.REGULAR, 20));
        gridPane.getChildren().add(text);

        Rectangle mainRect = new Rectangle(55, 3, 15, 15);
        mainRect.setFill(Color.LIGHTSEAGREEN);
        gridPane.getChildren().add(mainRect);

        Rectangle accRect = new Rectangle(55, 20, 15, 15);
        accRect.setFill(Color.rgb(147, 143, 234, 0.5));
        gridPane.getChildren().add(accRect);

        // 创建 时间显示器
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            if (getCurInfo().getCurrentTime() >= 0)
            {
                sec++;
                if (sec >= 60)
                {
                    min++;
                    sec = 0;
                }
                if (min >= 60)
                {
                    hour++;
                    min = 0;
                }
                text.setText(String.format("%s:%s:%S", format(hour), format(min), format(sec)));

            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.setRate(1);
        timeline.play();
        return gridPane;
    }


    private String format(int t)
    {
        if (t < 10) return "0" + t;
        return String.valueOf(t);
    }


    /** 发光效果 */
    private Effect glow(Color color)
    {
        Glow glow = new Glow();
        glow.setLevel(10);
        GaussianBlur g = new GaussianBlur();
        g.setRadius(5);
        glow.setInput(g);
        Shadow shadow = new Shadow();
        shadow.setBlurType(BlurType.GAUSSIAN);
        shadow.setColor(color);
        shadow.setHeight(30);
        shadow.setRadius(12);
        shadow.setWidth(20);
        glow.setInput(shadow);
        return glow;
    }


    private int calcHeight(int value)
    {
        return 10 * (maxVal - value) + 100;

    }


    public static AnimationFx build(List<NoteBO> list, String name, CurInfoBO curInfo)
    {
        instance = new AnimationFx();
        instance.setList(list);
        instance.setCurInfo(curInfo);
        instance.setName(name);
        return instance;
    }


    public void start()
    {

        launch();
    }


    public static void main(String[] args)
    {
        List<NoteBO> noteList_n = ReadXmlAsNoteUtil.getInstance()
                .readAsNote("/Users/gulihua/tmp/tempx/webUpload/起风了_主奏.xml", NoteBO.Mode.MAIN);
        List<NoteBO> noteList_n1 = ReadXmlAsNoteUtil.getInstance()
                .readAsNote("/Users/gulihua/tmp/tempx/webUpload/起风了_伴奏.xml", NoteBO.Mode.ACCOMPANIMENTS);

        CurInfoBO curInfo = new CurInfoBO();
        List<NoteBO> list = new ArrayList<>();
        list.addAll(noteList_n);
        list.addAll(noteList_n1);
        list = SortNoteUtil.processMergeNoteBo(list);
        AnimationFx.build(list, "大鱼", curInfo).setIsDebug(true).setMinAndMaxVal(65, 87).start();

    }


    @Override
    public void run()
    {
        launch();
    }
}

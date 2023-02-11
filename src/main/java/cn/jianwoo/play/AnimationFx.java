package cn.jianwoo.play;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import cn.jianwoo.bo.CurInfoBO;
import cn.jianwoo.bo.NoteBO;
import cn.jianwoo.util.NoteUtil;
import cn.jianwoo.util.ReadXmlAsNoteUtil;
import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
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

    public static final int WIDTH = 1500;
    public static final int HEIGHT = 900;

    private final static int UNIT = 400;
    private final static String TITLE = "[B站 | 顾咕咕了] https://jianwoo.cn";

    private static AnimationFx instance;

    private static final Log log = LogFactory.get();
    /** 音符 */
    private List<NoteBO> list;
    /** list 当前索引1 */
    private int curIdx1;
    /** list 当前索引2 */
    private int curIdx2;
    /** 指针当前 x 坐标像素位置 */
    private Double pointer = 0d;
    /** 指针组件 */
    private Line line;

    /** 指针移动到的最大宽度 */
    private int maxWidth;

    /** 起始时间 1 */
    private Double startTime1 = 0d;
    /** 起始时间 2 */
    private Double startTime2 = 0d;
    /** 音符最大值, 用于确定音符的 y 坐标位置 */
    private int maxVal;

    /** 指针最后扫描过的累计长度 */
    private Double lastPoint = 0d;

    /** 指针对象, 用于同步AudioPlayNew里的当前时间 */
    private CurInfoBO curInfo;
    /** 音符面板 */
    private Pane notePane;

    /** 左上角时间显示 */
    private int hour = 0;
    private int min = 0;
    private int sec = 0;
    private int costSec = 0;

    /** 是否调试 */
    private boolean isDebug;
    /** 歌曲名称 */
    private String name;
    /** 是否结束 */
    private boolean isFinished;

    /** 是否暂停 */
    private boolean isPause;

    /** 是否在拖拽 */
    private boolean isDrag;
    /** 速率 */
    private Double rate;

    private List<NoteBO> currNoteList;

    private static final URL RES = ResourceUtil.getResource("assets");

    public AnimationFx()
    {

    }


    public String getName()
    {
        return this.name;
    }


    private void setName(String name)
    {
        this.name = name;
    }


    private void setCurInfo(CurInfoBO curInfo)
    {
        this.curInfo = curInfo;
    }


    private void setList(List<NoteBO> list)
    {
        this.list = list;
        if (CollUtil.isNotEmpty(list))
        {
            this.rate = list.get(0).getRate();
            if (this.rate == null)
            {
                this.rate = 1D;
            }
            System.out.println(rate);
        }
    }


    public int getMaxVal()
    {
        return this.maxVal;
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


    public AnimationFx setMaxVal(int maxVal)
    {
        this.maxVal = maxVal;

        if (this.maxVal <= 21)
        {
            this.maxVal = 108;
        }

        if (this.maxVal % 12 == 1 || this.maxVal % 12 == 3 || this.maxVal % 12 == 6 || this.maxVal % 12 == 8
                || this.maxVal % 12 == 10)
        {
            this.maxVal = this.maxVal + 1;
        }

        return this;
    }


    @Override
    public void start(Stage stage) throws Exception
    {
        if (null == instance)
        {
            log.error("实例为空!!");
            System.exit(0);
            return;
        }
        if (null == instance.curInfo)
        {
            log.error("请初始化参数curInfo!!");
            System.exit(0);
            return;
        }
        if (CollUtil.isEmpty(instance.list))
        {
            log.error("音符集合为空!!");
            System.exit(0);
            return;
        }
        Pane gridPane = instance.createGridPane();
        instance.notePane = new Pane();
        instance.createNotePane();
        Pane pointPane = instance.createPointPane();
        Pane pianoPane = instance.createPianoKey();
        final Scene scene = new Scene(new Group(gridPane, pianoPane, pointPane, instance.notePane), WIDTH, HEIGHT,
                Color.WHITE);
        Platform.runLater(() -> {
            instance.scanNotePane(scene);
        });
        stage.setScene(scene);
        stage.show();
        stage.setTitle(instance.name + " _ " + TITLE);
        scene.getStylesheets().add(RES.toExternalForm() + "css/style.css");
        stage.setOnCloseRequest(event -> {
            System.exit(0);
        });
        instance.curInfo.completeReady();

    }


    /***
     * 动态指针
     */
    private Pane createPointPane()
    {
        Pane pointPane = new Pane();
        pointPane.setLayoutY(40);
        pointPane.setLayoutX(100);
        line = new Line(0, 0, 0, WIDTH);
        line.setStroke(Color.GRAY);
        pointPane.getChildren().add(line);
        Platform.runLater(() -> {
            Timeline timeline = new Timeline();
            KeyFrame kf = new KeyFrame(Duration.millis(10), event -> {
                if (curInfo.getTimestamp() == 0 || isPause)
                {
                    return;
                }
                long now = System.currentTimeMillis();
                double cost = now - curInfo.getTimestamp();

                pointer = cost / 180 * 25;
                if (!isDrag)
                {
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
                            createNotePane();
                        }
                    }

                }
                line.setTranslateX(pointer - lastPoint);
            });
            timeline.getKeyFrames().add(kf);
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.setRate(1);
            timeline.play();
        });

        return pointPane;
    }


    /***
     * 音符
     */
    private void createNotePane()
    {
        notePane.setLayoutY(30);
        notePane.setLayoutX(100);
        boolean isFinished = false;
        currNoteList = new ArrayList<>();
        for (int i = curIdx2; i < list.size(); i++)
        {
            NoteBO note = list.get(i);
            if (note.getValue() == 0)
            {
                continue;
            }
//            System.out.println("<<"+note.getStartTime());
            Double startTime = (note.getStartTime() - startTime2) * UNIT * rate;
            Double length = note.getNoteLength() * UNIT * rate;
            int height = calcHeight(note.getValue());
            // 当当前时间快大于面板宽度时，记录下 list 索引和指针的最大移动位置
            if (!isFinished && startTime > WIDTH - 200)
            {
                startTime1 = note.getStartTime();
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

            Button btn = new Button();
            btn.setId(note.getId());
            btn.setLayoutX(startTime.intValue());
            btn.setLayoutY(height);
            btn.setPrefWidth(length.intValue());
            btn.setMaxHeight(10);
            btn.setMinHeight(10);
            btn.setWrapText(true);
            btn.getStyleClass().add(note.getMode().name());
            Tooltip tooltip = new Tooltip();
            tooltip.setText(String.format("音符:%s, 时长:%s", note.getNote(), note.getId()));
            btn.setTooltip(tooltip);
            btn.setOnAction(arg0 -> {
                new Audio(note.getNote()).start();
            });
            notePane.getChildren().add(btn);
            currNoteList.add(new NoteBO(note.getMode(), note.getValue(), btn, note.getStartTime()));
            if (CollUtil.isNotEmpty(note.getMergeVals()))
            {
                for (NoteBO.MergeNote mergeNote : note.getMergeVals())
                {
                    length = mergeNote.getNoteLength() * UNIT * rate;
                    height = calcHeight(mergeNote.getValue());
                    Button btnMerge = new Button();
                    btnMerge.setId(mergeNote.getId());
                    btnMerge.setLayoutX(startTime.intValue());
                    btnMerge.setLayoutY(height);
                    btnMerge.setPrefWidth(length.intValue());
                    btnMerge.setPrefHeight(10);
                    btnMerge.setMaxHeight(10);
                    btnMerge.setMinHeight(10);
                    btnMerge.setWrapText(true);
                    btnMerge.getStyleClass().add(mergeNote.getMode().name());
                    Tooltip tooltipMerge = new Tooltip();
                    tooltipMerge.setText(String.format("音符:%s, 时长:%s", mergeNote.getNote(), mergeNote.getId()));
                    btnMerge.setTooltip(tooltipMerge);
                    btnMerge.setOnAction(arg0 -> {
                        new Audio(mergeNote.getNote()).start();
                    });
                    notePane.getChildren().add(btnMerge);
                    currNoteList.add(new NoteBO(mergeNote.getMode(), note.getValue(), btnMerge, note.getStartTime()));

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
            if (null == currNoteList)
            {
                return;
            }
            Iterator<NoteBO> it = currNoteList.iterator();
            while (it.hasNext())
            {
                NoteBO note = it.next();
                Color color = Color.LIGHTSEAGREEN;
                if (NoteBO.Mode.ACCOMPANIMENTS.equals(note.getMode()))
                {
                    color = Color.rgb(147, 143, 234);
                }
                // 当指针扫描到时, 音符发光
                if (pointer - lastPoint > note.getNode().getLayoutX())
                {
                    if (note.getNode().getEffect() == null)
                    {
                        if (curInfo.getIsDebug())
                        {
                            log.debug(">>>>>AnimationFx:: mode= {}, value= {}, startTime= {}, posX= {}, pointer= {}",
                                    note.getMode(), note.getValue(), note.getStartTime(), note.getNode().getLayoutX(),
                                    (pointer - lastPoint));
                        }

                        note.getNode().setEffect(glow(color));

                        Node node = scene.lookup("#" + note.getValue());
                        if (node != null)
                        {

                            String blackBg = "-fx-background-color: BLACK;";
                            String whiteBg = "-fx-background-color: white;";
                            String blackHover = "-fx-background-color: #666;";
                            String whiteHover = "-fx-background-color: #ccc;";
                            String bg = whiteBg;
                            String hover = whiteHover;
                            if (isBlackKey(note.getValue()))
                            {
                                bg = blackBg;
                                hover = blackHover;
                            }
                            final String finalBg = bg;
                            final String finalHover = hover;
                            final Animation animation = new Transition() {
                                {
                                    setCycleDuration(Duration.millis(200));
                                }

                                @Override
                                protected void interpolate(double progress)
                                {
                                    if (progress >= 1)
                                    {
                                        node.setStyle(finalBg);
                                    }
                                    else
                                    {
                                        node.setStyle(finalHover);
                                    }
                                }
                            };
                            animation.play();
                        }

                        it.remove();
                    }
                }
                else
                {
                    break;
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
        gridPane.setLayoutX(100);
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

        try
        {

            Button btn = new Button();
            btn.setWrapText(true);
            btn.setLayoutX(300);
            btn.setLayoutY(5);
            btn.getStyleClass().add("icon-button-pause");
            btn.setPickOnBounds(true);
            Tooltip tooltip = new Tooltip();
            tooltip.setText("暂停");
            btn.setTooltip(tooltip);
            btn.setOnAction(arg0 -> {
                // 暂停
                if (!this.isPause)
                {
                    btn.getStyleClass().remove("icon-button-pause");
                    btn.getStyleClass().add("icon-button-play");
                    tooltip.setText("播放");
                    this.curInfo.pause();
                }
                // 播放
                else
                {
                    btn.getStyleClass().remove("icon-button-play");
                    btn.getStyleClass().add("icon-button-pause");
                    tooltip.setText("暂停");
                    this.curInfo.play();
                    synchronized (this.curInfo.getLock())
                    {
                        this.curInfo.getLock().notifyAll();
                    }

                    this.curInfo.calcIdxAndTime(-1D);

                }
                this.isPause = !this.isPause;

            });
            gridPane.getChildren().add(btn);

            Slider slider = new Slider(0, 100, 0);
            slider.setPrefWidth(500);
            slider.setLayoutX(350);
            slider.setLayoutY(10);
            gridPane.getChildren().add(slider);
            double totalTime = NoteBO.calcTime(list.get(list.size() - 1).getEndTime(),
                    list.get(list.size() - 1).getRate()) / 1000;
            // 创建 时间显示器
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
                if (curInfo.getTimestamp() > 0 && !curInfo.isPause())
                {
                    sec++;
                    costSec++;
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
                    text.setText(String.format("%02d:%02d:%02d", hour, min, sec));
                    slider.setValue(costSec / totalTime * 100);
                }
            }));
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.setRate(1);
            timeline.play();
            slider.setOnMouseReleased((EventHandler<Event>) event -> {
                this.isDrag = true;
                Double v = slider.getValue();
                Double t = v / 100 * totalTime;
                costSec = t.intValue();
                hour = costSec / 60 / 60;
                min = costSec / 60;
                sec = costSec % 60;
                NoteUtil.findTime(list, t * 1000 / 180 * 0.0625, p -> {

                    this.curInfo.calcIdxAndTime(t * 1000 / 180 * 0.0625);
                    this.curIdx2 = p.getSequence();
                    this.startTime2 = p.getStartTime();
                    this.notePane.getChildren().clear();
                    long now = System.currentTimeMillis();
                    this.curInfo.setTimestamp(
                            System.currentTimeMillis() - NoteBO.calcTime(p.getStartTime(), p.getRate()).intValue());
                    double cost = now - curInfo.getTimestamp();
                    pointer = cost / 180 * 25;
                    this.lastPoint = pointer;
                    line.setTranslateX(0);
                    createNotePane();
                    if (curInfo.getIsDebug())
                    {
                        log.debug(">>>>>\t\tOnMouseReleased:: mode= {}, startTime= {}", p.getMode(), p.getStartTime());
                    }
                    System.out.println(">>M_" + p.getMode() + ", " + p.getStartTime());

                });
                this.isDrag = false;

            });
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        return gridPane;
    }


    /***
     * 左侧的钢琴键
     */
    private Pane createPianoKey()
    {
        Pane pianoPane = new Pane();
        // C
        int index1 = 0;
        int index2 = 4;
        int offset1 = -7;
        int offset2 = -4;
        // D
        if (this.maxVal % 12 == 2)
        {
            index1 = 1;
            index2 = 5;
            offset1 = -3;
            offset2 = -1;
        }
        // E
        else if (this.maxVal % 12 == 4)
        {
            index1 = 2;
            index2 = 6;
            offset1 = -1;
            offset2 = 1;
        }
        // F
        else if (this.maxVal % 12 == 5)
        {
            index1 = 0;
            index2 = 3;
            offset1 = -8;
            offset2 = -6;
        }
        // G
        else if (this.maxVal % 12 == 7)
        {
            index1 = 1;
            index2 = 4;
            offset1 = -5;
            offset2 = -3;
        }
        // A
        else if (this.maxVal % 12 == 9)
        {
            index1 = 2;
            index2 = 5;
            offset1 = -1;
            offset2 = 1;
        }
        // B
        else if (this.maxVal % 12 == 11)
        {
            index1 = 3;
            index2 = 6;
            offset1 = 0;
            offset2 = 2;
        }
        int key = this.maxVal;
        int group = 0;
        for (int i = 0; i < 100; i++)
        {
            Button whiteBtn = new Button();
            int height = 17;
            if (key % 12 == 0)
            {
                String str = "\t\t    C";
                if (key == 60)
                {
                    str = "\t\t   · C";
                }
                Text text = new Text(str + key / 12);
                whiteBtn.setGraphic(text);
                height = 18;
            }

            String note = NoteBO.generateNote(key);
            whiteBtn.setId(Integer.toString(key));
            whiteBtn.setPrefWidth(100);
            whiteBtn.setMaxHeight(height);
            whiteBtn.setMinHeight(height);
            whiteBtn.setWrapText(true);
            whiteBtn.setLayoutX(0);
            whiteBtn.setLayoutY(i * 17 + 40 + offset1 + group);
            whiteBtn.getStyleClass().add("piano-white-key");
            pianoPane.getChildren().add(whiteBtn);
            Tooltip tooltipMerge = new Tooltip();
            tooltipMerge.setText(String.format("琴键:%s", note));
            whiteBtn.setTooltip(tooltipMerge);
            whiteBtn.setOnAction(arg0 -> {
                new Audio(note).start();
            });
            if (key % 12 == 0)
            {
                group++;
            }

            key--;
            if (isBlackKey(key))
            {
                key--;
            }
            if (key < 21)
            {
                break;
            }

        }

        key = this.maxVal;
        group = 0;
        for (int i = 0; i < 50; i++)
        {

            if (i % 7 == index1 || i % 7 == index2)
            {
                continue;
            }
            key--;
            while (isWhiteKey(key))
            {
                key--;
            }
            if (key < 0)
            {
                break;
            }
            // C#
            if (key % 12 == 1)
            {
                offset1 = 1;
            }
            // D#
            if (key % 12 == 3)
            {
                offset1 = -1;
            }
            // F#
            if (key % 12 == 6)
            {
                offset1 = 2;
            }
            // G#
            if (key % 12 == 8)
            {
                offset1 = -1;
            }
            // A#
            if (key % 12 == 10)
            {
                offset1 = -4;
            }
            String note = NoteBO.generateNote(key);
            Button blackBtn = new Button();
            blackBtn.setId(Integer.toString(key));
            blackBtn.setPrefWidth(60);
            blackBtn.setMaxHeight(10);
            blackBtn.setMinHeight(10);
            blackBtn.setWrapText(true);
            blackBtn.setLayoutX(0);
            blackBtn.setLayoutY(i * 17 + 51 + offset1 + offset2 + group);
            blackBtn.getStyleClass().add("piano-black-key");
            Tooltip tooltipMerge = new Tooltip();
            tooltipMerge.setText(String.format("琴键:%s", blackBtn));
            blackBtn.setTooltip(tooltipMerge);
            pianoPane.getChildren().add(blackBtn);
            blackBtn.setOnAction(arg0 -> {
                new Audio(note).start();
            });
            if (key % 12 == 1)
            {
                group++;
            }
            if (key < 23)
            {
                break;
            }

        }

        return pianoPane;

    }


    /***
     * 判断是否是白键
     *
     * @param key 键值
     * @return 布尔值
     */
    private boolean isWhiteKey(int key)
    {
        return key % 12 == 0 || key % 12 == 2 || key % 12 == 4 || key % 12 == 5 || key % 12 == 7 || key % 12 == 9
                || key % 12 == 11;
    }


    /***
     * 判断是否是黑键
     *
     * @param key 键值
     * @return 布尔值
     */
    private boolean isBlackKey(int key)
    {
        return key % 12 == 1 || key % 12 == 3 || key % 12 == 6 || key % 12 == 8 || key % 12 == 10;
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
        if (maxVal <= 21)
        {
            maxVal = 108;
        }
        return 10 * (maxVal - value) + 10;

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
        list = NoteUtil.processMergeNoteBo(list);
        AnimationFx.build(list, "大鱼", curInfo).setIsDebug(true).setMaxVal(105).start();

    }


    @Override
    public void run()
    {
        launch();
    }
}

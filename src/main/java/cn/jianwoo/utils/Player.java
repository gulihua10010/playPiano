package cn.jianwoo.utils;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.jianwoo.play.Animation;
import cn.jianwoo.play.AudioPlay;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author GerogeLiu
 * @date 2022/11/29
 */
public class Player {

    private String notePath;
    private String accompanimentPath;

    public Player(String notePath, String accompanimentPath) {
        this.notePath = notePath;
        this.accompanimentPath = accompanimentPath;
    }

    /**
     * 播放
     * @throws IOException
     */
    public void play() throws IOException {
        InputStream in = ResourceUtil.getStream(this.notePath);

        ReadFile noteFile = new ReadFile(in);
        String note = noteFile.read();

        in = ResourceUtil.getStream(this.accompanimentPath);
        ReadFile accompanimentFile = new ReadFile(in);
        String accompaniment = accompanimentFile.read();


        if (null != in) {
            in.close();
        }

        if (!StringUtils.hasLength(note) && !StringUtils.hasLength(accompaniment)) {
            throw new RuntimeException("主乐谱note 和 伴奏accompaniment 均为空");
        }

        doPlay(note, accompaniment);
    }


    private void doPlay(String note, String accompaniment) {
        AudioPlay ap1 = new AudioPlay(225).loadNotes(note);
        AudioPlay ap2 = new AudioPlay(225).loadNotes(accompaniment);
        Animation an = new Animation(225).loadNotes(note);

        ap1.start();
        ap2.start();
        an.start();
    }
}

package cn.jianwoo.play;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.jianwoo.bo.NoteBO;
import javafx.scene.media.Media;

import java.io.File;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author gulihua
 * @Description
 * @date 2023-01-18 16:45
 */
public class MediaPlayer {

    private  javafx.scene.media.MediaPlayer mediaPlayer ;


    private static ExecutorService service = Executors.newCachedThreadPool();

    public MediaPlayer(String name) {
        String path = ResourceUtil.getResource("pianoKey").getPath() + File.separator;
        path = path + name + ".mp3";
        Media media = new Media(new File(path).toURI().toString());
        mediaPlayer = new javafx.scene.media.MediaPlayer(media);

    }
    public MediaPlayer setVolume(Double volume){
        mediaPlayer.setVolume(volume);
        return this;
    }

    public void play(){
        service.submit(() -> {
            try
            {

                mediaPlayer.setAutoPlay(true);

            }
            catch (Exception e)
            {


            }

        });
    }
}

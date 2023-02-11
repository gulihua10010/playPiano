package cn.jianwoo.test;

import cn.jianwoo.bo.CurInfoBO;
import cn.jianwoo.bo.NoteBO;
import cn.jianwoo.play.AnimationFx;
import cn.jianwoo.play.Audio;
import cn.jianwoo.play.MediaPlayer;
import cn.jianwoo.util.ReadXmlAsNoteUtil;
import javafx.scene.media.Media;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Port;
import javax.swing.*;
import java.io.File;
import java.util.List;

/**
 * @author gulihua
 * @Description
 * @date 2023-01-18 16:35
 */
public class TestAudio {
    public static void main(String[] args) throws Exception{
        CurInfoBO curInfo = new CurInfoBO();
        List<NoteBO> noteListMain = ReadXmlAsNoteUtil.getInstance()
                .readAsNote("/Users/gulihua/Downloads/枫-周杰伦-伴奏1.xml", NoteBO.Mode.MAIN);
        Runnable runnable = AnimationFx.build(noteListMain, "songName",curInfo).setMaxVal(105);
        new Thread(runnable).start();
////        String path = "/Users/gulihua/tmp/playPiano/src/main/resources/pianoKey/A#5.mp3";
////
////        //Instantiating Media class
////        Media media = new Media(new File(path).toURI().toString());
////
////        //Instantiating MediaPlayer class
////        MediaPlayer mediaPlayer = new MediaPlayer(media);
////
////        //by setting this property to true, the audio will be played
////        mediaPlayer.setAutoPlay(true);
//        long start = System.currentTimeMillis();
//
//        new MediaPlayer("C5").setVolume(1d).play();
//
//        long end = System.currentTimeMillis();
//        System.out.println(end - start);
//        start = System.currentTimeMillis();
//        new MediaPlayer("A5").setVolume(1d).play();
//        end = System.currentTimeMillis();
//        System.out.println(end - start);
//new Audio("C5",NoteBO.Mode.ACCOMPANIMENTS).start();
//setGain(0.1f);
        new MediaPlayer("C5").play();
        new MediaPlayer("E5").play();
        Thread.sleep(100);
        new MediaPlayer("A5").play();
        new MediaPlayer("B5").play();
    }
    public void setGain(float ctrl)
    {
        try {
            Mixer.Info[] infos = AudioSystem.getMixerInfo();
            for (Mixer.Info info: infos)
            {
                Mixer mixer = AudioSystem.getMixer(info);
                if (mixer.isLineSupported(Port.Info.SPEAKER))
                {
                    Port port = (Port)mixer.getLine(Port.Info.SPEAKER);
                    port.open();
                    if (port.isControlSupported(FloatControl.Type.VOLUME))
                    {
                        FloatControl volume =  (FloatControl)port.getControl(FloatControl.Type.VOLUME);
                        volume.setValue(ctrl);
                    }
                    port.close();
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,"Erro\n"+e);
        }
    }
    }

package cn.jianwoo.play;

import java.io.File;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.hutool.core.io.resource.ResourceUtil;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

public class Audio
{
    private static InputStream is;
    private Player player;

    private static ExecutorService service = Executors.newCachedThreadPool();

    public Audio(String name)
    {
        String path = ResourceUtil.getResource("pianoKey").getPath() + File.separator;
        path = path + name + ".mp3";
        is = ResourceUtil.getStream(path);
        try
        {
            player = new Player(is);
        }
        catch (JavaLayerException e)
        {
            e.printStackTrace();
        }
    }


    public void start()
    {
        service.submit(() -> {
            try
            {
                player.play();
            }
            catch (Exception e)
            {

            }

        });

    }
}

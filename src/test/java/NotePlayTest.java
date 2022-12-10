import cn.jianwoo.play.Animation;
import cn.jianwoo.play.AudioPlay;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.jianwoo.utils.ReadFile;
import org.springframework.util.StringUtils;

import java.io.*;

/**
 * @author GerogeLiu
 * @date 2022/11/27
 */
public class NotePlayTest {

    /**
     * 测试将resouces目录中的琴谱读取进来
     */
    public static void main(String[] arg) throws IOException {

        String notePath = "notes/起风了_180.notes";
        InputStream in = ResourceUtil.getStream(notePath);

        ReadFile noteFile = new ReadFile(in);
        String note = noteFile.read();

        String accompanimentPath = "notes/起风了_180.accompaniments";
        in = ResourceUtil.getStream(accompanimentPath);
        ReadFile accompanimentFile = new ReadFile(in);
        String accompaniment = accompanimentFile.read();

        if (null != in) {
            in.close();
        }

        if (!StringUtils.hasLength(note) && !StringUtils.hasLength(accompaniment)) {
            System.out.println("note or accompanies 为空");
            return;
        }

        AudioPlay ap1 = new AudioPlay(180).loadNotes(note);
        AudioPlay ap2 = new AudioPlay(180).loadNotes(accompaniment);
        Animation an = new Animation(180).loadNotes(note);

        ap1.start();
        ap2.start();
        an.start();


    }

}

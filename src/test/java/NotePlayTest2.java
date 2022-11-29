import cn.jianwoo.utils.Player;

import java.io.IOException;

/**
 * @author GerogeLiu
 * @date 2022/11/29
 */
public class NotePlayTest2 {

    public static void main(String[] args) throws IOException {
        String basePath = "notes/";
        Player player = new Player(basePath + "孤勇者.notes",  basePath + "孤勇者.accompaniments");
        player.play();
    }
}

import cn.jianwoo.play.Audio;
import cn.jianwoo.play.Note2Sound;

import java.util.Scanner;

/**
 * @author GerogeLiu
 * @date 2022/11/27
 */
public class SingleNotePlayTest {

    public static void main(String[] args) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);
        String inputStr = scanner.next();
        int times = 180;

        while(!inputStr.equals("q") && !inputStr.equals("Q")) {

            if (Note2Sound.map.containsKey(inputStr)) {
                new Audio(Note2Sound.map.get(inputStr)).start();
                Thread.sleep(times / 2);
            }

            if ("0".equals(inputStr)) {
                Thread.sleep(times / 2);
            }

            Thread.sleep(times / 2);


            // 获取新的输入
            inputStr = scanner.next();
        }

        System.out.println("正常退出程序...");
        System.exit(0);
    }
}

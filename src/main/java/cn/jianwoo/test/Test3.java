package cn.jianwoo.test;

import cn.hutool.core.util.RuntimeUtil;
import cn.jianwoo.play.Audio;

/**
 * @author gulihua
 * @Description
 * @date 2022-12-29 19:02
 */
public class Test3 {
    public static void main(String[] args) {
//        String jarPath ="/Users/gulihua/tmp/player/out/artifacts/player_jar/player.jar";
//        RuntimeUtil.execForStr("java -jar "+jarPath+" C5");
//        RuntimeUtil.execForStr("java -jar "+jarPath+" A6");
        new Audio("C5").start();
        new Audio("A5").start();

    }
}

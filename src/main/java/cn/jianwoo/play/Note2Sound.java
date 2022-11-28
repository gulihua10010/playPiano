package cn.jianwoo.play;


import cn.jianwoo.utils.NoteEum;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author GerogeLiu
 * @date 2022/11/27
 */
public class Note2Sound {

    public static Map<String, String> map = new ConcurrentHashMap<>(40);
    private static final String NOTE_BASE_PATH = "audio/";
    static {
        map.put(NoteEum.LL1.getNote(), getResource(NoteEum.LL1.getMp3()));
        map.put(NoteEum.LL2.getNote(), getResource(NoteEum.LL2.getMp3()));
        map.put(NoteEum.LL3.getNote(), getResource(NoteEum.LL3.getMp3()));
        map.put(NoteEum.LL4.getNote(), getResource(NoteEum.LL4.getMp3()));
        map.put(NoteEum.LL5.getNote(), getResource(NoteEum.LL5.getMp3()));
        map.put(NoteEum.LL6.getNote(), getResource(NoteEum.LL6.getMp3()));
        map.put(NoteEum.LL7.getNote(), getResource(NoteEum.LL7.getMp3()));

        map.put(NoteEum.L1.getNote(), getResource(NoteEum.L1.getMp3()));
        map.put(NoteEum.L2.getNote(), getResource(NoteEum.L2.getMp3()));
        map.put(NoteEum.L3.getNote(), getResource(NoteEum.L3.getMp3()));
        map.put(NoteEum.L4.getNote(), getResource(NoteEum.L4.getMp3()));
        map.put(NoteEum.L5.getNote(), getResource(NoteEum.L5.getMp3()));
        map.put(NoteEum.L6.getNote(), getResource(NoteEum.L6.getMp3()));
        map.put(NoteEum.L7.getNote(), getResource(NoteEum.L7.getMp3()));


        map.put(NoteEum.M1.getNote(),  getResource(NoteEum.M1.getMp3()));
        map.put(NoteEum.M2.getNote(),  getResource(NoteEum.M2.getMp3()));
        map.put(NoteEum.M3.getNote(),  getResource(NoteEum.M3.getMp3()));
        map.put(NoteEum.M4.getNote(),  getResource(NoteEum.M4.getMp3()));
        map.put(NoteEum.M5.getNote(),  getResource(NoteEum.M5.getMp3()));
        map.put(NoteEum.M6.getNote(),  getResource(NoteEum.M6.getMp3()));
        map.put(NoteEum.M7.getNote(),  getResource(NoteEum.M7.getMp3()));


        map.put(NoteEum.H1.getNote(),  getResource(NoteEum.H1.getMp3()));
        map.put(NoteEum.H2.getNote(),  getResource(NoteEum.H2.getMp3()));
        map.put(NoteEum.H3.getNote(),  getResource(NoteEum.H3.getMp3()));
        map.put(NoteEum.H4.getNote(),  getResource(NoteEum.H4.getMp3()));
        map.put(NoteEum.H5.getNote(),  getResource(NoteEum.H5.getMp3()));
        map.put(NoteEum.H6.getNote(),  getResource(NoteEum.H6.getMp3()));
        map.put(NoteEum.H7.getNote(),  getResource(NoteEum.H7.getMp3()));

        map.put(NoteEum.HH1.getNote(),  getResource(NoteEum.HH1.getMp3()));
        map.put(NoteEum.HH2.getNote(),  getResource(NoteEum.HH2.getMp3()));
        map.put(NoteEum.HH3.getNote(),  getResource(NoteEum.HH3.getMp3()));
        map.put(NoteEum.HH4.getNote(),  getResource(NoteEum.HH4.getMp3()));
        map.put(NoteEum.HH5.getNote(),  getResource(NoteEum.HH5.getMp3()));
        map.put(NoteEum.HH6.getNote(),  getResource(NoteEum.HH6.getMp3()));
        map.put(NoteEum.HH7.getNote(),  getResource(NoteEum.HH7.getMp3()));
    }

    /**
     * 获得完整的地址
     * @param sound    声音文件
     * @return
     */
    private static String getResource(String sound) {

        return NOTE_BASE_PATH + sound;
    }
}



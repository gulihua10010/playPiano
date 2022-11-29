package cn.jianwoo.play;

import cn.jianwoo.utils.NoteEum;

import java.util.HashMap;
import java.util.Map;

/**
 * @author GerogeLiu
 * @date 2022/11/27
 */
public class Note2Sound {

    public static Map<String, String> map = new HashMap<>(40);
    private static final String NOTE_BASE_PATH = "audio/";
    static {
        map.put(NoteEum.LL1.getNote(), getPath(NoteEum.LL1.getMp3()));
        map.put(NoteEum.LL2.getNote(), getPath(NoteEum.LL2.getMp3()));
        map.put(NoteEum.LL3.getNote(), getPath(NoteEum.LL3.getMp3()));
        map.put(NoteEum.LL4.getNote(), getPath(NoteEum.LL4.getMp3()));
        map.put(NoteEum.LL5.getNote(), getPath(NoteEum.LL5.getMp3()));
        map.put(NoteEum.LL6.getNote(), getPath(NoteEum.LL6.getMp3()));
        map.put(NoteEum.LL7.getNote(), getPath(NoteEum.LL7.getMp3()));

        map.put(NoteEum.L1.getNote(), getPath(NoteEum.L1.getMp3()));
        map.put(NoteEum.L2.getNote(), getPath(NoteEum.L2.getMp3()));
        map.put(NoteEum.L3.getNote(), getPath(NoteEum.L3.getMp3()));
        map.put(NoteEum.L4.getNote(), getPath(NoteEum.L4.getMp3()));
        map.put(NoteEum.L5.getNote(), getPath(NoteEum.L5.getMp3()));
        map.put(NoteEum.L6.getNote(), getPath(NoteEum.L6.getMp3()));
        map.put(NoteEum.L7.getNote(), getPath(NoteEum.L7.getMp3()));


        map.put(NoteEum.M1.getNote(),  getPath(NoteEum.M1.getMp3()));
        map.put(NoteEum.M2.getNote(),  getPath(NoteEum.M2.getMp3()));
        map.put(NoteEum.M3.getNote(),  getPath(NoteEum.M3.getMp3()));
        map.put(NoteEum.M4.getNote(),  getPath(NoteEum.M4.getMp3()));
        map.put(NoteEum.M5.getNote(),  getPath(NoteEum.M5.getMp3()));
        map.put(NoteEum.M6.getNote(),  getPath(NoteEum.M6.getMp3()));
        map.put(NoteEum.M7.getNote(),  getPath(NoteEum.M7.getMp3()));


        map.put(NoteEum.H1.getNote(),  getPath(NoteEum.H1.getMp3()));
        map.put(NoteEum.H2.getNote(),  getPath(NoteEum.H2.getMp3()));
        map.put(NoteEum.H3.getNote(),  getPath(NoteEum.H3.getMp3()));
        map.put(NoteEum.H4.getNote(),  getPath(NoteEum.H4.getMp3()));
        map.put(NoteEum.H5.getNote(),  getPath(NoteEum.H5.getMp3()));
        map.put(NoteEum.H6.getNote(),  getPath(NoteEum.H6.getMp3()));
        map.put(NoteEum.H7.getNote(),  getPath(NoteEum.H7.getMp3()));

        map.put(NoteEum.HH1.getNote(),  getPath(NoteEum.HH1.getMp3()));
        map.put(NoteEum.HH2.getNote(),  getPath(NoteEum.HH2.getMp3()));
        map.put(NoteEum.HH3.getNote(),  getPath(NoteEum.HH3.getMp3()));
        map.put(NoteEum.HH4.getNote(),  getPath(NoteEum.HH4.getMp3()));
        map.put(NoteEum.HH5.getNote(),  getPath(NoteEum.HH5.getMp3()));
        map.put(NoteEum.HH6.getNote(),  getPath(NoteEum.HH6.getMp3()));
        map.put(NoteEum.HH7.getNote(),  getPath(NoteEum.HH7.getMp3()));
    }

    /**
     * 获得完整的地址
     * 待完善
     * @param sound    声音文件
     * @return
     */
    private static String getPath(String sound) {
        // TODO: 进一步完善地址
        return NOTE_BASE_PATH + sound;
    }
}



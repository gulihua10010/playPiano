package cn.jianwoo.utils;

/**
 * 音调枚举
 */
public enum NoteEum {

    // 倍低音
    LL1("1--", "ll1.mp3"),
    LL2("2--","ll2.mp3"),
    LL3("3--", "ll3.mp3"),
    LL4("4--", "ll4.mp3"),
    LL5("5--", "ll5.mp3"),
    LL6("6--", "ll6.mp3"),
    LL7("7--", "ll7.mp3"),
    // 低音
    L1("1-", "l1.mp3"),
    L2("2-","l2.mp3"),
    L3("3-", "l3.mp3"),
    L4("4-", "l4.mp3"),
    L5("5-", "l5.mp3"),
    L6("6-", "l6.mp3"),
    L7("7-", "l7.mp3"),
    // 中音
    M1("1", "m1.mp3"),
    M2("2", "m2.mp3"),
    M3("3", "m3.mp3"),
    M4("4", "m4.mp3"),
    M5("5", "m5.mp3"),
    M6("6", "m6.mp3"),
    M7("7", "m7.mp3"),
    // 高音
    H1("1+", "h1.mp3"),
    H2("2+", "h2.mp3"),
    H3("3+", "h3.mp3"),
    H4("4+", "h4.mp3"),
    H5("5+", "h5.mp3"),
    H6("6+", "h6.mp3"),
    H7("7+", "h7.mp3"),
    // 倍高音
    HH1("1++", "hh1.mp3"),
    HH2("2++", "hh2.mp3"),
    HH3("3++", "hh3.mp3"),
    HH4("4++", "hh4.mp3"),
    HH5("5++", "hh5.mp3"),
    HH6("6++", "hh6.mp3"),
    HH7("7++", "hh7.mp3");


    private final String note;
    private final String mp3;

    NoteEum(String note, String mp3) {
        this.note = note;
        this.mp3 = mp3;
    }

    public String getNote() {
        return note;
    }

    public String getMp3() {
        return mp3;
    }
}

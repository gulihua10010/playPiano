module cn.jianwoo {
    requires javafx.controls;
    requires javafx.fxml;
        requires javafx.web;
            
        requires org.controlsfx.controls;
            requires com.dlsc.formsfx;
                requires org.kordamp.ikonli.javafx;
            requires org.kordamp.bootstrapfx.core;
            requires eu.hansolo.tilesfx;
    requires hutool.all;
    requires filters;
    requires java.desktop;
    requires jdom;
    requires org.apache.commons.lang3;
    requires fastjson;
    requires net.sourceforge.lame;
    requires javafx.media;
    requires java.sql;
    requires slf4j.api;
    requires jfugue;
    requires jlayer.extend;

    opens cn.jianwoo.test to javafx.fxml;
    opens cn.jianwoo.play to javafx.fxml;
    opens cn.jianwoo.util to fastjson;
    opens cn.jianwoo.bo to hutool.all;
    exports cn.jianwoo.test;
    exports cn.jianwoo.util;
    exports cn.jianwoo.play;
}
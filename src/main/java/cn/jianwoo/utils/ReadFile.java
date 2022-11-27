package cn.jianwoo.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author GerogeLiu
 * @date 2022/11/27
 */
public class ReadFile {

    private final InputStream in;

    public ReadFile(InputStream in) {
        this.in = in;
    }

    /**
     * 读取琴谱文件
     * 文件的每行结尾添加空格和换行符
     * @return 返回整个文件内容
     */
    public String read() throws IOException {

        String content = "";
        StringBuilder builder = new StringBuilder();

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(this.in));
        while ((content = bufferedReader.readLine()) != null) {
            builder.append(content).append(" ").append("\n");
        }

        bufferedReader.close();
        return builder.toString();
    }
}

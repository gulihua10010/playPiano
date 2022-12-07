package cn.jianwoo.util;

import java.util.List;

import cn.jianwoo.bo.NoteBO;
import com.alibaba.fastjson.JSON;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;

/**
 * @author gulihua
 * @Description
 * @date 2022-11-30 23:08
 */
public class GenerateJsonUtil
{
    private static GenerateJsonUtil instance;

    private GenerateJsonUtil()
    {
    }


    public static GenerateJsonUtil getInstance()
    {
        synchronized (GenerateJsonUtil.class)
        {
            if (instance == null)
            {
                instance = new GenerateJsonUtil();
            }
        }

        return instance;
    }


    /***
     * 生成 JSON 文件
     * 
     * @param noteList 音符集合
     * @param targetFile 输出文件全路径
     * @return
     */
    public void generate2JsonFile(List<NoteBO> noteList, String targetFile)
    {
        if (CollUtil.isEmpty(noteList))
        {
            System.err.println("音符集合为空!");
        }
        FileUtil.writeUtf8String(JSONUtil.formatJsonStr(JSON.toJSONString(noteList)), targetFile);
    }
}

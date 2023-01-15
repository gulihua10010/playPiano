package cn.jianwoo.util;

/**
 * @author gulihua
 * @Description
 * @date 2023-01-03 00:16
 */
@FunctionalInterface
public interface Callback<T>
{
    /**
     * 方法回调
     * 
     * @param param 参数
     * @date 00:18 2023/1/3
     * @author gulihua
     **/
    void call(T param);
}

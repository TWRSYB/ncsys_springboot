package com.dc.ncsys_springboot.vo;

import com.dc.ncsys_springboot.util.JsonUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@Accessors(chain = true)
public class ResVo<T> {
    private Integer code;
    private String message;
    private T data;

    public static <T> ResVo<T> success(String message, T data){
        return new ResVo<>(200, message, data);
    }
    public static <T> ResVo<T> success(String message){
        return new ResVo<>(200, message, null);
    }
    public static <T> ResVo<T> success(){
        return new ResVo<>(200, "操作成功", null);
    }
    public static <T> ResVo<T> fail(Integer code, String message) {
        return new ResVo<>(code, message, null);
    }
    public static <T> ResVo<T> fail(String message) {
        return new ResVo<>(500, message, null);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "=" + JsonUtils.toJson(this);
    }

}

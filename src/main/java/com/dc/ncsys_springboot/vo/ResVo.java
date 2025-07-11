package com.dc.ncsys_springboot.vo;

import com.dc.ncsys_springboot.util.JsonUtils;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResVo {
    private Integer code;
    private String message;
    private Object data;

    public static ResVo success(String message, Object data){
        return new ResVo(200, message, data);
    }
    public static ResVo success(String message){
        return new ResVo(200, message, null);
    }
    public static ResVo success(){
        return new ResVo(200, "操作成功", null);
    }
    public static ResVo fail(Integer code, String message) {
        return new ResVo(code, message, null);
    }
    public static ResVo fail(String message) {
        return new ResVo(500, message, null);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "=" + JsonUtils.toJson(this);
    }

}

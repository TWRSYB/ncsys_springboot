package com.dc.ncsys_springboot.vo;

import com.dc.ncsys_springboot.util.JsonUtils;
import com.github.pagehelper.Page;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PageResVo<T>{

    private Integer code;
    private String message;
    private List<T> list;
    private Long total;

//    public static <T> PageResVo<T> success(String message, List<T> list, Integer total){
//        return new PageResVo<>(200, message, list, total);
//    }
//
//    public static <T> PageResVo<T> success(String message, List<T> list){
//        return new PageResVo<>(200, message, list, list.size());
//    }

    public static <T> PageResVo<T> success(List<T> list, Long total){
        return new PageResVo<>(200, "查询成功", list, total);
    }

    public static <T> PageResVo<T> success(List<T> list){
        return new PageResVo<>(200, "查询成功", list, (long) list.size());
    }

    public static <T> PageResVo<T> success(Page<T> page){
        return new PageResVo<>(200, "查询成功", page.getResult(), page.getTotal());
    }

    public static <T> PageResVo<T> fail(){
        return new PageResVo<>(500, "查询失败", null, 0L);
    }

    public static <T> PageResVo<T> fail(String message){
        return new PageResVo<>(500, message, null, 0L);
    }

    public static <T> PageResVo<T> fail(Integer code, String message) {
        return new PageResVo<>(code, message, null, 0L);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "=" + JsonUtils.toJson(this);
    }

}

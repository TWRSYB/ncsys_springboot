package com.dc.ncsys_springboot.vo;

import com.dc.ncsys_springboot.util.JsonUtils;
import lombok.Data;

import java.util.Map;

@Data
public class PageQueryVo<T> {
    private Integer pageNum;
    private Integer pageSize;
    private String sortField;
    private String sortOrder;
    private T query;
    private Map<String, String> params;

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "=" + JsonUtils.toJson(this);
    }

}

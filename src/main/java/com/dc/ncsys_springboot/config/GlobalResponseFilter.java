package com.dc.ncsys_springboot.config;

import com.dc.ncsys_springboot.constants.ComConst;
import com.dc.ncsys_springboot.daoVo.PersonDo;
import com.dc.ncsys_springboot.daoVo.UserDo;
import com.dc.ncsys_springboot.util.SessionUtils;
import com.dc.ncsys_springboot.vo.ResVo;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalResponseFilter implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;  // 对所有响应生效
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {

        recursionFilter(body);
        return body;
    }

    /**
     * 递归过滤
     *
     * @param body 响应体
     */
    private void recursionFilter(Object body) {
        // 忽略空值
        if (ObjectUtils.isEmpty(body)) {
            return;
        }

        // 处理 ResVo 包装类
        if (body instanceof ResVo resVo) {
            recursionFilter(resVo.getData());
        }

        // 处理 List 类型
        if (body instanceof List<?> list) {
            // 过滤列表中的 User 对象
            for (Object e : list) {
                recursionFilter(e);
            }
        }

        // 处理 Map 类型
        if (body instanceof Map) {
            // 过滤 Map 中的 User 对象
            for (Object value : ((Map<?,?>) body).values()) {
                recursionFilter(value);
            }
        }

        // 过滤 User 对象
        filterUser(body);
        // 过滤 Person 对象
        filterPerson(body);

    }



    private void filterUser(Object body) {
        if (body instanceof UserDo userDo) {
            UserDo sessionUserDo = SessionUtils.getSessionUser();
            if (sessionUserDo == null) {
                userDo = null;
                return;
            }
            if (ComConst.ROLE_SYS_ADMIN.equals(sessionUserDo.getRoleCode())) {
                userDo.setLoginPassword(null);
                return;
            }
            if (ComConst.ROLE_MANAGER.equals(sessionUserDo.getRoleCode())) {
                userDo.setLoginPassword(null);
                userDo.setPhoneNum(null);
                return;
            }
            userDo = null;
        }
    }

    private void filterPerson(Object body) {
        if (body instanceof PersonDo personDo) {
            UserDo sessionUserDo = SessionUtils.getSessionUser();
            if (sessionUserDo == null) {
                personDo = null;
                return;
            }
            if (ComConst.ROLE_OPERATOR.equals(sessionUserDo.getRoleCode())) {
                personDo.setPersonId(null);
                return;
            }
        }
    }

}
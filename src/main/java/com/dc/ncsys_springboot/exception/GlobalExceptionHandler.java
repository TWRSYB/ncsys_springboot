package com.dc.ncsys_springboot.exception;

import com.dc.ncsys_springboot.vo.ResVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ResVo> handleBusinessException(BusinessException ex) {
        // 记录内部日志（包含敏感信息）
        log.error("业务异常 - 内部信息: {}", ex.getInternalMessage(), ex);
        return new ResponseEntity<>(ResVo.fail(ex.getCustomerMessage()), HttpStatus.BAD_REQUEST);
    }

}
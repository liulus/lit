package com.github.lit.support.web;

import com.github.lit.support.exception.BizException;
import com.github.lit.support.util.WebUtils;
import com.github.lit.support.web.annotation.ViewName;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;


/**
 * User : liulu
 * Date : 2017/6/14 19:25
 * version $Id: ExceptionAdvice.java, v 0.1 Exp $
 */

@Slf4j
@ControllerAdvice
public class ExceptionAdvice {

    @Value("${unchecked.error.message:系统错误}")
    private String errorMsg;


    @ExceptionHandler(Exception.class)
    public String exception(HandlerMethod handlerMethod, Model model, Exception ex) {
        model.addAttribute("success", false);

        BizException bizException = findBizException(ex);
        if (bizException != null) {
            if (StringUtils.hasText(bizException.getCode())) {
                model.addAttribute("code", bizException.getCode());
            }
            model.addAttribute("message", bizException.getMessage());

            StackTraceElement traceElement = ex.getStackTrace()[0];
            log.warn("\n biz exception --> class: [{}], method: [{}], line: [{}], code: [{}],  message: [{}]",
                    traceElement.getClassName(), traceElement.getMethodName(), traceElement.getLineNumber(),
                    bizException.getCode(), bizException.getMessage());
            // 处理自定义视图名称
            ViewName viewName = handlerMethod.getMethodAnnotation(ViewName.class);
            if (viewName != null && !viewName.spel()) {
                return viewName.value();
            }
        } else {
            model.addAttribute("code", "9999");
            model.addAttribute("message", errorMsg);
            model.addAttribute("error", ex.getMessage());
            log.error("unchecked exception", ex);
        }

        // json 请求直接返回空
        HttpServletRequest request = WebUtils.getRequest();
        boolean isJsonContentType = Optional.ofNullable(request.getContentType())
                .map(contentType -> contentType.contains(MediaType.APPLICATION_JSON_VALUE))
                .orElse(false);
        if (request.getRequestURI().endsWith(".json") || isJsonContentType) {
            return "";
        }
        return "error/error";
    }

    private BizException findBizException(Throwable ex) {
        while (ex != null) {
            if (ex instanceof BizException) {
                return (BizException) ex;
            }
            ex = ex.getCause();
        }
        return null;
    }

}

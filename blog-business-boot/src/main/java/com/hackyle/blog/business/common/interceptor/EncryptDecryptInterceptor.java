package com.hackyle.blog.business.common.interceptor;

import com.hackyle.blog.business.util.IDUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 针对GET请求中的queryString，解密后把数据放在HttpServletRequest域中
 */
@Component
public class EncryptDecryptInterceptor implements HandlerInterceptor {
    //private static final Logger LOGGER = LoggerFactory.getLogger(EncryptDecryptInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //解密GET请求中的id
        String getId = request.getParameter("id");
        if(StringUtils.isNotBlank(getId)) {
            long deGetId = IDUtils.decryptByAES(getId);
            request.setAttribute("id", deGetId);
        }

        String ids = request.getParameter("ids");
        if(StringUtils.isNotBlank(ids)) {
            String[] idArr = ids.split(",");
            StringBuilder resultIds = new StringBuilder();
            for (String id : idArr) {
                long idTemp = IDUtils.decryptByAES(id);
                resultIds.append(idTemp);
            }
            request.setAttribute("ids", resultIds.toString());
        }

        return true; //放行
    }

}

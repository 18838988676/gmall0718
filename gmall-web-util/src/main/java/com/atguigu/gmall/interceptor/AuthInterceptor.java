package com.atguigu.gmall.interceptor;

import com.atguigu.gmall.annotation.LoginRequire;
import com.atguigu.gmall.util.CookieUtil;
import com.atguigu.gmall.util.HttpClientUtil;
import com.atguigu.gmall.util.JwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

//  由于是为gmall-cart-web做拦截服务的，  所以必须让她能扫描到此类   com.atguigu.gmall.interceptor;  因此：gmall-cart-web的启动类的包：com.atguigu.gmall;  debug启动这个类后 此类中的所有方法都被拦截。
//因此 需要进一步完善功能，加注解  用于具体说明哪一个方法 需不需要被拦截

@Component
public class AuthInterceptor extends HandlerInterceptorAdapter {

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 判断当前访问的方法是否需要认证拦截
        HandlerMethod method = (HandlerMethod)handler;
        LoginRequire methodAnnotation = method.getMethodAnnotation(LoginRequire.class);

        if(methodAnnotation==null){
            return true;
        }

        String oldToken = CookieUtil.getCookieValue(request, "oldToken", true);
        String newToken = request.getParameter("newToken");

        String token = "";


//        oldToken不空，新token空，用户登陆过
//        oldToken空，新token不空，用户第一次登陆
//        oldToken空，新token空，用户从没登陆
//        oldToken不空，新token不空，用户登录过期

        if(StringUtils.isNotBlank(oldToken)&&StringUtils.isBlank(newToken)){
            //登陆过
            token = oldToken;
        }

        if(StringUtils.isBlank(oldToken)&&StringUtils.isNotBlank(newToken)){
            //第一次登陆
            token = newToken;
        }

        if(StringUtils.isNotBlank(oldToken)&&StringUtils.isNotBlank(newToken)){
            //登陆过期
            token = newToken;
        }


        if(methodAnnotation.ifNeedSuccess()&& StringUtils.isBlank(token))
        {
            StringBuffer requestURL = request.getRequestURL();
            response.sendRedirect("http://localhost:8085/index?returnURL="+requestURL);
            return false;
        }

        String success = "";
        if(StringUtils.isNotBlank(token)){
            // 远程访问passport，验证token
            success = HttpClientUtil.doGet("http://localhost:8085/verify?token="+token+"&salt="+getMyIp(request));

        }

        if(!success.equals("success")&&methodAnnotation.ifNeedSuccess()){
            response.sendRedirect("http://localhost:8085/index");
            return false;
        }

        if(!success.equals("success")&&!methodAnnotation.ifNeedSuccess())
        {
            // 购物车方法

            return true;
        }



        if(success.equals("success")){
            // cookie验证通过，重新刷新cookie的过期时间
            CookieUtil.setCookie(request,response,"oldToken",token,60*60*2,true);

            // 将用户信息放入应用请求
            Map userMap = JwtUtil.decode("atguigu0328", token, getMyIp(request));
            request.setAttribute("userId",userMap.get("userId"));
            request.setAttribute("nickName",userMap.get("nickName"));
        }




        return true;
    }

    private String getMyIp(HttpServletRequest request) {
        String ip = "";
        ip = request.getHeader("x-forwarded-for");
        if(StringUtils.isBlank(ip)){
            ip = request.getRemoteAddr();//直接获取ip
        }
        if(StringUtils.isBlank(ip)){
            ip = "127.0.0.1";//设置一个虚拟ip
        }
        return ip;
    }

}

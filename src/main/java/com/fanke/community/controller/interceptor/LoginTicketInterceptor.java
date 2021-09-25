package com.fanke.community.controller.interceptor;

import com.fanke.community.entity.LoginTicket;
import com.fanke.community.entity.User;
import com.fanke.community.service.UserService;
import com.fanke.community.util.CookieUtil;
import com.fanke.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * @ClassName LoginTicketInterceptor
 * @Author Fanke
 * @Created 2021/9/25 0:24
 */

@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

    private final UserService userService;
    private final HostHolder hostHolder;

    @Autowired
    public LoginTicketInterceptor(UserService userService, HostHolder hostHolder) {
        this.userService = userService;
        this.hostHolder = hostHolder;
    }

    /**
     * 在 Controller之前：验证当前是否有登录状态，如果有，交由 hostHolder 来进行状态保持
     * @param request 主要是用来获取 request 中的 cookie
     * @param response 可以对响应做一些修改，这里没用到
     * @param handler 当前被拦截的请求，这里没用到
     * @return 是否拦截当前请求
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从cookie中获取凭证
        String ticket = CookieUtil.getValue(request, "ticket");

        if (ticket != null) {
            // 查询凭证
            LoginTicket loginTicket = userService.findLoginTicket(ticket);
            // 检查凭证是否有效
            if (loginTicket != null && loginTicket.getStatus() == 0 && loginTicket.getExpired().after(new Date())) {
                // 根据凭证查询用户
                User user = userService.findUserById(loginTicket.getUserId());
                // 在本次请求中持有用户
                hostHolder.setUser(user);
            }
        }
        return true;
    }

    /**
     * 在 Controller之后：通过 hostHolder 设置当前用户的已登录状态
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if (user != null && modelAndView != null) {
            modelAndView.addObject("loginUser", user);
        }
    }

    /**
     * 模板处理之后：表示整个请求已经完结，清除当前线程的登录用户
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
    }
}

package com.fanke.community.service;

import com.fanke.community.dao.LoginTicketMapper;
import com.fanke.community.dao.UserMapper;
import com.fanke.community.entity.LoginTicket;
import com.fanke.community.entity.User;
import com.fanke.community.util.CommunityConstant;
import com.fanke.community.util.CommunityUtil;
import com.fanke.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import sun.plugin.com.event.COMEventListener;

import javax.activation.MailcapCommandMap;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @ClassName UserService
 * @Author Fanke
 * @Created 2021/9/21 9:42
 */

// 嘿嘿
@Service
public class UserService implements CommunityConstant {

    private final UserMapper userMapper;
    private final MailClient mailClient;
    private final TemplateEngine templateEngine;
    private final LoginTicketMapper loginTicketMapper;

    @Value("${community.path.domain}")
    private String domain;

    @Value("/community")
    private String contextPath;

    @Autowired(required = false)
    public UserService(UserMapper userMapper, MailClient mailClient, TemplateEngine templateEngine,
                       LoginTicketMapper loginTicketMapper) {
        this.userMapper = userMapper;
        this.mailClient = mailClient;
        this.templateEngine = templateEngine;
        this.loginTicketMapper = loginTicketMapper;
    }

    /**
     * 通过id查找用户
     * @param id 指定id
     * @return 用户
     */
    public User findUserById(int id) {
        return userMapper.selectById(id);
    }


    /**
     * 注册业务
     * @param user 传入的新注册用户对象
     * @return 返回消息承载体
     */
    public Map<String, Object> register(User user) {
        Map<String, Object> map = new HashMap<>();

        // 空值处理
        if (user == null) {
            throw new IllegalArgumentException("参数不能为空!");
        }
        if (StringUtils.isBlank(user.getUsername())) {
            map.put("usernameMsg", "账号不能为空!");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg", "密码不能为空!");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())) {
            map.put("emailMsg", "邮箱不能为空!");
            return map;
        }

        // 验证账号是否存在
        User u = userMapper.selectByName(user.getUsername());
        if (u != null) {
            map.put("usernameMsg", "该账号已存在!");
            return map;
        }

        // 验证邮箱是否已被注册
        u = userMapper.selectByEmail(user.getEmail());
        if (u != null) {
            map.put("emailMsg", "该邮箱已被注册!");
            return map;
        }

        // 注册用户，添加到数据库
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        // 激活邮件
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        // http://localhost:8080/community/activation/101/code
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(), "激活账号", content);

        return map;
    }


    /**
     * 激活指定用户
     * @param userId 用户id
     * @param code 用户的激活码
     * @return 激活结果信息码
     */
    public int activation(int userId, String code) {
        User user = userMapper.selectById(userId);
        if (user.getStatus() == 1) {
            return ACTIVATION_REPEAT;
        } else if (user.getActivationCode().equals(code)) {
            userMapper.updateStatus(userId, 1);
            return ACTIVATION_SUCCESS;
        } else {
            return ACTIVATION_FAILURE;
        }
    }


    /**
     * 登录业务
     * @param username 用户名
     * @param password 密码
     * @param expiredSeconds 期望多久后登录状态过期
     * @return 返回消息承载体
     */
    public Map<String, Object> login(String username, String password, int expiredSeconds) {
        Map<String, Object> map = new HashMap<>();

        // 空值处理
        if (StringUtils.isBlank(username)) {
            map.put("usernameMsg", "账号不能为空!");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("passwordMsg", "密码不能为空!");
            return map;
        }

        // 验证账号
        User user = userMapper.selectByName(username);
        if (user == null) {
            map.put("usernameMsg", "该账号不存在!");
            return map;
        }

        // 验证状态
        if (user.getStatus() == 0) {
            map.put("usernameMsg", "该账号未激活!");
            return map;
        }

        // 验证密码
        password = CommunityUtil.md5(password + user.getSalt());
        if (!user.getPassword().equals(password)) {
            map.put("passwordMsg", "密码不正确!");
            return map;
        }

        // 验证通过，生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));
        loginTicketMapper.insertLoginTicket(loginTicket);

        // 只给登录凭证（一个String）即可
        map.put("ticket", loginTicket.getTicket());
        return map;
    }


    /**
     * 退出登录
     * @param ticket 当前的登录凭证号
     */
    public void logout(String ticket) {
        loginTicketMapper.updateStatus(ticket, 1);
    }

    /**
     * 查找当前登录凭证
     * @param ticket 当前的登录凭证号
     * @return 当前登录凭证
     */
    public LoginTicket findLoginTicket(String ticket) {
        return loginTicketMapper.selectByTicket(ticket);
    }


    /**
     * 更新用户头像
     * @param userId 用户id
     * @param headerUrl 新头像的url
     * @return
     */
    public int updateHeader(int userId, String headerUrl) {
        return userMapper.updateHeader(userId, headerUrl);
    }

    public Map<String, Object> updatePassword(User user, String oldPassword, String newPassword) {
        Map<String, Object> map = new HashMap<>();

        // 验证空值
        if (StringUtils.isBlank(oldPassword)) {
            map.put("pwdMsg", "原密码不能为空!");
            return map;
        }
        if (StringUtils.isBlank(newPassword)) {
            map.put("newPwdMsg", "新密码不能为空!");
            return map;
        }

        // 验证输入的旧密码是否正确
        String verify = CommunityUtil.md5(oldPassword + user.getSalt());
        if (!user.getPassword().equals(verify)) {
            map.put("pwdMsg", "旧密码不正确！");
            return map;
        }
        // 验证新密码与旧密码是否相同
        String newSalted = CommunityUtil.md5(newPassword + user.getSalt());
        if (user.getPassword().equals(newSalted)) {
            map.put("newPwdMsg", "旧密码不能与新密码相同！");
            return map;
        }

        // 修改密码
        userMapper.updatePassword(user.getId(), newSalted);

        return map;
    }

}

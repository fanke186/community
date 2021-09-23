package com.fanke.community;

import com.fanke.community.util.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * @ClassName MailTests
 * @Author Fanke
 * @Created 2021/9/23 22:32
 */

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MailTests {

    private MailClient mailClient;

    private TemplateEngine templateEngine;

    @Autowired
    public MailTests (MailClient mailClient, TemplateEngine templateEngine) {
        this.mailClient = mailClient;
        this.templateEngine = templateEngine;
    }

    @Test
    public void testTextMail() {
        mailClient.sendMail("fanke186@163.com", "TEST MAIL", "Welcome...");
    }

    @Test
    public void testHtmlMail() {
        Context context = new Context();
        context.setVariable("username", "sunday");

        String content = templateEngine.process("/mail/demo", context);
        System.out.println(content);

        mailClient.sendMail("lihonghe@nowcoder.com", "HTML", content);
    }
}

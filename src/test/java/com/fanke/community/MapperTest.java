package com.fanke.community;

import com.fanke.community.dao.DiscussPostMapper;
import com.fanke.community.dao.LoginTicketMapper;
import com.fanke.community.dao.UserMapper;
import com.fanke.community.entity.DiscussPost;
import com.fanke.community.entity.LoginTicket;
import com.fanke.community.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;
import java.util.List;

/**
 * @ClassName MapperTest
 * @Author Fanke
 * @Created 2021/9/20 22:49
 */


// I said
// ok
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTest {

    private final UserMapper userMapper;
    private final DiscussPostMapper discussPostMapper;
    private final LoginTicketMapper loginTicketMapper;

    // 构造器注入
    @Autowired(required = false)
    public MapperTest(UserMapper userMapper,
                      DiscussPostMapper discussPostMapper,
                      LoginTicketMapper loginTicketMapper) {
        this.userMapper = userMapper;
        this.discussPostMapper = discussPostMapper;
        this.loginTicketMapper = loginTicketMapper;
    }

    @Test
    public void testUserSelect() {
        User user = userMapper.selectById(101);
        System.out.println(user);

        user = userMapper.selectByEmail("nowcoder101@sina.com");
        System.out.println(user);

        user = userMapper.selectByName("liubei");
        System.out.println(user);
    }

    @Test
    public void testInsertUser() {
        User user = new User();
        user.setUsername("test");
        user.setPassword("123456");
        user.setSalt("abcd");
        user.setEmail("test@qq.com");
        user.setHeaderUrl("http://www.nowcoder.com/101.png");
        user.setCreateTime(new Date());
        int rows = userMapper.insertUser(user);
        System.out.println(rows);
        System.out.println(user.getId());
    }

    @Test
    public void testUpdateUser() {
        int rows = userMapper.updateStatus(150, 1);
        System.out.println(rows);

        rows = userMapper.updateHeader(150, "http://www.nowcoder.com/102.png");
        System.out.println(rows);

        rows = userMapper.updatePassword(150, "123");
        System.out.println(rows);


    }

    @Test
    public void testSelectPosts() {
        List<DiscussPost> list = discussPostMapper.selectDiscussPosts(149, 0, 10);
        for(DiscussPost post : list) {
            System.out.println(post);
        }

        int rows = discussPostMapper.selectDiscussPostRows(149);
        System.out.println(rows);
    }

    @Test
    public void testInsertLoginTicket() {
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setTicket("abc");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000 * 60 * 10));

        loginTicketMapper.insertLoginTicket(loginTicket);
    }

    @Test
    public void testSelectLoginTicket() {
        LoginTicket loginTicket = loginTicketMapper.selectByTicket("abc");
        System.out.println(loginTicket);

        loginTicketMapper.updateStatus("abc", 1);
        loginTicket = loginTicketMapper.selectByTicket("abc");
        System.out.println(loginTicket);
    }


}

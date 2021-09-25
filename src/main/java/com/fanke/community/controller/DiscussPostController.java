package com.fanke.community.controller;

import com.fanke.community.entity.DiscussPost;
import com.fanke.community.entity.Page;
import com.fanke.community.entity.User;
import com.fanke.community.service.DiscussPostService;
import com.fanke.community.service.UserService;
import com.fanke.community.util.CommunityConstant;
import com.fanke.community.util.CommunityUtil;
import com.fanke.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {

    private final DiscussPostService discussPostService;
    private final HostHolder hostHolder;
    private final UserService userService;

    @Autowired
    public DiscussPostController(DiscussPostService discussPostService, HostHolder hostHolder,
                                 UserService userService) {
        this.discussPostService = discussPostService;
        this.hostHolder = hostHolder;
        this.userService = userService;
    }

    @RequestMapping(path = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title, String content) {
        User user = hostHolder.getUser();
        if (user == null) {
            return CommunityUtil.getJSONString(403, "你还没有登录哦!");
        }

        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        discussPostService.addDiscussPost(post);

        // 报错的情况,将来统一处理.
        return CommunityUtil.getJSONString(0, "发布成功!");
    }

    @RequestMapping(path = "/detail/{discussPostId}", method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model) {
        // 查询帖子
        DiscussPost post = discussPostService.findDiscussPostById(discussPostId);
        model.addAttribute("post", post);
        // 查询作者
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user", user);
        // todo...评论

        return "site/discuss-detail";
    }
}

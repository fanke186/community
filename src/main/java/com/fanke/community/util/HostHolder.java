package com.fanke.community.util;

import com.fanke.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * 持有用户信息,用于代替session对象.
 * 实际上就是在每个线程自己的 map 上记录user，由此来实现线程间的隔离
 */
@Component
public class HostHolder {

    // 可以查看 ThreadLocal 的源码，主要是 get 和 set 方法
    private ThreadLocal<User> users = new ThreadLocal<>();

    public void setUser(User user) {
        users.set(user);
    }

    public User getUser() {
        return users.get();
    }

    public void clear() {
        users.remove();
    }

}

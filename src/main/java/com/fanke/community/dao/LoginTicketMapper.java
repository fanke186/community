package com.fanke.community.dao;


import com.fanke.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

@Mapper
public interface LoginTicketMapper {
    // 一般通过一个xml文件来写sql语句实现
    // 但是也可以通过注解的方式来声明对应的 sql实现
    // 缺点是，如果 sql 比较复杂，看起来可能比较乱，比如下面的 updateStatus

    // 这些个注解可以分为多个 字符串(后面多加个空格) 来写，最后会被拼成一个sql语句去执行
    // 声明这句 sql 相应的一些选项，使用 @Option 注解
    @Insert({
            "insert into login_ticket(user_id,ticket,status,expired) ",
            "values(#{userId},#{ticket},#{status},#{expired})"
    })
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertLoginTicket(LoginTicket loginTicket);

    @Select({
            "select id,user_id,ticket,status,expired ",
            "from login_ticket where ticket=#{ticket}"
    })
    LoginTicket selectByTicket(String ticket);

    // 动态语句sql，这里只是演示一下，update其实不需要动态sql
    @Update({
            "<script>",
            "update login_ticket set status=#{status} where ticket=#{ticket} ",
            "<if test=\"ticket!=null\"> ",
            "and 1=1 ",
            "</if>",
            "</script>"
    })
    int updateStatus(String ticket, int status);

}

package com.zhangyangyang.proxy.data;

import com.alibaba.fastjson.JSON;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangyangyang on 2018/4/3.
 * 目前加载配置文件user.json 来读取用户数据
 */
public class UserDB {

    private static Map<String, UserBean> users = new HashMap<>();

    public static void init() throws IOException {
        InputStream stream = UserDB.class.getResourceAsStream("/user.json");
        if (null == stream) {
            throw new FileNotFoundException("user.json not found");
        }
        byte[] bytes = new byte[2048];
        int n;
        StringBuilder sb = new StringBuilder();
        while ((n = stream.read(bytes)) != -1) {
            sb.append(new String(bytes, 0, n));
        }
        List<UserBean> userArray = JSON.parseArray(sb.toString(), UserBean.class);
        userArray.forEach(u -> users.put(u.getName(), u));
    }

    public static UserBean getUserByName(String name) {
        return users.get(name);
    }

}

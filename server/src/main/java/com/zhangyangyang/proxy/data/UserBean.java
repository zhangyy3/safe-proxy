package com.zhangyangyang.proxy.data;

/**
 * Created by zhangyangyang on 2018/4/3.
 * 用户信息
 */
public class UserBean {

    private String name;
    private char[] password;
    private long expire;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public char[] getPassword() {
        return password;
    }

    public void setPassword(char[] password) {
        this.password = password;
    }

    public long getExpire() {
        return expire;
    }

    public void setExpire(long expire) {
        this.expire = expire;
    }

    public boolean validPassword(String pwd) {
        if (pwd.length() != password.length) {
            return false;
        }
        for (int i = 0; i < password.length; i++) {
            if (password[i] != pwd.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    public boolean validExpire() {
        return System.currentTimeMillis() < expire;
    }

}

package com.singpeng;

import com.singpeng.utils.MailUtils;

public class Main {
    public static void main(String[] args) {
        String msg = "hello World!<p></p>";

        MailUtils.sendMail("2723387589@qq.com",msg+"123456","测试邮件");
    }
}
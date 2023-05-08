package com.singpeng.service;

import com.singpeng.pojo.Member;

public interface MemberService {
    void add(Member member);

    Member findByTelephone(String teelphone);
}

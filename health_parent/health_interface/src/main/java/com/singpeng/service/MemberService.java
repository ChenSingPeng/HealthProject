package com.singpeng.service;

import com.singpeng.pojo.Member;

import java.util.List;

public interface MemberService {
    void add(Member member);

    Member findByTelephone(String teelphone);

    List<Integer> findMemberCountByMonth(List<String> month);
}

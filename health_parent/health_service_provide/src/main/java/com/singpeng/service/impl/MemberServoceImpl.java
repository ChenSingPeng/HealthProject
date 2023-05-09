package com.singpeng.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.singpeng.dao.MemberDao;
import com.singpeng.pojo.Member;
import com.singpeng.service.MemberService;
import com.singpeng.utils.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service(interfaceClass = MemberService.class)
@Transactional
public class MemberServoceImpl implements MemberService {
    @Autowired
    private MemberDao memberDao;

    @Override
    public void add(Member member) {
        if (member.getPassword() !=null){
            member.setPassword(MD5Utils.md5(member.getPassword()));
        }
        memberDao.add(member);
    }

    @Override
    public Member findByTelephone(String teelphone) {
        return memberDao.findByTelephone(teelphone);
    }

    @Override
    public List<Integer> findMemberCountByMonth(List<String> month) {
        List<Integer> list = new ArrayList<>();
        for (String m : month) {
            m=m+".31";//2022.02.25
            Integer count = memberDao.findMemberCountBeforeDate(m);
            list.add(count);
        }
        return list;

    }
}

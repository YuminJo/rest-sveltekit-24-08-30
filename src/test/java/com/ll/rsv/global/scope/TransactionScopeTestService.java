package com.ll.rsv.global.scope;

import com.ll.rsv.domain.member.member.repository.MemberRepository;
import com.ll.rsv.global.transactionCache.TransactionCache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TransactionScopeTestService {
    private final TransactionCache transactionCache;
    private final MemberRepository memberRepository;

    public String test1() {
        return (String) transactionCache.computeIfAbsent("test", k -> UUID.randomUUID().toString()); // test 키가 있으면 그 값을 반환, 없으면 랜덤 UUID 생성해서 저장 후 반환
    }

    public String test2() {
        return (String) transactionCache.computeIfAbsent("test", k -> UUID.randomUUID().toString()); // test 키가 있으면 그 값을 반환, 없으면 랜덤 UUID 생성해서 저장 후 반환
    }
}
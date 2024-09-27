package com.ll.rsv.global.scope;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@ActiveProfiles("test")
@RequiredArgsConstructor
public class TransactionScopeTest {
    @Autowired
    private TransactionScopeTestService transactionScopeTestService;

    @Test
    @DisplayName("t1")
    public void t1() {
        // 아래 2개의 메서드는 서로 다른 트랜잭션에서 수행 됩니다.
        // 그래서 둘다 transactionCache 에 test 키값에 해당하는 랜덤 UUID 를 가져오지만, 같은 값이 아닌 다른 값을 가져옵니다.
        // 왜냐하면 transactionCache 는 트랜잭션이 다르면 공유가 안되기 때문입니다.
        String uuid1 = transactionScopeTestService.test1();
        String uuid2 = transactionScopeTestService.test2();

        // 이 경우에는 값이 다른게 정상입니다.
        assertThat(uuid1).isNotEqualTo(uuid2);
    }

    @Test
    @DisplayName("t2")
    @Transactional // 이 어노테이션 덕분에, 아래 2개의 메서드는 같은 트랜잭션에서 수행 됩니다.
    public void t2() {
        // 아래 2개의 메서드는 같은 트랜잭션에서 수행 됩니다.
        String uuid1 = transactionScopeTestService.test1();
        String uuid2 = transactionScopeTestService.test2();

        // 이 경우에는 값이 같은게 정상입니다.
        assertThat(uuid1).isEqualTo(uuid2);
    }
}
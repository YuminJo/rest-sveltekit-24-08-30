package com.ll.rsv.global.jpa.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.data.domain.Persistable;

import static jakarta.persistence.GenerationType.IDENTITY;

@MappedSuperclass
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class BaseEntity implements Persistable<Long> {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    // 이 메서드는 해당 객체가 db 에 저장되기 전인지 알려준다.
    // 사실 딱히 이 메서드는 만들 필요가 없다.
    // 그냥 추가한 것
    @Override
    public boolean isNew() {
        return id == null;
    }
}
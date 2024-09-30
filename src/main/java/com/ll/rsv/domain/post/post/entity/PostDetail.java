package com.ll.rsv.domain.post.post.entity;

import com.ll.rsv.global.jpa.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"post_id", "name"})
})
@Entity
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PROTECTED)
@Builder
@Getter
@Setter
@ToString(callSuper = true)
public class PostDetail extends BaseEntity {
    @ManyToOne(fetch = LAZY)
    private Post post;
    private String name;
    @Column(columnDefinition = "LONGTEXT")
    private String val;
}
package com.microservices.instagrambackend.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@IdClass(KeyFollow.class)
public class Follow {
    //người đc theo dõi
    @Id
    private String followerId;
    //người theo dõi
    @Id
    private String followeeId;
    private Date createdDate;
}

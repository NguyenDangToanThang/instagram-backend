package com.microservices.instagrambackend.domain;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
public class KeyFollow implements Serializable {
    protected String followerId;
    protected String followeeId;
}

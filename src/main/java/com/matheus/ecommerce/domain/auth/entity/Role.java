package com.matheus.ecommerce.domain.auth.entity;

import com.matheus.ecommerce.domain.auth.enums.RoleName;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_role")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    @Column(unique = true, nullable = false)
    private RoleName name;

    public Role(RoleName name){
        this.name = name;
    }
}

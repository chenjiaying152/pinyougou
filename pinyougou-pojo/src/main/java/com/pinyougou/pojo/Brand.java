package com.pinyougou.pojo;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 品牌实体类(配置品牌实体类与品牌表之间的映射关系 ORM JPA)
 */

@Table(name = "tb_brand")  //类与表之间的映射关系
public class Brand implements Serializable {

    //品牌id
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")   //属性与列之间的映射
    private Long id;
    //品牌名称
    @Column(name = "name")
    private String name;
    //品牌首字母
    @Column(name = "first_char")
    private String firstChar;

    @Override
    public String toString() {
        return "Brand{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", firstChar='" + firstChar + '\'' +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstChar() {
        return firstChar;
    }

    public void setFirstChar(String firstChar) {
        this.firstChar = firstChar;
    }
}

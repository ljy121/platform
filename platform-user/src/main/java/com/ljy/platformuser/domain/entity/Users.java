package com.ljy.platformuser.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @TableName users
 */
@TableName(value ="users")
@Data
@EqualsAndHashCode
public class Users implements Serializable {
    /**
     *
     */
    @TableField(value = "id")
    private String id;

    /**
     *
     */
    @TableField(value = "login_name")
    private String login_name;

    /**
     *
     */
    @TableField(value = "real_name")
    private String real_name;

    /**
     *
     */
    @TableField(value = "pinyin_name")
    private String pinyin_name;

    /**
     *
     */
    @TableField(value = "password")
    private String password;

    /**
     *
     */
    @TableField(value = "sex")
    private Integer sex;

    /**
     *
     */
    @TableField(value = "email")
    private String email;

    /**
     *
     */
    @TableField(value = "telephone")
    private String telephone;

    /**
     *
     */
    @TableField(value = "mobilephone")
    private String mobilephone;

    /**
     *
     */
    @TableField(value = "id_card")
    private String id_card;

    /**
     *
     */
    @TableField(value = "worker_state")
    private Integer worker_state;

    /**
     *
     */
    @TableField(value = "worker_type")
    private Integer worker_type;

    /**
     *
     */
    @TableField(value = "extend1")
    private String extend1;

    /**
     *
     */
    @TableField(value = "extend2")
    private String extend2;

    /**
     *
     */
    @TableField(value = "extend3")
    private String extend3;

    /**
     *
     */
    @TableField(value = "extend4")
    private String extend4;

    /**
     *
     */
    @TableField(value = "e_signature")
    private String e_signature;

    /**
     *
     */
    @TableField(value = "index_code")
    private Integer index_code;

    /**
     *
     */
    @TableField(value = "create_worker")
    private String create_worker;

    /**
     *
     */
    @TableField(value = "create_time")
    private Date create_time;

    /**
     *
     */
    @TableField(value = "latest_modify_worker")
    private String latest_modify_worker;

    /**
     *
     */
    @TableField(value = "latest_modify_time")
    private Date latest_modify_time;

    /**
     *
     */
    @TableField(value = "isvalid")
    private Integer isvalid;

    /**
     *
     */
    @TableField(value = "bz1")
    private String bz1;

    /**
     *
     */
    @TableField(value = "bz2")
    private String bz2;

    /**
     *
     */
    @TableField(value = "bz3")
    private String bz3;

    /**
     *
     */
    @TableField(value = "bz4")
    private String bz4;

    /**
     *
     */
    @TableField(value = "bz")
    private String bz;

    /**
     *
     */
    @TableField(value = "is_leader")
    private Integer is_leader;

    /**
     *
     */
    @TableField(value = "ca_key")
    private String ca_key;

    /**
     *
     */
    @TableField(value = "worker_level")
    private Integer worker_level;

    /**
     * 是否首次登陆(0：否 1：是)
     */
    @TableField(value = "i_first_login")
    private Integer i_first_login;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}

package com.lit.support.model;

import lombok.Data;

import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author liulu
 * @version v1.0
 * date 2018-12-14 21:36
 */
@Data
@Table(name = "sign_product")
public class SignProduct implements Serializable {

    private static final long serialVersionUID = -3445991382244565705L;

    private Long id;

    private String code;

    private String fullName;

    private Integer inventory;

    private LocalDateTime gmtCreate;

    private Date gmtUpdate;


}

package com.dyf.db.domain

import com.baomidou.mybatisplus.annotations.TableId
import com.baomidou.mybatisplus.annotations.TableLogic
import com.baomidou.mybatisplus.annotations.TableName

import java.time.LocalDateTime

@TableName("wx_mall_issue")
class WxMallIssueDO {
    @TableId
    Integer id
    String question
    String answer
    LocalDateTime addTime
    @TableLogic
    Boolean deleted
}

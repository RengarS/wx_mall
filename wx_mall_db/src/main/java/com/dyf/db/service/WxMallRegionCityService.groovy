package com.dyf.db.service

import com.baomidou.mybatisplus.service.impl.ServiceImpl
import com.dyf.db.domain.WxMallRegionCityDO
import com.dyf.db.repository.WxMallRegionCityRepository
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Primary
@Transactional
class WxMallRegionCityService extends ServiceImpl<WxMallRegionCityRepository, WxMallRegionCityDO> {
}

package com.yh.register.hosp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yh.register.hosp.mapper.HospitalSetMapper;
import com.yh.register.hosp.service.HospitalSetService;
import com.yh.register.model.hosp.HospitalSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//两个泛型：Mapper和实体类
@Service
public class HospitalSetServiceImpl extends ServiceImpl<HospitalSetMapper, HospitalSet> implements HospitalSetService {

    

}

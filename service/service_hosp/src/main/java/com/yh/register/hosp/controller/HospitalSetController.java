package com.yh.register.hosp.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yh.register.common.exception.RegisterException;
import com.yh.register.common.result.Result;
import com.yh.register.common.result.ResultCodeEnum;
import com.yh.register.common.util.MD5;
import com.yh.register.hosp.service.HospitalSetService;
import com.yh.register.model.hosp.HospitalSet;
import com.yh.register.vo.hosp.HospitalSetQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Api(tags = "医院设置管理")
@RestController
@RequestMapping("/admin/hosp/hospitalSet")
public class HospitalSetController {

    @Autowired
    HospitalSetService hospitalSetService;

    @ApiOperation(value = "获取所有医院设置")
    @GetMapping("/findAll")
    public Result<List<HospitalSet>> findAll(){
        List<HospitalSet> hospitalSetList = hospitalSetService.list();
        return Result.ok(hospitalSetList);
    }

    @ApiOperation(value = "删除医院设置")
    @ApiImplicitParam(name="id",value="医院设置id",dataType = "Long", paramType = "path")
    @DeleteMapping("/{id}")
    public Result deleteHopSet(@PathVariable Long id){
        boolean flag = hospitalSetService.removeById(id);
        if(flag){
            return Result.ok();
        }
        return Result.fail();
    }

    /**
     * 给我当前页、页面大小和非必要的查询条件
     * 1. 参数上的@RequestBody:前台需要传来json类型的参数
     * 2. required = false，非必要，即前台不传该参数也可以匹配到该函数，
     *    若没有required = false，那么前台不传输该json就找不到该函数了，因为参数不匹配
     * @param current 当前页
     * @param size 分页大小
     * @param queryVo 查询条件
     * @return
     */
    @ApiOperation("带条件的分页查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页面", dataType = "Integer", paramType = "path"),
            @ApiImplicitParam(name = "limit", value = "页面大小", dataType = "Integer", paramType = "path"),
            @ApiImplicitParam(name = "queryVo", value = "查询条件", dataType = "HospitalSetQueryVo", paramType = "path")
    })
    @PostMapping("/findPageHospSet/{current}/{limit}")
    public Result<Page<HospitalSet>> findPageHosp(@PathVariable Integer current,
                                          @PathVariable("limit") Integer size,
                                          @RequestBody(required = false) HospitalSetQueryVo queryVo){
        //构造 page 对象
        Page<HospitalSet> page = new Page<>(current,size);
        Page<HospitalSet> hospitalSetPage = null;
        if(queryVo != null){
            //构造查询条件
            LambdaQueryWrapper<HospitalSet> wrapper = new LambdaQueryWrapper<>();
            wrapper.like(!StringUtils.isEmpty(queryVo.getHosname()),HospitalSet::getHosname, queryVo.getHosname())
                    .eq(!StringUtils.isEmpty(queryVo.getHoscode()),HospitalSet::getHoscode,queryVo.getHoscode());
            hospitalSetPage = hospitalSetService.page(page, wrapper);
        }else{
            hospitalSetPage = hospitalSetService.page(page);
        }
        return Result.ok(hospitalSetPage);
    }

    @ApiOperation("添加医院设置")
    @ApiImplicitParam(name = "hospitalSet", value = "医院设置对象", dataType = "HospitalSet", paramType = "body")
    @PostMapping("/saveHospitalSet")
    public Result<?> saveHospSet(@RequestBody HospitalSet hospitalSet){
        hospitalSet.setStatus(1);
        String signKey = MD5.encrypt(System.currentTimeMillis() + "" + new Random().nextInt(1000));
        hospitalSet.setSignKey(signKey);
        boolean flag = hospitalSetService.save(hospitalSet);
        if(flag){
            return Result.ok();
        }
        return Result.fail();
    }

    @ApiOperation("根据id查询医院设置")
    @ApiImplicitParam(name = "id", value = "医院设置ID", dataType = "Long", paramType = "path")
    @GetMapping("/getHospSet/{id}")
    public Result<HospitalSet> findHospSetById(@PathVariable Long id){
        //处理自定义异常
//        try {
//            int i = 10 / 0;
//        } catch (Exception e) {
//            throw new RegisterException(ResultCodeEnum.DATA_ERROR);
//        }
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        if(hospitalSet != null)
            return Result.ok(hospitalSet);
        return Result.fail();
    }

    @ApiOperation("修改医院设置")
    @ApiImplicitParam(name = "hospitalSet", value = "医院设置对象", dataType = "HospitalSet", paramType = "body")
    @PostMapping("/updateHospitalSet")
    public Result<?> updateHospSet(@RequestBody HospitalSet hospitalSet){
        boolean flag = false;
        if(hospitalSet != null && hospitalSet.getId() != null)
            flag = hospitalSetService.updateById(hospitalSet);
        if(flag)
            return Result.ok();
        return Result.fail();
    }

    @ApiOperation("批量删除医院设置")
    @ApiImplicitParam(name = "ids", value = "需要批量删除的医院设置id", dataType = "List<Long>", paramType = "body")
    @DeleteMapping("/batchRemove")
    public Result<?> removeHospSetBatchs(@RequestBody List<Long> ids){
        boolean flag = false;
        if(ids != null && ids.size() > 0){
            flag = hospitalSetService.removeByIds(ids);
        }
        if(flag)
            return Result.ok();
        return Result.fail();
    }

    @ApiOperation("锁定/解锁医院设置")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "医院设置ID", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "status", value = "医院设置对象锁定状态", dataType = "Integer", paramType = "path")

    })
    @PutMapping("/lockHospitalSet/{id}/{status}")
    public Result<?> lock(@PathVariable Long id, @PathVariable Integer status){
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        hospitalSet.setStatus(status);
        boolean flag = hospitalSetService.updateById(hospitalSet);
        if (flag) {
            return Result.ok();
        }
        return Result.fail();
    }

    @ApiOperation("发送签名key")
    @PutMapping("/sendKey/{id}")
    public Result<?> sendSignKey(@PathVariable Long id){

        HospitalSet hospitalSet = hospitalSetService.getById(id);
        String hoscode = hospitalSet.getHoscode();
        String signkey = hospitalSet.getSignKey();
        // TODO 短信发送
        return Result.ok();
    }

}

package com.moli.system.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moli.common.core.MoliResult;
import com.moli.common.domain.entity.SysDictData;
import com.moli.common.domain.entity.SysDictType;
import com.moli.common.domain.vo.DictDataVo;
import com.moli.common.domain.vo.DictTypeVo;
import com.moli.common.page.PageRes;
import com.moli.common.utils.MoliDateUtils;
import com.moli.system.mapper.DictDataMapper;
import com.moli.system.mapper.DictTypeMapper;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("dict")
@Api(tags = "字典管理")
@Slf4j
public class DictController {

    @Autowired
    private DictDataMapper dictDataMapper;

    @Autowired
    private DictTypeMapper dictTypeMapper;

    /**
     * 字典类型列表
     *
     * @return 菜单列表
     */
    @GetMapping("/type/list")
    public MoliResult<PageRes<SysDictType>> list(DictTypeVo dictTypeVo) {

        PageRes<SysDictType> result = new PageRes<>();
        LambdaQueryWrapper<SysDictType> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotBlank(dictTypeVo.getDictName())) {
            lambdaQueryWrapper.eq(SysDictType::getDictName, dictTypeVo.getDictName());
        }
        if (StringUtils.isNotBlank(dictTypeVo.getDictType())) {
            lambdaQueryWrapper.eq(SysDictType::getDictType, dictTypeVo.getDictType());
        }
        if (dictTypeVo.getStatus() != null) {
            lambdaQueryWrapper.eq(SysDictType::getStatus, dictTypeVo.getStatus());
        }
        if (StringUtils.isNotBlank(dictTypeVo.getBeginTime())) {
            lambdaQueryWrapper.between(SysDictType::getCreateTime, MoliDateUtils.startTimeToDateStart(dictTypeVo.getBeginTime()), MoliDateUtils.endTimeToDateEnd(dictTypeVo.getEndTime()));
        }

        Page page = new Page();
        page.setCurrent(dictTypeVo.getPageNum());
        page.setSize(dictTypeVo.getPageSize());
        dictTypeMapper.selectPage(page, lambdaQueryWrapper);
        Long total = page.getTotal();
        result.setTotal(total.intValue());
        result.setList(page.getRecords());
        result.setPageNum(dictTypeVo.getPageNum());
        result.setPageSize(dictTypeVo.getPageSize());
        return MoliResult.success(result);

    }

    /**
     * 字典类型列表
     *
     * @return 菜单列表
     */
    @GetMapping("/type/listAll")
    public MoliResult<List<SysDictType>> listAll(DictTypeVo dictTypeVo) {
        return MoliResult.success(dictTypeMapper.selectList(new LambdaQueryWrapper<>()));
    }

    /**
     * 添加字典类型
     *
     * @return 添加字典类型
     */
    @PostMapping("/type")
    public MoliResult<Boolean> insert(@RequestBody SysDictType dictType) {
        return MoliResult.success(dictTypeMapper.insert(dictType) > 0 ? Boolean.TRUE : Boolean.FALSE);
    }

    /**
     * 获取字典类型列表
     *
     * @return 菜单列表
     */
    @PutMapping("/type")
    public MoliResult<Boolean> update(@RequestBody SysDictType dictType) {
        return MoliResult.success(dictTypeMapper.updateById(dictType) > 0 ? Boolean.TRUE : Boolean.FALSE);
    }

    /**
     * 查询字典类型
     */
    @GetMapping(value = "/type/{id}")
    public MoliResult<SysDictType> getDictTypeInfo(@PathVariable Long id) {

        return MoliResult.success(dictTypeMapper.selectById(id));
    }

    /**
     * 删除字典类型
     */
    @DeleteMapping("/type/{dictIds}")
    public MoliResult delete(@PathVariable Long[] dictIds) {
        for (Long id : dictIds) {
            dictTypeMapper.deleteById(id);
        }
        return MoliResult.success(Boolean.TRUE);
    }


    /**
     * 字典数据列表
     *
     * @return 菜单数据列表
     */
    @GetMapping("/data/list")
    public MoliResult<PageRes<SysDictData>> list(DictDataVo dictDataVo) {

        PageRes<SysDictData> result = new PageRes<>();
        LambdaQueryWrapper<SysDictData> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        lambdaQueryWrapper.eq(SysDictData::getDictType, dictDataVo.getDictType());

        if (StringUtils.isNotBlank(dictDataVo.getDictValue())) {
            lambdaQueryWrapper.eq(SysDictData::getDictValue, dictDataVo.getDictValue());
        }
        if (dictDataVo.getStatus() != null) {
            lambdaQueryWrapper.eq(SysDictData::getStatus, dictDataVo.getStatus());
        }
        if (StringUtils.isNotBlank(dictDataVo.getBeginTime())) {
            lambdaQueryWrapper.between(SysDictData::getCreateTime, MoliDateUtils.startTimeToDateStart(dictDataVo.getBeginTime()), MoliDateUtils.endTimeToDateEnd(dictDataVo.getEndTime()));
        }

        Page page = new Page();
        page.setCurrent(dictDataVo.getPageNum());
        page.setSize(dictDataVo.getPageSize());
        dictDataMapper.selectPage(page, lambdaQueryWrapper);
        Long total = page.getTotal();
        result.setTotal(total.intValue());
        result.setList(page.getRecords());
        result.setPageNum(dictDataVo.getPageNum());
        result.setPageSize(dictDataVo.getPageSize());
        return MoliResult.success(result);

    }

    /**
     * 查询字典类型
     */
    @GetMapping(value = "/data/{id}")
    public MoliResult<SysDictData> getDictDataInfo(@PathVariable Long id) {

        return MoliResult.success(dictDataMapper.selectById(id));
    }

    /**
     * 查询字典类型
     */
    @PutMapping(value = "/data")
    public MoliResult<Boolean> getDictDataInfo(@RequestBody SysDictData dictData) {

        return MoliResult.success(dictDataMapper.updateById(dictData) > 0 ? Boolean.TRUE : Boolean.FALSE);
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/data/{dictIds}")
    public MoliResult deleteData(@PathVariable Long[] dataIds) {
        for (Long id : dataIds) {
            dictTypeMapper.deleteById(id);
        }
        return MoliResult.success(Boolean.TRUE);
    }

}

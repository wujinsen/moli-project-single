package com.moli.system.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moli.common.constant.PermissionConstants;
import com.moli.common.core.MoliResult;
import com.moli.common.domain.entity.SysDictData;
import com.moli.common.domain.entity.SysDictType;
import com.moli.common.domain.vo.DictDataVo;
import com.moli.common.domain.vo.DictTypeVo;
import com.moli.common.page.PageRes;
import com.moli.common.utils.MoliDateUtils;
import com.moli.system.mapper.DictDataMapper;
import com.moli.system.mapper.DictTypeMapper;
import com.moli.common.utils.I18nUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("dict")
@Api(tags = "字典管理")
@Slf4j
public class DictController {

    @Autowired
    private DictDataMapper dictDataMapper;

    @Autowired
    private DictTypeMapper dictTypeMapper;


    @GetMapping("/type/list")
    @RequiresPermissions(PermissionConstants.SYSTEM_DICT_LIST)
    @ApiOperation(value = "字典类型分页", notes = "字典类型分页")
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

    @GetMapping("/type/listAll")
    @RequiresPermissions(PermissionConstants.SYSTEM_DICT_LIST)
    @ApiOperation(value = "字典类型列表", notes = "字典类型列表")
    public MoliResult<List<SysDictType>> listAll() {
        return MoliResult.success(dictTypeMapper.selectList(new LambdaQueryWrapper<>()));
    }


    @PostMapping("/type")
    @RequiresPermissions(PermissionConstants.SYSTEM_DICT_LIST)
    @ApiOperation(value = "添加字典类型", notes = "添加字典类型")
    public MoliResult<Boolean> insert(@RequestBody SysDictType dictType) {
        return MoliResult.success(dictTypeMapper.insert(dictType) > 0 ? Boolean.TRUE : Boolean.FALSE);
    }

    @PutMapping("/type")
    @RequiresPermissions(PermissionConstants.SYSTEM_DICT_LIST)
    @ApiOperation(value = "更新字典类型", notes = "更新字典类型")
    public MoliResult<Boolean> update(@RequestBody SysDictType dictType) {
        return MoliResult.success(dictTypeMapper.updateById(dictType) > 0 ? Boolean.TRUE : Boolean.FALSE);
    }

    @GetMapping(value = "/type/{id}")
    @RequiresPermissions(PermissionConstants.SYSTEM_DICT_LIST)
    @ApiOperation(value = "查询字典类型", notes = "查询字典类型")
    public MoliResult<SysDictType> getDictTypeInfo(@PathVariable Long id) {

        return MoliResult.success(dictTypeMapper.selectById(id));
    }

    /**
     * 删除字典类型
     */
    @DeleteMapping("/type/{dictIds}")
    @RequiresPermissions(PermissionConstants.SYSTEM_DICT_LIST)
    public MoliResult delete(@PathVariable Long[] dictIds) {
        for (Long id : dictIds) {
            dictTypeMapper.deleteById(id);
        }
        return MoliResult.success(Boolean.TRUE);
    }


    /**
     * 根据字典类型查询字典数据（前端下拉/标签）
     */
    @GetMapping("/data/type/{dictType}")
    @ApiOperation(value = "按类型查询字典", notes = "按类型查询字典")
    public MoliResult<List<Map<String, Object>>> dictType(@PathVariable String dictType) {
        String lang = I18nUtils.resolveLanguage();
        List<SysDictData> list = dictDataMapper.selectList(new LambdaQueryWrapper<SysDictData>()
                .eq(SysDictData::getDictType, dictType)
                .eq(SysDictData::getStatus, 1)
                .orderByAsc(SysDictData::getSort));
        List<Map<String, Object>> result = list.stream().map(item -> {
            Map<String, Object> map = new HashMap<>(4);
            map.put("dictLabel", I18nUtils.resolveLocalizedText(
                    item.getDictValue(), item.getDictValueEn(), item.getDictValueJa(), lang));
            map.put("dictValue", item.getDictKey());
            map.put("dictType", item.getDictType());
            map.put("status", item.getStatus());
            return map;
        }).collect(Collectors.toList());
        return MoliResult.success(result);
    }

    /**
     * 字典数据列表
     *
     * @return 菜单数据列表
     */
    @GetMapping("/data/list")
    @RequiresPermissions(PermissionConstants.SYSTEM_DICT_LIST)
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
    @RequiresPermissions(PermissionConstants.SYSTEM_DICT_LIST)
    public MoliResult<SysDictData> getDictDataInfo(@PathVariable Long id) {

        return MoliResult.success(dictDataMapper.selectById(id));
    }

    @PostMapping("/data")
    @RequiresPermissions(PermissionConstants.SYSTEM_DICT_LIST)
    @ApiOperation(value = "添加字典数据", notes = "添加字典数据")
    public MoliResult<Boolean> insertData(@RequestBody SysDictData dictData) {
        return MoliResult.success(dictDataMapper.insert(dictData) > 0 ? Boolean.TRUE : Boolean.FALSE);
    }

    /**
     * 修改字典数据
     */
    @PutMapping(value = "/data")
    @RequiresPermissions(PermissionConstants.SYSTEM_DICT_LIST)
    public MoliResult<Boolean> getDictDataInfo(@RequestBody SysDictData dictData) {

        return MoliResult.success(dictDataMapper.updateById(dictData) > 0 ? Boolean.TRUE : Boolean.FALSE);
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/data/{dictIds}")
    @RequiresPermissions(PermissionConstants.SYSTEM_DICT_LIST)
    public MoliResult deleteData(@PathVariable("dictIds") Long[] dataIds) {
        for (Long id : dataIds) {
            dictDataMapper.deleteById(id);
        }
        return MoliResult.success(Boolean.TRUE);
    }

}

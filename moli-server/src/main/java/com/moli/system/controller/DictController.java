package com.moli.system.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moli.common.constant.CommonConstant;
import com.moli.common.core.MoliResult;
import com.moli.common.domain.entity.DictData;
import com.moli.common.domain.entity.DictType;
import com.moli.common.domain.entity.Menu;
import com.moli.common.domain.entity.User;
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
    public MoliResult<PageRes<DictType>> list(DictTypeVo dictTypeVo) {

        PageRes<DictType> result = new PageRes<>();
        LambdaQueryWrapper<DictType> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotBlank(dictTypeVo.getDictName())) {
            lambdaQueryWrapper.eq(DictType::getDictName, dictTypeVo.getDictName());
        }
        if (StringUtils.isNotBlank(dictTypeVo.getDictType())) {
            lambdaQueryWrapper.eq(DictType::getDictType, dictTypeVo.getDictType());
        }
        if (dictTypeVo.getStatus() != null) {
            lambdaQueryWrapper.eq(DictType::getStatus, dictTypeVo.getStatus());
        }
        if (StringUtils.isNotBlank(dictTypeVo.getBeginTime())) {
            lambdaQueryWrapper.between(DictType::getCreateTime, MoliDateUtils.startTimeToDateStart(dictTypeVo.getBeginTime()), MoliDateUtils.endTimeToDateEnd(dictTypeVo.getEndTime()));
        }

        Page page = new Page();
        page.setPages(dictTypeVo.getPageNum());
        page.setSize(dictTypeVo.getPageSize());
        dictTypeMapper.selectPage(page, lambdaQueryWrapper);
        Long total = page.getTotal();
        result.setTotal(total.intValue());
        result.setItems(page.getRecords());
        result.setPageNum(dictTypeVo.getPageNum());
        result.setPageSize(dictTypeVo.getPageSize());
        return MoliResult.success(result);

    }

    /**
     * 添加字典类型
     *
     * @return 添加字典类型
     */
    @PostMapping("/type")
    public MoliResult<Boolean> insert(@RequestBody DictType dictType) {
        return MoliResult.success(dictTypeMapper.insert(dictType) > 0 ? Boolean.TRUE : Boolean.FALSE);
    }

    /**
     * 获取字典类型列表
     *
     * @return 菜单列表
     */
    @PutMapping("/type")
    public MoliResult<Boolean> update(@RequestBody DictType dictType) {
        return MoliResult.success(dictTypeMapper.updateById(dictType) > 0 ? Boolean.TRUE : Boolean.FALSE);
    }

    /**
     * 查询字典类型
     */
    @GetMapping(value = "/type/{id}")
    public MoliResult<DictType> getInfo(@PathVariable Long id) {

        return MoliResult.success(dictTypeMapper.selectById(id));
    }

    /**
     * 删除用户
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
    public MoliResult<PageRes<DictData>> list(DictDataVo dictDataVo) {

        PageRes<DictData> result = new PageRes<>();
        LambdaQueryWrapper<DictData> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        lambdaQueryWrapper.eq(DictData::getDictType, dictDataVo.getDictType());

        if (StringUtils.isNotBlank(dictDataVo.getDictValue())) {
            lambdaQueryWrapper.like(DictData::getDictValue, dictDataVo.getDictValue());
        }
        if (dictDataVo.getStatus() != null) {
            lambdaQueryWrapper.eq(DictData::getStatus, dictDataVo.getStatus());
        }
        if (StringUtils.isNotBlank(dictDataVo.getBeginTime())) {
            lambdaQueryWrapper.between(DictData::getCreateTime, MoliDateUtils.startTimeToDateStart(dictDataVo.getBeginTime()), MoliDateUtils.endTimeToDateEnd(dictDataVo.getEndTime()));
        }

        Page page = new Page();
        page.setPages(dictDataVo.getPageNum());
        page.setSize(dictDataVo.getPageSize());
        dictDataMapper.selectPage(page, lambdaQueryWrapper);
        Long total = page.getTotal();
        result.setTotal(total.intValue());
        result.setItems(page.getRecords());
        result.setPageNum(dictDataVo.getPageNum());
        result.setPageSize(dictDataVo.getPageSize());
        return MoliResult.success(result);

    }

}

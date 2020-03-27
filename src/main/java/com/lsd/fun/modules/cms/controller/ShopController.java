package com.lsd.fun.modules.cms.controller;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.lsd.fun.common.annotation.SysLog;
import com.lsd.fun.common.utils.excel.ExcelReader;
import com.lsd.fun.common.utils.excel.writer.ExcelWriterFactory;
import com.lsd.fun.modules.cms.dto.ShopExcelDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import com.lsd.fun.common.utils.BaseQuery;
import com.lsd.fun.modules.cms.entity.ShopEntity;
import com.lsd.fun.modules.cms.service.ShopService;
import com.lsd.fun.common.utils.PageUtils;
import com.lsd.fun.common.utils.R;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;


/**
 * 店铺表
 *
 * @author lsd
 * @email syndaliang@foxmail.com
 * @date 2020-03-26 01:29:43
 */
@Api(tags = "店铺")
@RestController
@RequestMapping("cms/shop")
public class ShopController {
    @Autowired
    private ShopService shopService;
    @Autowired
    private ExcelWriterFactory excelWriterFactory;

    /**
     * 列表
     */
    @GetMapping("/list")
    @RequiresPermissions("cms:shop:list")
    public R list(BaseQuery query) {
        PageUtils page = shopService.queryPage(query);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @GetMapping("/info/{id}")
    @RequiresPermissions("cms:shop:info")
    public R info(@PathVariable("id") Integer id) {
        ShopEntity shop = shopService.getById(id);

        return R.ok().put("shop", shop);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    @RequiresPermissions("cms:shop:save")
    public R save(@RequestBody ShopEntity shop) {
        shopService.save(shop);

        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @RequiresPermissions("cms:shop:update")
    public R update(@RequestBody ShopEntity shop) {
        shopService.updateById(shop);

        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @RequiresPermissions("cms:shop:delete")
    public R delete(@RequestBody Integer[] ids) {
        shopService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }


    @Transactional
    @SysLog("批量上架/下架")
    @ApiOperation("批量上架/下架")
    @PostMapping("/enable")
    @RequiresPermissions("cms:shop:delete")
    public R delete(@RequestBody List<Integer> ids, Integer disabledFlag) {
        shopService.lambdaUpdate()
                .set(ShopEntity::getDisabledFlag, disabledFlag)
                .in(ShopEntity::getId, ids)
                .update();
        return R.ok();
    }

    @SysLog("Excel批量导入")
    @ApiOperation(value = "Excel批量导入", notes = "Excel批量导入")
    @PostMapping("/uploadExcel")
    @RequiresPermissions("cms:shop:save")
    public R uploadExcel(MultipartFile file,
                         @RequestParam(required = false, defaultValue = "0") @ApiParam("是否更新，0：否，1：是") Integer isUpdate) {
        if (null == file || file.isEmpty()) {
            return R.error("上传文件为空");
        }
        // 解析Excel
        List<ShopExcelDTO> parsedResult = ExcelReader.readExcel(file, isUpdate);
        // ShopExcelDTO -> ShopEntity 批量新增或更新
        shopService.saveFromExcelParsedResult(parsedResult, isUpdate);
        return R.ok();
    }


    @ApiOperation(value = "导出Excel", notes = "导出所有商铺")
    @GetMapping("/listExport")
    @RequiresPermissions("cms:shop:list")
    public R listExport(@RequestParam(required = false, defaultValue = "0") @ApiParam("是否导出模板，0：否，1：是") Integer isTemplate, HttpServletResponse response) {
        // 全部按照固定资产的列头导出，非固定的把对应列值填充空白即可
        Workbook workbook = excelWriterFactory.getWriter("shopExcelWriter")
                .exportExcel(null, isTemplate == 1 ? null : shopService.listAll());
        OutputStream out = null;
        // 输出Excel文件流
        String excelName = isTemplate == 1 ? "商铺信息模板" : "商铺信息";
        String fileName = excelName + (workbook instanceof HSSFWorkbook ? ".xls" : ".xlsx");
        try {
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);   // 网页直接下载
            response.setCharacterEncoding("UTF-8");
            response.addHeader("pragma", "no-cache");
            response.addHeader("Cache-Control", "no-cache");
            response.flushBuffer();
            out = response.getOutputStream();
            workbook.write(out);
            out.flush();
            return R.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("Excel导出发生错误");
        } finally {
            IOUtils.closeQuietly(out);
            if (workbook instanceof SXSSFWorkbook) {
                SXSSFWorkbook sxssfWorkbook = (SXSSFWorkbook) workbook;
                sxssfWorkbook.dispose();
            }
            IOUtils.closeQuietly(workbook);
        }
    }

}

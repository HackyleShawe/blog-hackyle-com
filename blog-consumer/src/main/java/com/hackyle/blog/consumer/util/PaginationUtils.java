package com.hackyle.blog.consumer.util;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hackyle.blog.consumer.dto.OrderItemDto;
import com.hackyle.blog.consumer.dto.PageRequestDto;
import com.hackyle.blog.consumer.dto.PageResponseDto;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 基于MyBatista的分页操作
 */
public class PaginationUtils {

    /**
     * 抽取分页请求类中的分页关键参数，组装MyBatis-Plus中分页Page
     * T: PageRequestDto中的查询条件的泛型
     * E：本次分页查询表所对应的实体类(XxxEntity)
     */
    public static <T, E> Page<E> PageRequest2IPage(PageRequestDto<T> PageRequestDto, Class<E> target) {
        if(target == null) {
            throw new RuntimeException("Target can't be null");
        }

        Page<E> page = new Page<>();

        List<OrderItemDto> orders = PageRequestDto.getOrders();
        if (orders != null && !orders.isEmpty()) {
            List<OrderItem> orderItemList = orders.stream().map(PaginationUtils::convertOrderItem).collect(Collectors.toList());
            page.setOrders(orderItemList);
        }
        page.setCurrent(PageRequestDto.getCurrentPage());
        page.setSize(PageRequestDto.getPageSize());

        return page;
    }

    private static OrderItem convertOrderItem(OrderItemDto OrderItemDto) {
        if (OrderItemDto.isAsc()) {
            return OrderItem.asc(OrderItemDto.getColumn());
        } else {
            return OrderItem.desc(OrderItemDto.getColumn());
        }
    }

    /**
     * MyBatis-Plus的IPage转分页显示类PageResponseDTO
     */
    public static <T> PageResponseDto<T> IPage2PageResponse(IPage<T> page) {
        PageResponseDto<T> pageResponse = new PageResponseDto<>(page.getRecords(), page.getTotal());
        pageResponse.setCurrentPage(page.getCurrent());
        pageResponse.setPageSize(page.getSize());
        pageResponse.setPages(page.getPages());

        return pageResponse;
    }

    /**
     * MyBatis-Plus的IPage转分页显示类PageResponseDTO
     */
    public static <T, E> PageResponseDto<T> IPage2PageResponse(IPage<E> page, Class<T> targetClazz) {
        if(targetClazz == null) {
            throw new RuntimeException("Target can't be null");
        }

        PageResponseDto<T> pageResultDTO = new PageResponseDto<>();
        pageResultDTO.setCurrentPage(page.getCurrent());
        pageResultDTO.setPageSize(page.getSize());
        pageResultDTO.setTotal(page.getTotal());
        pageResultDTO.setPages(page.getPages());

        List<T> pageRows = BeanCopyUtils.copyList(page.getRecords(), targetClazz);
        pageResultDTO.setRows(pageRows);

        return pageResultDTO;
    }
}

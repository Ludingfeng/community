package com.ldf.community.community.dto;

import lombok.Data;

import java.util.LinkedList;
import java.util.List;

@Data
public class PaginationDTO {
    private List<QuestionDTO> questions;
    private boolean showPrevious;//是否展示上一页按钮
    private boolean showNext;//是否展示下一页按钮
    private boolean showFirstPage;//是否展示跳转第一页按钮
    private boolean showEndPage;//是否展示跳转最后一页按钮
    private Integer currentPage;//当前页
    private List<Integer> pages;//当前显示的所有页码
    private Integer totalPage;//总页数

    //设置分页
    public void setPagination(Integer totalPage, Integer page) {
        this.totalPage = totalPage;
        this.currentPage = page;

        pages = new LinkedList<>();
        for (int i = Math.max(currentPage - 3, 1); i <= Math.min(currentPage + 3, totalPage); i++) {
            pages.add(i);
        }

        showPrevious = currentPage == 1 ? false : true;
        showNext = currentPage == totalPage ? false : true;

        showFirstPage = pages.contains(1) ? false : true;
        showEndPage = pages.contains(totalPage) ? false : true;

    }
}

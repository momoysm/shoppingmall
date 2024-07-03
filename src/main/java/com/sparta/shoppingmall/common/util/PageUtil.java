package com.sparta.shoppingmall.common.util;

import com.sparta.shoppingmall.common.exception.customexception.PageNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public final class PageUtil {

    private PageUtil() {
    }

    public static final Integer PAGE_SIZE_FIVE = 5;
    public static final String NO_ELEMENT_MESSAGE = "조회된 데이터가 없습니다.";

    public static Pageable createPageable(Integer pageNum, Integer pageSize, Boolean isDesc) {
        if(pageNum < 1){
            throw new PageNotFoundException("페이지는 1보다 작을 수 없습니다.");
        }

        Sort.Direction direction = isDesc ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, "createAt");

        return PageRequest.of(pageNum-1, pageSize, sort);
    }

    public static <T> String validateAndSummarizePage(Integer pageNum, Page<T> page) {
        if(page.getTotalElements() <= 0) {
            return NO_ELEMENT_MESSAGE;
        }
        if(pageNum > page.getTotalPages()) {
            throw new PageNotFoundException("접근할 수 없는 페이지입니다.");
        }

        return page.getTotalElements() + "개 조회 완료!";
    }

}

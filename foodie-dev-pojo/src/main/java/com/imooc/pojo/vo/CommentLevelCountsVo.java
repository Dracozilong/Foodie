package com.imooc.pojo.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentLevelCountsVo {

    //总评价数
    private Integer totalCounts;

    //好评数
    private Integer goodCounts;

    //中评数
    private Integer normalCounts;

    //差评数
    private Integer badCounts;

    public Integer getTotalCounts() {
        return totalCounts;
    }

    public void setTotalCounts(Integer totalCounts) {
        this.totalCounts = totalCounts;
    }

    public Integer getGoodCounts() {
        return goodCounts;
    }

    public void setGoodCounts(Integer goodCounts) {
        this.goodCounts = goodCounts;
    }

    public Integer getNormalCounts() {
        return normalCounts;
    }

    public void setNormalCounts(Integer normalCounts) {
        this.normalCounts = normalCounts;
    }

    public Integer getBadCounts() {
        return badCounts;
    }

    public void setBadCounts(Integer badCounts) {
        this.badCounts = badCounts;
    }
}

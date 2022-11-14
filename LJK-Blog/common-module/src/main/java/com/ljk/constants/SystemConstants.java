package com.ljk.constants;

public class SystemConstants
{
    /**
     *  文章是草稿
     */
    public static final int ARTICLE_STATUS_DRAFT = 1;
    /**
     *  文章是正常分布状态
     */
    public static final int ARTICLE_STATUS_NORMAL = 0;
    /**
     * 分页时的开始页码
     */
    public static final int PAGE_CURRENT=1;
    /**
     * 分页时的页面量
     */
    public static final int PAGE_SIZE=10;

    public static final String STATUS_NORMAL= "0";
    /**
     * 友链审核通过的状态码
     */
    public static final String LINK_STATUS_NORMAL= "0";
    /**
     * 评论中根id
     */
    public static final Long ROOT_ID=-1L;
    /**
     * 文章类型
     */
    public static final String ARTICLE_COMMENT="0";
    /**
     * 友链类型
     */
    public static final String LINK_COMMENT="1";

    public static final String MENU="C";

    public static final String BUTTON="F";
    public static final String ADMIN_USER = "1";

    /**
     * 文章是否逻辑删除
     */
    public static final Integer DEL_FLAG=0;
    public static final char ROLE_DEL = '1';
    public static final Integer USER_DEL_FLAG = 1;
    public static final Integer CATEGORY_LOGIC_DEL = 1;
    public static final Integer LINK_DEL = 1;
}

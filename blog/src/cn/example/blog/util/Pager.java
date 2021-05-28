package cn.example.blog.util;

import java.util.regex.Pattern;

public class Pager {
    //用户传递过来的参数
    private Integer currentPage;//当前页
    private Integer pageSize;//每页显示的条数
    private Integer totalCount;//数据总记录数
    private String url;
    //计算出来的数据
    private Integer totalPage;//总页数
    private Integer prevPage;//上一页
    private Integer nextPage;//下一页

    //使用构造器来封装数据
    public Pager(String page, Integer pageSize, String url, Integer totalCount) {
        this.currentPage = page(page);
        this.pageSize = pageSize;
        this.url = url;
        this.totalCount = totalCount;
        //计算出得到的数据
        this.totalPage = totalCount % pageSize == 0 ? totalCount / pageSize : totalCount / pageSize + 1;
        this.prevPage = currentPage > 1 ? currentPage - 1 : 1;
        this.nextPage = currentPage < totalPage ? currentPage + 1 : totalPage;
        if (this.currentPage > this.totalPage) {
            this.currentPage = this.totalPage;
        }
    }

    private int page(String page) {
        int p = 1;
        Pattern pattern = Pattern.compile("^[1-9][0-9]*$");
        Boolean b = pattern.matcher(page).matches();
        if (b) {
            p = Integer.parseInt(page);
        }
        return p;
    }

    private String prevHtml() {
        if (this.currentPage == 1) {
            return "<li class='disabled'><a style='cursor: not-allowed;color:#000' href='javascript:void(0)'>上一页</a></li>";
        } else {
            return "<li><a  href='" + this.url + "&page=" + (this.currentPage - 1) + "'>上一页</a></li>";
        }
    }

    private String pageHtml() {
        String pageHtml = "";
        if (this.totalPage <= 9) {
            for (int i = 1; i <= this.totalPage; i++) {
                if (this.currentPage == i) {
                    pageHtml += "<li class='active'><a href='javascript:void(0)'>" + i + "</a></li>";
                } else {
                    pageHtml += "<li><a  href='" + this.url + "&page=" + i + "'>" + i + "</a></li>";
                }
            }
        } else if (this.totalPage > 9) {
            for (int i = 1; i <= 3; i++) {
                if (this.currentPage == i) {
                    pageHtml += "<li class='active'><a href='javascript:void(0)'>" + i + "</a></li>";
                } else {
                    if (i == 3 && this.currentPage > 6) {
                        pageHtml += "<li><a href='javascript:void(0)'>..</a></li>";
                    } else {
                        pageHtml += "<li><a href='" + this.url + "&page=" + i + "'>" + i + "</a></li>";
                    }
                }
            }
            //=======================
            if (this.currentPage <= 6) {
                for (int i = 4; i <= 9; i++) {
                    if (this.currentPage == i) {
                        pageHtml += "<li class='active'><a href='javascript:void(0)'>" + i + "</a></li>";
                    } else {
                        if (i == 9) {
                            pageHtml += "<li><a href='javascript:void(0)'>..</a></li>";
                        } else {
                            pageHtml += "<li ><a href='" + this.url + "&page=" + i + "'>" + i + "</a></li>";
                        }
                    }
                }
            }
            //=============
            if (this.currentPage > 6 && this.totalPage - this.currentPage >= 3) {
                for (int i = this.currentPage - 2; i <= this.currentPage + 3 && i <= this.totalPage; i++) {
                    if (this.currentPage == i) {
                        pageHtml += "<li class='active'><a href='javascript:void(0)'>" + i + "</a></li>";
                    } else {
                        if (i == this.currentPage + 3 && this.currentPage + 3 < this.totalPage) {
                            pageHtml += "<li><a href='javascript:void(0)'>..</a></li>";
                        } else {
                            pageHtml += "<li ><a href='" + this.url + "&page=" + i + "'>" + i + "</a></li>";
                        }
                    }
                }
            }
            //=====
            if (this.totalPage - this.currentPage < 3) {
                for (int i = this.totalPage - 5; i <= this.totalPage; i++) {
                    if (this.currentPage == i) {
                        pageHtml += "<li class='active'><a href='javascript:void(0)'>" + i + "</a></li>";
                    } else {
                        pageHtml += "<li ><a href='" + this.url + "&page=" + i + "'>" + i + "</a></li>";
                    }
                }
            }
        }
        return pageHtml;
        //====
    }

    private String nextHtml() {
        if (this.currentPage >= this.totalPage) {
            return "<li class='disabled'><a style='cursor: not-allowed;color:#000' href='javascript:void(0)'>下一页</a></li>";
        } else {
            return "<li><a href='" + this.url + "&page=" + this.nextPage + "'>下一页</a></li>";
        }
    }

    private String goPage() {
        String goPage = "";
        //goPage += "<form action='" + this.url + "' method='get' style='display:inline'>";
        goPage += "<input id='goPageValue' style='margin-top:-3px;text-align: center;width:50px;vertical-align: middle;' id='goPage' class='page_text' type='number'value='" + this.currentPage + "'>";
        goPage += "<input onclick='var goPageValue=document.getElementById(\"goPageValue\").value;window.location=\""+this.url+"&page=\"+goPageValue+\"\";' style='margin-top:-3px;vertical-align: middle;' type='button' value='确定'>";

        return goPage;
    }

    public String style() {
        String style = "";
        style += "<style>";
        style += ".pagination{list-style:none;}";
        style += ".pagination li{text-align:center;float:left;margin-left:5px;}";
        style += ".pagination li a{padding:8px 10px;color: #000;text-decoration:none;}";
        style += ".pagination .active a:hover{background:#277db9!important;color:#FFFFFF!important;}";
        style += ".pagination li a:hover{background:#FFFFFF;border-color:#277db9;color:#277db9;}";
        style += ".pagination .active a{background:#277db9!important;color:#FFFFFF!important;padding:8px 10px;}";
        style += ".pagination .disabled{cursor: not-allowed!important;color:#000!important;}";
        style += "</style>";
        return style;
    }

    public String buildPageHtml() {
        String html = "";
        html += this.style();
        html += "<ul class='pagination pagination-sm'>";
        html += this.prevHtml();
        html += this.pageHtml();
        html += this.nextHtml();
        html += "<li class='disabled'>共" + this.totalPage + "页</li>";
        html += "<li><a href='javascript:void(0)'>" + this.goPage() + "</a></li>";
        html += "</ul>";
        return html;
    }
}

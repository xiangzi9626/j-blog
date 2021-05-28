package cn.example.blog.bean;

import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.List;

public class Dtree {
    private Long id;
    private Long parentId;
    private String title;
    private boolean last;

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    private List<Dtree> children;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Dtree> getChildren() {
        return children;
    }

    public void setChildren(List<Dtree> children) {
        this.children = children;
    }
}

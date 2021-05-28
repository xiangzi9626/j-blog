package cn.example.blog.util;

import cn.example.blog.bean.Dtree;

import java.util.ArrayList;
import java.util.List;

public class DtreeUtil {

    public static List<Dtree> toTree(List<Dtree> treeList, Long pid) {
        List<Dtree> retList = new ArrayList<Dtree>();
        for (Dtree parent : treeList) {
            if (pid.equals(parent.getParentId())) {
                retList.add(findChildren(parent, treeList));
            }
        }
        return retList;
    }

    private static Dtree findChildren(Dtree parent, List<Dtree> treeList) {
        for (Dtree child : treeList) {
            if (parent.getId().equals(child.getParentId())) {
                if (parent.getChildren() == null) {
                    parent.setChildren(new ArrayList<>());
                }
                parent.getChildren().add(findChildren(child, treeList));
            }
        }
        return parent;
    }
}
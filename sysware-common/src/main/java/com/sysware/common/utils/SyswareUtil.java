package com.sysware.common.utils;

import com.sysware.common.core.domain.BaseEntity;
import com.sysware.common.helper.LoginHelper;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SyswareUtil {


    /**
     * 高亮显示查询结果
     * @param res   查询出的结果集
     * @param fields  需要高亮显示的字段集
     * @param params  需要高亮显示的值
     */
    public static  void  highLight(List<Map> res, List<String> fields, Map<String,Object> params){

        String searchContent = "";
        if(params.get("searchValue") !=null){
            searchContent = String.valueOf(params.get("searchValue"));
        }

        String[] searchArr = searchContent.split(" ");

        int i = 0;
        for (Map map:res) {
            fields.forEach(field -> {
                //搜索条件
                String search = String.valueOf(params.get(field));
                if(StringUtils.isNotBlank(search)){
                    String result = String.valueOf(map.get(field));
                    if(StringUtils.isNotBlank(result)) {
                        map.put(field+"Show", replaceSearch(result, search.trim()));
                    }
                }else{
                    map.put(field+"Show", map.get(field));
                }
                for (String s : searchArr) {
                    if(StringUtils.isNotBlank(s)){
                        String result = String.valueOf(map.get(field));
                        if(StringUtils.isNotBlank(result)){
                            map.put(field+"Show",replaceSearch(result,s.trim()));

                        }
                    }
                }
            });
            map.put("localIndex",i++);
        }
    }

    /**
     * 替换字符串（红色高亮）
     * @param resource 原字符
     * @param search 替换的字符
     * @return
     */
    private static String replaceSearch(String resource,String search){

        List<Integer> listIndex = new ArrayList<>();
        StringBuffer sb = new StringBuffer("");

        String rs = resource;
        int index = getIndex(rs, search);

        if(index==-1){
            return resource;
        }

        while (index!=-1){
            listIndex.add(index);
            rs = rs.substring(index+search.length(), rs.length());
            index = getIndex(rs, search);
        }


        for (int i = 0; i < listIndex.size(); i++) {
            String a1 =  resource.substring(0,listIndex.get(i));
            sb.append(a1);
            sb.append("<span style='color:red'>");
            String a2 =  resource.substring(listIndex.get(i),listIndex.get(i)+search.length());
            sb.append(a2);
            sb.append("</span>");
            resource =  resource.substring(listIndex.get(i)+search.length(),resource.length());
            if(i==listIndex.size()-1){
                sb.append(resource);
            }
        }
        return sb.toString();
    }

    /**
     * 获取字符所在的位置
     * @param resource  原字符
     * @param search  查询字符
     * @return
     */
    private static int getIndex(String resource,String search){
        return   resource.toLowerCase().indexOf(search.toLowerCase());
    }

    /**
     * 将下划线改为驼峰
     * @param res   查询出的结果集
     */
    public static  List<Map>  listToCamelCase(List<Map> res){
        List<Map> newResult = new ArrayList<>();
        res.forEach(map -> {
            Object children = map.get("children");
            if(children != null) {
                map.put("children", listToCamelCase( (List<Map>)children));
            }

            Map newMap = new HashMap();
            map.forEach((key,value) -> {

                newMap.put(StringUtils.toCamelCase(String.valueOf(key)), value);

            });
            newResult.add(newMap);
        });

        return newResult;

    }

    /**
     * 将下划线改为驼峰
     * @param res   查询出的结果集
     */
    public static  Map  toCamelCase(Map res){
        Object children = res.get("children");
        if(children != null) {
            res.put("children", listToCamelCase( (List<Map>)children));
        }

        Map newMap = new HashMap();
        res.forEach((key,value) -> {
            newMap.put(StringUtils.toCamelCase(String.valueOf(key)), value);

        });

        return newMap;

    }

    /**
     * 组装搜索字段
     * @param searchField
     * @return
     */
    public static List<String> getSearchFieldMap(Object searchField) {

        String search = SyswareUtil.ObjectToString(searchField);
        List<String> fields = new ArrayList<>();
        if(StringUtils.isNotBlank(search)){
            for (String prop : search.split(",")) {
                fields.add(prop);
            }
        }
        return fields;
    }

    /**
     * 组装搜索字段
     * @param searchField
     * @return
     */
    public static List<Map> getSearchFieldMap(List<String> searchField) {

        List<Map> fields = new ArrayList<>();
        if(searchField!= null && searchField.size()>0){
            for (String prop : searchField) {
                Map field = new HashMap();
                field.put("prop",prop);
                fields.add(field);
            }
        }
        return fields;
    }

    /**
     * 根据提供的列表和字段信息，构建树形结构的列表。
     * @param list 原始列表，其中每个元素是一个Map，包含有关条目的信息。
     * @param idStr 用于标识每个条目的字段名。
     * @param parentStr 用于标识每个条目父级的字段名。
     * @param parentId 指定根节点的父级ID。
     * @param hasChildren 是否添加 hasChildren 字段
     * @return 返回一个列表，其中包含根据指定的parentId构建的树形结构。
     */
    public static List<Map> getTreeMap(List<Map> list, String idStr, String parentStr, String parentId,Boolean hasChildren) {
        // 初始化一个空列表，用于存放结果
        List<Map> tList = new ArrayList<Map>();

        // 使用parallelStream和groupingBy按parentId对列表进行分组
        Map<String, List<Map>> collect = list.parallelStream().collect(Collectors.groupingBy(map -> String.valueOf(map.get(parentStr))));

        // 为原始列表的每个元素添加“children”字段，该字段包含其子元素
        list.forEach(val -> {
            val.put("children", collect.get(val.get(idStr)));
            if(hasChildren){
                val.put("hasChildren", collect.get(val.get(idStr)) != null);
            }
        });

        // 过滤出父ID与指定的parentId匹配的元素，形成最终返回的列表
        List<Map> returnList = list.stream().filter(val -> parentId.contains(String.valueOf(val.get(parentStr)))).collect(Collectors.toList());

        // 返回构建好的树形结构列表
        return returnList;
    }

    /**
     * 根据提供的列表和字段信息，构建树形结构的列表。
     * @param list 原始列表，其中每个元素是一个Map，包含有关条目的信息。
     * @param idStr 用于标识每个条目的字段名。
     * @param parentStr 用于标识每个条目父级的字段名。
     * @param parentList 指定根节点的父级集合。
     * @param hasChildren 是否添加 hasChildren 字段
     * @return 返回一个列表，其中包含根据指定的parentId构建的树形结构。
     */
    public static List<Map> getTreeMap(List<Map> list, String idStr, String parentStr, List<String> parentList,Boolean hasChildren) {
        // 初始化一个空列表，用于存放结果
        List<Map> tList = new ArrayList<Map>();

        // 使用parallelStream和groupingBy按parentId对列表进行分组
        Map<String, List<Map>> collect = list.parallelStream().collect(Collectors.groupingBy(map -> String.valueOf(map.get(parentStr))));

        // 为原始列表的每个元素添加“children”字段，该字段包含其子元素
        list.forEach(val -> {
            val.put("children", collect.get(val.get(idStr)));

            if(hasChildren){
                val.put("hasChildren", collect.get(val.get(idStr)) != null);
            }
        });

        // 过滤出父ID与指定的parentId匹配的元素，形成最终返回的列表
        List<Map> returnList = list.stream().filter(val -> parentList.contains(String.valueOf(val.get(parentStr)))).collect(Collectors.toList());

        // 返回构建好的树形结构列表
        return returnList;
    }
    /**
     * 判断给定的对象是否为空。
     * 本方法通过Optional对象的isPresent方法来判断对象是否为空，避免了直接使用null比较，提高了代码的可读性和安全性。
     *
     * @param obj 待检查的对象，可以是任何类型的对象。
     * @return 如果obj不为空，则返回false；如果obj为空，则返回true。
     */
    public  static  Boolean isEmpty(Object obj){
        if(obj == null){
            return true;
        }else if(obj instanceof String){
            return StringUtils.isBlank(String.valueOf(obj));
        }else if(obj instanceof String[]){
            return ((String[]) obj).length == 0 || ((String[]) obj)[0] == null;
        }else if(obj instanceof ArrayList){
            return ((ArrayList) obj).size() == 0 ;
        }else if(obj instanceof Map){
            return ((Map) obj).isEmpty();
        }

        return false;
    }


    /**
     * 获取数组中不连续的最小数字
     * @param numList 整数数组
     * @return
     */
    public static Long getListMinNum(List<Long> numList) {

        if(numList == null || numList.size()==0){
            return 1l;
        }

        Long expectedNumber = 1l;

        for (Long number : numList) {
            if (number != expectedNumber) {
                return expectedNumber;
            }
            expectedNumber++;
        }

        return expectedNumber;

    }

    /**
     * 设置创建人/时间和更新人/时间
     * @param entity
     */
    public static void setCreateAndUpdate(BaseEntity entity) {
        setCreate(entity);
        setUpdate(entity);
    }

    /**
     * 设置创建人/时间
     * @param entity
     */
    public static void setCreate(BaseEntity entity) {
        entity.setCreateBy(LoginHelper.getUserId());
    }

    /**
     * 设置创建人/时间和更新人/时间
     * @param entity
     */
    public static void setUpdate(BaseEntity entity) {
        entity.setUpdateBy(LoginHelper.getUserId());
    }

    /**
     * 获取字符串中的数字
     * @param str
     */
    public static Integer getNum(String str) {
        if(isEmpty(str)){
            return 0;
        }
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
            return Integer.valueOf(matcher.group());
        }
        return 0;
    }

    /**
     * Object类型转换为String类型
     * @param obj
     * @return
     */
    public static String ObjectToString(Object obj) {

        if(obj != null && !"null".equals(obj)){
            return String.valueOf(obj);
        }
        return "";
    }

    public static String formatDate(LocalDateTime approvalTime, String formatStr) {
        if(approvalTime == null){
            return "";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatStr);
        return approvalTime.format(formatter);
    }

    public static String formatDate(LocalDateTime approvalTime) {
        if(approvalTime == null){
            return "";
        }
        return formatDate(approvalTime,"yyyy-MM-dd");
    }

/*
    public void copyWithoutId(Object source, Object target) {
        try {
            // 获取源对象所有属性描述器
            PropertyDescriptor[] descriptors = BeanUtils.getPropertyDescriptors(source.getClass());
            for (PropertyDescriptor descriptor : descriptors) {
                // 排除ID属性
                if (!"id".equals(descriptor.getName())) {
                    // 将其他属性值拷贝到目标对象
                    BeanUtils.copyProperty(target, descriptor.getName(), descriptor.getReadMethod().invoke(source));
                }
            }
        } catch (Exception e) {
            // 处理异常
            e.printStackTrace();
        }
    }*/

}

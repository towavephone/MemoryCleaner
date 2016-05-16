package edu.wkd.towave.memorycleaner.tools;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by towave on 2016/5/15.
 */
public class ListSortUtil {
    /**
     * @param targetList 目标排序List
     * @param sortField 排序字段(实体类属性名)
     * @param sortMode 排序方式（asc or  desc）
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void sort(List<T> targetList, final String sortField, final String sortMode) {
        Collections.sort(targetList, new Comparator() {
            public int compare(Object obj1, Object obj2) {
                int retVal = 0;
                try {
                    //首字母转大写
                    String newStr = sortField.substring(0, 1).toUpperCase() +
                            sortField.replaceFirst("\\w", "");
                    String methodStr = "get" + newStr;

                    //Class<T> c1= (Class<T>) obj1.getClass();
                    //Method method1=c1.getMethod(methodStr,new Class[0]);
                    //Class<T> c2= (Class<T>) obj2.getClass();
                    //Method method2=c2.getMethod(methodStr,new Class[0]);

                    Method method1 = ((T) obj1).getClass()
                                               .getMethod(methodStr, new Class[0]);
                    Method method2 = ((T) obj2).getClass()
                                               .getMethod(methodStr, new Class[0]);
                    if (sortMode != null && "desc".equals(sortMode)) {
                        retVal = method2.invoke(((T) obj2), new Object[]{})
                                        .toString()
                                        .compareTo(
                                                method1.invoke(((T) obj1), new Object[]{})
                                                       .toString()); // 倒序
                    }
                    else {
                        retVal = method1.invoke(((T) obj1), new Object[]{})
                                        .toString()
                                        .compareTo(
                                                method2.invoke(((T) obj2), new Object[]{})
                                                       .toString()); // 正序
                    }
                } catch (Exception e) {
                    throw new RuntimeException();
                }
                return retVal;
            }
        });
    }
}

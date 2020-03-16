package lyd.git.aptannotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author linyingdong
 * @time 2020/3/16 9:12 AM
 * @describe 属性
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RepeaterField {

    /**
     * 方法名称
     *
     * @return
     */
    String name();

}

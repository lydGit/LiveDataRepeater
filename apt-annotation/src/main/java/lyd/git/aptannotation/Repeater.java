package lyd.git.aptannotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author linyingdong
 * @time 2020/3/16 9:13 AM
 * @describe 类
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface Repeater {
}

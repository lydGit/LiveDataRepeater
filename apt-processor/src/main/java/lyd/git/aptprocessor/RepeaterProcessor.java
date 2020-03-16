package lyd.git.aptprocessor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import lyd.git.aptannotation.Repeater;
import lyd.git.aptannotation.RepeaterField;

@AutoService(Processor.class)
public class RepeaterProcessor extends AbstractProcessor {

    /** 信息打印 */
    Messager messager;
    /** 元素工具类 */
    Elements elementUtils;
    /** 属性工具类 */
    Types typeUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        messager = processingEnvironment.getMessager();
        elementUtils = processingEnvironment.getElementUtils();
        typeUtils = processingEnvironment.getTypeUtils();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_7;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(Repeater.class.getCanonicalName());
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(Repeater.class);
        //循环被注解的元素
        for (Element element : elements) {
            //判断是否为类
            if (!(element instanceof TypeElement)) {
                continue;
            }
            TypeElement typeElement = (TypeElement) element;
            // 获取该类的全部成员，包括
            List<? extends Element> members = elementUtils.getAllMembers(typeElement);
            //类名称
            String className = element.getSimpleName() + "Repeater";
            //接口名称
            String interfaceName = "I" + className;
            //构造方法
            MethodSpec.Builder methodSpecBuilder = getMethod(getPackageName(typeElement), element.getSimpleName().toString());
            //接口
            TypeSpec.Builder iRepeaterBuilder = TypeSpec.interfaceBuilder(interfaceName).addModifiers(Modifier.PUBLIC);
            //lifecycle
            ClassName lifecycleObserver = ClassName.get("androidx.lifecycle", "Observer");
            for (Element item : members) {
                //忽略除了成员方法
                if (item instanceof ExecutableElement) {
                    continue;
                }
                // 检查是否有注解
                RepeaterField annotation = item.getAnnotation(RepeaterField.class);
                if (annotation == null) {
                    continue;
                }
                //注释中的方法名称
                String methodName = annotation.name();
                //方法中的数据类型
                TypeName dataType = getDataClass(item.asType());
                methodSpecBuilder.addCode(
                        "  model.$N.observe(owner, new $T<$T>() {\n" +
                                "      @Override\n" +
                                "      public void onChanged($T value) {\n" +
                                "          iRepeater.$N(value);\n" +
                                "      }\n" +
                                "  });\n", item.getSimpleName(), lifecycleObserver, dataType, dataType, methodName);
                //接口中的方法
                MethodSpec spec = MethodSpec.methodBuilder(methodName)
                        .addModifiers(Modifier.PUBLIC)
                        .addModifiers(Modifier.ABSTRACT)
                        .addParameter(dataType, "value").build();
                iRepeaterBuilder.addMethod(spec);
            }
            //主类
            TypeSpec repeater = TypeSpec
                    //类名
                    .classBuilder(className)
                    //公用类
                    .addModifiers(Modifier.PUBLIC)
                    //定量
                    .addModifiers(Modifier.FINAL)
                    .addMethod(methodSpecBuilder.build())
                    .addType(iRepeaterBuilder.build())
                    .build();
            //保存地址
            JavaFile javaFile = JavaFile.builder(getPackageName(typeElement), repeater)
                    .build();
            try {
                // 最后要将内容写入到 java 文件中，这里必须使用 processingEnv 中获取的 Filer 对象
                // 它会自动处理路径问题，我们只需要定义好包名类名和文件内容即可。
                Filer filer = processingEnv.getFiler();
                javaFile.writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // 返回值表示处理了 set 参数中包含的所有注解，不会再将这些注解移交给编译流程中的
        // 其他 Annotation Processor。一般都不会有多个 Annotation Processor，一般都写 true。
        return true;
    }

    /**
     * 获取包名
     * @param type
     * @return
     */
    private String getPackageName(TypeElement type) {
        return elementUtils.getPackageOf(type).getQualifiedName().toString();
    }

    /**
     * 获取数据类型
     * @param mirror
     * @return
     */
    private ClassName getDataClass(TypeMirror mirror) {
        String str = mirror.toString();
        Element element = typeUtils.asElement(mirror);
        str = str.replace(element.toString(), "");
        str = str.substring(1);
        str = str.substring(0, str.length() - 1);
        ClassName className = ClassName.bestGuess(str);
        return className;
    }

    /**
     * 构造方法
     *
     * @param packageName viewmodel所在包名称
     * @param simpleName
     * @return
     */
    private MethodSpec.Builder getMethod(String packageName, String simpleName) {
        //viewModel名称
        ClassName model = ClassName.get(packageName, simpleName);
        //lifecycle
        ClassName owner = ClassName.get("androidx.lifecycle", "LifecycleOwner");
        //接口
        ClassName iRepeater = ClassName.get("", "I" + simpleName + "Repeater");
        MethodSpec.Builder method = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(model, "model")
                .addParameter(owner, "owner")
                .addParameter(iRepeater, "iRepeater", Modifier.FINAL);
        return method;
    }

}

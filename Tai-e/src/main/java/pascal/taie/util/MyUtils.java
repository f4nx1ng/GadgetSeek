package pascal.taie.util;

import pascal.taie.World;
import pascal.taie.analysis.pta.core.cs.context.Context;
import pascal.taie.analysis.pta.core.cs.element.CSObj;
import pascal.taie.analysis.pta.plugin.taint.TaintManager;
import pascal.taie.analysis.pta.pts.PointsToSet;
import pascal.taie.ir.exp.Var;
import pascal.taie.ir.stmt.Invoke;
import pascal.taie.language.classes.ClassHierarchy;
import pascal.taie.language.classes.JClass;
import pascal.taie.language.classes.JMethod;
import pascal.taie.language.classes.Subsignature;
import pascal.taie.language.type.ArrayType;
import pascal.taie.language.type.ClassType;
import pascal.taie.language.type.Type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class MyUtils {

    public static String[] basicType = {"long", "byte", "short", "int", "float", "double", "char", "boolean", "long[]", "byte[]", "short[]", "int[]", "float[]", "double[]", "char[]", "boolean[]", "long[][]", "byte[][]", "short[][]", "int[][]", "float[][]", "double[][]", "char[][]", "boolean[][]"};
    public static List<String> list = Arrays.asList(basicType);

    /**
     * 该方法用于追溯污点数据类的所有子类
     * @param type 源数据类型
     * @param jMethod 源数据调用的目标方法
     * @param targetMethods 在处理processCall的时候，其处理逻辑是先获得变量然后再获取有关变量的invokes，所以涉及的方法会很多
     * 其他情况下不会使用到targetMethods变量。
     */
    public static Collection<Type> getAllTypes(Type type, JMethod jMethod, List<JMethod> targetMethods, boolean isTransfer){




        boolean flag;

        Collection<Type> expectType = expectClassJudge(type, jMethod);
        if(expectType != null){
            return expectType;
        }

        JClass jClass = null;
        //首先找到Serializeable，java.io.ObjectInput
        ClassHierarchy hierarchy = World.get().getClassHierarchy();
        JClass serializeInterface = hierarchy.getClass("java.io.Serializable");
        JClass objectInput = hierarchy.getClass("java.io.ObjectInput");
        JClass editor = hierarchy.getClass("java.beans.PropertyEditor");
        JClass context = hierarchy.getClass("javax.naming.Context");


        //输入参数是数组类型，那么就拿到其中的原始类
        if(type instanceof ArrayType arrayType){
            type = arrayType.elementType();
            if(type instanceof ArrayType arrayType2){
                type = arrayType2.elementType();
            }
        }

        if (type instanceof ClassType classType){
            jClass = classType.getJClass();
        }else{
            jClass = hierarchy.getClass(type.getName());
        }

        Collection<JClass> implementors = new ArrayList<>();
        Collection<JClass> removeclass = new ArrayList<>();
        Collection<Type> allType = new ArrayList<>();
        //获取类及其所有子类
//        if(jClass == null){
//            System.out.println("wtf");
//        }
        implementors.addAll(World.get().getClassHierarchy().getAllSubclassesOf(jClass));
        //处理内部类


        //先后关系,拿到内部类的主体




//        if(!isTransfer){
//            for (JClass implementor : implementors){
//                if (implementor.getOuterClass() == null){
//                    if (!hierarchy.isSubclass(serializeInterface, implementor) && !hierarchy.isSubclass(objectInput, implementor) && !hierarchy.isSubclass(context, implementor)){
//                        removeclass.add(implementor);
//                    }
//                }else{
//                    if(!hierarchy.isSubclass(serializeInterface, implementor.getOuterClass()) && !hierarchy.isSubclass(serializeInterface, implementor) && !hierarchy.isSubclass(objectInput, implementor.getOuterClass()) && !hierarchy.isSubclass(context, implementor)){
//                        removeclass.add(implementor);
//                    }
//                }
//            }
//        }


        //删去所有的editor


      for (JClass implementor : implementors){
            if (implementor.getOuterClass() == null){
                if (!hierarchy.isSubclass(serializeInterface, implementor) && !hierarchy.isSubclass(objectInput, implementor)){
                    removeclass.add(implementor);
                }
            }else{
                if(!hierarchy.isSubclass(serializeInterface, implementor.getOuterClass()) && !hierarchy.isSubclass(serializeInterface, implementor) && !hierarchy.isSubclass(objectInput, implementor.getOuterClass())){
                    removeclass.add(implementor);
                }
            }
        }






        //排除没有目标方法的类
//        if (targetMethods != null){
//            for (JClass implementor :implementors){
//                flag = false;
//                for (JMethod jMethod1 : targetMethods){
//                    if (implementor.getDeclaredMethod(jMethod1.getSubsignature()) != null){
//                        flag = true;
//                    }
//                }
//                if(!flag){
//                    removeclass.add(implementor);
//                }
//            }
//        }


        implementors.removeAll(removeclass);
//        implementors.removeIf(implementor -> (implementor.getInterfaces().contains(serializeInterface) ||
//                implementor.getInterfaces().contains(objectInput) ||
//                implementor.getName().contains("$")) == false);
        if (jClass.getName().equals("java.lang.Object")){
            implementors.remove(jClass);
        }

        //排除抽象类
        implementors.removeIf(JClass::isAbstract);


        implementors.forEach(implementor -> {allType.add(implementor.getType());});
//        allType.forEach(atype -> {
//            if(atype.getName().equals("org.apache.commons.collections.keyvalue.TiedMapEntry")){
//                System.out.println("find the type");
//            }
//        });

        return allType;
    }

    /**
     * 如果目标语句为$r.invoke语句，且invoke函数是构造方法的话，
     * 则不使用cha方式对目标对象类型进行遍历。
     * */

    public static Collection<Type> expectClassJudge(Type type, JMethod jMethod){
        Collection<Type> exceptType = new ArrayList<>();

        if (MyUtils.list.contains(type.getName())){
            exceptType.add(type);
            return exceptType;
        }else if(jMethod != null){
            if (jMethod.getName().equals("<init>")){
                exceptType.add(type);
                return exceptType;
            }
        }
//        else if (jMethodList != null && !jMethodList.isEmpty()) {
//            for (JMethod jMethod1 : jMethodList){
//                if (jMethod1.getName().equals("<init>")){
//                    exceptType.add(type);
//                }
//            }
//            return exceptType;
//        }
        return null;
    }

}

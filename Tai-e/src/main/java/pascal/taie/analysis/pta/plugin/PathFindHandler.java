package pascal.taie.analysis.pta.plugin;

import pascal.taie.World;
import pascal.taie.analysis.pta.core.cs.context.Context;
import pascal.taie.analysis.pta.core.cs.element.CSCallSite;
import pascal.taie.analysis.pta.core.cs.element.CSManager;
import pascal.taie.analysis.pta.core.cs.element.CSMethod;
import pascal.taie.analysis.pta.core.cs.element.CSObj;
import pascal.taie.analysis.pta.core.solver.Solver;
import pascal.taie.analysis.pta.plugin.taint.*;
import pascal.taie.analysis.pta.pts.PointsToSet;
import pascal.taie.ir.exp.*;
import pascal.taie.ir.proginfo.FieldRef;
import pascal.taie.ir.stmt.Invoke;
import pascal.taie.ir.stmt.LoadField;
import pascal.taie.ir.stmt.Stmt;
import pascal.taie.language.classes.ClassHierarchy;
import pascal.taie.language.classes.JClass;
import pascal.taie.language.classes.JMethod;
import pascal.taie.language.type.Type;
import pascal.taie.util.CSRCMethod;
import pascal.taie.util.MyInferenceV2;
import pascal.taie.util.graph.SimpleGraph;

import java.util.*;

import static pascal.taie.util.MyInferenceV2.*;
import static pascal.taie.util.ObjectStructGeneration.*;
import static pascal.taie.util.ObjectVisualizer.exportToDot;

public class PathFindHandler implements Plugin{
    private static Solver solver;
    private TaintConfig config = TaintConfig.EMPTY;
    private static CSManager csManager;
    private static String Empty = "empty";
    private static String NoAssociation = "No Association";
    private static String NoArg = "No Arg";

    private static String NoRecv = "No Recv";

    @Override
    public void setSolver(Solver solver) {
        this.solver = solver;
    }

    public static boolean hasTainted(Context context, Var var){

        TaintManager manager = new TaintManager(solver.getHeapModel());
        PointsToSet pointsToSet = solver.getCSManager().getCSVar(context, var).getPointsToSet();
        if(pointsToSet == null || pointsToSet.isEmpty()){
            return false;
        }
        for(CSObj csobj : pointsToSet.getObjects()){
            if (manager.isTaint(csobj.getObject())){
                return true;
            }
        }
        return false;
    }

    public static boolean judgeParams(Context context, List<Var> params){
        for(Var param : params){
            if(hasTainted(context, param)){
                return true;
            }
        }
        return false;
    }


    @Override
    public void onFinish() {
        config = TaintAnalysis.config;


        csManager = solver.getCSManager();
        List<JClass> jClassList = solver.getHierarchy().allClasses().toList();
        List<Source> sources  = config.sources();
        List<String> sourceMethod = new ArrayList<>();

        sources.forEach(source -> {
            if (source instanceof CallSource callSource){
                sourceMethod.add(callSource.method().getSignature());
            } else if (source instanceof ParamSource paramSource) {
                sourceMethod.add(paramSource.method().getSignature());
            }
        });

        //根据配置文件当中的sink信息找到sink方法，通过方法自底向上搜寻
        for(Sink sink : config.sinks()){
            solver.getCallGraph().reachableMethods().forEach(csMethod -> {
                if (csMethod.getMethod().getSignature().equals(sink.method().getSignature())){
                    findPaths(csMethod, 6, jClassList, sourceMethod);

                }
            });
        }

//        solver.getCallGraph().reachableMethods().forEach(csMethod -> {
//            if (csMethod.getMethod().getSignature().equals("<com.sun.syndication.feed.impl.ToStringBean: java.lang.String toString(java.lang.String)>")){
//                HashSet<List<String>> paths = findPaths(csMethod, 8, jClassList);
//                for (List<String> path : paths){
////                    if (path.get(4).equals("<com.sun.syndication.feed.impl.ObjectBean: int hashCode()>")){
//
////                    }
//                    System.out.println(path);
//                }
//                System.out.println(paths.size());
//
//            }
//        });
    }

    /**
     * 用于在路径当中获取方法名称
     * */

    private static String dealWithPath(List<String> path){
        String[] stringGroup = path.get(path.size() - 1).split(" ");
        String methodName = stringGroup[2].substring(0, 6);

        return methodName;
    }

    /**
     * 依赖于先验知识的绝对错误机制
     * 当前仅收录另一种函数错误，及多种类型错误，非反序列化类型错误
     * 绝对错误机制仅作用在source方法当中，因为source方法是路径的起始点，该方法的衍生分支越少，后续的需要查询的越少
     * 函数错误其一：HashMap.putVal(),想要从该函数调用其他对象的方法，必须满足静态hashCode的条件
     * 其他错误：不知道为什么会出现的类：java.lang.Object,java.util.Objects
     * */

    private static boolean absoluteFault(List<JMethod> path, JMethod currentMethod, List<JClass> jClassList, CSCallSite csCallSite){
        //debug code

        //todo 路径上的非反序列化减枝

//        ClassHierarchy hierarchy = World.get().getClassHierarchy();
//        JClass serializeInterface = hierarchy.getClass("java.io.Serializable");

        if (currentMethod.getName().equals("putVal")) {
            String[] stringGroup = path.get(path.size() - 2).getSignature().split(":");
            String className = stringGroup[0].substring(1);
            for (JClass jClass : jClassList) {
                if (jClass.getName().equals(className)) {
                    JMethod jmethod = jClass.getDeclaredMethod("hashCode");
                    if (jmethod == null) {
                        return true;
                    }
                    Var thisVar = jmethod.getIR().getThis();

                    for (Stmt stmt : jmethod.getIR().getStmts()) {
                        //在hashCode方法当中不能出现x =this.y，如果出现则证明hash的结果是动态的
                        if (stmt instanceof LoadField lf && lf.getRValue() instanceof InstanceFieldAccess instanceFieldAccess) {
                            Var base = instanceFieldAccess.getBase();
                            if (base.equals(thisVar)) {
                                return true;
                            }
                            //在hashCode方法当中不能出现x = this.function()，如果出现则证明hash的结果是动态的
                        } else if (stmt instanceof Invoke invoke && invoke.getRValue() instanceof InvokeInstanceExp invokeInstanceExp) {
                            Var base2 = invokeInstanceExp.getBase();
                            if (base2.equals(thisVar)) {
                                return true;
                            }
                        }
                    }

                }
            }
        }
        //路径上有关Object都删掉
        if (currentMethod.getDeclaringClass().getName().equals("java.lang.Object") || currentMethod.getDeclaringClass().getName().equals("java.util.Objects") || currentMethod.getDeclaringClass().getName().equals("java.io.ObjectInputStream")) {
            return true;
        }

        //我们只能获得当前的前一个方法的callsite，所以只能判断前面一个是否为需要裁剪对象
//        CSRCMethod csrcMethod = new CSRCMethod(null);
//        getRecvOfCSRCMethod(csCallSite, currentMethod, csrcMethod);
//
//
//        //如果他不是方法内部出现的接收者对象（临时的或者是静态的）
//        if (path.size() > 2 && csrcMethod.getRecvAssociation().contains("field") || csrcMethod.getRecvAssociation().contains("param")) {
//            if (!hierarchy.isSubclass(serializeInterface, path.get(path.size() - 2).getDeclaringClass())){
//                return true;
//            }
//        }

        //this的上下级不同问题
        if(csCallSite != null && csCallSite.getCallSite().getRValue() instanceof InvokeInstanceExp invokeInstanceExp && invokeInstanceExp.getBase().getName().equals("%this")){
            if(!path.get(path.size() - 1).getDeclaringClass().equals(path.get(path.size() - 2).getDeclaringClass())){
                return true;
            }
        }
        //删除重复出现三次一样方法名的路径，不是同一类的同一方法，而是相同方法名
        if(path.size() > 3 && path.get(path.size() - 3).getSubsignature().equals(currentMethod.getSubsignature()) && path.get(path.size() - 2).getSubsignature().equals(currentMethod.getSubsignature())){
            //
            return true;
        }


        //高价值利用连搜索（简易版）
//        if(path.size() > 2 && currentMethod.getName().equals(path.get(path.size() - 2).getName())){
//            return true;
//        }

        //同一方法出现两次
        for(int i=0; i < path.size() - 1; i++){
            if(path.get(i).equals(currentMethod)){
                return true;
            }
        }



        return false;
    }

    /**
     * 该方法用于在当前环境中通过污点分析输出的结果，自底向上的搜寻潜在的可用路径
     * @param csMethod 该方法就是sink方法，通过该方法为起点迭代搜寻
     * @param maxdepth 最大搜索深度
     * @param jClassList 当前所有类
     * @param sourceMethod 源头方法
     * */

    private void findPaths(CSMethod csMethod, int maxdepth, List<JClass> jClassList, List<String> sourceMethod) {


        HashMap result = new HashMap<>();

        if (csMethod == null) return;

        Stack<Pair> stack = new Stack<>();
        stack.push(new Pair(csMethod, new ArrayList<>(), 0, null,  new ArrayList<>()));

        while (!stack.isEmpty()) {
            Pair current = stack.pop();
            CSMethod node = current.node;
            List<JMethod> path = current.path;
            List<CSCallSite> callSites = current.callSites;

            CSCallSite csCallSite = current.csCallSite;

            int depth = current.depth;

//            if(node.getMethod().getSignature().equals("<org.aspectj.weaver.tools.cache.SimpleCache$StoreableCachingMap: java.lang.Object put(java.lang.Object,java.lang.Object)>")){
//                System.out.println("hh");
//            }

            if (depth > maxdepth){
                continue;
            }

            path.add(node.getMethod());
            callSites.add(csCallSite);


            //增设path不为空，有些路径直接扼杀
            if( path.size() >= 2 && absoluteFault(path, node.getMethod(), jClassList, callSites.get(callSites.size() - 1))){
                continue;
            }

//            if( path.size() >= 2){
//                continue;
//            }

            //solver.getCallGraph().getCallersOf(node).isEmpty()
            if (sourceMethod.contains(path.get(path.size() - 1).getSignature())) {

                result.put(new ArrayList<>(path),new ArrayList<>(callSites));
                continue;
            }else {

                for (CSCallSite childcallsite : solver.getCallGraph().getCallersOf(node)) {
                    CSMethod dealMethod = childcallsite.getContainer();
                    Context context = dealMethod.getContext();
                    Var thisVar = dealMethod.getMethod().getIR().getThis();
                    List<Var> params = dealMethod.getMethod().getIR().getParams();


                    if ((context != null && thisVar != null && hasTainted(context, thisVar)) || dealMethod.getMethod().isStatic() || (context != null && params != null && !params.isEmpty() && judgeParams(context, dealMethod.getMethod().getIR().getParams()))){
                        //我们不希望让他搜索两次同名方法
                        if (!childcallsite.getContainer().getMethod().equals(node.getMethod())){
                            stack.push(new Pair(childcallsite.getContainer(), new ArrayList<>(path), depth + 1, childcallsite, new ArrayList<>(callSites)));
                        }
                    }
                }
            }

        }

        printResult(result);
    }

    public static boolean judgeContainer(List<JMethod> containers, List<CSRCMethod> csrcMethods){
        ClassHierarchy hierarchy = World.get().getClassHierarchy();
        JClass serializeInterface = hierarchy.getClass("java.io.Serializable");

        for(int i = csrcMethods.size() - 1; i > 0; i--){
            if(containers.get(i).isAbstract() || hierarchy.isSubclass(serializeInterface, containers.get(i).getDeclaringClass())){
                String recvAssociation = csrcMethods.get(i).getRecvAssociation();
                if(!recvAssociation.equals(Empty) && !recvAssociation.equals(NoAssociation)){
                    return false;
                }else {
                    return true;
                }

            }
        }
        return true;
    }


    /**
     * 用于测试打印
     *
     * */

    public void printResult(HashMap<List<JMethod>,List<CSCallSite>> result){



        int count = 0;
        for (Map.Entry<List<JMethod>,List<CSCallSite>> entry : result.entrySet()){

            List<CSCallSite> callSites = entry.getValue();
            List<JMethod> containers = entry.getKey();
            List<CSRCMethod> csrcMethods = new ArrayList<>();

            //judgeContainers

//            System.out.println(callSites);
            System.out.println(containers);
            for (int i = 1; i < callSites.size(); i++){
                CSRCMethod csrcMethod = getArgOfCSRCMethod(callSites.get(i), containers.get(i));
                getRecvOfCSRCMethod(callSites.get(i), containers.get(i), csrcMethod);
                csrcMethods.add(csrcMethod);
                //System.out.print("[" + "RecvAssociation: " + csrcMethod.getRecvAssociation() + "; ParamAssociation: " + csrcMethod.getParamAssociation() + " ]"+ ", ");
            }

            if(judgeContainer(containers, csrcMethods)){
                System.out.println(containers);
                for(int j = 0; j < csrcMethods.size(); j++){
                    CSRCMethod csrcMethod2 = csrcMethods.get(j);
                    System.out.print("[" + "RecvAssociation: " + csrcMethod2.getRecvAssociation() + "; ParamAssociation: " + csrcMethod2.getParamAssociation() + " ]"+ ", ");
                }

//                SimpleGraph ObjectStruct = null;
                String filename = "D:\\毕设研究\\Tai-e\\output\\object.dot";
//
                SimpleGraph ObjectStruct = getTheStructOfObjectTrulyV2(csrcMethods, containers);
                exportToDot(ObjectStruct, filename);

                System.out.print("\n");
                count++;
            }

        }
        System.out.println(count);
    }

    /**
     * 测试代码段
     * */
    public static boolean judge(List<JMethod> containers){
        if(containers.get(0).getDeclaringClass().getName().equals("com.sun.syndication.feed.impl.ToStringBean") && containers.get(1).getDeclaringClass().getName().equals("com.sun.syndication.feed.impl.ToStringBean")){
            if(containers.get(2).getDeclaringClass().getName().equals("com.sun.org.apache.xpath.internal.objects.XString") && containers.get(3).getDeclaringClass().getName().equals("org.springframework.aop.target.HotSwappableTargetSource")){
                return true;
            }
        }
        return false;
    }


    /**
     * 通过输入的callsite构造相应的csrcmethod并返回
     *  但是我们知道所有调用分为三类invokestatic, invokespecial, (invokeinterface, invokevirtual)
     * */

    public static void getRecvOfCSRCMethod(CSCallSite csCallSite, JMethod container, CSRCMethod csrcMethod){

        //如果调用点为空则返回空集
        if (csCallSite == null){
            csrcMethod.setRecvAssociation(Empty);
            return;
        }
        Invoke invoke = csCallSite.getCallSite();

        InvokeExp invokeExp = invoke.getRValue();
        //如果参数为空，该方法没有参数
        if (invokeExp instanceof InvokeStatic){
            csrcMethod.setRecvAssociation(Empty);
            return;
        } else if (invokeExp instanceof InvokeInstanceExp invokeInstanceExp) {
            Var var = invokeInstanceExp.getBase();
            boolean anew = isNew(var, container, csCallSite);
            FieldRef resultThis = findFieldAssociationTruly(var, container, container.getIR().getThis(), csCallSite, true);
            FieldRef resultRealThis = findFieldAssociationTrulyFromRealParam(var, container, container.getIR().getThis(), csCallSite, true);

            if(anew){
                csrcMethod.setRecvAssociation(NoAssociation);
                return;
            }

            //是不是与this本体有关
            if(resultThis != null){
                csrcMethod.setRecvAssociation("this field: " + resultThis);
                return;
            } else if (resultRealThis != null) {
                csrcMethod.setRecvAssociation("this field: " + resultRealThis);
                return;
            } else if (var.equals(container.getIR().getThis())) {
                csrcMethod.setRecvAssociation("this");
                return;
            } else if (container.getIR().getThis() != null && findFormalParameterAssociation(var, container, container.getIR().getThis(), csCallSite)) {
                //与this有关但没有显式表明field，且不是this本身，总的来说就是通过函数形成的关联
                csrcMethod.setRecvAssociation("inner this");
                return;
            } else{
                List<Var> argList = container.getIR().getParams();
                for(int i = 0; i < argList.size(); i++){
                    Var paramVar = argList.get(i);
                    FieldRef resultParamField = findFieldAssociationTruly(var, container, paramVar, csCallSite, false);
                    FieldRef resultRealParamField = findFieldAssociationTrulyFromRealParam(var, container, paramVar, csCallSite, false);
                    if(resultParamField != null){
                        csrcMethod.setRecvAssociation("param " + i + " field: " + resultParamField);
                        return;
                    } else if (resultRealParamField != null) {
                        csrcMethod.setRecvAssociation("param " + i + " field: " + resultRealParamField);
                        return;
                    } else if(findFormalParameterAssociation(var, container, paramVar, csCallSite)){
                        csrcMethod.setRecvAssociation("param " + i);
                        return;

                    }
                }

            }
        }

        csrcMethod.setRecvAssociation(NoAssociation);
    }

    /**
     * 推理paramAssociation
     * */
    public static CSRCMethod getArgOfCSRCMethod(CSCallSite csCallSite, JMethod container){

        List<String> paramClass = new ArrayList<>();
        //如果调用点为空则返回空集
        if (csCallSite == null){
            paramClass.add(Empty);
            return new CSRCMethod(paramClass);
        }
        Invoke invoke = csCallSite.getCallSite();

        InvokeExp invokeExp = invoke.getRValue();
        //如果参数为空，该方法没有参数
        if (invokeExp.getArgs().isEmpty()){
            paramClass.add(NoArg);
            return new CSRCMethod(paramClass);
        }

        for (Var var : invokeExp.getArgs()){
            //记录是否改变过
            boolean changed = false;
            boolean anew = isNew(var, container, csCallSite);

            if(anew){
                paramClass.add(NoAssociation);
                continue;
            }

            FieldRef resultThis = findFieldAssociationTruly(var, container, container.getIR().getThis(), csCallSite, false);
            FieldRef resultRealThis = findFieldAssociationTrulyFromRealParam(var, container, container.getIR().getThis(), csCallSite, false);
            if(resultThis != null){
                paramClass.add("this field: " + resultThis);
                changed = true;
                continue;
            } else if (resultRealThis != null) {
                paramClass.add("this field: " + resultRealThis);
                changed = true;
                continue;
            } else if (var.equals(container.getIR().getThis())) {
                paramClass.add("this");
                changed = true;
                continue;
            } else if (container.getIR().getThis() != null && findFormalParameterAssociation(var, container, container.getIR().getThis(), csCallSite)) {
                paramClass.add("inner this");
                changed = true;
                continue;
            } else{
                List<Var> argList = container.getIR().getParams();
                for(int i = 0; i < argList.size(); i++){
                    Var paramVar = argList.get(i);
                    FieldRef resultParamField = findFieldAssociationTruly(var, container, paramVar, csCallSite, false);
                    FieldRef resultRealParamField = findFieldAssociationTrulyFromRealParam(var, container, paramVar, csCallSite, false);
                    if(resultParamField != null){
                        paramClass.add("param " + i + " field: " + resultParamField);
                        changed = true;
                        break;
                    }else if (resultRealParamField != null) {
                        paramClass.add("param " + i + " field: " + resultRealParamField);
                        changed = true;
                        break;
                    }else if(findFormalParameterAssociation(var, container, paramVar, csCallSite)){
                        paramClass.add("param " + i);
                        changed = true;
                        break;
                    }
                }

            }
            if (changed == false){
                paramClass.add(NoAssociation);
            }
        }
        return new CSRCMethod(paramClass);
    }


    /**
     * 获取目标污点对象的类型
     * 当前认为无需判断类的继承关系，因为只要目标对象能留到这里就是有原因的
     * 但是还有可能是很多同类继承下的子类。
     * */
//    public static Set<String> getTaintClass(PointsToSet pts){
//        Set<String> declaredClass = new HashSet<>();
//        if(pts == null || pts.isEmpty()){
//            return declaredClass;
//        }
//        for(CSObj csObj : pts.getObjects()){
//            if(csObj.getObject().getDeclareClassName() != null){
//                declaredClass.addAll(csObj.getObject().getDeclareClassName());
//            }
//        }
//        return declaredClass;
//    }


    static class Pair {
        CSMethod node;

        List<JMethod> path;
        List<CSCallSite> callSites;


        CSCallSite csCallSite;
        int depth;

        Pair(CSMethod node, List<JMethod> path, int depth, CSCallSite csCallSite, List<CSCallSite> callSites) {
            this.node = node;
            this.path = path;
            this.depth = depth;
            this.csCallSite = csCallSite;
            this.callSites = callSites;
        }
    }


}

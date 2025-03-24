package pascal.taie.analysis.pta.plugin;

import pascal.taie.World;
import pascal.taie.analysis.pta.core.cs.context.Context;
import pascal.taie.analysis.pta.core.cs.element.*;
import pascal.taie.analysis.pta.core.heap.Obj;
import pascal.taie.analysis.pta.core.solver.Solver;
import pascal.taie.analysis.pta.plugin.taint.*;
import pascal.taie.analysis.pta.pts.PointsToSet;
import pascal.taie.config.Options;
import pascal.taie.ir.exp.*;
import pascal.taie.ir.proginfo.FieldRef;
import pascal.taie.ir.stmt.*;
import pascal.taie.language.classes.JClass;
import pascal.taie.language.classes.JMethod;

import javax.swing.TransferHandler;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;
import pascal.taie.util.MyInference;
import picocli.CommandLine;

import static pascal.taie.util.MyInference.*;


/**
 * 专门用于测试的handler，用于添加在各个阶段的测试代码
 *
 * */
public class TestHandler implements Plugin {
    private static Solver solver;
    private TaintConfig config = TaintConfig.EMPTY;


    @Override
    public void setSolver(Solver solver) {
        this.solver = solver;
    }

    @Override
    public void onStart() {
//        World.get().getClassHierarchy().allClasses().forEach(jClass -> {
//            if(jClass.getName().equals("org.springframework.aop.target.HotSwappableTargetSource")){
//                JMethod jMethod = jClass.getDeclaredMethod("equals");
//
//                jMethod.getIR().getStmts().forEach(stmt -> {
//                    System.out.printf("%s\n", stmt);
////                    if(stmt instanceof Invoke invoke && invoke.getRValue() instanceof InvokeInstanceExp invokeInstanceExp){
////                        Var var = invokeInstanceExp.getBase();
////                        if (var.getName().equals("$r9")){
////                            System.out.printf("%s, %s, %s\n", stmt, var, invokeInstanceExp.getMethodRef().resolve().getName());
////                        }
////
////                    }
//                });
//            }
//        });

//        solver.getCallGraph().reachableMethods().forEach(csMethod -> {
//            if (csMethod.getMethod().getDeclaringClass().getName().equals("org.aspectj.weaver.tools.cache.SimpleCache$StoreableCachingMap")){
//              System.out.printf("%s, %s\n",csMethod.getMethod().getName(), csMethod.getMethod().getReturnType());
//                csMethod.getMethod().getIR().getStmts().forEach(stmt -> {
//                    System.out.printf("%s\n", stmt);
//
//                });
//
//            }
//        });
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

//    @Override
//    public void onPhaseFinish() {
//
//        TaintManager manager = new TaintManager(solver.getHeapModel());
//        CSManager csManager = solver.getCSManager();
//
//        solver.getCallGraph().reachableMethods().forEach(csMethod -> {
//            if (csMethod.getMethod().getDeclaringClass().getName().equals("org.apache.commons.collections.map.LazyMap") && csMethod.getMethod().getName().equals("get")) {
//                Var thisVar = csMethod.getMethod().getIR().getThis();
//                csMethod.getMethod().getIR().getStmts().forEach(stmt -> {
//                    //testField(stmt, csManager, csMethod);
////                    System.out.println(stmt);
//                    //                  System.out.println(csManager.getCSVar(csMethod.getContext(), thisVar).getPointsToSet());
//
//                    if (stmt instanceof Invoke invoke && invoke.getRValue() instanceof InvokeInstanceExp invokeInstanceExp) {
//                        if(invokeInstanceExp.getBase().getName().equals("$r5")){
////                            System.out.println(csManager.getCSVar(csMethod.getContext(), invoke.getRValue().getArg(0)).getPointsToSet());
//                            if (csManager.getCSVar(csMethod.getContext(), invokeInstanceExp.getBase()).getPointsToSet() != null){
////                                System.out.println(invokeInstanceExp.getArg(0));
////                                System.out.println(csManager.getCSVar(csMethod.getContext(), invokeInstanceExp.getArg(0)).getPointsToSet());
//                                System.out.println(invokeInstanceExp.getBase());
//                                System.out.println(csManager.getCSVar(csMethod.getContext(), invokeInstanceExp.getBase()).getPointsToSet());
//                                //System.out.println(invoke.getMethodRef().resolve());
//                            }
//
////                            CSCallSite csCallSite = csManager.getCSCallSite(csMethod.getContext(), invoke);
////                            findFormalParameterAssociation(invokeInstanceExp.getBase(), csMethod.getMethod(), csMethod.getMethod().getIR().getThis(), csCallSite);
////                            System.out.println("hh");
//                        }
////                        if (invoke.getLValue()!=null && invoke.getLValue().getName().equals("$r2")){
////                            System.out.println(stmt);
////                            System.out.println(csManager.getCSVar(csMethod.getContext(), invoke.getLValue()).getPointsToSet());
//////                            if (csManager.getCSVar(csMethod.getContext(), invoke.getLValue()).getPointsToSet() != null){
//////                                System.out.println(csManager.getCSVar(csMethod.getContext(), invokeInstanceExp.getBase()).getPointsToSet());
//////                            }
////                        }
//                    }
//
//                });
//            }
//        });
//
//
//
//
//////                    if(stmt1 instanceof Invoke invoke && (invoke.isVirtual() || invoke.isInterface()) && invoke.getRValue() instanceof InvokeInstanceExp invokeInstanceExp){
//////                        Var var = invokeInstanceExp.getBase();
//////                        Context context = csMethod.getContext();
//////                        PointsToSet pts = solver.getCSManager().getCSVar(context, var).getPointsToSet();
//////                        System.out.printf("%s, %s, %s\n", stmt1, var, pts);
//////                        if(pts!= null){
//////                            for (CSObj csObj : pts.getObjects()){
//////                                System.out.printf("%s\n", csObj.getObject().getImplicited());
//////                            }
//////                        }
//////                    }
////            }
////        });
//
////        solver.getCallGraph().reachableMethods().forEach(csMethod -> {
////            if (csMethod.getMethod().getDeclaringClass().getName().equals("java.util.PriorityQueue") && csMethod.getMethod().getName().equals("heapify")) {
//////                Var thisVar = csMethod.getMethod().getIR().getThis();
//////                Context context = csMethod.getContext();
//////                System.out.printf("%s\n", csManager.getCSVar(context, thisVar).getPointsToSet());
////                csMethod.getMethod().getIR().getStmts().forEach(stmt -> {
////                    System.out.printf("%s\n", stmt);
////                    if (stmt instanceof LoadField lf && lf.getRValue() instanceof InstanceFieldAccess instanceFieldAccess){
////                        Var var = lf.getLValue();
////                        Context context = csMethod.getContext();
////                        PointsToSet pts = solver.getCSManager().getCSVar(context, var).getPointsToSet();
////                        System.out.printf("%s, %s, %s\n", stmt, var, pts);
////                    }
//////                    if(stmt instanceof Invoke invoke && (invoke.isVirtual() || invoke.isInterface()) && invoke.getRValue() instanceof InvokeInstanceExp invokeInstanceExp){
//////                        Var var = invokeInstanceExp.getBase();
//////                        Context context = csMethod.getContext();
//////                        PointsToSet pts = solver.getCSManager().getCSVar(context, var).getPointsToSet();
//////                        if (var.getName().equals("$r2")){
//////                            System.out.printf("%s, %s, %s, %s\n", stmt, var, pts, invokeInstanceExp.getMethodRef());
//////                        }
//////
//////                    }
////                });
////            }
////        });
//    }

//    @Override
//    public void onFinish(){
//        TaintManager manager = new TaintManager(solver.getHeapModel());
//        CSManager csManager = solver.getCSManager();
//
//        solver.getCallGraph().reachableMethods().forEach(csMethod -> {
//            if (csMethod.getMethod().getDeclaringClass().getName().equals("org.apache.commons.collections.map.LazyMap") && csMethod.getMethod().getName().equals("get")) {
//                Var thisVar = csMethod.getMethod().getIR().getThis();
//            }
//        });
//
//    }




//    public String findAssociation(Var var, JMethod container){
//
//        String result;
//
//        Var thisVar = container.getIR().getThis();
//
//        if (var == null) return null;
//
//        // 使用栈来存储待访问的节点
//        Stack<Var> stack = new Stack<>();
//        stack.push(var);
//
//        while (!stack.isEmpty()) {
//            Var current = stack.pop();
//
//            //访问当前节点
//            if (!var.getLoadFields().isEmpty()){
//                for (LoadField lf : var.getLoadFields()){
//                    if (lf.getRValue() instanceof InstanceFieldAccess instanceFieldAccess && instanceFieldAccess.getBase().equals(thisVar)){
//                        result = instanceFieldAccess.getFieldRef().getName();
//                        return result;
//                    }
//                }
//            }
//
//            // 将子节点逆序压栈（保证从左到右的遍历顺序）
//
//        }
//
//        return result;
//    }

    public FieldRef findAssociation(Var var, JMethod container){



        List<Stmt> stmtGroup = new ArrayList<>(container.getIR().getStmts());

        Var thisVar = container.getIR().getThis();

        Stack<Node> stack = new Stack<>();

        //初始化栈，如果一开始的赋值与我们的目标变量不同，就将这些变量压栈

        if (thisVar.getLoadFields().isEmpty()){
            return null;
        }else {
            for(LoadField lf : thisVar.getLoadFields()){
                //为了节省后续的搜索次数，我们需要做出“减枝”
                stmtGroup.remove(lf);
                Var var1 = lf.getLValue();
                String fieldname = lf.getRValue().getFieldRef().getName();
                FieldRef fieldRef = lf.getRValue().getFieldRef();

                if(var1.equals(var)){
                    return fieldRef;
                }else{
                    stack.push(new Node(var1, fieldname, fieldRef));
                }
            }
        }

        while (!stack.isEmpty()){

            Node current = stack.pop();


            //访问当前节点
            for(Stmt stmt : stmtGroup){
                if (stmt instanceof AssignStmt<?,?> assignStmt){
                    if(assignStmt instanceof StoreArray sa){
                        //先看右边再看左边
                        //此时确认右边的变量为我们当前处理的变量
                        if (sa.getRValue().equals(current.var)){
                            //减负
                            //如果目标变量就是我们要找的变量
                            Var lvar = sa.getLValue().getBase();
                            if(lvar.equals(var)){
                                return current.fieldRef;
                            } else {
                                stack.push(new Node(lvar, current.fieldname, current.fieldRef));
                            }
                        }

                    } else if (assignStmt instanceof Cast cast) {
                        //先看右边再看左边
                        //此时确认右边的变量为我们当前处理的变量
                        if (cast.getRValue().getValue().equals(current.var)){
                            //减负
                            Var lvar = cast.getLValue();
                            if(lvar.equals(var)){
                                return current.fieldRef;
                            } else {
                                stack.push(new Node(lvar, current.fieldname, current.fieldRef));
                            }
                        }

                    } else if (assignStmt instanceof Copy copy) {
                        if (copy.getRValue().equals(current.var)){
                            //减负
                            Var lvar = copy.getLValue();
                            if(lvar.equals(var)){
                                return current.fieldRef;
                            } else {
                                stack.push(new Node(lvar, current.fieldname, current.fieldRef));
                            }
                        }

                    } else if (assignStmt instanceof InstanceOf insf) {
                        if (insf.getRValue().getValue().equals(current.var)){
                            //减负
                            Var lvar = insf.getLValue();
                            if(lvar.equals(var)){
                                return current.fieldRef;
                            } else {
                                stack.push(new Node(lvar, current.fieldname, current.fieldRef));
                            }
                        }
                    } else if (assignStmt instanceof StoreField sf) {
                        if (sf.getRValue().equals(current.var)){
                            if (sf.getLValue() instanceof InstanceFieldAccess instanceFieldAccess){
                                Var lvar = instanceFieldAccess.getBase();
                                if(lvar.equals(var)){
                                    return current.fieldRef;
                                } else {
                                    stack.push(new Node(lvar, current.fieldname, current.fieldRef));
                                }
                            }

                        }
                    }
                } else if (stmt instanceof Invoke invoke) {
                    //没有处理dynamic invoke
                    if (invoke.getRValue() instanceof InvokeInstanceExp invokeInstanceExp){
                        //先看右边再看左边
                        //此时确认右边的变量为我们当前处理的变量
                        if (invokeInstanceExp.getBase().equals(current.var) || invokeInstanceExp.getArgs().contains(current.var)){
                            Var lvar = invoke.getLValue();
                            if(lvar.equals(var)){
                                return current.fieldRef;
                            } else {
                                stack.push(new Node(lvar, current.fieldname, current.fieldRef));
                            }
                        }

                    } else if (invoke.getRValue() instanceof InvokeStatic invokeStatic) {
                        if (invokeStatic.getArgs().contains(current.var)){
                            Var lvar = invoke.getLValue();
                            if(lvar.equals(var)){
                                return current.fieldRef;
                            } else {
                                stack.push(new Node(lvar, current.fieldname, current.fieldRef));
                            }
                        }
                    }
                }
            }

            if (stmtGroup.isEmpty()){
                break;
            }

            //节点访问结束

        }
        return null;
    }

    static class Node {
        Var var;
        String fieldname;
        FieldRef fieldRef;

        Node(Var var, String fieldname, FieldRef fieldRef){
            this.var = var;
            this.fieldname = fieldname;
            this.fieldRef = fieldRef;
        }
    }







    @Override
    public void onFinish() {

        TaintManager manager = new TaintManager(solver.getHeapModel());
        CSManager csManager = solver.getCSManager();
        Options options = World.get().getOptions();



//        solver.getCallGraph().reachableMethods().forEach(csMethod -> {
//            if (csMethod.getMethod().getName().equals("toString") && csMethod.getMethod().getDeclaringClass().getName().equals("com.sun.syndication.feed.impl.ToStringBean") && csMethod.getMethod().getParamCount() == 1) {
////                Var thisVar = csMethod.getMethod().getIR().getThis();
////                System.out.println(csManager.getCSVar(csMethod.getContext(), thisVar).getPointsToSet());
//                csMethod.getMethod().getIR().getStmts().forEach(stmt -> {
//                    //testField(stmt, csManager, csMethod);
//                    System.out.println(stmt);
////                    System.out.println(stmt.getClass());
////                    if (stmt.getDef().get() instanceof Var var && var.getName().equals("$r4") ){
////                        System.out.println(stmt);
////                        System.out.println(stmt.getClass());
////                    }
//
//
////                    if (stmt instanceof Invoke invoke && invoke.getRValue() instanceof InvokeInstanceExp invokeInstanceExp) {
////                        if(invokeInstanceExp.getBase().getName().equals("$r1")){
//////                            System.out.println(csManager.getCSVar(csMethod.getContext(), invoke.getRValue().getArg(0)).getPointsToSet());
////                            if (csManager.getCSVar(csMethod.getContext(), invokeInstanceExp.getBase()).getPointsToSet() != null){
//////                                System.out.println(invokeInstanceExp.getArg(0));
//////                                System.out.println(csManager.getCSVar(csMethod.getContext(), invokeInstanceExp.getArg(0)).getPointsToSet());
////                                System.out.println(invokeInstanceExp.getBase());
////                                System.out.println(csManager.getCSVar(csMethod.getContext(), invokeInstanceExp.getBase()).getPointsToSet());
////                                //System.out.println(invoke.getMethodRef().resolve());
////                            }
////
//////                            CSCallSite csCallSite = csManager.getCSCallSite(csMethod.getContext(), invoke);
//////                            findFormalParameterAssociation(invokeInstanceExp.getBase(), csMethod.getMethod(), csMethod.getMethod().getIR().getThis(), csCallSite);
//////                            System.out.println("hh");
////                        }
//////                        if (invoke.getLValue()!=null && invoke.getLValue().getName().equals("$r3")){
//////                            System.out.println(stmt);
//////                            System.out.println(invoke.getMethodRef());
//////                            System.out.println(csManager.getCSVar(csMethod.getContext(), invoke.getLValue()).getPointsToSet());
////////                            if (csManager.getCSVar(csMethod.getContext(), invoke.getLValue()).getPointsToSet() != null){
////////                                System.out.println(csManager.getCSVar(csMethod.getContext(), invokeInstanceExp.getBase()).getPointsToSet());
////////                            }
//////                        }
////                    }
//
//                });
//            }
//        });
//                World.get().getClassHierarchy().allClasses().forEach(jClass -> {
//            if(jClass.getName().equals("org.hibernate.engine.spi.TypedValue$1")){
////                JMethod jMethod = jClass.getDeclaredMethod("initTransients");
//                System.out.println(jClass.getDeclaredMethods());
////                jMethod.getIR().getStmts().forEach(stmt -> {
////                    //System.out.printf("%s\n", stmt);
////                    if(stmt instanceof Invoke invoke && invoke.getRValue() instanceof InvokeInstanceExp invokeInstanceExp){
////                        Var var = invokeInstanceExp.getBase();
////                        if (var.getName().equals("$r2")){
////                            System.out.printf("%s, %s, %s\n", stmt, var, invokeInstanceExp.getMethodRef().resolve());
////                        }
////
////                    }
////                });
//            }
//        });


    }


    public void testField(Stmt stmt, CSManager csManager, CSMethod csMethod){
        if (stmt instanceof LoadField lf && lf.getRValue() instanceof InstanceFieldAccess instanceFieldAccess && instanceFieldAccess.getBase().getName().equals("%this")){
            System.out.println(csManager.getCSVar(csMethod.getContext(), instanceFieldAccess.getBase()).getPointsToSet());
            System.out.println(lf.getLValue());
            System.out.println(lf.getLValue().getType());
            System.out.println(csManager.getCSVar(csMethod.getContext(), lf.getLValue()).getPointsToSet());
        }
    }
}

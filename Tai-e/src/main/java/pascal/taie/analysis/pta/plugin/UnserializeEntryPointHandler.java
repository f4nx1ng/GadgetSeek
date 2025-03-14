package pascal.taie.analysis.pta.plugin;

import pascal.taie.World;
import pascal.taie.analysis.pta.core.cs.context.Context;
import pascal.taie.analysis.pta.core.cs.element.CSManager;
import pascal.taie.analysis.pta.core.cs.element.CSObj;
import pascal.taie.analysis.pta.core.cs.element.CSVar;
import pascal.taie.analysis.pta.core.cs.element.InstanceField;
import pascal.taie.analysis.pta.core.heap.Obj;
import pascal.taie.analysis.pta.core.solver.DeclaredParamProvider;
import pascal.taie.analysis.pta.core.solver.EntryPoint;
import pascal.taie.analysis.pta.core.solver.Solver;
import pascal.taie.analysis.pta.plugin.taint.*;
import pascal.taie.analysis.pta.pts.PointsToSet;
import pascal.taie.config.AnalysisOptions;
import pascal.taie.ir.exp.InstanceFieldAccess;
import pascal.taie.ir.exp.InvokeExp;
import pascal.taie.ir.exp.InvokeInstanceExp;
import pascal.taie.ir.exp.Var;
import pascal.taie.ir.stmt.Invoke;
import pascal.taie.ir.stmt.LoadField;
import pascal.taie.ir.stmt.Stmt;
import pascal.taie.language.classes.JClass;
import pascal.taie.language.classes.JField;
import pascal.taie.language.classes.JMethod;
import pascal.taie.language.type.Type;
import pascal.taie.util.MyUtils;

import java.util.*;

public class UnserializeEntryPointHandler implements Plugin {
    private Solver solver;
    private TaintConfig config = TaintConfig.EMPTY;
    private static String[] iniClass = {"org.apache.commons.collections4.comparators.TransformingComparator", "org.apache.commons.collections.keyvalue.TiedMapEntry", "org.apache.commons.beanutils.BeanComparator", "org.apache.commons.collections.map.LazyMap", "com.sun.syndication.feed.impl.ObjectBean", "com.sun.syndication.feed.impl.EqualsBean", "com.sun.syndication.feed.impl.ToStringBean", "org.springframework.aop.target.HotSwappableTargetSource"};
    private static List<String> iniClassList = Arrays.asList(iniClass);



    @Override
    public void setSolver(Solver solver) {
        this.solver = solver;
    }

    @Override
    public void onStart() {
        // 从污点配置文件当中找source点，然后把source点视为一个入口点entry 加入到worklist当中
        config = TaintAnalysis.config;
        List<JClass> list = solver.getHierarchy().allClasses().toList();

        //找org.apache.commons.collections4.comparators.TransformingComparator，看看compare的签名
//        for(JClass jClass: list){
//            if (jClass.getName().equals("org.apache.commons.collections4.comparators.TransformingComparator")){
//                System.out.println("find the target class");
//                jClass.getDeclaredMethods().forEach(method -> {
//                    if (method.getName().equals("compare")){
//                        System.out.printf("find the method : %s\n the param type: %s\n",method.getGSignature(), method.getParamType(0));
//                    }
//                });
//            }
//
//
//        }


        for (JClass jclass : list){
            config.sources().forEach(source -> {
                if (jclass.getName().equals(source.type().getName())){
                    if (source instanceof CallSource callSrc) {
                        JMethod jMethod = callSrc.method();
                        solver.addEntryPoint(new EntryPoint(jMethod, new DeclaredParamProvider(jMethod, solver.getHeapModel())));
                    } else if (source instanceof ParamSource paramSrc) {
                        JMethod jMethod = paramSrc.method();
                        System.out.printf("%s\n", jMethod.getSignature());
                        solver.addEntryPoint(new EntryPoint(jMethod, new DeclaredParamProvider(jMethod, solver.getHeapModel())));
                    }
                }
            });
        }
    }

    public boolean hasTainted(Var var, Context context){
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

    public void test(TaintManager manager, CSManager csManager){
        solver.getCallGraph().reachableMethods().forEach(csMethod -> {
            Var thisVar = csMethod.getMethod().getIR().getThis();
            Context context = csMethod.getContext();
            if (thisVar != null && context != null && hasTainted(thisVar, context) && iniClassList.contains(csMethod.getMethod().getDeclaringClass().getName())){
                //打印执行类及方法
                System.out.println(csMethod.getMethod().getDeclaringClass().getName());
                System.out.println(csMethod.getMethod().getName());
                    PointsToSet thisPts = solver.getCSManager().getCSVar(context, thisVar).getPointsToSet();
                    csMethod.getMethod().getIR().getStmts().forEach(stmt1 -> {
                        if (stmt1 instanceof LoadField lf && lf.getRValue() instanceof InstanceFieldAccess instanceFieldAccess){
                            //拿到base，field以及type
                            Var base = instanceFieldAccess.getBase();
                            PointsToSet basePts = solver.getPointsToSetOf(solver.getCSManager().getCSVar(context, base));
                            Type type = lf.getFieldRef().getType();
//                            JField jField = lf.getFieldRef().resolve();
                            Var targetVar = lf.getLValue();
                            CSVar csTargetVar = csManager.getCSVar(context, targetVar);

                            if(!MyUtils.list.contains(type.getName())){
                                //如果base就是this，证明this的field是需要用的，就需要传递
                                if (base.equals(thisVar)){
                                    if(thisPts != null && !thisPts.isEmpty()){
                                        for(CSObj csObj : thisPts.getObjects()){
                                            Obj obj = csObj.getObject();
                                            if(manager.isTaint(obj)) {
                                                if (csMethod.getMethod().getDeclaringClass().getName().equals("com.sun.syndication.feed.impl.ToStringBean") && csMethod.getMethod().getName().equals("toString") && csMethod.getMethod().getParamCount() == 0){
                                                    System.out.println("aaaa");
                                                }
                                                SourcePoint sourcePoint = manager.getSourcePoint(obj);
                                                CSObj taintobj = solver.getCSManager().getCSObj(solver.getContextSelector().getEmptyContext(), manager.makeTaint(sourcePoint, type));
//                                                CSObj taintobj = solver.getCSManager().getCSObj(solver.getContextSelector().getEmptyContext(), manager.makeTaint(sourcePoint, type));
                                                if(!solver.getPointsToSetOf(csTargetVar).contains(taintobj)){
                                                    solver.addPointsTo(csTargetVar, manager.makeTaint(sourcePoint, type));
//                                                    solver.addPointsTo(csTargetVar, manager.makeTaint(sourcePoint, type));
//                                                    System.out.printf("the Obj declareClass: %s\n", manager.makeTaint(sourcePoint, type, csMethod.getMethod()).getDeclareClassName());
                                                }
                                            }
                                        }
//                                        System.out.printf("%s\n", csTargetVar.getPointsToSet());
                                    }
                                } else {
                                    //广义的this->this.field,当base为变量的时候也赋值
                                    if(basePts != null && !basePts.isEmpty()){
                                        for(CSObj csObj : basePts.getObjects()){
                                            Obj obj = csObj.getObject();
                                            if(manager.isTaint(obj)) {
                                                SourcePoint sourcePoint = manager.getSourcePoint(obj);
                                                CSObj taintobj = solver.getCSManager().getCSObj(solver.getContextSelector().getEmptyContext(), manager.makeTaint(sourcePoint, type));
                                                if(!solver.getPointsToSetOf(csTargetVar).contains(taintobj)){
                                                    solver.addPointsTo(csTargetVar, manager.makeTaint(sourcePoint, type));
//                                                    System.out.printf("%s\n", manager.makeTaint(sourcePoint, type).getImplicited());
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                        }
                    });

            }
        });
    }

    /**
    * 如果在方法当中出现 $r = $this.field; $r.invoke时
     * 要判断$this是否指向污点数据taint data
     * 则$r->tantObj,我因为污点数据可能存在于对象的任意field当中
     * 同时执行广义的this->this.field
    * */
    @Override
    public void onPhaseFinish() {
        //处理污点传播 this-> this.field

        TaintManager manager = new TaintManager(solver.getHeapModel());
        CSManager csManager = solver.getCSManager();
        //test2("before");
        test(manager, csManager);
        //test2("after");

//        solver.getCallGraph().reachableMethods().forEach(csMethod -> {
//            if (csMethod.getMethod().getDeclaringClass().getName().equals("java.util.PriorityQueue") && csMethod.getMethod().getName().equals("siftDownUsingComparator")){
//                Var thisVar = csMethod.getMethod().getIR().getThis();
//                Context context = csMethod.getContext();
//                PointsToSet thisPts = solver.getCSManager().getCSVar(context, thisVar).getPointsToSet();
//                csMethod.getMethod().getIR().getStmts().forEach(stmt1 -> {
//                    if (stmt1 instanceof LoadField lf && lf.getRValue() instanceof InstanceFieldAccess instanceFieldAccess){
//                        //拿到base，field以及type
//                        Var base = instanceFieldAccess.getBase();
//                        Type type = lf.getFieldRef().getType();
//                        if(!MyUtils.list.contains(type.getName())){
//                            CSVar csVar = csManager.getCSVar(context, lf.getLValue());
//                            //如果base就是this，证明this的field是需要用的，就需要传递
//                            if (base.equals(thisVar)){
//                                if(thisPts != null && !thisPts.isEmpty()){
//                                    thisPts.objects().forEach(csObj -> {
//                                        Obj obj = csObj.getObject();
//                                        if(manager.isTaint(obj)){
//                                            SourcePoint sourcePoint = manager.getSourcePoint(obj);
//                                            CSObj taintobj = solver.getCSManager().getCSObj(solver.getContextSelector().getEmptyContext(), manager.makeTaint(sourcePoint, type));
//                                            if(!solver.getPointsToSetOf(csVar).contains(taintobj)){
//                                                if(csVar.getVar().getName().equals("$r3") && csMethod.getMethod().getName().equals("siftDownUsingComparator")){
//                                                    System.out.println("1");
//                                                }
////                                            System.out.printf("%s \n", taintobj.getObject().getImplicited());
//                                                solver.addPointsTo(csVar, manager.makeTaint(sourcePoint, type));
//                                            }
//                                        }
//                                    });
//                                }
//                            }
//                        }
//
//                    }
//                });
//            }
//        });
    }
    @Override
    public void onFinish() {
        solver.getHierarchy().allClasses().forEach(jClass -> {
            if (jClass.getName().equals("org.springframework.aop.target.HotSwappableTargetSource")){
                JMethod jMethod = jClass.getDeclaredMethod("hashCode");
                jMethod.getIR().getStmts().forEach(stmt -> {
                    System.out.printf("%s\n", stmt);
                });
            }
        });
    }
}

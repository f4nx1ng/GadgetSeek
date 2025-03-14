package pascal.taie.analysis.pta.plugin;

import pascal.taie.World;
import pascal.taie.analysis.graph.flowgraph.FlowKind;
import pascal.taie.analysis.pta.core.cs.context.Context;
import pascal.taie.analysis.pta.core.cs.element.CSManager;
import pascal.taie.analysis.pta.core.cs.element.CSMethod;
import pascal.taie.analysis.pta.core.cs.element.CSObj;
import pascal.taie.analysis.pta.core.cs.element.CSVar;
import pascal.taie.analysis.pta.core.heap.Obj;
import pascal.taie.analysis.pta.core.solver.DeclaredParamProvider;
import pascal.taie.analysis.pta.core.solver.EntryPoint;
import pascal.taie.analysis.pta.core.solver.PointerFlowEdge;
import pascal.taie.analysis.pta.core.solver.Solver;
import pascal.taie.analysis.pta.plugin.taint.*;
import pascal.taie.analysis.pta.pts.PointsToSet;
import pascal.taie.ir.exp.InstanceFieldAccess;
import pascal.taie.ir.exp.InvokeInstanceExp;
import pascal.taie.ir.exp.StaticFieldAccess;
import pascal.taie.ir.exp.Var;
import pascal.taie.ir.stmt.Invoke;
import pascal.taie.ir.stmt.LoadField;
import pascal.taie.ir.stmt.Stmt;
import pascal.taie.ir.stmt.StoreField;
import pascal.taie.language.classes.*;
import pascal.taie.language.type.Type;
import pascal.taie.util.MyUtils;

import java.util.List;
import java.util.Stack;

public class UnserializeHandlerAutomaticVerNoPruning implements Plugin{

    private Solver solver;
    private TaintConfig config = TaintConfig.EMPTY;

    private JClass serializableInterface;

    @Override
    public void setSolver(Solver solver) {
        this.solver = solver;
    }

    /**
     * 更新版的全自动形式的source提取，从taint-config文件当中拿到source的信息，并将其加入入口
     * */
    @Override
    public void onStart() {
        // 从污点配置文件当中找source点，然后把source点视为一个入口点entry 加入到worklist当中
        config = TaintAnalysis.config;
        List<JClass> list = solver.getHierarchy().allClasses().toList();
        serializableInterface = solver.getHierarchy().getClass("java.io.Serializable");

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

    /**
     * 判断当前的指向集里面有没有污点数据
     * */
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

    /**
     * 用于判断当前方法的声明类是否满足需求，
     * 在之前的MyAlltypes的处理当中，判断了很多条件，但在这个judgeclass当中并不需要
     * 因为在这个判断里面，所有的类都应该是能够作为反序列化流当中的组成对象
     *
     * */

    public boolean judgeClass(JClass jClass){
        if(solver.getHierarchy().isSubclass(serializableInterface, jClass) &&
                !jClass.getName().equals("java.lang.Object") &&
                !jClass.isAbstract() &&
                !jClass.isInterface()){
            return true;
        }
        return false;
    }


    /**
     * 如果在方法当中出现 $r = $this.field; $r.invoke时
     * 要判断$this是否指向污点数据taintObj，如果当前存在taintObj
     * 则那么就将$r->tantObj,我因为污点数据可能存在于this对象的任意field当中
     * 当前版本为全自动版的前行版本，用于实验摒弃了硬编码方式的效率和一些条件问题
     * */
    @Override
    public void onPhaseFinish(){
        //处理污点传播 this-> this.field
        TaintManager manager = new TaintManager(solver.getHeapModel());
        CSManager csManager = solver.getCSManager();


        solver.getCallGraph().reachableMethods().forEach(csMethod -> {

            Var thisVar = csMethod.getMethod().getIR().getThis();
            Context context = csMethod.getContext();
            //no pruning
            if (thisVar != null && context != null && hasTainted(thisVar, context)){
                System.out.println(csMethod.getMethod().getDeclaringClass().getName());
                System.out.println(csMethod.getMethod().getName());

                PointsToSet thisPts = solver.getCSManager().getCSVar(context, thisVar).getPointsToSet();
                JMethod readObject;
                if(!csMethod.getMethod().getDeclaringClass().getName().equals("java.io.ObjectInputStream")){
                    readObject = csMethod.getMethod().getDeclaringClass().getDeclaredMethod("readObject");
                }else {
                    readObject = null;
                }




                csMethod.getMethod().getIR().getStmts().forEach(stmt1 -> {
//                    //debug code
                    //common process
                    if (stmt1 instanceof LoadField lf && lf.getRValue() instanceof InstanceFieldAccess instanceFieldAccess && judgeField(readObject, lf)){
                        //拿到base，field以及type
                        Var base = instanceFieldAccess.getBase();
                        PointsToSet basePts = solver.getPointsToSetOf(solver.getCSManager().getCSVar(context, base));
                        Type type = lf.getFieldRef().getType();


                        Var targetVar = lf.getLValue();
                        CSVar csTargetVar = csManager.getCSVar(context, targetVar);


                        if(!MyUtils.list.contains(type.getName())){
                            //如果base就是this，证明this的field是需要用的，就需要传递
                            if (base.equals(thisVar)){
                                narrowTaintTransfer(thisPts, manager, csTargetVar, type);
                            }else {
                                //广义的this->this.field,当base为变量的时候也赋值
                                broadTaintTransfer(basePts, manager, csTargetVar, type);
                            }
                        }

                    } else if (stmt1 instanceof LoadField lf && lf.getRValue() instanceof StaticFieldAccess && judgeField(readObject, lf)) {
                        Type type = lf.getFieldRef().getType();
                        Var targetVar = lf.getLValue();
                        CSVar csTargetVar = csManager.getCSVar(context, targetVar);

                        narrowTaintTransfer(thisPts, manager, csTargetVar, type);
                    }
                });

            }else {
                csMethod.getMethod().getIR().getStmts().forEach(stmt1 -> {
//                    //debug code
//                    if (csMethod.getMethod().getDeclaringClass().getName().equals("org.mozilla.javascript.ScriptableObject") && csMethod.getMethod().getName().equals("getImpl")){
//                        System.out.println("hh");
//                    }
                    //common process
                    if (stmt1 instanceof LoadField lf && lf.getRValue() instanceof InstanceFieldAccess instanceFieldAccess && judgeFieldSimple(lf)){
                        //拿到base，field以及type
                        Var base = instanceFieldAccess.getBase();
                        PointsToSet basePts = solver.getPointsToSetOf(solver.getCSManager().getCSVar(context, base));
                        Type type = lf.getFieldRef().getType();

                        Var targetVar = lf.getLValue();
                        CSVar csTargetVar = csManager.getCSVar(context, targetVar);

                        if(!MyUtils.list.contains(type.getName())){
                            //如果base就是this，证明this的field是需要用的，就需要传递
                            if (!base.equals(thisVar)){
                                //广义的this->this.field,当base为变量的时候也赋值
                                broadTaintTransfer(basePts, manager, csTargetVar, type);
                            }
                        }

                    }
                });
            }
        });
    }

    //用于判断transient修饰的变量，同时获取其readObject方法探寻其中是否包含，给transient变量赋值的情况
    /**
     * 由于debug需要我们使用了如下的处理方式将judgeMethod设置默认为false
     * */
    public boolean judgeField(JMethod readObject, LoadField lf){


        JField jField = lf.getFieldRef().resolve();

        //debug专用false
        if(jField.getModifiers().contains(Modifier.TRANSIENT) && !judgeMethod(readObject, jField, false)){
            return false;
        }
        return true;
    }

    public boolean judgeFieldSimple(LoadField lf){
        JField jField = lf.getFieldRef().resolve();
        if(jField.getModifiers().contains(Modifier.TRANSIENT)){
            return false;
        }
        return true;
    }

    public static boolean judgeMethod(JMethod readObject, JField jField, boolean flag){
        if (readObject == null){
            return false;
        }

        for (Stmt stmt : readObject.getIR().getStmts()){
            if (stmt instanceof StoreField sf && sf.getFieldRef().resolve().equals(jField)){
                return true;
            }
        }

        if(flag){
            for(Stmt stmt : readObject.getIR().getStmts()){
                if(stmt instanceof Invoke invoke && invoke.getRValue() instanceof InvokeInstanceExp invokeInstanceExp && !invokeInstanceExp.getMethodRef().getName().equals("defaultReadObject")){
                    //再进入一层
                    try{
                        return judgeMethod(invokeInstanceExp.getMethodRef().resolve(), jField, false);
                    }catch (Exception e){
                        //todo
                    }
                }
            }
        }

        return false;

    }

    /**
     * 广义的this->this.field处理
     * 里面内含有关于额外cast，copy语句的部分，详见2025/1/12日志
     * **/
    public void broadTaintTransfer(PointsToSet basePts, TaintManager manager, CSVar csTargetVar, Type type){
        if(basePts != null && !basePts.isEmpty()){
            for(CSObj csObj : basePts.getObjects()){
                Obj obj = csObj.getObject();
                if(manager.isTaint(obj)) {
                    SourcePoint sourcePoint = manager.getSourcePoint(obj);
                    CSObj taintobj = solver.getCSManager().getCSObj(solver.getContextSelector().getEmptyContext(), manager.makeTaint(sourcePoint, type));
                    if(!solver.getPointsToSetOf(csTargetVar).contains(taintobj)){
                        solver.addPointsTo(csTargetVar, manager.makeTaint(sourcePoint, type));
                        flowTaintByPFGSimple(csTargetVar, sourcePoint, manager);

                    }
                }
            }
        }
    }

    /**
     * 狭义的this->this.field
     * */

    public void narrowTaintTransfer(PointsToSet thisPts, TaintManager manager, CSVar csTargetVar, Type type){
        if(thisPts != null && !thisPts.isEmpty()){
            for(CSObj csObj : thisPts.getObjects()){
                Obj obj = csObj.getObject();
                if(manager.isTaint(obj)) {
                    SourcePoint sourcePoint = manager.getSourcePoint(obj);
                    CSObj taintobj = solver.getCSManager().getCSObj(solver.getContextSelector().getEmptyContext(), manager.makeTaint(sourcePoint, type));
                    if(!solver.getPointsToSetOf(csTargetVar).contains(taintobj)){
                        solver.addPointsTo(csTargetVar, manager.makeTaint(sourcePoint, type));
                        flowTaintByPFGSimple(csTargetVar, sourcePoint, manager);
                    }
                }
            }
//                                        System.out.printf("%s\n", csTargetVar.getPointsToSet());
        }
    }


    /**
     * 随指针流图进行污点传播的简易版本，是用于配置较低的电脑（内存或cpu配置过低）
     * 其对应的内容是满血版本的深度为1
     * */
    public void flowTaintByPFGSimple(CSVar csTargetVar, SourcePoint sourcePoint, TaintManager manager){
        if(csTargetVar.getOutEdges() != null){
            for(PointerFlowEdge indirect : csTargetVar.getOutEdges()){
                if(indirect.kind().equals(FlowKind.CAST) && indirect.target() instanceof CSVar indirectTargetVar){
                    CSObj taintobj2 = solver.getCSManager().getCSObj(solver.getContextSelector().getEmptyContext(), manager.makeTaint(sourcePoint, indirect.target().getType()));
                    if(!solver.getPointsToSetOf(indirectTargetVar).contains(taintobj2)){
                        solver.addPointsTo(indirectTargetVar, manager.makeTaint(sourcePoint, indirect.target().getType()));
                    }
                }
            }
        }

    }



    public void flowTaintByPFG(CSVar csTargetVar, SourcePoint sourcePoint, TaintManager manager){
        Stack<Node> worklist = new Stack<>();

        worklist.push(new Node(csTargetVar, 0));
        while (!worklist.isEmpty()){
            Node currentNode = worklist.pop();
            CSVar currentVar = currentNode.csTargetVar;
            int currentDepth = currentNode.depth;

            if (currentDepth >= 2){
                continue;
            }


            if(currentVar.getOutEdges() != null){
                for(PointerFlowEdge indirect : currentVar.getOutEdges()){
                    if((indirect.kind().equals(FlowKind.CAST) || indirect.kind().equals(FlowKind.LOCAL_ASSIGN)) && indirect.target() instanceof CSVar indirectTargetVar){
                        CSObj taintobj2 = solver.getCSManager().getCSObj(solver.getContextSelector().getEmptyContext(), manager.makeTaint(sourcePoint, indirect.target().getType()));
                        if(!solver.getPointsToSetOf(indirectTargetVar).contains(taintobj2)){
                            solver.addPointsTo(indirectTargetVar, manager.makeTaint(sourcePoint, indirect.target().getType()));
                            worklist.push(new Node(indirectTargetVar, currentDepth + 1));

                        }
                    }
                }
            }
        }

    }





    public class Node {
        CSVar csTargetVar;
        int depth;

        public Node(CSVar csTargetVar, int depth){
            this.csTargetVar = csTargetVar;
            this.depth = depth;
        }

    }



}

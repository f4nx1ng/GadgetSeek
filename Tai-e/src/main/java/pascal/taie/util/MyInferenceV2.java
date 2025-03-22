package pascal.taie.util;

import pascal.taie.analysis.pta.core.cs.element.CSCallSite;
import pascal.taie.ir.exp.*;
import pascal.taie.ir.proginfo.FieldRef;
import pascal.taie.ir.stmt.*;
import pascal.taie.language.classes.JMethod;

import java.util.*;

public class MyInferenceV2 {

    /**
     * 对于我们来说什么是有价值的，事实上如果是在利用链路径上的函数调用
     * 那么它的recvObj是有用的，param也是有用的，而recvObj根本不需要我们管，只看下级函数就行了
     * 对于param来讲，哪些情况是我们需要考虑的，首先是它来源于形参，其次是它来源于this，其他的所有情况就都是本函数内部的局部变量了
     * 我们当前实现的部分是观察推理，本次函数调用的param是否与this.field有关，因为这个param不可能会传this
     *
     * 我们将要实现的部分是观察推理param与入参之间的关系。
     * 从优先级顺序的角度，其实是可以做一些逻辑上的处理的，我们呢可以先判断param与入参的field之间的关系如果没有再找与param的关系
     *
     * 当前的问题是this和param谁先
     * 之前在搜寻的时候一直在讨论关于传入参数在函数内部的样子，但是结局是令人意外的，所以我们需要分情况讨论
     *
     *
     * */

    /**
     * @param var invoke语句当中的参数
     *
     *
     * */
    @Deprecated
    public static FieldRef findThisFieldAssociation(Var var, JMethod container, CSCallSite csCallSite){

        List<Stmt> stmtGroup = new ArrayList<>(container.getIR().getStmts());

        Var thisVar = container.getIR().getThis();

        Stack<Node> stack = new Stack<>();

        //初始化栈，如果一开始的赋值与我们的目标变量不同，就将这些变量压栈

        if (thisVar == null || thisVar.getLoadFields().isEmpty()){
            return null;
        }else {
            for(LoadField lf : thisVar.getLoadFields()){
                //为了节省后续的搜索次数，我们需要做出“减枝”
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

                if (csCallSite.getCallSite().equals(stmt)){
                    break;
                }

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
                } else if (stmt instanceof Invoke invoke && !invoke.equals(csCallSite.getCallSite()) && invoke.getLValue() != null) {
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

    /**
     * 之前在搜寻的时候一直在讨论关于传入参数在函数内部的样子，但是结局是令人意外的，所以我们需要分情况讨论
     * 第一种情况入参通过某种方式给了另外的便改良
     * 第二种情况只用了本体
     *
     *
     * */

    public static FieldRef findFormalParameterFieldAssociation(Var var, JMethod container, Var paramVar, CSCallSite csCallSite){

        List<Stmt> stmtGroup = new ArrayList<>(container.getIR().getStmts());

        //获取这个方法的传入参数列表
        List<Var> paramVars = container.getIR().getParams();


        if(paramVars.isEmpty()){
            return null;
        }

        //多个参数


        Stack<Node> stack = new Stack<>();
        HashSet<Var> realParam;

        //初始化栈，如果一开始的赋值与我们的目标变量不同，就将这些变量压栈
        //面对第一种情况，找到目标变量


        //todo 函数封装，stack值会影响函数外的stack值
        if (paramVar.getLoadFields().isEmpty()){
            //把真正的变量放进来
            realParam = findRealVar(paramVar, container, csCallSite);
            // todo: 减枝
            //如果没有用传入变量
            if(realParam.isEmpty()){
                return null;
            }else {
                //对realParam也获取field的使用情况，为stack的初始值设定一些fieldRef
                for (Var rp : realParam){
                    for(LoadField lf : rp.getLoadFields()){
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
            }

        } else {
            //todo 我们已经获得了realParam，接下来的是进行不用获取realParam时，对于field的处理
            for(LoadField lf : paramVar.getLoadFields()){
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


        while(!stack.isEmpty()){
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
                } else if (stmt instanceof Invoke invoke && !invoke.equals(csCallSite.getCallSite()) && invoke.getLValue() != null) {
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
            //节点访问结束

        }
        return  null;
    }


    /**
     *
     * 这里为了避免出现类似$r2 = (org.springframework.aop.target.HotSwappableTargetSource) other的情况
     * 然后后面直接不用other直接使用$r2了，这种畜生情况
     * 名为搜寻真实的变量
     * */
    public static HashSet<Var> findRealVar(Var param, JMethod container, CSCallSite csCallSite) {
        HashSet<Var> realParam = new HashSet<>();
        for (Stmt stmt : container.getIR().getStmts()){
            if (csCallSite.getCallSite().equals(stmt)){
                break;
            }

            if (stmt instanceof AssignStmt<?, ?> assignStmt) {
                if(assignStmt instanceof StoreArray sa){
                    //先看右边再看左边
                    //此时确认右边的变量为我们当前处理的变量
                    if (sa.getRValue().equals(param)){
                        //减负
                        //如果目标变量就是我们要找的变量
                        realParam.add(sa.getLValue().getBase());
                    }

                } else if (assignStmt instanceof Cast cast) {
                    //先看右边再看左边
                    //此时确认右边的变量为我们当前处理的变量
                    if (cast.getRValue().getValue().equals(param)){
                        //减负
                        realParam.add(cast.getLValue());
                    }

                } else if (assignStmt instanceof Copy copy) {
                    if (copy.getRValue().equals(param)){
                        //减负
                        realParam.add(copy.getLValue());
                    }

                } else if (assignStmt instanceof InstanceOf insf) {
                    if (insf.getRValue().getValue().equals(param)){
                        //减负
                        realParam.add(insf.getLValue());
                    }
                } else if (assignStmt instanceof StoreField sf) {
                    if (sf.getRValue().equals(param)){
                        if (sf.getLValue() instanceof InstanceFieldAccess instanceFieldAccess){
                            realParam.add(instanceFieldAccess.getBase());
                        }
                    }
                } else if (assignStmt instanceof LoadArray la) {
                    if (la.getRValue().getBase().equals(param)){
                        realParam.add(la.getLValue());
                    }
                }
            } else if (stmt instanceof Invoke invoke && !invoke.equals(csCallSite.getCallSite())) {
                //没有处理dynamic invoke
                if (invoke.getRValue() instanceof InvokeInstanceExp invokeInstanceExp){
                    //先看右边再看左边
                    //此时确认右边的变量为我们当前处理的变量
                    //todo,这个需不需要像由于构造函数的改变
//                    if (invokeInstanceExp.getBase().equals(param) || invokeInstanceExp.getArgs().contains(param)){
//                        Var lvar = invoke.getLValue();
//                        realParam.add(lvar);
//                    }
                    Var base = invokeInstanceExp.getBase();
                    if (base.equals(param) || invokeInstanceExp.getArgs().contains(param)) {
                        Var lvar = invoke.getLValue();
                        //todo 先判断是不是null
                        if (lvar != null) {
                            realParam.add(lvar);
                        } else {
                            if (!base.equals(param) && invokeInstanceExp.getArgs().contains(param)) {
                                realParam.add(base);
                            }
                        }
                    }


                } else if (invoke.getRValue() instanceof InvokeStatic invokeStatic && invoke.getLValue() != null) {
                    if (invokeStatic.getArgs().contains(param)){
                        Var lvar = invoke.getLValue();
                        realParam.add(lvar);
                    }
                }
            }
        }

        return realParam;
    }

    /**
     * 该函数为findRealParam的衍生版本
     * 单次的findRealParam可能无法找到真正的目标，我们需要使用不停的传递，直到找到存在field关系的变量
     * */
    public static HashSet<Var> cleanRealParam(Var param, JMethod container, CSCallSite csCallSite){
        Stack<Var> stack = new Stack<>();
        HashSet<Var> result = new HashSet<>();
        //debug
        HashSet<Var> initial = findRealVar(param, container, csCallSite);

        if (initial == null || initial.isEmpty()){
            return null;
        }

        //初始结果入栈
        for(Var temp : initial){
            stack.push(temp);
        }


        while(!stack.isEmpty()){
            Var var2 = stack.pop();
            if (!var2.getLoadFields().isEmpty()){
                result.add(var2);
            }else{
                HashSet<Var> tempSet = findRealVar(var2, container, csCallSite);
                if (!tempSet.isEmpty()){
                    for(Var temp : tempSet){
                        stack.push(temp);
                    }
                }
            }
        }


        return result;

    }




//    public FieldRef dealAssignStmt(AssignStmt assignStmt){
//
//
//    }


    /**
     * @param var invoke语句的参数;
     * @param paramVar container方法当中的参数
     * @param csCallSite 对应的invoke语句
     * 也许对this也好使
     * */
    public static boolean findFormalParameterAssociation(Var var, JMethod container, Var paramVar, CSCallSite csCallSite){
        List<Stmt> stmtGroup = container.getIR().getStmts();
        Queue<Var> queue = new LinkedList<>();
        HashSet<Stmt> usedStmt = new HashSet<>();
        //增设特殊情况，目标var就是此var
        if (paramVar.equals(var)){
            return true;
        }

        queue.offer(paramVar);
        while (!queue.isEmpty()){
            Var current = queue.poll();
            if(current==null){
                continue;
            }

            for (Stmt stmt : stmtGroup){
                if (stmt instanceof AssignStmt<?,?> assignStmt){
                    if(assignStmt instanceof StoreArray sa){
                        //先看右边再看左边
                        //此时确认右边的变量为我们当前处理的变量
                        if (sa.getRValue().equals(current)){
                            //减负
                            //如果目标变量就是我们要找的变量
                            Var lvar = sa.getLValue().getBase();
                            if(lvar.equals(var)){
                                return true;
                            } else if(!usedStmt.contains(stmt)){
                                queue.offer(lvar);
                                usedStmt.add(stmt);
                            }
                        }

                    } else if (assignStmt instanceof Cast cast) {
                        //先看右边再看左边
                        //此时确认右边的变量为我们当前处理的变量
                        if (cast.getRValue().getValue().equals(current)){
                            //减负
                            Var lvar = cast.getLValue();
                            if(lvar.equals(var)){
                                return true;
                            } else if(!usedStmt.contains(stmt)){
                                queue.offer(lvar);
                                usedStmt.add(stmt);
                            }
                        }

                    } else if (assignStmt instanceof Copy copy) {
                        if (copy.getRValue().equals(current)){
                            //减负
                            Var lvar = copy.getLValue();
                            if(lvar.equals(var)){
                                return true;
                            } else if(!usedStmt.contains(stmt)){
                                queue.offer(lvar);
                                usedStmt.add(stmt);
                            }
                        }

                    } else if (assignStmt instanceof InstanceOf insf) {
                        if (insf.getRValue().getValue().equals(current)){
                            //减负
                            Var lvar = insf.getLValue();
                            if(lvar.equals(var)){
                                return true;
                            } else if(!usedStmt.contains(stmt)){
                                queue.offer(lvar);
                                usedStmt.add(stmt);
                            }
                        }
                    } else if (assignStmt instanceof StoreField sf) {
                        if (sf.getRValue().equals(current)){
                            if (sf.getLValue() instanceof InstanceFieldAccess instanceFieldAccess){
                                Var lvar = instanceFieldAccess.getBase();
                                if(lvar.equals(var)){
                                    return true;
                                } else if(!usedStmt.contains(stmt)){
                                    queue.offer(lvar);
                                    usedStmt.add(stmt);
                                }
                            }

                        }
                    }else if(assignStmt instanceof LoadArray la){
                       if (la.getRValue().getBase().equals(current)){
                           Var lvar = la.getLValue();
                           if (lvar.equals(var)){
                               return true;
                           } else if(!usedStmt.contains(stmt)){
                               queue.offer(lvar);
                               usedStmt.add(stmt);
                           }
                       }
                    }
                } else if (stmt instanceof Invoke invoke && !invoke.equals(csCallSite.getCallSite())) {
                    //没有处理dynamic invoke
                    if (invoke.getRValue() instanceof InvokeInstanceExp invokeInstanceExp){
                        //先看右边再看左边
                        //此时确认右边的变量为我们当前处理的变量
//                        if (invokeInstanceExp.getBase().equals(current) || invokeInstanceExp.getArgs().contains(current)){
//                            Var lvar = invoke.getLValue();
//                            if(lvar.equals(var)){
//                                return true;
//                            } else {
//                                stack.push(lvar);
//                            }
//                        }
//                        try{
//                            boolean result = instanceInvokeDealVar(invokeInstanceExp, current, invoke, var, stack);
//                        }catch (Exception e){
//                            e.printStackTrace();
//                        }
                        boolean result = instanceInvokeDealVar(invokeInstanceExp, current, invoke, var, queue, usedStmt);
                        if(result){
                            return true;
                        }

                    } else if (invoke.getRValue() instanceof InvokeStatic invokeStatic && invoke.getLValue() != null) {
                        if (invokeStatic.getArgs().contains(current)){
                            Var lvar = invoke.getLValue();
                            if(lvar.equals(var)){
                                return true;
                            } else if(!usedStmt.contains(stmt)){
                                queue.offer(lvar);
                                usedStmt.add(stmt);
                            }
                        }
                    }
                }
            }

        }
        return false;
    }


    /**
     * 发现一个惊奇的现象，我们目前有一个问题，就是在一段代码当中
     * 可能既有param。field也有类似$r2 = (org.springframework.aop.target.HotSwappableTargetSource) other的出生情况
     * ，但是这个param。field和我们要找的没关系，反而是初生和我们有关系
     * 并且我们的语句是使用List存的所以我们可以多一层判断，适时跳出循环
     *
     * 因此我们需要做额外的处理
     *
     *  @param var invoke语句的参数/或者是recvObj;
     *  @param paramVar container方法当中的参数
     *  @param csCallSite 对应的invoke语句
     *  如下这个方法仅处理paramfield当场存在的情况，或者是this.field
     * */


    public static FieldRef findFieldAssociationTruly(Var var, JMethod container, Var paramVar, CSCallSite csCallSite, boolean isThis){

        List<Stmt> stmtGroup = container.getIR().getStmts();
        HashSet<Stmt> usedStmt = new HashSet<>();

//        //获取这个方法的传入参数列表
//        List<Var> paramVars = container.getIR().getParams();


        if(paramVar == null){
            return null;
        }

        //多个参数


        Queue<Node> queue = new LinkedList<>();

        //初始化栈，如果一开始的赋值与我们的目标变量不同，就将这些变量压栈
        //面对第一种情况，找到目标变量


        if (paramVar != null && !paramVar.getLoadFields().isEmpty()){
            for(LoadField lf : paramVar.getLoadFields()){
                //为了节省后续的搜索次数，我们需要做出“减枝”
                Var var1 = lf.getLValue();
                String fieldname = lf.getRValue().getFieldRef().getName();
                FieldRef fieldRef = lf.getRValue().getFieldRef();

                if(var1.equals(var)){
                    return fieldRef;
                }else{
                    queue.offer(new Node(var1, fieldname, fieldRef));
                }
            }


        } else {
            return null;
        }


        while(!queue.isEmpty()){
            Node current = queue.poll();
            //访问当前节点
            for(Stmt stmt : stmtGroup){
                //基于流敏感的考虑，考虑语句之间的先后关系，当遍历到当前invoke的时候，跳出循环
                if (csCallSite.getCallSite().equals(stmt)){
                    break;
                }

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
                            } else if(!usedStmt.contains(stmt)){
                                queue.offer(new Node(lvar, current.fieldname, current.fieldRef));
                                usedStmt.add(stmt);
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
                            } else if(!usedStmt.contains(stmt)){
                                queue.offer(new Node(lvar, current.fieldname, current.fieldRef));
                                usedStmt.add(stmt);
                            }
                        }

                    } else if (assignStmt instanceof Copy copy) {
                        if (copy.getRValue().equals(current.var)){
                            //减负
                            Var lvar = copy.getLValue();
                            if(lvar.equals(var)){
                                return current.fieldRef;
                            } else if(!usedStmt.contains(stmt)){
                                queue.offer(new Node(lvar, current.fieldname, current.fieldRef));
                                usedStmt.add(stmt);
                            }
                        }

                    } else if (assignStmt instanceof InstanceOf insf) {
                        if (insf.getRValue().getValue().equals(current.var)){
                            //减负
                            Var lvar = insf.getLValue();
                            if(lvar.equals(var)){
                                return current.fieldRef;
                            } else if(!usedStmt.contains(stmt)){
                                queue.offer(new Node(lvar, current.fieldname, current.fieldRef));
                                usedStmt.add(stmt);
                            }
                        }
                    } else if (assignStmt instanceof StoreField sf) {
                        if (sf.getRValue().equals(current.var)){
                            if (sf.getLValue() instanceof InstanceFieldAccess instanceFieldAccess){
                                Var lvar = instanceFieldAccess.getBase();
                                if(lvar.equals(var)){
                                    return current.fieldRef;
                                } else if(!usedStmt.contains(stmt)){
                                    queue.offer(new Node(lvar, current.fieldname, current.fieldRef));
                                    usedStmt.add(stmt);
                                }
                            }

                        }
                    } else if (assignStmt instanceof LoadField lf) {
                        //用于解决日志中2024/12/22当中的错误
                        if (lf.getFieldAccess() instanceof InstanceFieldAccess instanceFieldAccess && instanceFieldAccess.getBase().equals(current.var)){
                            Var lvar = lf.getLValue();
                            if (lvar.equals(var)){
                                return lf.getFieldRef();
                            }else if(!usedStmt.contains(stmt)){
                                queue.offer(new Node(lvar, lf.getFieldRef().getName(), lf.getFieldRef()));
                                usedStmt.add(stmt);
                            }
                        } else if (isThis && lf.getFieldAccess() instanceof StaticFieldAccess staticFieldAccess) {
                            Var lvar = lf.getLValue();
                            if (lvar.equals(var)){
                                return lf.getFieldRef();
                            }else if(!usedStmt.contains(stmt)){
                                queue.offer(new Node(lvar, lf.getFieldRef().getName(), lf.getFieldRef()));
                                usedStmt.add(stmt);
                            }
                        }
                    }
                } else if (stmt instanceof Invoke invoke && !invoke.equals(csCallSite.getCallSite())) {
                    //没有处理dynamic invoke
                    if (invoke.getRValue() instanceof InvokeInstanceExp invokeInstanceExp){
                        //先看右边再看左边
                        //此时确认右边的变量为我们当前处理的变量
//                        if (invokeInstanceExp.getBase().equals(current.var) || invokeInstanceExp.getArgs().contains(current.var)){
//                            Var lvar = invoke.getLValue();
//                            if(lvar.equals(var)){
//                                return current.fieldRef;
//                            } else {
//                                stack.push(new Node(lvar, current.fieldname, current.fieldRef));
//                            }
//                        }
                        FieldRef result = instanceInvokeDeal(invokeInstanceExp, current, invoke, var, queue, usedStmt);
                        if (result!=null){
                            return result;
                        }

                    } else if (invoke.getRValue() instanceof InvokeStatic invokeStatic && invoke.getLValue() != null) {
                        if (invokeStatic.getArgs().contains(current.var)){
                            Var lvar = invoke.getLValue();
                            if(lvar.equals(var)){
                                return current.fieldRef;
                            } else if (!usedStmt.contains(stmt)){
                                queue.offer(new Node(lvar, current.fieldname, current.fieldRef));
                                usedStmt.add(stmt);
                            }
                        }
                    }
                }
            }
            //节点访问结束
        }
        return  null;
    }

    /**
     * 在前者调用结果为null的时候调用该方法，用于面对前者注释中提到的特殊情况
     * 同样是搜寻fieldAssociation的关系
     *
     * */

    public static FieldRef findFieldAssociationTrulyFromRealParam(Var var, JMethod container, Var paramVar, CSCallSite csCallSite, boolean isThis){

        List<Stmt> stmtGroup = container.getIR().getStmts();
        HashSet<Var> realParam = new HashSet<>();


        Queue<Node> queue = new LinkedList<>();
        HashSet<Stmt> usedStmt = new HashSet<>();

        if(paramVar == null){
            return null;
        }

        //初始化栈，如果一开始的赋值与我们的目标变量不同，就将这些变量压栈
        //面对第一种情况，找到目标变量

        realParam = cleanRealParam(paramVar, container, csCallSite);

        //如果没有用传入变量
        if(realParam == null || realParam.isEmpty()){
            return null;
        }else {
            //对realParam也获取field的使用情况，为stack的初始值设定一些fieldRef

            for (Var rp : realParam){
                for(LoadField lf : rp.getLoadFields()){
                    Var var1 = lf.getLValue();
                    String fieldname = lf.getRValue().getFieldRef().getName();
                    FieldRef fieldRef = lf.getRValue().getFieldRef();

                    if(var1.equals(var)){
                        return fieldRef;
                    }else{
                        queue.offer(new Node(var1, fieldname, fieldRef));
                        usedStmt.add(lf);
                    }
                }
            }
        }


        while(!queue.isEmpty()){
            Node current = queue.poll();
            //访问当前节点
            for(Stmt stmt : stmtGroup){
                //基于流敏感的考虑，考虑语句之间的先后关系，当遍历到当前invoke的时候，跳出循环
                if (csCallSite.getCallSite().equals(stmt)){
                    break;
                }

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
                            } else if(!usedStmt.contains(stmt)){
                                queue.offer(new Node(lvar, current.fieldname, current.fieldRef));
                                usedStmt.add(stmt);
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
                            } else if(!usedStmt.contains(stmt)){
                                queue.offer(new Node(lvar, current.fieldname, current.fieldRef));
                                usedStmt.add(stmt);
                            }
                        }

                    } else if (assignStmt instanceof Copy copy) {
                        if (copy.getRValue().equals(current.var)){
                            //减负
                            Var lvar = copy.getLValue();
                            if(lvar.equals(var)){
                                return current.fieldRef;
                            } else if(!usedStmt.contains(stmt)){
                                queue.offer(new Node(lvar, current.fieldname, current.fieldRef));
                                usedStmt.add(stmt);
                            }
                        }

                    } else if (assignStmt instanceof InstanceOf insf) {
                        if (insf.getRValue().getValue().equals(current.var)){
                            //减负
                            Var lvar = insf.getLValue();
                            if(lvar.equals(var)){
                                return current.fieldRef;
                            } else if(!usedStmt.contains(stmt)){
                                queue.offer(new Node(lvar, current.fieldname, current.fieldRef));
                                usedStmt.add(stmt);
                            }
                        }
                    } else if (assignStmt instanceof StoreField sf) {
                        if (sf.getRValue().equals(current.var)){
                            if (sf.getLValue() instanceof InstanceFieldAccess instanceFieldAccess){
                                Var lvar = instanceFieldAccess.getBase();
                                if(lvar.equals(var)){
                                    return current.fieldRef;
                                } else if(!usedStmt.contains(stmt)){
                                    queue.offer(new Node(lvar, current.fieldname, current.fieldRef));
                                    usedStmt.add(stmt);
                                }
                            }

                        }
                    }else if (assignStmt instanceof LoadField lf) {
                        //用于解决日志中2024/12/22当中的错误
                        if (lf.getFieldAccess() instanceof InstanceFieldAccess instanceFieldAccess && instanceFieldAccess.getBase().equals(current.var)){
                            Var lvar = lf.getLValue();
                            if (lvar.equals(var)){
                                return lf.getFieldRef();
                            }else if(!usedStmt.contains(stmt)){
                                queue.offer(new Node(lvar, lf.getFieldRef().getName(), lf.getFieldRef()));
                                usedStmt.add(stmt);
                            }
                        } else if (isThis && lf.getFieldAccess() instanceof StaticFieldAccess staticFieldAccess) {
                            Var lvar = lf.getLValue();
                            if (lvar.equals(var)){
                                return lf.getFieldRef();
                            }else if(!usedStmt.contains(stmt)){
                                queue.offer(new Node(lvar, lf.getFieldRef().getName(), lf.getFieldRef()));
                            }
                        }
                    }
                } else if (stmt instanceof Invoke invoke && !invoke.equals(csCallSite.getCallSite())) {
                    //没有处理dynamic invoke
                    if (invoke.getRValue() instanceof InvokeInstanceExp invokeInstanceExp){
                        //先看右边再看左边
                        //此时确认右边的变量为我们当前处理的变量

                        //last version
//                        Var base = invokeInstanceExp.getBase();
//                        if (base.equals(current.var) || invokeInstanceExp.getArgs().contains(current.var)){
//                            Var lvar = invoke.getLValue();
//                            //last version
//                            if(lvar.equals(var)){
//                                return current.fieldRef;
//                            } else {
//                                stack.push(new Node(lvar, current.fieldname, current.fieldRef));
//                            }
//                        }
                        FieldRef result = instanceInvokeDeal(invokeInstanceExp, current, invoke, var, queue, usedStmt);
                        if (result!=null){
                            return result;
                        }

                    } else if (invoke.getRValue() instanceof InvokeStatic invokeStatic && invoke.getLValue() != null) {
                        if (invokeStatic.getArgs().contains(current.var)){
                            Var lvar = invoke.getLValue();
                            if(lvar.equals(var)){
                                return current.fieldRef;
                            } else if(!usedStmt.contains(stmt)){
                                queue.offer(new Node(lvar, current.fieldname, current.fieldRef));
                            }
                        }
                    }
                }
            }
            //节点访问结束

        }
        return  null;
    }

    public static boolean isNew(Var var, JMethod container, CSCallSite csCallSite){
        List<Stmt> stmtGroup = container.getIR().getStmts();

        for(Stmt stmt : stmtGroup){
            if (csCallSite.getCallSite().equals(stmt)){
                break;
            }
            if(stmt instanceof New newStmt && newStmt.getLValue().equals(var)){
                return true;
            }
        }
        return false;
    }



    /**
     * 由于需要处理关于构造函数的特殊情况，所以没有办法，必须要统一处理一下
     * 同时加上了之前关于函数调用方面的内容
     * */

    public static FieldRef instanceInvokeDeal(InvokeInstanceExp invokeInstanceExp, Node current, Invoke invoke, Var var, Queue<Node> queue, HashSet<Stmt> usedStmt){
        Var base = invokeInstanceExp.getBase();
        if (base.equals(current.var) || invokeInstanceExp.getArgs().contains(current.var)) {
            Var lvar = invoke.getLValue();
            //todo 先判断是不是null
            if (lvar != null) {
                if (lvar.equals(var)) {
                    return current.fieldRef;
                } else {
                    queue.offer(new Node(lvar, current.fieldname, current.fieldRef));
                    usedStmt.add(invoke);
                    return null;
                }
            } else {
                if (!base.equals(current.var) && invokeInstanceExp.getArgs().contains(current.var)) {
                    if (base.equals(var)) {
                        return current.fieldRef;
                    } else {
                        queue.offer(new Node(base, current.fieldname, current.fieldRef));
                        usedStmt.add(invoke);
                        return null;
                    }
                }
            }
        }
        return null;
    }

    public static boolean instanceInvokeDealVar(InvokeInstanceExp invokeInstanceExp, Var current, Invoke invoke, Var var, Queue<Var> queue, HashSet<Stmt> usedStmt){
        Var base = invokeInstanceExp.getBase();
//        try{
//            invokeInstanceExp.getArgs().contains(current);
//        }catch (Exception e){
//            System.out.println("wa");
//        }
        if (base.equals(current) || invokeInstanceExp.getArgs().contains(current)) {
            Var lvar = invoke.getLValue();
            //todo 先判断是不是null
            if (lvar != null) {
                if (lvar.equals(var)) {
                    return true;
                } else if(!usedStmt.contains(invoke)){
                    queue.offer(lvar);
                    usedStmt.add(invoke);
                    return false;
                }
            } else {
                if (!base.equals(current) && invokeInstanceExp.getArgs().contains(current)) {
                    if (base.equals(var)) {
                        return true;
                    } else if(!usedStmt.contains(invoke)){
                        queue.offer(lvar);
                        usedStmt.add(invoke);
                        return false;
                    }
                }
            }
        }
        return false;
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

}

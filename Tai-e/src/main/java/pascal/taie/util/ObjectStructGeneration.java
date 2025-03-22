package pascal.taie.util;


import com.sun.source.tree.Tree;
import pascal.taie.language.classes.JMethod;
import pascal.taie.util.graph.SimpleGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjectStructGeneration {
    public static SimpleGraph getTheStructOfObject(List<CSRCMethod> CSRCMethods, List<JMethod> container){
        int size = container.size();
        int id = 0;
        SimpleGraph ObjectStruct = new SimpleGraph<>();
        TreeNode root  = new TreeNode(container.get(size-1).getDeclaringClass().getName(), id);
        ObjectStruct.addNode(root);
        for (int i = size - 2; i > 0; i--){
            CSRCMethod currentCSRCMethod = CSRCMethods.get(i);
            String preClassName = container.get(i+1).getDeclaringClass().getName();
            TreeNode preNode = new TreeNode(preClassName, id);
            TreeNode currentNode = null;
            if (container.get(i).getDeclaringClass().getName().equals(preClassName)){
                if (currentCSRCMethod.getRecvAssociation() == "empty" || currentCSRCMethod.getRecvAssociation() == "this"){
                   continue; //是静态且对象是一个
                }else if(currentCSRCMethod.getRecvAssociation().contains("field")){
                    TreeFieldNode fieldNode  = new TreeFieldNode(currentCSRCMethod.getRecvAssociation(), id);
                    if (currentCSRCMethod.getRecvAssociation().contains("this field")){
                        //增添field节点
                        ObjectStruct.addNode(fieldNode);
                        ObjectStruct.addEdge(preNode, fieldNode);
                        //除去field,其本身也需要一个节点 field->本体
                        currentNode = new TreeNode(container.get(i).getDeclaringClass().getName(), id);
                        if(ObjectStruct.hasNode(currentNode)){
                            currentNode.addId();
                        }
                        ObjectStruct.addNode(currentNode);
                        ObjectStruct.addEdge(fieldNode, currentNode);

                    }
                }else{
                    //param deal
                    currentNode = new TreeNode(container.get(i).getDeclaringClass().getName(), id);
                    if(ObjectStruct.hasNode(currentNode)){
                        currentNode.addId();
                    }
                    ObjectStruct.addNode(currentNode);
                    ObjectStruct.addEdge(preNode, currentNode);
                }

            }else {
                //一下情况全是上级与下级不同类的时候
                if(currentCSRCMethod.getRecvAssociation() != "this"){
                    //this.field, param.field

                    if(currentCSRCMethod.getRecvAssociation().contains("field")){
                        //todo field的处理不止这些，因为事实上field是需要添加两个点
                        //this field and param field
                        TreeFieldNode fieldNode  = new TreeFieldNode(currentCSRCMethod.getRecvAssociation(), id);
                        if (currentCSRCMethod.getRecvAssociation().contains("this field")){
                            //增添field节点
                            ObjectStruct.addNode(fieldNode);
                            ObjectStruct.addEdge(preNode, fieldNode);
                            //除去field,其本身也需要一个节点 field->本体
                            currentNode = new TreeNode(container.get(i).getDeclaringClass().getName(), id);
                            if(ObjectStruct.hasNode(currentNode)){
                                currentNode.addId();
                            }
                            ObjectStruct.addNode(currentNode);
                            ObjectStruct.addEdge(fieldNode, currentNode);

                        }

                    }else{
                        //param deal
                        currentNode = new TreeNode(container.get(i).getDeclaringClass().getName(), id);
                        if(ObjectStruct.hasNode(currentNode)){
                            currentNode.addId();
                        }
                        ObjectStruct.addNode(currentNode);
                        ObjectStruct.addEdge(preNode, currentNode);
                    }
                }
            }
        }

        return ObjectStruct;
    }


    public static SimpleGraph getTheStructOfObjectTruly(List<CSRCMethod> CSRCMethods, List<JMethod> container){
        int size = container.size();
        HashMap<String, Integer> identity = new HashMap<>();
        SimpleGraph ObjectStruct = new SimpleGraph<>();
        TreeNode root  = new TreeNode(container.get(size-1).getDeclaringClass().getName(), 0);
        identity.put(container.get(size-1).getDeclaringClass().getName(), 0);
        ObjectStruct.addNode(root);
        for (int i = size - 2; i > 0; i--){
            CSRCMethod currentCSRCMethod = CSRCMethods.get(i);
            String preClassName = container.get(i+1).getDeclaringClass().getName();
            TreeNode preNode = new TreeNode(preClassName, identity.get(preClassName));
            TreeNode currentNode = null;
            if (container.get(i).getDeclaringClass().getName().equals(preClassName)){
                if (currentCSRCMethod.getRecvAssociation() == "empty" || currentCSRCMethod.getRecvAssociation() == "this"){
                    continue; //是静态且对象是一个
                }else if(currentCSRCMethod.getRecvAssociation().contains("field")){

                    TreeFieldNode fieldNode  = createFieldNodeSafety(currentCSRCMethod.getRecvAssociation(), identity);
                    if (currentCSRCMethod.getRecvAssociation().contains("this field")){
                        //增添field节点
                        ObjectStruct.addNode(fieldNode);
                        ObjectStruct.addEdge(preNode, fieldNode);
                        //除去field,其本身也需要一个节点 field->本体
                        currentNode = createNodeSafety(container.get(i).getDeclaringClass().getName(), identity);
                        if(ObjectStruct.hasNode(currentNode)){
                            currentNode.addId();
                        }
                        ObjectStruct.addNode(currentNode);
                        ObjectStruct.addEdge(fieldNode, currentNode);

                    }
                }else{
                    //param deal
                    currentNode = createNodeSafety(container.get(i).getDeclaringClass().getName(), identity);
                    if(ObjectStruct.hasNode(currentNode)){
                        currentNode.addId();
                    }
                    ObjectStruct.addNode(currentNode);
                    ObjectStruct.addEdge(preNode, currentNode);
                }

            }else {
                //一下情况全是上级与下级不同类的时候
                if(currentCSRCMethod.getRecvAssociation() != "this"){
                    //this.field, param.field

                    if(currentCSRCMethod.getRecvAssociation().contains("field")){
                        //todo field的处理不止这些，因为事实上field是需要添加两个点
                        //this field and param field
                        TreeFieldNode fieldNode  = createFieldNodeSafety(currentCSRCMethod.getRecvAssociation(), identity);
                        if (currentCSRCMethod.getRecvAssociation().contains("this field")){
                            //增添field节点
                            ObjectStruct.addNode(fieldNode);
                            ObjectStruct.addEdge(preNode, fieldNode);
                            //除去field,其本身也需要一个节点 field->本体
                            currentNode = createNodeSafety(container.get(i).getDeclaringClass().getName(), identity);
                            if(ObjectStruct.hasNode(currentNode)){
                                currentNode.addId();
                            }
                            ObjectStruct.addNode(currentNode);
                            ObjectStruct.addEdge(fieldNode, currentNode);

                        }

                    }else{
                        //param deal 总的来说是有一个对象流到了这里才被用到了，所以直接声明没有问题
                        currentNode = createNodeSafety(container.get(i).getDeclaringClass().getName(), identity);
                        if(ObjectStruct.hasNode(currentNode)){
                            currentNode.addId();
                        }
                        ObjectStruct.addNode(currentNode);
                        ObjectStruct.addEdge(preNode, currentNode);
                    }
                }
            }
        }

        return ObjectStruct;
    }

    /**
     * 这是最新版本的对象结构构造推理，实现了交叉信息流的对象结构推理
     * @param CSRCMethods 输入的CSRCMethod Path
     * @param container 与CSRCMethod Path对应的方法调用路径
     *
     *
     * */
    public static SimpleGraph getTheStructOfObjectTrulyV2(List<CSRCMethod> CSRCMethods, List<JMethod> container){

        //两个全新的数据结构，用于存储每一层新定义的对象
        //例如recvEveryFloor用于存储在recxvObj位置声明的新对象（这里可能不止一个，但是保证最后一个为TreeNode）
        //paramEveryFloor用于存储在param位置上声明的新对象，每个参数位置只存一个最新的变量
        List<List<MyNode>> recvEveryFloor = new ArrayList<>();
        List<Map<Integer, MyNode>> paramEveryFloor = new ArrayList<>();


        int size = container.size();
        //用于记录所有对象的特殊id，标识唯一性
        HashMap<String, Integer> identity = new HashMap<>();
        SimpleGraph ObjectStruct = new SimpleGraph<>();
        //初始化根节点信息
        TreeNode root  = new TreeNode(container.get(size-1).getDeclaringClass().getName(), 0);
        identity.put(container.get(size-1).getDeclaringClass().getName(), 0);
        ObjectStruct.addNode(root);
        recvEveryFloor.add(new ArrayList<>());
        paramEveryFloor.add(new HashMap<>());
        recvEveryFloor.get(0).add(root);

        //处理后续节点情况
        for (int i = size - 2, floor = 1; i > 0; i--, floor++){
            //每一层的初始化
            recvEveryFloor.add(floor,new ArrayList<>());
            paramEveryFloor.add(floor, new HashMap<>());
            CSRCMethod currentCSRCMethod = CSRCMethods.get(i);

            //获取上一层的方法调用对象的节点，以便于后续直接连接
//            String preClassName = container.get(i+1).getDeclaringClass().getName();
            if (floor == 4){
                System.out.println("stop");
            }

            try{
                TreeNode preNode = (TreeNode) recvEveryFloor.get(floor - 1).get(recvEveryFloor.get(floor - 1).size() - 1);

            }catch (Exception e){
                e.printStackTrace();
            }
            TreeNode preNode = (TreeNode) recvEveryFloor.get(floor - 1).get(recvEveryFloor.get(floor - 1).size() - 1);
            String preClassName = preNode.classname;
            TreeNode currentNode = null;

            //上级对象与本层对象类型一致的时候
            if (container.get(i).getDeclaringClass().getName().equals(preClassName)){
                if (currentCSRCMethod.getRecvAssociation() == "empty" || currentCSRCMethod.getRecvAssociation() == "this"){

                    //继承前者的层级，以便于后续前向查找时，不用跨越过多层级
                    recvEveryFloor.get(floor).add(recvEveryFloor.get(floor-1).get(recvEveryFloor.get(floor-1).size() - 1));

                    //支线处理
                    branchLineProcess(recvEveryFloor, paramEveryFloor, ObjectStruct, currentCSRCMethod.getParamAssociation(), floor, identity);
                    continue; //是静态且对象是一个


                } else if (currentCSRCMethod.getRecvAssociation().equals("inner this")) {

                    //todo 暂未发现当前情况



                } else if(currentCSRCMethod.getRecvAssociation().contains("field")){

                    TreeFieldNode fieldNode  = createFieldNodeSafety(currentCSRCMethod.getRecvAssociation(), identity);
                    if (currentCSRCMethod.getRecvAssociation().contains("this field")){
                        //增添field节点，及对应边
                        ObjectStruct.addNode(fieldNode);
                        ObjectStruct.addEdge(preNode, fieldNode);

                        //当前楼层存储，现存fieldnode
                        recvEveryFloor.get(floor).add(fieldNode);

                        //除去field,其本身也需要一个节点 field->本体
                        currentNode = createNodeSafety(container.get(i).getDeclaringClass().getName(), identity);
                        ObjectStruct.addNode(currentNode);
                        ObjectStruct.addEdge(fieldNode, currentNode);

                        //当前楼层存储 保证最后一个对象是TreeNode
                        recvEveryFloor.get(floor).add(currentNode);



                    }else{
                        //param field
                        paramFieldMainDeal(currentCSRCMethod.getRecvAssociation(), paramEveryFloor, recvEveryFloor, ObjectStruct, floor, identity);
                    }

                    //处理完主线，就去处理支线
                    branchLineProcess(recvEveryFloor, paramEveryFloor, ObjectStruct, currentCSRCMethod.getParamAssociation(), floor, identity);

                }else if(currentCSRCMethod.getRecvAssociation().contains("param") && !currentCSRCMethod.getRecvAssociation().contains("field")){
                    //param deal
                    //创建一个当前层级类型的对象
                    currentNode = createNodeSafety(container.get(i).getDeclaringClass().getName(), identity);
                    ObjectStruct.addNode(currentNode);

                    //数据处理，拿到参数的序号
                    int argIndex = Integer.parseInt(currentCSRCMethod.getRecvAssociation().split(" ")[1]);

                    //前向感染，需要判断条件。
                    //所谓前向感染，意思是当recvObj为param相关的信息时，如果前向的parameveryfloor里面没有任何能获取的信息，
                    // 则通过此时的recv声明的对象来前向感染
                    MyNode tempNode = paramEveryFloor.get(floor -1).get(argIndex);

                    if (tempNode == null ){
                        ObjectStruct.addEdge(preNode, currentNode);
                        //前向感染
                        paramEveryFloor.get(floor-1).put(argIndex, currentNode);
                    } else if (tempNode instanceof TreeFieldNode treeTempFieldNode) {
                        //连接field以及新创建的对象
                        ObjectStruct.addEdge(treeTempFieldNode, currentNode);

                    }else if(tempNode instanceof TreeNode){
//                            TreeNode treeTempNode = (TreeNode) tempNode;
                        //todo 也许根本没有这个情况

                    }

                    //楼层存储
                    recvEveryFloor.get(floor).add(currentNode);

                    //处理完主线，就去处理支线
                    branchLineProcess(recvEveryFloor, paramEveryFloor, ObjectStruct, currentCSRCMethod.getParamAssociation(), floor, identity);
                }

            }else {
                //一下情况全是上级与下级不同类的时候
                if(currentCSRCMethod.getRecvAssociation() != "this"){
                    //this.field, param.field

                    if(currentCSRCMethod.getRecvAssociation().contains("field")){

                        //this field and param field
                        TreeFieldNode fieldNode  = createFieldNodeSafety(currentCSRCMethod.getRecvAssociation(), identity);
                        if (currentCSRCMethod.getRecvAssociation().contains("this field")){
                            //增添field节点
                            ObjectStruct.addNode(fieldNode);
                            ObjectStruct.addEdge(preNode, fieldNode);

                            //当前楼层存储
                            recvEveryFloor.get(floor).add(fieldNode);

                            //除去field,其本身也需要一个节点 field->本体
                            currentNode = createNodeSafety(container.get(i).getDeclaringClass().getName(), identity);
                            ObjectStruct.addNode(currentNode);
                            ObjectStruct.addEdge(fieldNode, currentNode);

                            //当前楼层存储
                            recvEveryFloor.get(floor).add(currentNode);

                        }else{
                            //param field deal
                            paramFieldMainDeal(currentCSRCMethod.getRecvAssociation(), paramEveryFloor, recvEveryFloor, ObjectStruct, floor, identity);
                        }

                        //支线处理
                        branchLineProcess(recvEveryFloor, paramEveryFloor, ObjectStruct, currentCSRCMethod.getParamAssociation(), floor, identity);

                    } else if (currentCSRCMethod.getRecvAssociation().equals("inner this")) {

                        //inner this的含义是 这个参数与this有关但是与this.field没有关系，且不等于this的情况。
                        currentNode = createNodeSafety(container.get(i).getDeclaringClass().getName(), identity);
                        ObjectStruct.addNode(currentNode);

                        ObjectStruct.addEdge(preNode, currentNode);

                        recvEveryFloor.get(floor).add(currentNode);

                        //支线处理
                        branchLineProcess(recvEveryFloor, paramEveryFloor, ObjectStruct, currentCSRCMethod.getParamAssociation(), floor, identity);

                    } else if(currentCSRCMethod.getRecvAssociation().contains("param") && !currentCSRCMethod.getRecvAssociation().contains("field")){
                        //param deal
                        currentNode = createNodeSafety(container.get(i).getDeclaringClass().getName(), identity);
                        ObjectStruct.addNode(currentNode);

                        //获取参数的位置索引
                        int argIndex = Integer.parseInt(currentCSRCMethod.getRecvAssociation().split(" ")[1]);

                        //前向感染，需要判断条件，具体参照前面的处理过程注释
                        MyNode tempNode = paramEveryFloor.get(floor - 1).get(argIndex);

                        if (tempNode == null ){
                            ObjectStruct.addEdge(preNode, currentNode);
                            paramEveryFloor.get(floor-1).put(argIndex, currentNode);
                        } else if (tempNode instanceof TreeFieldNode treeTempFieldNode) {
                            //连接field以及新创建的对象
                            ObjectStruct.addEdge(treeTempFieldNode, currentNode);

                        }else if(tempNode instanceof TreeNode){
//                            TreeNode treeTempNode = (TreeNode) tempNode;
                            //todo 也许根本没有，目前没遇到

                        }
                        recvEveryFloor.get(floor).add(currentNode);

                        //处理完主线，就去处理支线
                        branchLineProcess(recvEveryFloor, paramEveryFloor, ObjectStruct, currentCSRCMethod.getParamAssociation(), floor, identity);
                    } else if (currentCSRCMethod.getRecvAssociation().equals("empty") || currentCSRCMethod.getRecvAssociation().equals("No Association")) {
                        //此时为静态方法前前后两者不一致
                        //继承前者的层级，以便于后续前向查找时，不用跨越过多层级
                        recvEveryFloor.get(floor).add(recvEveryFloor.get(floor-1).get(recvEveryFloor.get(floor-1).size() - 1));

                        //支线处理
                        branchLineProcess(recvEveryFloor, paramEveryFloor, ObjectStruct, currentCSRCMethod.getParamAssociation(), floor, identity);

                    }
                }else{
                    //本以为没有但是，由于路径可能在某一处开始不可控，那么也就有了
                    recvEveryFloor.get(floor).add(recvEveryFloor.get(floor-1).get(recvEveryFloor.get(floor-1).size() - 1));

                    //支线处理
                    branchLineProcess(recvEveryFloor, paramEveryFloor, ObjectStruct, currentCSRCMethod.getParamAssociation(), floor, identity);
                }
            }
        }

        return ObjectStruct;

    }

    /**
     * 整个支线处理流程的入口
     * @param identity 用于存放所有声明对象的唯一标识符，对应关系为 “类型”-> id
     * @param ObjectStruct 当前正在构造的对象结构
     * @param currentFloor 当前的楼层层级
     * @param paramAssociation CSRCMethod当中对应param的关联信息
     * @param paramEveryFloor 参数的每层对象信息
     * @param recvEveryFloor recvObj 的每层对象信息
     * */

    public static void branchLineProcess(List<List<MyNode>> recvEveryFloor, List<Map<Integer, MyNode>> paramEveryFloor, SimpleGraph ObjectStruct, List<String> paramAssociation, int currentFloor, HashMap<String, Integer> identity){
        int size = paramAssociation.size();
        for (int i = 0; i < size; i++){
            String currentAssociation = paramAssociation.get(i);

            if (currentAssociation.contains("param") && !currentAssociation.contains("field")){
                //param deal
                paramDeal(paramEveryFloor, currentAssociation, currentFloor, i);
            }else if (currentAssociation.equals("this")){
                //todo 目前没遇到这种情况

            } else if (currentAssociation.equals("inner this")) {
                //什么都不做，等着后面进行前向感染

            } else if (currentAssociation.contains("this field")) {
                //这一定是TreeNode，这是由于recv就是这么存的
                TreeNode preNode = (TreeNode) getPreLastNode(recvEveryFloor, currentFloor);
                TreeFieldNode currentNode = createFieldNodeSafety(currentAssociation, identity);
                //加入floor
                paramEveryFloor.get(currentFloor).put(i, currentNode);
                ObjectStruct.addNode(currentNode);
                ObjectStruct.addEdge(preNode, currentNode);
            } else if (currentAssociation.contains("param") && currentAssociation.contains("field")) {
                //对于param field的处理
                int argIndex = Integer.parseInt(currentAssociation.split(" ")[1]);
                //上一层对应参数位置的节点对象
                MyNode preNode = paramEveryFloor.get(currentFloor - 1).get(argIndex);

                //事实上，如果支线当中还关联了支线，就会发生最尴尬的情况，前者不会确定后者的field具体在哪一个类型当中，
                //只有field自己通过fieldref的信息才能拿到对应的类对象
                //这里是通过fieldref拿到对应的类型
                String className = currentAssociation.split(":")[1].substring(2);
                TreeNode currentNode = createNodeSafety(className, identity);
                //field node
                TreeFieldNode currentFieldNode = createFieldNodeSafety(currentAssociation, identity);

                //中间过程变量不进floor
                paramEveryFloor.get(currentFloor).put(i, currentFieldNode);

                //加入边和点
                ObjectStruct.addNode(currentNode);
                ObjectStruct.addNode(currentFieldNode);
                //
                ObjectStruct.addEdge(preNode, currentNode);
                ObjectStruct.addEdge(currentNode, currentFieldNode);
            }else if(currentAssociation.equals("No Arg")){
                continue;
            }
        }
    }


//    public static void branchLineProcessOnce(List<List<MyNode>> recvEveryFloor, List<List<MyNode>> paramEveryFloor, SimpleGraph ObjectStruct, List<String> paramAssociation, int currentFloor, HashMap<String, Integer> identity){
//
//    }



    /**
     * 用于寻找上一楼层当中的最后一个对象，常用于寻找recvEveryFloor最后一个对象当中
     * 针对于支线的参数当中出现的this field情况
     * @param recvEveryFloor 接收者对象的每层信息
     * @param floor 当前的处理楼层级数
     * */
    public static MyNode getPreLastNode(List<List<MyNode>> recvEveryFloor, int floor){
        //找到上一楼层的最后一个节点
        return recvEveryFloor.get(floor - 1).get(recvEveryFloor.get(floor - 1).size() - 1);
    }

    /**
     * 该方法仅用于支线当中出现param的情况
     * */
    public static void paramDeal(List<Map<Integer, MyNode>> paramEveryFloor, String currentAssociation, int currentFloor, int currentArgIndex){
        int argIndex = Integer.parseInt(currentAssociation.split(" ")[1]);

        //上一个对应参数位置的对象
        MyNode preNode = paramEveryFloor.get(currentFloor - 1).get(argIndex);

        if (preNode == null){
            return;
        }else{
            //todo 目前没遇到，但估计早晚要遇到
            //如果前面楼层是有东西的，当前的想法是把前面的东西直接拿过来,"后向感染"
            paramEveryFloor.get(currentFloor).put(currentArgIndex, preNode);
        }
    }


    /**
     * 用于处理主线当中的param field的情况
     * @param recvEveryFloor recvObj的每层信息
     * @param paramEveryFloor param声明对象的每层信息
     * @param ObjectStruct 正在处理的对象结构
     * @param identity 用于标识对象唯一性的数据结构
     * @param currentFloor 当前楼层数
     * @param currentAssociation 但钱处理的关联信息
     * */

    public static void paramFieldMainDeal(String currentAssociation, List<Map<Integer, MyNode>> paramEveryFloor, List<List<MyNode>> recvEveryFloor, SimpleGraph ObjectStruct, int currentFloor, HashMap<String, Integer> identity){
        int argIndex = Integer.parseInt(currentAssociation.split(" ")[1]);
//        MyNode preNode = getElementOrNull(paramEveryFloor.get(currentFloor - 1), argIndex);
        MyNode preNode = paramEveryFloor.get(currentFloor - 1).get(argIndex);

        //本体(在支线的处理当中，field类型不会当场确定自己的类型),他不会进入floor当中，算作中间过程变量
        String className = currentAssociation.split(":")[1].substring(2);
        TreeNode currentNode = createNodeSafety(className, identity);
        //field node
        TreeFieldNode currentFieldNode = createFieldNodeSafety(currentAssociation, identity);

        //中间过程变量不进floor
        recvEveryFloor.get(currentFloor).add(currentFieldNode);
        recvEveryFloor.get(currentFloor).add(currentNode);

        //加入边和点
        ObjectStruct.addNode(currentNode);
        ObjectStruct.addNode(currentFieldNode);
        ObjectStruct.addEdge(preNode, currentNode);
        ObjectStruct.addEdge(currentNode, currentFieldNode);
    }


    /**
     * 用于安全唯一的创建某一类型的field对象
     * */
    public static TreeFieldNode createFieldNodeSafety(String name, HashMap<String, Integer> identity){
        name = "<" + name.split("<")[1];
        if (identity.get(name) == null){
            identity.put(name, 0);
            return new TreeFieldNode(name, 0);
        }else {
            identity.put(name, identity.get(name) + 1);
            return new TreeFieldNode(name, identity.get(name));
        }
    }

    /**
     * 用于安全唯一的创建某一类型的node对象
     * */
    public static TreeNode createNodeSafety(String name, HashMap<String, Integer> identity){
        if (identity.get(name) == null){
            identity.put(name, 0);
            return new TreeNode(name, 0);
        }else {
            identity.put(name, identity.get(name) + 1);
            return new TreeNode(name, identity.get(name));
        }
    }
}

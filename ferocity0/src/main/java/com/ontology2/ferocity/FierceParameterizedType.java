package com.ontology2.ferocity;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class FierceParameterizedType implements ParameterizedType {
    final Type[] actualTypeArguments;
    final Type rawType;

    FierceParameterizedType(Type rawType, Type[] actualTypeArguments) {
        this.actualTypeArguments = actualTypeArguments;
        this.rawType = rawType;
    }

    @Override
    public Type[] getActualTypeArguments() {
        return actualTypeArguments;
    }

    @Override
    public Type getRawType() {
        return rawType;
    }

    @Override
    public Type getOwnerType() {
        return null;
    }

    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append(((Class) rawType).getCanonicalName());
      if(actualTypeArguments!=null && actualTypeArguments.length>0) {
          sb.append("<");
          for(int i=0;i<actualTypeArguments.length;i++) {
              if(i>0) {
                  sb.append(",");
              }
              sb.append(actualTypeArguments[i].getTypeName());
          }
          sb.append(">");
      }
      return sb.toString();
    };
}

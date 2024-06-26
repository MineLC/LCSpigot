package com.mojang.authlib.properties;

import com.google.common.collect.ForwardingMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map.Entry;

public class PropertyMap extends ForwardingMultimap<String, Property> {
   private final Multimap<String, Property> properties = LinkedHashMultimap.create();

   protected Multimap<String, Property> delegate() {
      return this.properties;
   }

   public static class Serializer implements JsonSerializer<PropertyMap>, JsonDeserializer<PropertyMap> {
      public PropertyMap deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
         PropertyMap result = new PropertyMap();
         if (json instanceof JsonObject) {
            JsonObject object = (JsonObject)json;
            Iterator i$ = object.entrySet().iterator();

            while(true) {
               Entry entry;
               do {
                  if (!i$.hasNext()) {
                     return result;
                  }

                  entry = (Entry)i$.next();
               } while(!(entry.getValue() instanceof JsonArray));

               Iterator re = ((JsonArray)entry.getValue()).iterator();

               while(re.hasNext()) {
                  JsonElement element = (JsonElement)re.next();
                  result.put((String) entry.getKey(), new Property((String)entry.getKey(), element.getAsString()));
               }
            }
         } else if (json instanceof JsonArray) {
            Iterator i$ = ((JsonArray)json).iterator();

            while(i$.hasNext()) {
               JsonElement element = (JsonElement)i$.next();
               if (element instanceof JsonObject) {
                  JsonObject object = (JsonObject)element;
                  String name = object.getAsJsonPrimitive("name").getAsString();
                  String value = object.getAsJsonPrimitive("value").getAsString();
                  if (object.has("signature")) {
                     result.put(name, new Property(name, value, object.getAsJsonPrimitive("signature").getAsString()));
                  } else {
                     result.put(name, new Property(name, value));
                  }
               }
            }
         }

         return result;
      }

      public JsonElement serialize(PropertyMap src, Type typeOfSrc, JsonSerializationContext context) {
         JsonArray result = new JsonArray();

         JsonObject object;
         for(Iterator i$ = src.values().iterator(); i$.hasNext(); result.add(object)) {
            Property property = (Property)i$.next();
            object = new JsonObject();
            object.addProperty("name", property.getName());
            object.addProperty("value", property.getValue());
            if (property.hasSignature()) {
               object.addProperty("signature", property.getSignature());
            }
         }

         return result;
      }
   }
}

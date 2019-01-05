package usa.cactuspuppy.uhc_automation.entity.unique;

import lombok.NoArgsConstructor;
import usa.cactuspuppy.uhc_automation.utils.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@NoArgsConstructor
public class EntityIDManager {
    private static final int MAX_GEN_ATTEMPTS = 10000;
    private static Map<Long, UniqueEntity> uniqueEntityMap = new HashMap<>();

   public static UniqueEntity getEntity(long id) {
       if (id == -1) return null;
       return uniqueEntityMap.get(id);
   }

   public static void trackEntity(long id, UniqueEntity e) {
       if (id == -1) return;
       uniqueEntityMap.put(id, e);
   }

   public static void untrackEntity(long id) {
       uniqueEntityMap.remove(id);
   }

   public static void untrackAll() {
       uniqueEntityMap.clear();
   }

   public static long getNewID() {
       return new IDGenerator().get();
   }

   public static long getNewID(IDGenerator generator) {
       return generator.get();
   }

   @NoArgsConstructor
   public static class IDGenerator {
       private Random rng = new Random();

       public IDGenerator(Random random) {
           rng = random;
       }

       /**
        * Gets a new unique ID
        * @return Long ID which is not currently being tracked.
        */
       public long get() {
           for (int i = 0; i < MAX_GEN_ATTEMPTS; i++) {
               long candidate = rng.nextLong();
               if (!uniqueEntityMap.keySet().contains(candidate)) return candidate;
           }
           Logger.logWarning(this.getClass(), "Exceeded max generation attempts while generating new ID");
           return -1;
       }
   }
}

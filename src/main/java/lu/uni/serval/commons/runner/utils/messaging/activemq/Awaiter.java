package lu.uni.serval.commons.runner.utils.messaging.activemq;

/*-
 * #%L
 * Runner Utils
 * %%
 * Copyright (C) 2021 University of Luxembourg
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class Awaiter {
    private static final Logger logger = LogManager.getLogger(Awaiter.class);

    private final Set<Consumer<Runnable>> runnables = new HashSet<>();
    private final Set<Consumer<Consumer>> consumers = new HashSet<>();

    public void listen(Consumer<Runnable> callback){
        runnables.add(callback);
    }

    public void listenWithArg(Consumer<Consumer> callback){
        consumers.add(callback);
    }

    public boolean waitOnMessages(int timeout, TimeUnit timeUnit) throws InterruptedException {
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        runnables.forEach(m -> m.accept(countDownLatch::countDown));
        consumers.forEach(m -> m.accept(e -> countDownLatch.countDown()));

        return countDownLatch.await(timeout, timeUnit);
    }

    public static boolean waitUntil(int timeout, Callable<Boolean> condition){
        try{
            long maxTime = timeout + System.currentTimeMillis();
            while (maxTime > System.currentTimeMillis()){
                if(Boolean.TRUE.equals(condition.call())) return true;
                Thread.sleep(100);
            }
        } catch (Exception e) {
            logger.printf(Level.ERROR,
                    "Failure during waiting on condition: [%s] %s",
                    e.getClass().getSimpleName(),
                    e.getMessage()
            );
        }

        return false;
    }
}

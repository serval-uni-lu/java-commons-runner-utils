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


import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

public class Observer {
    private final Set<Consumer<Runnable>> runnables = new HashSet<>();
    private final Set<Consumer<Consumer>> consumers = new HashSet<>();

    public void addRunner(Consumer<Runnable> callback){
        runnables.add(callback);
    }

    public void addConsumer(Consumer<Consumer> callback){
        consumers.add(callback);
    }

    public void waitOnMessages() throws InterruptedException {
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        runnables.forEach(m -> m.accept(countDownLatch::countDown));
        consumers.forEach(m -> m.accept(e -> countDownLatch.countDown()));

        countDownLatch.await();
    }
}

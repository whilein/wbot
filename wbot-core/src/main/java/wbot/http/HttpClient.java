/*
 *    Copyright 2024 Whilein
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package wbot.http;

import java.util.concurrent.CompletableFuture;

/**
 * @author whilein
 */
public interface HttpClient {

    void start();

    void stop();

    CompletableFuture<HttpResponse> post(String url, Content content);

    CompletableFuture<HttpResponse> get(String url);


}

/*
 * Copyright 2016 mrwc1264.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
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
 */
package org.mitre.openid.connect.assertion;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author mrwc1264
 */
public class RelayStateRepositoryService {
    private final Set<String> repo = new HashSet<>();
    
    public void addRelayState(String un){
        repo.add(un);
    }
    public boolean existRelayState(String un){
        return repo.contains(un);
    }
}

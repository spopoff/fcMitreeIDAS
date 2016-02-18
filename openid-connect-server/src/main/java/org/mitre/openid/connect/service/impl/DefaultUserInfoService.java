/*******************************************************************************
 * Copyright 2016 The MITRE Corporation
 *   and the MIT Internet Trust Consortium
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package org.mitre.openid.connect.service.impl;

import java.util.ArrayList;
import java.util.List;
import org.mitre.oauth2.model.ClientDetailsEntity;
import org.mitre.oauth2.model.ClientDetailsEntity.SubjectType;
import org.mitre.oauth2.service.ClientDetailsEntityService;
import org.mitre.openid.connect.model.UserInfo;
import org.mitre.openid.connect.repository.UserInfoRepository;
import org.mitre.openid.connect.service.PairwiseIdentiferService;
import org.mitre.openid.connect.service.UserInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation of the UserInfoService
 * 
 * @author Michael Joseph Walsh, jricher
 * 
 */
@Service
public class DefaultUserInfoService implements UserInfoService {
    static final Logger LOGGER = LoggerFactory.getLogger(DefaultUserInfoService.class);
    private List<UserInfo> userSaml;

	@Autowired
	private UserInfoRepository userInfoRepository;

	@Autowired
	private ClientDetailsEntityService clientService;

	@Autowired
	private PairwiseIdentiferService pairwiseIdentifierService;

	@Override
	public UserInfo getByUsername(String username) {
            UserInfo ret = null;
            ret = userInfoRepository.getByUsername(username);
            if(ret==null){
                //seconde change
                if(userSaml!=null){
                    if(!userSaml.isEmpty()){
                        for(UserInfo un: userSaml){
                            if(un.getSub().equals(username)){
                                ret = un;
                                break;
                            }
                        }
                    }
                }
            }
            return ret;
	}

	@Override
	public UserInfo getByUsernameAndClientId(String username, String clientId) {

		ClientDetailsEntity client = clientService.loadClientByClientId(clientId);

		UserInfo userInfo = getByUsername(username);

		if (client == null || userInfo == null) {
			return null;
		}

		if (SubjectType.PAIRWISE.equals(client.getSubjectType())) {
			String pairwiseSub = pairwiseIdentifierService.getIdentifier(userInfo, client);
			userInfo.setSub(pairwiseSub);
		}

		return userInfo;

	}

	@Override
	public UserInfo getByEmailAddress(String email) {
            UserInfo ret = null;
            ret = userInfoRepository.getByEmailAddress(email);
            if(ret==null){
                //seconde change
                if(userSaml!=null){
                    if(!userSaml.isEmpty()){
                        for(UserInfo un: userSaml){
                            if(un.getEmail().equals(email)){
                                ret = un;
                                break;
                            }
                        }
                    }
                }
            }
            return ret;
	}
        /**
         * une fonction pour ajouter dynamiquement les utilisateurs provenant des
         * assertions sans passer par la persistance
         * @param user
         * @param clientId 
         */
        public void addUserInfo(UserInfo user){
            if(userSaml==null){
                userSaml = new ArrayList<>();
                userSaml.add(user);
                LOGGER.debug("ajouté premier userSaml pour user="+user.getSub());
                return;
            }
            boolean found = false;
            for(UserInfo un: userSaml){
                if(un.getSub().equals(user.getSub())){
                    found = true;
                    break;
                }
            }
            if(!found){
                userSaml.add(user);
                LOGGER.debug("ajouté nouveau userSaml pour user="+user.getSub());
            }
        }
}

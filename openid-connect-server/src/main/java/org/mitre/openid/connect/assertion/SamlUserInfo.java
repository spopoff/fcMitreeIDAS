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

import com.google.gson.JsonObject;
import eu.eidas.auth.commons.IPersonalAttributeList;
import eu.eidas.auth.commons.PersonalAttribute;
import java.security.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.mitre.openid.connect.model.Address;
import org.mitre.openid.connect.model.UserInfo;

/**
 * une coquille pour récupérer les attributs d'un utilisateur
 * @author mrwc1264
 */
public class SamlUserInfo implements UserInfo{
    private String PersonIdentifier;
    private String CurrentGivenName;
    private String CurrentFamilyName;
    private String Gender;
    private String DateOfBirth;
//        ret.add("PersonIdentifier");
//        ret.add("CurrentGivenName");
//        ret.add("CurrentFamilyName");
//        ret.add("Gender");
//        ret.add("DateOfBirth");
    
    public SamlUserInfo(IPersonalAttributeList personalAttributeList){
        for(String nom: lesNoms()){
            for(PersonalAttribute pa : personalAttributeList){
                if(pa.getName().equalsIgnoreCase(nom)){
                    switch (nom) {
                        case "PersonIdentifier": PersonIdentifier = pa.getValue().get(0);
                        case "CurrentGivenName": CurrentGivenName = pa.getValue().get(0);
                        case "CurrentFamilyName": CurrentFamilyName = pa.getValue().get(0);
                        case "Gender": Gender = pa.getValue().get(0);
                        case "DateOfBirth": DateOfBirth = pa.getValue().get(0);
                    }
                }
            }
        }
    }

    @Override
    public String getSub() {
        return PersonIdentifier;
    }

    @Override
    public void setSub(String PersonIdentifier) {
        this.PersonIdentifier = PersonIdentifier;
    }

    @Override
    public String getPreferredUsername() {
        return "rien";
    }

    @Override
    public void setPreferredUsername(String preferredUsername) {
        
    }

    @Override
    public String getName() {
        return CurrentGivenName+" "+CurrentFamilyName;
    }

    @Override
    public void setName(String name) {
        
    }

    @Override
    public String getGivenName() {
        return CurrentGivenName;
    }

    @Override
    public void setGivenName(String givenName) {
        
    }

    @Override
    public String getFamilyName() {
        return CurrentFamilyName;
    }

    @Override
    public void setFamilyName(String familyName) {
        
    }

    @Override
    public String getMiddleName() {
        return "jr";
    }

    @Override
    public void setMiddleName(String middleName) {
        
    }

    @Override
    public String getNickname() {
        return "rien";
    }

    @Override
    public void setNickname(String nickname) {
        
    }

    @Override
    public String getProfile() {
        return "rien";
    }

    @Override
    public void setProfile(String profile) {
        
    }

    @Override
    public String getPicture() {
        return "rien";
    }

    @Override
    public void setPicture(String picture) {
        
    }

    @Override
    public String getWebsite() {
        return "http://spopoff.com";
    }

    @Override
    public void setWebsite(String website) {
        
    }

    @Override
    public String getEmail() {
        return "sgpopoff@yahoo.com";
    }

    @Override
    public void setEmail(String email) {
        
    }

    @Override
    public Boolean getEmailVerified() {
        return true;
    }

    @Override
    public void setEmailVerified(Boolean emailVerified) {
        
    }

    @Override
    public String getGender() {
        return Gender;
    }

    @Override
    public void setGender(String gender) {
        
    }

    @Override
    public String getZoneinfo() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setZoneinfo(String zoneinfo) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getLocale() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setLocale(String locale) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getPhoneNumber() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setPhoneNumber(String phoneNumber) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Boolean getPhoneNumberVerified() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setPhoneNumberVerified(Boolean phoneNumberVerified) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Address getAddress() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setAddress(Address address) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getUpdatedTime() {
        Date d = new Date();
        return d.toString();
    }

    @Override
    public void setUpdatedTime(String updatedTime) {
        
    }

    @Override
    public String getBirthdate() {
        return DateOfBirth;
    }

    @Override
    public void setBirthdate(String birthdate) {
        
    }

    @Override
    public JsonObject toJson() {
        JsonObject ret = new JsonObject();
        ret.addProperty("sub", PersonIdentifier);
        ret.addProperty("Gender", Gender);
        ret.addProperty("GivenName", CurrentGivenName);
        ret.addProperty("FamilyName", CurrentFamilyName);
        ret.addProperty("Birthdate", DateOfBirth);
        return ret;
    }

    @Override
    public JsonObject getSource() {
        return toJson();
    }
    private List<String> lesNoms(){
        List<String> ret = new ArrayList<>();
        ret.add("PersonIdentifier");
        ret.add("CurrentGivenName");
        ret.add("CurrentFamilyName");
        ret.add("Gender");
        ret.add("DateOfBirth");
        return ret;
    }
    
}

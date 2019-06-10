package com.kkagurazaka.reactive.repository.sample;

import com.kkagurazaka.reactive.repository.annotation.InMemoryEntity;
import com.kkagurazaka.reactive.repository.annotation.PrefsEntity;
import com.kkagurazaka.reactive.repository.annotation.PrefsKey;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@InMemoryEntity
@PrefsEntity(typeAdapter = SomeClassListTypeAdapter.class)
public class JavaGetterSetterEntity {

    private boolean isVeteran = false;
    private String someStr = null;
    private int age = -1;
    private float pie = 3.1415f;
    private long amount = 123456789L;
    private Set<String> strList = new HashSet<>();
    private List<SomeClass> someClassList = new ArrayList<>();

    @PrefsKey
    public boolean isVeteran() {
        return isVeteran;
    }

    @PrefsKey
    public void setIsVeteran(boolean veteran) {
        isVeteran = veteran;
    }

    @PrefsKey
    public String getSomeStr() {
        return someStr;
    }

    @PrefsKey
    public void setSomeStr(String someStr) {
        this.someStr = someStr;
    }

    @PrefsKey
    public int getAge() {
        return age;
    }

    @PrefsKey
    public void setAge(int age) {
        this.age = age;
    }

    @PrefsKey
    public float getPie() {
        return pie;
    }

    @PrefsKey
    public void setPie(float pie) {
        this.pie = pie;
    }

    @PrefsKey
    public long getAmount() {
        return amount;
    }

    @PrefsKey
    public void setAmount(long amount) {
        this.amount = amount;
    }

    @PrefsKey
    public Set<String> getStrList() {
        return strList;
    }

    @PrefsKey
    public void setStrList(Set<String> strList) {
        this.strList = strList;
    }

    @PrefsKey
    public List<SomeClass> getSomeClassList() {
        return someClassList;
    }

    @PrefsKey
    public void setSomeClassList(List<SomeClass> someClassList) {
        this.someClassList = someClassList;
    }
}

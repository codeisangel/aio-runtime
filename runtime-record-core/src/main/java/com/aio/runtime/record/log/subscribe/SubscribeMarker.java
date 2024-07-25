package com.aio.runtime.record.log.subscribe;

import org.slf4j.Marker;

import java.util.Iterator;

/**
 * @author lzm
 * @desc 订阅标识
 * @date 2024/07/22
 */
public class SubscribeMarker implements Marker {
    private String name;
    public static SubscribeMarker getMarker(){
        return new SubscribeMarker("subscribe");
    }
    public static SubscribeMarker getErrorMarker(){
        return new SubscribeMarker("error");
    }
    public static SubscribeMarker getMarker(String name){
        return new SubscribeMarker(name);
    }
    public SubscribeMarker(String name){
        this.name = name;
    }
    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void add(Marker marker) {

    }

    @Override
    public boolean remove(Marker marker) {
        return false;
    }

    @Override
    public boolean hasChildren() {
        return false;
    }

    @Override
    public boolean hasReferences() {
        return false;
    }

    @Override
    public Iterator<Marker> iterator() {
        return null;
    }

    @Override
    public boolean contains(Marker marker) {
        return false;
    }

    @Override
    public boolean contains(String s) {
        return false;
    }
}
